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
                .addSignupUseCase()
                .addLoginUseCase()
                .addLogoutUseCase()
                .addChangePasswordUseCase()
                .addUserSearchUseCase()
                .addCreateChatUseCase()
                .addCreateGroupChatUseCase()
                .addChatUseCase()
                .build();

        application.pack();
        application.setLocationRelativeTo(null);
        application.setVisible(true);
    }
}
