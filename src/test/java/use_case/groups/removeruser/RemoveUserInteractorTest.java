package use_case.groups.removeruser;

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

import usecase.groups.removeuser.*;

/**
 * Complete test suite for RemoveUserInteractor with 100% code coverage.
 * Tests all execution paths including success, validation errors, and exception handling.
 */
class RemoveUserInteractorTest {
    private TestChatRepository chatRepository;
    private TestOutputBoundary outputBoundary;
    private TestDataAccess dataAccess;
    private RemoveUserInteractor interactor;

    @BeforeEach
    void setUp() {
        chatRepository = new TestChatRepository();
        outputBoundary = new TestOutputBoundary();
        dataAccess = new TestDataAccess();
        interactor = new RemoveUserInteractor(chatRepository, outputBoundary, dataAccess);
    }

    @Test
    void testExecute_Success() {
        // Arrange - Create chat with 4 participants (above minimum of 3)
        Chat chat = new Chat("chat123", "Test Group", Color.BLUE, Instant.now());
        chat.addParticipant("user1");
        chat.addParticipant("user2");
        chat.addParticipant("user3");
        chat.addParticipant("userId123");
        chatRepository.addChat(chat);
        dataAccess.addUserMapping("removeMe", "userId123");

        RemoveUserInputData inputData = new RemoveUserInputData("chat123", "removeMe", "user1");

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(outputBoundary.isSuccess, "Should indicate success");
        assertNotNull(outputBoundary.successData, "Success data should not be null");
        assertEquals("chat123", outputBoundary.successData.getChatId(), "Chat ID should match");
        assertEquals("removeMe", outputBoundary.successData.getRemovedUsername(), "Username should match");
        assertFalse(chat.getParticipantUserIds().contains("userId123"), "User should be removed from chat");
        assertTrue(dataAccess.removeUserCalled, "removeUser should be called");
        assertTrue(dataAccess.saveChatCalled, "saveChat should be called");
        assertEquals(3, chat.getParticipantUserIds().size(), "Chat should have 3 participants");
        assertNull(outputBoundary.errorMessage, "Error message should be null on success");
    }

    @Test
    void testExecute_EmptyUsername() {
        // Arrange
        RemoveUserInputData inputData = new RemoveUserInputData("chat123", "", "user1");

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
        RemoveUserInputData inputData = new RemoveUserInputData("chat123", null, "user1");

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
        RemoveUserInputData inputData = new RemoveUserInputData("chat123", "   ", "user1");

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
        RemoveUserInputData inputData = new RemoveUserInputData("nonexistent", "user", "user1");

        // Act
        interactor.execute(inputData);

        // Assert
        assertFalse(outputBoundary.isSuccess, "Should indicate failure");
        assertEquals("Chat not found", outputBoundary.errorMessage, "Error message should match");
        assertNull(outputBoundary.successData, "Success data should be null on failure");
    }

    @Test
    void testExecute_UserNotFound() {
        // Arrange
        Chat chat = new Chat("chat123", "Test Group", Color.GREEN, Instant.now());
        chat.addParticipant("user1");
        chat.addParticipant("user2");
        chat.addParticipant("user3");
        chat.addParticipant("user4");
        chatRepository.addChat(chat);

        RemoveUserInputData inputData = new RemoveUserInputData("chat123", "unknownUser", "user1");

        // Act
        interactor.execute(inputData);

        // Assert
        assertFalse(outputBoundary.isSuccess, "Should indicate failure");
        assertEquals("User not found: unknownUser", outputBoundary.errorMessage, "Error message should match");
        assertNull(outputBoundary.successData, "Success data should be null on failure");
        assertEquals(4, chat.getParticipantUserIds().size(), "Chat should still have 4 participants");
    }

