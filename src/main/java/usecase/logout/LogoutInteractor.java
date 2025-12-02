package usecase.logout;

import entity.ports.ChatRepository;

/**
 * The Logout Interactor.
 */
public class LogoutInteractor implements LogoutInputBoundary {
    private LogoutUserDataAccessInterface userDataAccessObject;
    private LogoutOutputBoundary logoutPresenter;
    private ChatRepository chatRepository;

    public LogoutInteractor(LogoutUserDataAccessInterface userDataAccessInterface,
                            LogoutOutputBoundary logoutOutputBoundary,
                            ChatRepository chatRepository) {
        this.userDataAccessObject = userDataAccessInterface;
        this.logoutPresenter = logoutOutputBoundary;
        this.chatRepository = chatRepository;
    }

    @Override
    public void execute() {
        // * set the current username to null in the DAO
        String username = userDataAccessObject.getCurrentUsername();
        userDataAccessObject.setCurrentUsername(null);
        chatRepository.clear();
        // * instantiate the `LogoutOutputData`, which needs to contain the username.
        final LogoutOutputData logoutOutputData = new LogoutOutputData(username);
        // * tell the presenter to prepare a success view.
        logoutPresenter.prepareSuccessView(logoutOutputData);
    }
}

