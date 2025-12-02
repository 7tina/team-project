package interface_adapter.search_user;

import use_case.search_user.SearchUserInputBoundary;
import use_case.search_user.SearchUserInputData;

public class SearchUserController {
    private final SearchUserInputBoundary searchUserInteractor;

    public SearchUserController(SearchUserInputBoundary searchUserInteractor) {
        // Initialize the field with the passed-in argument
        this.searchUserInteractor = searchUserInteractor;
    }

    /**
     * Executes the Search User use case by creating the required
     * {@link SearchUserInputData} object and delegating the request
     * to the interactor.
     * @param user  the username of the currently logged-in user
     * @param query the search query entered by the user
     */
    public void execute(String user, String query) {
        final SearchUserInputData inputData = new SearchUserInputData(user, query);
        searchUserInteractor.execute(inputData);
    }
}
