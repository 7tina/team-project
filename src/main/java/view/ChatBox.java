package view;

import entity.UserFactory;
import entity.User;
import entity.repo.InMemoryChatRepository;
import entity.repo.InMemoryMessageRepository;
import entity.repo.InMemoryUserRepository;

import use_case.messaging.ChatMessageDto;

import use_case.messaging.send_m.SendMessageInputBoundary;
import use_case.messaging.send_m.SendMessageInputData;
import use_case.messaging.send_m.SendMessageInteractor;
import use_case.messaging.send_m.SendMessageOutputBoundary;
import use_case.messaging.send_m.SendMessageOutputData;
import use_case.messaging.view_history.ViewChatHistoryInputData;
import use_case.messaging.view_history.ViewChatHistoryInteractor;
import use_case.messaging.view_history.ViewChatHistoryOutputBoundary;
import use_case.messaging.view_history.ViewChatHistoryOutputData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Swing chat demo:
 * - Sent (you) on the right, Received (bot) on the left
 * - Auto-replies to simulate incoming messages
 * - Auto-scroll and compact, even spacing
 * - Seeds a chat + two users; uses the actual saved IDs
 */
public class ChatBox extends JFrame implements ViewChatHistoryOutputBoundary {

    // Repositories (in-memory)
    private final InMemoryChatRepository chatRepository;
    private final InMemoryMessageRepository messageRepository;
    private final InMemoryUserRepository userRepository;

    // Use cases
    private final SendMessageInputBoundary sendMessageInteractor;
    private final ViewChatHistoryInteractor viewChatHistoryInteractor;

    // UI
    private final JPanel chatPanel;
    private final JScrollPane chatScrollPane;
    private final JTextField inputField;
    private final JButton sendButton;

    // IDs (resolved at runtime after seeding)
    private String currentChatId;
    private String currentUserId;
    private String botUserId;

    // Temporary UI-only store: messageId -> emoji
    private final java.util.Map<String, String> localReactions = new java.util.HashMap<>();
    // The emojis for reaction
    private static final String[] EMOJIS = new String[] {
            "👍", "\uD83E\uDE77", "😂", "😮", "😢", "🔥", "👏", "🎉", "🤔", "😡"
    };

    // UI-only read receipts for demo
    private final java.util.Set<String> readIds = new java.util.HashSet<>();
    private boolean markAllMyReadOnNextRender = false; // flip to true when bot replies

    // UI-only: the time of the latest bot reply (used to mark messages as "Read")
    private java.time.Instant lastBotReplyTime = null;

