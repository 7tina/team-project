package usecase.search_user;

public interface SearchUserOutputBoundary {
    /**
     * Prepares the view for a successful search operation.
     *
     * @param outputData the data returned from the search use case
     */
    void prepareSuccessView(SearchUserOutputData outputData);

    /**
     * Prepares the view for a failed search operation.
     *
     * @param error the error message describing why the search failed
     */
    void prepareFailView(String error);
}
