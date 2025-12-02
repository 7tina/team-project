package view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.*;

import interfaceadapter.ViewManagerModel;
import interfaceadapter.logged_in.ChangePasswordController;
import interfaceadapter.logged_in.LoggedInState;
import interfaceadapter.logged_in.LoggedInViewModel;
import interfaceadapter.logout.LogoutController;

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
    private final DefaultListModel<String> recentChatsModel;
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

        final JPanel profileChatPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        profileChatPanel.add(profileButton);

        usernameLabel = new JLabel("User");
        usernameLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        userInfoPanel.add(profileChatPanel);
        userInfoPanel.add(usernameLabel);

        topBar.add(userInfoPanel, BorderLayout.WEST);

        // Right Side: New Chat Button (Plus Icon)
        newChatButton = new JButton("➕");
        newChatButton.setFont(new Font("SansSerif", Font.PLAIN, 24));

        final JPanel newChatPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        newChatPanel.add(newChatButton);

        topBar.add(newChatPanel, BorderLayout.EAST);

        // Recent Chats Panel
        final JLabel recentChatsTitle = new JLabel("Recent Chats");
        recentChatsTitle.setFont(new Font("SansSerif", Font.BOLD, 20));

        recentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        recentPanel.add(recentChatsTitle);
        recentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        recentPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, recentPanel.getPreferredSize().height));

        final JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setBorder(BorderFactory.createDashedBorder(Color.BLACK));
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        recentChatsModel = new DefaultListModel<String>();
        recentChatsList = new JList<>(recentChatsModel);
        final JScrollPane scrollPane = new JScrollPane(recentChatsList);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        final JPanel buttonPanel = new JPanel();
        logOut = new JButton("Logout");

        // PROFILE BUTTON: Navigate to AccountDetailsView
        profileButton.addActionListener(evnt -> {
            viewManagerModel.setState("account details");
            viewManagerModel.firePropertyChange();
        });

        // NEW CHAT BUTTON: Navigate to NewChatView
        newChatButton.addActionListener(evnt -> {
            viewManagerModel.setState("new chat");
            viewManagerModel.firePropertyChange();
        });

        // LOGOUT BUTTON:
        logOut.addActionListener(this);

        buttonPanel.add(logOut);
        // Keep hidden
        buttonPanel.setVisible(false);

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

        else if (evt.getPropertyName().equals("recentChats")) {
            final LoggedInState state = (LoggedInState) evt.getNewValue();
            final List<String> chatNames = state.getChatNames();
            for (String name : chatNames) {
                recentChatsModel.addElement(name);
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