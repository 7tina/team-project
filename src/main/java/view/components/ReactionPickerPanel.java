package view.components;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * A popup panel for selecting emoji reactions.
 */
public class ReactionPickerPanel extends JPopupMenu {

    // The 10 recommended emoji reactions
    private static final String[] EMOJIS = {
            "❤️", "👍", "😂", "😮", "😢",
            "🙏", "🔥", "🎉", "👎", "🤔"
    };

    /**
     * Creates a reaction picker panel.
     *
     * @param onReactionSelected callback when an emoji is selected
     */
    public ReactionPickerPanel(Consumer<String> onReactionSelected) {
        setLayout(new GridLayout(2, 5, 5, 5));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        for (String emoji : EMOJIS) {
            final JButton emojiButton = new JButton();

            // Use HTML to render emoji - this sometimes works better
            emojiButton.setText("<html><div style='font-size:24px'>" + emoji + "</div></html>");

            emojiButton.setPreferredSize(new Dimension(50, 50));
            emojiButton.setFocusable(false);
            emojiButton.setBorderPainted(false);
            emojiButton.setContentAreaFilled(false);

            // Hover effect
            emojiButton.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    emojiButton.setContentAreaFilled(true);
                    emojiButton.setBackground(new Color(240, 240, 240));
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    emojiButton.setContentAreaFilled(false);
                }
            });

            emojiButton.addActionListener(e -> {
                onReactionSelected.accept(emoji);
                setVisible(false);
            });

            add(emojiButton);
        }
    }
}