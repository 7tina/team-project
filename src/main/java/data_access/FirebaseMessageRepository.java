package data_access;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import entity.Message;
import entity.ports.MessageRepository;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Concrete implementation of the MessageRepository interface for Firebase Firestore.
 * This class handles all CRUD operations for the Message entity directly with Firebase.
 */
public class FirebaseMessageRepository implements MessageRepository {
    private static final String COLLECTION_CHAT = "chats";
    private static final String CHAT_MESSAGE = "messageIds";
    private final Firestore db;

    // Constants copied from FireBaseUserDataAccessObject
    private static final String COLLECTION_MESSAGE = "messages";
    private static final String MESSAGE_CHAT_ID = "chatId";
    private static final String MESSAGE_CONTENT = "content";
    private static final String MESSAGE_SENDER = "senderUserId";
    private static final String MESSAGE_TIME = "timestamp";
    private static final String MESSAGE_REPLY_ID = "repliedId";
    private static final String MESSAGE_REACTION = "reactions";

    public FirebaseMessageRepository(Firestore db) {
        this.db = db;
    }

    private void removeMessageIdFromChat(String chatId, String messageId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = db.collection(COLLECTION_CHAT).document(chatId);

        ApiFuture<WriteResult> future = docRef.update(CHAT_MESSAGE, FieldValue.arrayRemove(messageId));
        future.get();
    }

    // --- Helper Method: DocumentSnapshot to Message (Copied from FireBaseUserDataAccessObject) ---

    private Message toMessage(DocumentSnapshot doc) {
        String id = doc.getId();
        String chatId = doc.getString(MESSAGE_CHAT_ID);
        String senderId = doc.getString(MESSAGE_SENDER);
        String content = doc.getString(MESSAGE_CONTENT);
        String repliedId = doc.getString(MESSAGE_REPLY_ID);
        // Safe casting from Object to Map<String, String> is necessary for Firestore
        Map<String, String> reactions = (Map<String, String>) doc.get(MESSAGE_REACTION);
        Long timeMs = doc.getLong(MESSAGE_TIME);
        Instant timestamp = timeMs != null ? Instant.ofEpochMilli(timeMs) : Instant.now();

        Message message = new Message(id, chatId, senderId, repliedId, content, timestamp);
        if (reactions != null) {
            for (Map.Entry<String, String> reaction : reactions.entrySet()) {
                message.addReaction(reaction.getKey(), reaction.getValue());
            }
        }
        return message;
    }

    // ---------------- MessageRepository Interface Implementation ----------------

    @Override
    public Optional<Message> findById(String id) {
        DocumentReference docRef = db.collection(COLLECTION_MESSAGE).document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        try {
            DocumentSnapshot snapshot = future.get();
            if (snapshot.exists()) {
                return Optional.of(toMessage(snapshot));
            } else {
                return Optional.empty();
            }
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("ERROR retrieving message " + id + " from Firestore: " + e.getMessage());
            // It's usually better to throw, but returning empty Optional is a safe fall-back here.
            return Optional.empty();
        }
    }

    // Copy from Miles' code in FireBaseMessageDao
    /** -------------------- DELETE BY ID -------------------- **/
    @Override
    public void deleteById(String id) {
        try {
            Optional<Message> opt = findById(id);
            if (opt.isEmpty()) return;

            String chatId = opt.get().getChatId();

            db.collection(COLLECTION_MESSAGE).document(id).delete().get();

            db.collection(COLLECTION_CHAT)
                    .document(chatId)
                    .update(CHAT_MESSAGE, FieldValue.arrayRemove(id))
                    .get();

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to delete message " + id, e);
        }
    }


    @Override
    public Message save(Message message) {
            return message;
    }

    // Copy from Miles' code in FireBaseMessageDao
    /** -------------------- FIND MESSAGES BY CHAT ID -------------------- **/
    @Override
    public List<Message> findByChatId(String chatId) {
        // Core findByChatId logic (migrated from FireBaseUserDataAccessObject)
        try {
            CollectionReference col = db.collection(COLLECTION_MESSAGE);

            Query query = col
                    .whereEqualTo(MESSAGE_CHAT_ID, chatId)
                    .orderBy(MESSAGE_TIME, Query.Direction.ASCENDING);

            ApiFuture<QuerySnapshot> future = query.get();
            QuerySnapshot snapshot = future.get();

            List<Message> results = new ArrayList<>();
            for (QueryDocumentSnapshot doc : snapshot.getDocuments()) {
                results.add(toMessage(doc));
            }

            return results;

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to load chat messages from Firebase", e);
        }
    }

    @Override
    public void clear() {
        // Typically a no-op for a production Firebase Repository, unless it's designed for clearing data.
        // We will leave this method empty or throw an UnsupportedOperationException
        // if this method is not supposed to be called on the live database.
    }
}