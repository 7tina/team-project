package use_case.messaging.delete_m;

public class DeleteMessageInputData {
    private final String messageId;
    private final String currentUserId;

    public DeleteMessageInputData(String messageId, String currentUserId) {
        this.messageId = messageId;
        this.currentUserId = currentUserId;
    }

    String getMessageId() {
        return messageId;
    }

    String getCurrentUserId() {
        return currentUserId;
    }
}