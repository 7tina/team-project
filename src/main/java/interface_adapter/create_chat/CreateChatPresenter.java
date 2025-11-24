package interface_adapter.create_chat;

import interface_adapter.ViewManagerModel;
import interface_adapter.groupchat.GroupChatState;
import interface_adapter.messaging.send_m.ChatState;
import interface_adapter.messaging.send_m.ChatViewModel;
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
        state.setChatId(response.getChatId());

        String displayName = response.getGroupName();
        if (displayName == null || displayName.isEmpty()) {
            // Individual chat - use target user's name
            displayName = response.getGroupName();  // This should actually be the target username
        }

        state.setGroupName(response.getGroupName());
        for (String userId : response.getUsers()) {state.addParticipant(userId);}
        for (String messageId : response.getMessageIds()) {state.addMessageId(messageId);}
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
