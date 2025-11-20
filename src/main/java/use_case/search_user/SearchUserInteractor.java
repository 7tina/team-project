package use_case.search_user;

import java.util.List;
import java.util.stream.Collectors;

public class SearchUserInteractor implements SearchUserInputBoundary {
    private final SearchUserDataAccessInterface userDataAccessObject;
    private final SearchUserOutputBoundary userPresenter;

    public SearchUserInteractor(SearchUserDataAccessInterface userDataAccessObject,
                                SearchUserOutputBoundary userPresenter) {
        this.userDataAccessObject = userDataAccessObject;
        this.userPresenter = userPresenter;
    }

    @Override
    public void execute(SearchUserInputData inputData) {
        String query = inputData.getQuery();
        String currentUsername = inputData.getCurrentUsername(); // Need to add this to inputData

        if (query == null) {
            query = ""; // Treat null query as empty search
        }

        // Get all search results
        List<String> results = userDataAccessObject.searchUsers(query);

        // Filter out current user from results
        if (currentUsername != null && !currentUsername.isEmpty()) {
            results = results.stream()
                    .filter(username -> !username.equals(currentUsername))
                    .collect(Collectors.toList());
        }

        if (results.isEmpty()) {
            userPresenter.prepareFailView("No users found matching: " + query);
        } else {
            SearchUserOutputData outputData = new SearchUserOutputData(results);
            userPresenter.prepareSuccessView(outputData);
        }
    }
}