    public ChatBox() {
        super("GoChat Demo");

        // --- repositories
        chatRepository = new InMemoryChatRepository();
        messageRepository = new InMemoryMessageRepository();
        userRepository = new InMemoryUserRepository();

        // --- seed a current user and capture the actual saved id
        final UserFactory userFactory = new UserFactory();
        User me = userFactory.create("user-1", "demo");
        me = userRepository.save(me);             // ensure stored & id resolved
        currentUserId = me.getName();

        // --- seed a chat and make sure current user is a participant
        entity.Chat c = new entity.Chat("chat-1"); // if your Chat auto-ids, that's fine
        c.addParticipant(currentUserId);
        c = chatRepository.save(c);
        currentChatId = c.getId();                // always use id from saved entity

        // --- seed a "bot" user and add it to the same chat
        User bot = userFactory.create("user-2", "bot");
        bot = userRepository.save(bot);
        botUserId = bot.getName();
        c.addParticipant(botUserId);
        chatRepository.save(c);                   // re-save with both participants

        // --- use case wiring: SendMessage
        SendMessageOutputBoundary sendPresenter = new SendMessageOutputBoundary() {
            @Override
            public void prepareSuccessView(SendMessageOutputData outputData) {
                // nothing
            }

            @Override
            public void prepareFailView(String errorMessage) {
                JOptionPane.showMessageDialog(
                        ChatBox.this,
                        errorMessage,
                        "Send Message Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        };

        sendMessageInteractor =
                new SendMessageInteractor(chatRepository, messageRepository, userRepository, sendPresenter);

        // --- use case wiring: ViewChatHistory
        viewChatHistoryInteractor =
                new ViewChatHistoryInteractor(chatRepository, messageRepository, userRepository, this);

        // --- UI: chat area
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(new Color(30, 30, 30));

        chatScrollPane = new JScrollPane(chatPanel);
        chatScrollPane.setBorder(null);
        chatScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // --- UI: input row
        inputField = new JTextField();
        sendButton = new JButton("Send");

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(inputField, BorderLayout.CENTER);
        bottom.add(sendButton, BorderLayout.EAST);

        setLayout(new BorderLayout());
        add(chatScrollPane, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        // actions
        sendButton.addActionListener(e -> sendAndRefresh());
        inputField.addActionListener(e -> sendAndRefresh());

        // frame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 640);
        setLocationRelativeTo(null);

        // initial load
        loadChatHistory();

        chatScrollPane.getViewport().addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                loadChatHistory();
                scrollToBottomSoon();
            }
        });
    }

    // ===================== sending & loading =====================

    private void sendAndRefresh() {
        String text = inputField.getText().trim();
        if (text.isEmpty()) return;

        // send my message
        SendMessageInputData myInput = new SendMessageInputData(
                currentChatId,
                currentUserId,
                text
        );
        sendMessageInteractor.execute(myInput);
        inputField.setText("");

        loadChatHistory();
        scrollToBottomSoon();

        // simulate an incoming reply after 600ms
        javax.swing.Timer t = new javax.swing.Timer(2000, ev -> {
            ((javax.swing.Timer) ev.getSource()).stop();
            String reply = autoReply(text);
            SendMessageInputData botInput = new SendMessageInputData(
                    currentChatId,
                    botUserId,
                    reply
            );
            sendMessageInteractor.execute(botInput);

            // Mark all of *my* messages up to now as "Read"
            lastBotReplyTime = java.time.Instant.now();

            loadChatHistory();
            scrollToBottomSoon();
        });
        t.setRepeats(false);
        t.start();
    }

    private void loadChatHistory() {
        viewChatHistoryInteractor.execute(new ViewChatHistoryInputData(currentChatId));
    }

    // ===================== presenter callbacks =====================

    @Override
    public void prepareSuccessView(ViewChatHistoryOutputData outputData) {
        chatPanel.removeAll();

        List<ChatMessageDto> messages = outputData.getMessages();

        for (ChatMessageDto dto : messages) {
            boolean isMe = dto.getSenderUserId().equals(currentUserId);

            // bubble (message + timestamp)
            JPanel bubble = new JPanel();
            bubble.setLayout(new BoxLayout(bubble, BoxLayout.Y_AXIS));
            bubble.setOpaque(false);
            bubble.setBorder(new EmptyBorder(6, 10, 4, 10));
            int maxW = currentMaxBubbleWidth();

            String raw = dto.getContent();
            JComponent msgComp = makeWrappingBubbleText(raw, isMe, maxW);

            msgComp.setOpaque(true);
            msgComp.setBorder(new javax.swing.border.EmptyBorder(8, 10, 8, 10));

            if (isMe) {
                msgComp.setBackground(new java.awt.Color(0x95EC69));
                msgComp.setForeground(java.awt.Color.BLACK);
            } else {
                msgComp.setBackground(new java.awt.Color(60, 60, 60));
                msgComp.setForeground(java.awt.Color.WHITE);
            }

            JPanel msgRow = new JPanel();
            msgRow.setOpaque(false);
            msgRow.setLayout(new BoxLayout(msgRow, BoxLayout.X_AXIS));
            msgRow.add(msgComp);
            msgRow.setAlignmentX(LEFT_ALIGNMENT);
            msgComp.setAlignmentX(LEFT_ALIGNMENT);

            // if you render a reaction, add it to the right as before
            if (!isMe) {
                String r = localReactions.get(getMessageId(dto));
                if (r != null) {
                    JLabel reaction = new JLabel(r);
                    reaction.setBorder(new javax.swing.border.EmptyBorder(0, 6, 0, 0));
                    // reaction.setFont(emojiFont());
                    msgRow.add(javax.swing.Box.createHorizontalStrut(6));
                    msgRow.add(reaction);
                }
            }

            bubble.add(msgRow);

            // ---- meta (time + status) OUTSIDE the bubble ----
            JLabel timeLabel = new JLabel(formatTimestamp(dto.getTimestamp()));
            timeLabel.setFont(new Font("Dialog", Font.PLAIN, 11));
            timeLabel.setForeground(new Color(160, 160, 160));

            JLabel statusLabel = null;
            if (isMe) {
                boolean isRead = lastBotReplyTime != null
                        && (!dto.getTimestamp().isAfter(lastBotReplyTime));
                statusLabel = new JLabel(isRead ? "Read" : "✓");
                statusLabel.setFont(new Font("Dialog", Font.PLAIN, 11));
                statusLabel.setForeground(new Color(160, 160, 160));
            }

            // action button + popup
            JButton actionBtn = makeActionButton();
            JPopupMenu menu = buildActionMenu(isMe, dto, actionBtn);
            actionBtn.addActionListener(e -> menu.show(actionBtn, 0, actionBtn.getHeight()));

            // row: (button + bubble) for others, (bubble + button) for me
            final int GAP = 4; // tighter gap than before
            JPanel side = new JPanel();
            side.setOpaque(false);
            side.setLayout(new BoxLayout(side, BoxLayout.X_AXIS));
            if (isMe) {
                side.add(bubble);
                side.add(Box.createHorizontalStrut(GAP));
                side.add(actionBtn);
            } else {
                side.add(actionBtn);
                side.add(Box.createHorizontalStrut(GAP));
                side.add(bubble);
            }

            // meta row (time [+ status]) aligned with bubble’s OUTER edge
            JPanel metaRow = new JPanel();
            metaRow.setOpaque(false);
            metaRow.setLayout(new BoxLayout(metaRow, BoxLayout.X_AXIS));
            metaRow.add(timeLabel);
            if (statusLabel != null) {
                metaRow.add(Box.createHorizontalStrut(6));
                metaRow.add(statusLabel);
            }

            // stack side (top) and meta (bottom)
            JPanel column = new JPanel();
            column.setOpaque(false);
            column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
            column.add(side);
            column.add(Box.createVerticalStrut(2));

            // for others: indent the meta by the button width + gap
            JPanel metaWrap = new JPanel(new BorderLayout());
            metaWrap.setOpaque(false);
            if (isMe) {
                metaWrap.add(metaRow, BorderLayout.EAST);
            } else {
                int btnW = actionBtn.getPreferredSize().width;
                JPanel padded = new JPanel(new BorderLayout());
                padded.setOpaque(false);
                padded.setBorder(new EmptyBorder(0, btnW + GAP, 0, 0));
                padded.add(metaRow, BorderLayout.WEST);
                metaWrap.add(padded, BorderLayout.WEST);
            }
            column.add(metaWrap);

            // wrapper: keeps left/right alignment and prevents tall stretching
            JPanel wrapper = new JPanel(new BorderLayout());
            wrapper.setOpaque(false);
            wrapper.setBorder(new EmptyBorder(2, 10, 2, 10));
            if (isMe) wrapper.add(column, BorderLayout.EAST);
            else      wrapper.add(column, BorderLayout.WEST);

            // cap row height for even vertical spacing
            int rowH = column.getPreferredSize().height + 4;
            wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, rowH));
            wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

            chatPanel.add(wrapper);
            chatPanel.add(Box.createVerticalStrut(4));

            // If the bot just replied, mark all my messages as "Read" for this demo.
            if (markAllMyReadOnNextRender && dto.getSenderUserId().equals(currentUserId)) {
                readIds.add(getMessageId(dto));
            }
            // After we process the list once, clear the flag.
        }

