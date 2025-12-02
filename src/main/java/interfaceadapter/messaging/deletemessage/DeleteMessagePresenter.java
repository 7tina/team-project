package interfaceadapter.messaging.deletemessage;

import java.util.ArrayList;
import java.util.List;

import interfaceadapter.ViewManagerModel;
import interfaceadapter.messaging.ChatState;
import interfaceadapter.messaging.ChatViewModel;
import usecase.messaging.deletemessage.DeleteMessageOutputBoundary;
import usecase.messaging.deletemessage.DeleteMessageOutputData;

/**
 * Presenter for the delete message use case.
 * <p>
 * It removes the deleted message from the {@link ChatState} and notifies
 * the {@link ChatViewModel} so that the View can update.
 */
public class DeleteMessagePresenter implements DeleteMessageOutputBoundary {

    private final ChatViewModel chatViewModel;

    /**
     * Constructs a {@code DeleteMessagePresenter}.
     *
     * @param chatViewModel    view model for the chat screen
     * @param viewManagerModel manager controlling which view is active
     */
    public DeleteMessagePresenter(ChatViewModel chatViewModel,
                                  ViewManagerModel viewManagerModel) {
        this.chatViewModel = chatViewModel;
    }

    /**
     * Called when the message has been successfully deleted.
     * <p>
     * It filters the deleted message out of both the message list and
     * the message ID list stored in {@link ChatState}, clears any error,
     * and fires a property change so the View re-renders.
     *
     * @param outputData output data describing the deleted message
     */
    @Override
    public void prepareSuccessView(DeleteMessageOutputData outputData) {

        String deletedId = outputData.getMessageId();
        ChatState state = chatViewModel.getState();

        // ---- filter messages ----
        List<String[]> filteredMessages = new ArrayList<>();
        for (String[] msg : state.getMessages()) {
            if (!msg[0].equals(deletedId)) {   // msg[0] = messageId
                filteredMessages.add(msg);
            }
        }

        // ---- filter messageIds ----
        List<String> filteredIds = new ArrayList<>();
        for (String id : state.getMessageIds()) {
            if (!id.equals(deletedId)) {
                filteredIds.add(id);
            }
        }

        // ---- replace in state ----
        state.setMessages(filteredMessages);
        state.setMessageIds(filteredIds);

        // ---- clear error + notify view ----
        state.setError(null);
        chatViewModel.setState(state);
        chatViewModel.firePropertyChange();
    }

    /**
     * Called when deleting the message fails.
     * <p>
     * It sets an error message in the {@link ChatState} and notifies
     * the View via the {@link ChatViewModel}.
     *
     * @param outputData output data containing the failure reason
     */
    @Override
    public void prepareFailView(DeleteMessageOutputData outputData) {
        ChatState state = chatViewModel.getState();
        state.setError("Failed to delete: " + outputData.getFailReason());
        chatViewModel.setState(state);
        chatViewModel.firePropertyChange();
    }
}