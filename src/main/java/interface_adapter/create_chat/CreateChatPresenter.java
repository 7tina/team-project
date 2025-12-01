package interface_adapter.create_chat;

import java.util.List;

import interface_adapter.ViewManagerModel;
import interface_adapter.messaging.ChatState;
import interface_adapter.messaging.ChatViewModel;
import use_case.create_chat.CreateChatOutputBoundary;
import use_case.create_chat.CreateChatOutputData;

/**
 * Presenter for the create chat use case.
 * Updates the {@link ChatViewModel} and {@link ViewManagerModel}
 * based on the interactor's output.
 */
public class CreateChatPresenter implements CreateChatOutputBoundary {

    private final ChatViewModel chatViewModel;
    private final ViewManagerModel viewManagerModel;

    /**
     * Constructs a CreateChatPresenter.
     *
     * @param viewManagerModel the view manager model
     * @param chatViewModel    the chat view model
     */
    public CreateChatPresenter(ViewManagerModel viewManagerModel,
                               ChatViewModel chatViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.chatViewModel = chatViewModel;
    }

    /**
     * Prepares the success view when a chat is created successfully.
     *
     * @param response the response data from the interactor
     */
    @Override
    public void prepareSuccessView(CreateChatOutputData response) {
        final ChatState state = new ChatState();
        state.setIsGroup(response.isGroupChat());
        state.setChatId(response.getChatId());

        if (response.isGroupChat()) {
            state.setGroupName(response.getGroupName());
        }
        else {
            // For one-on-one chat, use the other user's name.
            final List<String> users = response.getUsers();
            state.setGroupName(users.get(1));
        }

        for (String userId : response.getUsers()) {
            state.addParticipant(userId);
        }
        for (String messageId : response.getMessageIds()) {
            state.addMessageId(messageId);
        }

        state.setSuccess(true);
        state.setError(null);

        chatViewModel.setState(state);
        chatViewModel.firePropertyChange();

        viewManagerModel.setState("chat");
        viewManagerModel.firePropertyChange();
    }

    /**
     * Prepares the failure view when creating a chat fails.
     *
     * @param response the response data from the interactor
     */
    @Override
    public void prepareFailView(CreateChatOutputData response) {
        final ChatState state = new ChatState();
        state.setSuccess(false);
        state.setError(response.getMessage());

        chatViewModel.setState(state);
        chatViewModel.firePropertyChange();
    }
}
