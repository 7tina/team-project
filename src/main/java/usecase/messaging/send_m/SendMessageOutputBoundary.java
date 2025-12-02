package usecase.messaging.send_m;

public interface SendMessageOutputBoundary {
    void prepareSuccessView(SendMessageOutputData outputData);
    void prepareFailView(String errorMessage);
}
