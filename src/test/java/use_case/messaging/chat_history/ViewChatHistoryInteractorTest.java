package use_case.messaging.chat_history;

import entity.Chat;
import entity.Message;
import entity.User;
import entity.UserFactory;
import entity.ports.ChatRepository;
import entity.ports.MessageRepository;
import entity.ports.UserRepository;
import entity.repo.InMemoryChatRepository;
import entity.repo.InMemoryMessageRepository;
import entity.repo.InMemoryUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import usecase.messaging.viewhistory.ViewChatHistoryDataAccessInterface;
import usecase.messaging.viewhistory.ViewChatHistoryInputBoundary;
import usecase.messaging.viewhistory.ViewChatHistoryInputData;
import usecase.messaging.viewhistory.ViewChatHistoryInteractor;
import usecase.messaging.viewhistory.ViewChatHistoryOutputBoundary;
import usecase.messaging.viewhistory.ViewChatHistoryOutputData;

import java.awt.Color;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ✅ Final working version for current entity & use case structure.
 */
public class ViewChatHistoryInteractorTest {

    private ChatRepository chatRepo;
    private MessageRepository messageRepo;
    private UserRepository userRepo;
    private CapturingPresenter presenter;
    private ViewChatHistoryDataAccessInterface dataAccess;
    private ViewChatHistoryInputBoundary interactor;

    @BeforeEach
    void setUp() {
        chatRepo = new InMemoryChatRepository();
        messageRepo = new InMemoryMessageRepository();
        userRepo = new InMemoryUserRepository();
        presenter = new CapturingPresenter();
        dataAccess = new DummyDataAccess();

        interactor = new ViewChatHistoryInteractor(
                chatRepo,
                messageRepo,
                userRepo,
                presenter,
                dataAccess
        );
    }

    /**
     * ✅ Happy path: chat exists and has two messages.
     * They should be returned in chronological order (oldest → newest).
     */
    @Test
    void messagesAreReturnedInChronologicalOrder() {
        // 1. Create chat (with 4 params: id, groupName, color, lastMessage)
        Chat chat = new Chat(
                "chat-1",
                "Test Group",
                Color.LIGHT_GRAY,
                Instant.now()
        );
        chat.addParticipant("u1");
        chat.addParticipant("u2");
        chatRepo.save(chat);

        // 2. Create users
        UserFactory userFactory = new UserFactory();
        User alice = userFactory.create("Alice", "pw1");
        User bob   = userFactory.create("Bob",   "pw2");
        userRepo.save(alice);
        userRepo.save(bob);

        // 3. Create messages (deliberately reverse chronological order)
        Message later = new Message(
                "m2",
                "chat-1",
                "u2",
                null,
                "Later message",
                Instant.parse("2025-11-14T11:00:00Z")
        );
        Message earlier = new Message(
                "m1",
                "chat-1",
                "u1",
                null,
                "Earlier message",
                Instant.parse("2025-11-14T10:00:00Z")
        );

        messageRepo.save(later);
        messageRepo.save(earlier);

        // 4. Execute use case
        ViewChatHistoryInputData input =
                new ViewChatHistoryInputData(
                        "chat-1",
                        Arrays.asList("u1", "u2"),
                        Arrays.asList("m1", "m2")
                );
        interactor.execute(input);

        // 5. Assertions
        assertNotNull(presenter.successData);
        assertNull(presenter.errorMessage);
        assertNull(presenter.noMessagesChatId);

        List<String[]> msgs = presenter.successData.getMessages();
        assertEquals(2, msgs.size());

        // Array order: [messageId, senderUserId, content, timestamp, repliedId]
        assertEquals("Earlier message", msgs.get(0)[2]);
        assertEquals("Later message",   msgs.get(1)[2]);
    }

    /**
     * ✅ Chat exists but has no messages.
     */
    @Test
    void noMessagesInChat() {
        Chat chat = new Chat(
                "empty-chat",
                "Empty Group",
                Color.LIGHT_GRAY,
                Instant.now()
        );
        chatRepo.save(chat);

        ViewChatHistoryInputData input =
                new ViewChatHistoryInputData(
                        "empty-chat",
                        Collections.emptyList(),
                        Collections.emptyList()
                );
        interactor.execute(input);

        assertNull(presenter.successData);
        assertNull(presenter.errorMessage);
        assertEquals("empty-chat", presenter.noMessagesChatId);
    }

    /**
     * ✅ Chat does not exist.
     */
    @Test
    void chatNotFound() {
        ViewChatHistoryInputData input =
                new ViewChatHistoryInputData(
                        "does-not-exist",
                        Collections.emptyList(),
                        Collections.emptyList()
                );

        interactor.execute(input);

        assertNull(presenter.successData);
        assertNull(presenter.noMessagesChatId);
        assertNotNull(presenter.errorMessage);
        assertTrue(presenter.errorMessage.contains("Chat not found"));
    }

    // ------------------------------------------------------------------
    // Mock helper classes
    // ------------------------------------------------------------------

    private static class CapturingPresenter
            implements ViewChatHistoryOutputBoundary {

        ViewChatHistoryOutputData successData;
        String noMessagesChatId;
        String errorMessage;

        @Override
        public void prepareSuccessView(ViewChatHistoryOutputData outputData) {
            this.successData = outputData;
        }

        @Override
        public void prepareNoMessagesView(String chatId) {
            this.noMessagesChatId = chatId;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }

    private static class DummyDataAccess
            implements ViewChatHistoryDataAccessInterface {
        @Override
        public void findChatMessages(String chatId,
                                     List<String> userIds,
                                     List<String> messageIds) {
            // no-op for tests
        }
    }
}
