package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.messaging.send_m.SendMessageController;
import interface_adapter.messaging.send_m.ChatState;
import interface_adapter.messaging.view_history.ViewChatHistoryController;
import use_case.messaging.ChatMessageDto;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ChatView extends JPanel implements ActionListener, PropertyChangeListener {

    public final String viewName = "chat";
    private final ViewManagerModel viewManagerModel;
    private final SendMessageController sendMessageController;
    private final ViewChatHistoryController viewChatHistoryController;

    private String currentChatId;
    private String currentUserId;


    // Components
    private final JLabel chatPartnerLabel; // Displays the name of the user you're chatting with
    private final JTextArea messageInputField;
    private final JButton sendButton;

    // Use this to display the initial prompt or history
    private final JPanel chatDisplayPanel;
    private final JLabel initialPrompt;

    public ChatView(ViewManagerModel viewManagerModel, SendMessageController sendMessageController,
                    interface_adapter.messaging.view_history.ViewChatHistoryController viewChatHistoryController) {
        this.viewManagerModel = viewManagerModel;
        this.sendMessageController = sendMessageController;
        this.viewChatHistoryController = viewChatHistoryController;
        this.setLayout(new BorderLayout());

        // Top Bar (Chat Partner and Exit/Back Button)
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Left Side: Chat Partner Name (Placeholder)
        JPanel partnerInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        // Using the Profile icon again, but this time for the chat partner
        JLabel partnerIcon = new JLabel("👤");
        partnerIcon.setFont(new Font("SansSerif", Font.PLAIN, 24));
        chatPartnerLabel = new JLabel("User"); // Placeholder
        chatPartnerLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        // Right Side: Back/Exit Button (Go back to LoggedInView/Recent Chats)
        JButton backButton = new JButton("⬅");
        backButton.setFont(new Font("SansSerif", Font.BOLD, 20));

        backButton.addActionListener(e -> {
            // Navigate back to the LoggedInView
            viewManagerModel.setState("logged in");
            viewManagerModel.firePropertyChange();
        });

        partnerInfoPanel.add(backButton);
        partnerInfoPanel.add(partnerIcon);
        partnerInfoPanel.add(chatPartnerLabel);
        topBar.add(partnerInfoPanel, BorderLayout.WEST);

        // Right Side: Back/Exit Button (Go back to LoggedInView/Recent Chats)
        JButton settingButton = new JButton("⛭");
        settingButton.setFont(new Font("SansSerif", Font.BOLD, 20));
        settingButton.setFocusPainted(false);
        settingButton.setBorderPainted(false);
        settingButton.setContentAreaFilled(false);

        settingButton.addActionListener(e -> {
            // Navigate to chat setting view (need to make)
            System.out.println("clicked chat setting button");
        });

        topBar.add(settingButton, BorderLayout.EAST);

        // Chat Display Area (Main Content)
        chatDisplayPanel = new JPanel();
        // change to y-axis display frame
        chatDisplayPanel.setLayout(new BoxLayout(chatDisplayPanel, BoxLayout.Y_AXIS));

        // Initial Prompt: "Send 'User' a message to start a chat!"
        initialPrompt = new JLabel("<html><div style='text-align: center;'>Send \"" + chatPartnerLabel.getText() +
                "\" a message to start a chat!</div></html>");
        initialPrompt.setFont(new Font("SansSerif", Font.ITALIC, 16));

        chatDisplayPanel.add(initialPrompt);

        JScrollPane chatScrollPane = new JScrollPane(chatDisplayPanel);
        chatScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        chatScrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Input Panel (Text field and Send Button)
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10)); // Padding

        messageInputField = new JTextArea(3, 1);
        messageInputField.setLineWrap(true);
        messageInputField.setWrapStyleWord(true);
        JScrollPane inputScrollPane = new JScrollPane(messageInputField);

        sendButton = new JButton("Send");
        sendButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        sendButton.setPreferredSize(new Dimension(80, inputScrollPane.getPreferredSize().height));

        sendButton.addActionListener(this);

        inputPanel.add(inputScrollPane, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        // Assembly
        this.add(topBar, BorderLayout.NORTH);
        this.add(chatScrollPane, BorderLayout.CENTER);
        this.add(inputPanel, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource().equals(sendButton)) {
            String message = messageInputField.getText().trim();
            if (!message.isEmpty()) {
                sendMessageController.execute(currentChatId, currentUserId, message);
                messageInputField.setText("");
            }
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

        ChatState state = (ChatState) newValue;

        // remove previous ui
        chatDisplayPanel.removeAll();

        if (state.getError() != null) {
            JLabel errorLabel = new JLabel(state.getError());
            errorLabel.setForeground(Color.RED);
            chatDisplayPanel.add(errorLabel);
        } else {
            List<ChatMessageDto> messages = state.getMessages();

            if (messages.isEmpty()) {
                chatDisplayPanel.add(initialPrompt);
            } else {
                for (ChatMessageDto msg : messages) {
                    boolean fromCurrentUser =
                            msg.getSenderUserId().equals(currentUserId);

                    JPanel row = new JPanel(new BorderLayout());
                    row.setOpaque(false);
                    row.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

                    JLabel bubble = new JLabel(msg.getContent());
                    bubble.setOpaque(true);
                    bubble.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

                    if (fromCurrentUser) {
                        bubble.setBackground(new Color(0x95EC69));
                        // green
                        bubble.setForeground(Color.BLACK);
                        row.add(bubble, BorderLayout.EAST);
                    } else {
                        bubble.setBackground(new Color(230, 230, 230));
                        // black
                        bubble.setForeground(Color.BLACK);
                        row.add(bubble, BorderLayout.WEST);
                    }
                    chatDisplayPanel.add(row);
                }
            }
        }
        chatDisplayPanel.revalidate();
        chatDisplayPanel.repaint();
    }

    public String getViewName() {
        return viewName;
    }

    /**
     * Public method to set the chat partner's username and update the view.
     * @param username The username of the chat partner.
     */
    public void setChatPartner(String username) {
        this.chatPartnerLabel.setText(username);
        // Update the initial prompt to reflect the new user
        this.initialPrompt.setText("<html><div style='text-align: center;'>Send \"" + username +
                "\" a message to start a chat!</div></div>");
        this.revalidate();
        this.repaint();
    }

    public void setChatContext(String chatId, String currentUserId, String partnerUsername) {
        this.currentChatId = chatId;
        this.currentUserId = currentUserId;
        setChatPartner(partnerUsername);
        viewChatHistoryController.execute(chatId);
    }
}