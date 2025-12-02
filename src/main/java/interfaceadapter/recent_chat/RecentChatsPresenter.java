package interfaceadapter.recent_chat;

import interfaceadapter.ViewManagerModel;
import interfaceadapter.logged_in.LoggedInState;
import interfaceadapter.logged_in.LoggedInViewModel;
import interfaceadapter.messaging.ChatState;
import interfaceadapter.messaging.ChatViewModel;
import usecase.recent_chat.RecentChatsOutputBoundary;
import usecase.recent_chat.RecentChatsOutputData;

public class RecentChatsPresenter implements RecentChatsOutputBoundary {

    private final LoggedInViewModel loggedInViewModel;
    private final ViewManagerModel viewManagerModel;
    private final ChatViewModel chatViewModel;

    // AppBuilder 里应该是：new RecentChatsPresenter(viewManagerModel, loggedInViewModel)
    public RecentChatsPresenter(ViewManagerModel viewManagerModel,
                                LoggedInViewModel loggedInViewModel,
                                ChatViewModel chatViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.loggedInViewModel = loggedInViewModel;
        this.chatViewModel = chatViewModel;
    }

    @Override
    public void prepareSuccessView(RecentChatsOutputData outputData) {
        // 从 LoggedInViewModel 中取出 state
        LoggedInState state = loggedInViewModel.getState();

        // 塞入最近聊天列表（我们在 RecentChatsOutputData 里实现了 getRecentChats）
        state.setChatNames(outputData.getChatNames());
        state.setNameToChatIds(outputData.getNameToChatIds());
        state.setLoggedIn(true);

        // 更新 ViewModel
        loggedInViewModel.setState(state);

        // 通知所有监听这个 ViewModel 的 View（比如 LoggedInView）
        // “state” 发生了变化
        loggedInViewModel.firePropertyChange("recentChats");

        ChatState chatState = chatViewModel.getState();
        chatState.clearMessages();
        chatState.clearMessageIds();
        chatState.clearReactions();
        chatState.chatViewStop();
        chatViewModel.setState(chatState);
        chatViewModel.firePropertyChange("state");

        // 确保当前显示的是 LoggedInView（可要可不要，看你 AppBuilder 的逻辑）
        viewManagerModel.setState(loggedInViewModel.getViewName());
        viewManagerModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(String errorMessage) {
        LoggedInState state = loggedInViewModel.getState();
        state.setRecentChatsError(errorMessage);
        loggedInViewModel.setState(state);
    }

}
