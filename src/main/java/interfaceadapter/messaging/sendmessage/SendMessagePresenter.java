package interfaceadapter.messaging.sendmessage;

import interfaceadapter.ViewManagerModel;
import interfaceadapter.messaging.ChatState;
import interfaceadapter.messaging.ChatViewModel;
import usecase.messaging.sendmessage.SendMessageOutputBoundary;
import usecase.messaging.sendmessage.SendMessageOutputData;

/**
 * Presenter for the send message use case.
 * <p>
 * It updates the {@link ChatState} in {@link ChatViewModel} when a new
 * message is sent, and notifies both the chat view and the view manager.
 */
public class SendMessagePresenter implements SendMessageOutputBoundary {

    private final ChatViewModel chatViewModel;
    private final ViewManagerModel viewManagerModel;

    /**
     * Constructs a {@code SendMessagePresenter}.
     *
     * @param chatViewModel    view model for the chat screen
     * @param viewManagerModel manager that controls which view is active
     */
    public SendMessagePresenter(ChatViewModel chatViewModel,
                                ViewManagerModel viewManagerModel) {
        this.chatViewModel = chatViewModel;
        this.viewManagerModel = viewManagerModel;
    }

    /**
     * Called when the message is sent successfully.
     * <p>
     * It appends the new message and its ID to the {@link ChatState},
     * clears any previous error, and notifies the view and view manager.
     *
     * @param outputData output data describing the sent message
     */
    @Override
    public void prepareSuccessView(SendMessageOutputData outputData) {
        ChatState state = chatViewModel.getState();
        String[] msg = outputData.getMessage();

        state.addMessage(msg);
        state.addMessageId(msg[0]);
        state.setError(null);

        chatViewModel.firePropertyChange();

        viewManagerModel.setState(chatViewModel.getViewName());
        viewManagerModel.firePropertyChange();
    }

    /**
     * Called when sending the message fails.
     * <p>
     * It records the error message in {@link ChatState} and notifies the view.
     *
     * @param errorMessage description of the error that occurred
     */
    @Override
    public void prepareFailView(String errorMessage) {
        ChatState state = chatViewModel.getState();
        state.setError(errorMessage);
        chatViewModel.firePropertyChange();
    }
}
