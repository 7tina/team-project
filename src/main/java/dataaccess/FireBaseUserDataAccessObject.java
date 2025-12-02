package dataaccess;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

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
import entity.ports.MessageRepository;
import entity.ports.UserRepository;
import usecase.accesschat.AccessChatDataAccessInterface;
import usecase.change_password.ChangePasswordUserDataAccessInterface;
import usecase.create_chat.CreateChatUserDataAccessInterface;
import usecase.groups.adduser.AddUserDataAccessInterface;
import usecase.groups.changegroupname.ChangeGroupNameDataAccessInterface;
import usecase.groups.removeuser.RemoveUserDataAccessInterface;
import usecase.login.LoginUserDataAccessInterface;
import usecase.logout.LogoutUserDataAccessInterface;
import usecase.messaging.deletemessage.DeleteMessageDataAccessInterface;
import usecase.messaging.sendmessage.SendMessageDataAccessInterface;
import usecase.messaging.view_history.ViewChatHistoryDataAccessInterface;
import usecase.recent_chat.RecentChatsUserDataAccessInterface;
import usecase.search_user.SearchUserDataAccessInterface;
import usecase.signup.SignupUserDataAccessInterface;

// CHECKSTYLE:OFF
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
        SendMessageDataAccessInterface,
        DeleteMessageDataAccessInterface,
        AddUserDataAccessInterface,
        RemoveUserDataAccessInterface,
        ChangeGroupNameDataAccessInterface,
        RecentChatsUserDataAccessInterface,
        AccessChatDataAccessInterface {

    private static final String COLLECTION_NAME = "users";
    private static final String NAME_PASSWORD = "password";
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

    // Error messages extracted to constants to avoid MultipleStringLiterals checkstyle error
    private static final String ERR_CHAT_NOT_FOUND = "Chat document not found";
    private static final String ERR_LOAD_CHAT = "Failed to load chat";
    private static final String ERR_DB_SEARCH = "Database error during searchUsers operation.";

    private static Firestore db;
    private final UserFactory userFactory;
    private String currentUsername;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;

    /**
     * Constructs the DAO and initializes the Firebase Admin SDK.
     * @param userRepository The repository for users.
     * @param chatRepository The repository for chats.
     * @param messageRepository The repository for messages.
     * @param serviceAccountKeyPath The path to the Firebase service account JSON file.
     * @param userFactory The factory to create User entities.
     * @throws RuntimeException if initialization fails.
     */
    public FireBaseUserDataAccessObject(UserRepository userRepository,
                                        ChatRepository chatRepository,
                                        MessageRepository messageRepository,
                                        String serviceAccountKeyPath,
                                        UserFactory userFactory) {
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
        this.userFactory = userFactory;
        try {
            // 1. Initialize Firebase App
            final FileInputStream serviceAccount = new FileInputStream(serviceAccountKeyPath);

            final FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            // Check if Firebase is already initialized
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }

            // 2. Get Firestore instance
            this.db = FirestoreClient.getFirestore();

        }
        catch (FileNotFoundException ex) {
            throw new RuntimeException("Failed to initialize Firebase: Service account key not found.", ex);
        }
        catch (IOException ex) {
            throw new RuntimeException("Failed to initialize Firebase: IO error.", ex);
        }
    }

    /**
     * Saves a new user to the 'users' collection using the username as the document ID.
     * @param user The User entity to save.
     */
    @Override
    public void save(User user) {
        // Prepare the data to be saved
        final UserDocument documentData = new UserDocument(user.getPassword());

        // Get a reference to the document using the username as the ID
        final DocumentReference docRef = db.collection(COLLECTION_NAME).document(user.getName());

        // Asynchronously set the document data
        final ApiFuture<WriteResult> future = docRef.set(documentData);

        try {
            // Block until the write is complete
            future.get();
        }
        catch (InterruptedException | ExecutionException ex) {
            throw new RuntimeException("Database error during save operation.", ex);
        }
    }

    /**
     * Retrieves a user by their username (document ID).
     * @param identifier The username of the user to retrieve.
     * @return The User entity, or null if not found.
     */
    @Override
    public User get(String identifier) {
        User result = null;
        final DocumentReference docRef = db.collection(COLLECTION_NAME).document(identifier);
        final ApiFuture<DocumentSnapshot> future = docRef.get();

        try {
            final DocumentSnapshot document = future.get();
            if (document.exists()) {
                // Map the document to the UserDocument class
                final UserDocument doc = document.toObject(UserDocument.class);
                // Create the final User entity using the factory
                if (doc != null) {
                    result = userFactory.create(identifier, doc.getPassword());
                }
            }
            // If not found, result remains null
        }
        catch (InterruptedException | ExecutionException ex) {
            throw new RuntimeException("Database error during get operation.", ex);
        }
        return result;
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
        final DocumentReference docRef = db.collection(COLLECTION_NAME).document(user.getName());

        // Create a Map with the field to update
        final Map<String, Object> updates = Map.of(NAME_PASSWORD, user.getPassword());

        // Asynchronously update the document
        final ApiFuture<WriteResult> future = docRef.update(updates);

        try {
            // Wait for the update to complete
            future.get();
        }
        catch (InterruptedException | ExecutionException ex) {
            throw new RuntimeException("Database error during changePassword operation.", ex);
        }
    }

    /**
     * Searches for usernames containing the query string.
     * @param userId The current user's ID.
     * @param query The search string.
     * @return A list of matching usernames.
     */
    @Override
    public List<String> searchUsers(String userId, String query) {
        final String lowerCaseQuery = query.toLowerCase();
        final List<String> matchingUsers = new ArrayList<>();

        // Get all documents in the collection
        final CollectionReference collection = db.collection(COLLECTION_NAME);
        final ApiFuture<QuerySnapshot> queryFuture = collection.get();

        try {
            final List<QueryDocumentSnapshot> documents = queryFuture.get().getDocuments();

            // Filter in-memory (Document ID is the username)
            for (QueryDocumentSnapshot document : documents) {
                final String username = document.getId();
                if (username.equals(userId)) {
                    continue;
                }
                if (username.toLowerCase().contains(lowerCaseQuery)) {
                    matchingUsers.add(username);
                }
            }
        }
        catch (InterruptedException | ExecutionException ex) {
            throw new RuntimeException(ERR_DB_SEARCH, ex);
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
        boolean success = false;
        if (username != null) {
            final User user = get(username);
            userRepository.save(user);
            success = true;
        }
        return success;
    }

    @Override
    public void updateChatRepository(String username) {
        // Get all documents in the collection
        final CollectionReference collection = db.collection(COLLECTION_CHAT);
        final ApiFuture<QuerySnapshot> future = collection.get();

        try {
            final List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            // Filter in-memory (Document ID is the username)
            for (QueryDocumentSnapshot document : documents) {
                final String chatId = document.getId();
                final String groupName = document.getString(CHAT_NAME);
                final String colorHex = document.getString(CHAT_COLOR);
                final Color chatColor;
                if (colorHex != null) {
                    chatColor = Color.decode(colorHex);
                }
                else {
                    chatColor = Color.WHITE;
                }

                final Long timeMs = document.getLong(CHAT_RECENT);
                final Instant timestamp;
                if (timeMs != null) {
                    timestamp = Instant.ofEpochMilli(timeMs);
                }
                else {
                    timestamp = Instant.now();
                }

                final List<String> participants = (List<String>) document.get(CHAT_USERS);
                if (participants != null && participants.contains(username)) {
                    final Chat chat = new Chat(chatId, groupName, chatColor, timestamp);
                    final List<String> messageIds = (List<String>) document.get(CHAT_MESSAGE);
                    for (String participant : participants) {
                        chat.addParticipant(participant);
                    }
                    if (messageIds != null) {
                        for (String messageId : messageIds) {
                            chat.addMessage(messageId);
                        }
                    }
                    chatRepository.save(chat);
                }
            }
        }
        catch (InterruptedException | ExecutionException ex) {
            throw new RuntimeException(ERR_DB_SEARCH, ex);
        }
    }

    @Override
    public Chat saveChat(Chat chat) {
        try {
            final Map<String, Object> data = new HashMap<>();
            data.put(CHAT_NAME, chat.getGroupName());
            data.put(CHAT_USERS, chat.getParticipantUserIds());
            data.put(CHAT_MESSAGE, chat.getMessageIds());
            data.put(CHAT_COLOR, colorToHex(chat.getBackgroundColor()));
            data.put(CHAT_RECENT, chat.getLastMessage().toEpochMilli());

            final CollectionReference col = db.collection(COLLECTION_CHAT);
            final DocumentReference doc = col.document(chat.getId());
            final ApiFuture<WriteResult> future = doc.set(data);
            future.get();

        }
        catch (InterruptedException | ExecutionException ex) {
            throw new RuntimeException("Failed to save chat", ex);
        }
        chatRepository.save(chat);
        return chat;
    }

    /**
     * Helper function that converts java's color class into a hex representation string.
     * @param color is the color stored using java's color class.
     * @return the color as a string.
     */
    private String colorToHex(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    @Override
    public void findChatMessages(String chatId, List<String> userIds, List<String> messageIds) {
        this.messageRepository.clear();

        List<Message> messages = findByChatId(chatId);

        for (Message msg : messages) {
            if (userIds == null || userIds.isEmpty() || userIds.contains(msg.getSenderUserId())) {
                this.messageRepository.save(msg);
            }
        }
    }

    /**
     * Helper function made using extract method.
     * Converts a document snapshot from firebase to a Message entity.
     * @param doc is the document snapshot.
     * @return the message as a message entity.
     */
    private Message toMessage(DocumentSnapshot doc) {
        final String id = doc.getId();
        final String chatId = doc.getString(MESSAGE_CHAT_ID);
        final String senderId = doc.getString(MESSAGE_SENDER);
        final String content = doc.getString(MESSAGE_CONTENT);
        final String repliedId = doc.getString(MESSAGE_REPLY_ID);
        final Long timeMs = doc.getLong(MESSAGE_TIME);

        Map<String, Object> reactionsRaw = (Map<String, Object>) doc.get(MESSAGE_REACTION);
        Map<String, String> reactions = new HashMap<>();

        if (reactionsRaw != null) {
            for (Map.Entry<String, Object> entry : reactionsRaw.entrySet()) {
                reactions.put(entry.getKey(), entry.getValue().toString());
            }
        }

        final Instant timestamp;
        if (timeMs != null) {
            timestamp = Instant.ofEpochMilli(timeMs);
        }
        else {
            timestamp = Instant.now();
        }

        final Message message = new Message(id, chatId, senderId, repliedId, content, timestamp);

        // Add reactions to the message
        for (Map.Entry<String, String> reaction : reactions.entrySet()) {
            message.addReaction(reaction.getKey(), reaction.getValue());
        }
        return message;
    }

    @Override
    public Message sendMessage(Message message) {
        try {
            final Map<String, Object> data = new HashMap<>();
            data.put(MESSAGE_CHAT_ID, message.getChatId());
            data.put(MESSAGE_SENDER, message.getSenderUserId());
            data.put(MESSAGE_REPLY_ID, message.getRepliedMessageId());
            data.put(MESSAGE_REACTION, message.getReactions());
            data.put(MESSAGE_CONTENT, message.getContent());
            data.put(MESSAGE_TIME, message.getTimestamp().toEpochMilli());

            final CollectionReference col = db.collection(COLLECTION_MESSAGE);

            final ApiFuture<?> future;
            if (message.getId() == null || message.getId().isEmpty()) {
                // Auto-generate ID
                future = col.add(data);
            }
            else {
                final DocumentReference doc = col.document(message.getId());
                future = doc.set(data);
            }
            future.get();
            messageRepository.save(message);
        }
        catch (InterruptedException | ExecutionException ex) {
            throw new RuntimeException("Failed to save message", ex);
        }
        return message;
    }

    @Override
    public void updateChat(String chatId, String messageId) {
        try {
            final DocumentReference doc = db.collection(COLLECTION_CHAT).document(chatId);

            final ApiFuture<DocumentSnapshot> future = doc.get();
            final DocumentSnapshot snapshot = future.get();

            if (snapshot.exists()) {
                final Map<String, Object> data = snapshot.getData();
                if (data != null) {
                    final List<String> messages = (List<String>) data.get(CHAT_MESSAGE);
                    if (messages != null) {
                        messages.add(messageId);
                        // Create a Map with the field to update
                        final Map<String, Object> updates = Map.of(CHAT_MESSAGE, messages);
                        // Asynchronously update the document
                        final ApiFuture<WriteResult> futures = doc.update(updates);
                        futures.get();
                    }
                }
            }
            else {
                System.err.println(ERR_CHAT_NOT_FOUND);
            }
        }
        catch (InterruptedException | ExecutionException ex) {
            throw new RuntimeException(ERR_LOAD_CHAT, ex);
        }
    }

    @Override
    public void changeGroupName(String chatId, String groupName) {
        final DocumentReference docRef = db.collection(COLLECTION_CHAT).document(chatId);

        // Create a Map with the field to update
        final Map<String, Object> updates = Map.of(CHAT_NAME, groupName);

        // Asynchronously update the document
        final ApiFuture<WriteResult> future = docRef.update(updates);

        try {
            // Wait for the update to complete
            future.get();
        }
        catch (InterruptedException | ExecutionException ex) {
            throw new RuntimeException("Database error during changeGroupName operation.", ex);
        }
    }

    /**
     * Gets a user ID by their username.
     * In this system, the username IS the user ID (document ID).
     * This method verifies the user exists before returning the username.
     * @param username The username to look up.
     * @return The user ID (same as username), or null if user doesn't exist.
     */
    @Override
    public String getUserIdByUsername(String username) {
        String result = null;
        if (username != null && !username.trim().isEmpty() && existsByName(username.trim())) {
            result = username.trim();
        }
        return result;
    }

    @Override
    public void addUser(String chatId, String userId) {
        try {
            final DocumentReference doc = db.collection(COLLECTION_CHAT).document(chatId);

            final ApiFuture<DocumentSnapshot> future = doc.get();
            final DocumentSnapshot snapshot = future.get();

            if (snapshot.exists()) {
                final Map<String, Object> data = snapshot.getData();
                if (data != null) {
                    final List<String> participants = (List<String>) data.get(CHAT_USERS);
                    if (participants != null) {
                        participants.add(userId);
                        // Create a Map with the field to update
                        final Map<String, Object> updates = Map.of(CHAT_USERS, participants);
                        // Asynchronously update the document
                        final ApiFuture<WriteResult> futures = doc.update(updates);
                        futures.get();
                    }
                }
            }
            else {
                System.err.println(ERR_CHAT_NOT_FOUND);
            }
        }
        catch (InterruptedException | ExecutionException ex) {
            throw new RuntimeException(ERR_LOAD_CHAT, ex);
        }
    }

    @Override
    public void removeUser(String chatId, String userId) {
        try {
            final DocumentReference doc = db.collection(COLLECTION_CHAT).document(chatId);

            final ApiFuture<DocumentSnapshot> future = doc.get();
            final DocumentSnapshot snapshot = future.get();

            if (snapshot.exists()) {
                final Map<String, Object> data = snapshot.getData();
                if (data != null) {
                    final List<String> participants = (List<String>) data.get(CHAT_USERS);
                    if (participants != null) {
                        participants.remove(userId);
                        // Create a Map with the field to update
                        final Map<String, Object> updates = Map.of(CHAT_USERS, participants);
                        // Asynchronously update the document
                        final ApiFuture<WriteResult> futures = doc.update(updates);
                        futures.get();
                    }
                }
            }
            else {
                System.err.println(ERR_CHAT_NOT_FOUND);
            }
        }
        catch (InterruptedException | ExecutionException ex) {
            throw new RuntimeException(ERR_LOAD_CHAT, ex);
        }
    }

    /**
     * Finds messages by Chat ID.
     * @param chatId The ID of the chat.
     * @return A list of messages.
     * @throws RuntimeException .
     */
    public List<Message> findByChatId(String chatId) {
        try {
            final CollectionReference col = db.collection(COLLECTION_MESSAGE);

            final Query query = col
                    .whereEqualTo(MESSAGE_CHAT_ID, chatId);

            final ApiFuture<QuerySnapshot> future = query.get();
            final QuerySnapshot snapshot = future.get();

            final List<Message> results = new ArrayList<>();
            for (QueryDocumentSnapshot doc : snapshot.getDocuments()) {
                results.add(toMessage(doc));
            }

            return results;

        }
        catch (InterruptedException | ExecutionException ex) {
            throw new RuntimeException("Failed to load chat messages", ex);
        }
    }

    /**
     * Deletes a message by its ID.
     * @param messageId The ID of the message to delete.
     */
    @Override
    public void deleteMessageById(String messageId) {
        try {
            final DocumentReference msgRef = db.collection(COLLECTION_MESSAGE).document(messageId);
            final DocumentSnapshot msgSnap = msgRef.get().get();

            String chatId = null;
            if (msgSnap.exists()) {
                chatId = msgSnap.getString(MESSAGE_CHAT_ID);
            }

            msgRef.delete().get();

            if (chatId != null) {
                final DocumentReference chatRef = db.collection(COLLECTION_CHAT).document(chatId);
                final DocumentSnapshot chatSnap = chatRef.get().get();

                if (chatSnap.exists()) {
                    final List<String> ids = (List<String>) chatSnap.get(CHAT_MESSAGE);
                    if (ids != null && ids.remove(messageId)) {
                        chatRef.update(CHAT_MESSAGE, ids).get();
                    }
                }
            }

            messageRepository.deleteById(messageId);

        }
        catch (InterruptedException | ExecutionException ex) {
            throw new RuntimeException("Failed to delete message " + messageId, ex);
        }
    }

    // Inner class moved to the end to comply with InnerTypeLast checkstyle rule
    private static class UserDocument {
        private String password;

        UserDocument() {
        }

        UserDocument(String password) {
            this.password = password;
        }

        public String getPassword() {
            return password;
        }
    }

    /**
     * Adds a reaction to a message in Firebase.
     *
     * @param messageId the message ID
     * @param userId the user ID who is reacting
     * @param emoji the emoji reaction
     */
    public void addReactionToMessage(String messageId, String userId, String emoji) {
        try {
            final DocumentReference messageRef = db
                    .collection("messages")
                    .document(messageId);

            // Update the reactions map in Firebase
            messageRef.update("reactions." + userId, emoji).get();

        } catch (Exception e) {
            throw new RuntimeException("Failed to add reaction to Firebase: " + e.getMessage(), e);
        }
    }

    /**
     * Removes a reaction from a message in Firebase.
     *
     * @param messageId the message ID
     * @param userId the user ID whose reaction to remove
     */
    public void removeReactionFromMessage(String messageId, String userId) {
        try {
            final DocumentReference messageRef = db
                    .collection("messages")
                    .document(messageId);

            // Remove the user's reaction from Firebase
            messageRef.update("reactions." + userId, FieldValue.delete()).get();

        } catch (Exception e) {
            throw new RuntimeException("Failed to remove reaction from Firebase: " + e.getMessage(), e);
        }
    }
}
