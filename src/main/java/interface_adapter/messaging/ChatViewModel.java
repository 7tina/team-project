package interface_adapter.messaging;

import interface_adapter.ViewModel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * ViewModel for the ChatView.
 */
public class ChatViewModel extends ViewModel<ChatState> {

    // Re-declare state field and PropertyChangeSupport to manage listeners
    private ChatState state = new ChatState();
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public ChatViewModel() {
        super("chat");
    }

    @Override
    public ChatState getState() {
        return this.state;
    }

    @Override
    public void setState(ChatState state) {
        this.state = state;
    }


    public void firePropertyChange() {
        support.firePropertyChange("state", null, this.state);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}
