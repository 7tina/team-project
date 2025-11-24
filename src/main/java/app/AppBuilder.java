package app;

import data_access.FireBaseUserDataAccessObject;
import entity.UserFactory;
import entity.User;
import entity.ports.ChatRepository;
import entity.ports.UserRepository;
import entity.repo.InMemoryChatRepository;
import entity.repo.InMemoryMessageRepository;
import entity.repo.InMemoryUserRepository;
import interface_adapter.ViewManagerModel;
import interface_adapter.create_chat.CreateChatController;
import interface_adapter.create_chat.CreateChatPresenter;
import interface_adapter.logged_in.ChangePasswordController;
import interface_adapter.logged_in.ChangePasswordPresenter;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.login.LoginController;
import interface_adapter.login.LoginPresenter;
import interface_adapter.login.LoginViewModel;
import interface_adapter.logout.LogoutController;
import interface_adapter.logout.LogoutPresenter;
import interface_adapter.messaging.delete_m.DeleteMessageController;
import interface_adapter.messaging.delete_m.DeleteMessagePresenter;
import interface_adapter.messaging.delete_m.DeleteMessageViewModel;
import interface_adapter.messaging.view_history.ViewChatHistoryController;
import interface_adapter.messaging.view_history.ViewChatHistoryPresenter;
import interface_adapter.signup.SignupController;
import interface_adapter.signup.SignupPresenter;
import interface_adapter.signup.SignupViewModel;
// Corrected SearchUser Imports (Assuming your packages are named 'search_user')
import interface_adapter.user_search.SearchUserController;
import interface_adapter.user_search.SearchUserPresenter;
import interface_adapter.user_search.SearchUserViewModel;
import interface_adapter.groupchat.ChangeGroupNameController;
import interface_adapter.groupchat.ChangeGroupNamePresenter;
import interface_adapter.groupchat.CreateGroupChatController;
import interface_adapter.groupchat.CreateGroupChatPresenter;
import interface_adapter.groupchat.GroupChatViewModel;
import interface_adapter.messaging.send_m.SendMessageController;
import interface_adapter.messaging.send_m.SendMessagePresenter;
import interface_adapter.messaging.send_m.ChatViewModel;
import use_case.create_chat.CreateChatInputBoundary;
import use_case.create_chat.CreateChatInteractor;
import use_case.create_chat.CreateChatOutputBoundary;
import use_case.messaging.delete_m.DeleteMessageInputBoundary;
import use_case.messaging.delete_m.DeleteMessageInteractor;
import use_case.messaging.delete_m.DeleteMessageOutputBoundary;
import use_case.messaging.send_m.SendMessageInputBoundary;
import use_case.messaging.send_m.SendMessageOutputBoundary;
import use_case.messaging.send_m.SendMessageInteractor;

import use_case.messaging.view_history.ViewChatHistoryInputBoundary;
import use_case.messaging.view_history.ViewChatHistoryInteractor;
import use_case.messaging.view_history.ViewChatHistoryOutputBoundary;
import use_case.search_user.SearchUserInputBoundary;
import use_case.search_user.SearchUserInteractor;
import use_case.search_user.SearchUserOutputBoundary;
import use_case.search_user.SearchUserDataAccessInterface;

import use_case.change_password.ChangePasswordInputBoundary;
import use_case.change_password.ChangePasswordInteractor;
import use_case.change_password.ChangePasswordOutputBoundary;
import use_case.login.LoginInputBoundary;
import use_case.login.LoginInteractor;
import use_case.login.LoginOutputBoundary;
import use_case.logout.LogoutInputBoundary;
import use_case.logout.LogoutInteractor;
import use_case.logout.LogoutOutputBoundary;
import use_case.signup.SignupInputBoundary;
import use_case.signup.SignupInteractor;
import use_case.signup.SignupOutputBoundary;
import view.LoggedInView;
import view.LoginView;
import view.SignupView;
import view.ViewManager;
import view.WelcomeView;
import view.SearchUserView;
import view.ChatView;
import view.AccountDetailsView;
import view.ChatSettingView;

import entity.ports.MessageRepository;
import data_access.FirebaseMessageRepository;
import com.google.cloud.firestore.Firestore;

import javax.swing.*;
import java.awt.*;

public class AppBuilder {
    private final JPanel cardPanel = new JPanel();
    private final CardLayout cardLayout = new CardLayout();
    // ChatRepository
    private final ChatRepository chatRepository =
            new InMemoryChatRepository();

    // MessageRepository
    private final MessageRepository messageRepository =
            new InMemoryMessageRepository();

