package interface_adapter.groupchat.changegroupname;


import interface_adapter.messaging.ChatState;
import interface_adapter.messaging.ChatViewModel;
import use_case.groups.changegroupname.ChangeGroupNameOutputBoundary;
import use_case.groups.changegroupname.ChangeGroupNameOutputData;

/**
 * Presenter for the Change Group Name use case.
 * Converts output data to view model state.
 */
public class ChangeGroupNamePresenter implements ChangeGroupNameOutputBoundary {

    private final ChatViewModel viewModel;

    public ChangeGroupNamePresenter(ChatViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccessView(ChangeGroupNameOutputData outputData) {
        // Update the view model with success
        ChatState state = viewModel.getState();
        state.setChatId(outputData.getChatId());
        state.setGroupName(outputData.getNewGroupName());
        state.setSuccess(true);
        state.setError(null);

        viewModel.setState(state);
        viewModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(ChangeGroupNameOutputData outputData) {
        // Update the view model with error
        ChatState state = viewModel.getState();
        state.setChatId(outputData.getChatId());
        state.setSuccess(false);
        state.setError(outputData.getErrorMessage());

        viewModel.setState(state);
        viewModel.firePropertyChange();
    }
}