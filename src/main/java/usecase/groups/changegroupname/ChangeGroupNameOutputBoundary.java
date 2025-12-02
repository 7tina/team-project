package usecase.groups.changegroupname;

/**
 * Output boundary interface for the Change Group Name use case.
 * Defines the contract for presenting the results of the change group name operation.
 * This interface is implemented by the presenter layer to handle success and failure scenarios.
 */
public interface ChangeGroupNameOutputBoundary {

    /**
     * Prepares the success view with the output data.
     *
     * @param outputData the output data containing information about the successfully changed group name
     */
    void prepareSuccessView(ChangeGroupNameOutputData outputData);

    /**
     * Prepares the failure view with the output data.
     *
     * @param outputData the output data containing error information about why the operation failed
     */
    void prepareFailView(ChangeGroupNameOutputData outputData);
}
