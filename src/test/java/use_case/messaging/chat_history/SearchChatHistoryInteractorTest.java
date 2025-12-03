package use_case.messaging.search_history;

import entity.Chat;
import entity.Message;
import entity.repo.InMemoryChatRepository;
import entity.repo.InMemoryMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import usecase.messaging.search_history.SearchChatHistoryInputBoundary;
import usecase.messaging.search_history.SearchChatHistoryInputData;
import usecase.messaging.search_history.SearchChatHistoryInteractor;
import usecase.messaging.search_history.SearchChatHistoryOutputBoundary;
import usecase.messaging.search_history.SearchChatHistoryOutputData;

import java.awt.Color;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Final working version for SearchChatHistoryInteractor tests.
 */
public class SearchChatHistoryInteractorTest {

    private InMemoryChatRepository chatRepository;
    private InMemoryMessageRepository messageRepository;
    private CapturingPresenter presenter;
    private SearchChatHistoryInputBoundary interactor;

    @BeforeEach
    public void setUp() {
        chatRepository = new InMemoryChatRepository();
        messageRepository = new InMemoryMessageRepository();
        presenter = new CapturingPresenter();
        interactor = new SearchChatHistoryInteractor(chatRepository, messageRepository, presenter);

        // Prepare one chat.
        final Chat chat = new Chat("chat-1", "Test Group", Color.WHITE, Instant.now());
        chatRepository.save(chat);

        /*
         * Message constructor:
         * Message(String id, String chatId, String senderUserId,
         *         String repliedMessageId, String content, Instant timestamp)
         */
        final Message m1 = new Message(
                "m1",
                "chat-1",
                "user-1",
                null,
                "hello world",
                Instant.now()
        );
        final Message m2 = new Message(
                "m2",
                "chat-1",
                "user-2",
                null,
                "something else",
                Instant.now()
        );
        final Message m3 = new Message(
                "m3",
                "chat-1",
                "user-1",
                null,
                "HELLO again",
                Instant.now()
        );

        messageRepository.save(m1);
        messageRepository.save(m2);
        messageRepository.save(m3);
    }

    /**
     * Happy path: messages containing the keyword (case-insensitive) are returned.
     */
    @Test
    public void searchSuccessReturnsMatchingMessages() {
        final SearchChatHistoryInputData input =
                new SearchChatHistoryInputData("chat-1", "hello");

        interactor.execute(input);

        assertNull(presenter.getLastFailError());
        assertNull(presenter.getLastNoMatchesChatId());

        assertNotNull(presenter.getLastSuccessOutput());
        final List<Message> messages = presenter.getLastSuccessOutput().getMessages();

        // Should match m1 and m3 (case-insensitive).
        assertEquals(2, messages.size());
        final List<String> ids = messages.stream()
                .map(Message::getId)
                .collect(Collectors.toList());
        assertTrue(ids.contains("m1"));
        assertTrue(ids.contains("m3"));
    }

    /**
     * No messages contain the keyword → call prepareNoMatchesView.
     */
    @Test
    public void searchNoMatchesCallsNoMatchesView() {
        final SearchChatHistoryInputData input =
                new SearchChatHistoryInputData("chat-1", "xyz");

        interactor.execute(input);

        assertNull(presenter.getLastFailError());
        assertNull(presenter.getLastSuccessOutput());

        assertEquals("chat-1", presenter.getLastNoMatchesChatId());
        assertEquals("xyz", presenter.getLastNoMatchesKeyword());
    }

    /**
     * Chat not found → call prepareFailView.
     */
    @Test
    public void chatNotFoundCallsFailView() {
        final SearchChatHistoryInputData input =
                new SearchChatHistoryInputData("unknown", "hello");

        interactor.execute(input);

        assertNull(presenter.getLastSuccessOutput());
        assertNull(presenter.getLastNoMatchesChatId());
        assertNotNull(presenter.getLastFailError());
        assertTrue(presenter.getLastFailError().contains("Chat not found"));
    }

    /**
     * Empty keyword → call prepareFailView.
     */
    @Test
    public void emptyKeywordCallsFailView() {
        final SearchChatHistoryInputData input =
                new SearchChatHistoryInputData("chat-1", "");

        interactor.execute(input);

        assertNull(presenter.getLastSuccessOutput());
        assertNull(presenter.getLastNoMatchesChatId());
        assertEquals("Search keyword must not be empty.", presenter.getLastFailError());
    }

    /**
     * Keyword with only whitespace → also treated as empty.
     */
    @Test
    public void whitespaceOnlyKeywordCallsFailView() {
        final SearchChatHistoryInputData input =
                new SearchChatHistoryInputData("chat-1", "   ");

        interactor.execute(input);

        assertNull(presenter.getLastSuccessOutput());
        assertNull(presenter.getLastNoMatchesChatId());
        assertEquals("Search keyword must not be empty.", presenter.getLastFailError());
    }

    /**
     * Keyword with leading/trailing whitespace → trimmed then searched successfully.
     */
    @Test
    public void keywordWithWhitespaceTrimmedAndSearchesSuccessfully() {
        final SearchChatHistoryInputData input =
                new SearchChatHistoryInputData("chat-1", "  hello  ");

        interactor.execute(input);

        assertNull(presenter.getLastFailError());
        assertNull(presenter.getLastNoMatchesChatId());
        assertNotNull(presenter.getLastSuccessOutput());

        final List<Message> messages = presenter.getLastSuccessOutput().getMessages();
        assertEquals(2, messages.size());
    }

    // ------------------ Capturing Presenter ------------------

    private static class CapturingPresenter implements SearchChatHistoryOutputBoundary {

        private SearchChatHistoryOutputData lastSuccessOutput;
        private String lastFailError;
        private String lastNoMatchesChatId;
        private String lastNoMatchesKeyword;

        @Override
        public void prepareSuccessView(final SearchChatHistoryOutputData outputData) {
            this.lastSuccessOutput = outputData;
        }

        @Override
        public void prepareNoMatchesView(final String chatId, final String keyword) {
            this.lastNoMatchesChatId = chatId;
            this.lastNoMatchesKeyword = keyword;
        }

        @Override
        public void prepareFailView(final String errorMessage) {
            this.lastFailError = errorMessage;
        }

        public SearchChatHistoryOutputData getLastSuccessOutput() {
            return lastSuccessOutput;
        }

        public String getLastFailError() {
            return lastFailError;
        }

        public String getLastNoMatchesChatId() {
            return lastNoMatchesChatId;
        }

        public String getLastNoMatchesKeyword() {
            return lastNoMatchesKeyword;
        }
    }
}
