package app;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        AppBuilder appBuilder = new AppBuilder();
        JFrame application = appBuilder
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
                .addChangeGroupNameUseCase()
                .addRemoveUserUseCase()
                .build();

        application.pack();
        application.setLocationRelativeTo(null);
        application.setVisible(true);
    }
}
