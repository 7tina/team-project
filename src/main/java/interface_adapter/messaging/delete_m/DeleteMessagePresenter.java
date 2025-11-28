package interface_adapter.messaging.delete_m;

import interface_adapter.ViewManagerModel;
import interface_adapter.messaging.ChatState;
import interface_adapter.messaging.ChatViewModel;
import use_case.messaging.delete_m.DeleteMessageOutputBoundary;
import use_case.messaging.delete_m.DeleteMessageOutputData;

import java.util.ArrayList;
import java.util.List;

public class DeleteMessagePresenter implements DeleteMessageOutputBoundary {

    private final ChatViewModel chatViewModel;
    private final ViewManagerModel viewManagerModel;

    public DeleteMessagePresenter(ChatViewModel chatViewModel,
                                  ViewManagerModel viewManagerModel) {
        this.chatViewModel = chatViewModel;
        this.viewManagerModel = viewManagerModel;
    }

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

    @Override
    public void prepareFailView(DeleteMessageOutputData outputData) {
        ChatState state = chatViewModel.getState();
        state.setError("Failed to delete: " + outputData.getFailReason());
        chatViewModel.setState(state);
        chatViewModel.firePropertyChange();
    }
}
