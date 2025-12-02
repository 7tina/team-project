package app;

import javax.swing.JFrame;

public class Main {

    /**
     * The main entry point of the application.
     * @param args command line arguments
     */
    public static void main(String[] args) {
        final AppBuilder appBuilder = new AppBuilder();
        final JFrame application = appBuilder
                .addWelcomeView()
                .addLoginView()
                .addSignupView()
                .addLoggedInView()
                .addAccountDetailsView()
                .addSearchUserView()
                .addChatView()
                .addChatSettingView()
                .addSignupUseCase()
                .addLoginUseCase()
                .addLogoutUseCase()
                .addChangePasswordUseCase()
                .addUserSearchUseCase()
                .addCreateChatUseCase()
                .addCreateGroupChatUseCase()
                .addChatUseCase()
                .addReactionUseCases()
                .addChangeGroupNameUseCase()
                .addRemoveUserUseCase()
                .addAddUserUseCase()
                .addDeleteMessageUseCase()
                .addRecentChatsUseCase()
                .build();

        application.pack();
        application.setLocationRelativeTo(null);
        application.setVisible(true);
    }
}
