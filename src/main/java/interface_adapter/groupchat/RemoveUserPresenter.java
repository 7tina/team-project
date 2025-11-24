package interface_adapter.groupchat;

import use_case.groups.RemoveUserOutputBoundary;
import use_case.groups.RemoveUserOutputData;

public class RemoveUserPresenter implements RemoveUserOutputBoundary {
    private final GroupChatViewModel viewModel;

    public RemoveUserPresenter(GroupChatViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccessView(RemoveUserOutputData outputData) {
        GroupChatState state = viewModel.getState();
        state.setError(null);
        viewModel.setState(state);
        viewModel.firePropertyChange();

        // Show success message
        System.out.println("User " + outputData.getRemovedUsername() +
                " removed successfully from chat " + outputData.getChatId());
    }

    @Override
    public void prepareFailView(String error) {
        GroupChatState state = viewModel.getState();
        state.setError(error);
        viewModel.setState(state);
        viewModel.firePropertyChange();
    }
}