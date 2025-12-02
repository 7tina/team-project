package view;

import interfaceadapter.ViewManagerModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * The initial view with Login and Sign Up buttons.
 */
public class WelcomeView extends JPanel implements ActionListener, PropertyChangeListener {

    // Change the view name constant
    private final String viewName = "welcome";
    private final ViewManagerModel viewManagerModel;

    private final JButton logInButton;
    private final JButton signUpButton;

    public WelcomeView(ViewManagerModel viewManagerModel) {
        this.viewManagerModel = viewManagerModel;

        final JLabel title = new JLabel("Welcome to GoChat!");
        title.setFont(new Font("Oxygen", Font.BOLD, 24));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        final JPanel buttons = new JPanel();
        logInButton = new JButton("Login");
        buttons.add(logInButton);
        signUpButton = new JButton("Sign Up");
        buttons.add(signUpButton);

        // Action Listeners for navigation
        // The viewManagerModel needs to be updated to switch to the target views: "log in" and "sign up"
        logInButton.addActionListener(
                e -> {
                    viewManagerModel.setState("log in");
                    viewManagerModel.firePropertyChange();
                }
        );

        signUpButton.addActionListener(
                e -> {
                    viewManagerModel.setState("sign up");
                    viewManagerModel.firePropertyChange();
                }
        );

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        this.add(Box.createVerticalStrut(100));
        this.add(title);
        this.add(Box.createVerticalStrut(50));
        this.add(buttons);
        this.add(Box.createVerticalGlue());
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
    }

    public String getViewName() {
        return viewName;
    }
}