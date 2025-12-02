package usecase.groups.adduser;

/**
 * Output boundary interface for the Add User use case.
 * Defines the contract for presenting the results of the add user operation.
 * This interface is implemented by the presenter layer to handle success and failure scenarios.
 */
public interface AddUserOutputBoundary {

    /**
     * Prepares the success view with the output data.
     *
     * @param outputData the output data containing information about the successfully added user
     */
    void prepareSuccessView(AddUserOutputData outputData);

    /**
     * Prepares the failure view with an error message.
     *
     * @param error the error message describing why the operation failed
     */
    void prepareFailView(String error);
}
