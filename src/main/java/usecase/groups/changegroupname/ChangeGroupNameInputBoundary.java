package usecase.groups.changegroupname;

/**
 * Input boundary interface for the Change Group Name use case.
 * Defines the contract for executing the change group name operation.
 */
public interface ChangeGroupNameInputBoundary {

    /**
     * Executes the change group name use case with the provided input data.
     *
     * @param inputData the input data containing the chat ID and new group name
     */
    void execute(ChangeGroupNameInputData inputData);
}
