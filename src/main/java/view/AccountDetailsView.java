package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.logged_in.ChangePasswordController;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.logout.LogoutController;
import interface_adapter.logged_in.LoggedInState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import interface_adapter.logged_in.ChangeUsernameController;

/**
 * The View for displaying Account Details, allowing password changes, and handling the Logout functionality.
 */
public class AccountDetailsView extends JPanel implements ActionListener, PropertyChangeListener {

    public final String viewName = "account details";
    private final ViewManagerModel viewManagerModel;
    private final LoggedInViewModel loggedInViewModel;
    private LogoutController logoutController;
    private ChangePasswordController changePasswordController;
    private ChangeUsernameController changeUsernameController;

    // Components
    private final JButton logoutButton;
    private final JButton changeUsernameButton;
    private final JButton changePasswordButton;
    private final JLabel usernameLabel;
    private final JPanel topBar;

    public AccountDetailsView(ViewManagerModel viewManagerModel, LoggedInViewModel loggedInViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.loggedInViewModel = loggedInViewModel;
        this.loggedInViewModel.addPropertyChangeListener(this);

        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Bar (Title and Back Button)
        topBar = new JPanel(new BorderLayout());

        JButton backButton = new JButton("⬅");
        backButton.setFont(new Font("SansSerif", Font.BOLD, 20));


        backButton.addActionListener(e -> {
            viewManagerModel.setState("logged in");
            viewManagerModel.firePropertyChange();
        });

        topBar.add(backButton, BorderLayout.WEST);

        JLabel title = new JLabel("Account Details", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        topBar.add(title, BorderLayout.CENTER);

        // Main Content Area
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(50, 20, 50, 20));

        String currentUsername = loggedInViewModel.getState().getUsername();
        usernameLabel = new JLabel("Username: " + (currentUsername != null ? currentUsername : "User"));
        usernameLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        changeUsernameButton = new JButton("Change Username");
        changeUsernameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        changeUsernameButton.addActionListener(this);

        changePasswordButton = new JButton("Change Password");
        changePasswordButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        changePasswordButton.addActionListener(this);

        contentPanel.add(usernameLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(changeUsernameButton);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(changePasswordButton);
        contentPanel.add(Box.createVerticalGlue());

        // Logout Button Panel (Bottom)
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoutButton = new JButton("Logout");
        logoutButton.addActionListener(this);

        southPanel.add(logoutButton);

        // Assembly
        this.add(topBar, BorderLayout.NORTH);
        this.add(contentPanel, BorderLayout.CENTER);
        this.add(southPanel, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource().equals(logoutButton) && logoutController != null) {
            logoutController.execute();
        } else if (evt.getSource().equals(changePasswordButton) && changePasswordController != null) {

            // Get the current username
            final String currentUsername = loggedInViewModel.getState().getUsername();

            // Prompt user for the new password
            String newPassword = JOptionPane.showInputDialog(this,
                    "Enter new password for " + currentUsername + ":",
                    "Change Password",
                    JOptionPane.PLAIN_MESSAGE
            );

            // Execute the use case if a password was entered
            if (newPassword != null) {
                changePasswordController.execute(
                        currentUsername,
                        newPassword
                );
            }
        } else if (evt.getSource().equals(changeUsernameButton) && changeUsernameController != null) {
            final String currentUsername = loggedInViewModel.getState().getUsername();

            String newUsername = JOptionPane.showInputDialog(this,
                    "Enter new username:",
                    "Change Username",
                    JOptionPane.PLAIN_MESSAGE
            );

            if (newUsername != null) {
                changeUsernameController.execute(
                        currentUsername,
                        newUsername
                );
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // Check for either a general state update or a specific username update event.
        if (evt.getPropertyName().equals("state") || evt.getPropertyName().equals("username")) {
            final LoggedInState state = (LoggedInState) evt.getNewValue();
            String newUsername = state.getUsername();

            // Update the label text
            usernameLabel.setText("Username: " + (newUsername != null ? newUsername : "User"));

        }
    }

    public String getViewName() {
        return viewName;
    }

    public void setLogoutController(LogoutController logoutController) {
        this.logoutController = logoutController;
    }

    public void setChangePasswordController(ChangePasswordController changePasswordController) {
        this.changePasswordController = changePasswordController;
    }

    public void setChangeUsernameController(ChangeUsernameController changeUsernameController) {
        this.changeUsernameController = changeUsernameController;
    }
}