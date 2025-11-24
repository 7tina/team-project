package interface_adapter.messaging.delete_m;

import interface_adapter.ViewModel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * ViewModel for the Delete Message use case.
 * Holds the state to notify the view (ChatView) about a message deletion.
 */
public class DeleteMessageViewModel extends ViewModel {

    public static final String VIEW_NAME = "delete_message";

    private DeleteMessageState state = new DeleteMessageState();

    public DeleteMessageViewModel() {
        super(VIEW_NAME);
    }

    public void setState(DeleteMessageState state) {
        this.state = state;
    }

    @Override
    public DeleteMessageState getState() {
        return state;
    }

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    @Override
    public void firePropertyChange() {
        support.firePropertyChange("state", null, this.state);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}