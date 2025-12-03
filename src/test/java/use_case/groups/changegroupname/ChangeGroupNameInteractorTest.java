package use_case.groups.changegroupname;

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

import usecase.groups.changegroupname.*;

/**
 * Complete test suite for ChangeGroupNameInteractor with 100% code coverage.
 */
class ChangeGroupNameInteractorTest {
    private TestChatRepository chatRepository;
    private TestOutputBoundary outputBoundary;
    private TestDataAccess dataAccess;
    private ChangeGroupNameInteractor interactor;

    @BeforeEach
    void setUp() {
        chatRepository = new TestChatRepository();
        outputBoundary = new TestOutputBoundary();
        dataAccess = new TestDataAccess();
        interactor = new ChangeGroupNameInteractor(chatRepository, outputBoundary, dataAccess);
    }

    @Test
    void testExecute_Success() {
        // Arrange
        Chat chat = new Chat("chat123", "Old Name", Color.BLUE, Instant.now());
        chatRepository.addChat(chat);

        ChangeGroupNameInputData inputData = new ChangeGroupNameInputData("chat123", "New Name");

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(outputBoundary.successCalled, "Success view should be called");
        assertFalse(outputBoundary.failCalled, "Fail view should not be called");
        assertNotNull(outputBoundary.successData, "Success data should not be null");
        assertEquals("chat123", outputBoundary.successData.getChatId());
        assertEquals("New Name", outputBoundary.successData.getNewGroupName());
        assertTrue(outputBoundary.successData.isSuccess());
        assertNull(outputBoundary.successData.getErrorMessage());
        assertEquals("New Name", chat.getGroupName());
        assertTrue(dataAccess.changeGroupNameCalled);
        assertTrue(dataAccess.saveChatCalled);
    }

    @Test
    void testExecute_EmptyGroupName() {
        // Arrange
        ChangeGroupNameInputData inputData = new ChangeGroupNameInputData("chat123", "");

        // Act
        interactor.execute(inputData);

        // Assert
        assertFalse(outputBoundary.successCalled, "Success view should not be called");
        assertTrue(outputBoundary.failCalled, "Fail view should be called");
        assertNotNull(outputBoundary.failData);
        assertEquals("chat123", outputBoundary.failData.getChatId());
        assertFalse(outputBoundary.failData.isSuccess());
        assertEquals("Group name cannot be empty", outputBoundary.failData.getErrorMessage());
    }

    @Test
    void testExecute_NullGroupName() {
        // Arrange
        ChangeGroupNameInputData inputData = new ChangeGroupNameInputData("chat123", null);

        // Act
        interactor.execute(inputData);

        // Assert
        assertFalse(outputBoundary.successCalled);
        assertTrue(outputBoundary.failCalled);
        assertNotNull(outputBoundary.failData);
        assertEquals("Group name cannot be empty", outputBoundary.failData.getErrorMessage());
    }

    @Test
    void testExecute_WhitespaceGroupName() {
        // Arrange
        ChangeGroupNameInputData inputData = new ChangeGroupNameInputData("chat123", "   ");

        // Act
        interactor.execute(inputData);

        // Assert
        assertFalse(outputBoundary.successCalled);
        assertTrue(outputBoundary.failCalled);
        assertEquals("Group name cannot be empty", outputBoundary.failData.getErrorMessage());
    }

    @Test
    void testExecute_GroupNameTooLong() {
        // Arrange
        String longName = "a".repeat(101); // 101 characters
        ChangeGroupNameInputData inputData = new ChangeGroupNameInputData("chat123", longName);

        // Act
        interactor.execute(inputData);

        // Assert
        assertFalse(outputBoundary.successCalled);
        assertTrue(outputBoundary.failCalled);
        assertEquals("Group name cannot exceed 100 characters", outputBoundary.failData.getErrorMessage());
    }

    @Test
    void testExecute_GroupNameExactly100Characters() {
        // Arrange
        Chat chat = new Chat("chat123", "Old Name", Color.GREEN, Instant.now());
        chatRepository.addChat(chat);
        String exactName = "a".repeat(100); // Exactly 100 characters
        ChangeGroupNameInputData inputData = new ChangeGroupNameInputData("chat123", exactName);

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(outputBoundary.successCalled);
        assertFalse(outputBoundary.failCalled);
        assertEquals(exactName, chat.getGroupName());
    }