        markAllMyReadOnNextRender = false;
        chatPanel.revalidate();
        chatPanel.repaint();
        scrollToBottomSoon(); // always land at latest message
    }

    @Override
    public void prepareNoMessagesView(String chatId) {
        chatPanel.removeAll();
        chatPanel.add(new JLabel("No messages yet."));
        chatPanel.revalidate();
        chatPanel.repaint();
    }

    @Override
    public void prepareFailView(String errorMessage) {
        JOptionPane.showMessageDialog(this, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // ===================== helpers =====================

    private void scrollToBottomSoon() {
        SwingUtilities.invokeLater(() -> {
            chatPanel.revalidate();
            chatPanel.repaint();
            JScrollBar vb = chatScrollPane.getVerticalScrollBar();
            vb.setValue(vb.getMaximum());
        });
    }

    private String formatTimestamp(Instant timestamp) {
        ZonedDateTime time = timestamp.atZone(ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return formatter.format(time);
    }

    private String autoReply(String last) {
        String l = last.toLowerCase();
        if (l.contains("hello") || l.contains("hi")) return "hey!";
        if (l.contains("how are")) return "doing great, you?";
        if (l.endsWith("?")) return "good question!";
        return "nice ✓";
    }

    // Small, neutral button used next to each bubble
    private JButton makeActionButton() {
        JButton b = new JButton("⋯");
        b.setMargin(new Insets(2, 8, 2, 8));
        b.setFocusable(false);
        b.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
        b.setBackground(new Color(50, 50, 50));
        b.setForeground(new Color(220, 220, 220));
        b.setPreferredSize(new Dimension(26, 18));
        return b;
    }

    // Build the popup menu; for now handlers are UI-only placeholders
    private JPopupMenu buildActionMenu(boolean isMe, ChatMessageDto dto, JButton anchor) {
        JPopupMenu menu = new JPopupMenu();

        if (isMe) {
            JMenuItem delete = new JMenuItem("Delete");
            delete.addActionListener(e ->
                    JOptionPane.showMessageDialog(this, "Delete (UI only for now)"));
            menu.add(delete);

            JMenuItem reply = new JMenuItem("Reply");
            reply.addActionListener(e ->
                    JOptionPane.showMessageDialog(this, "Reply (UI only for now)"));
            menu.add(reply);
        } else {
            JMenuItem reply = new JMenuItem("Reply");
            reply.addActionListener(e ->
                    JOptionPane.showMessageDialog(this, "Reply (UI only for now)"));
            menu.add(reply);

            JMenuItem report = new JMenuItem("Report");
            report.addActionListener(e ->
                    JOptionPane.showMessageDialog(this, "Report (UI only for now)"));
            menu.add(report);

            JMenuItem react = new JMenuItem("React");
            react.addActionListener(e -> showReactionChooser(dto, anchor)); // ⭐ open emoji picker
            menu.add(react);

            JMenuItem clear = new JMenuItem("Clear reaction");
            clear.addActionListener(e -> {
                localReactions.remove(getMessageId(dto));
                loadChatHistory();
                scrollToBottomSoon();
            });
            menu.add(clear);
        }

        menu.addSeparator();
        JMenuItem cancel = new JMenuItem("Cancel");
        cancel.addActionListener(e -> menu.setVisible(false));
        menu.add(cancel);

        return menu;
    }

    private void showReactionChooser(ChatMessageDto dto, Component anchor) {
        JPopupMenu menu = new JPopupMenu();
        for (String e : EMOJIS) {
            JMenuItem it = new JMenuItem(e);
            it.addActionListener(a -> {
                String msgId = getMessageId(dto);
                localReactions.put(msgId, e);     // save UI-only reaction
                // re-render messages so the emoji appears in-bubble
                loadChatHistory();
                scrollToBottomSoon();
            });
            menu.add(it);
        }
        menu.addSeparator();
        JMenuItem cancel = new JMenuItem("Cancel");
        cancel.addActionListener(a -> menu.setVisible(false));
        menu.add(cancel);

        menu.show(anchor, 0, anchor.getHeight());
    }

    // utility to read the dto id (rename if your DTO uses a different getter)
    private String getMessageId(ChatMessageDto dto) {
        // return dto.getId();  // ← if your dto uses getId()
        return dto.getMessageId(); // ← if your dto uses getMessageId()
    }

    // ~2/3 of current viewport width (with a little margin)
    private int currentMaxBubbleWidth() {
        int w = chatScrollPane.getViewport().getExtentSize().width;
        if (w <= 0) w = chatScrollPane.getWidth();
        if (w <= 0) w = 480;
        return (int)Math.round(w * 0.66) - 16; // subtract a bit for padding/scrollbar
    }

    // Build a wrapping bubble that is COMPACT for short text and wraps at ~2/3 width for long text
    private JComponent makeWrappingBubbleText(String raw, boolean isMe, int maxW) {
        JTextArea ta = new JTextArea(raw);
        ta.setWrapStyleWord(true);
        ta.setEditable(false);
        ta.setOpaque(true);
        ta.setBorder(new javax.swing.border.EmptyBorder(8, 10, 8, 10));
        ta.setFont(new Font("Dialog", Font.PLAIN, 13));
        ta.setHighlighter(null);
        ta.setAlignmentX(LEFT_ALIGNMENT);

        if (isMe) {
            ta.setBackground(new Color(0x95EC69));
            ta.setForeground(Color.BLACK);
        } else {
            ta.setBackground(new Color(60, 60, 60));
            ta.setForeground(Color.WHITE);
        }

        // --- 1) Measure natural single-line width (no wrapping) ---
        ta.setLineWrap(false);
        // Let Swing compute based on actual text metrics
        Dimension noWrap = ta.getPreferredSize();

        // --- 2) Decide compact vs wrapped ---
        if (noWrap.width <= maxW) {
            // Short text: keep it compact (use the natural width)
            ta.setPreferredSize(noWrap);
            ta.setMaximumSize(new Dimension(noWrap.width, Integer.MAX_VALUE));
        } else {
            // Long text: enable wrapping and cap at maxW
            ta.setLineWrap(true);
            // Ask layout to compute height when constrained to maxW
            ta.setSize(new Dimension(maxW, Short.MAX_VALUE));
            Dimension wrapped = ta.getPreferredSize();
            // Enforce width cap; height grows automatically
            wrapped.width = Math.min(wrapped.width, maxW);
            ta.setPreferredSize(wrapped);
            ta.setMaximumSize(new Dimension(maxW, Integer.MAX_VALUE));
        }

        return ta;
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChatBox window = new ChatBox();
            window.setVisible(true);
        });
    }
}
