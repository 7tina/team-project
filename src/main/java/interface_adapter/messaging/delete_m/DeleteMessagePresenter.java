package interface_adapter.messaging.delete_m;

import use_case.messaging.delete_m.DeleteMessageOutputBoundary;
import use_case.messaging.delete_m.DeleteMessageOutputData;

/**
 * Presenter for the Delete Message use case.
 * Prepares the success or failure view model state based on the output data.
 */
// File: interface_adapter/messaging/delete_m/DeleteMessagePresenter.java

// ... (imports)

public class DeleteMessagePresenter implements DeleteMessageOutputBoundary {

    private final DeleteMessageViewModel deleteMessageViewModel;

    public DeleteMessagePresenter(DeleteMessageViewModel deleteMessageViewModel) {
        this.deleteMessageViewModel = deleteMessageViewModel;
    }

    @Override
    public void prepareSuccessView(DeleteMessageOutputData output) {
        DeleteMessageState state = deleteMessageViewModel.getState();
        state.setDeletedMessageId(output.getDeletedMessageId());
        state.setSuccess(true);
        state.setError(null);
        deleteMessageViewModel.setState(state);
        deleteMessageViewModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(DeleteMessageOutputData output) {
        DeleteMessageState state = deleteMessageViewModel.getState();
        state.setDeletedMessageId(output.getMessageId());
        state.setSuccess(false);
        state.setError(output.getFailReason());
        deleteMessageViewModel.setState(state);
        deleteMessageViewModel.firePropertyChange();
    }
}