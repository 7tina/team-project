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
        String currentUserID = inputData.getUser();
        String query = inputData.getQuery();
        String currentUserId = inputData.getUser();

        if (currentUserID != null) {
            Optional<User> currentUserOpt = userRepository.findByUsername(currentUserID);
            if (currentUserOpt.isEmpty()) {
                userPresenter.prepareFailView("Session error. Please log in again.");
                return;
            }
            currentUserId = currentUserOpt.get().getName();
        }

        if (query == null) {
            query = ""; // Treat null query as empty search
        }

        List<String> results = userDataAccessObject.searchUsers(currentUserId, query);

        if (results.isEmpty()) {
            userPresenter.prepareFailView("No users found matching: " + query);
        } else {
            SearchUserOutputData outputData = new SearchUserOutputData(results);
            userPresenter.prepareSuccessView(outputData);
        }
    }
}
