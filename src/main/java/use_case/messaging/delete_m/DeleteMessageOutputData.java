package use_case.messaging.delete_m;

public class DeleteMessageOutputData {
    private final String deletedMessageId;

    public DeleteMessageOutputData(String deletedMessageId) {
        this.deletedMessageId = deletedMessageId;
    }

    public String getDeletedMessageId() { return deletedMessageId; }
}
