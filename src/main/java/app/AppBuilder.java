package app;

import data_access.FireBaseUserDataAccessObject; // Corrected class name
import data_access.FileUserDataAccessObject;
import entity.UserFactory;
import interface_adapter.ViewManagerModel;
import interface_adapter.groupchat.*;
import interface_adapter.logged_in.ChangePasswordController;
import interface_adapter.logged_in.ChangePasswordPresenter;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.login.LoginController;
import interface_adapter.login.LoginPresenter;
import interface_adapter.login.LoginViewModel;
import interface_adapter.logout.LogoutController;
import interface_adapter.logout.LogoutPresenter;
import interface_adapter.messaging.view_history.ViewChatHistoryController;
import interface_adapter.messaging.view_history.ViewChatHistoryPresenter;
import interface_adapter.signup.SignupController;
import interface_adapter.signup.SignupPresenter;
import interface_adapter.signup.SignupViewModel;
import interface_adapter.user_search.SearchUserController;
import interface_adapter.user_search.SearchUserPresenter;
import interface_adapter.user_search.SearchUserViewModel;
import interface_adapter.messaging.send_m.SendMessageController;
import interface_adapter.messaging.send_m.SendMessagePresenter;
import use_case.groups.*;
import use_case.messaging.send_m.SendMessageInputBoundary;
import use_case.messaging.send_m.SendMessageOutputBoundary;
import use_case.messaging.send_m.SendMessageInteractor;
import interface_adapter.messaging.send_m.ChatViewModel;

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
import view.*;
import interface_adapter.logged_in.ChangeUsernameController;
import interface_adapter.logged_in.ChangeUsernamePresenter;
import use_case.change_username.ChangeUsernameInputBoundary;
import use_case.change_username.ChangeUsernameInteractor;
import use_case.change_username.ChangeUsernameOutputBoundary;
import use_case.change_username.ChangeUsernameUserDataAccessInterface;
import use_case.signup.SignupUserDataAccessInterface;
import use_case.change_password.ChangePasswordUserDataAccessInterface;
import use_case.login.LoginUserDataAccessInterface;
import use_case.logout.LogoutUserDataAccessInterface;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class AppBuilder {
    private final JPanel cardPanel = new JPanel();
    private final CardLayout cardLayout = new CardLayout();

    // ChatRepository (for messaging use cases)
    private final use_case.ports.ChatRepository chatRepository =
            new interface_adapter.repo.InMemoryChatRepository();

    // MessageRepository (for messaging use cases)
    private final use_case.ports.MessageRepository messageRepository =
            new interface_adapter.repo.InMemoryMessageRepository();

    // UserRepository (for messaging use cases)
    private final use_case.ports.UserRepository userRepository =
            new interface_adapter.repo.InMemoryUserRepository();

    final UserFactory userFactory = new UserFactory();
    final ViewManagerModel viewManagerModel = new ViewManagerModel();
    ViewManager viewManager = new ViewManager(cardPanel, cardLayout, viewManagerModel);

    // --- Data Access Objects (User Authentication/Account) ---
    // Concrete DAO initialized here
    private final FireBaseUserDataAccessObject concreteUserDAO;

    // Interface fields assigned to the concrete DAO
    private final SignupUserDataAccessInterface signupUserDataAccessObject; // Renamed to avoid conflict
    private final ChangePasswordUserDataAccessInterface changePasswordUserRepository;
    private final ChangeUsernameUserDataAccessInterface changeUsernameUserRepository;
    private final LoginUserDataAccessInterface loginUserRepository;
    private final LogoutUserDataAccessInterface logoutUserRepository;
    private final SearchUserDataAccessInterface searchUserRepository;


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

    private final GroupChatViewModel groupChatViewModel = new GroupChatViewModel();
    private ChatSettingView chatSettingView;

    // Field for send message
    private final ChatViewModel chatViewModel = new ChatViewModel();
    private ViewChatHistoryController viewChatHistoryController;

    private CreateGroupChatController createGroupChatController;


    public AppBuilder() {
        cardPanel.setLayout(cardLayout);

        // 1. Initialize Firebase DAO
        // The serviceAccountKey.json file is used for Firebase Admin SDK initialization.
        String serviceAccountKeyPath = "src/main/resources/serviceAccountKeyUsers.json";
        this.concreteUserDAO = new FireBaseUserDataAccessObject(serviceAccountKeyPath, userFactory);

        // 2. Assign the concrete DAO instance to all required interfaces
        this.signupUserDataAccessObject = this.concreteUserDAO;
        this.changePasswordUserRepository = this.concreteUserDAO;
        this.changeUsernameUserRepository = this.concreteUserDAO;
        this.loginUserRepository = this.concreteUserDAO;
        this.logoutUserRepository = this.concreteUserDAO;
        this.searchUserRepository = this.concreteUserDAO;


        // Use repo to store
        goc.chat.entity.User me =
                new goc.chat.entity.User("user-1", "demo", "demo@example.com", "hash");
        me = userRepository.save(me);
        messagingUserId = me.getId();

        // Get or create the demo user for messaging
        Optional<goc.chat.entity.User> demoUserOpt = userRepository.findByUsername("demo");
        if (demoUserOpt.isPresent()) {
            messagingUserId = demoUserOpt.get().getId();
        } else {
            // If demo user doesn't exist in CSV, create it
            goc.chat.entity.User me = new goc.chat.entity.User(
                    "user-1", "demo", "demo@example.com", "hash"
            );
            me = userRepository.save(me);
            messagingUserId = me.getId();
        }

        // Create initial chat
        entity.Chat c = new entity.Chat("chat-1");
        c.addParticipant(messagingUserId);
        c = chatRepository.save(c);
        messagingChatId = c.getId();
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

        // Use the new, correctly named interface field
        final SignupInputBoundary userSignupInteractor = new SignupInteractor(
                this.signupUserDataAccessObject, signupOutputBoundary, userFactory);

        SignupController controller = new SignupController(userSignupInteractor);
        signupView.setSignupController(controller);
        return this;
    }

    public AppBuilder addLoginUseCase() {
        final LoginOutputBoundary loginOutputBoundary = new LoginPresenter(viewManagerModel,
                loggedInViewModel, loginViewModel);

        // Use the login interface field
        final LoginInputBoundary loginInteractor = new LoginInteractor(
                this.loginUserRepository, loginOutputBoundary);

        LoginController loginController = new LoginController(loginInteractor);
        loginView.setLoginController(loginController);
        return this;
    }

    public AppBuilder addChangePasswordUseCase() {
        final ChangePasswordOutputBoundary changePasswordOutputBoundary = new ChangePasswordPresenter(viewManagerModel,
                loggedInViewModel);

        // Use the change password interface field
        final ChangePasswordInputBoundary changePasswordInteractor =
                new ChangePasswordInteractor(this.changePasswordUserRepository, changePasswordOutputBoundary, userFactory);

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

        // Use the logout interface field
        final LogoutInputBoundary logoutInteractor =
                new LogoutInteractor(this.logoutUserRepository, logoutOutputBoundary);

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

    public AppBuilder addChangeUsernameUseCase() {
        final ChangeUsernameOutputBoundary changeUsernameOutputBoundary =
                new ChangeUsernamePresenter(loggedInViewModel, viewManagerModel);

        // Use the change username interface field
        final ChangeUsernameInputBoundary changeUsernameInteractor =
                new ChangeUsernameInteractor(
                        this.changeUsernameUserRepository, // Already cast to the correct interface in field assignment
                        changeUsernameOutputBoundary,
                        userFactory
                );

        ChangeUsernameController changeUsernameController = new ChangeUsernameController(changeUsernameInteractor);

        if (accountDetailsView != null) {
            accountDetailsView.setChangeUsernameController(changeUsernameController);
        }

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
                chatView,
                groupChatViewModel,
                chatRepository,
                userRepository,
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

        // Use the search interface field
        final SearchUserInputBoundary searchUsersInteractor =
                new SearchUserInteractor(
                        this.searchUserRepository, // Already cast to the correct interface in field assignment
                        searchUserOutputBoundary
                );

        final SearchUserController searchUserController = new SearchUserController(searchUsersInteractor);

        if (this.searchUserView != null) {
            this.searchUserView.setUserSearchController(searchUserController);
        }

        return this;
    }

    public AppBuilder addChatUseCase() {
        SendMessageController sendMessageController;
        // Presenter send and history
        SendMessageOutputBoundary sendMessagePresenter =
                new SendMessagePresenter(chatViewModel, viewManagerModel);

        ViewChatHistoryOutputBoundary viewHistoryPresenter =
                new ViewChatHistoryPresenter(chatViewModel, viewManagerModel);

        // Interactor
        SendMessageInputBoundary sendMessageInteractor =
                new SendMessageInteractor(
                        chatRepository,
                        messageRepository,
                        userRepository, // This is the InMemoryUserRepository for messaging, which is correct
                        sendMessagePresenter
                );

        ViewChatHistoryInputBoundary viewHistoryInteractor =
                new ViewChatHistoryInteractor(
                        chatRepository,
                        messageRepository,
                        userRepository, // This is the InMemoryUserRepository for messaging, which is correct
                        viewHistoryPresenter
                );

        // Controller
        viewChatHistoryController = new ViewChatHistoryController(viewHistoryInteractor);
        sendMessageController = new SendMessageController(sendMessageInteractor);

        // view - 4 parameters: viewManagerModel, loggedInViewModel, sendMessageController, viewChatHistoryController
        chatView = new ChatView(viewManagerModel, loggedInViewModel, sendMessageController, viewChatHistoryController);
        chatViewModel.addPropertyChangeListener(chatView);
        cardPanel.add(chatView, chatView.getViewName());

        return this;
    }

    public AppBuilder addChatSettingView() {
        this.chatSettingView = new ChatSettingView(viewManagerModel);
        cardPanel.add(chatSettingView, chatSettingView.getViewName());

        if (this.chatView != null) {
            this.chatView.setChatSettingView(chatSettingView);
        }

        return this;
    }

    public AppBuilder addChangeGroupNameUseCase() {
        final ChangeGroupNameOutputBoundary outputBoundary =
                new ChangeGroupNamePresenter(groupChatViewModel);

        final ChangeGroupNameInputBoundary interactor =
                new RenameGroupChatInteractor(
                        chatRepository,
                        outputBoundary
                );

        final ChangeGroupNameController controller =
                new ChangeGroupNameController(interactor);

        // Wire controller to the view
        if (this.chatSettingView != null) {
            this.chatSettingView.setChangeGroupNameController(controller);
        }

        return this;
    }

    public AppBuilder addCreateGroupChatUseCase() {
        // Output Boundary
        final CreateGroupChatOutputBoundary outputBoundary =
                new CreateGroupChatPresenter(groupChatViewModel, viewManagerModel);

        // Interactor
        final CreateGroupChatInputBoundary interactor =
                new CreateGroupChatInteractor(
                        chatRepository,
                        userRepository,
                        outputBoundary
                );

        // Controller
        createGroupChatController = new CreateGroupChatController(interactor);

        // Wire controller to SearchUserView
        if (searchUserView != null) {
            searchUserView.setCreateGroupChatController(createGroupChatController);
        }

        return this;
    }
}