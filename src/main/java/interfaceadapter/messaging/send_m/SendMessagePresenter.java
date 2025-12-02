package interfaceadapter.messaging.send_m;

import interfaceadapter.ViewManagerModel;
import interfaceadapter.messaging.ChatState;
import interfaceadapter.messaging.ChatViewModel;
import usecase.messaging.send_m.SendMessageOutputBoundary;
import usecase.messaging.send_m.SendMessageOutputData;

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
