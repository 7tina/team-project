package use_case.create_chat;

public class CreateChatInputData {
    private final String currentUserId;
    private final String targetUserId;

    public CreateChatInputData(String currentUserId, String targetUserId) {
        this.currentUserId = currentUserId;
        this.targetUserId = targetUserId;
    }

    public String getCurrentUserId() {return currentUserId;}

    public String getTargetUserId() {return targetUserId;}

}
