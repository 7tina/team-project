package use_case.groups.changegroupname;

public class ChangeGroupNameOutputData {
    private final String chatId;
    private final String newGroupName;
    private final boolean success;
    private final String errorMessage;

    public ChangeGroupNameOutputData(String chatId, String newGroupName,
                                     boolean success, String errorMessage) {
        this.chatId = chatId;
        this.newGroupName = newGroupName;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public String getChatId() {
        return chatId;
    }

    public String getNewGroupName() {
        return newGroupName;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}