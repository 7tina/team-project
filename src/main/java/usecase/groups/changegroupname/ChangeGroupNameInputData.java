package usecase.groups.changegroupname;

public class ChangeGroupNameInputData {
    private final String chatId;
    private final String newGroupName;

    public ChangeGroupNameInputData(String chatId, String newGroupName) {
        this.chatId = chatId;
        this.newGroupName = newGroupName;
    }

    public String getChatId() {
        return chatId;
    }

    public String getNewGroupName() {
        return newGroupName;
    }
}