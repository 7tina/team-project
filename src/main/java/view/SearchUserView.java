package view;

import interface_adapter.ViewManagerModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import interface_adapter.groupchat.*;
import interface_adapter.logged_in.LoggedInState;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.search_user.SearchUserController;
import interface_adapter.search_user.SearchUserViewModel;
import interface_adapter.search_user.SearchUserState;
import interface_adapter.create_chat.CreateChatController;

import java.util.List;

public class SearchUserView extends JPanel implements ActionListener, PropertyChangeListener {

    public final String viewName = "new chat";

    private SearchUserController searchUserController;
    private final ViewManagerModel viewManagerModel;
    private final SearchUserViewModel searchUserViewModel;
    private final GroupChatViewModel groupChatViewModel;
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


    public SearchUserView(ViewManagerModel viewManagerModel, SearchUserViewModel searchUserViewModel,
                          GroupChatViewModel groupChatViewModel,
                          LoggedInViewModel loggedInViewModel) {

        this.viewManagerModel = viewManagerModel;
        this.searchUserViewModel = searchUserViewModel;
        this.groupChatViewModel = groupChatViewModel;
        this.loggedInViewModel = loggedInViewModel;
        this.searchUserViewModel.addPropertyChangeListener(this);
        this.groupChatViewModel.addPropertyChangeListener(this);
        this.loggedInViewModel.addPropertyChangeListener(this);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));

        JLabel searchLabel = new JLabel("Search for User: ");
        searchInputField = new JTextField(20);

        // Exit/Cancel Button
        searchExitButton = new JButton("❌");
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

        JScrollPane scrollPane = new JScrollPane(userList);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Selection label
        selectionLabel = new JLabel("0 users selected");
        selectionLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        selectionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Hint label
        JLabel hintLabel = new JLabel("Tip: Hold Ctrl to select multiple users for group chat");
        hintLabel.setFont(new Font("SansSerif", Font.ITALIC, 10));
        hintLabel.setForeground(Color.GRAY);
        hintLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Create button BEFORE adding listener
        startChatButton = new JButton("Start Chat");

        // Add selection listener for visual feedback
        userList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int count = userList.getSelectedValuesList().size();
                selectionLabel.setText(count + (count == 1 ? " user" : " users") + " selected");

                // Update button text dynamically
                if (count == 1) {
                    startChatButton.setText("Start Chat");
                } else if (count > 1) {
                    startChatButton.setText("Create Group Chat (" + count + ")");
                } else {
                    startChatButton.setText("Start Chat");
                }
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
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
            this.started = false;
            // Exit button to return to the home screen
            viewManagerModel.setState("logged in");
            viewManagerModel.firePropertyChange();
        } else if (evt.getSource().equals(startChatButton)) {
            List<String> selectedUsernames = userList.getSelectedValuesList();

            if (selectedUsernames.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please select at least one user",
                        "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Check if any selected user is an error message
            for (String user : selectedUsernames) {
                if (user.startsWith("Error:") || user.equals("No users found.")) {
                    JOptionPane.showMessageDialog(this, "Please select a valid user to start a chat.");
                    return;
                }
            }

            if (selectedUsernames.size() == 1) {
                // Individual chat
                startIndividualChat(selectedUsernames.get(0));
            } else {
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

    private void startIndividualChat(String username) {
        // Get current logged-in user from session
        String currentUsername = loggedInViewModel.getState().getUsername();

        // Find the target user
        if (createChatController != null) {
            createChatController.execute(currentUsername, username);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Chat feature not initialized",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void startGroupChat(List<String> usernames) {
        // Prompt user for group name
        String groupName = JOptionPane.showInputDialog(this,
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
            String currentUsername = loggedInViewModel.getState().getUsername();

            createGroupChatController.execute(currentUsername, usernames, groupName.trim());
        } else {
            JOptionPane.showMessageDialog(this,
                    "Group chat feature not initialized",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String currentUser = loggedInViewModel.getState().getUsername();

        if ("state".equals(evt.getPropertyName())) {
            Object newValue = evt.getNewValue();

            // Check if it's SearchUserState
            if (newValue instanceof SearchUserState) {
                SearchUserState state = (SearchUserState) newValue;
                userListModel.clear();

                if (state.getSearchError() != null) {
                    userListModel.addElement("Error: " + state.getSearchError());
                }
                else if (state.getSearchResults() != null) {
                    for (String username : state.getSearchResults()) {
                        if (!username.equals(currentUser)) {
                            userListModel.addElement(username);
                        }
                    }
                    if (state.getSearchResults().isEmpty()) {
                        userListModel.addElement("No users found.");
                    }
                }
            }

            else if (newValue instanceof LoggedInState && !this.started) {
                this.started = true;
                findUsers(loggedInViewModel.getState().getUsername(), "");
            }

            // Check if it's GroupChatState
            else if (newValue instanceof GroupChatState) {
                GroupChatState chatState = (GroupChatState) newValue;

                // Check if the group chat was created successfully
                if (chatState.isSuccess()) {

                    // Check if chatView is available
                    if (chatView != null) {
                        // Get current username
                        String currentUsername = loggedInViewModel.getState().getUsername();

                        // ADD THIS NULL CHECK:
                        String groupName = chatState.getGroupName();
                        if (groupName == null || groupName.isEmpty()) {
                            System.err.println("ERROR: groupName is null or empty!");
                            JOptionPane.showMessageDialog(this,
                                    "Group name is missing. Please try again.",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                            return;  // Don't navigate if group name is missing
                        }

                        // Call setChatContext with the correct signature matching ChatView
                        chatView.setChatContext(
                                chatState.getChatId(),           // chatId
                                chatState.getParticipants(),     // List<String> userIds (participants)
                                chatState.getMessageIds(),       // List<String> messageIds
                                currentUsername,                 // currentUserId (using username)
                                groupName,                       // groupName - NOW VERIFIED NOT NULL
                                true                             // isGroupChat = true
                        );

                        viewManagerModel.setState("chat");
                        viewManagerModel.firePropertyChange();
                    } else {
                        System.err.println("ERROR: chatView is null!");
                        JOptionPane.showMessageDialog(this,
                                "Chat view not initialized",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
                else if (chatState.getError() != null) {
                    // Show error message if group chat creation failed
                    JOptionPane.showMessageDialog(this,
                            chatState.getError(),
                            "Error Creating Group Chat",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    public String getViewName() {
        return viewName;
    }

    public void setUserSearchController(SearchUserController searchUserController) {
        this.searchUserController = searchUserController;
        searchInputField.addActionListener(e -> {
            String currentUsername = loggedInViewModel.getState().getUsername();
            findUsers(currentUsername, searchInputField.getText());
        });
    }

    public void findUsers(String username, String query) {
        if (this.searchUserController != null) {
            this.searchUserController.execute(username, query);
        }
    }
}