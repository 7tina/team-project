package interface_adapter.messaging.view_history;

import interface_adapter.ViewManagerModel;
import interface_adapter.messaging.send_m.ChatState;
import interface_adapter.messaging.send_m.ChatViewModel;
import use_case.messaging.view_history.ViewChatHistoryOutputBoundary;
import use_case.messaging.view_history.ViewChatHistoryOutputData;

import java.util.List;

public class ViewChatHistoryPresenter implements ViewChatHistoryOutputBoundary {

    private final ChatViewModel chatViewModel;
    private final ViewManagerModel viewManagerModel;

    public ViewChatHistoryPresenter(ChatViewModel chatViewModel,
                                    ViewManagerModel viewManagerModel) {
        this.chatViewModel = chatViewModel;
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void prepareSuccessView(ViewChatHistoryOutputData outputData) {
        ChatState state = chatViewModel.getState();

        state.clearMessages();
        state.setError(null);
        state.chatViewStart();

        List<String[]> messages = outputData.getMessages();
        for (String[] m : messages) {
            state.addMessage(m);
        }

        chatViewModel.firePropertyChange();
    }

    @Override
    public void prepareNoMessagesView(String chatId) {
        ChatState state = chatViewModel.getState();
        state.clearMessages();
        state.setError(null);
        chatViewModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(String errorMessage) {
        ChatState state = chatViewModel.getState();
        state.setError(errorMessage);
        chatViewModel.firePropertyChange();
    }
}
