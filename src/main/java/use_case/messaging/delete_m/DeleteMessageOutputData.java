package use_case.messaging.delete_m;

import java.time.LocalDateTime;

public class DeleteMessageOutputData {
    private final String deletedMessageId;
    private final LocalDateTime deletionTime; // Record time
    private final boolean success;
    private final String failReason;

    public DeleteMessageOutputData(String deletedMessageId, LocalDateTime deletionTime, boolean success, String failReason) {
        this.deletedMessageId = deletedMessageId;
        this.deletionTime = deletionTime;
        this.success = success;
        this.failReason = failReason;
    }

    public DeleteMessageOutputData(String deletedMessageId, LocalDateTime deletionTime) {
        this(deletedMessageId, deletionTime, true, null);
    }

    public String getMessageId() {
        return deletedMessageId;
    }

    public String getDeletedMessageId() {
        return deletedMessageId;
    }

    public LocalDateTime getDeletionTime() {
        return deletionTime;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getFailReason() {
        return failReason;
    }
}