package interfaceadapter.logout;

import interfaceadapter.ViewManagerModel;
import interfaceadapter.logged_in.LoggedInState;
import interfaceadapter.logged_in.LoggedInViewModel;
import interfaceadapter.login.LoginState;
import interfaceadapter.login.LoginViewModel;
import usecase.logout.LogoutOutputBoundary;
import usecase.logout.LogoutOutputData;

/**
 * The Presenter for the Logout Use Case.
 */
public class LogoutPresenter implements LogoutOutputBoundary {

    private LoggedInViewModel loggedInViewModel;
    private ViewManagerModel viewManagerModel;
    private LoginViewModel loginViewModel;

    public LogoutPresenter(ViewManagerModel viewManagerModel,
                          LoggedInViewModel loggedInViewModel,
                           LoginViewModel loginViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.loggedInViewModel = loggedInViewModel;
        this.loginViewModel = loginViewModel;
    }

    @Override
    public void prepareSuccessView(LogoutOutputData response) {
        // System.out.println("Logged Out: " + response.getUsername());
        // We need to switch to the login view, which should have
        // an empty username and password.

        // We also need to set the username in the LoggedInState to
        // the empty string.
        final LoggedInState loggedInState = loggedInViewModel.getState();
        loggedInState.setUsername("");
        loggedInViewModel.setState(loggedInState);
        loggedInViewModel.firePropertyChange();

        final LoginState loginState = loginViewModel.getState();
        loginState.setUsername(null);
        // Clear password
        loginState.setPassword(null);
        // Clear any old error message
        loginState.setLoginError(null);

        loginViewModel.setState(loginState);
        loginViewModel.firePropertyChange();

        // This code tells the View Manager to switch to the LoginView.
        this.viewManagerModel.setState(loginViewModel.getViewName());
        this.viewManagerModel.firePropertyChange();
    }
}
