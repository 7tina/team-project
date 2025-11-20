// LoggedInView.java (Only showing the necessary changes/additions)
package view;

import interface_adapter.ViewManagerModel; // <-- NEW IMPORT NEEDED
import interface_adapter.logged_in.ChangePasswordController;
import interface_adapter.logged_in.LoggedInState;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.logout.LogoutController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * The View for when the user is logged into the program, now displaying the chat list.
 */
public class LoggedInView extends JPanel implements ActionListener, PropertyChangeListener {

    private final String viewName = "logged in";
    private final LoggedInViewModel loggedInViewModel;
    // Removed passwordErrorField
    private ChangePasswordController changePasswordController = null;
    private LogoutController logoutController;
    private final ViewManagerModel viewManagerModel;

    // Components for the New Design
    private final JLabel usernameLabel;
    private final JList<String> recentChatsList;
    private final JPanel recentPanel;
    private final JButton profileButton;
    private final JButton newChatButton;
    private final JButton logOut;

    public LoggedInView(LoggedInViewModel loggedInViewModel, ViewManagerModel viewManagerModel) {
        this.loggedInViewModel = loggedInViewModel;
        this.viewManagerModel = viewManagerModel;
        this.loggedInViewModel.addPropertyChangeListener(this);

        // Top Bar Panel (User, New Chat)
        JPanel topBar = new JPanel(new BorderLayout(5, 0));
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, topBar.getPreferredSize().height));

        // Left Side: Profile Button and Username Label
        JPanel userInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));

        profileButton = new JButton("👤");
        profileButton.setFont(new Font("SansSerif", Font.PLAIN, 24));
//        profileButton.setFocusPainted(false);
//        profileButton.setBorderPainted(false);
//        profileButton.setContentAreaFilled(false);

        JPanel profileChatPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        profileChatPanel.add(profileButton);

        usernameLabel = new JLabel("User");
        usernameLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        userInfoPanel.add(profileChatPanel);
        userInfoPanel.add(usernameLabel);

        topBar.add(userInfoPanel, BorderLayout.WEST);

        // Right Side: New Chat Button (Plus Icon)
        newChatButton = new JButton("➕");
        newChatButton.setFont(new Font("SansSerif", Font.PLAIN, 24));

        JPanel newChatPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        newChatPanel.add(newChatButton);

        topBar.add(newChatPanel, BorderLayout.EAST);

        // Recent Chats Panel
        JLabel recentChatsTitle = new JLabel("Recent Chats");
        recentChatsTitle.setFont(new Font("SansSerif", Font.BOLD, 20));

        recentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        recentPanel.add(recentChatsTitle);
        recentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        recentPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, recentPanel.getPreferredSize().height));

        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setBorder(BorderFactory.createDashedBorder(Color.BLACK));
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        recentChatsList = new JList<>(new DefaultListModel<String>());
        JScrollPane scrollPane = new JScrollPane(recentChatsList);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel buttonPanel = new JPanel();
        logOut = new JButton("Logout");

        // PROFILE BUTTON: Navigate to AccountDetailsView
        profileButton.addActionListener(e -> {
            viewManagerModel.setState("account details");
            viewManagerModel.firePropertyChange();
        });

        // NEW CHAT BUTTON: Navigate to NewChatView
        newChatButton.addActionListener(e -> {
            viewManagerModel.setState("new chat");
            viewManagerModel.firePropertyChange();
        });

        // LOGOUT BUTTON:
        logOut.addActionListener(this);

        buttonPanel.add(logOut);
        buttonPanel.setVisible(false); // Keep hidden

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        this.add(topBar);
        this.add(recentPanel);
        this.add(Box.createVerticalStrut(5));
        this.add(separator);
        this.add(scrollPane);
        this.add(buttonPanel);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource().equals(logOut) && logoutController != null) {
            logoutController.execute();
        }
        System.out.println("Click " + evt.getActionCommand());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("state")) {
            final LoggedInState state = (LoggedInState) evt.getNewValue();
            usernameLabel.setText(state.getUsername());
        }
        else if (evt.getPropertyName().equals("username")) {
            final LoggedInState state = (LoggedInState) evt.getNewValue();

            if (state.getUsernameError() == null) {
                JOptionPane.showMessageDialog(this,
                        "Username successfully updated to " + state.getUsername());
                usernameLabel.setText(state.getUsername());  // Update label too
            }
            else {
                JOptionPane.showMessageDialog(this,
                        state.getUsernameError(),
                        "Username Change Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        }

        else if (evt.getPropertyName().equals("password")) {
            final LoggedInState state = (LoggedInState) evt.getNewValue();
            if (state.getPasswordError() == null) {
                // This will show when a password change succeeds
                JOptionPane.showMessageDialog(this, "password updated for " + state.getUsername());
            }
            else {
                JOptionPane.showMessageDialog(this, state.getPasswordError());
            }
        }
    }

    public String getViewName() {
        return viewName;
    }

    public void setChangePasswordController(ChangePasswordController changePasswordController) {
        this.changePasswordController = changePasswordController;
    }

    public void setLogoutController(LogoutController logoutController) {
        this.logoutController = logoutController;
    }
}