package app;

import java.awt.CardLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import dataaccess.FireBaseUserDataAccessObject;
import dataaccess.FirebaseClientProvider;
import dataaccess.FirestoreUserRepository;
import entity.UserFactory;
import entity.ports.ChatRepository;
import entity.ports.MessageRepository;
import entity.ports.UserRepository;
import entity.repo.InMemoryChatRepository;
import entity.repo.InMemoryMessageRepository;
import interfaceadapter.ViewManagerModel;
import interfaceadapter.accesschat.AccessChatController;
import interfaceadapter.accesschat.AccessChatPresenter;
import interfaceadapter.create_chat.CreateChatController;
import interfaceadapter.create_chat.CreateChatPresenter;
import interfaceadapter.groupchat.adduser.AddUserController;
import interfaceadapter.groupchat.adduser.AddUserPresenter;
import interfaceadapter.groupchat.changegroupname.ChangeGroupNameController;
import interfaceadapter.groupchat.changegroupname.ChangeGroupNamePresenter;
import interfaceadapter.groupchat.removeuser.RemoveUserController;
import interfaceadapter.groupchat.removeuser.RemoveUserPresenter;
import interfaceadapter.logged_in.ChangePasswordController;
import interfaceadapter.logged_in.ChangePasswordPresenter;
import interfaceadapter.logged_in.LoggedInViewModel;
import interfaceadapter.login.LoginController;
import interfaceadapter.login.LoginPresenter;
import interfaceadapter.login.LoginViewModel;
import interfaceadapter.logout.LogoutController;
import interfaceadapter.logout.LogoutPresenter;
import interfaceadapter.messaging.ChatViewModel;
import interfaceadapter.messaging.deletemessage.DeleteMessageController;
import interfaceadapter.messaging.deletemessage.DeleteMessagePresenter;
import interfaceadapter.messaging.search_history.SearchChatHistoryController;
import interfaceadapter.messaging.search_history.SearchChatHistoryPresenter;
import interfaceadapter.messaging.sendmessage.SendMessageController;
import interfaceadapter.messaging.sendmessage.SendMessagePresenter;
import interfaceadapter.messaging.view_history.ViewChatHistoryController;
import interfaceadapter.messaging.view_history.ViewChatHistoryPresenter;
import interfaceadapter.recent_chat.RecentChatsController;
import interfaceadapter.recent_chat.RecentChatsPresenter;
import interfaceadapter.search_user.SearchUserController;
import interfaceadapter.search_user.SearchUserPresenter;
import interfaceadapter.search_user.SearchUserViewModel;
import interfaceadapter.signup.SignupController;
import interfaceadapter.signup.SignupPresenter;
import interfaceadapter.signup.SignupViewModel;
import usecase.accesschat.AccessChatInputBoundary;
import usecase.accesschat.AccessChatInteractor;
import usecase.accesschat.AccessChatOutputBoundary;
import usecase.change_password.ChangePasswordInputBoundary;
import usecase.change_password.ChangePasswordInteractor;
import usecase.change_password.ChangePasswordOutputBoundary;
import usecase.create_chat.CreateChatInputBoundary;
import usecase.create_chat.CreateChatInteractor;
import usecase.create_chat.CreateChatOutputBoundary;
import usecase.groups.adduser.AddUserInputBoundary;
import usecase.groups.adduser.AddUserInteractor;
import usecase.groups.adduser.AddUserOutputBoundary;
import usecase.groups.changegroupname.ChangeGroupNameInputBoundary;
import usecase.groups.changegroupname.ChangeGroupNameInteractor;
import usecase.groups.changegroupname.ChangeGroupNameOutputBoundary;
import usecase.groups.removeuser.RemoveUserInputBoundary;
import usecase.groups.removeuser.RemoveUserInteractor;
import usecase.groups.removeuser.RemoveUserOutputBoundary;
import usecase.login.LoginInputBoundary;
import usecase.login.LoginInteractor;
import usecase.login.LoginOutputBoundary;
import usecase.logout.LogoutInputBoundary;
import usecase.logout.LogoutInteractor;
import usecase.logout.LogoutOutputBoundary;
import usecase.messaging.deletemessage.DeleteMessageInputBoundary;
import usecase.messaging.deletemessage.DeleteMessageInteractor;
import usecase.messaging.deletemessage.DeleteMessageOutputBoundary;
import usecase.messaging.search_history.SearchChatHistoryInputBoundary;
import usecase.messaging.search_history.SearchChatHistoryInteractor;
import usecase.messaging.search_history.SearchChatHistoryOutputBoundary;
import usecase.messaging.sendmessage.SendMessageInputBoundary;
import usecase.messaging.sendmessage.SendMessageInteractor;
import usecase.messaging.sendmessage.SendMessageOutputBoundary;
import usecase.messaging.view_history.ViewChatHistoryInputBoundary;
import usecase.messaging.view_history.ViewChatHistoryInteractor;
import usecase.messaging.view_history.ViewChatHistoryOutputBoundary;
import usecase.recent_chat.RecentChatsInputBoundary;
import usecase.recent_chat.RecentChatsInteractor;
import usecase.recent_chat.RecentChatsOutputBoundary;
import usecase.search_user.SearchUserInputBoundary;
import usecase.search_user.SearchUserInteractor;
import usecase.search_user.SearchUserOutputBoundary;
import usecase.signup.SignupInputBoundary;
import usecase.signup.SignupInteractor;
import usecase.signup.SignupOutputBoundary;
import view.AccountDetailsView;
import view.ChatSettingView;
import view.ChatView;
import view.LoggedInView;
import view.LoginView;
import view.SearchUserView;
import view.SignupView;
import view.ViewManager;
import view.WelcomeView;
import interfaceadapter.recent_chat.RecentChatsController;
import interfaceadapter.recent_chat.RecentChatsPresenter;
import usecase.recent_chat.RecentChatsInputBoundary;
import usecase.recent_chat.RecentChatsInteractor;
import usecase.recent_chat.RecentChatsOutputBoundary;
import interfaceadapter.messaging.add_reaction.AddReactionController;
import interfaceadapter.messaging.add_reaction.AddReactionPresenter;
import interfaceadapter.messaging.remove_reaction.RemoveReactionController;
import interfaceadapter.messaging.remove_reaction.RemoveReactionPresenter;
import usecase.messaging.add_reaction.AddReactionInputBoundary;
import usecase.messaging.add_reaction.AddReactionInteractor;
import usecase.messaging.add_reaction.AddReactionOutputBoundary;
import usecase.messaging.remove_reaction.RemoveReactionInputBoundary;
import usecase.messaging.remove_reaction.RemoveReactionInteractor;
import usecase.messaging.remove_reaction.RemoveReactionOutputBoundary;

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

    // Field for send message / chat.
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
                new LogoutInteractor(userDataAccessObject, logoutOutputBoundary, chatRepository);

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

        final SearchChatHistoryOutputBoundary searchHistoryPresenter =
                new SearchChatHistoryPresenter(chatViewModel);

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

        final SearchChatHistoryInputBoundary searchHistoryInteractor =
                new SearchChatHistoryInteractor(
                        chatRepository,
                        messageRepository,
                        searchHistoryPresenter
                );

        // Controllers.
        final ViewChatHistoryController viewChatHistoryController =
                new ViewChatHistoryController(viewHistoryInteractor);
        final SendMessageController sendMessageController =
                new SendMessageController(sendMessageInteractor);

        // SearchChatHistoryController
        final SearchChatHistoryController searchChatHistoryController =
                new SearchChatHistoryController(searchHistoryInteractor);

        if (this.chatView != null) {
            this.chatView.setSendMessageController(sendMessageController);
            this.chatView.setViewChatHistoryController(viewChatHistoryController);
            this.chatView.setSearchChatHistoryController(searchChatHistoryController);
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

    /**
     * Adds the recent-chats use case wiring to the application.
     *
     * @return this builder
     */
    public AppBuilder addRecentChatsUseCase() {
        final RecentChatsOutputBoundary recentChatsPresenter =
                new RecentChatsPresenter(viewManagerModel, loggedInViewModel, chatViewModel);

        final RecentChatsInputBoundary recentChatsInteractor =
                new RecentChatsInteractor(recentChatsPresenter,
                        userDataAccessObject,
                        messageRepository,
                        userRepository,
                        chatRepository);

        final RecentChatsController recentChatsController =
                new RecentChatsController(recentChatsInteractor);

        if (this.chatView != null) {
            this.chatView.setRecentChatsController(recentChatsController);
        }

        if (this.loggedInView != null) {
            this.loggedInView.setRecentChatsController(recentChatsController);
        }

        return this;
    }

    /**
     * Adds the access-chats use case wiring to the application.
     *
     * @return this builder
     */
    public AppBuilder addAccessChatsUseCase() {
        final AccessChatOutputBoundary accessChatPresenter =
                new AccessChatPresenter(viewManagerModel, loggedInViewModel, chatViewModel);

        final AccessChatInputBoundary accessChatInteractor =
                new AccessChatInteractor(userDataAccessObject,
                        accessChatPresenter,
                        userRepository,
                        chatRepository);

        final AccessChatController accessChatController =
                new AccessChatController(accessChatInteractor);

        if (this.loggedInView != null) {
            this.loggedInView.setAccessChatController(accessChatController);
        }

        return this;
    }

    /**
     * Adds the reaction use cases (add and remove) to the application.
     *
     * @return this builder
     */
    public AppBuilder addReactionUseCases() {
        // Add Reaction
        final AddReactionOutputBoundary addReactionPresenter =
                new AddReactionPresenter(chatViewModel, viewManagerModel);

        final AddReactionInputBoundary addReactionInteractor =
                new AddReactionInteractor(
                        messageRepository,
                        userDataAccessObject,
                        addReactionPresenter
                );

        final AddReactionController addReactionController =
                new AddReactionController(addReactionInteractor);

        // Remove Reaction
        final RemoveReactionOutputBoundary removeReactionPresenter =
                new RemoveReactionPresenter(chatViewModel, viewManagerModel);

        final RemoveReactionInputBoundary removeReactionInteractor =
                new RemoveReactionInteractor(
                        messageRepository,
                        userDataAccessObject,
                        removeReactionPresenter
                );

        final RemoveReactionController removeReactionController =
                new RemoveReactionController(removeReactionInteractor);

        // Set controllers in chat view
        if (this.chatView != null) {
            this.chatView.setAddReactionController(addReactionController);
            this.chatView.setRemoveReactionController(removeReactionController);
        }

        return this;
    }
}
