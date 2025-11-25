package interface_adapter.messaging.search_history;

import entity.Message;
import interface_adapter.messaging.send_m.ChatState;
import interface_adapter.messaging.send_m.ChatViewModel;
import use_case.messaging.search_history.SearchChatHistoryOutputBoundary;
import use_case.messaging.search_history.SearchChatHistoryOutputData;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
        ChatState state = chatViewModel.getState();
        state.clearMessages();
        state.clearMessageIds();

        List<Message> messages = outputData.getMatchingMessages();
        for (Message m : messages) {
            String[] formattedMessage = new String[]{
                    m.getId(),
                    m.getSenderUserId(),
                    m.getContent(),
                    formatTimestamp(m.getTimestamp())
            };
            state.addMessage(formattedMessage);
            state.addMessageId(m.getId());
        }

        state.setError(null);
        chatViewModel.firePropertyChange();
    }

    @Override
    public void prepareNoMatchesView(String chatId, String keyword) {
        ChatState state = chatViewModel.getState();
        state.clearMessages();
        state.clearMessageIds();
        state.setError("No messages found containing keyword: \"" + keyword + "\".");
        chatViewModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(String errorMessage) {
        ChatState state = chatViewModel.getState();
        state.setError(errorMessage);
        chatViewModel.firePropertyChange();
    }

    private String formatTimestamp(Instant timestamp) {
        if (timestamp == null) return "";
        ZoneId zone = ZoneId.of("UTC");
        ZonedDateTime zdt = timestamp.atZone(zone);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return zdt.format(formatter);
    }
}
