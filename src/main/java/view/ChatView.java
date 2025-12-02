package view;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.*;

import interfaceadapter.ViewManagerModel;
import interfaceadapter.logged_in.LoggedInViewModel;
import interfaceadapter.messaging.ChatState;
import interfaceadapter.messaging.ChatViewModel;
import interfaceadapter.messaging.delete_m.DeleteMessageController;
import interfaceadapter.messaging.search_history.SearchChatHistoryController;
import interfaceadapter.messaging.send_m.SendMessageController;
import interfaceadapter.messaging.view_history.ViewChatHistoryController;
import interfaceadapter.recent_chat.RecentChatsController;

public class ChatView extends JPanel implements ActionListener, PropertyChangeListener {

    private final String viewName = "chat";

    private final ViewManagerModel viewManagerModel;
    private final ChatViewModel chatViewModel;
    private final LoggedInViewModel loggedInViewModel;

    private SendMessageController sendMessageController;
    private ViewChatHistoryController viewChatHistoryController;
    private DeleteMessageController deleteMessageController;
    private SearchChatHistoryController searchChatHistoryController;
    private RecentChatsController recentChatsController;
    private ChatSettingView chatSettingView;

    private String currentChatId;
    private String currentUserId;
    private boolean isGroupChat;
    private boolean isDisplayingSearchResults = false;

    // Components
    private final JLabel chatPartnerLabel;
    private final JLabel replyingToLabel;
    private final JTextArea messageInputField;
    private final JButton sendButton;
    private final JButton backButton;
    private final JButton settingButton;
    private final JButton searchHistoryButton;
    private final JButton clearSearchButton;

    // Reply preview
    private final JPanel replyPreviewBox;
    private final JLabel replyPreviewText;
    private final JButton cancelReplyButton;
    private String replyingToMessageId = null;

    // Chat display
    private final JPanel chatDisplayPanel;
    private final JLabel initialPrompt;
    private final JScrollPane chatScrollPane;

    // Time refresher
    private Timer refreshTimer;
    private List<String> currentUserIds;
    private List<String> currentMessageIds;

    public ChatView(ViewManagerModel viewManagerModel,
                    ChatViewModel chatViewModel,
                    LoggedInViewModel loggedInViewModel) {

        this.viewManagerModel = viewManagerModel;
        this.chatViewModel = chatViewModel;
        this.loggedInViewModel = loggedInViewModel;

        this.chatViewModel.addPropertyChangeListener(this);
        this.setLayout(new BorderLayout());

        // ==========================
        // Top bar
        // ==========================
        final JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Left: back + icon + title
        final JPanel partnerInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        final JLabel partnerIcon = new JLabel("👤");
        partnerIcon.setFont(new Font("SansSerif", Font.PLAIN, 24));

        chatPartnerLabel = new JLabel(this.chatViewModel.getState().getGroupName());
        chatPartnerLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        backButton = new JButton("⬅");
        backButton.setFont(new Font("SansSerif", Font.BOLD, 20));
        backButton.addActionListener(evnt -> {
            if (refreshTimer != null) {
                refreshTimer.stop();
            }

            viewManagerModel.setState("logged in");
            viewManagerModel.firePropertyChange();
        });

        partnerInfoPanel.add(backButton);
        partnerInfoPanel.add(partnerIcon);
        partnerInfoPanel.add(chatPartnerLabel);
        topBar.add(partnerInfoPanel, BorderLayout.WEST);

        // Right: Search + Setting
        final JPanel rightButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        rightButtonPanel.setOpaque(false);

        searchHistoryButton = new JButton("Search");
        searchHistoryButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchHistoryButton.setFocusable(false);
        searchHistoryButton.addActionListener(evnt -> handleSearchHistory());

        settingButton = new JButton("⛭");
        settingButton.setFont(new Font("SansSerif", Font.BOLD, 20));
        settingButton.setFocusable(false);
        settingButton.addActionListener(evnt -> {
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

        clearSearchButton = new JButton("Clear");
        clearSearchButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        clearSearchButton.setFocusable(false);
        // Hidden by default
        clearSearchButton.setVisible(false);
        clearSearchButton.addActionListener(evnt -> {
            isDisplayingSearchResults = false;
            clearSearchButton.setVisible(false);
            // Refresh to show all messages again
            if (viewChatHistoryController != null) {
                viewChatHistoryController.execute(currentChatId, currentUserIds, currentMessageIds);
            }
        });

        rightButtonPanel.add(searchHistoryButton);
        rightButtonPanel.add(clearSearchButton);
        rightButtonPanel.add(settingButton);
        topBar.add(rightButtonPanel, BorderLayout.EAST);

        // ==========================
        // Chat display
        // ==========================
        chatDisplayPanel = new JPanel();
        chatDisplayPanel.setLayout(new BoxLayout(chatDisplayPanel, BoxLayout.Y_AXIS));

        initialPrompt = new JLabel("<html><div style='text-align: center;'>Send \""
                + chatPartnerLabel.getText()
                + "\" a message to start a chat!</div></html>");
        initialPrompt.setFont(new Font("SansSerif", Font.ITALIC, 16));
        chatDisplayPanel.add(initialPrompt);

        chatScrollPane = new JScrollPane(chatDisplayPanel);
        chatScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        chatScrollPane.setBorder(BorderFactory.createEmptyBorder());

        // ==========================
        // Reply preview + input
        // ==========================
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
        cancelReplyButton.addActionListener(evnt -> clearReplyPreview());

        replyPreviewBox.add(replyPreviewText, BorderLayout.WEST);
        replyPreviewBox.add(cancelReplyButton, BorderLayout.EAST);

        replyingToLabel = new JLabel("Replying to:");
        replyingToLabel.setVisible(false);

        messageInputField = new JTextArea(1, 1);
        messageInputField.setLineWrap(true);
        messageInputField.setWrapStyleWord(true);
        final JScrollPane inputScrollPane = new JScrollPane(messageInputField);

        // Enter to send
        final InputMap im = messageInputField.getInputMap(JComponent.WHEN_FOCUSED);
        final ActionMap am = messageInputField.getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "sendMessage");
        am.put("sendMessage", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendButton.doClick();
            }
        });

