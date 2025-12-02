package view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.*;

import interfaceadapter.ViewManagerModel;
import interfaceadapter.create_chat.CreateChatController;
import interfaceadapter.groupchat.creategroupchat.CreateGroupChatController;
import interfaceadapter.logged_in.LoggedInState;
import interfaceadapter.logged_in.LoggedInViewModel;
import interfaceadapter.search_user.SearchUserController;
import interfaceadapter.search_user.SearchUserState;
import interfaceadapter.search_user.SearchUserViewModel;

public class SearchUserView extends JPanel implements ActionListener, PropertyChangeListener {

    private final String viewName = "new chat";

    private SearchUserController searchUserController;
    private final ViewManagerModel viewManagerModel;
    private final SearchUserViewModel searchUserViewModel;
    private final LoggedInViewModel loggedInViewModel;

    private CreateChatController createChatController;
    private CreateGroupChatController createGroupChatController;

    // Reference to ChatView for navigation
    private ChatView chatView;

    // UI Components
    private final JTextField searchInputField;
    private final JButton searchExitButton;
    private final JButton startChatButton;
    private JLabel selectionLabel;

    // JList and its model for displaying users
    private final JList<String> userList;
    private final DefaultListModel<String> userListModel;

    private boolean started = false;
    // Track the last user we loaded results for
    private String lastLoggedInUser = null;
    // Track if this view is currently displayed
    private boolean viewIsActive = false;

