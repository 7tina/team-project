package usecase.accesschat;

public interface AccessChatInputBoundary {
    /**
     * Will execute the access chat use case.
     * @param inputData The required input data to execute the use case.
     */
    void execute(AccessChatInputData inputData);
}
