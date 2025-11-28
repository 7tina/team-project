package interface_adapter.messaging.send_m;

import interface_adapter.ViewManagerModel;
import interface_adapter.messaging.ChatState;
import interface_adapter.messaging.ChatViewModel;
import use_case.messaging.send_m.SendMessageOutputBoundary;
import use_case.messaging.send_m.SendMessageOutputData;

public class SendMessagePresenter implements SendMessageOutputBoundary {

    private final ChatViewModel chatViewModel;
    private final ViewManagerModel viewManagerModel;

    public SendMessagePresenter(ChatViewModel chatViewModel,
                                ViewManagerModel viewManagerModel) {
        this.chatViewModel = chatViewModel;
        this.viewManagerModel = viewManagerModel;
    }

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

    @Override
    public void prepareFailView(String errorMessage) {
        ChatState state = chatViewModel.getState();
        state.setError(errorMessage);
        chatViewModel.firePropertyChange();
    }
}
