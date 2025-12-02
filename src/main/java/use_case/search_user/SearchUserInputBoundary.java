package use_case.search_user;

public interface SearchUserInputBoundary {
    /**
     * Executes the Search User use case with the given input data.
     *
     * @param inputData the user's search request information
     */
    void execute(SearchUserInputData inputData);
}
