package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.messaging.send_m.ChatViewModel;
import interface_adapter.messaging.send_m.SendMessageController;
import interface_adapter.messaging.send_m.ChatState;
import interface_adapter.messaging.view_history.ViewChatHistoryController;

import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ChatView extends JPanel implements ActionListener, PropertyChangeListener {

    public final String viewName = "chat";
    private final ViewManagerModel viewManagerModel;
    private final ChatViewModel chatViewModel;
    private final LoggedInViewModel loggedInViewModel;
    private SendMessageController sendMessageController;
    private ViewChatHistoryController viewChatHistoryController;
    private ChatSettingView chatSettingView;

    private String currentChatId;
    private String currentUserId;
    private boolean isGroupChat;

    // Components
    private final JLabel chatPartnerLabel; // Displays the name of the user you're chatting with
    private final JLabel replyingToLabel;
    private final JTextArea messageInputField;
    private final JButton sendButton;
    private final JButton settingButton;

    private final JPanel replyPreviewBox;         // NEW
    private final JLabel replyPreviewText;        // NEW
    private final JButton cancelReplyButton;      // NEW
    private String replyingToMessageId = null;    // NEW

    // Use this to display the initial prompt or history
    private final JPanel chatDisplayPanel;
    private final JLabel initialPrompt;

    public ChatView(ViewManagerModel viewManagerModel,
                    ChatViewModel chatViewModel,
                    LoggedInViewModel loggedInViewModel) {

        this.viewManagerModel = viewManagerModel;
        this.chatViewModel = chatViewModel;
        this.loggedInViewModel = loggedInViewModel;
        this.chatViewModel.addPropertyChangeListener(this);
        this.setLayout(new BorderLayout());

        // Top Bar (Chat Partner and Exit/Back Button)
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel partnerInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel partnerIcon = new JLabel("👤");
        partnerIcon.setFont(new Font("SansSerif", Font.PLAIN, 24));
        chatPartnerLabel = new JLabel(this.chatViewModel.getState().getGroupName());
        chatPartnerLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        JButton backButton = new JButton("⬅");
        backButton.setFont(new Font("SansSerif", Font.BOLD, 20));

        backButton.addActionListener(e -> {
            viewManagerModel.setState("logged in");
            viewManagerModel.firePropertyChange();
        });

        partnerInfoPanel.add(backButton);
        partnerInfoPanel.add(partnerIcon);
        partnerInfoPanel.add(chatPartnerLabel);
        topBar.add(partnerInfoPanel, BorderLayout.WEST);

        settingButton = new JButton("⛭");
        settingButton.setFont(new Font("SansSerif", Font.BOLD, 20));
        settingButton.addActionListener(e -> {
            if (currentChatId == null) {
                JOptionPane.showMessageDialog(this,
                        "Chat is still loading. Please wait a moment and try again.",
                        "Please Wait",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (chatSettingView != null) {
                chatSettingView.setChatId(currentChatId);
            }
            viewManagerModel.setState("chat setting");
            viewManagerModel.firePropertyChange();
        });
        topBar.add(settingButton, BorderLayout.EAST);

        chatDisplayPanel = new JPanel();
        chatDisplayPanel.setLayout(new BoxLayout(chatDisplayPanel, BoxLayout.Y_AXIS));

        initialPrompt = new JLabel("<html><div style='text-align: center;'>Send \"" +
                chatPartnerLabel.getText() +
                "\" a message to start a chat!</div></html>");
        initialPrompt.setFont(new Font("SansSerif", Font.ITALIC, 16));
        chatDisplayPanel.add(initialPrompt);

        JScrollPane chatScrollPane = new JScrollPane(chatDisplayPanel);
        chatScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        chatScrollPane.setBorder(BorderFactory.createEmptyBorder());

        // -------------------------------
        // Reply Preview Box
        // -------------------------------
        replyPreviewBox = new JPanel(new BorderLayout());
        replyPreviewBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        replyPreviewBox.setBackground(new Color(245, 245, 245));
        replyPreviewBox.setVisible(false);

        replyPreviewText = new JLabel("Replying to: ");
        cancelReplyButton = new JButton("✕");
        cancelReplyButton.setFocusable(false);
        cancelReplyButton.setBorderPainted(false);
        cancelReplyButton.setContentAreaFilled(false);
        cancelReplyButton.addActionListener(e -> clearReplyPreview());
        replyPreviewBox.add(replyPreviewText, BorderLayout.WEST);
        replyPreviewBox.add(cancelReplyButton, BorderLayout.EAST);

        replyingToLabel = new JLabel("Replying to:");
        replyingToLabel.setVisible(false);

        messageInputField = new JTextArea(1, 1);
        messageInputField.setLineWrap(true);
        messageInputField.setWrapStyleWord(true);
        JScrollPane inputScrollPane = new JScrollPane(messageInputField);

        sendButton = new JButton("Send");
        sendButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        sendButton.setPreferredSize(new Dimension(80, inputScrollPane.getPreferredSize().height));
        sendButton.addActionListener(this);

        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        inputPanel.add(replyPreviewBox, BorderLayout.NORTH);
        inputPanel.add(inputScrollPane, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        this.add(topBar, BorderLayout.NORTH);
        this.add(chatScrollPane, BorderLayout.CENTER);
        this.add(inputPanel, BorderLayout.SOUTH);
    }

    private void clearReplyPreview() {
        replyingToMessageId = null;
        replyPreviewBox.setVisible(false);
        replyPreviewText.setText("");
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource().equals(sendButton)) {
            String message = messageInputField.getText().trim();
            if (!message.isEmpty()) {
                sendMessageController.execute(
                        currentChatId,
                        currentUserId,
                        replyingToMessageId,
                        message
                );
                messageInputField.setText("");
                clearReplyPreview();
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (!"state".equals(evt.getPropertyName())) return;
        if (!(evt.getNewValue() instanceof ChatState)) return;

        ChatState state = (ChatState) evt.getNewValue();

        if (!state.getFirst() && state.getChatId() != null && state.getGroupName() != null) {
            state.chatViewStart();

            // Determine if it's a group chat based on number of participants
            boolean isGroup = state.getParticipants().size() > 2;

            // Set the chat context
            setChatContext(
                    state.getChatId(),
                    state.getParticipants(),
                    state.getMessageIds(),
                    loggedInViewModel.getState().getUsername(),
                    state.getGroupName(),
                    isGroup
            );
            return;
        }

        // remove previous ui
        chatDisplayPanel.removeAll();

        if (state.getError() != null) {
            JLabel errorLabel = new JLabel(state.getError());
            errorLabel.setForeground(Color.RED);
            chatDisplayPanel.add(errorLabel);
        }
        else {

            // Array index order: [messageId, senderUserId, messageContent, messageTimestamp]
            List<String[]> messages = state.getMessages();

            if (messages.isEmpty()) {
                chatDisplayPanel.add(initialPrompt);
            } else {
                for (String[] msg : messages) {

                    boolean fromCurrentUser = msg[1].equals(currentUserId);
                    String messageId = msg[0];
                    String content = msg[2];
                    String timestamp = msg[3];

                    String repliedMessageId = msg.length > 4 ? msg[4] : null;
                    String repliedPreview = null;

                    if (repliedMessageId != null && !repliedMessageId.isEmpty()) {
                        for (String[] m : messages) {
                            if (m[0].equals(repliedMessageId)) {
                                String original = m[2];
                                repliedPreview = original.length() > 25 ?
                                        original.substring(0, 25) + "…" : original;
                                break;
                            }
                        }
                    }

                    JPanel row = new JPanel();
                    row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
                    row.setOpaque(false);
                    row.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

                    int viewportWidth = chatDisplayPanel.getParent().getWidth();
                    int maxBubbleWidth = (int)(viewportWidth * 0.66);

                    // Build wrapped bubble
                    JPanel bubble = createWrappedBubble(content,timestamp, repliedPreview, fromCurrentUser, maxBubbleWidth);

                    JButton actionButton = new JButton("⋯");
                    actionButton.setFocusable(false);
                    actionButton.setPreferredSize(new Dimension(28, 20));
                    actionButton.setMargin(new Insets(0, 4, 0, 4));

                    JPopupMenu menu = buildPopupMenu(fromCurrentUser, messageId, content);
                    actionButton.addActionListener(e -> menu.show(actionButton, 0, actionButton.getHeight()));

                    bubble.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            replyingToMessageId = messageId;

                            String preview = content.length() > 20 ?
                                    content.substring(0, 20) + "…" : content;

                            replyPreviewText.setText("Replying to: " + preview);
                            replyPreviewBox.setVisible(true);
                        }
                    });

                    if (fromCurrentUser) {
                        row.add(Box.createHorizontalGlue());
                        row.add(bubble);
                        row.add(Box.createHorizontalStrut(4));
                        row.add(actionButton);
                    } else {
                        row.add(actionButton);
                        row.add(Box.createHorizontalStrut(4));
                        row.add(bubble);
                        row.add(Box.createHorizontalGlue());
                    }

                    chatDisplayPanel.add(row);
                }
            }
        }

        chatDisplayPanel.revalidate();
        chatDisplayPanel.repaint();
    }

    // -------------------------------
    // Popup Menu Builder
    // -------------------------------
    private JPopupMenu buildPopupMenu(boolean fromCurrentUser, String messageId, String content) {
        JPopupMenu menu = new JPopupMenu();

        if (fromCurrentUser) {
            JMenuItem deleteItem = new JMenuItem("Delete");
            deleteItem.addActionListener(e -> JOptionPane.showMessageDialog(this,
                    "Delete feature coming soon."));
            menu.add(deleteItem);

            JMenuItem replyItem = new JMenuItem("Reply");
            replyItem.addActionListener(e -> {
                replyingToMessageId = messageId;
                String shortText = content.length() > 20 ? content.substring(0, 20) + "…" : content;
                replyPreviewText.setText("Replying to: " + shortText);
                replyPreviewBox.setVisible(true);
            });
            menu.add(replyItem);
        }
        else {
            JMenuItem replyItem = new JMenuItem("Reply");
            replyItem.addActionListener(e -> {
                replyingToMessageId = messageId;
                String shortText = content.length() > 20 ? content.substring(0, 20) + "…" : content;
                replyPreviewText.setText("Replying to: " + shortText);
                replyPreviewBox.setVisible(true);
            });
            menu.add(replyItem);

            JMenuItem reactItem = new JMenuItem("React");
            reactItem.addActionListener(e -> JOptionPane.showMessageDialog(this,
                    "React feature coming soon."));
            menu.add(reactItem);

            JMenuItem clearReaction = new JMenuItem("Clear Reaction");
            clearReaction.addActionListener(e -> JOptionPane.showMessageDialog(this,
                    "Clear Reaction feature coming soon."));
            menu.add(clearReaction);
        }

        menu.addSeparator();
        JMenuItem cancel = new JMenuItem("Cancel");
        menu.add(cancel);

        return menu;
    }

    public String getViewName() { return viewName; }

    /**
     * Public method to set the chat partner's username and update the view.
     * @param username The username of the chat partner.
     */
    public void setChatPartner(String username) {
        this.chatPartnerLabel.setText(username);
        this.initialPrompt.setText("<html><div style='text-align: center;'>Send \"" +
                username +
                "\" a message to start a chat!</div></div>");
        this.revalidate();
        this.repaint();
    }

    public void setChatContext(String chatId, List<String> userIds, List<String> messageIds,
                               String currentUserId, String groupName, boolean isGroupChat) {
        this.currentChatId = chatId;
        this.currentUserId = currentUserId;
        this.isGroupChat = isGroupChat;
        setChatPartner(groupName);
        settingButton.setVisible(isGroupChat);
        viewChatHistoryController.execute(chatId, userIds, messageIds);
    }

    public void setChatId(String chatId) { this.currentChatId = chatId; }

    public void setSendMessageController(SendMessageController sendMessageController) {
        this.sendMessageController = sendMessageController;
    }

    public void setViewChatHistoryController(ViewChatHistoryController viewChatHistoryController) {
        this.viewChatHistoryController = viewChatHistoryController;
    }

    // --------------------------------------------------------
    // PERFECT WRAPPED BUBBLE (this is the fixed version)
    // --------------------------------------------------------
    private JPanel createWrappedBubble(String text, String time, String repliedPreview,
                                       boolean fromCurrentUser, int maxWidth) {

        // Container panel
        JPanel bubble = new JPanel();
        bubble.setLayout(new BoxLayout(bubble, BoxLayout.Y_AXIS));
        bubble.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        bubble.setOpaque(true);
        bubble.setBackground(fromCurrentUser ? new Color(0x95EC69) : new Color(230, 230, 230));

        // Reply header (if exists)
        if (repliedPreview != null) {
            JLabel replyHeader = new JLabel("↪ " + repliedPreview);
            replyHeader.setFont(new Font("SansSerif", Font.PLAIN, 11));
            replyHeader.setForeground(new Color(100, 100, 100));
            replyHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
            bubble.add(replyHeader);
        }

        // Main text label
        JLabel label = new JLabel("<html>" + text + "</html>");
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Wrap width calculation
        label.setSize(new Dimension(maxWidth, Integer.MAX_VALUE));
        Dimension preferred = label.getPreferredSize();

        label.setMaximumSize(new Dimension(maxWidth, preferred.height + 10));
        bubble.setMaximumSize(new Dimension(maxWidth + 20, preferred.height + 30));

        bubble.add(label);

        JLabel timeLabel = new JLabel(time);
        timeLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
        timeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Color depends on who sent the message
        if (fromCurrentUser) {
            timeLabel.setForeground(new Color(80,80,80));   // dark gray
        } else {
            timeLabel.setForeground(new Color(50,50,50));   // darker gray
        }

        bubble.add(Box.createVerticalStrut(3));
        bubble.add(timeLabel);


        return bubble;
    }
    public void setChatSettingView(ChatSettingView chatSettingView) {
        this.chatSettingView = chatSettingView;
    }
}
