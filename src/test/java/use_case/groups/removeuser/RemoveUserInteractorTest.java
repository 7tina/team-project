package use_case.groups.removeuser;

import entity.Chat;
import entity.ports.ChatRepository;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class RemoveUserInteractorTest {

    @Test
    void successTest() {
        RemoveUserInputData inputData = new RemoveUserInputData("chat123", "removeuser");

        ChatRepository mockRepo = new ChatRepository() {
            public Optional<Chat> findById(String id) {
                Chat chat = new Chat(id, "Test", Color.BLUE, Instant.now());
                chat.addParticipant("user1");
                chat.addParticipant("user2");
                chat.addParticipant("user3");
                chat.addParticipant("user456");
                return Optional.of(chat);
            }
            public Chat save(Chat chat) { return chat; }
            public List<Chat> findAll() { return new ArrayList<>(); }
        };

        RemoveUserDataAccessInterface mockData = new RemoveUserDataAccessInterface() {
            public String getUserIdByUsername(String u) { return "user456"; }
            public void removeUser(String c, String u) { }
            public Chat saveChat(Chat chat) { return chat; }
        };

        RemoveUserOutputBoundary presenter = new RemoveUserOutputBoundary() {
            public void prepareSuccessView(RemoveUserOutputData data) {
                assertEquals("chat123", data.getChatId());
                assertEquals("removeuser", data.getRemovedUsername());
            }
            public void prepareFailView(String error) { fail("Unexpected failure"); }
        };

        new RemoveUserInteractor(mockRepo, presenter, mockData).execute(inputData);
    }

    @Test
    void successWithWhitespaceTest() {
        RemoveUserInputData inputData = new RemoveUserInputData("chat123", "  removeuser  ");

        ChatRepository mockRepo = new ChatRepository() {
            public Optional<Chat> findById(String id) {
                Chat chat = new Chat(id, "Test", Color.BLUE, Instant.now());
                chat.addParticipant("user1");
                chat.addParticipant("user2");
                chat.addParticipant("user3");
                chat.addParticipant("user456");
                return Optional.of(chat);
            }
            public Chat save(Chat chat) { return chat; }
            public List<Chat> findAll() { return new ArrayList<>(); }
        };

        RemoveUserDataAccessInterface mockData = new RemoveUserDataAccessInterface() {
            public String getUserIdByUsername(String u) {
                assertEquals("removeuser", u);
                return "user456";
            }
            public void removeUser(String c, String u) { }
            public Chat saveChat(Chat chat) { return chat; }
        };

        RemoveUserOutputBoundary presenter = new RemoveUserOutputBoundary() {
            public void prepareSuccessView(RemoveUserOutputData data) {
                assertEquals("removeuser", data.getRemovedUsername());
            }
            public void prepareFailView(String error) { fail("Unexpected failure"); }
        };

        new RemoveUserInteractor(mockRepo, presenter, mockData).execute(inputData);
    }

    @Test
    void failureNullUsernameTest() {
        RemoveUserInputData inputData = new RemoveUserInputData("chat123", null);

        ChatRepository mockRepo = new ChatRepository() {
            public Optional<Chat> findById(String id) { fail("Should not be called"); return Optional.empty(); }
            public Chat save(Chat chat) { return chat; }
            public List<Chat> findAll() { return new ArrayList<>(); }
        };

        RemoveUserDataAccessInterface mockData = new RemoveUserDataAccessInterface() {
            public String getUserIdByUsername(String u) { fail("Should not be called"); return null; }
            public void removeUser(String c, String u) { }
            public Chat saveChat(Chat chat) { return chat; }
        };

        RemoveUserOutputBoundary presenter = new RemoveUserOutputBoundary() {
            public void prepareSuccessView(RemoveUserOutputData data) { fail("Unexpected success"); }
            public void prepareFailView(String error) { assertEquals("Username cannot be empty", error); }
        };

        new RemoveUserInteractor(mockRepo, presenter, mockData).execute(inputData);
    }

    @Test
    void failureEmptyUsernameTest() {
        RemoveUserInputData inputData = new RemoveUserInputData("chat123", "");

        ChatRepository mockRepo = new ChatRepository() {
            public Optional<Chat> findById(String id) { fail("Should not be called"); return Optional.empty(); }
            public Chat save(Chat chat) { return chat; }
            public List<Chat> findAll() { return new ArrayList<>(); }
        };

        RemoveUserDataAccessInterface mockData = new RemoveUserDataAccessInterface() {
            public String getUserIdByUsername(String u) { fail("Should not be called"); return null; }
            public void removeUser(String c, String u) { }
            public Chat saveChat(Chat chat) { return chat; }
        };

        RemoveUserOutputBoundary presenter = new RemoveUserOutputBoundary() {
            public void prepareSuccessView(RemoveUserOutputData data) { fail("Unexpected success"); }
            public void prepareFailView(String error) { assertEquals("Username cannot be empty", error); }
        };

        new RemoveUserInteractor(mockRepo, presenter, mockData).execute(inputData);
    }

    @Test
    void failureWhitespaceUsernameTest() {
        RemoveUserInputData inputData = new RemoveUserInputData("chat123", "   ");

        ChatRepository mockRepo = new ChatRepository() {
            public Optional<Chat> findById(String id) { fail("Should not be called"); return Optional.empty(); }
            public Chat save(Chat chat) { return chat; }
            public List<Chat> findAll() { return new ArrayList<>(); }
        };

        RemoveUserDataAccessInterface mockData = new RemoveUserDataAccessInterface() {
            public String getUserIdByUsername(String u) { fail("Should not be called"); return null; }
            public void removeUser(String c, String u) { }
            public Chat saveChat(Chat chat) { return chat; }
        };

        RemoveUserOutputBoundary presenter = new RemoveUserOutputBoundary() {
            public void prepareSuccessView(RemoveUserOutputData data) { fail("Unexpected success"); }
            public void prepareFailView(String error) { assertEquals("Username cannot be empty", error); }
        };

        new RemoveUserInteractor(mockRepo, presenter, mockData).execute(inputData);
    }

    @Test
    void failureChatNotFoundTest() {
        RemoveUserInputData inputData = new RemoveUserInputData("nonexistent", "username");

        ChatRepository mockRepo = new ChatRepository() {
            public Optional<Chat> findById(String id) { return Optional.empty(); }
            public Chat save(Chat chat) { return chat; }
            public List<Chat> findAll() { return new ArrayList<>(); }
        };

        RemoveUserDataAccessInterface mockData = new RemoveUserDataAccessInterface() {
            public String getUserIdByUsername(String u) { fail("Should not be called"); return null; }
            public void removeUser(String c, String u) { }
            public Chat saveChat(Chat chat) { return chat; }
        };

        RemoveUserOutputBoundary presenter = new RemoveUserOutputBoundary() {
            public void prepareSuccessView(RemoveUserOutputData data) { fail("Unexpected success"); }
            public void prepareFailView(String error) { assertEquals("Chat not found", error); }
        };

        new RemoveUserInteractor(mockRepo, presenter, mockData).execute(inputData);
    }

    @Test
    void failureUserNotFoundTest() {
        RemoveUserInputData inputData = new RemoveUserInputData("chat123", "nonexistentuser");

        ChatRepository mockRepo = new ChatRepository() {
            public Optional<Chat> findById(String id) {
                Chat chat = new Chat(id, "Test", Color.BLUE, Instant.now());
                chat.addParticipant("user1");
                chat.addParticipant("user2");
                chat.addParticipant("user3");
                return Optional.of(chat);
            }
            public Chat save(Chat chat) { return chat; }
            public List<Chat> findAll() { return new ArrayList<>(); }
        };

        RemoveUserDataAccessInterface mockData = new RemoveUserDataAccessInterface() {
            public String getUserIdByUsername(String u) { return null; }
            public void removeUser(String c, String u) { fail("Should not be called"); }
            public Chat saveChat(Chat chat) { return chat; }
        };

        RemoveUserOutputBoundary presenter = new RemoveUserOutputBoundary() {
            public void prepareSuccessView(RemoveUserOutputData data) { fail("Unexpected success"); }
            public void prepareFailView(String error) { assertEquals("User not found: nonexistentuser", error); }
        };

        new RemoveUserInteractor(mockRepo, presenter, mockData).execute(inputData);
    }

    @Test
    void failureUserNotInChatTest() {
        RemoveUserInputData inputData = new RemoveUserInputData("chat123", "notmember");

        ChatRepository mockRepo = new ChatRepository() {
            public Optional<Chat> findById(String id) {
                Chat chat = new Chat(id, "Test", Color.BLUE, Instant.now());
                chat.addParticipant("user1");
                chat.addParticipant("user2");
                chat.addParticipant("user3");
                return Optional.of(chat);
            }
            public Chat save(Chat chat) { return chat; }
            public List<Chat> findAll() { return new ArrayList<>(); }
        };

        RemoveUserDataAccessInterface mockData = new RemoveUserDataAccessInterface() {
            public String getUserIdByUsername(String u) { return "user999"; }
            public void removeUser(String c, String u) { fail("Should not be called"); }
            public Chat saveChat(Chat chat) { return chat; }
        };

        RemoveUserOutputBoundary presenter = new RemoveUserOutputBoundary() {
            public void prepareSuccessView(RemoveUserOutputData data) { fail("Unexpected success"); }
            public void prepareFailView(String error) { assertEquals("User is not a member of this chat", error); }
        };

        new RemoveUserInteractor(mockRepo, presenter, mockData).execute(inputData);
    }

    @Test
    void failureMinimumParticipantsTest() {
        RemoveUserInputData inputData = new RemoveUserInputData("chat123", "user");

        ChatRepository mockRepo = new ChatRepository() {
            public Optional<Chat> findById(String id) {
                Chat chat = new Chat(id, "Test", Color.BLUE, Instant.now());
                chat.addParticipant("user1");
                chat.addParticipant("user2");
                chat.addParticipant("user3");
                return Optional.of(chat);
            }
            public Chat save(Chat chat) { return chat; }
            public List<Chat> findAll() { return new ArrayList<>(); }
        };

        RemoveUserDataAccessInterface mockData = new RemoveUserDataAccessInterface() {
            public String getUserIdByUsername(String u) { return "user1"; }
            public void removeUser(String c, String u) { fail("Should not be called"); }
            public Chat saveChat(Chat chat) { return chat; }
        };

        RemoveUserOutputBoundary presenter = new RemoveUserOutputBoundary() {
            public void prepareSuccessView(RemoveUserOutputData data) { fail("Unexpected success"); }
            public void prepareFailView(String error) { assertEquals("Minimum number of participants is 3", error); }
        };

        new RemoveUserInteractor(mockRepo, presenter, mockData).execute(inputData);
    }

    @Test
    void failureExceptionDuringExecutionTest() {
        RemoveUserInputData inputData = new RemoveUserInputData("chat123", "username");

        ChatRepository mockRepo = new ChatRepository() {
            public Optional<Chat> findById(String id) { throw new RuntimeException("Database error"); }
            public Chat save(Chat chat) { return chat; }
            public List<Chat> findAll() { return new ArrayList<>(); }
        };

        RemoveUserDataAccessInterface mockData = new RemoveUserDataAccessInterface() {
            public String getUserIdByUsername(String u) { return null; }
            public void removeUser(String c, String u) { }
            public Chat saveChat(Chat chat) { return chat; }
        };

        RemoveUserOutputBoundary presenter = new RemoveUserOutputBoundary() {
            public void prepareSuccessView(RemoveUserOutputData data) { fail("Unexpected success"); }
            public void prepareFailView(String error) { assertEquals("Failed to remove user: Database error", error); }
        };

        new RemoveUserInteractor(mockRepo, presenter, mockData).execute(inputData);
    }

    @Test
    void failureExceptionDuringRemoveUserTest() {
        RemoveUserInputData inputData = new RemoveUserInputData("chat123", "removeuser");

        ChatRepository mockRepo = new ChatRepository() {
            public Optional<Chat> findById(String id) {
                Chat chat = new Chat(id, "Test", Color.BLUE, Instant.now());
                chat.addParticipant("user1");
                chat.addParticipant("user2");
                chat.addParticipant("user3");
                chat.addParticipant("user456");
                return Optional.of(chat);
            }
            public Chat save(Chat chat) { return chat; }
            public List<Chat> findAll() { return new ArrayList<>(); }
        };

        RemoveUserDataAccessInterface mockData = new RemoveUserDataAccessInterface() {
            public String getUserIdByUsername(String u) { return "user456"; }
            public void removeUser(String c, String u) { throw new RuntimeException("Remove failed"); }
            public Chat saveChat(Chat chat) { return chat; }
        };

        RemoveUserOutputBoundary presenter = new RemoveUserOutputBoundary() {
            public void prepareSuccessView(RemoveUserOutputData data) { fail("Unexpected success"); }
            public void prepareFailView(String error) { assertEquals("Failed to remove user: Remove failed", error); }
        };

        new RemoveUserInteractor(mockRepo, presenter, mockData).execute(inputData);
    }

    @Test
    void failureExceptionDuringSaveChatTest() {
        RemoveUserInputData inputData = new RemoveUserInputData("chat123", "removeuser");

        ChatRepository mockRepo = new ChatRepository() {
            public Optional<Chat> findById(String id) {
                Chat chat = new Chat(id, "Test", Color.BLUE, Instant.now());
                chat.addParticipant("user1");
                chat.addParticipant("user2");
                chat.addParticipant("user3");
                chat.addParticipant("user456");
                return Optional.of(chat);
            }
            public Chat save(Chat chat) { return chat; }
            public List<Chat> findAll() { return new ArrayList<>(); }
        };

        RemoveUserDataAccessInterface mockData = new RemoveUserDataAccessInterface() {
            public String getUserIdByUsername(String u) { return "user456"; }
            public void removeUser(String c, String u) { }
            public Chat saveChat(Chat chat) { throw new RuntimeException("Save failed"); }
        };

        RemoveUserOutputBoundary presenter = new RemoveUserOutputBoundary() {
            public void prepareSuccessView(RemoveUserOutputData data) { fail("Unexpected success"); }
            public void prepareFailView(String error) { assertEquals("Failed to remove user: Save failed", error); }
        };

        new RemoveUserInteractor(mockRepo, presenter, mockData).execute(inputData);
    }
}