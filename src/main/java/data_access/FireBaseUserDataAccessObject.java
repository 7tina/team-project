package data_access;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import entity.Chat;
import entity.Message;
import entity.User;
import entity.UserFactory;
import entity.ports.ChatRepository;
import entity.ports.UserRepository;
import entity.ports.MessageRepository;
import use_case.change_password.ChangePasswordUserDataAccessInterface;
import use_case.create_chat.CreateChatUserDataAccessInterface;
import use_case.login.LoginUserDataAccessInterface;
import use_case.logout.LogoutUserDataAccessInterface;
import use_case.messaging.send_m.SendMessageDataAccessInterface;
import use_case.messaging.view_history.ViewChatHistoryDataAccessInterface;
import use_case.search_user.SearchUserDataAccessInterface;
import use_case.signup.SignupUserDataAccessInterface;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Data Access Object for user data implemented using Google Cloud Firestore.
 */
public class FireBaseUserDataAccessObject implements SignupUserDataAccessInterface,
        LoginUserDataAccessInterface,
        ChangePasswordUserDataAccessInterface,
        LogoutUserDataAccessInterface,
        SearchUserDataAccessInterface,
        CreateChatUserDataAccessInterface,
        ViewChatHistoryDataAccessInterface,
        SendMessageDataAccessInterface{

    /**
     * Returns the initialized Firestore instance.
     * Required for creating other Firebase-backed repositories.
     */
    public Firestore getDbInstance() {
        return this.db;
    }

    // Inner class to represent the structure of a user document in Firestore
    // Note: The username is the document ID, so it is not stored in the document body.
    private static class UserDocument {
        private String password; // Stored as a plain string, as done in FileDAO

        public UserDocument() {} // Required for Firestore automatic data mapping

        public UserDocument(String password) {
            this.password = password;
        }

        public String getPassword() {
            return password;
        }
    }

    private static final String COLLECTION_NAME = "users";
    private static final String COLLECTION_CHAT = "chats";
    private static final String CHAT_NAME = "Groupname";
    private static final String CHAT_USERS = "participants";
    private static final String CHAT_MESSAGE = "messageIds";
    private static final String CHAT_COLOR = "colorhex";
    private static final String CHAT_RECENT = "recent";
    private static final String COLLECTION_MESSAGE = "messages";
    private static final String MESSAGE_CHAT_ID = "chatId";
    private static final String MESSAGE_CONTENT = "content";
    private static final String MESSAGE_SENDER = "senderUserId";
    private static final String MESSAGE_TIME = "timestamp";
    private static final String MESSAGE_REPLY_ID = "repliedId";
    private static final String MESSAGE_REACTION = "reactions";
    private final Firestore db;
    private final UserFactory userFactory;
    private String currentUsername;
    private final UserRepository  userRepository;
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;

    /**
     * Constructs the DAO and initializes the Firebase Admin SDK.
     * @param serviceAccountKeyPath The path to the Firebase service account JSON file.
     * @param userFactory The factory to create User entities.
     */
    public FireBaseUserDataAccessObject(UserRepository userRepository,
                                        ChatRepository chatrepository,
                                        MessageRepository messageRepository,
                                        String serviceAccountKeyPath,
                                        UserFactory userFactory) {
        this.userRepository = userRepository;
        this.chatRepository = chatrepository;
        this.messageRepository = messageRepository;
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
        // Prepare the data to be saved (username and password, email is the Document ID)
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
                assert doc != null;
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
     * @param name The username to check.
     * @return true if the user exists, false otherwise.
     */
    @Override
    public boolean existsByName(String name) {
        return get(name) != null;
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
     * Searches for usernames containing the query string.
     * Note: Firestore does not support case-insensitive 'contains' queries efficiently.
     * This implementation fetches all documents and filters in-memory (OK for small user bases).
     * For large user bases, a full-text search index (like Algolia or ElasticSearch) should be used.
     * @param query The search string.
     * @return A list of matching usernames.
     */
    @Override
    public List<String> searchUsers(String userId, String query) {
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
                if (username.equals(userId)) {continue;}
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
        loadToEntity(username);
        this.currentUsername = username;
    }

    @Override
    public boolean loadToEntity(String username) {
        if (username != null) {
            User user = get(username);
            userRepository.save(user);
            return true;
        }
        else { return false; }
    }

    @Override
    public void updateChatRepository(String username) {
        // Get all documents in the collection
        CollectionReference collection = db.collection(COLLECTION_CHAT);
        ApiFuture<QuerySnapshot> future = collection.get();

        try {
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            // Filter in-memory (Document ID is the username)
            for (QueryDocumentSnapshot document : documents) {
                String chatId = document.getId();
                String groupName = document.getString(CHAT_NAME);
                Color chatColor = Color.decode(document.getString(CHAT_COLOR));
                Long timeMs = document.getLong(CHAT_RECENT);
                Instant timestamp = timeMs != null
                        ? Instant.ofEpochMilli(timeMs)
                        : Instant.now();
                List<String> participants = (List<String>) document.get(CHAT_USERS);
                if (participants.contains(username)) {
                    Chat chat = new Chat(chatId, groupName, chatColor, timestamp);
                    List<String> messageIds = (List<String>) document.get(CHAT_MESSAGE);
                    for (String participant : participants) {
                        chat.addParticipant(participant);
                    }
                    for (String messageId : messageIds) {
                        chat.addMessage(messageId);
                    }
                    chatRepository.save(chat);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("ERROR searching users in Firestore: " + e.getMessage());
            throw new RuntimeException("Database error during searchUsers operation.", e);
        }
    }

    @Override
    public Chat saveChat(Chat chat) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put(CHAT_NAME, chat.getGroupName());
            data.put(CHAT_USERS, chat.getParticipantUserIds());
            data.put(CHAT_MESSAGE, chat.getMessageIds());
            data.put(CHAT_COLOR, colorToHex(chat.getBackgroundColor()));
            data.put(CHAT_RECENT, chat.getLastMessage().toEpochMilli());

            CollectionReference col = db.collection(COLLECTION_CHAT);
            DocumentReference doc = col.document(chat.getId());
            ApiFuture<WriteResult> future = doc.set(data);
            future.get();

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to save chat", e);
        }
        chatRepository.save(chat);
        return chat;
    }

    private String colorToHex(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    @Override
    public void findChatMessages(String chatId, List<String> userIds, List<String> messageIds) {
        this.messageRepository.clear();

        for (String messageId : messageIds) {
            DocumentReference docRef = db.collection(COLLECTION_MESSAGE).document(messageId);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            try {
                DocumentSnapshot snapshot = future.get();
                if (snapshot.exists()) {
                    Message msg = toMessage(snapshot);
                    if (msg.getChatId().equals(chatId) && userIds.contains(msg.getSenderUserId())
                            && messageIds.contains(msg.getId())) {this.messageRepository.save(msg);}
                }
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException("Failed to load message by ID", e);
            }
        }
    }

    private Message toMessage(DocumentSnapshot doc) {
        String id = doc.getId();
        String chatId = doc.getString(MESSAGE_CHAT_ID);
        String senderId = doc.getString(MESSAGE_SENDER);
        String content = doc.getString(MESSAGE_CONTENT);
        String repliedId = doc.getString(MESSAGE_REPLY_ID);
        Map<String, String> reactions = (Map<String, String>) doc.get(MESSAGE_REACTION);
        Long timeMs = doc.getLong(MESSAGE_TIME);
        Instant timestamp = timeMs != null
                ? Instant.ofEpochMilli(timeMs)
                : Instant.now();

        Message message = new Message(id, chatId, senderId, repliedId, content, timestamp);
        assert reactions != null;
        for (Map.Entry<String, String> reaction : reactions.entrySet()) {
            message.addReaction(reaction.getKey(), reaction.getValue());
        }
        return message;
    }

    @Override
    public Message sendMessage(Message message) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put(MESSAGE_CHAT_ID, message.getChatId());
            data.put(MESSAGE_SENDER, message.getSenderUserId());
            data.put(MESSAGE_REPLY_ID, message.getRepliedMessageId());
            data.put(MESSAGE_REACTION, message.getReactions());
            data.put(MESSAGE_CONTENT, message.getContent());
            data.put(MESSAGE_TIME, message.getTimestamp().toEpochMilli());

            CollectionReference col = db.collection(COLLECTION_MESSAGE);

            if (message.getId() == null || message.getId().isEmpty()) {
                // Auto-generate ID
                ApiFuture<DocumentReference> future = col.add(data);
                future.get();
                messageRepository.save(message);
                return message;
            } else {
                DocumentReference doc = col.document(message.getId());
                ApiFuture<WriteResult> future = doc.set(data);
                future.get();
                messageRepository.save(message);
                return message;
            }

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to save message", e);
        }
    }

    public void updateChat(String chatId, String messageId) {
        try {
            DocumentReference doc = db.collection(COLLECTION_CHAT).document(chatId);

            ApiFuture<DocumentSnapshot> future = doc.get();
            DocumentSnapshot snapshot = future.get();

            if (snapshot.exists()) {
                Map<String, Object> data = snapshot.getData();  // <-- Your HashMap
                List<String> messages = (List<String>) data.get(CHAT_MESSAGE);
                messages.add(messageId);

                // Create a Map with the field to update
                Map<String, Object> updates = Map.of(CHAT_MESSAGE, messages);

                // Asynchronously update the document
                ApiFuture<WriteResult> futures = doc.update(updates);
                futures.get();
            } else {
                System.err.println("Chat document not found");
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to load chat", e);
        }
    }

    /** -------------------- FIND MESSAGES BY CHAT ID -------------------- **/
    public void deleteMessageById(String messageId) {
        try {
            DocumentReference msgDoc = db.collection(COLLECTION_MESSAGE).document(messageId);
            DocumentSnapshot snap = msgDoc.get().get();
            if (!snap.exists()) return;

            String chatId = snap.getString(MESSAGE_CHAT_ID);

            msgDoc.delete().get();

            db.collection(COLLECTION_CHAT)
                    .document(chatId)
                    .update(CHAT_MESSAGE, FieldValue.arrayRemove(messageId))
                    .get();

            messageRepository.deleteById(messageId);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}