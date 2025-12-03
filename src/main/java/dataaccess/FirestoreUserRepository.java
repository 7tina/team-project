package dataaccess;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import entity.User;
import entity.UserFactory;
import entity.ports.UserRepository;

import java.util.concurrent.ExecutionException;
import java.util.Optional;
import java.util.Map;

public class FirestoreUserRepository implements UserRepository {

    private static final String COLLECTION_NAME = "users";
    private final Firestore db;
    private final UserFactory userFactory;

    public FirestoreUserRepository(Firestore db, UserFactory userFactory) {
        this.db = db;
        this.userFactory = userFactory;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        try {
            DocumentReference docRef = db.collection(COLLECTION_NAME).document(username);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();

            if (document.exists()) {
                String password = document.getString("password");
                User user = userFactory.create(username, password);
                return Optional.of(user);
            } else {
                return Optional.empty();
            }
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("ERROR retrieving user from Firestore: " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public User save(User user) {
        try {
            DocumentReference docRef = db.collection(COLLECTION_NAME).document(user.getName());
            Map<String, Object> data = Map.of("password", user.getPassword());
            ApiFuture<WriteResult> future = docRef.set(data);
            future.get();
            return user;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to save user", e);
        }
    }
}
