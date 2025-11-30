package use_case.search_user;

import entity.User;
import entity.ports.UserRepository;
import use_case.create_chat.CreateChatOutputData;

import java.util.List;
import java.util.Optional;

public class SearchUserInteractor implements SearchUserInputBoundary {
    private final SearchUserDataAccessInterface userDataAccessObject;
    private final SearchUserOutputBoundary userPresenter;
    private final UserRepository userRepository;

    public SearchUserInteractor(SearchUserDataAccessInterface userDataAccessObject,
                                SearchUserOutputBoundary userPresenter,
                                UserRepository userRepository) {
        this.userDataAccessObject = userDataAccessObject;
        this.userPresenter = userPresenter;
        this.userRepository = userRepository;
    }

    @Override
    public void execute(SearchUserInputData inputData) {
        String currentUsername = inputData.getUser();
        String query = inputData.getQuery();

        // Validate current user
        if (currentUsername == null || currentUsername.trim().isEmpty()) {
            userPresenter.prepareFailView("Session error. Please log in again.");
            return;
        }

        // Treat null query as empty search
        if (query == null) {
            query = "";
        }

        // Search for users, passing the current username to filter them out
        // No need to look up the user - we already have their username
        List<String> results = userDataAccessObject.searchUsers(currentUsername, query);

        if (results.isEmpty()) {
            userPresenter.prepareFailView("No users found matching: " + query);
        } else {
            SearchUserOutputData outputData = new SearchUserOutputData(results);
            userPresenter.prepareSuccessView(outputData);
        }
    }
}