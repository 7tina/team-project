package interfaceadapter.groupchat.adduser;

import interfaceadapter.messaging.ChatState;
import interfaceadapter.messaging.ChatViewModel;
import usecase.groups.adduser.AddUserOutputBoundary;
import usecase.groups.adduser.AddUserOutputData;

public class AddUserPresenter implements AddUserOutputBoundary {
    private final ChatViewModel viewModel;

    public AddUserPresenter(ChatViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccessView(AddUserOutputData outputData) {
        final ChatState state = viewModel.getState();
        state.setError(null);
        state.addParticipant(outputData.getAddedUsername());
        viewModel.setState(state);
        viewModel.firePropertyChange();

        System.out.println("User "
                + outputData.getAddedUsername()
                + " added successfully to chat "
                + outputData.getChatId());
    }

    @Override
    public void prepareFailView(String error) {
        final ChatState state = viewModel.getState();
        state.setError(error);
        viewModel.setState(state);
        viewModel.firePropertyChange();
    }
}
