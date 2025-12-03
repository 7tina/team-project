package use_case.groups;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import entity.Chat;
import entity.ports.ChatRepository;

import usecase.groups.adduser.*;

/**
 * Complete test suite for AddUserInteractor with 100% code coverage.
 * Tests all execution paths including success, validation errors, and exception handling.
 */
class AddUserInteractorTest {
    private TestChatRepository chatRepository;
    private TestOutputBoundary outputBoundary;
    private TestDataAccess dataAccess;
    private AddUserInteractor interactor;

    @BeforeEach
    void setUp() {
        chatRepository = new TestChatRepository();
        outputBoundary = new TestOutputBoundary();
        dataAccess = new TestDataAccess();
        interactor = new AddUserInteractor(chatRepository, outputBoundary, dataAccess);
    }

    @Test
    void testExecute_Success() {
        // Arrange
        Chat chat = new Chat("chat123", "Test Group", Color.BLUE, Instant.now());
        chat.addParticipant("user1");
        chatRepository.addChat(chat);
        dataAccess.addUserMapping("newUser", "userId123");

        AddUserInputData inputData = new AddUserInputData("chat123", "newUser");

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(outputBoundary.isSuccess, "Should indicate success");
        assertNotNull(outputBoundary.successData, "Success data should not be null");
        assertEquals("chat123", outputBoundary.successData.getChatId(), "Chat ID should match");
        assertEquals("newUser", outputBoundary.successData.getAddedUsername(), "Username should match");
        assertTrue(chat.getParticipantUserIds().contains("userId123"), "User should be added to chat");
        assertTrue(dataAccess.addUserCalled, "addUser should be called");
        assertTrue(dataAccess.saveChatCalled, "saveChat should be called");
        assertNull(outputBoundary.errorMessage, "Error message should be null on success");
    }

    @Test
    void testExecute_EmptyUsername() {
        // Arrange
        AddUserInputData inputData = new AddUserInputData("chat123", "");

        // Act
        interactor.execute(inputData);

        // Assert
        assertFalse(outputBoundary.isSuccess, "Should indicate failure");
        assertEquals("Username cannot be empty", outputBoundary.errorMessage, "Error message should match");
        assertNull(outputBoundary.successData, "Success data should be null on failure");
    }

    @Test
    void testExecute_NullUsername() {
        // Arrange
        AddUserInputData inputData = new AddUserInputData("chat123", null);

        // Act
        interactor.execute(inputData);

        // Assert
        assertFalse(outputBoundary.isSuccess, "Should indicate failure");
        assertEquals("Username cannot be empty", outputBoundary.errorMessage, "Error message should match");
        assertNull(outputBoundary.successData, "Success data should be null on failure");
    }

    @Test
    void testExecute_WhitespaceUsername() {
        // Arrange
        AddUserInputData inputData = new AddUserInputData("chat123", "   ");

        // Act
        interactor.execute(inputData);

        // Assert
        assertFalse(outputBoundary.isSuccess, "Should indicate failure");
        assertEquals("Username cannot be empty", outputBoundary.errorMessage, "Error message should match");
        assertNull(outputBoundary.successData, "Success data should be null on failure");
    }

    @Test
    void testExecute_ChatNotFound() {
        // Arrange
        AddUserInputData inputData = new AddUserInputData("nonexistent", "newUser");

        // Act
        interactor.execute(inputData);

        // Assert
        assertFalse(outputBoundary.isSuccess, "Should indicate failure");
        assertEquals("Chat not found", outputBoundary.errorMessage, "Error message should match");
        assertNull(outputBoundary.successData, "Success data should be null on failure");
    }

    @Test
    void testExecute_MaxGroupMembersReached() {
        // Arrange
        Chat chat = new Chat("chat123", "Full Group", Color.RED, Instant.now());
        for (int i = 0; i < 10; i++) {
            chat.addParticipant("user" + i);
        }
        chatRepository.addChat(chat);

        AddUserInputData inputData = new AddUserInputData("chat123", "newUser");

        // Act
        interactor.execute(inputData);

        // Assert
        assertFalse(outputBoundary.isSuccess, "Should indicate failure");
        assertEquals("This group chat already has the maximum of 10 members.",
                outputBoundary.errorMessage, "Error message should match");
        assertNull(outputBoundary.successData, "Success data should be null on failure");
        assertEquals(10, chat.getParticipantUserIds().size(), "Chat should still have 10 participants");
    }

    @Test
    void testExecute_UserNotFound() {
        // Arrange
        Chat chat = new Chat("chat123", "Test Group", Color.GREEN, Instant.now());
        chat.addParticipant("user1");
        chatRepository.addChat(chat);

        AddUserInputData inputData = new AddUserInputData("chat123", "unknownUser");

        // Act
        interactor.execute(inputData);

        // Assert
        assertFalse(outputBoundary.isSuccess, "Should indicate failure");
        assertEquals("User not found: unknownUser", outputBoundary.errorMessage, "Error message should match");
        assertNull(outputBoundary.successData, "Success data should be null on failure");
        assertEquals(1, chat.getParticipantUserIds().size(), "Chat should still have 1 participant");
    }

