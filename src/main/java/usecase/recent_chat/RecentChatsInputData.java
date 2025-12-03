package usecase.recent_chat;

public class RecentChatsInputData {

    private final String userId;

    public RecentChatsInputData(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
