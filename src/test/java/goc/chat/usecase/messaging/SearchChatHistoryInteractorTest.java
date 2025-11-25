package goc.chat.usecase.messaging;

import entity.Chat;
import entity.Message;
import entity.User;
import entity.ports.ChatRepository;
import entity.ports.MessageRepository;
import entity.ports.UserRepository;
import entity.repo.InMemoryChatRepository;
import entity.repo.InMemoryMessageRepository;
import entity.repo.InMemoryUserRepository;

import use_case.messaging.search_history.SearchChatHistoryInputData;
import use_case.messaging.search_history.SearchChatHistoryInteractor;
import use_case.messaging.search_history.SearchChatHistoryOutputBoundary;
import use_case.messaging.search_history.SearchChatHistoryOutputData;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SearchChatHistoryInteractor.
 */
public class SearchChatHistoryInteractorTest {

    private ChatRepository chatRepository;
    private MessageRepository messageRepository;
    private UserRepository userRepository;
    private TestPresenter presenter;
    private SearchChatHistoryInteractor interactor;

    @BeforeEach
    public void setUp() {
        chatRepository = new InMemoryChatRepository();
        messageRepository = new InMemoryMessageRepository();
        userRepository = new InMemoryUserRepository();
        presenter = new TestPresenter();
        interactor = new SearchChatHistoryInteractor(
                chatRepository, messageRepository, userRepository, presenter
        );

        // prepare one chat, one user, and a few messages
        Chat chat = new Chat(
                "chat-1",
                "Test Group",
                Color.WHITE,
                Instant.now()
        );
        chat.addParticipant("alice");
        chatRepository.save(chat);

        User alice = new User("alice", "password");
        userRepository.save(alice);

        Message m1 = new Message(
                "m1",
                "chat-1",
                "alice",
                null,
                "hello world",
                Instant.now()
        );
        Message m2 = new Message(
                "m2",
                "chat-1",
                "alice",
                null,
                "this does not match",
                Instant.now().plusSeconds(1)
        );

        messageRepository.save(m1);
        messageRepository.save(m2);

        chat.addMessage("m1");
        chat.addMessage("m2");
    }

    @Test
    public void testSearchFindsMatchingMessages() {
        SearchChatHistoryInputData input =
                new SearchChatHistoryInputData("chat-1", "hello");

        interactor.execute(input);

        assertNull(presenter.errorMessage, "No error expected");
        assertFalse(presenter.noMatchesCalled, "No-matches presenter should not be called");
        assertNotNull(presenter.outputData, "Output data should be set");

        List<String[]> result = presenter.outputData.getMessages();
        assertEquals(1, result.size(), "Exactly one message should match");

        String[] msg = result.get(0);
        // format in interactor: [id, senderId, senderName, content, time]
        assertEquals("m1", msg[0]);
        assertEquals("alice", msg[1]);
        assertEquals("alice", msg[2]);
        assertEquals("hello world", msg[3]);
        assertNotNull(msg[4], "Timestamp string should not be null");
    }

    @Test
    public void testSearchNoMatches() {
        SearchChatHistoryInputData input =
                new SearchChatHistoryInputData("chat-1", "xyz-not-present");

        interactor.execute(input);

        assertTrue(presenter.noMatchesCalled, "No-matches presenter should be called");
        assertNull(presenter.outputData, "Output data should be null when no matches");
        assertNull(presenter.errorMessage, "Error message should be null");
    }

    @Test
    public void testSearchChatNotFound() {
        SearchChatHistoryInputData input =
                new SearchChatHistoryInputData("non-existent-chat", "hello");

        interactor.execute(input);

        assertNull(presenter.outputData, "Output data should be null when chat not found");
        assertFalse(presenter.noMatchesCalled, "No-matches presenter should not be called");
        assertNotNull(presenter.errorMessage, "Error message should be set");
        assertTrue(presenter.errorMessage.contains("Chat not found"));
    }

    @Test
    public void testSearchEmptyKeyword() {
        SearchChatHistoryInputData input =
                new SearchChatHistoryInputData("chat-1", "   ");

        interactor.execute(input);

        assertNull(presenter.outputData, "Output data should be null");
        assertFalse(presenter.noMatchesCalled, "No-matches presenter should not be called");
        assertNotNull(presenter.errorMessage, "Error message should be set for empty keyword");
        assertTrue(presenter.errorMessage.contains("must not be empty"));
    }

    /**
     * Simple presenter stub to capture interactor output.
     */
    private static class TestPresenter implements SearchChatHistoryOutputBoundary {
        SearchChatHistoryOutputData outputData;
        String errorMessage;
        boolean noMatchesCalled = false;

        @Override
        public void prepareSuccessView(SearchChatHistoryOutputData outputData) {
            this.outputData = outputData;
        }

        @Override
        public void prepareNoMatchesView(String chatId, String keyword) {
            this.noMatchesCalled = true;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }
}