    // UserRepository
    private final UserRepository userRepository =
            new InMemoryUserRepository();

    // Delete Message ViewModel
    private final DeleteMessageViewModel deleteMessageViewModel = new DeleteMessageViewModel();

    // Firebase Message Repository
    final MessageRepository firebaseMessageRepository;

    final UserFactory userFactory = new UserFactory();
    final ViewManagerModel viewManagerModel = new ViewManagerModel();
    ViewManager viewManager = new ViewManager(cardPanel, cardLayout, viewManagerModel);

    // set which data access implementation to use, can be any
    // of the classes from the data_access package

    // DAO version using a shared external database
    static final String serviceAccountKeyPath = "src/main/resources/serviceAccountKey.json";
    final FireBaseUserDataAccessObject userDataAccessObject = new FireBaseUserDataAccessObject(
            userRepository,
            chatRepository,
            messageRepository,
            serviceAccountKeyPath,
            userFactory
    );

    {
        Firestore dbInstance = userDataAccessObject.getDbInstance();
        firebaseMessageRepository = new FirebaseMessageRepository(dbInstance);
    }

    private SignupView signupView;
    private SignupViewModel signupViewModel;
    private LoginViewModel loginViewModel;
    private LoggedInViewModel loggedInViewModel;
    private LoggedInView loggedInView;
    private LoginView loginView;
    private WelcomeView welcomeView;
    private ChatView chatView;
    private AccountDetailsView accountDetailsView;

    // Field for the Search User use case
    private final SearchUserViewModel searchUserViewModel = new SearchUserViewModel();
    private SearchUserView searchUserView;

    // Field for send message
    private final ChatViewModel chatViewModel = new ChatViewModel();
    private final GroupChatViewModel groupChatViewModel = new GroupChatViewModel();
    private ChatSettingView chatSettingView;

    public AppBuilder() {
        cardPanel.setLayout(cardLayout);
    }

    public AppBuilder addWelcomeView() {
        welcomeView = new WelcomeView(viewManagerModel);
        cardPanel.add(welcomeView, welcomeView.getViewName());
        return this;
    }

    public AppBuilder addSignupView() {
        signupViewModel = new SignupViewModel();
        signupView = new SignupView(signupViewModel);
        cardPanel.add(signupView, signupView.getViewName());
        return this;
    }

    public AppBuilder addLoginView() {
        loginViewModel = new LoginViewModel();
        loginView = new LoginView(loginViewModel, viewManagerModel);
        cardPanel.add(loginView, loginView.getViewName());
        return this;
    }

    public AppBuilder addLoggedInView() {
        loggedInViewModel = new LoggedInViewModel();
        loggedInView = new LoggedInView(loggedInViewModel, viewManagerModel);
        cardPanel.add(loggedInView, loggedInView.getViewName());
        return this;
    }

    public AppBuilder addSignupUseCase() {
        final SignupOutputBoundary signupOutputBoundary = new SignupPresenter(viewManagerModel,
                signupViewModel, loginViewModel);
        final SignupInputBoundary userSignupInteractor = new SignupInteractor(
                userDataAccessObject, signupOutputBoundary, userFactory);

        SignupController controller = new SignupController(userSignupInteractor);
        signupView.setSignupController(controller);
        return this;
    }

    public AppBuilder addLoginUseCase() {
        final LoginOutputBoundary loginOutputBoundary = new LoginPresenter(viewManagerModel,
                loggedInViewModel, loginViewModel);
        final LoginInputBoundary loginInteractor = new LoginInteractor(
                userDataAccessObject, loginOutputBoundary);

        LoginController loginController = new LoginController(loginInteractor);
        loginView.setLoginController(loginController);
        return this;
    }

    public AppBuilder addChangePasswordUseCase() {
        final ChangePasswordOutputBoundary changePasswordOutputBoundary = new ChangePasswordPresenter(viewManagerModel,
                loggedInViewModel);

        final ChangePasswordInputBoundary changePasswordInteractor =
                new ChangePasswordInteractor(userDataAccessObject, changePasswordOutputBoundary, userFactory);

        ChangePasswordController changePasswordController = new ChangePasswordController(changePasswordInteractor);

        loggedInView.setChangePasswordController(changePasswordController);

        if (accountDetailsView != null) {
            accountDetailsView.setChangePasswordController(changePasswordController);
        }

        return this;
    }

