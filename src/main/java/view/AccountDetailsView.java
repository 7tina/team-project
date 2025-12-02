package view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;

import interfaceadapter.ViewManagerModel;
import interfaceadapter.logged_in.ChangePasswordController;
import interfaceadapter.logged_in.LoggedInState;
import interfaceadapter.logged_in.LoggedInViewModel;
import interfaceadapter.logout.LogoutController;

/**
 * The View for displaying Account Details, allowing
 * password changes, and handling the Logout
 * functionality.
 */
public class AccountDetailsView extends JPanel implements
        ActionListener, PropertyChangeListener {
    /** The name identifier for this account details view. */
    private final String viewName = "account details";

    /** The model responsible for managing view transitions and state. */
    private final ViewManagerModel viewManagerModel;

    /** The view model containing data and state for the logged-in user interface. */
    private final LoggedInViewModel loggedInViewModel;

    /** The controller responsible for handling user logout operations. */
    private LogoutController logoutController;

    /** The controller responsible for handling password change requests. */
    private ChangePasswordController changePasswordController;

    // Components

    /** Button that triggers the logout process when clicked. */
    private final JButton logoutButton;

    /** Button that initiates the password change workflow when clicked. */
    private final JButton changePasswordButton;

    /** Label displaying the current user's username. */
    private final JLabel usernameLabel;

    /** Panel containing the top navigation bar components. */
    private final JPanel topBar;

    /**
     * Constructs an AccountDetailsView with the specified view
     * management and user state models.
     * @param newViewManagerModel the model responsible for managing
     *                         view transitions and navigation state
     * @param newLoggedInViewModel the model containing the current user's
     *                          state and data
     * @throws NullPointerException if either viewManagerModel or loggedInViewModel is null
     */
    public AccountDetailsView(final ViewManagerModel newViewManagerModel,
                              final LoggedInViewModel newLoggedInViewModel) {
        this.viewManagerModel = newViewManagerModel;
        this.loggedInViewModel = newLoggedInViewModel;
        this.loggedInViewModel.addPropertyChangeListener(this);

        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Bar (Title and Back Button)
        topBar = new JPanel(new BorderLayout());

        final JButton backButton = new JButton("⬅");
        backButton.setFont(new Font("SansSerif", Font.BOLD, 20));
        backButton.addActionListener(evnt -> {
            newViewManagerModel.setState("logged in");
            newViewManagerModel.firePropertyChange();
        });

        topBar.add(backButton, BorderLayout.WEST);

        final JLabel title = new JLabel("Account Details", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        topBar.add(title, BorderLayout.CENTER);

        // Main Content Area
        final JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(50, 20, 50, 20));

        final String currentUsername = newLoggedInViewModel.getState().getUsername();
        usernameLabel = new JLabel("Username: " + (currentUsername
                != null ? currentUsername : "User"));
        usernameLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        changePasswordButton = new JButton("Change Password");
        changePasswordButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        changePasswordButton.addActionListener(this);

        contentPanel.add(usernameLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(changePasswordButton);
        contentPanel.add(Box.createVerticalGlue());

        // Logout Button Panel (Bottom)
        final JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoutButton = new JButton("Logout");
        logoutButton.addActionListener(this);

        southPanel.add(logoutButton);

        // Assembly
        this.add(topBar, BorderLayout.NORTH);
        this.add(contentPanel, BorderLayout.CENTER);
        this.add(southPanel, BorderLayout.SOUTH);
    }

    /**
     * Handles action events from UI components, specifically
     * the logout and change password buttons.
     * When the logout button is clicked, triggers the logout
     * controller to execute the logout process.
     * When the change password button is clicked, displays a modal
     * dialog for password change.
     * @param evt the action event triggered by user interaction with
     *            UI components
     * @throws IllegalStateException if a controller is required but not set (null)
     */
    @Override
    public void actionPerformed(final ActionEvent evt) {
        if (evt.getSource().equals(logoutButton) && logoutController != null) {
            logoutController.execute();
        } else if (evt.getSource().equals(changePasswordButton)
                && changePasswordController != null) {
            final String currentUsername =
                    loggedInViewModel.getState().getUsername();

            // Create a custom dialog
            final JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(
                    this), "Change Password",
                    Dialog.ModalityType.APPLICATION_MODAL);
            dialog.setLayout(new BorderLayout());
            dialog.setSize(400, 150);
            dialog.setLocationRelativeTo(this);

            // Message panel
            final JPanel messagePanel = new JPanel();
            messagePanel.add(new JLabel(
                    "Enter new password for " + currentUsername + ":"));
            dialog.add(messagePanel, BorderLayout.NORTH);

            // Text field
            final JPasswordField passwordField = new JPasswordField(20);
            final JPanel fieldPanel = new JPanel();
            fieldPanel.add(passwordField);
            dialog.add(fieldPanel, BorderLayout.CENTER);

            // Buttons
            final JButton okButton = new JButton("OK");
            final JButton cancelButton = new JButton("Cancel");
            final JPanel buttonPanel = new JPanel();
            buttonPanel.add(okButton);
            buttonPanel.add(cancelButton);
            dialog.add(buttonPanel, BorderLayout.SOUTH);

            // Make OK triggered by Enter
            dialog.getRootPane().setDefaultButton(okButton);

            // Action listeners
            okButton.addActionListener(evnt -> {
                final String newPassword = new String(passwordField.getPassword());
                if (!newPassword.isEmpty()) {
                    changePasswordController.execute(
                            currentUsername, newPassword);
                    dialog.dispose();
                }
                else {
                    JOptionPane.showMessageDialog(dialog,
                            "Password cannot be empty.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            cancelButton.addActionListener(evnt -> dialog.dispose());

            dialog.setVisible(true);
        }
    }

    /**
     * Handles property change events to update the view when the
     * user's state changes.
     * Listens for either general state updates ("state") or
     * specific username updates ("username")
     * and refreshes the username display accordingly.
     *
     * @param evt the property change event containing the
     *            updated state information
     * @throws ClassCastException if the new value of the event is not a LoggedInState object
     */
    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        // Check for either a general state update or a
        // specific username update event.
        if (evt.getPropertyName().equals("state")
                || evt.getPropertyName().equals("username")) {
            final LoggedInState state = (LoggedInState) evt.getNewValue();
            final String newUsername = state.getUsername();

            // Update the label text
            usernameLabel.setText("Username: "
                    + (newUsername != null ? newUsername : "User"));

        }
    }

    /**
     * Returns the name identifier for this view.
     * The view name is used for view management and navigation purposes.
     *
     * @return the view name as a String, typically "account details"
     */
    public String getViewName() {
        return viewName;
    }

    /**
     * Sets the LogoutController for this view.
     * This controller will handle user logout operations triggered when
     * the logout button is clicked in the UI.
     *
     * @param newLogoutController the controller to handle logout operations
     * @throws NullPointerException if the provided controller is null
     */
    public void setLogoutController(
            final LogoutController newLogoutController) {
        this.logoutController = newLogoutController;
    }

    /**
     * Sets the ChangePasswordController for this view.
     * This controller will handle password change requests
     * initiated by the user
     * through the change password button in the UI.
     *
     * @param newChangePasswordController the controller to handle
     *                                    password change operations
     * @throws NullPointerException if the provided controller is null
     */
    public void setChangePasswordController(
            final ChangePasswordController newChangePasswordController) {
        this.changePasswordController = newChangePasswordController;
    }
}
