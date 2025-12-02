package usecase.accesschat;

public interface AccessChatOutputBoundary {
    /**
     * Prepares the success view for the Access Chat Use Case.
     * @param outputData the output data
     */
    void prepareSuccessView(AccessChatOutputData outputData);

    /**
     * Prepares the failure view for the Access Chat Use Case.
     * @param errorMessage the output data with explanation of the failure
     */
    void prepareFailView(String errorMessage);
}
