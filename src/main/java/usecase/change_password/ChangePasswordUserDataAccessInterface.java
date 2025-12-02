package usecase.change_password;

import entity.User;
import usecase.login.LoginUserDataAccessInterface;
import usecase.signup.SignupUserDataAccessInterface;

/**
 * The DAO interface for the Change Password Use Case.
 */
public interface ChangePasswordUserDataAccessInterface extends
        LoginUserDataAccessInterface, SignupUserDataAccessInterface {

    /**
     * Updates the system to record this user's password.
     * @param user the user whose password is to be updated
     */
    void changePassword(User user);
}
