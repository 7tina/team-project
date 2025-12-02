package usecase.groups.removeuser;

/**
 * Output boundary interface for the Remove User use case.
 * Defines the contract for presenting the results of the remove user operation.
 * This interface is implemented by the presenter layer to handle success and failure scenarios.
 */
public interface RemoveUserOutputBoundary {

    /**
     * Prepares the success view with the output data.
     *
     * @param outputData the output data containing information about the successfully removed user
     */
    void prepareSuccessView(RemoveUserOutputData outputData);

    /**
     * Prepares the failure view with an error message.
     *
     * @param error the error message describing why the operation failed
     */
    void prepareFailView(String error);
}
