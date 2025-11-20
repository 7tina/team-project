package interface_adapter.user_search;

import use_case.search_user.SearchUserInputBoundary;
import use_case.search_user.SearchUserInputData;

public class SearchUserController {
    private final SearchUserInputBoundary interactor;
    private String currentUsername; // Store current username

    public SearchUserController(SearchUserInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void setCurrentUsername(String currentUsername) {
        this.currentUsername = currentUsername;
    }

    public void execute(String query) {
        SearchUserInputData inputData = new SearchUserInputData(query, currentUsername);
        interactor.execute(inputData);
    }
}