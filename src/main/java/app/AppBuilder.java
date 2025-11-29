package app;

import java.awt.CardLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import data_access.FireBaseUserDataAccessObject;
import data_access.FirebaseClientProvider;
import data_access.FirestoreUserRepository;
import entity.UserFactory;
import entity.ports.ChatRepository;
import entity.ports.MessageRepository;
import entity.ports.UserRepository;
import entity.repo.InMemoryChatRepository;
import entity.repo.InMemoryMessageRepository;
import interface_adapter.ViewManagerModel;
import interface_adapter.create_chat.CreateChatController;
import interface_adapter.create_chat.CreateChatPresenter;
import interface_adapter.groupchat.adduser.AddUserController;
import interface_adapter.groupchat.adduser.AddUserPresenter;
import interface_adapter.groupchat.changegroupname.ChangeGroupNameController;
import interface_adapter.groupchat.changegroupname.ChangeGroupNamePresenter;
import interface_adapter.groupchat.removeuser.RemoveUserController;
import interface_adapter.groupchat.removeuser.RemoveUserPresenter;
import interface_adapter.logged_in.ChangePasswordController;
import interface_adapter.logged_in.ChangePasswordPresenter;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.login.LoginController;
import interface_adapter.login.LoginPresenter;
import interface_adapter.login.LoginViewModel;
import interface_adapter.logout.LogoutController;
import interface_adapter.logout.LogoutPresenter;
import interface_adapter.messaging.ChatViewModel;
import interface_adapter.messaging.delete_m.DeleteMessageController;
import interface_adapter.messaging.delete_m.DeleteMessagePresenter;
import interface_adapter.messaging.send_m.SendMessageController;
import interface_adapter.messaging.send_m.SendMessagePresenter;
import interface_adapter.messaging.view_history.ViewChatHistoryController;
import interface_adapter.messaging.view_history.ViewChatHistoryPresenter;
import interface_adapter.recent_chat.RecentChatsController;
import interface_adapter.recent_chat.RecentChatsPresenter;
import interface_adapter.signup.SignupController;
import interface_adapter.signup.SignupPresenter;
import interface_adapter.signup.SignupViewModel;
import interface_adapter.search_user.SearchUserController;
import interface_adapter.search_user.SearchUserPresenter;
import interface_adapter.search_user.SearchUserViewModel;
import interface_adapter.signup.SignupController;
import interface_adapter.signup.SignupPresenter;
import interface_adapter.signup.SignupViewModel;
import use_case.change_password.ChangePasswordInputBoundary;
import use_case.change_password.ChangePasswordInteractor;
import use_case.change_password.ChangePasswordOutputBoundary;
import use_case.create_chat.CreateChatInputBoundary;
import use_case.create_chat.CreateChatInteractor;
import use_case.create_chat.CreateChatOutputBoundary;
import use_case.groups.adduser.AddUserInputBoundary;
import use_case.groups.adduser.AddUserInteractor;
import use_case.groups.adduser.AddUserOutputBoundary;
import use_case.groups.changegroupname.ChangeGroupNameInputBoundary;
import use_case.groups.changegroupname.ChangeGroupNameInteractor;
import use_case.groups.changegroupname.ChangeGroupNameOutputBoundary;
import use_case.groups.removeuser.RemoveUserInputBoundary;
import use_case.groups.removeuser.RemoveUserInteractor;
import use_case.groups.removeuser.RemoveUserOutputBoundary;
import use_case.login.LoginInputBoundary;
import use_case.login.LoginInteractor;
import use_case.login.LoginOutputBoundary;
import use_case.logout.LogoutInputBoundary;
import use_case.logout.LogoutInteractor;
import use_case.logout.LogoutOutputBoundary;
import use_case.messaging.delete_m.DeleteMessageInputBoundary;
import use_case.messaging.delete_m.DeleteMessageInteractor;
import use_case.messaging.delete_m.DeleteMessageOutputBoundary;
import use_case.messaging.send_m.SendMessageInputBoundary;
import use_case.messaging.send_m.SendMessageInteractor;
import use_case.messaging.send_m.SendMessageOutputBoundary;
import use_case.messaging.view_history.ViewChatHistoryInputBoundary;
import use_case.messaging.view_history.ViewChatHistoryInteractor;
import use_case.messaging.view_history.ViewChatHistoryOutputBoundary;
import use_case.recent_chat.RecentChatsInputBoundary;
import use_case.recent_chat.RecentChatsInteractor;
import use_case.recent_chat.RecentChatsOutputBoundary;
import use_case.search_user.SearchUserInputBoundary;
import use_case.search_user.SearchUserInteractor;
import use_case.search_user.SearchUserOutputBoundary;
import use_case.signup.SignupInputBoundary;
import use_case.signup.SignupInteractor;
import use_case.signup.SignupOutputBoundary;
import view.AccountDetailsView;
import view.ChatSettingView;
import view.ChatView;
import view.LoggedInView;
import view.LoginView;
import view.SearchUserView;
import view.SignupView;
import view.ViewManager;
import view.WelcomeView;

