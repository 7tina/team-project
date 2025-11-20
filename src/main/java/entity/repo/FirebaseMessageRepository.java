package entity.repo;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import entity.Message;
import entity.ports.MessageRepository;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class FirebaseMessageRepository implements MessageRepository {

    private static final String COLLECTION = "messages";
    private final Firestore db;

    private static final String FIELD_CHAT_ID = "chatId";
    private static final String FIELD_SENDER_ID = "senderUserId";
    private static final String FIELD_CONTENT = "content";
    private static final String FIELD_TIMESTAMP = "timestamp"; // stored as epochMillis

    public FirebaseMessageRepository(Firestore db) {
        this.db = db;
    }

    @Override
    public Optional<Message> findById(String id) {
        try {
            DocumentReference docRef = db.collection(COLLECTION).document(id);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot snapshot = future.get();

            if (!snapshot.exists()) return Optional.empty();

            return Optional.of(toMessage(snapshot));
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to load message by ID", e);
        }
    }

    /** -------------------- SAVE MESSAGE -------------------- **/
    @Override
    public Message save(Message message) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put(FIELD_CHAT_ID, message.getChatId());
            data.put(FIELD_SENDER_ID, message.getSenderUserId());
            data.put(FIELD_CONTENT, message.getContent());
            data.put(FIELD_TIMESTAMP, message.getTimestamp().toEpochMilli());

            CollectionReference col = db.collection(COLLECTION);

            if (message.getId() == null || message.getId().isEmpty()) {
                // Auto-generate ID
                ApiFuture<DocumentReference> future = col.add(data);
                DocumentReference ref = future.get();
                return message;
            } else {
                DocumentReference doc = col.document(message.getId());
                ApiFuture<WriteResult> future = doc.set(data);
                future.get();
                return message;
            }

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to save message", e);
        }
    }

    /** -------------------- FIND MESSAGES BY CHAT ID -------------------- **/
    @Override
    public List<Message> findByChatId(String chatId) {
        try {
            CollectionReference col = db.collection(COLLECTION);

            Query query = col
                    .whereEqualTo(FIELD_CHAT_ID, chatId)
                    .orderBy(FIELD_TIMESTAMP, Query.Direction.ASCENDING);

            ApiFuture<QuerySnapshot> future = query.get();
            QuerySnapshot snapshot = future.get();

            List<Message> results = new ArrayList<>();
            for (QueryDocumentSnapshot doc : snapshot.getDocuments()) {
                results.add(toMessage(doc));
            }

            return results;

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to load chat messages", e);
        }
    }

    /** -------------------- DELETE BY ID -------------------- **/
    @Override
    public void deleteById(String id) {
        try {
            DocumentReference doc = db.collection(COLLECTION).document(id);
            ApiFuture<WriteResult> future = doc.delete();
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to delete message", e);
        }
    }

    /** -------------------- HELPER: Convert Firebase Doc → Message -------------------- **/
    private Message toMessage(DocumentSnapshot doc) {
        String id = doc.getId();
        String chatId = doc.getString(FIELD_CHAT_ID);
        String senderId = doc.getString(FIELD_SENDER_ID);
        String content = doc.getString(FIELD_CONTENT);

        Long timeMs = doc.getLong(FIELD_TIMESTAMP);
        Instant timestamp = timeMs != null
                ? Instant.ofEpochMilli(timeMs)
                : Instant.now();

        return new Message(id, chatId, senderId, content, timestamp);
    }
}
