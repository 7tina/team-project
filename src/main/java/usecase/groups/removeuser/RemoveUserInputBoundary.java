package usecase.groups.removeuser;

/**
 * Input boundary interface for the Remove User use case.
 * Defines the contract for executing the remove user operation.
 */
public interface RemoveUserInputBoundary {

    /**
     * Executes the remove user use case with the provided input data.
     *
     * @param inputData the input data containing the chat ID and username to remove
     */
    void execute(RemoveUserInputData inputData);
}
