package use_case.groups.adduser;

import entity.Chat;
import entity.ports.ChatRepository;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AddUserInteractorTest {

    @Test
    void successTest() {
        AddUserInputData inputData = new AddUserInputData("chat123", "newuser");

        ChatRepository mockRepo = new ChatRepository() {
            public Optional<Chat> findById(String id) {
                Chat chat = new Chat(id, "Test", Color.BLUE, Instant.now());
                chat.addParticipant("user1");
                return Optional.of(chat);
            }
            public Chat save(Chat chat) { return chat; }
            public List<Chat> findAll() { return new ArrayList<>(); }
        };

        AddUserDataAccessInterface mockData = new AddUserDataAccessInterface() {
            public String getUserIdByUsername(String u) { return "user456"; }
            public void addUser(String c, String u) { }
            public Chat saveChat(Chat chat) { return chat; }
        };

        AddUserOutputBoundary presenter = new AddUserOutputBoundary() {
            public void prepareSuccessView(AddUserOutputData data) {
                assertEquals("chat123", data.getChatId());
                assertEquals("newuser", data.getAddedUsername());
            }
            public void prepareFailView(String error) { fail("Unexpected failure"); }
        };

        new AddUserInteractor(mockRepo, presenter, mockData).execute(inputData);
    }

    @Test
    void successWithWhitespaceTest() {
        AddUserInputData inputData = new AddUserInputData("chat123", "  newuser  ");

        ChatRepository mockRepo = new ChatRepository() {
            public Optional<Chat> findById(String id) {
                Chat chat = new Chat(id, "Test", Color.BLUE, Instant.now());
                chat.addParticipant("user1");
                return Optional.of(chat);
            }
            public Chat save(Chat chat) { return chat; }
            public List<Chat> findAll() { return new ArrayList<>(); }
        };

        AddUserDataAccessInterface mockData = new AddUserDataAccessInterface() {
            public String getUserIdByUsername(String u) {
                assertEquals("newuser", u);
                return "user456";
            }
            public void addUser(String c, String u) { }
            public Chat saveChat(Chat chat) { return chat; }
        };

        AddUserOutputBoundary presenter = new AddUserOutputBoundary() {
            public void prepareSuccessView(AddUserOutputData data) {
                assertEquals("newuser", data.getAddedUsername());
            }
            public void prepareFailView(String error) { fail("Unexpected failure"); }
        };

        new AddUserInteractor(mockRepo, presenter, mockData).execute(inputData);
    }

    @Test
    void failureNullUsernameTest() {
        AddUserInputData inputData = new AddUserInputData("chat123", null);

        ChatRepository mockRepo = new ChatRepository() {
            public Optional<Chat> findById(String id) { fail("Should not be called"); return Optional.empty(); }
            public Chat save(Chat chat) { return chat; }
            public List<Chat> findAll() { return new ArrayList<>(); }
        };

        AddUserDataAccessInterface mockData = new AddUserDataAccessInterface() {
            public String getUserIdByUsername(String u) { fail("Should not be called"); return null; }
            public void addUser(String c, String u) { }
            public Chat saveChat(Chat chat) { return chat; }
        };

        AddUserOutputBoundary presenter = new AddUserOutputBoundary() {
            public void prepareSuccessView(AddUserOutputData data) { fail("Unexpected success"); }
            public void prepareFailView(String error) { assertEquals("Username cannot be empty", error); }
        };

        new AddUserInteractor(mockRepo, presenter, mockData).execute(inputData);
    }

    @Test
    void failureEmptyUsernameTest() {
        AddUserInputData inputData = new AddUserInputData("chat123", "");

        ChatRepository mockRepo = new ChatRepository() {
            public Optional<Chat> findById(String id) { fail("Should not be called"); return Optional.empty(); }
            public Chat save(Chat chat) { return chat; }
            public List<Chat> findAll() { return new ArrayList<>(); }
        };

        AddUserDataAccessInterface mockData = new AddUserDataAccessInterface() {
            public String getUserIdByUsername(String u) { fail("Should not be called"); return null; }
            public void addUser(String c, String u) { }
            public Chat saveChat(Chat chat) { return chat; }
        };

        AddUserOutputBoundary presenter = new AddUserOutputBoundary() {
            public void prepareSuccessView(AddUserOutputData data) { fail("Unexpected success"); }
            public void prepareFailView(String error) { assertEquals("Username cannot be empty", error); }
        };

        new AddUserInteractor(mockRepo, presenter, mockData).execute(inputData);
    }

    @Test
    void failureWhitespaceUsernameTest() {
        AddUserInputData inputData = new AddUserInputData("chat123", "   ");

        ChatRepository mockRepo = new ChatRepository() {
            public Optional<Chat> findById(String id) { fail("Should not be called"); return Optional.empty(); }
            public Chat save(Chat chat) { return chat; }
            public List<Chat> findAll() { return new ArrayList<>(); }
        };

        AddUserDataAccessInterface mockData = new AddUserDataAccessInterface() {
            public String getUserIdByUsername(String u) { fail("Should not be called"); return null; }
            public void addUser(String c, String u) { }
            public Chat saveChat(Chat chat) { return chat; }
        };

        AddUserOutputBoundary presenter = new AddUserOutputBoundary() {
            public void prepareSuccessView(AddUserOutputData data) { fail("Unexpected success"); }
            public void prepareFailView(String error) { assertEquals("Username cannot be empty", error); }
        };

        new AddUserInteractor(mockRepo, presenter, mockData).execute(inputData);
    }

    @Test
    void failureChatNotFoundTest() {
        AddUserInputData inputData = new AddUserInputData("nonexistent", "username");

        ChatRepository mockRepo = new ChatRepository() {
            public Optional<Chat> findById(String id) { return Optional.empty(); }
            public Chat save(Chat chat) { return chat; }
            public List<Chat> findAll() { return new ArrayList<>(); }
        };

        AddUserDataAccessInterface mockData = new AddUserDataAccessInterface() {
            public String getUserIdByUsername(String u) { fail("Should not be called"); return null; }
            public void addUser(String c, String u) { }
            public Chat saveChat(Chat chat) { return chat; }
        };

        AddUserOutputBoundary presenter = new AddUserOutputBoundary() {
            public void prepareSuccessView(AddUserOutputData data) { fail("Unexpected success"); }
            public void prepareFailView(String error) { assertEquals("Chat not found", error); }
        };

        new AddUserInteractor(mockRepo, presenter, mockData).execute(inputData);
    }

    @Test
    void failureUserNotFoundTest() {
        AddUserInputData inputData = new AddUserInputData("chat123", "nonexistentuser");

        ChatRepository mockRepo = new ChatRepository() {
            public Optional<Chat> findById(String id) {
                Chat chat = new Chat(id, "Test", Color.BLUE, Instant.now());
                chat.addParticipant("user1");
                return Optional.of(chat);
            }
            public Chat save(Chat chat) { return chat; }
            public List<Chat> findAll() { return new ArrayList<>(); }
        };

        AddUserDataAccessInterface mockData = new AddUserDataAccessInterface() {
            public String getUserIdByUsername(String u) { return null; }
            public void addUser(String c, String u) { fail("Should not be called"); }
            public Chat saveChat(Chat chat) { return chat; }
        };

        AddUserOutputBoundary presenter = new AddUserOutputBoundary() {
            public void prepareSuccessView(AddUserOutputData data) { fail("Unexpected success"); }
            public void prepareFailView(String error) { assertEquals("User not found: nonexistentuser", error); }
        };

        new AddUserInteractor(mockRepo, presenter, mockData).execute(inputData);
    }

    @Test
    void failureUserAlreadyInChatTest() {
        AddUserInputData inputData = new AddUserInputData("chat123", "existinguser");

        ChatRepository mockRepo = new ChatRepository() {
            public Optional<Chat> findById(String id) {
                Chat chat = new Chat(id, "Test", Color.BLUE, Instant.now());
                chat.addParticipant("user2");
                return Optional.of(chat);
            }
            public Chat save(Chat chat) { return chat; }
            public List<Chat> findAll() { return new ArrayList<>(); }
        };

        AddUserDataAccessInterface mockData = new AddUserDataAccessInterface() {
            public String getUserIdByUsername(String u) { return "user2"; }
            public void addUser(String c, String u) { fail("Should not be called"); }
            public Chat saveChat(Chat chat) { return chat; }
        };

        AddUserOutputBoundary presenter = new AddUserOutputBoundary() {
            public void prepareSuccessView(AddUserOutputData data) { fail("Unexpected success"); }
            public void prepareFailView(String error) { assertEquals("User is already a member of this chat", error); }
        };

        new AddUserInteractor(mockRepo, presenter, mockData).execute(inputData);
    }

    @Test
    void failureMaxParticipantsReachedTest() {
        AddUserInputData inputData = new AddUserInputData("chat123", "newuser");

        ChatRepository mockRepo = new ChatRepository() {
            public Optional<Chat> findById(String id) {
                Chat chat = new Chat(id, "Test", Color.BLUE, Instant.now());
                for (int i = 1; i <= 10; i++) chat.addParticipant("user" + i);
                return Optional.of(chat);
            }
            public Chat save(Chat chat) { return chat; }
            public List<Chat> findAll() { return new ArrayList<>(); }
        };

        AddUserDataAccessInterface mockData = new AddUserDataAccessInterface() {
            public String getUserIdByUsername(String u) { return "user11"; }
            public void addUser(String c, String u) { fail("Should not be called"); }
            public Chat saveChat(Chat chat) { return chat; }
        };

        AddUserOutputBoundary presenter = new AddUserOutputBoundary() {
            public void prepareSuccessView(AddUserOutputData data) { fail("Unexpected success"); }
            public void prepareFailView(String error) { assertEquals("Max number of participants reached", error); }
        };

        new AddUserInteractor(mockRepo, presenter, mockData).execute(inputData);
    }

    @Test
    void failureExceptionDuringExecutionTest() {
        AddUserInputData inputData = new AddUserInputData("chat123", "username");

        ChatRepository mockRepo = new ChatRepository() {
            public Optional<Chat> findById(String id) { throw new RuntimeException("Database connection error"); }
            public Chat save(Chat chat) { return chat; }
            public List<Chat> findAll() { return new ArrayList<>(); }
        };

        AddUserDataAccessInterface mockData = new AddUserDataAccessInterface() {
            public String getUserIdByUsername(String u) { return null; }
            public void addUser(String c, String u) { }
            public Chat saveChat(Chat chat) { return chat; }
        };

        AddUserOutputBoundary presenter = new AddUserOutputBoundary() {
            public void prepareSuccessView(AddUserOutputData data) { fail("Unexpected success"); }
            public void prepareFailView(String error) { assertEquals("Failed to add user: Database connection error", error); }
        };

        new AddUserInteractor(mockRepo, presenter, mockData).execute(inputData);
    }

    @Test
    void failureExceptionDuringAddUserTest() {
        AddUserInputData inputData = new AddUserInputData("chat123", "newuser");

        ChatRepository mockRepo = new ChatRepository() {
            public Optional<Chat> findById(String id) {
                Chat chat = new Chat(id, "Test", Color.BLUE, Instant.now());
                chat.addParticipant("user1");
                return Optional.of(chat);
            }
            public Chat save(Chat chat) { return chat; }
            public List<Chat> findAll() { return new ArrayList<>(); }
        };

        AddUserDataAccessInterface mockData = new AddUserDataAccessInterface() {
            public String getUserIdByUsername(String u) { return "user456"; }
            public void addUser(String c, String u) { throw new RuntimeException("Failed to update database"); }
            public Chat saveChat(Chat chat) { return chat; }
        };

        AddUserOutputBoundary presenter = new AddUserOutputBoundary() {
            public void prepareSuccessView(AddUserOutputData data) { fail("Unexpected success"); }
            public void prepareFailView(String error) { assertEquals("Failed to add user: Failed to update database", error); }
        };

        new AddUserInteractor(mockRepo, presenter, mockData).execute(inputData);
    }

    @Test
    void failureExceptionDuringSaveChatTest() {
        AddUserInputData inputData = new AddUserInputData("chat123", "newuser");

        ChatRepository mockRepo = new ChatRepository() {
            public Optional<Chat> findById(String id) {
                Chat chat = new Chat(id, "Test", Color.BLUE, Instant.now());
                chat.addParticipant("user1");
                return Optional.of(chat);
            }
            public Chat save(Chat chat) { return chat; }
            public List<Chat> findAll() { return new ArrayList<>(); }
        };

        AddUserDataAccessInterface mockData = new AddUserDataAccessInterface() {
            public String getUserIdByUsername(String u) { return "user456"; }
            public void addUser(String c, String u) { }
            public Chat saveChat(Chat chat) { throw new RuntimeException("Save operation failed"); }
        };

        AddUserOutputBoundary presenter = new AddUserOutputBoundary() {
            public void prepareSuccessView(AddUserOutputData data) { fail("Unexpected success"); }
            public void prepareFailView(String error) { assertEquals("Failed to add user: Save operation failed", error); }
        };

        new AddUserInteractor(mockRepo, presenter, mockData).execute(inputData);
    }
}