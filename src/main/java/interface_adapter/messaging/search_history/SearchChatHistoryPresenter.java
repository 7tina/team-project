package interface_adapter.messaging.search_history;

import interface_adapter.messaging.send_m.ChatState;
import interface_adapter.messaging.send_m.ChatViewModel;
import use_case.messaging.search_history.SearchChatHistoryOutputBoundary;
import use_case.messaging.search_history.SearchChatHistoryOutputData;

import java.util.List;

public class SearchChatHistoryPresenter implements SearchChatHistoryOutputBoundary {

    private final ChatViewModel chatViewModel;

    public SearchChatHistoryPresenter(ChatViewModel chatViewModel) {
        this.chatViewModel = chatViewModel;
    }

    @Override
    public void prepareSuccessView(SearchChatHistoryOutputData outputData) {
        ChatState state = chatViewModel.getState();

        // Clear existing chat data before showing the search result
        state.clearMessageIds();
        state.clearMessages();
        state.clearReactions();
        state.setError(null);
        state.chatViewStart();

        // Add filtered messages to the state (same format as ViewChatHistory)
        List<String[]> messages = outputData.getMessages();
        for (String[] m : messages) {
            state.addMessage(m);
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
}