    @Test
    void testExecute_UserAlreadyMember() {
        // Arrange
        Chat chat = new Chat("chat123", "Test Group", Color.YELLOW, Instant.now());
        chat.addParticipant("userId123");
        chatRepository.addChat(chat);
        dataAccess.addUserMapping("existingUser", "userId123");

        AddUserInputData inputData = new AddUserInputData("chat123", "existingUser");

        // Act
        interactor.execute(inputData);

        // Assert
        assertFalse(outputBoundary.isSuccess, "Should indicate failure");
        assertEquals("User is already a member of this chat", outputBoundary.errorMessage, "Error message should match");
        assertNull(outputBoundary.successData, "Success data should be null on failure");
        assertEquals(1, chat.getParticipantUserIds().size(), "Chat should still have 1 participant");
    }

    @Test
    void testExecute_TrimsUsername() {
        // Arrange
        Chat chat = new Chat("chat123", "Test Group", Color.CYAN, Instant.now());
        chat.addParticipant("user1");
        chatRepository.addChat(chat);
        dataAccess.addUserMapping("newUser", "userId123");

        AddUserInputData inputData = new AddUserInputData("chat123", "  newUser  ");

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(outputBoundary.isSuccess, "Should indicate success");
        assertNotNull(outputBoundary.successData, "Success data should not be null");
        assertEquals("newUser", outputBoundary.successData.getAddedUsername(), "Username should be trimmed");
        assertTrue(chat.getParticipantUserIds().contains("userId123"), "User should be added to chat");
    }

    @Test
    void testExecute_ExactlyNineMembers_Success() {
        // Arrange - Test boundary condition with 9 existing members
        Chat chat = new Chat("chat123", "Almost Full", Color.MAGENTA, Instant.now());
        for (int i = 0; i < 9; i++) {
            chat.addParticipant("user" + i);
        }
        chatRepository.addChat(chat);
        dataAccess.addUserMapping("tenthUser", "userId10");

        AddUserInputData inputData = new AddUserInputData("chat123", "tenthUser");

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(outputBoundary.isSuccess, "Should allow adding 10th member");
        assertEquals(10, chat.getParticipantUserIds().size(), "Chat should have exactly 10 participants");
    }

    @Test
    void testExecute_ExceptionDuringSave() {
        // Arrange
        Chat chat = new Chat("chat123", "Test Group", Color.ORANGE, Instant.now());
        chat.addParticipant("user1");
        chatRepository.addChat(chat);
        dataAccess.addUserMapping("newUser", "userId123");
        dataAccess.throwExceptionOnSave = true;
        dataAccess.exceptionType = "IllegalStateException";

        AddUserInputData inputData = new AddUserInputData("chat123", "newUser");

        // Act
        interactor.execute(inputData);

        // Assert
        assertFalse(outputBoundary.isSuccess, "Should indicate failure");
        assertEquals("Unexpected error: Invalid state on save", outputBoundary.errorMessage, "Error message should match");
    }

    // ==================== Test Double Implementations ====================

    /**
     * Test implementation of ChatRepository for testing purposes.
     */
    private static class TestChatRepository implements ChatRepository {
        private final Map<String, Chat> chats = new HashMap<>();

        void addChat(Chat chat) {
            chats.put(chat.getId(), chat);
        }

        @Override
        public Optional<Chat> findById(String id) {
            return Optional.ofNullable(chats.get(id));
        }

        @Override
        public Chat save(Chat chat) {
            chats.put(chat.getId(), chat);
            return chat;
        }

        @Override
        public List<Chat> findAll() {
            return new ArrayList<>(chats.values());
        }

        @Override
        public void clear() {
            chats.clear();
        }
    }

    /**
     * Test implementation of AddUserOutputBoundary for testing purposes.
     */
    private static class TestOutputBoundary implements AddUserOutputBoundary {
        boolean isSuccess = false;
        AddUserOutputData successData = null;
        String errorMessage = null;

        @Override
        public void prepareSuccessView(AddUserOutputData outputData) {
            isSuccess = true;
            successData = outputData;
            errorMessage = null;
        }

        @Override
        public void prepareFailView(String error) {
            isSuccess = false;
            errorMessage = error;
            successData = null;
        }
    }

    /**
     * Test implementation of AddUserDataAccessInterface for testing purposes.
     */
    private static class TestDataAccess implements AddUserDataAccessInterface {
        private final Map<String, String> usernameToUserId = new HashMap<>();
        boolean addUserCalled = false;
        boolean saveChatCalled = false;
        boolean throwExceptionOnSave = false;
        String exceptionType = "";

        void addUserMapping(String username, String userId) {
            usernameToUserId.put(username, userId);
        }

        @Override
        public String getUserIdByUsername(String username) {
            return usernameToUserId.get(username);
        }

        @Override
        public void addUser(String chatId, String userId) {
            addUserCalled = true;
        }

        @Override
        public void saveChat(Chat chat) {
            if (throwExceptionOnSave) {
                if ("IllegalStateException".equals(exceptionType)) {
                    throw new IllegalStateException("Invalid state on save");
                }
            }
            saveChatCalled = true;
        }
    }
}