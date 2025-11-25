package use_case.search_user;

import entity.User;
import entity.repo.InMemoryUserRepository;
import entity.ports.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SearchUserInteractorTest {

    private static class MockSearchUserDataAccess implements SearchUserDataAccessInterface {
        private final List<String> mockResults;
        private final boolean shouldReturnResults;

        // Constructor for success case
        MockSearchUserDataAccess(List<String> mockResults) {
            this.mockResults = mockResults;
            this.shouldReturnResults = true;
        }

        // Constructor for failure (no results found) case
        MockSearchUserDataAccess() {
            this.mockResults = Arrays.asList();
            this.shouldReturnResults = false;
        }

        @Override
        public List<String> searchUsers(String currentUserId, String query) {
            // Check if the current user exists, though this logic is primarily
            // handled by the UserRepository in the interactor.
            if (!currentUserId.equals("testUser")) {
                // The interactor should catch the session error before calling this,
                // but we simulate a potential call just in case.
                return Arrays.asList();
            }

            // Simulate the DAO logic
            if (shouldReturnResults) {
                return mockResults;
            } else {
                return Arrays.asList();
            }
        }
    }

    private UserRepository setupUserRepository() {
        // Create and save a user for a valid session (testUser)
        UserRepository userRepository = new InMemoryUserRepository();
        User testUser = new User("testUser", "testPassword");
        userRepository.save(testUser);

        // Add other users to the repository, although the Interactor relies on
        // the SearchUserDataAccessInterface for the actual search results.
        User user1 = new User("Alice", "pass");
        User user2 = new User("Bob", "pass");
        User user3 = new User("Charlie", "pass");
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        return userRepository;
    }

    // Success Tests

    @Test
    void successTest_queryMatchesUsers() {
        List<String> expectedResults = Arrays.asList("Alice", "Charlie");
        SearchUserInputData inputData = new SearchUserInputData("testUser", "ice");
        SearchUserDataAccessInterface userDataAccessObject = new MockSearchUserDataAccess(expectedResults);
        UserRepository userRepository = setupUserRepository();

        // Create a presenter that tests whether the output data is as expected.
        SearchUserOutputBoundary successPresenter = new SearchUserOutputBoundary() {
            @Override
            public void prepareSuccessView(SearchUserOutputData outputData) {
                // Check if the usernames list matches the expected results
                assertEquals(expectedResults, outputData.getUsernames());
            }

            @Override
            public void prepareFailView(String error) {
                fail("Use case failure is unexpected: " + error);
            }
        };

        SearchUserInputBoundary interactor = new SearchUserInteractor(userDataAccessObject, successPresenter, userRepository);
        interactor.execute(inputData);
    }

    @Test
    void successTest_emptyQuery() {
        // Interactor treats null or empty query as an empty search, which should return all users
        List<String> expectedResults = Arrays.asList("Alice", "Bob", "Charlie", "Doris");
        SearchUserInputData inputData = new SearchUserInputData("testUser", "");
        SearchUserDataAccessInterface userDataAccessObject = new MockSearchUserDataAccess(expectedResults);
        UserRepository userRepository = setupUserRepository(); // User repo only for current user validation

        // Create a presenter that tests whether the output data is as expected.
        SearchUserOutputBoundary successPresenter = new SearchUserOutputBoundary() {
            @Override
            public void prepareSuccessView(SearchUserOutputData outputData) {
                // Check if the usernames list matches the expected results (e.g., all users)
                assertEquals(expectedResults, outputData.getUsernames());
            }

            @Override
            public void prepareFailView(String error) {
                fail("Use case failure is unexpected for an empty query: " + error);
            }
        };

        SearchUserInputBoundary interactor = new SearchUserInteractor(userDataAccessObject, successPresenter, userRepository);
        interactor.execute(inputData);
    }

    // Failure Tests

    @Test
    void failureTest_noUsersFound() {
        String query = "nonexistentuser";
        SearchUserInputData inputData = new SearchUserInputData("testUser", query);
        // This DAO mock returns an empty list
        SearchUserDataAccessInterface userDataAccessObject = new MockSearchUserDataAccess();
        UserRepository userRepository = setupUserRepository();

        // Create a presenter that tests whether the error message is as expected.
        SearchUserOutputBoundary failurePresenter = new SearchUserOutputBoundary() {
            @Override
            public void prepareSuccessView(SearchUserOutputData outputData) {
                fail("Use case success is unexpected.");
            }

            @Override
            public void prepareFailView(String error) {
                assertEquals("No users found matching: " + query, error);
            }
        };

        SearchUserInputBoundary interactor = new SearchUserInteractor(userDataAccessObject, failurePresenter, userRepository);
        interactor.execute(inputData);
    }

    @Test
    void failureTest_currentUserDoesNotExist() {
        SearchUserInputData inputData = new SearchUserInputData("nonexistentUser", "query");
        SearchUserDataAccessInterface userDataAccessObject = new MockSearchUserDataAccess(); // Mock doesn't matter much here
        UserRepository userRepository = new InMemoryUserRepository(); // Repository is empty or doesn't contain 'nonexistentUser'

        // Create a presenter that tests for the session error.
        SearchUserOutputBoundary failurePresenter = new SearchUserOutputBoundary() {
            @Override
            public void prepareSuccessView(SearchUserOutputData outputData) {
                fail("Use case success is unexpected.");
            }

            @Override
            public void prepareFailView(String error) {
                assertEquals("Session error. Please log in again.", error);
            }
        };

        SearchUserInputBoundary interactor = new SearchUserInteractor(userDataAccessObject, failurePresenter, userRepository);
        interactor.execute(inputData);
    }
}