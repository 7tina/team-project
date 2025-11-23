package use_case.create_chat;

import use_case.change_password.ChangePasswordInputData;

public interface CreateChatInputBoundary {
    /**
     * Execute the Create Chat Use Case.
     * @param createChatInputData the input data for this use case
     */
    void execute(CreateChatInputData createChatInputData);
}
