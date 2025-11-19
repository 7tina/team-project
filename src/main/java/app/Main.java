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
                .addChatUseCase()
                .addSearchUserView()
                .addSignupUseCase()
                .addLoginUseCase()
                .addLogoutUseCase()
                .addChangePasswordUseCase()
                .addChangeUsernameUseCase()
                .addUserSearchUseCase()
                .build();

        application.pack();
        application.setLocationRelativeTo(null);
        application.setVisible(true);
    }
}