// CHECKSTYLE:OFF

/**
 * Builder that wires together all views and use cases for the GoChat application.
 */
public class AppBuilder {

    private static final String SERVICE_ACCOUNT_KEY_PATH =
            "src/main/resources/serviceAccountKey.json";

    private final JPanel cardPanel = new JPanel();
    private final CardLayout cardLayout = new CardLayout();

    // ChatRepository and MessageRepository.
    private final ChatRepository chatRepository = new InMemoryChatRepository();
    private final MessageRepository messageRepository = new InMemoryMessageRepository();

    private final UserFactory userFactory = new UserFactory();
    private final ViewManagerModel viewManagerModel = new ViewManagerModel();

    private final UserRepository userRepository = new FirestoreUserRepository(
            FirebaseClientProvider.getFirestore(),
            userFactory
    );

    // DAO version using a shared external database.
    private final FireBaseUserDataAccessObject userDataAccessObject =
            new FireBaseUserDataAccessObject(
                    userRepository,
                    chatRepository,
                    messageRepository,
                    SERVICE_ACCOUNT_KEY_PATH,
                    userFactory
            );

    private SignupView signupView;
    private SignupViewModel signupViewModel;
    private LoginViewModel loginViewModel;
    private LoggedInViewModel loggedInViewModel;
    private LoggedInView loggedInView;
    private LoginView loginView;
    private WelcomeView welcomeView;
    private ChatView chatView;
    private AccountDetailsView accountDetailsView;

    // Field for the Search User use case.
    private final SearchUserViewModel searchUserViewModel = new SearchUserViewModel();
    private SearchUserView searchUserView;

    // Field for send message.
    private final ChatViewModel chatViewModel = new ChatViewModel();
    private ChatSettingView chatSettingView;

    /**
     * Constructs an instance of AppBuilder.
     */
    public AppBuilder() {
        cardPanel.setLayout(cardLayout);
        // Create ViewManager to manage transitions.
        new ViewManager(cardPanel, cardLayout, viewManagerModel);
    }

    /**
     * Adds the welcome view to the application.
     *
     * @return this builder
     */
    public AppBuilder addWelcomeView() {
        welcomeView = new WelcomeView(viewManagerModel);
        cardPanel.add(welcomeView, welcomeView.getViewName());
        return this;
    }

    /**
     * Adds the signup view to the application.
     *
     * @return this builder
     */
    public AppBuilder addSignupView() {
        signupViewModel = new SignupViewModel();
        signupView = new SignupView(signupViewModel);
        cardPanel.add(signupView, signupView.getViewName());
        return this;
    }

    /**
     * Adds the login view to the application.
     *
     * @return this builder
     */
    public AppBuilder addLoginView() {
        loginViewModel = new LoginViewModel();
        loginView = new LoginView(loginViewModel, viewManagerModel);
        cardPanel.add(loginView, loginView.getViewName());
        return this;
    }

    /**
     * Adds the logged-in view to the application.
     *
     * @return this builder
     */
    public AppBuilder addLoggedInView() {
        loggedInViewModel = new LoggedInViewModel();
        loggedInView = new LoggedInView(loggedInViewModel, viewManagerModel);
        cardPanel.add(loggedInView, loggedInView.getViewName());
        return this;
    }

    /**
     * Adds the signup use case wiring to the application.
     *
     * @return this builder
     */
    public AppBuilder addSignupUseCase() {
        final SignupOutputBoundary signupOutputBoundary = new SignupPresenter(
                viewManagerModel,
                signupViewModel,
                loginViewModel
        );
        final SignupInputBoundary userSignupInteractor = new SignupInteractor(
                userDataAccessObject,
                signupOutputBoundary,
                userFactory
        );

        final SignupController controller = new SignupController(userSignupInteractor);
        signupView.setSignupController(controller);
        return this;
    }

