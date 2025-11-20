package goc.chat.usecase.messaging;

import entity.Chat;
import entity.Message;
import entity.UserFactory;
import entity.User;
import entity.repo.InMemoryChatRepository;
import entity.repo.InMemoryMessageRepository;
import entity.repo.InMemoryUserRepository;
import use_case.messaging.*;
import use_case.messaging.view_history.ViewChatHistoryInputData;
import use_case.messaging.view_history.ViewChatHistoryInteractor;
import use_case.messaging.view_history.ViewChatHistoryOutputBoundary;
import use_case.messaging.view_history.ViewChatHistoryOutputData;
import entity.ports.ChatRepository;
import entity.ports.MessageRepository;
import entity.ports.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ViewChatHistoryInteractor.
 */
public class ViewChatHistoryInteractorTest {

    private ChatRepository chatRepo;
    private MessageRepository messageRepo;
    private UserRepository userRepo;
    private CapturingPresenter presenter;
    private ViewChatHistoryInteractor interactor;

    @BeforeEach
    void setUp() {
        // use in-memory repositories as fake database
        chatRepo = new InMemoryChatRepository();
        messageRepo = new InMemoryMessageRepository();
        userRepo = new InMemoryUserRepository();
        presenter = new CapturingPresenter();

        interactor = new ViewChatHistoryInteractor(
                chatRepo, messageRepo, userRepo, presenter
        );
    }

    /**
     * Happy path: chat exists and has two messages.
     * They should be returned in chronological order (oldest -> newest).
     */
    @Test
    void testMessagesAreReturnedInChronologicalOrder() {
        // 1. create chat
        Chat chat = new Chat("chat-1");
        chatRepo.save(chat);

        // 2. create users
        UserFactory userFactory = new UserFactory();
        User alice = userFactory.create("u1", "Alice", "alice@example.com");
        User bob   = userFactory.create("u2", "Bob",   "bob@example.com");
        userRepo.save(alice);
        userRepo.save(bob);

        // 3. create messages (save them in reverse time order on purpose)
        Message later = new Message(
                "m2",
                "chat-1",
                "u2",
                "Later message",
                Instant.parse("2025-11-14T11:00:00Z")
        );
        Message earlier = new Message(
                "m1",
                "chat-1",
                "u1",
                "Earlier message",
                Instant.parse("2025-11-14T10:00:00Z")
        );

        messageRepo.save(later);
        messageRepo.save(earlier);

        // 4. execute use case
        ViewChatHistoryInputData input =
                new ViewChatHistoryInputData("chat-1");
        interactor.execute(input);

        // 5. assertions: success branch, messages sorted
        assertNotNull(presenter.successData);
        assertNull(presenter.errorMessage);
        assertNull(presenter.noMessagesChatId);

        List<ChatMessageDto> msgs = presenter.successData.getMessages();
        assertEquals(2, msgs.size());

        // first message should be the earlier one
        assertEquals("Earlier message", msgs.get(0).getContent());
        assertEquals("Alice", msgs.get(0).getSenderName());

        // second message should be the later one
        assertEquals("Later message", msgs.get(1).getContent());
        assertEquals("Bob", msgs.get(1).getSenderName());
    }

    /**
     * Chat exists but has no messages.
     */
    @Test
    void testNoMessagesInChat() {
        Chat chat = new Chat("empty-chat");
        chatRepo.save(chat);

        ViewChatHistoryInputData input =
                new ViewChatHistoryInputData("empty-chat");
        interactor.execute(input);

        assertNull(presenter.successData);
        assertNull(presenter.errorMessage);
        assertEquals("empty-chat", presenter.noMessagesChatId);
    }

    /**
     * Chat does not exist.
     */
    @Test
    void testChatNotFound() {
        ViewChatHistoryInputData input =
                new ViewChatHistoryInputData("does-not-exist");
        interactor.execute(input);

        assertNull(presenter.successData);
        assertNull(presenter.noMessagesChatId);
        assertNotNull(presenter.errorMessage);
        assertTrue(presenter.errorMessage.contains("Chat not found"));
    }

    /**
     * Simple presenter used only for capturing what the interactor sends out.
     */
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
}



