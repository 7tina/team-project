package use_case.groups.changegroupname;

import entity.Chat;
import entity.ports.ChatRepository;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ChangeGroupNameInteractorTest {

    @Test
    void successTest() {
        ChangeGroupNameInputData inputData = new ChangeGroupNameInputData("chat123", "New Group Name");

        ChatRepository mockRepo = new ChatRepository() {
            public Optional<Chat> findById(String id) {
                Chat chat = new Chat(id, "Old Name", Color.BLUE, Instant.now());
                return Optional.of(chat);
            }
            public Chat save(Chat chat) { return chat; }
            public List<Chat> findAll() { return new ArrayList<>(); }
        };

        ChangeGroupNameDataAccessInterface mockData = new ChangeGroupNameDataAccessInterface() {
            public void changeGroupName(String chatId, String newName) { }
            public Chat saveChat(Chat chat) { return chat; }
        };

        ChangeGroupNameOutputBoundary presenter = new ChangeGroupNameOutputBoundary() {
            public void prepareSuccessView(ChangeGroupNameOutputData data) {
                assertEquals("chat123", data.getChatId());
                assertEquals("New Group Name", data.getNewGroupName());
                assertTrue(data.isSuccess());
                assertNull(data.getErrorMessage());
            }
            public void prepareFailView(ChangeGroupNameOutputData data) { fail("Unexpected failure"); }
        };

        new ChangeGroupNameInteractor(mockRepo, presenter, mockData).execute(inputData);
    }

    @Test
    void successWithWhitespaceTest() {
        ChangeGroupNameInputData inputData = new ChangeGroupNameInputData("chat123", "  Trimmed Name  ");

        ChatRepository mockRepo = new ChatRepository() {
            public Optional<Chat> findById(String id) {
                Chat chat = new Chat(id, "Old Name", Color.BLUE, Instant.now());
                return Optional.of(chat);
            }
            public Chat save(Chat chat) { return chat; }
            public List<Chat> findAll() { return new ArrayList<>(); }
        };

        ChangeGroupNameDataAccessInterface mockData = new ChangeGroupNameDataAccessInterface() {
            public void changeGroupName(String chatId, String newName) {
                assertEquals("Trimmed Name", newName);
            }
            public Chat saveChat(Chat chat) { return chat; }
        };

        ChangeGroupNameOutputBoundary presenter = new ChangeGroupNameOutputBoundary() {
            public void prepareSuccessView(ChangeGroupNameOutputData data) {
                assertEquals("Trimmed Name", data.getNewGroupName());
            }
            public void prepareFailView(ChangeGroupNameOutputData data) { fail("Unexpected failure"); }
        };

        new ChangeGroupNameInteractor(mockRepo, presenter, mockData).execute(inputData);
    }

    @Test
    void failureNullNameTest() {
        ChangeGroupNameInputData inputData = new ChangeGroupNameInputData("chat123", null);

        ChatRepository mockRepo = new ChatRepository() {
            public Optional<Chat> findById(String id) { fail("Should not be called"); return Optional.empty(); }
            public Chat save(Chat chat) { return chat; }
            public List<Chat> findAll() { return new ArrayList<>(); }
        };

        ChangeGroupNameDataAccessInterface mockData = new ChangeGroupNameDataAccessInterface() {
            public void changeGroupName(String chatId, String newName) { fail("Should not be called"); }
            public Chat saveChat(Chat chat) { return chat; }
        };

        ChangeGroupNameOutputBoundary presenter = new ChangeGroupNameOutputBoundary() {
            public void prepareSuccessView(ChangeGroupNameOutputData data) { fail("Unexpected success"); }
            public void prepareFailView(ChangeGroupNameOutputData data) {
                assertEquals("chat123", data.getChatId());
                assertNull(data.getNewGroupName());
                assertFalse(data.isSuccess());
                assertEquals("Group name cannot be empty", data.getErrorMessage());
            }
        };

        new ChangeGroupNameInteractor(mockRepo, presenter, mockData).execute(inputData);
    }

    @Test
    void failureEmptyNameTest() {
        ChangeGroupNameInputData inputData = new ChangeGroupNameInputData("chat123", "");

        ChatRepository mockRepo = new ChatRepository() {
            public Optional<Chat> findById(String id) { fail("Should not be called"); return Optional.empty(); }
            public Chat save(Chat chat) { return chat; }
            public List<Chat> findAll() { return new ArrayList<>(); }
        };

        ChangeGroupNameDataAccessInterface mockData = new ChangeGroupNameDataAccessInterface() {
            public void changeGroupName(String chatId, String newName) { fail("Should not be called"); }
            public Chat saveChat(Chat chat) { return chat; }
        };

        ChangeGroupNameOutputBoundary presenter = new ChangeGroupNameOutputBoundary() {
            public void prepareSuccessView(ChangeGroupNameOutputData data) { fail("Unexpected success"); }
            public void prepareFailView(ChangeGroupNameOutputData data) {
                assertFalse(data.isSuccess());
                assertEquals("Group name cannot be empty", data.getErrorMessage());
            }
        };

        new ChangeGroupNameInteractor(mockRepo, presenter, mockData).execute(inputData);
    }

    @Test
    void failureWhitespaceNameTest() {
        ChangeGroupNameInputData inputData = new ChangeGroupNameInputData("chat123", "   ");

        ChatRepository mockRepo = new ChatRepository() {
            public Optional<Chat> findById(String id) { fail("Should not be called"); return Optional.empty(); }
            public Chat save(Chat chat) { return chat; }
            public List<Chat> findAll() { return new ArrayList<>(); }
        };

        ChangeGroupNameDataAccessInterface mockData = new ChangeGroupNameDataAccessInterface() {
            public void changeGroupName(String chatId, String newName) { fail("Should not be called"); }
            public Chat saveChat(Chat chat) { return chat; }
        };

        ChangeGroupNameOutputBoundary presenter = new ChangeGroupNameOutputBoundary() {
            public void prepareSuccessView(ChangeGroupNameOutputData data) { fail("Unexpected success"); }
            public void prepareFailView(ChangeGroupNameOutputData data) {
                assertFalse(data.isSuccess());
                assertEquals("Group name cannot be empty", data.getErrorMessage());
            }
        };

        new ChangeGroupNameInteractor(mockRepo, presenter, mockData).execute(inputData);
    }

    @Test
    void failureNameTooLongTest() {
        String longName = "a".repeat(101);
        ChangeGroupNameInputData inputData = new ChangeGroupNameInputData("chat123", longName);

        ChatRepository mockRepo = new ChatRepository() {
            public Optional<Chat> findById(String id) { fail("Should not be called"); return Optional.empty(); }
            public Chat save(Chat chat) { return chat; }
            public List<Chat> findAll() { return new ArrayList<>(); }
        };

        ChangeGroupNameDataAccessInterface mockData = new ChangeGroupNameDataAccessInterface() {
            public void changeGroupName(String chatId, String newName) { fail("Should not be called"); }
            public Chat saveChat(Chat chat) { return chat; }
        };

        ChangeGroupNameOutputBoundary presenter = new ChangeGroupNameOutputBoundary() {
            public void prepareSuccessView(ChangeGroupNameOutputData data) { fail("Unexpected success"); }
            public void prepareFailView(ChangeGroupNameOutputData data) {
                assertFalse(data.isSuccess());
                assertEquals("Group name is too long (max 100 characters)", data.getErrorMessage());
            }
        };

        new ChangeGroupNameInteractor(mockRepo, presenter, mockData).execute(inputData);
    }

    @Test
    void failureChatNotFoundTest() {
        ChangeGroupNameInputData inputData = new ChangeGroupNameInputData("nonexistent", "New Name");

        ChatRepository mockRepo = new ChatRepository() {
            public Optional<Chat> findById(String id) { return Optional.empty(); }
            public Chat save(Chat chat) { return chat; }
            public List<Chat> findAll() { return new ArrayList<>(); }
        };

        ChangeGroupNameDataAccessInterface mockData = new ChangeGroupNameDataAccessInterface() {
            public void changeGroupName(String chatId, String newName) { fail("Should not be called"); }
            public Chat saveChat(Chat chat) { return chat; }
        };

        ChangeGroupNameOutputBoundary presenter = new ChangeGroupNameOutputBoundary() {
            public void prepareSuccessView(ChangeGroupNameOutputData data) { fail("Unexpected success"); }
            public void prepareFailView(ChangeGroupNameOutputData data) {
                assertFalse(data.isSuccess());
                assertEquals("Chat not found", data.getErrorMessage());
            }
        };

        new ChangeGroupNameInteractor(mockRepo, presenter, mockData).execute(inputData);
    }

    @Test
    void failureExceptionDuringExecutionTest() {
        ChangeGroupNameInputData inputData = new ChangeGroupNameInputData("chat123", "New Name");

        ChatRepository mockRepo = new ChatRepository() {
            public Optional<Chat> findById(String id) { throw new RuntimeException("Database error"); }
            public Chat save(Chat chat) { return chat; }
            public List<Chat> findAll() { return new ArrayList<>(); }
        };

        ChangeGroupNameDataAccessInterface mockData = new ChangeGroupNameDataAccessInterface() {
            public void changeGroupName(String chatId, String newName) { }
            public Chat saveChat(Chat chat) { return chat; }
        };

        ChangeGroupNameOutputBoundary presenter = new ChangeGroupNameOutputBoundary() {
            public void prepareSuccessView(ChangeGroupNameOutputData data) { fail("Unexpected success"); }
            public void prepareFailView(ChangeGroupNameOutputData data) {
                assertFalse(data.isSuccess());
                assertEquals("Failed to rename group: Database error", data.getErrorMessage());
            }
        };

        new ChangeGroupNameInteractor(mockRepo, presenter, mockData).execute(inputData);
    }

    @Test
    void failureExceptionDuringChangeNameTest() {
        ChangeGroupNameInputData inputData = new ChangeGroupNameInputData("chat123", "New Name");

        ChatRepository mockRepo = new ChatRepository() {
            public Optional<Chat> findById(String id) {
                Chat chat = new Chat(id, "Old Name", Color.BLUE, Instant.now());
                return Optional.of(chat);
            }
            public Chat save(Chat chat) { return chat; }
            public List<Chat> findAll() { return new ArrayList<>(); }
        };

        ChangeGroupNameDataAccessInterface mockData = new ChangeGroupNameDataAccessInterface() {
            public void changeGroupName(String chatId, String newName) { throw new RuntimeException("Update failed"); }
            public Chat saveChat(Chat chat) { return chat; }
        };

        ChangeGroupNameOutputBoundary presenter = new ChangeGroupNameOutputBoundary() {
            public void prepareSuccessView(ChangeGroupNameOutputData data) { fail("Unexpected success"); }
            public void prepareFailView(ChangeGroupNameOutputData data) {
                assertFalse(data.isSuccess());
                assertEquals("Failed to rename group: Update failed", data.getErrorMessage());
            }
        };

        new ChangeGroupNameInteractor(mockRepo, presenter, mockData).execute(inputData);
    }

    @Test
    void failureExceptionDuringSaveChatTest() {
        ChangeGroupNameInputData inputData = new ChangeGroupNameInputData("chat123", "New Name");

        ChatRepository mockRepo = new ChatRepository() {
            public Optional<Chat> findById(String id) {
                Chat chat = new Chat(id, "Old Name", Color.BLUE, Instant.now());
                return Optional.of(chat);
            }
            public Chat save(Chat chat) { return chat; }
            public List<Chat> findAll() { return new ArrayList<>(); }
        };

        ChangeGroupNameDataAccessInterface mockData = new ChangeGroupNameDataAccessInterface() {
            public void changeGroupName(String chatId, String newName) { }
            public Chat saveChat(Chat chat) { throw new RuntimeException("Save failed"); }
        };

        ChangeGroupNameOutputBoundary presenter = new ChangeGroupNameOutputBoundary() {
            public void prepareSuccessView(ChangeGroupNameOutputData data) { fail("Unexpected success"); }
            public void prepareFailView(ChangeGroupNameOutputData data) {
                assertFalse(data.isSuccess());
                assertEquals("Failed to rename group: Save failed", data.getErrorMessage());
            }
        };

        new ChangeGroupNameInteractor(mockRepo, presenter, mockData).execute(inputData);
    }
}