package use_case.messaging.send_m;

import entity.Chat;
import entity.Message;
import entity.User;
import entity.ports.ChatRepository;
import entity.ports.MessageRepository;
import entity.ports.UserRepository;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SendMessageInteractorTest {

    // -------- Chat Repo --------
    static class FakeChatRepo implements ChatRepository {
        private final Map<String, Chat> data = new HashMap<>();

        public void put(Chat c) { data.put(c.getId(), c); }

        @Override
        public Chat save(Chat chat) {
            data.put(chat.getId(), chat);
            return chat;
        }

        @Override
        public Optional<Chat> findById(String chatId) {
            return Optional.ofNullable(data.get(chatId));
        }

        @Override
        public List<Chat> findAll() {
            return new ArrayList<>(data.values());
        }
    }

    // -------- User Repo --------
    static class FakeUserRepo implements UserRepository {
        private final Map<String, User> data = new HashMap<>();

        public void put(User u) { data.put(u.getName(), u); }

        @Override
        public Optional<User> findByUsername(String username) {
            return Optional.ofNullable(data.get(username));
        }

        @Override
        public User save(User user) {
            data.put(user.getName(), user);
            return user;
        }
    }

    // -------- Message Repo --------
    static class FakeMessageRepo implements MessageRepository {
        Message last;

        @Override
        public Optional<Message> findById(String id) { return Optional.empty(); }

        @Override
        public Message save(Message message) {
            last = message;
            return message;
        }

        @Override
        public List<Message> findByChatId(String chatId) { return Collections.emptyList(); }

        @Override
        public void deleteById(String id) { }

        @Override
        public void clear() { }
    }

    // -------- DAO --------
    static class FakeSendDao implements SendMessageDataAccessInterface {
        Message sent;
        String updatedChatId;
        String updatedMsgId;

        @Override
        public Message sendMessage(Message message) {
            sent = message;
            return message;
        }

        @Override
        public void updateChat(String chatId, String messageId) {
            updatedChatId = chatId;
            updatedMsgId = messageId;
        }
    }

    // -------- Presenter --------
    static class FakePresenter implements SendMessageOutputBoundary {
        SendMessageOutputData success;
        String fail;

        @Override
        public void prepareSuccessView(SendMessageOutputData outputData) {
            success = outputData;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            fail = errorMessage;
        }
    }

    // ================================================================
    //                              TESTS
    // ================================================================

    @Test
    void successFlow() {
        FakeChatRepo chatRepo = new FakeChatRepo();
        FakeUserRepo userRepo = new FakeUserRepo();
        FakeMessageRepo messageRepo = new FakeMessageRepo();
        FakeSendDao dao = new FakeSendDao();
        FakePresenter presenter = new FakePresenter();

        Chat chat = new Chat(
                "chat-1",
                "group",
                Color.GRAY,
                Instant.now()
        );
        chatRepo.put(chat);

        User alice = new User("alice", "pw");
        userRepo.put(alice);

        SendMessageInteractor interactor =
                new SendMessageInteractor(chatRepo, messageRepo, userRepo, presenter, dao);

        SendMessageInputData input =
                new SendMessageInputData("chat-1", "alice", null, "hello");

        interactor.execute(input);

        assertNull(presenter.fail);
        assertNotNull(presenter.success);

        String[] msg = presenter.success.getMessage();
        assertEquals("chat-1", presenter.success.getChatId());
        assertEquals("alice", msg[1]);
        assertEquals("hello", msg[2]);
        assertNotNull(msg[0]);
        assertNotNull(msg[3]);

        assertNotNull(dao.sent);
        assertEquals("chat-1", dao.updatedChatId);
        assertEquals(dao.sent.getId(), dao.updatedMsgId);
    }

    @Test
    void chatNotFound() {
        FakeChatRepo chatRepo = new FakeChatRepo();
        FakeUserRepo userRepo = new FakeUserRepo();
        FakeMessageRepo messageRepo = new FakeMessageRepo();
        FakeSendDao dao = new FakeSendDao();
        FakePresenter presenter = new FakePresenter();

        SendMessageInteractor interactor =
                new SendMessageInteractor(chatRepo, messageRepo, userRepo, presenter, dao);

        SendMessageInputData input =
                new SendMessageInputData("missing", "alice", null, "hi");

        interactor.execute(input);

        assertNotNull(presenter.fail);
        assertTrue(presenter.fail.contains("Chat not found"));
        assertNull(dao.sent);
    }

    @Test
    void senderNotFound() {
        FakeChatRepo chatRepo = new FakeChatRepo();
        FakeUserRepo userRepo = new FakeUserRepo();
        FakeMessageRepo messageRepo = new FakeMessageRepo();
        FakeSendDao dao = new FakeSendDao();
        FakePresenter presenter = new FakePresenter();

        Chat chat = new Chat(
                "chat-1",
                "group",
                Color.GRAY,
                Instant.now()
        );
        chatRepo.put(chat);

        SendMessageInteractor interactor =
                new SendMessageInteractor(chatRepo, messageRepo, userRepo, presenter, dao);

        SendMessageInputData input =
                new SendMessageInputData("chat-1", "nobody", null, "hi");

        interactor.execute(input);

        assertNotNull(presenter.fail);
        assertTrue(presenter.fail.contains("Sender not found"));
        assertNull(dao.sent);
    }
}