        sendButton = new JButton("Send");
        sendButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        sendButton.setPreferredSize(new Dimension(80, inputScrollPane.getPreferredSize().height));
        sendButton.addActionListener(this);

        final JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        inputPanel.add(replyPreviewBox, BorderLayout.NORTH);
        inputPanel.add(inputScrollPane, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        // Layout
        this.add(topBar, BorderLayout.NORTH);
        this.add(chatScrollPane, BorderLayout.CENTER);
        this.add(inputPanel, BorderLayout.SOUTH);
    }

    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            final JScrollBar bar = chatScrollPane.getVerticalScrollBar();
            bar.setValue(bar.getMaximum());
        });
    }

    private void handleSearchHistory() {
        if (currentChatId == null) {
            JOptionPane.showMessageDialog(this,
                    "Please open a chat first.",
                    "No Chat Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (searchChatHistoryController == null) {
            JOptionPane.showMessageDialog(this,
                    "Search history feature is not wired yet.",
                    "Not Available",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        final String keyword = JOptionPane.showInputDialog(
                this,
                "Enter keyword to search in this chat:",
                "Search Chat History",
                JOptionPane.QUESTION_MESSAGE
        );

        if (keyword == null) {
            // user cancelled
            return;
        }

        final String trimmed = keyword.trim();
        if (trimmed.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Keyword cannot be empty.",
                    "Invalid Input",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        searchChatHistoryController.execute(currentChatId, trimmed);
    }

    private void clearReplyPreview() {
        replyingToMessageId = null;
        replyPreviewBox.setVisible(false);
        replyPreviewText.setText("");
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource().equals(sendButton)) {
            final String message = messageInputField.getText().trim();
            if (!message.isEmpty() && sendMessageController != null) {
                sendMessageController.execute(
                        currentChatId,
                        currentUserId,
                        replyingToMessageId,
                        message
                );
                messageInputField.setText("");
                clearReplyPreview();
            }
        } else if (evt.getSource().equals(backButton)) {
            System.out.println("back button pressed");
            if (recentChatsController != null) {
                System.out.println("recentChatsController pressed");
                recentChatsController.execute(currentUserId);
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (!"state".equals(evt.getPropertyName())) {
            return;
        }
        if (!(evt.getNewValue() instanceof ChatState)) {
            return;
        }

        final ChatState state = (ChatState) evt.getNewValue();

        JScrollBar verticalBar = chatScrollPane.getVerticalScrollBar();
        int currentScrollValue = verticalBar.getValue();
        boolean wasAtBottom = currentScrollValue + verticalBar.getVisibleAmount() >= verticalBar.getMaximum() - 50;

        if (!state.getFirst() && state.getChatId() != null && state.getGroupName() != null) {
            state.chatViewStart();

            final boolean isGroup = state.getIsGroup();

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

        chatDisplayPanel.removeAll();

        if (state.getError() != null) {
            final JLabel errorLabel = new JLabel(state.getError());
            errorLabel.setForeground(Color.RED);
            chatDisplayPanel.add(errorLabel);
        }
        else {

            chatPartnerLabel.setText(state.getGroupName());

            final List<String[]> messages = state.getMessages();

            if (messages.isEmpty()) {
                chatDisplayPanel.add(initialPrompt);
            }
            else {
                for (String[] msg : messages) {

                    final boolean fromCurrentUser = msg[1].equals(currentUserId);
                    final String messageId = msg[0];
                    final String content = msg[2];
                    final String timestamp = msg[3];

                    final String repliedMessageId = msg.length > 4 ? msg[4] : null;
                    String repliedPreview = null;

                    if (repliedMessageId != null && !repliedMessageId.isEmpty()) {
                        for (String[] m : messages) {
                            if (m[0].equals(repliedMessageId)) {
                                final String original = m[2];
                                repliedPreview = original.length() > 25
                                        ? original.substring(0, 25) + "…"
                                        : original;
                                break;
                            }
                        }
                    }

                    final JPanel row = new JPanel();
                    row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
                    row.setOpaque(false);
                    row.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

                    final int viewportWidth =
                            chatDisplayPanel.getParent() == null
                                    ? 600
                                    : chatDisplayPanel.getParent().getWidth();
                    final int maxBubbleWidth = (int) (viewportWidth * 0.66);

                    final JPanel bubble =
                            createWrappedBubble(content, timestamp, repliedPreview,
                                    fromCurrentUser, maxBubbleWidth);

                    final JButton actionButton = new JButton("⋯");
                    actionButton.setFocusable(false);
                    actionButton.setPreferredSize(new Dimension(28, 20));
                    actionButton.setMargin(new Insets(0, 4, 0, 4));

                    final JPopupMenu menu =
                            buildPopupMenu(fromCurrentUser, messageId, content);
                    actionButton.addActionListener(
                            evnt -> menu.show(actionButton, 0, actionButton.getHeight())
                    );

                    bubble.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            replyingToMessageId = messageId;
                            final String preview = content.length() > 20
                                    ? content.substring(0, 20) + "…"
                                    : content;
                            replyPreviewText.setText("Replying to: " + preview);
                            replyPreviewBox.setVisible(true);
                        }
                    });

                    if (fromCurrentUser) {
                        row.add(Box.createHorizontalGlue());
                        row.add(bubble);
                        row.add(Box.createHorizontalStrut(4));
                        row.add(actionButton);
                    }
                    else {
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

        if (isDisplayingSearchResults && state.getMessages() != null && !state.getMessages().isEmpty()) {
            clearSearchButton.setVisible(true);
        }
        if (wasAtBottom && !isDisplayingSearchResults) {
            scrollToBottom();
        }
    }

    // ==========================
    // Popup Menu
    // ==========================
    private JPopupMenu buildPopupMenu(boolean fromCurrentUser,
                                      String messageId,
                                      String content) {
        final JPopupMenu menu = new JPopupMenu();

        if (fromCurrentUser) {
            final JMenuItem deleteItem = new JMenuItem("Delete");
            deleteItem.addActionListener(evnt -> {
                final int choice = JOptionPane.showConfirmDialog(
                        this,
                        "Delete this message?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION
                );

                if (choice == JOptionPane.YES_OPTION) {
                    if (deleteMessageController == null) {
                        JOptionPane.showMessageDialog(this,
                                "DeleteMessageController not set.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (messageId.equals(replyingToMessageId)) {
                        clearReplyPreview();
                    }
                    deleteMessageController.execute(messageId, currentUserId);
                }
            });
            menu.add(deleteItem);

            final JMenuItem replyItem = new JMenuItem("Reply");
            replyItem.addActionListener(evnt -> {
                replyingToMessageId = messageId;
                final String shortText = content.length() > 20
                        ? content.substring(0, 20) + "…"
                        : content;
                replyPreviewText.setText("Replying to: " + shortText);
                replyPreviewBox.setVisible(true);
            });
            menu.add(replyItem);
        }
        else {
            final JMenuItem replyItem = new JMenuItem("Reply");
            replyItem.addActionListener(evnt -> {
                replyingToMessageId = messageId;
                final String shortText = content.length() > 20
                        ? content.substring(0, 20) + "…"
                        : content;
                replyPreviewText.setText("Replying to: " + shortText);
                replyPreviewBox.setVisible(true);
            });
            menu.add(replyItem);

            final JMenuItem reactItem = new JMenuItem("React");
            reactItem.addActionListener(evnt -> {
                JOptionPane.showMessageDialog(this, "React feature coming soon.");
            });
            menu.add(reactItem);

            final JMenuItem clearReaction = new JMenuItem("Clear Reaction");
            clearReaction.addActionListener(evnt -> {
                JOptionPane.showMessageDialog(this, "Clear Reaction feature coming soon.");
            });
            menu.add(clearReaction);
        }

        menu.addSeparator();
        final JMenuItem cancel = new JMenuItem("Cancel");
        menu.add(cancel);

        return menu;
    }

    public String getViewName() {
        return viewName;
    }

    public void setChatPartner(String username) {
        this.chatPartnerLabel.setText(username);
        this.initialPrompt.setText("<html><div style='text-align: center;'>Send \""
                + username
                + "\" a message to start a chat!</div></div>");
        this.revalidate();
        this.repaint();
    }

    private void startAutoRefresh() {
        if (refreshTimer != null) {
            refreshTimer.stop();
        }

        if (currentChatId == null || viewChatHistoryController == null
                || currentUserIds == null || currentMessageIds == null) {
            return;
        }

        refreshTimer = new Timer(1000, evnt -> {
            if (!isDisplayingSearchResults) {
                viewChatHistoryController.execute(currentChatId, currentUserIds, currentMessageIds);
            }
        });
        refreshTimer.start();
    }

    public void setChatContext(String chatId,
                               List<String> userIds,
                               List<String> messageIds,
                               String currentUserId,
                               String groupName,
                               boolean isGroupChat) {
        this.currentChatId = chatId;
        this.currentUserId = currentUserId;
        this.isGroupChat = isGroupChat;
        this.currentUserIds = userIds;
        this.currentMessageIds = messageIds;

        setChatPartner(groupName);
        settingButton.setVisible(isGroupChat);
        if (viewChatHistoryController != null) {
            viewChatHistoryController.execute(chatId, userIds, messageIds);
        }
        startAutoRefresh();

        // Default for group chats or if set correctly
        String displayName = groupName;

        if (!isGroupChat && userIds.size() == 2) {
            // For individual chat, find the participant who is NOT the current user
            for (String userId : userIds) {
                if (!userId.equals(currentUserId)) {
                    // The name of the other person
                    displayName = userId;
                    break;
                }
            }
        }
        setChatPartner(displayName);
    }

    public void setChatId(String chatId) {
        this.currentChatId = chatId;
    }

    public void setSendMessageController(SendMessageController sendMessageController) {
        this.sendMessageController = sendMessageController;
    }

    public void setViewChatHistoryController(ViewChatHistoryController controller) {
        this.viewChatHistoryController = controller;
    }

    public void setDeleteMessageController(DeleteMessageController controller) {
        this.deleteMessageController = controller;
    }

    public void setSearchChatHistoryController(SearchChatHistoryController controller) {
        this.searchChatHistoryController = controller;
    }

    public void setRecentChatsController(RecentChatsController controller) {
        this.recentChatsController = controller;
    }

    public void setChatSettingView(ChatSettingView chatSettingView) {
        this.chatSettingView = chatSettingView;
    }

    // --------------------------------------------------------
    // PERFECT WRAPPED BUBBLE (this is the fixed version)
    // --------------------------------------------------------
    private JPanel createWrappedBubble(String text, String time, String repliedPreview,
                                       boolean fromCurrentUser, int maxWidth) {

        final JPanel bubble = new JPanel();
        bubble.setLayout(new BoxLayout(bubble, BoxLayout.Y_AXIS));
        bubble.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        bubble.setOpaque(true);
        bubble.setBackground(fromCurrentUser
                ? new Color(0x95EC69)
                : new Color(230, 230, 230));

        if (repliedPreview != null) {
            final JLabel replyHeader = new JLabel("↪ " + repliedPreview);
            replyHeader.setFont(new Font("SansSerif", Font.PLAIN, 11));
            replyHeader.setForeground(new Color(100, 100, 100));
            replyHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
            bubble.add(replyHeader);
        }

        final JLabel label = new JLabel("<html>" + text + "</html>");
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        label.setSize(new Dimension(maxWidth, Integer.MAX_VALUE));
        final Dimension preferred = label.getPreferredSize();

        label.setMaximumSize(new Dimension(maxWidth, preferred.height + 10));
        bubble.setMaximumSize(new Dimension(maxWidth + 20, preferred.height + 30));

        bubble.add(label);

        final JLabel timeLabel = new JLabel(time);
        timeLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
        timeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        timeLabel.setForeground(new Color(80, 80, 80));

        bubble.add(Box.createVerticalStrut(3));
        bubble.add(timeLabel);

        return bubble;
    }
}