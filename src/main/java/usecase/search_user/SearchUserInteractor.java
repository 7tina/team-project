package usecase.search_user;

import entity.ports.UserRepository;
import java.util.List;

public class SearchUserInteractor implements SearchUserInputBoundary {
    /**
     * Interface for querying user data needed for search operations.
     */
    private final SearchUserDataAccessInterface userDataAccessObject;
    /**
     * Presenter used to format and deliver the search results
     * or errors to the UI.
     */
    private final SearchUserOutputBoundary userPresenter;
    /**
     * Repository providing access to the currently
     * logged-in user's information.
     */
    private final UserRepository userRepository;

    /**
     * Constructs a new SearchUserInteractor with the specified dependencies.
     *
     * @param newUserDataAccessObject the data access object for searching users
     * @param newUserPresenter the output boundary for presenting search results
     * @param newUserRepository the repository for user data operations
     */
    public SearchUserInteractor(final SearchUserDataAccessInterface
                                        newUserDataAccessObject,
                                final SearchUserOutputBoundary newUserPresenter,
                                final UserRepository newUserRepository) {
        this.userDataAccessObject = newUserDataAccessObject;
        this.userPresenter = newUserPresenter;
        this.userRepository = newUserRepository;
    }

    /**
     * Executes the Search User use case. This method validates
     * the current session and processes the user's search query.
     * If the current username is missing or invalid, the
     * presenter is instructed to show a failure view. Otherwise,
     * the search query is forwarded for further processing.
     * @param inputData the input data containing the current user's username
     *                  and the search query they entered
     */
    @Override
    public void execute(final SearchUserInputData inputData) {
        String currentUsername = inputData.getUser();
        String query = inputData.getQuery();

        // Validate current user
        if (currentUsername == null || currentUsername.trim().isEmpty()) {
            userPresenter.prepareFailView("Session error. "
                    + "Please log in again.");
            return;
        }

        // Treat null query as empty search
        if (query == null) {
            query = "";
        }

        // Search for users, passing the current username to filter them out
        // No need to look up the user - we already have their username
        List<String> results =
                userDataAccessObject.searchUsers(currentUsername, query);

        if (results.isEmpty()) {
            userPresenter.prepareFailView("No users found matching: " + query);
        } else {
            SearchUserOutputData outputData = new SearchUserOutputData(results);
            userPresenter.prepareSuccessView(outputData);
        }
    }
}
