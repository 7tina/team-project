package interface_adapter.messaging.view_history;

import interface_adapter.ViewManagerModel;
import interface_adapter.messaging.ChatState;
import interface_adapter.messaging.ChatViewModel;
import use_case.messaging.view_history.ViewChatHistoryOutputBoundary;
import use_case.messaging.view_history.ViewChatHistoryOutputData;

import java.util.List;
import java.util.Map;

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

        state.clearMessageIds();
        state.clearMessages();
        state.clearReactions();
        state.setError(null);
        state.chatViewStart();

        List<String[]> messages = outputData.getMessages();
        for (String[] m : messages) {
            state.addMessage(m);
        }

        Map<String, Map<String, String>> msgToReaction = outputData.getReactions();
        for (Map.Entry<String, Map<String, String>> entry : msgToReaction.entrySet()) {
            String messageId = entry.getKey();
            for (Map.Entry<String, String> reaction : entry.getValue().entrySet()) {
                state.addReaction(messageId, reaction.getKey(), reaction.getValue());
            }
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