    /**
     * Adds the login use case wiring to the application.
     *
     * @return this builder
     */
    public AppBuilder addLoginUseCase() {
        final LoginOutputBoundary loginOutputBoundary = new LoginPresenter(
                viewManagerModel,
                loggedInViewModel,
                loginViewModel
        );
        final LoginInputBoundary loginInteractor = new LoginInteractor(
                userDataAccessObject,
                loginOutputBoundary
        );

        final LoginController loginController = new LoginController(loginInteractor);
        loginView.setLoginController(loginController);
        return this;
    }

    /**
     * Adds the change-password use case wiring to the application.
     *
     * @return this builder
     */
    public AppBuilder addChangePasswordUseCase() {
        final ChangePasswordOutputBoundary changePasswordOutputBoundary =
                new ChangePasswordPresenter(
                        viewManagerModel,
                        loggedInViewModel
                );

        final ChangePasswordInputBoundary changePasswordInteractor =
                new ChangePasswordInteractor(
                        userDataAccessObject,
                        changePasswordOutputBoundary,
                        userFactory
                );

        final ChangePasswordController changePasswordController =
                new ChangePasswordController(changePasswordInteractor);

        loggedInView.setChangePasswordController(changePasswordController);

        if (accountDetailsView != null) {
            accountDetailsView.setChangePasswordController(changePasswordController);
        }

        return this;
    }

    /**
     * Adds the logout use case wiring to the application.
     *
     * @return this builder
     */
    public AppBuilder addLogoutUseCase() {
        final LogoutOutputBoundary logoutOutputBoundary = new LogoutPresenter(
                viewManagerModel,
                loggedInViewModel,
                loginViewModel
        );

        final LogoutInputBoundary logoutInteractor =
                new LogoutInteractor(userDataAccessObject, logoutOutputBoundary);

        final LogoutController logoutController = new LogoutController(logoutInteractor);

        loggedInView.setLogoutController(logoutController);

        if (accountDetailsView != null) {
            accountDetailsView.setLogoutController(logoutController);
        }

        return this;
    }

    /**
     * Builds the main application frame.
     *
     * @return the application frame
     */
    public JFrame build() {
        final JFrame application = new JFrame("GoChat");
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        application.add(cardPanel);

        // Set the active view to WelcomeView.
        viewManagerModel.setState(welcomeView.getViewName());
        viewManagerModel.firePropertyChange();

        return application;
    }

    /**
     * Adds the account details view to the application.
     *
     * @return this builder
     */
    public AppBuilder addAccountDetailsView() {
        accountDetailsView = new AccountDetailsView(viewManagerModel, loggedInViewModel);
        cardPanel.add(accountDetailsView, accountDetailsView.getViewName());
        return this;
    }

    /**
     * Adds the search-user view to the application and instantiates it.
     *
     * @return this builder
     */
    public AppBuilder addSearchUserView() {
        this.searchUserView = new SearchUserView(
                viewManagerModel,
                searchUserViewModel,
                chatViewModel,
                loggedInViewModel
        );
        cardPanel.add(searchUserView, searchUserView.getViewName());
        return this;
    }

    /**
     * Adds the user-search use case wiring to the application.
     *
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

        final SearchUserController searchUserController =
                new SearchUserController(searchUsersInteractor);

        if (this.searchUserView != null) {
            this.searchUserView.setUserSearchController(searchUserController);
        }

        return this;
    }

    /**
     * Adds the create-chat use case wiring to the application.
     *
     * @return this builder
     */
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

        final CreateChatController createChatController =
                new CreateChatController(createChatInteractor);

        if (this.searchUserView != null) {
            this.searchUserView.setCreateChatController(createChatController);
        }

