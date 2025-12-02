package interfaceadapter.logged_in;

import interfaceadapter.ViewModel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * The View Model for the Logged In View.
 */
public class LoggedInViewModel extends ViewModel<LoggedInState> {

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public LoggedInViewModel() {
        super("logged in");
        setState(new LoggedInState());
    }

    /**
     * Fires a property change event with the given property name.
     * This is used for specific state updates (like "username" or "password").
     * @param propertyName The name of the property that changed.
     */
    public void firePropertyChange(String propertyName) {
        support.firePropertyChange(propertyName, null, this.getState());
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

}