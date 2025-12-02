package interfaceadapter.accesschat;

import interfaceadapter.ViewManagerModel;
import interfaceadapter.logged_in.LoggedInState;
import interfaceadapter.logged_in.LoggedInViewModel;
import interfaceadapter.messaging.ChatState;
import interfaceadapter.messaging.ChatViewModel;
import usecase.accesschat.AccessChatOutputBoundary;
import usecase.accesschat.AccessChatOutputData;

public class AccessChatPresenter implements AccessChatOutputBoundary {

    private final ChatViewModel chatViewModel;
    private final LoggedInViewModel loggedInViewModel;
    private final ViewManagerModel viewManagerModel;

    /**
     * Constructs a AccessChatPresenter.
     *
     * @param viewManagerModel the view manager model
     * @param loggedInViewModel the logged in view model
     * @param chatViewModel    the chat view model
     */
    public AccessChatPresenter(ViewManagerModel viewManagerModel,
                               LoggedInViewModel loggedInViewModel,
                               ChatViewModel chatViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.loggedInViewModel = loggedInViewModel;
        this.chatViewModel = chatViewModel;
    }

    /**
     * Prepares the success view when a chat is created successfully.
     *
     * @param response the response data from the interactor
     */
    @Override
    public void prepareSuccessView(AccessChatOutputData response) {
        final ChatState state = new ChatState();

        state.chatViewStop();

        state.setIsGroup(response.isGroupChat());
        state.setChatId(response.getChatId());

        if (response.isGroupChat()) {
            state.setGroupName(response.getGroupName());
        }
        else {
            // For individual chats, find the other user (not the current user)
            final String currentUserId = response.getCurrentUserId();
            String otherUsername = null;

            for (String userId : response.getUsers()) {
                if (!userId.equals(currentUserId)) {
                    otherUsername = userId;
                    break;
                }
            }

            if (otherUsername != null) {
                state.setGroupName(otherUsername);
            }
            else {
                state.setGroupName("Unknown User");
            }
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
     * Prepares the failure view when finding a chat fails.
     *
     * @param errorMessage the output data from the interactor with explanation of failure.
     */
    @Override
    public void prepareFailView(String errorMessage) {
        final LoggedInState state = loggedInViewModel.getState();
        state.setRecentChatsError(errorMessage);

        loggedInViewModel.setState(state);
        loggedInViewModel.firePropertyChange("access chat");
    }
}
