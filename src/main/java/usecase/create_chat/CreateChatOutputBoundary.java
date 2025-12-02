package usecase.create_chat;

public interface CreateChatOutputBoundary {
    /**
     * Prepares the success view for the Create Chat Use Case.
     * @param outputData the output data
     */
    void prepareSuccessView(CreateChatOutputData outputData);

    /**
     * Prepares the failure view for the Create Chat Use Case.
     * @param outputData the output data with explanation of the failure
     */
    void prepareFailView(CreateChatOutputData outputData);
}
