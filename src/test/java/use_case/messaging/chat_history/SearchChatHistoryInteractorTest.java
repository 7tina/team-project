package use_case.messaging.chat_history;

import entity.Chat;
import entity.Message;
import entity.ports.ChatRepository;
import entity.ports.MessageRepository;
import entity.repo.InMemoryChatRepository;
import entity.repo.InMemoryMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import usecase.messaging.searchhistory.SearchChatHistoryInputBoundary;
import usecase.messaging.searchhistory.SearchChatHistoryInputData;
import usecase.messaging.searchhistory.SearchChatHistoryInteractor;
import usecase.messaging.searchhistory.SearchChatHistoryOutputBoundary;
import usecase.messaging.searchhistory.SearchChatHistoryOutputData;

import java.awt.Color;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SearchChatHistoryInteractor.
 */
public class SearchChatHistoryInteractorTest {

    private ChatRepository chatRepository;
    private MessageRepository messageRepository;
    private TestPresenter presenter;
    private SearchChatHistoryInputBoundary interactor;

    @BeforeEach
    public void setUp() {
        chatRepository = new InMemoryChatRepository();
        messageRepository = new InMemoryMessageRepository();
        presenter = new TestPresenter();
        interactor = new SearchChatHistoryInteractor(
                chatRepository, messageRepository, presenter
        );

        // Prepare one chat.
        Chat chat = new Chat(
                "chat-1",
                "Test Group",
                Color.WHITE,
                Instant.now()
        );
        chatRepository.save(chat);

        // Message constructor:
        // Message(String id, String chatId, String senderUserId,
        //         String repliedMessageId, String content, Instant timestamp)
        Message m1 = new Message(
                "m1",          // id
                "chat-1",      // chatId
                "user-1",      // senderUserId
                null,          // repliedMessageId
                "hello world", // content
                Instant.now()  // timestamp
        );
        Message m2 = new Message(
                "m2",
                "chat-1",
                "user-2",
                null,
                "something else",
                Instant.now()
        );
        Message m3 = new Message(
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

    @Test
    public void searchSuccess_returnsMatchingMessages() {
        SearchChatHistoryInputData input =
                new SearchChatHistoryInputData("chat-1", "hello");

        interactor.execute(input);

        assertNull(presenter.lastFailError);
        assertNull(presenter.lastNoMatchesChatId);

        assertNotNull(presenter.lastSuccessOutput);
        List<Message> messages = presenter.lastSuccessOutput.getMessages();

        // Should match m1 and m3 (case-insensitive).
        assertEquals(2, messages.size());
        List<String> ids = messages.stream()
                .map(Message::getId)
                .collect(Collectors.toList());
        assertTrue(ids.contains("m1"));
        assertTrue(ids.contains("m3"));
    }

    @Test
    public void searchNoMatches_callsNoMatchesView() {
        SearchChatHistoryInputData input =
                new SearchChatHistoryInputData("chat-1", "xyz");

        interactor.execute(input);

        assertNull(presenter.lastFailError);
        assertNull(presenter.lastSuccessOutput);

        assertEquals("chat-1", presenter.lastNoMatchesChatId);
        assertEquals("xyz", presenter.lastNoMatchesKeyword);
    }

    @Test
    public void chatNotFound_callsFailView() {
        SearchChatHistoryInputData input =
                new SearchChatHistoryInputData("unknown", "hello");

        interactor.execute(input);

        assertNull(presenter.lastSuccessOutput);
        assertNull(presenter.lastNoMatchesChatId);
        assertNotNull(presenter.lastFailError);
        assertTrue(presenter.lastFailError.contains("Chat not found"));
    }

    @Test
    public void emptyKeyword_callsFailView() {
        SearchChatHistoryInputData input =
                new SearchChatHistoryInputData("chat-1", "");

        interactor.execute(input);

        assertNull(presenter.lastSuccessOutput);
        assertNull(presenter.lastNoMatchesChatId);
        assertEquals("Search keyword must not be empty.", presenter.lastFailError);
    }

    // ------------------ Test Presenter ------------------

    private static class TestPresenter implements SearchChatHistoryOutputBoundary {

        SearchChatHistoryOutputData lastSuccessOutput;
        String lastFailError;
        String lastNoMatchesChatId;
        String lastNoMatchesKeyword;

        @Override
        public void prepareSuccessView(SearchChatHistoryOutputData outputData) {
            this.lastSuccessOutput = outputData;
        }

        @Override
        public void prepareNoMatchesView(String chatId, String keyword) {
            this.lastNoMatchesChatId = chatId;
            this.lastNoMatchesKeyword = keyword;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            this.lastFailError = errorMessage;
        }
    }
}
