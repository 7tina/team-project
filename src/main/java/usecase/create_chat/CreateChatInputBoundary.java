package usecase.create_chat;

public interface CreateChatInputBoundary {
    /**
     * Execute the Create Chat Use Case.
     * @param createChatInputData the input data for this use case
     */
    void execute(CreateChatInputData createChatInputData);
}
