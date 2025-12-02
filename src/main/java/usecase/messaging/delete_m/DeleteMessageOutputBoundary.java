package usecase.messaging.delete_m;

public interface DeleteMessageOutputBoundary {
    void prepareSuccessView(DeleteMessageOutputData outputData);

    void prepareFailView(DeleteMessageOutputData output);
}