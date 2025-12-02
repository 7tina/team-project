package use_case.change_password;

import entity.User;
import entity.UserFactory;
import org.junit.jupiter.api.Test;
import usecase.change_password.*;

import static org.junit.jupiter.api.Assertions.*;

class ChangePasswordInteractorTest {

    private static class TestChangePasswordDataAccessObject implements ChangePasswordUserDataAccessInterface {
        private String storedPassword;
        private final String storedUsername;

        public TestChangePasswordDataAccessObject(String username, String initialPassword) {
            this.storedUsername = username;
            this.storedPassword = initialPassword;
        }

        @Override
        public void changePassword(User user) {
            // Simulate updating the password in the "database"
            if (user.getName().equals(storedUsername)) {
                this.storedPassword = user.getPassword();
            }
        }

        public String getStoredPassword() {
            return storedPassword;
        }

        @Override
        public void setCurrentUsername(String username) {
            // Implementation not strictly needed for this specific interactor test,
            // as ChangePasswordInteractor doesn't call it, but must be implemented.
        }

        @Override
        public boolean existsByName(String identifier) {
            return false;
        }

        @Override
        public void save(User user) {
        }

        @Override
        public User get(String username) {
            return null;
        }

        @Override
        public String getCurrentUsername() {
            return null;
        }
    }

    @Test
    void successTest() {
        String testUsername = "TestUser";
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";

        // Setup Input Data and Dependencies
        ChangePasswordInputData inputData = new ChangePasswordInputData(newPassword, testUsername);

        // Mock the DAO to allow us to check the password change
        TestChangePasswordDataAccessObject userRepository = new TestChangePasswordDataAccessObject(testUsername, oldPassword);
        UserFactory userFactory = new UserFactory();

        // Mock the Presenter to verify success
        ChangePasswordOutputBoundary successPresenter = new ChangePasswordOutputBoundary() {
            @Override
            public void prepareSuccessView(ChangePasswordOutputData outputData) {
                // Check that the presenter was called with the correct username
                assertEquals(testUsername, outputData.getUsername());

                // Check that the DAO was updated with the new password
                assertEquals(newPassword, userRepository.getStoredPassword());
            }

            @Override
            public void prepareFailView(String errorMessage) {
                fail("Use case failure is unexpected: " + errorMessage);
            }
        };

        // Run the Interactor
        ChangePasswordInputBoundary interactor = new ChangePasswordInteractor(
                userRepository, successPresenter, userFactory
        );
        interactor.execute(inputData);
    }

    @Test
    void failureEmptyPasswordTest() {
        String testUsername = "TestUser";
        String oldPassword = "oldPassword";
        String emptyPassword = "";

        // Setup Input Data and Dependencies
        ChangePasswordInputData inputData = new ChangePasswordInputData(emptyPassword, testUsername);

        // Mock the DAO, though it shouldn't be called in the failure case
        TestChangePasswordDataAccessObject userRepository = new TestChangePasswordDataAccessObject(testUsername, oldPassword);
        UserFactory userFactory = new UserFactory();

        // Mock the Presenter to verify failure
        ChangePasswordOutputBoundary failurePresenter = new ChangePasswordOutputBoundary() {
            @Override
            public void prepareSuccessView(ChangePasswordOutputData outputData) {
                fail("Use case success is unexpected.");
            }

            @Override
            public void prepareFailView(String errorMessage) {
                // Check that the presenter was called with the correct error message
                assertEquals("New password cannot be empty", errorMessage);

                // Check that the password in the DAO was NOT changed
                assertEquals(oldPassword, userRepository.getStoredPassword());
            }
        };

        // Run the Interactor
        ChangePasswordInputBoundary interactor = new ChangePasswordInteractor(
                userRepository, failurePresenter, userFactory
        );
        interactor.execute(inputData);
    }
}