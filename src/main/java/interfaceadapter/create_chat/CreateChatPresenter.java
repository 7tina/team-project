package interfaceadapter.create_chat;

import interfaceadapter.ViewManagerModel;
import interfaceadapter.messaging.ChatState;
import interfaceadapter.messaging.ChatViewModel;
import interfaceadapter.search_user.SearchUserState;
import interfaceadapter.search_user.SearchUserViewModel;
import usecase.create_chat.CreateChatOutputBoundary;
import usecase.create_chat.CreateChatOutputData;

/**
 * Presenter for the create chat use case.
 * Updates the {@link ChatViewModel} and {@link ViewManagerModel}
 * based on the interactor's output.
 */
public class CreateChatPresenter implements CreateChatOutputBoundary {

    private final ChatViewModel chatViewModel;
    private final ViewManagerModel viewManagerModel;
    private final SearchUserViewModel searchUserViewModel;

    /**
     * Constructs a CreateChatPresenter.
     *
     * @param viewManagerModel the view manager model
     * @param chatViewModel    the chat view model
     * @param searchUserViewModel the search user view model
     */
    public CreateChatPresenter(ViewManagerModel viewManagerModel,
                               ChatViewModel chatViewModel,
                               SearchUserViewModel searchUserViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.chatViewModel = chatViewModel;
        this.searchUserViewModel = searchUserViewModel;
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
        state.setCurrentUserId(response.getCurrentUserId());

        if (response.isGroupChat()) {
            state.setGroupName(response.getGroupName());
        }
        else {
            // For individual chats, find the other user (not the current user)
            final String currentUserId = response.getCurrentUserId();
            String otherUsername = null;

            // System.out.println("DEBUG: Current user ID: " + currentUserId);
            // System.out.println("DEBUG: All users in chat: " + response.getUsers());

            for (String userId : response.getUsers()) {
                // System.out.println("DEBUG: Checking user: " + userId);
                if (!userId.equals(currentUserId)) {
                    otherUsername = userId;
                    // System.out.println("DEBUG: Found other user: " + otherUsername);
                    break;
                }
            }

            if (otherUsername == null) {
                // System.out.println("DEBUG: ERROR - Could not find other user!");
                // System.out.println("DEBUG: Users list: " + response.getUsers());
                // System.out.println("DEBUG: Current user: " + currentUserId);
            }

            state.setGroupName(otherUsername != null ? otherUsername : "Unknown User");
            // System.out.println("DEBUG: Set group name to: " + state.getGroupName());
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
        final SearchUserState state = searchUserViewModel.getState();
        state.setCreateError(null);
        state.setCreateError(response.getMessage());

        searchUserViewModel.setState(state);
        searchUserViewModel.firePropertyChanged();
    }
}