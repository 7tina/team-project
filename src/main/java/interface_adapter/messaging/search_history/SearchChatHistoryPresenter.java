package interface_adapter.messaging.search_history;

import entity.Message;
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
        ChatState state = chatViewModel.getChatState();
        state.clearMessages();
        state.setError(null);

        List<Message> messages = outputData.getMessages();
        for (Message message : messages) {
            state.addMessage(message);
        }

        chatViewModel.firePropertyChange();
    }

    @Override
    public void prepareNoMatchesView(String chatId, String keyword) {
        ChatState state = chatViewModel.getChatState();
        state.clearMessages();
        state.setError("No messages containing \"" + keyword + "\".");
        chatViewModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(String errorMessage) {
        ChatState state = chatViewModel.getChatState();
        state.setError(errorMessage);
        chatViewModel.firePropertyChange();
    }
}
