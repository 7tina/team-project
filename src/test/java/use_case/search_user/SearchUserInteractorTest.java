package use_case.search_user;

import entity.User;
import entity.repo.InMemoryUserRepository;
import entity.ports.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SearchUserInteractorTest {

    /**
     * Mock implementation of SearchUserDataAccessInterface to control search results.
     */
    private static class MockSearchUserDataAccess implements SearchUserDataAccessInterface {
        private final List<String> mockResults;
        private final boolean shouldReturnResults;
        private final String expectedUserId; // To check what User ID is passed

        // Constructor for success case
        MockSearchUserDataAccess(List<String> mockResults, String expectedUserId) {
            this.mockResults = mockResults;
            this.shouldReturnResults = true;
            this.expectedUserId = expectedUserId;
        }

        // Constructor for failure (no results found) case
        MockSearchUserDataAccess(String expectedUserId) {
            this.mockResults = Arrays.asList();
            this.shouldReturnResults = false;
            this.expectedUserId = expectedUserId;
        }

        @Override
        public List<String> searchUsers(String currentUserId, String query) {
            // Assert the userId passed to the DAO is the expected one
            assertEquals(expectedUserId, currentUserId);

            // Simulate the DAO logic
            if (shouldReturnResults) {
                return mockResults;
            } else {
                return Arrays.asList();
            }
        }
    }

    /**
     * Setup a UserRepository with a valid test user for authenticated sessions.
     */
    private UserRepository setupUserRepository(String testUsername) {
        UserRepository userRepository = new InMemoryUserRepository();
        User testUser = new User(testUsername, "testPassword");
        userRepository.save(testUser);

        // Add other users to the repository
        userRepository.save(new User("Alice", "pass"));
        userRepository.save(new User("Bob", "pass"));
        userRepository.save(new User("Charlie", "pass"));

        return userRepository;
    }

    // Success Tests

    @Test
    void successTest_queryMatchesUsers() {
        String validUserId = "testUser";
        List<String> expectedResults = Arrays.asList("Alice", "Charlie");
        SearchUserInputData inputData = new SearchUserInputData(validUserId, "ice");
        SearchUserDataAccessInterface userDataAccessObject = new MockSearchUserDataAccess(expectedResults, validUserId);
        UserRepository userRepository = setupUserRepository(validUserId);

        // This presenter asserts a successful result
        SearchUserOutputBoundary successPresenter = new SearchUserOutputBoundary() {
            @Override
            public void prepareSuccessView(SearchUserOutputData outputData) {
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
        String validUserId = "testUser";
        List<String> expectedResults = Arrays.asList("All", "Users"); // DAO should return all users
        SearchUserInputData inputData = new SearchUserInputData(validUserId, "");
        SearchUserDataAccessInterface userDataAccessObject = new MockSearchUserDataAccess(expectedResults, validUserId);
        UserRepository userRepository = setupUserRepository(validUserId);

        // This presenter asserts a successful result
        SearchUserOutputBoundary successPresenter = new SearchUserOutputBoundary() {
            @Override
            public void prepareSuccessView(SearchUserOutputData outputData) {
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

    @Test
    void successTest_nullQuery() {
        String validUserId = "testUser";
        List<String> expectedResults = Arrays.asList("All", "Users"); // DAO should return all users
        SearchUserInputData inputData = new SearchUserInputData(validUserId, null); // Pass null query

        // DAO Mock will receive an empty string for the query because Interactor handles null
        SearchUserDataAccessInterface userDataAccessObject = new MockSearchUserDataAccess(expectedResults, validUserId);
        UserRepository userRepository = setupUserRepository(validUserId);

        // This presenter asserts a successful result
        SearchUserOutputBoundary successPresenter = new SearchUserOutputBoundary() {
            @Override
            public void prepareSuccessView(SearchUserOutputData outputData) {
                assertEquals(expectedResults, outputData.getUsernames());
            }

            @Override
            public void prepareFailView(String error) {
                fail("Use case failure is unexpected for a null query: " + error);
            }
        };

        SearchUserInputBoundary interactor = new SearchUserInteractor(userDataAccessObject, successPresenter, userRepository);
        interactor.execute(inputData);
    }

    @Test
    void successTest_nullUserUnauthenticatedSearch() {
        // This test covers the case where the currentUserID is null (unauthenticated user).
        List<String> expectedResults = Arrays.asList("Public", "Users");
        SearchUserInputData inputData = new SearchUserInputData(null, "query"); // Pass null user ID

        // DAO Mock will receive a null user ID, and returns results
        SearchUserDataAccessInterface userDataAccessObject = new MockSearchUserDataAccess(expectedResults, null);
        UserRepository userRepository = new InMemoryUserRepository(); // Repository can be empty since we skip validation

        // This presenter asserts a successful result
        SearchUserOutputBoundary successPresenter = new SearchUserOutputBoundary() {
            @Override
            public void prepareSuccessView(SearchUserOutputData outputData) {
                assertEquals(expectedResults, outputData.getUsernames());
            }

            @Override
            public void prepareFailView(String error) {
                fail("Use case failure is unexpected for a null user ID: " + error);
            }
        };

        SearchUserInputBoundary interactor = new SearchUserInteractor(userDataAccessObject, successPresenter, userRepository);
        interactor.execute(inputData);
    }

    // Failure Tests

    @Test
    void failureTest_noUsersFound() {
        String validUserId = "testUser";
        String query = "nonexistentuser";
        SearchUserInputData inputData = new SearchUserInputData(validUserId, query);

        // This DAO mock returns an empty list
        SearchUserDataAccessInterface userDataAccessObject = new MockSearchUserDataAccess(validUserId);
        UserRepository userRepository = setupUserRepository(validUserId);

        // This presenter asserts the correct failure message
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
        // The user ID is non-null, but not found in the repository
        String invalidUserId = "nonexistentUser";
        SearchUserInputData inputData = new SearchUserInputData(invalidUserId, "query");

        // DAO Mock is never called, so the expected user ID doesn't matter here.
        SearchUserDataAccessInterface userDataAccessObject = new MockSearchUserDataAccess(null);
        UserRepository userRepository = new InMemoryUserRepository(); // Repository is empty or doesn't contain the user

        // This presenter asserts the session error
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