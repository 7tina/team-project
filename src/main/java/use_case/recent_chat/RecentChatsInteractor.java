package use_case.recent_chat;

public class RecentChatsInteractor implements RecentChatsInputBoundary {
    private final RecentChatsOutputBoundary recentChatsPresenter;
    private final RecentChatsUserDataAccessInterface recentChatsUserDataAccess;

    public RecentChatsInteractor(RecentChatsOutputBoundary recentChatsPresenter,
                                 RecentChatsUserDataAccessInterface recentChatsUserDataAccess) {
        this.recentChatsPresenter = recentChatsPresenter;
        this.recentChatsUserDataAccess = recentChatsUserDataAccess;
    }

    @Override
    public void execute(RecentChatsInputData recentChatsInputData) {}
}
