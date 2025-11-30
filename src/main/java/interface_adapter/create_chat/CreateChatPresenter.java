package interface_adapter.create_chat;

import interface_adapter.ViewManagerModel;
import interface_adapter.messaging.ChatState;
import interface_adapter.messaging.ChatViewModel;
import use_case.create_chat.CreateChatOutputBoundary;
import use_case.create_chat.CreateChatOutputData;

public class CreateChatPresenter implements CreateChatOutputBoundary {

    private final ChatViewModel chatViewModel;
    private final ViewManagerModel viewManagerModel;

    public CreateChatPresenter(
            ViewManagerModel viewManagerModel,
            ChatViewModel chatViewModel
    ) {
        this.viewManagerModel = viewManagerModel;
        this.chatViewModel = chatViewModel;
    }

    @Override
    public void prepareSuccessView(CreateChatOutputData response) {
        ChatState state = new ChatState();
        state.setIsGroup(response.isGroupChat());
        state.setChatId(response.getChatId());

        if (response.isGroupChat()) {
            state.setGroupName(response.getGroupName());
        } else {
            // For individual chats, find the other user (not the current user)
            String currentUserId = response.getCurrentUserId();
            String otherUsername = null;

//            System.out.println("DEBUG: Current user ID: " + currentUserId);
//            System.out.println("DEBUG: All users in chat: " + response.getUsers());

            for (String userId : response.getUsers()) {
//                System.out.println("DEBUG: Checking user: " + userId);
                if (!userId.equals(currentUserId)) {
                    otherUsername = userId;
//                    System.out.println("DEBUG: Found other user: " + otherUsername);
                    break;
                }
            }

            if (otherUsername == null) {
//                System.out.println("DEBUG: ERROR - Could not find other user!");
//                System.out.println("DEBUG: Users list: " + response.getUsers());
//                System.out.println("DEBUG: Current user: " + currentUserId);
            }

            state.setGroupName(otherUsername != null ? otherUsername : "Unknown User");
//            System.out.println("DEBUG: Set group name to: " + state.getGroupName());
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

    @Override
    public void prepareFailView(CreateChatOutputData response) {
        ChatState state = new ChatState();
        state.setSuccess(false);
        state.setError(response.getMessage());

        chatViewModel.setState(state);
        chatViewModel.firePropertyChange();
    }
}