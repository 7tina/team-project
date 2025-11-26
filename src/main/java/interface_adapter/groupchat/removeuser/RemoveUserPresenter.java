package interface_adapter.groupchat.removeuser;

import interface_adapter.messaging.ChatState;
import interface_adapter.messaging.ChatViewModel;
import use_case.groups.removeuser.RemoveUserOutputBoundary;
import use_case.groups.removeuser.RemoveUserOutputData;

public class RemoveUserPresenter implements RemoveUserOutputBoundary {
    private final ChatViewModel viewModel;

    public RemoveUserPresenter(ChatViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccessView(RemoveUserOutputData outputData) {
        ChatState state = viewModel.getState();
        state.setError(null);
        state.removeParticipant(outputData.getRemovedUsername());
        viewModel.setState(state);
        viewModel.firePropertyChange();

        // Show success message
        System.out.println("User " + outputData.getRemovedUsername() +
                " removed successfully from chat " + outputData.getChatId());
    }

    @Override
    public void prepareFailView(String error) {
        ChatState state = viewModel.getState();
        state.setError(error);
        viewModel.setState(state);
        viewModel.firePropertyChange();
    }
}