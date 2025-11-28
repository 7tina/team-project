package interface_adapter.groupchat.adduser;

import interface_adapter.messaging.ChatState;
import interface_adapter.messaging.ChatViewModel;
import use_case.groups.adduser.AddUserOutputBoundary;
import use_case.groups.adduser.AddUserOutputData;

public class AddUserPresenter implements AddUserOutputBoundary {
    private final ChatViewModel viewModel;

    public AddUserPresenter(ChatViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccessView(AddUserOutputData outputData) {
        ChatState state = viewModel.getState();
        state.setError(null);
        state.addParticipant(outputData.getAddedUsername());
        viewModel.setState(state);
        viewModel.firePropertyChange();

        System.out.println("User " + outputData.getAddedUsername() +
                " added successfully to chat " + outputData.getChatId());
    }

    @Override
    public void prepareFailView(String error) {
        ChatState state = viewModel.getState();
        state.setError(error);
        viewModel.setState(state);
        viewModel.firePropertyChange();
    }
}