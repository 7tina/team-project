package use_case.messaging.delete_m;

public interface DeleteMessageOutputBoundary {
    void prepareSuccessView(DeleteMessageOutputData outputData);

    void prepareFailView(String error);
}