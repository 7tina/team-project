package interface_adapter.messaging.search_history;

import entity.Message;
import interface_adapter.messaging.send_m.ChatState;
import interface_adapter.messaging.send_m.ChatViewModel;
import use_case.messaging.search_history.SearchChatHistoryOutputBoundary;
import use_case.messaging.search_history.SearchChatHistoryOutputData;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Presenter for the SearchChatHistory use case.
 * Adapts output data into ChatViewModel / ChatState for the UI.
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
        state.setError(null);

        // Convert each Message into the same String[] format used elsewhere:
        // [messageId, senderUserId, content, timestampString, repliedMessageId]
        for (Message m : outputData.getMessages()) {
            String[] msg = new String[]{
                    m.getId(),
                    m.getSenderUserId(),
                    m.getContent(),
                    formatTimestamp(m.getTimestamp()),
                    m.getRepliedMessageId()
            };
            state.addMessage(msg);
            state.addMessageId(m.getId());
        }

        chatViewModel.firePropertyChange();
    }

    @Override
    public void prepareNoMatchesView(String chatId, String keyword) {
        ChatState state = chatViewModel.getState();
        state.clearMessages();
        state.setError("No messages containing \"" + keyword + "\".");
        chatViewModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(String errorMessage) {
        ChatState state = chatViewModel.getState();
        state.setError(errorMessage);
        chatViewModel.firePropertyChange();
    }

    /**
     * Format the timestamp in a human-readable way, consistent with view history.
     */
    private String formatTimestamp(java.time.Instant timestamp) {
        ZoneId zone = ZoneId.of("UTC");
        ZonedDateTime zdt = timestamp.atZone(zone);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return zdt.format(formatter);
    }
}
