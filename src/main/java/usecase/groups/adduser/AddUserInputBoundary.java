package usecase.groups.adduser;

/**
 * Input boundary interface for the Add User use case.
 * Defines the contract for executing the add user operation.
 */
public interface AddUserInputBoundary {

    /**
     * Executes the add user use case with the provided input data.
     *
     * @param inputData the input data containing the chat ID and username to add
     */
    void execute(AddUserInputData inputData);
}
