package interfaceadapter.messaging.view_history;

import java.util.List;
import java.util.Map;

import interfaceadapter.ViewManagerModel;
import interfaceadapter.messaging.ChatState;
import interfaceadapter.messaging.ChatViewModel;
import usecase.messaging.view_history.ViewChatHistoryOutputBoundary;
import usecase.messaging.view_history.ViewChatHistoryOutputData;

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
        final ChatState state = chatViewModel.getState();

        state.clearMessageIds();
        state.clearMessages();
        state.clearReactions();
        state.setError(null);

        final List<String[]> messages = outputData.getMessages();
        for (String[] m : messages) {
            state.addMessage(m);
        }

        final Map<String, Map<String, String>> msgToReaction = outputData.getReactions();
        for (Map.Entry<String, Map<String, String>> entry : msgToReaction.entrySet()) {
            final String messageId = entry.getKey();
            for (Map.Entry<String, String> reaction : entry.getValue().entrySet()) {
                state.addReaction(messageId, reaction.getKey(), reaction.getValue());
            }
        }

        chatViewModel.firePropertyChange();
    }

    @Override
    public void prepareNoMessagesView(String chatId) {
        final ChatState state = chatViewModel.getState();
        state.clearMessages();
        state.setError(null);
        chatViewModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(String errorMessage) {
        final ChatState state = chatViewModel.getState();
        state.setError(errorMessage);
        chatViewModel.firePropertyChange();
    }
}
