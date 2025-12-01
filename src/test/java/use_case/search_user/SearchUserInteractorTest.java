package use_case.search_user;

import entity.User;
import entity.UserFactory;
import entity.ports.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SearchUserInteractorTest {

    @Test
    void successTest() {
        // Setup test data
        SearchUserInputData inputData = new SearchUserInputData("Alice", "Bob");

        // Mock UserRepository
        UserRepository mockUserRepository = new UserRepository() {
            @Override
            public Optional<User> findByUsername(String username) {
                if ("Alice".equals(username)) {
                    return Optional.of(new User("Alice", "password"));
                }
                return Optional.empty();
            }

            @Override
            public User save(User user) {
                return user;
            }
        };

        // Mock DataAccessInterface
        SearchUserDataAccessInterface mockDataAccess = new SearchUserDataAccessInterface() {
            @Override
            public List<String> searchUsers(String userId, String query) {
                // Simulate finding users matching "Bob"
                List<String> results = new ArrayList<>();
                results.add("Bobby");
                results.add("BobTheBuilder");
                return results;
            }
        };

        // Success presenter
        SearchUserOutputBoundary successPresenter = new SearchUserOutputBoundary() {
            @Override
            public void prepareSuccessView(SearchUserOutputData outputData) {
                // Verify the output contains the expected usernames
                assertEquals(2, outputData.getUsernames().size());
                assertTrue(outputData.getUsernames().contains("Bobby"));
                assertTrue(outputData.getUsernames().contains("BobTheBuilder"));
            }

            @Override
            public void prepareFailView(String error) {
                fail("Use case failure is unexpected.");
            }
        };

        SearchUserInputBoundary interactor = new SearchUserInteractor(
                mockDataAccess, successPresenter, mockUserRepository);
        interactor.execute(inputData);
    }

    @Test
    void successEmptyQueryTest() {
        // Test with empty query (should return all users except current user)
        SearchUserInputData inputData = new SearchUserInputData("Alice", "");

        UserRepository mockUserRepository = new UserRepository() {
            @Override
            public Optional<User> findByUsername(String username) {
                if ("Alice".equals(username)) {
                    return Optional.of(new User("Alice", "password"));
                }
                return Optional.empty();
            }

            @Override
            public User save(User user) {
                return user;
            }
        };

        SearchUserDataAccessInterface mockDataAccess = new SearchUserDataAccessInterface() {
            @Override
            public List<String> searchUsers(String userId, String query) {
                // Return all users when query is empty
                List<String> results = new ArrayList<>();
                results.add("Bob");
                results.add("Charlie");
                results.add("Diana");
                return results;
            }
        };

        SearchUserOutputBoundary successPresenter = new SearchUserOutputBoundary() {
            @Override
            public void prepareSuccessView(SearchUserOutputData outputData) {
                assertEquals(3, outputData.getUsernames().size());
                assertTrue(outputData.getUsernames().contains("Bob"));
                assertTrue(outputData.getUsernames().contains("Charlie"));
                assertTrue(outputData.getUsernames().contains("Diana"));
            }

            @Override
            public void prepareFailView(String error) {
                fail("Use case failure is unexpected.");
            }
        };

        SearchUserInputBoundary interactor = new SearchUserInteractor(
                mockDataAccess, successPresenter, mockUserRepository);
        interactor.execute(inputData);
    }

    @Test
    void successNullQueryTest() {
        // Test with null query (should be treated as empty query)
        SearchUserInputData inputData = new SearchUserInputData("Alice", null);

        UserRepository mockUserRepository = new UserRepository() {
            @Override
            public Optional<User> findByUsername(String username) {
                if ("Alice".equals(username)) {
                    return Optional.of(new User("Alice", "password"));
                }
                return Optional.empty();
            }

            @Override
            public User save(User user) {
                return user;
            }
        };

        SearchUserDataAccessInterface mockDataAccess = new SearchUserDataAccessInterface() {
            @Override
            public List<String> searchUsers(String userId, String query) {
                // Verify that null query was converted to empty string
                assertEquals("", query);
                List<String> results = new ArrayList<>();
                results.add("Bob");
                return results;
            }
        };

        SearchUserOutputBoundary successPresenter = new SearchUserOutputBoundary() {
            @Override
            public void prepareSuccessView(SearchUserOutputData outputData) {
                assertEquals(1, outputData.getUsernames().size());
                assertTrue(outputData.getUsernames().contains("Bob"));
            }

            @Override
            public void prepareFailView(String error) {
                fail("Use case failure is unexpected.");
            }
        };

        SearchUserInputBoundary interactor = new SearchUserInteractor(
                mockDataAccess, successPresenter, mockUserRepository);
        interactor.execute(inputData);
    }

    @Test
    void failureNullUsernameTest() {
        // Test with null current username
        SearchUserInputData inputData = new SearchUserInputData(null, "Bob");

        UserRepository mockUserRepository = new UserRepository() {
            @Override
            public Optional<User> findByUsername(String username) {
                return Optional.empty();
            }

            @Override
            public User save(User user) {
                return user;
            }
        };

        SearchUserDataAccessInterface mockDataAccess = new SearchUserDataAccessInterface() {
            @Override
            public List<String> searchUsers(String userId, String query) {
                fail("Data access should not be called with null username");
                return new ArrayList<>();
            }
        };

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

        SearchUserInputBoundary interactor = new SearchUserInteractor(
                mockDataAccess, failurePresenter, mockUserRepository);
        interactor.execute(inputData);
    }

    @Test
    void failureEmptyUsernameTest() {
        // Test with empty current username
        SearchUserInputData inputData = new SearchUserInputData("   ", "Bob");

        UserRepository mockUserRepository = new UserRepository() {
            @Override
            public Optional<User> findByUsername(String username) {
                return Optional.empty();
            }

            @Override
            public User save(User user) {
                return user;
            }
        };

        SearchUserDataAccessInterface mockDataAccess = new SearchUserDataAccessInterface() {
            @Override
            public List<String> searchUsers(String userId, String query) {
                fail("Data access should not be called with empty username");
                return new ArrayList<>();
            }
        };

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

        SearchUserInputBoundary interactor = new SearchUserInteractor(
                mockDataAccess, failurePresenter, mockUserRepository);
        interactor.execute(inputData);
    }

    @Test
    void failureNoUsersFoundTest() {
        // Test when search returns no results
        SearchUserInputData inputData = new SearchUserInputData("Alice", "Zzzzzz");

        UserRepository mockUserRepository = new UserRepository() {
            @Override
            public Optional<User> findByUsername(String username) {
                if ("Alice".equals(username)) {
                    return Optional.of(new User("Alice", "password"));
                }
                return Optional.empty();
            }

            @Override
            public User save(User user) {
                return user;
            }
        };

        SearchUserDataAccessInterface mockDataAccess = new SearchUserDataAccessInterface() {
            @Override
            public List<String> searchUsers(String userId, String query) {
                // Return empty list - no users found
                return new ArrayList<>();
            }
        };

        SearchUserOutputBoundary failurePresenter = new SearchUserOutputBoundary() {
            @Override
            public void prepareSuccessView(SearchUserOutputData outputData) {
                fail("Use case success is unexpected.");
            }

            @Override
            public void prepareFailView(String error) {
                assertEquals("No users found matching: Zzzzzz", error);
            }
        };

        SearchUserInputBoundary interactor = new SearchUserInteractor(
                mockDataAccess, failurePresenter, mockUserRepository);
        interactor.execute(inputData);
    }

    @Test
    void successSingleUserFoundTest() {
        // Test when exactly one user is found
        SearchUserInputData inputData = new SearchUserInputData("Alice", "Bob");

        UserRepository mockUserRepository = new UserRepository() {
            @Override
            public Optional<User> findByUsername(String username) {
                if ("Alice".equals(username)) {
                    return Optional.of(new User("Alice", "password"));
                }
                return Optional.empty();
            }

            @Override
            public User save(User user) {
                return user;
            }
        };

        SearchUserDataAccessInterface mockDataAccess = new SearchUserDataAccessInterface() {
            @Override
            public List<String> searchUsers(String userId, String query) {
                List<String> results = new ArrayList<>();
                results.add("Bob");
                return results;
            }
        };

        SearchUserOutputBoundary successPresenter = new SearchUserOutputBoundary() {
            @Override
            public void prepareSuccessView(SearchUserOutputData outputData) {
                assertEquals(1, outputData.getUsernames().size());
                assertEquals("Bob", outputData.getUsernames().get(0));
            }

            @Override
            public void prepareFailView(String error) {
                fail("Use case failure is unexpected.");
            }
        };

        SearchUserInputBoundary interactor = new SearchUserInteractor(
                mockDataAccess, successPresenter, mockUserRepository);
        interactor.execute(inputData);
    }
}