        return this;
    }

    /**
     * Adds the chat view to the application.
     *
     * @return this builder
     */
    public AppBuilder addChatView() {
        this.chatView = new ChatView(viewManagerModel, chatViewModel, loggedInViewModel);
        cardPanel.add(chatView, chatView.getViewName());
        if (this.searchUserView != null) {
            this.searchUserView.setChatView(this.chatView);
        }
        return this;
    }

    /**
     * Adds the chat-related use cases to the application.
     *
     * @return this builder
     */
    public AppBuilder addChatUseCase() {
        // Presenters.
        final SendMessageOutputBoundary sendMessagePresenter =
                new SendMessagePresenter(chatViewModel, viewManagerModel);

        final ViewChatHistoryOutputBoundary viewHistoryPresenter =
                new ViewChatHistoryPresenter(chatViewModel, viewManagerModel);

        // Interactors.
        final SendMessageInputBoundary sendMessageInteractor =
                new SendMessageInteractor(
                        chatRepository,
                        messageRepository,
                        userRepository,
                        sendMessagePresenter,
                        userDataAccessObject
                );

        final ViewChatHistoryInputBoundary viewHistoryInteractor =
                new ViewChatHistoryInteractor(
                        chatRepository,
                        messageRepository,
                        userRepository,
                        viewHistoryPresenter,
                        userDataAccessObject
                );

        // Controllers.
        final ViewChatHistoryController viewChatHistoryController =
                new ViewChatHistoryController(viewHistoryInteractor);
        final SendMessageController sendMessageController =
                new SendMessageController(sendMessageInteractor);

        if (this.chatView != null) {
            this.chatView.setSendMessageController(sendMessageController);
            this.chatView.setViewChatHistoryController(viewChatHistoryController);
        }

        return this;
    }

    /**
     * Adds the chat settings view to the application.
     *
     * @return this builder
     */
    public AppBuilder addChatSettingView() {
        this.chatSettingView = new ChatSettingView(viewManagerModel, chatViewModel);
        cardPanel.add(chatSettingView, chatSettingView.getViewName());

        if (this.chatView != null) {
            this.chatView.setChatSettingView(this.chatSettingView);
        }

        return this;
    }

    /**
     * Adds the change-group-name use case wiring to the application.
     *
     * @return this builder
     */
    public AppBuilder addChangeGroupNameUseCase() {
        final ChangeGroupNameOutputBoundary changeGroupNameOutputBoundary =
                new ChangeGroupNamePresenter(chatViewModel);

        final ChangeGroupNameInputBoundary changeGroupNameInteractor =
                new ChangeGroupNameInteractor(
                        chatRepository,
                        changeGroupNameOutputBoundary,
                        userDataAccessObject
                );

        final ChangeGroupNameController changeGroupNameController =
                new ChangeGroupNameController(changeGroupNameInteractor);

        if (this.chatSettingView != null) {
            this.chatSettingView.setChangeGroupNameController(changeGroupNameController);
        }

        return this;
    }

    /**
     * Adds the remove-user-from-group use case wiring to the application.
     *
     * @return this builder
     */
    public AppBuilder addRemoveUserUseCase() {
        final RemoveUserOutputBoundary removeUserOutputBoundary =
                new RemoveUserPresenter(chatViewModel);

        final RemoveUserInputBoundary removeUserInteractor =
                new RemoveUserInteractor(
                        chatRepository,
                        removeUserOutputBoundary,
                        userDataAccessObject
                );

        final RemoveUserController removeUserController =
                new RemoveUserController(removeUserInteractor);

        if (this.chatSettingView != null) {
            this.chatSettingView.setRemoveUserController(removeUserController);
        }

        return this;
    }

    /**
     * Adds the add-user-to-group use case wiring to the application.
     *
     * @return this builder
     */
    public AppBuilder addAddUserUseCase() {
        final AddUserOutputBoundary addUserOutputBoundary =
                new AddUserPresenter(chatViewModel);

        final AddUserInputBoundary addUserInteractor =
                new AddUserInteractor(
                        chatRepository,
                        addUserOutputBoundary,
                        userDataAccessObject
                );

        final AddUserController addUserController =
                new AddUserController(addUserInteractor);

        if (this.chatSettingView != null) {
            this.chatSettingView.setAddUserController(addUserController);
        }

        return this;
    }

    /**
     * Adds the delete-message use case wiring to the application.
     *
     * @return this builder
     */
    public AppBuilder addDeleteMessageUseCase() {
        final DeleteMessageOutputBoundary deletePresenter =
                new DeleteMessagePresenter(chatViewModel, viewManagerModel);

        final DeleteMessageInputBoundary deleteInteractor =
                new DeleteMessageInteractor(userDataAccessObject, deletePresenter);

        final DeleteMessageController deleteController =
                new DeleteMessageController(deleteInteractor);

        if (this.chatView != null) {
            this.chatView.setDeleteMessageController(deleteController);
        }

        return this;
    }

    public AppBuilder addRecentChatsUseCase() {
        RecentChatsOutputBoundary recentChatsPresenter =
                new RecentChatsPresenter(viewManagerModel, loggedInViewModel, chatViewModel);

        RecentChatsInputBoundary recentChatsInteractor =
                new RecentChatsInteractor(recentChatsPresenter,
                        userDataAccessObject,
                        messageRepository,
                        userRepository,
                        chatRepository);

        RecentChatsController recentChatsController =
                new RecentChatsController(recentChatsInteractor);
        if (this.chatView != null) {
            this.chatView.setRecentChatsController(recentChatsController);
        }
        if (this.chatView != null) {
            this.chatView.setRecentChatsController(recentChatsController);
        }
        return this;
    }
}
