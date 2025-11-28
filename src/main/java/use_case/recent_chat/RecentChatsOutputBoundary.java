package use_case.recent_chat;

public interface RecentChatsOutputBoundary {
    void prepareSuccessView(RecentChatsOutputData outputData);

    void prepareFailView(String errorMessage);
}
