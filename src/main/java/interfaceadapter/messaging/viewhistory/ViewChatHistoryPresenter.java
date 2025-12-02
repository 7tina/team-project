package interfaceadapter.messaging.viewhistory;

import java.util.List;
import java.util.Map;

import interfaceadapter.ViewManagerModel;
import interfaceadapter.messaging.ChatState;
import interfaceadapter.messaging.ChatViewModel;
import usecase.messaging.viewhistory.ViewChatHistoryOutputBoundary;
import usecase.messaging.viewhistory.ViewChatHistoryOutputData;

/**
 * Presenter for the View Chat History use case.
 * Updates the ChatViewModel and triggers UI updates through the ViewManagerModel.
 */
public class ViewChatHistoryPresenter implements ViewChatHistoryOutputBoundary {

    private final ChatViewModel chatViewModel;
    private final ViewManagerModel viewManagerModel;

    /**
     * Constructs a ViewChatHistoryPresenter.
     *
     * @param chatViewModel   the ChatViewModel that holds chat state
     * @param viewManagerModel the ViewManagerModel that manages the active view
     */
    public ViewChatHistoryPresenter(final ChatViewModel chatViewModel,
                                    final ViewManagerModel viewManagerModel) {
        this.chatViewModel = chatViewModel;
        this.viewManagerModel = viewManagerModel;
    }

    /**
     * Prepares the success view by updating the chat state with messages and reactions.
     *
     * @param outputData the output data containing chat messages and reactions
     */
    @Override
    public void prepareSuccessView(final ViewChatHistoryOutputData outputData) {
        final ChatState state = chatViewModel.getState();

        state.clearMessageIds();
        state.clearMessages();
        state.clearReactions();
        state.setError(null);

        final List<String[]> messages = outputData.getMessages();
        for (String[] message : messages) {
            state.addMessage(message);
        }

        final Map<String, Map<String, String>> msgToReactions = outputData.getReactions();
        for (Map.Entry<String, Map<String, String>> entry : msgToReactions.entrySet()) {
            final String messageId = entry.getKey();
            final Map<String, String> reactions = entry.getValue();
            for (Map.Entry<String, String> reaction : reactions.entrySet()) {
                state.addReaction(messageId, reaction.getKey(), reaction.getValue());
            }
        }

        chatViewModel.firePropertyChange();
    }

    /**
     * Prepares the view when there are no messages in the chat.
     *
     * @param chatId the id of the chat
     */
    @Override
    public void prepareNoMessagesView(final String chatId) {
        final ChatState state = chatViewModel.getState();
        state.clearMessages();
        state.setError(null);
        chatViewModel.firePropertyChange();
    }

    /**
     * Prepares the view when the chat history loading fails.
     *
     * @param errorMessage the error message to display
     */
    @Override
    public void prepareFailView(final String errorMessage) {
        final ChatState state = chatViewModel.getState();
        state.setError(errorMessage);
        chatViewModel.firePropertyChange();
    }
}