    @Test
    void testExecute_UserNotMemberOfChat() {
        // Arrange
        Chat chat = new Chat("chat123", "Test Group", Color.YELLOW, Instant.now());
        chat.addParticipant("user1");
        chat.addParticipant("user2");
        chat.addParticipant("user3");
        chat.addParticipant("user4");
        chatRepository.addChat(chat);
        dataAccess.addUserMapping("notMember", "userId999");

        RemoveUserInputData inputData = new RemoveUserInputData("chat123", "notMember", "user1");

        // Act
        interactor.execute(inputData);

        // Assert
        assertFalse(outputBoundary.isSuccess, "Should indicate failure");
        assertEquals("User is not a member of this chat", outputBoundary.errorMessage, "Error message should match");
        assertNull(outputBoundary.successData, "Success data should be null on failure");
        assertEquals(4, chat.getParticipantUserIds().size(), "Chat should still have 4 participants");
    }

    @Test
    void testExecute_MinimumParticipants_ExactlyThree() {
        // Arrange - Chat with exactly 3 participants (minimum)
        Chat chat = new Chat("chat123", "Small Group", Color.RED, Instant.now());
        chat.addParticipant("user1");
        chat.addParticipant("user2");
        chat.addParticipant("userId123");
        chatRepository.addChat(chat);
        dataAccess.addUserMapping("removeMe", "userId123");

        RemoveUserInputData inputData = new RemoveUserInputData("chat123", "removeMe", "user1");

        // Act
        interactor.execute(inputData);

        // Assert
        assertFalse(outputBoundary.isSuccess, "Should indicate failure");
        assertEquals("Minimum number of participants is 3", outputBoundary.errorMessage, "Error message should match");
        assertNull(outputBoundary.successData, "Success data should be null on failure");
        assertEquals(3, chat.getParticipantUserIds().size(), "Chat should still have 3 participants");
    }

    @Test
    void testExecute_MinimumParticipants_LessThanThree() {
        // Arrange - Chat with less than 3 participants
        Chat chat = new Chat("chat123", "Tiny Group", Color.ORANGE, Instant.now());
        chat.addParticipant("user1");
        chat.addParticipant("userId123");
        chatRepository.addChat(chat);
        dataAccess.addUserMapping("removeMe", "userId123");

        RemoveUserInputData inputData = new RemoveUserInputData("chat123", "removeMe", "user1");

        // Act
        interactor.execute(inputData);

        // Assert
        assertFalse(outputBoundary.isSuccess, "Should indicate failure");
        assertEquals("Minimum number of participants is 3", outputBoundary.errorMessage, "Error message should match");
        assertNull(outputBoundary.successData, "Success data should be null on failure");
        assertEquals(2, chat.getParticipantUserIds().size(), "Chat should still have 2 participants");
    }

    @Test
    void testExecute_IllegalArgumentException() {
        // Arrange
        Chat chat = new Chat("chat123", "Test Group", Color.PINK, Instant.now());
        chat.addParticipant("user1");
        chat.addParticipant("user2");
        chat.addParticipant("user3");
        chat.addParticipant("userId123");
        chatRepository.addChat(chat);
        dataAccess.addUserMapping("removeMe", "userId123");
        dataAccess.throwExceptionOnRemove = true;
        dataAccess.exceptionType = "IllegalArgumentException";

        RemoveUserInputData inputData = new RemoveUserInputData("chat123", "removeMe", "user1");

        // Act
        interactor.execute(inputData);

        // Assert
        assertFalse(outputBoundary.isSuccess, "Should indicate failure");
        assertEquals("Failed to remove user: Invalid argument", outputBoundary.errorMessage, "Error message should match");
        assertNull(outputBoundary.successData, "Success data should be null on failure");
    }

    @Test
    void testExecute_IllegalStateException() {
        // Arrange
        Chat chat = new Chat("chat123", "Test Group", Color.CYAN, Instant.now());
        chat.addParticipant("user1");
        chat.addParticipant("user2");
        chat.addParticipant("user3");
        chat.addParticipant("userId123");
        chatRepository.addChat(chat);
        dataAccess.addUserMapping("removeMe", "userId123");
        dataAccess.throwExceptionOnRemove = true;
        dataAccess.exceptionType = "IllegalStateException";

        RemoveUserInputData inputData = new RemoveUserInputData("chat123", "removeMe", "user1");

        // Act
        interactor.execute(inputData);

        // Assert
        assertFalse(outputBoundary.isSuccess, "Should indicate failure");
        assertEquals("Failed to remove user: Invalid state", outputBoundary.errorMessage, "Error message should match");
        assertNull(outputBoundary.successData, "Success data should be null on failure");
    }

