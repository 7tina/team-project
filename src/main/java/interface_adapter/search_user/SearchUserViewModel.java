package interface_adapter.search_user;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import interface_adapter.ViewModel;

public class SearchUserViewModel extends ViewModel<SearchUserState> {
    public static final String TITLE_LABEL = "User Search";

    // Re-declare state field and PropertyChangeSupport to manage listeners
    private SearchUserState state = new SearchUserState();
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public SearchUserViewModel() {
        super("user search");
    }

    @Override
    public SearchUserState getState() {
        return state;
    }

    public void setState(SearchUserState state) {
        this.state = state;
    }

    /**
     * Notifies all registered listeners that the ViewModel's state
     * has changed by firing a property change event with the updated state.
     */
    public void firePropertyChanged() {
        support.firePropertyChange("state", null, this.state);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}
