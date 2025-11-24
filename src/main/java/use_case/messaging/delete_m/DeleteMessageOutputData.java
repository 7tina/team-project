package use_case.messaging.delete_m;

import java.time.LocalDateTime;

public class DeleteMessageOutputData {
    private final String deletedMessageId;
    private final LocalDateTime deletionTime; // Record time

    public DeleteMessageOutputData(String deletedMessageId, LocalDateTime deletionTime) {
        this.deletedMessageId = deletedMessageId;
        this.deletionTime = deletionTime;
    }

    public String getDeletedMessageId() {
        return deletedMessageId;
    }

    public LocalDateTime getDeletionTime() {
        return deletionTime;
    }
}