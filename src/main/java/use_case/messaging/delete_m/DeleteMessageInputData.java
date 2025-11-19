package use_case.messaging.delete_m;

public class DeleteMessageInputData {
    private final String messageId;
    private final String requestingUserId;

    public DeleteMessageInputData(String messageId, String requestingUserId) {
        this.messageId = messageId;
        this.requestingUserId = requestingUserId;
    }

    public String getMessageId() { return messageId; }
    public String getRequestingUserId() { return requestingUserId; }
}