    public SearchUserView(ViewManagerModel viewManagerModel, SearchUserViewModel searchUserViewModel,
                          LoggedInViewModel loggedInViewModel) {

        this.viewManagerModel = viewManagerModel;
        this.searchUserViewModel = searchUserViewModel;
        this.loggedInViewModel = loggedInViewModel;
        this.searchUserViewModel.addPropertyChangeListener(this);
        this.loggedInViewModel.addPropertyChangeListener(this);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        final JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));

        final JLabel searchLabel = new JLabel("Search for User: ");
        searchInputField = new JTextField(20);

        // Listen for when this view becomes active
        this.viewManagerModel.addPropertyChangeListener(evt -> {
            if ("state".equals(evt.getPropertyName())) {
                final String newViewName = (String) evt.getNewValue();
                final boolean wasActive = viewIsActive;
                viewIsActive = viewName.equals(newViewName);

                // If view just became active, refresh the search
                if (viewIsActive && !wasActive) {
                    final String currentUsername = loggedInViewModel.getState().getUsername();
                    if (currentUsername != null) {
                        findUsers(currentUsername, searchInputField.getText());
                    }
                }
            }
        });

        // Exit/Cancel Button
        searchExitButton = new JButton("✕");
        searchExitButton.setFont(new Font("Oxygen", Font.BOLD, 16));
        searchExitButton.setFocusPainted(false);
        searchExitButton.setBorderPainted(false);
        searchExitButton.setContentAreaFilled(false);
        searchExitButton.addActionListener(this);

        searchPanel.add(searchLabel);
        searchPanel.add(searchInputField);
        searchPanel.add(Box.createHorizontalStrut(5));
        searchPanel.add(searchExitButton);

        searchPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, searchPanel.getPreferredSize().height));

        // User List Components
        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        userList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        final JScrollPane scrollPane = new JScrollPane(userList);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Selection label
        selectionLabel = new JLabel("0 users selected");
        selectionLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        selectionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Hint label
        final JLabel hintLabel = new JLabel("Tip: Hold Ctrl to select multiple users for group chat");
        hintLabel.setFont(new Font("SansSerif", Font.ITALIC, 10));
        hintLabel.setForeground(Color.GRAY);
        hintLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Create button BEFORE adding listener
        startChatButton = new JButton("Start Chat (99)");
        final Dimension buttonSize = startChatButton.getPreferredSize();
        // Reset to default text
        startChatButton.setText("Start Chat");
        // Lock the size
        startChatButton.setPreferredSize(buttonSize);
        startChatButton.setMinimumSize(buttonSize);
        startChatButton.setMaximumSize(buttonSize);

        // Add selection listener for visual feedback
        userList.addListSelectionListener(evnt -> {
            if (!evnt.getValueIsAdjusting()) {
                final int count = userList.getSelectedValuesList().size();
                selectionLabel.setText(count + (count == 1 ? " user" : " users") + " selected");

                // Update button text dynamically
                if (count == 1) {
                    startChatButton.setText("Start Chat");
                }
                else if (count > 1) {
                    startChatButton.setText("Create Group Chat (" + count + ")");
                }
                else {
                    startChatButton.setText("Start Chat");
                }
            }
        });

        final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        startChatButton.addActionListener(this);
        buttonPanel.add(startChatButton);

        // Assembly
        this.add(searchPanel);
        this.add(Box.createVerticalStrut(10));
        this.add(hintLabel);
        this.add(Box.createVerticalStrut(5));
        this.add(selectionLabel);
        this.add(Box.createVerticalStrut(5));
        this.add(scrollPane);
        this.add(buttonPanel);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource().equals(searchExitButton)) {
            // Clear the list when exiting
            userListModel.clear();
            searchInputField.setText("");
            // Exit button to return to the home screen
            viewManagerModel.setState("logged in");
            viewManagerModel.firePropertyChange();
        }
        else if (evt.getSource().equals(startChatButton)) {
            final List<String> selectedUsernames = userList.getSelectedValuesList();

            if (selectedUsernames.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please select at least one user",
                        "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Check if any selected user is an error message
            for (String user : selectedUsernames) {
                if (user.startsWith("Error:") || "No users found.".equals(user)) {
                    JOptionPane.showMessageDialog(this, "Please select a valid user to start a chat.");
                    return;
                }
            }

            if (selectedUsernames.size() == 1) {
                // Individual chat
                startIndividualChat(selectedUsernames);
            }
            else {
                // Group chat
                startGroupChat(selectedUsernames);
            }
        }
    }

    public void setCreateChatController(CreateChatController controller) {
        this.createChatController = controller;
    }

    public void setCreateGroupChatController(CreateGroupChatController controller) {
        this.createGroupChatController = controller;
    }

    // Setter for ChatView
    public void setChatView(ChatView chatView) {
        this.chatView = chatView;
    }

    private void startIndividualChat(List<String> selectedUsernames) {
        // Get current logged-in user from session
        final String currentUsername = loggedInViewModel.getState().getUsername();

        // Find the target user
        if (createChatController != null) {
            createChatController.execute(currentUsername, selectedUsernames, "");
        }
        else {
            JOptionPane.showMessageDialog(this,
                    "Chat feature not initialized",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void startGroupChat(List<String> usernames) {
        // Prompt user for group name
        final String groupName = JOptionPane.showInputDialog(this,
                "Enter a name for the group chat:",
                "Create Group Chat",
                JOptionPane.PLAIN_MESSAGE);

        if (groupName == null) {
            System.out.println("Group chat creation cancelled by user");
            return;
        }

        if (groupName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Group name cannot be empty",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (createGroupChatController != null) {
            // Get current username
            final String currentUsername = loggedInViewModel.getState().getUsername();

            createGroupChatController.execute(currentUsername, usernames, groupName.trim());
        }
        else {
            JOptionPane.showMessageDialog(this,
                    "Group chat feature not initialized",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("state".equals(evt.getPropertyName())) {
            final Object newValue = evt.getNewValue();

            // Check if it's SearchUserState
            if (newValue instanceof SearchUserState) {
                final SearchUserState state = (SearchUserState) newValue;

                // Always clear the list before updating
                userListModel.clear();

                if (state.getSearchError() != null) {
                    userListModel.addElement("Error: " + state.getSearchError());
                }
                else if (state.getCreateError() != null) {
                    // Show error message if group chat creation failed
                    JOptionPane.showMessageDialog(this,
                            state.getCreateError(),
                            "Error Creating Chat",
                            JOptionPane.ERROR_MESSAGE);
                    findUsers(loggedInViewModel.getState().getUsername(), "");
                }
                else if (state.getSearchResults() != null) {
                    boolean usersAdded = false;

                    for (String username : state.getSearchResults()) {
                        // Get current user
                        final String currentUser1 = loggedInViewModel.getState().getUsername();

                        // This check prevents the current user from being added to the list
                        if (!username.equals(currentUser1)) {
                            userListModel.addElement(username);
                            usersAdded = true;
                        }
                    }

                    // This condition now correctly checks the results from the data access layer
                    // AND checks if anything was actually added to the displayed list.
                    if (state.getSearchResults().isEmpty() || !usersAdded) {
                        userListModel.addElement("No users found.");
                    }
                }
            }
            else if (newValue instanceof LoggedInState && !this.started) {
                this.started = true;
                findUsers(loggedInViewModel.getState().getUsername(), "");
            }
        }
    }

    public String getViewName() {
        return viewName;
    }

    /**
     * Sets the controller for the search user use case.
     * @param searchUserController the controller.
     */
    public void setUserSearchController(SearchUserController searchUserController) {
        this.searchUserController = searchUserController;
        searchInputField.addActionListener(e -> {
            final String currentUsername = loggedInViewModel.getState().getUsername();
            findUsers(currentUsername, searchInputField.getText());
        });
    }

    /**
     * Starts the search user use case by calling on the controller.
     * @param username the current username
     * @param query the query to search for
     */
    public void findUsers(String username, String query) {
        if (this.searchUserController != null) {
            this.searchUserController.execute(username, query);
        }
    }
}
