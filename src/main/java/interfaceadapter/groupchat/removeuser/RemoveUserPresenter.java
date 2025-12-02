package interfaceadapter.groupchat.removeuser;

import interfaceadapter.messaging.ChatState;
import interfaceadapter.messaging.ChatViewModel;
import usecase.groups.removeuser.RemoveUserOutputBoundary;
import usecase.groups.removeuser.RemoveUserOutputData;

public class RemoveUserPresenter implements RemoveUserOutputBoundary {
    private final ChatViewModel viewModel;

    public RemoveUserPresenter(ChatViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccessView(RemoveUserOutputData outputData) {
        final ChatState state = viewModel.getState();
        state.setError(null);
        state.removeParticipant(outputData.getRemovedUsername());
        viewModel.setState(state);
        viewModel.firePropertyChange();

        // Show success message
        System.out.println("User "
                + outputData.getRemovedUsername()
                + " removed successfully from chat " + outputData.getChatId());
    }

    @Override
    public void prepareFailView(String error) {
        final ChatState state = viewModel.getState();
        state.setError(error);
        viewModel.setState(state);
        viewModel.firePropertyChange();
    }
}