    /**
     * Adds the Logout Use Case to the application.
     * @return this builder
     */
    public AppBuilder addLogoutUseCase() {
        final LogoutOutputBoundary logoutOutputBoundary = new LogoutPresenter(viewManagerModel,
                loggedInViewModel, loginViewModel);

        final LogoutInputBoundary logoutInteractor =
                new LogoutInteractor(userDataAccessObject, logoutOutputBoundary);

        final LogoutController logoutController = new LogoutController(logoutInteractor);

        loggedInView.setLogoutController(logoutController);

        if (accountDetailsView != null) {
            accountDetailsView.setLogoutController(logoutController);
        }

        return this;
    }

    public JFrame build() {
        final JFrame application = new JFrame("GoChat");
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        application.add(cardPanel);

        // Set the active view to WelcomeView
        viewManagerModel.setState(welcomeView.getViewName());
        viewManagerModel.firePropertyChange();

        return application;
    }

    public AppBuilder addAccountDetailsView() {
        accountDetailsView = new AccountDetailsView(viewManagerModel, loggedInViewModel);
        cardPanel.add(accountDetailsView, accountDetailsView.getViewName());
        return this;
    }


    /**
     * Adds the Search User View to the application and instantiates it correctly.
     * @return this builder
     */
    public AppBuilder addSearchUserView() {
        this.searchUserView = new SearchUserView(
                viewManagerModel,
                searchUserViewModel,
                groupChatViewModel,
                loggedInViewModel
        );
        cardPanel.add(searchUserView, searchUserView.getViewName());
        return this;
    }

    /**
     * Adds the User Search Use Case to the application.
     * @return this builder
     */
    public AppBuilder addUserSearchUseCase() {
        final SearchUserOutputBoundary searchUserOutputBoundary =
                new SearchUserPresenter(searchUserViewModel);

        final SearchUserInputBoundary searchUsersInteractor =
                new SearchUserInteractor(
                        userDataAccessObject,
                        searchUserOutputBoundary,
                        userRepository
                );

        final SearchUserController searchUserController = new SearchUserController(searchUsersInteractor);

        if (this.searchUserView != null) {
            this.searchUserView.setUserSearchController(searchUserController);
        }

        return this;
    }

    public AppBuilder addCreateChatUseCase() {
        final CreateChatOutputBoundary createChatOutputBoundary =
                new CreateChatPresenter(viewManagerModel, chatViewModel);

        final CreateChatInputBoundary createChatInteractor =
                new CreateChatInteractor(
                        createChatOutputBoundary,
                        userDataAccessObject,
                        chatRepository,
                        userRepository
                );

        final CreateChatController createChatController = new CreateChatController(createChatInteractor);

        if (this.searchUserView != null) {
            this.searchUserView.setCreateChatController(createChatController);
        }

        return this;
    }

    public AppBuilder addChatView() {
        this.chatView = new ChatView(
                viewManagerModel,
                chatViewModel,
                loggedInViewModel,
                deleteMessageViewModel);
        cardPanel.add(chatView, chatView.getViewName());
        return this;
    }

    public AppBuilder addChatUseCase() {
        // Presenter send and history
        SendMessageOutputBoundary sendMessagePresenter =
                new SendMessagePresenter(chatViewModel, viewManagerModel);

        ViewChatHistoryOutputBoundary viewHistoryPresenter =
                new ViewChatHistoryPresenter(chatViewModel, viewManagerModel);

        DeleteMessageOutputBoundary deleteMessagePresenter =
                new DeleteMessagePresenter(deleteMessageViewModel);

        // Interactor
        SendMessageInputBoundary sendMessageInteractor =
                new SendMessageInteractor(
                        chatRepository,
                        messageRepository,
                        userRepository,
                        sendMessagePresenter,
                        userDataAccessObject
                );

        ViewChatHistoryInputBoundary viewHistoryInteractor =
                new ViewChatHistoryInteractor(
                        chatRepository,
                        messageRepository,
                        userRepository,
                        viewHistoryPresenter,
                        userDataAccessObject
                );

        DeleteMessageInputBoundary deleteMessageInteractor =
                new DeleteMessageInteractor(
                        userDataAccessObject,
                        deleteMessagePresenter
                );


        // Controller
        ViewChatHistoryController viewChatHistoryController = new ViewChatHistoryController(viewHistoryInteractor);
        SendMessageController sendMessageController = new SendMessageController(sendMessageInteractor);
        DeleteMessageController deleteMessageController = new DeleteMessageController(deleteMessageInteractor);

        if (this.chatView != null) {
            this.chatView.setSendMessageController(sendMessageController);
            this.chatView.setViewChatHistoryController(viewChatHistoryController);
            this.chatView.setDeleteMessageController(deleteMessageController);
        }

        return this;
    }
}