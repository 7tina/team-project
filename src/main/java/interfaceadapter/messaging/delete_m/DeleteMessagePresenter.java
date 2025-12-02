package interfaceadapter.messaging.delete_m;

import java.util.ArrayList;
import java.util.List;

import interfaceadapter.ViewManagerModel;
import interfaceadapter.messaging.ChatState;
import interfaceadapter.messaging.ChatViewModel;
import usecase.messaging.delete_m.DeleteMessageOutputBoundary;
import usecase.messaging.delete_m.DeleteMessageOutputData;

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

        final String deletedId = outputData.getMessageId();
        final ChatState state = chatViewModel.getState();

        // ---- filter messages ----
        final List<String[]> filteredMessages = new ArrayList<>();
        for (String[] msg : state.getMessages()) {
            // msg[0] = messageId
            if (!msg[0].equals(deletedId)) {
                filteredMessages.add(msg);
            }
        }

        // ---- filter messageIds ----
        final List<String> filteredIds = new ArrayList<>();
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
        final ChatState state = chatViewModel.getState();
        state.setError("Failed to delete: " + outputData.getFailReason());
        chatViewModel.setState(state);
        chatViewModel.firePropertyChange();
    }
}