    @Test
    void testExecute_ChatNotFound() {
        // Arrange
        ChangeGroupNameInputData inputData = new ChangeGroupNameInputData("nonexistent", "New Name");

        // Act
        interactor.execute(inputData);

        // Assert
        assertFalse(outputBoundary.successCalled);
        assertTrue(outputBoundary.failCalled);
        assertEquals("Chat not found", outputBoundary.failData.getErrorMessage());
    }

    @Test
    void testExecute_TrimsGroupName() {
        // Arrange
        Chat chat = new Chat("chat123", "Old Name", Color.YELLOW, Instant.now());
        chatRepository.addChat(chat);
        ChangeGroupNameInputData inputData = new ChangeGroupNameInputData("chat123", "  New Name  ");

        // Act
        interactor.execute(inputData);

        // Assert
        assertTrue(outputBoundary.successCalled);
        assertEquals("New Name", outputBoundary.successData.getNewGroupName());
        assertEquals("New Name", chat.getGroupName());
    }

    @Test
    void testExecute_IllegalArgumentException() {
        // Arrange
        Chat chat = new Chat("chat123", "Old Name", Color.RED, Instant.now());
        chatRepository.addChat(chat);
        dataAccess.throwExceptionOnChange = true;
        dataAccess.exceptionType = "IllegalArgumentException";

        ChangeGroupNameInputData inputData = new ChangeGroupNameInputData("chat123", "New Name");

        // Act
        interactor.execute(inputData);

        // Assert
        assertFalse(outputBoundary.successCalled);
        assertTrue(outputBoundary.failCalled);
        assertEquals("Failed to rename group: Invalid argument", outputBoundary.failData.getErrorMessage());
    }

    @Test
    void testExecute_IllegalStateException() {
        // Arrange
        Chat chat = new Chat("chat123", "Old Name", Color.CYAN, Instant.now());
        chatRepository.addChat(chat);
        dataAccess.throwExceptionOnChange = true;
        dataAccess.exceptionType = "IllegalStateException";

        ChangeGroupNameInputData inputData = new ChangeGroupNameInputData("chat123", "New Name");

        // Act
        interactor.execute(inputData);

        // Assert
        assertFalse(outputBoundary.successCalled);
        assertTrue(outputBoundary.failCalled);
        assertEquals("Failed to rename group: Invalid state", outputBoundary.failData.getErrorMessage());
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
     * Test implementation of ChangeGroupNameOutputBoundary for testing purposes.
     */
    private static class TestOutputBoundary implements ChangeGroupNameOutputBoundary {
        boolean successCalled = false;
        boolean failCalled = false;
        ChangeGroupNameOutputData successData = null;
        ChangeGroupNameOutputData failData = null;

        @Override
        public void prepareSuccessView(ChangeGroupNameOutputData outputData) {
            successCalled = true;
            successData = outputData;
        }

        @Override
        public void prepareFailView(ChangeGroupNameOutputData outputData) {
            failCalled = true;
            failData = outputData;
        }
    }

    /**
     * Test implementation of ChangeGroupNameDataAccessInterface for testing purposes.
     */
    private static class TestDataAccess implements ChangeGroupNameDataAccessInterface {
        boolean changeGroupNameCalled = false;
        boolean saveChatCalled = false;
        boolean throwExceptionOnChange = false;
        String exceptionType = "";

        @Override
        public void changeGroupName(String chatId, String groupName) {
            if (throwExceptionOnChange) {
                if ("IllegalArgumentException".equals(exceptionType)) {
                    throw new IllegalArgumentException("Invalid argument");
                } else if ("IllegalStateException".equals(exceptionType)) {
                    throw new IllegalStateException("Invalid state");
                }
            }
            changeGroupNameCalled = true;
        }

        @Override
        public void saveChat(Chat chat) {
            saveChatCalled = true;
        }
    }
}