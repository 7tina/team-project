package data_access;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import entity.User;
import entity.UserFactory;
import use_case.change_password.ChangePasswordUserDataAccessInterface;
import use_case.change_username.ChangeUsernameUserDataAccessInterface;
import use_case.login.LoginUserDataAccessInterface;
import use_case.logout.LogoutUserDataAccessInterface;
import use_case.search_user.SearchUserDataAccessInterface;
import use_case.signup.SignupUserDataAccessInterface;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Data Access Object for user data implemented using Google Cloud Firestore.
 */
public class FireBaseUserDataAccessObject implements SignupUserDataAccessInterface,
        LoginUserDataAccessInterface,
        ChangePasswordUserDataAccessInterface,
        LogoutUserDataAccessInterface,
        ChangeUsernameUserDataAccessInterface,
        SearchUserDataAccessInterface {

    // Inner class to represent the structure of a user document in Firestore
    // Note: The username is the document ID, so it is not stored in the document body.
    private static class UserDocument {
        public String password; // Stored as a plain string, as done in FileDAO

        public UserDocument() {} // Required for Firestore automatic data mapping

        public UserDocument(String password) {
            this.password = password;
        }

        public String getPassword() {
            return password;
        }
    }

    private static final String COLLECTION_NAME = "users";
    private final Firestore db;
    private final UserFactory userFactory;
    private String currentUsername;

    /**
     * Constructs the DAO and initializes the Firebase Admin SDK.
     * @param serviceAccountKeyPath The path to the Firebase service account JSON file.
     * @param userFactory The factory to create User entities.
     */
    public FireBaseUserDataAccessObject(String serviceAccountKeyPath, UserFactory userFactory) {
        this.userFactory = userFactory;
        try {
            // 1. Initialize Firebase App
            FileInputStream serviceAccount = new FileInputStream(serviceAccountKeyPath);

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    // DatabaseUrl is not strictly required for Firestore, but can be set for Realtime DB
                    .build();

            // Check if Firebase is already initialized (to prevent errors in tests or hot-reloads)
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }

            // 2. Get Firestore instance
            this.db = FirestoreClient.getFirestore();

            // Simple check to ensure connection works (optional)
            System.out.println("Firebase Firestore initialized successfully. Using Project: " + db.getOptions().getProjectId());

        } catch (FileNotFoundException e) {
            System.err.println("ERROR: Service account key not found at path: " + serviceAccountKeyPath);
            throw new RuntimeException("Failed to initialize Firebase: Service account key not found.", e);
        } catch (IOException e) {
            System.err.println("ERROR: Failed to read service account key file.");
            throw new RuntimeException("Failed to initialize Firebase: IO error.", e);
        }
    }

    /**
     * Saves a new user to the 'users' collection using the username as the document ID.
     * @param user The User entity to save.
     */
    @Override
    public void save(User user) {
        // Prepare the data to be saved (only password, username is the Document ID)
        UserDocument documentData = new UserDocument(user.getPassword());

        // Get a reference to the document using the username as the ID
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(user.getName());

        // Asynchronously set the document data
        ApiFuture<WriteResult> future = docRef.set(documentData);

        try {
            // Block until the write is complete (synchronous behavior for the DAO interface)
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("ERROR saving user " + user.getName() + " to Firestore: " + e.getMessage());
            throw new RuntimeException("Database error during save operation.", e);
        }
    }

    /**
     * Retrieves a user by their username (document ID).
     * @param identifier The username of the user to retrieve.
     * @return The User entity, or null if not found.
     */
    @Override
    public User get(String identifier) {
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(identifier);
        ApiFuture<DocumentSnapshot> future = docRef.get();

        try {
            DocumentSnapshot document = future.get();
            if (document.exists()) {
                // Map the document to the UserDocument class
                UserDocument doc = document.toObject(UserDocument.class);
                // Create the final User entity using the factory
                return userFactory.create(identifier, doc.getPassword());
            } else {
                return null; // User not found
            }
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("ERROR retrieving user " + identifier + " from Firestore: " + e.getMessage());
            throw new RuntimeException("Database error during get operation.", e);
        }
    }

    /**
     * Checks if a user with the given username exists in Firestore.
     * @param identifier The username to check.
     * @return true if the user exists, false otherwise.
     */
    @Override
    public boolean existsByName(String identifier) {
        return get(identifier) != null;
    }

    /**
     * Changes the user's password by updating the existing document.
     * @param user The User entity with the new password.
     */
    @Override
    public void changePassword(User user) {
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(user.getName());

        // Create a Map with the field to update
        Map<String, Object> updates = Map.of("password", user.getPassword());

        // Asynchronously update the document
        ApiFuture<WriteResult> future = docRef.update(updates);

        try {
            future.get(); // Wait for the update to complete
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("ERROR changing password for user " + user.getName() + ": " + e.getMessage());
            throw new RuntimeException("Database error during changePassword operation.", e);
        }
    }

    /**
     * Changes a user's username by deleting the old document and creating a new one.
     * Note: Firestore does not support direct renaming of documents, so a delete-and-recreate is necessary.
     * @param oldUsername The current username.
     * @param newUsername The new desired username.
     */
    @Override
    public void changeUsername(String oldUsername, String newUsername) {
        // 1. Get the old user data
        User oldUser = get(oldUsername);
        if (oldUser == null) {
            // User does not exist, nothing to change
            return;
        }

        // 2. Create the new User entity
        User newUser = userFactory.create(newUsername, oldUser.getPassword());

        // 3. Save the new user (create new document)
        save(newUser);

        // 4. Delete the old document
        DocumentReference oldDocRef = db.collection(COLLECTION_NAME).document(oldUsername);
        ApiFuture<WriteResult> deleteFuture = oldDocRef.delete();

        try {
            deleteFuture.get(); // Wait for delete to complete
        } catch (InterruptedException | ExecutionException e) {
            // Log the error but proceed, as the new user is saved (the old one remains in DB until rules are enforced)
            System.err.println("WARNING: Failed to delete old document for user " + oldUsername + ". New user " + newUsername + " is saved. Error: " + e.getMessage());
        }
    }

    /**
     * Searches for usernames containing the query string.
     * Note: Firestore does not support case-insensitive 'contains' queries efficiently.
     * This implementation fetches all documents and filters in-memory (OK for small user bases).
     * For large user bases, a full-text search index (like Algolia or ElasticSearch) should be used.
     * @param query The search string.
     * @return A list of matching usernames.
     */
    @Override
    public List<String> searchUsers(String query) {
        String lowerCaseQuery = query.toLowerCase();
        List<String> matchingUsers = new ArrayList<>();

        // Get all documents in the collection
        CollectionReference collection = db.collection(COLLECTION_NAME);
        ApiFuture<QuerySnapshot> queryFuture = collection.get();

        try {
            List<QueryDocumentSnapshot> documents = queryFuture.get().getDocuments();

            // Filter in-memory (Document ID is the username)
            for (QueryDocumentSnapshot document : documents) {
                String username = document.getId();
                if (username.toLowerCase().contains(lowerCaseQuery)) {
                    matchingUsers.add(username);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("ERROR searching users in Firestore: " + e.getMessage());
            throw new RuntimeException("Database error during searchUsers operation.", e);
        }

        return matchingUsers;
    }


    // --- Current User Tracking Methods (inherited from FileDAO) ---

    @Override
    public String getCurrentUsername() {
        return currentUsername;
    }

    @Override
    public void setCurrentUsername(String username) {
        this.currentUsername = username;
    }
}