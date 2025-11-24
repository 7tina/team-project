package interface_adapter.messaging.delete_m;

/**
 * State object for the Delete Message use case ViewModel.
 * Holds the information about the deletion result to update the ChatView.
 */
public class DeleteMessageState {

    // The ID of the message that was attempted to be deleted
    private String deletedMessageId = null;

    // Error message if the deletion failed (e.g., permission denied)
    private String error = null;

    // Whether the operation was successful
    private boolean isSuccess = false;

    public DeleteMessageState() {}

    // Copy constructor (Good practice for preventing unintended state changes)
    public DeleteMessageState(DeleteMessageState copy) {
        this.deletedMessageId = copy.deletedMessageId;
        this.error = copy.error;
        this.isSuccess = copy.isSuccess;
    }

    // --- Getters ---

    public String getDeletedMessageId() {
        return deletedMessageId;
    }

    public String getError() {
        return error;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    // --- Setters ---

    public void setDeletedMessageId(String deletedMessageId) {
        this.deletedMessageId = deletedMessageId;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }
}