package interfaceadapter.recent_chat;

import usecase.recent_chat.RecentChatsInputBoundary;
import usecase.recent_chat.RecentChatsInputData;

public class RecentChatsController {

    private final RecentChatsInputBoundary recentChatsInputBoundary;

    public RecentChatsController(RecentChatsInputBoundary recentChatsInputBoundary) {
        this.recentChatsInputBoundary = recentChatsInputBoundary;
    }

    public void execute(String userId) {
        final RecentChatsInputData recentChatsInputData = new RecentChatsInputData(userId);
        recentChatsInputBoundary.execute(recentChatsInputData);
    }
}