    @Test
    void testExecute_TrimsUsername() {
        // Arrange
        Chat chat = new Chat("chat123", "Test Group", Color.MAGENTA, Instant.now());
        chat.addParticipant("user1");
        chat.addParticipant("user2");
        chat.addParticipant("user3");
        chat.addParticipant("userId123");
        chatRepository.addChat(chat);
        dataAccess.addUserMapping("removeMe", "userId123");

        RemoveUserInputData inputData = new RemoveUserInputData("chat123", "  removeMe  ", "user1");

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(outputBoundary.isSuccess, "Should indicate success");
        assertNotNull(outputBoundary.successData, "Success data should not be null");
        assertEquals("removeMe", outputBoundary.successData.getRemovedUsername(), "Username should be trimmed");
        assertFalse(chat.getParticipantUserIds().contains("userId123"), "User should be removed from chat");
    }

    @Test
    void testExecute_ExactlyFourParticipants_Success() {
        // Arrange - Boundary test: 4 participants, can remove one to reach minimum of 3
        Chat chat = new Chat("chat123", "Four People", Color.GRAY, Instant.now());
        chat.addParticipant("user1");
        chat.addParticipant("user2");
        chat.addParticipant("user3");
        chat.addParticipant("userId123");
        chatRepository.addChat(chat);
        dataAccess.addUserMapping("removeMe", "userId123");

        RemoveUserInputData inputData = new RemoveUserInputData("chat123", "removeMe", "user1");

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(outputBoundary.isSuccess, "Should allow removing user to reach minimum");
        assertEquals(3, chat.getParticipantUserIds().size(), "Chat should have exactly 3 participants");
    }

    @Test
    void testExecute_ExceptionDuringSave() {
        // Arrange
        Chat chat = new Chat("chat123", "Test Group", Color.PINK, Instant.now());
        chat.addParticipant("user1");
        chat.addParticipant("user2");
        chat.addParticipant("user3");
        chat.addParticipant("userId123");
        chatRepository.addChat(chat);
        dataAccess.addUserMapping("removeMe", "userId123");
        dataAccess.throwExceptionOnSave = true;
        dataAccess.exceptionType = "IllegalStateException";

        RemoveUserInputData inputData = new RemoveUserInputData("chat123", "removeMe", "user1");

        // Act
        interactor.execute(inputData);

        // Assert
        assertFalse(outputBoundary.isSuccess, "Should indicate failure");
        assertEquals("Failed to remove user: Invalid state on save", outputBoundary.errorMessage, "Error message should match");
    }

    @Test
    void testInputData_GetCurrentUserId() {
        // Test the getCurrentUserId() method for coverage
        RemoveUserInputData inputData = new RemoveUserInputData("chat123", "user", "currentUser123");
        assertEquals("currentUser123", inputData.getCurrentUserId());
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
        public Optional<Chat> findById(String chatId) {
            return Optional.ofNullable(chats.get(chatId));
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
     * Test implementation of RemoveUserOutputBoundary for testing purposes.
     */
    private static class TestOutputBoundary implements RemoveUserOutputBoundary {
        boolean isSuccess = false;
        RemoveUserOutputData successData = null;
        String errorMessage = null;

        @Override
        public void prepareSuccessView(RemoveUserOutputData outputData) {
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
     * Test implementation of RemoveUserDataAccessInterface for testing purposes.
     */
    private static class TestDataAccess implements RemoveUserDataAccessInterface {
        private final Map<String, String> usernameToUserId = new HashMap<>();
        boolean removeUserCalled = false;
        boolean saveChatCalled = false;
        boolean throwExceptionOnRemove = false;
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
        public void removeUser(String chatId, String userId) {
            if (throwExceptionOnRemove) {
                if ("IllegalArgumentException".equals(exceptionType)) {
                    throw new IllegalArgumentException("Invalid argument");
                } else if ("IllegalStateException".equals(exceptionType)) {
                    throw new IllegalStateException("Invalid state");
                }
            }
            removeUserCalled = true;
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