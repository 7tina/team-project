package interface_adapter.messaging.delete_m;

import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

public class DeleteMessageViewModel {

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    private String deletedMessageId;
    private String error;

    public void setDeletedMessageId(String id) { this.deletedMessageId = id; }
    public void setError(String error) { this.error = error; }

    public String getDeletedMessageId() { return deletedMessageId; }
    public String getError() { return error; }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void firePropertyChanged() {
        support.firePropertyChange("deleteMessage", null, this);
    }
}
