package interface_adapter.messaging.view_history;

import interface_adapter.ViewManagerModel;
import interface_adapter.messaging.send_m.ChatState;
import interface_adapter.messaging.send_m.ChatViewModel;
import use_case.messaging.ChatMessageDto;
import use_case.messaging.view_history.ViewChatHistoryOutputBoundary;
import use_case.messaging.view_history.ViewChatHistoryOutputData;

import java.util.List;

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
        ChatState state = chatViewModel.getChatState();

        state.clearMessages();
        state.setError(null);

        List<ChatMessageDto> messages = outputData.getMessages();
        for (ChatMessageDto dto : messages) {
            state.addMessage(dto);
        }

        chatViewModel.firePropertyChange();
    }

    @Override
    public void prepareNoMessagesView(String chatId) {
        ChatState state = chatViewModel.getChatState();
        state.clearMessages();
        state.setError(null);
        chatViewModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(String errorMessage) {
        ChatState state = chatViewModel.getChatState();
        state.setError(errorMessage);
        chatViewModel.firePropertyChange();
    }
}
