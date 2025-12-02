package usecase.recent_chat;

public interface RecentChatsOutputBoundary {
    void prepareSuccessView(RecentChatsOutputData outputData);

    void prepareFailView(String errorMessage);
}
