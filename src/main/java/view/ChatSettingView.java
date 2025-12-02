package view;

import interfaceadapter.ViewManagerModel;
import interfaceadapter.groupchat.adduser.AddUserController;
import interfaceadapter.groupchat.changegroupname.ChangeGroupNameController;
import interfaceadapter.groupchat.removeuser.RemoveUserController;
import interfaceadapter.messaging.ChatViewModel;
import interfaceadapter.messaging.ChatState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ChatSettingView extends JPanel implements ActionListener, PropertyChangeListener {

    public static final String viewName = "chat setting";
    private final ViewManagerModel viewManagerModel;
    private final ChatViewModel chatViewModel;

    // Buttons
    private final JButton changeGroupNameButton;
    private final JButton addUserButton;
    private final JButton removeUserButton;

    private ChangeGroupNameController changeGroupNameController;
    private RemoveUserController removeUserController;
    private AddUserController addUserController;
    private String currentChatId;

    public ChatSettingView(ViewManagerModel viewManagerModel, ChatViewModel chatViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.chatViewModel = chatViewModel;
        this.chatViewModel.addPropertyChangeListener(this);

        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ===== Top Bar with Back Button =====
        JPanel topBar = new JPanel(new BorderLayout());

        JButton backButton = new JButton("⬅");
        backButton.setFont(new Font("SansSerif", Font.BOLD, 20));
        backButton.addActionListener(e -> {
            viewManagerModel.setState("chat");
            viewManagerModel.firePropertyChange();
        });
        topBar.add(backButton, BorderLayout.WEST);

        JLabel title = new JLabel("Chat Settings", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        topBar.add(title, BorderLayout.CENTER);

        this.add(topBar, BorderLayout.NORTH);

        // ===== Main Button Panel =====
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20));

        changeGroupNameButton = new JButton("Change Group Name");
        addUserButton = new JButton("Add User");
        removeUserButton = new JButton("Remove User");

        changeGroupNameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addUserButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        removeUserButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        changeGroupNameButton.addActionListener(this);
        addUserButton.addActionListener(this);
        removeUserButton.addActionListener(this);

        contentPanel.add(changeGroupNameButton);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(addUserButton);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(removeUserButton);

        this.add(contentPanel, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource().equals(changeGroupNameButton)) {
            if (changeGroupNameController == null) {
                JOptionPane.showMessageDialog(this,
                        "Controller not initialized",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (currentChatId == null) {
                JOptionPane.showMessageDialog(this,
                        "No chat selected",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            String newName = JOptionPane.showInputDialog(this,
                    "Enter new group name:",
                    "Change Group Name",
                    JOptionPane.PLAIN_MESSAGE);

            // Check if user cancelled
            if (newName == null) {
                return;
            }

            // Check if name is empty
            if (newName.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Group name cannot be empty",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            changeGroupNameController.execute(currentChatId, newName.trim());
        }
        else if (evt.getSource().equals(addUserButton)) {
            if (addUserController == null) {
                JOptionPane.showMessageDialog(this,
                        "Controller not initialized",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (currentChatId == null) {
                JOptionPane.showMessageDialog(this,
                        "No chat selected",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            String user = JOptionPane.showInputDialog(this,
                    "Enter username to add:",
                    "Add User",
                    JOptionPane.PLAIN_MESSAGE);

            // Check if user cancelled
            if (user == null) {
                return;
            }

            // Check if username is empty
            if (user.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Username cannot be empty",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            addUserController.execute(currentChatId, user.trim());
        }
        else if (evt.getSource().equals(removeUserButton)) {
            if (removeUserController == null) {
                JOptionPane.showMessageDialog(this,
                        "Controller not initialized",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (currentChatId == null) {
                JOptionPane.showMessageDialog(this,
                        "No chat selected",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            String user = JOptionPane.showInputDialog(this,
                    "Enter username to remove:",
                    "Remove User",
                    JOptionPane.PLAIN_MESSAGE);

            // Check if user cancelled
            if (user == null) {
                return;
            }

            // Check if username is empty
            if (user.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Username cannot be empty",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            removeUserController.execute(currentChatId, user.trim());
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (!"state".equals(evt.getPropertyName())) {
            return;
        }

        Object newValue = evt.getNewValue();
        if (!(newValue instanceof ChatState)) {
            return;
        }

        // Only react if this view is currently active
        if (!viewManagerModel.getState().equals(viewName)) {
            return;
        }

        ChatState state = (ChatState) newValue;

        if (state.getError() != null) {
            JOptionPane.showMessageDialog(this,
                    state.getError(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public String getViewName() {
        return viewName;
    }

    // Setter for the chat ID
    public void setChatId(String chatId) {
        this.currentChatId = chatId;
    }

    // Setter for the controller
    public void setChangeGroupNameController(ChangeGroupNameController controller) {
        this.changeGroupNameController = controller;
    }

    public void setRemoveUserController(RemoveUserController controller) {
        this.removeUserController = controller;
    }

    public void setAddUserController(AddUserController controller) {
        this.addUserController = controller;
    }
}