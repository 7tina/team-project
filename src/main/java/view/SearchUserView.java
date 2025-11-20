package view;

import interface_adapter.ViewManagerModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import interface_adapter.groupchat.CreateGroupChatController;
import interface_adapter.groupchat.GroupChatState;
import interface_adapter.groupchat.GroupChatViewModel;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.user_search.SearchUserController;
import interface_adapter.user_search.SearchUserViewModel;
import interface_adapter.user_search.SearchUserState;
import entity.ports.ChatRepository;
import entity.ports.UserRepository;
import entity.Chat;
import entity.User;
import java.util.Optional;
import java.util.UUID;

import java.util.List;

public class SearchUserView extends JPanel implements ActionListener, PropertyChangeListener {

    public final String viewName = "new chat";

    private final ViewManagerModel viewManagerModel;
    private final SearchUserViewModel searchUserViewModel;
    private final GroupChatViewModel groupChatViewModel;  // Fixed: added semicolon
    private final LoggedInViewModel loggedInViewModel;
    private final ChatView chatView;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    private SearchUserController searchUserController;
    private CreateGroupChatController createGroupChatController;

    // UI Components
    private final JTextField searchInputField;
    private final JButton searchExitButton;
    private final JButton startChatButton;
    private JLabel selectionLabel;

    // JList and its model for displaying users
    private final JList<String> userList;
    private final DefaultListModel<String> userListModel;


    public SearchUserView(ViewManagerModel viewManagerModel, SearchUserViewModel searchUserViewModel,
                          ChatView chatView, GroupChatViewModel groupChatViewModel,
                          ChatRepository chatRepository, UserRepository userRepository,
                          LoggedInViewModel loggedInViewModel) {

        this.viewManagerModel = viewManagerModel;
        this.searchUserViewModel = searchUserViewModel;
        this.groupChatViewModel = groupChatViewModel;
        this.chatView = chatView;
        this.searchUserViewModel.addPropertyChangeListener(this);
        this.groupChatViewModel.addPropertyChangeListener(this);
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.loggedInViewModel = loggedInViewModel;

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

    private void startIndividualChat(String username) {
        // Get current logged-in user from session
        String currentUsername = loggedInViewModel.getState().getUsername();
        Optional<User> currentUserOpt = userRepository.findByUsername(currentUsername);

        if (currentUserOpt.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Session error. Please log in again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String currentUserId = currentUserOpt.get().getName();

        // Find the target user
        Optional<User> targetUserOpt = userRepository.findByUsername(username);
        if (targetUserOpt.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "User not found: " + username,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String targetUserId = targetUserOpt.get().getName();

        // Find existing chat or create new one
        String chatId = findOrCreateChat(currentUserId, targetUserId);

        // Set the chat context with the unique chat ID
        if (chatView != null) {
            chatView.setChatContext(chatId, currentUserId, username, false);
        }

        // Navigate to chat view
        viewManagerModel.setState("chat");
        viewManagerModel.firePropertyChange();
    }

    private String findOrCreateChat(String userId1, String userId2) {
        // Find existing chat with both participants (and only these two)
        java.util.List<Chat> allChats = chatRepository.findAll();

        for (Chat chat : allChats) {
            java.util.List<String> participants = chat.getParticipantUserIds();  // Changed from getParticipants()
            if (participants.size() == 2 &&
                    participants.contains(userId1) &&
                    participants.contains(userId2)) {
                return chat.getId();
            }
        }

        // No existing chat found, create new one
        Chat newChat = new Chat(UUID.randomUUID().toString());
        newChat.addParticipant(userId1);
        newChat.addParticipant(userId2);
        Chat saved = chatRepository.save(newChat);
        return saved.getId();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("state".equals(evt.getPropertyName())) {
            Object newValue = evt.getNewValue();

            // Check if it's SearchUserState
            if (newValue instanceof SearchUserState) {
                SearchUserState state = (SearchUserState) newValue;
                userListModel.clear();

                if (state.getSearchError() != null) {
                    userListModel.addElement("Error: " + state.getSearchError());
                } else if (state.getSearchResults() != null) {
                    for (String username : state.getSearchResults()) {
                        userListModel.addElement(username);
                    }
                    if (state.getSearchResults().isEmpty()) {
                        userListModel.addElement("No users found.");
                    }
                }
            }
            // Check if it's GroupChatState
            else if (newValue instanceof GroupChatState) {
                GroupChatState chatState = (GroupChatState) newValue;

                // Check if the group chat was created successfully
                if (chatState.isSuccess()) {
                    // Navigate to chat view with the new group chat
                    chatView.setChatContext(
                            chatState.getChatId(),
                            "user-1",  // TODO: Get from session/logged in user
                            chatState.getGroupName(),
                            true  // isGroupChat = true
                    );

                    viewManagerModel.setState("chat");
                    viewManagerModel.firePropertyChange();
                } else if (chatState.getError() != null) {
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
            if (this.searchUserController != null) {
                this.searchUserController.execute(searchInputField.getText());
            }
        });

        // Trigger an initial search with an empty query to load all users on startup
        if (this.searchUserController != null) {
            this.searchUserController.execute("");
        }
    }

    public void setCreateGroupChatController(CreateGroupChatController controller) {
        this.createGroupChatController = controller;
    }

    private void startGroupChat(List<String> usernames) {
        // Prompt user for group name
        String groupName = JOptionPane.showInputDialog(this,
                "Enter a name for the group chat:",
                "Create Group Chat",
                JOptionPane.PLAIN_MESSAGE);

        if (groupName == null) {
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
            // Get current logged-in user from session
            String currentUsername = loggedInViewModel.getState().getUsername();
            Optional<User> currentUserOpt = userRepository.findByUsername(currentUsername);

            if (currentUserOpt.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Session error. Please log in again.");
                return;
            }

            String currentUserId = currentUserOpt.get().getName();

            createGroupChatController.execute(currentUserId, usernames, groupName.trim());
        } else {
            JOptionPane.showMessageDialog(this,
                    "Group chat feature not initialized",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}