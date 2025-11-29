package interface_adapter.messaging.search_history;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import entity.Message;
import interface_adapter.messaging.ChatState;
import interface_adapter.messaging.ChatViewModel;
import use_case.messaging.search_history.SearchChatHistoryOutputBoundary;
import use_case.messaging.search_history.SearchChatHistoryOutputData;

/**
 * Presenter that converts search results into ChatState updates.
 */
public class SearchChatHistoryPresenter implements SearchChatHistoryOutputBoundary {

    private final ChatViewModel chatViewModel;

    public SearchChatHistoryPresenter(ChatViewModel chatViewModel) {
        this.chatViewModel = chatViewModel;
    }

    @Override
    public void prepareSuccessView(SearchChatHistoryOutputData outputData) {
        final ChatState state = chatViewModel.getState();
        state.clearMessages();
        state.clearMessageIds();

        final List<Message> messages = outputData.getMatchingMessages();
        for (Message m : messages) {
            final String[] formattedMessage = new String[] {
                    m.getId(),
                    m.getSenderUserId(),
                    m.getContent(),
                    formatTimestamp(m.getTimestamp()),
            };
            state.addMessage(formattedMessage);
            state.addMessageId(m.getId());
        }

        state.setError(null);
        chatViewModel.firePropertyChange();
    }

    @Override
    public void prepareNoMatchesView(String chatId, String keyword) {
        final ChatState state = chatViewModel.getState();
        state.clearMessages();
        state.clearMessageIds();
        state.setError("No messages found containing keyword: \"" + keyword + "\".");
        chatViewModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(String errorMessage) {
        final ChatState state = chatViewModel.getState();
        state.setError(errorMessage);
        chatViewModel.firePropertyChange();
    }

    private String formatTimestamp(Instant timestamp) {
        String result = "";
        if (timestamp != null) {
            final ZoneId zone = ZoneId.of("UTC");
            final ZonedDateTime zdt = timestamp.atZone(zone);
            final DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            result = zdt.format(formatter);
        }
        return result;
    }
}
