package interface_adapter.groupchat;

import use_case.groups.AddUserOutputBoundary;
import use_case.groups.AddUserOutputData;

public class AddUserPresenter implements AddUserOutputBoundary {
    private final GroupChatViewModel viewModel;

    public AddUserPresenter(GroupChatViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccessView(AddUserOutputData outputData) {
        GroupChatState state = viewModel.getState();
        state.setError(null);
        viewModel.setState(state);
        viewModel.firePropertyChange();

        System.out.println("User " + outputData.getAddedUsername() +
                " added successfully to chat " + outputData.getChatId());
    }

    @Override
    public void prepareFailView(String error) {
        GroupChatState state = viewModel.getState();
        state.setError(error);
        viewModel.setState(state);
        viewModel.firePropertyChange();
    }
}