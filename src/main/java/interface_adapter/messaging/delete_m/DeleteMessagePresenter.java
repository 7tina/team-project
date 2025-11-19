package interface_adapter.messaging.delete_m;

import use_case.messaging.delete_m.DeleteMessageOutputBoundary;
import use_case.messaging.delete_m.DeleteMessageOutputData;

public class DeleteMessagePresenter implements DeleteMessageOutputBoundary {

    private final DeleteMessageViewModel viewModel;

    public DeleteMessagePresenter(DeleteMessageViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccessView(DeleteMessageOutputData outputData) {
        viewModel.setDeletedMessageId(outputData.getDeletedMessageId());
        viewModel.firePropertyChanged();
    }

    @Override
    public void prepareFailView(String errorMessage) {
        viewModel.setError(errorMessage);
        viewModel.firePropertyChanged();
    }
}
