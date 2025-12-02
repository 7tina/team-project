package interfaceadapter.groupchat.creategroupchat;

import interfaceadapter.ViewManagerModel;
import interfaceadapter.create_chat.CreateChatPresenter;
import interfaceadapter.messaging.ChatViewModel;
import interfaceadapter.search_user.SearchUserViewModel;

public class CreateGroupChatPresenter extends CreateChatPresenter {
    public CreateGroupChatPresenter(ViewManagerModel viewManagerModel,
                                    ChatViewModel chatViewModel,
                                    SearchUserViewModel searchUserViewModel) {
        super(viewManagerModel, chatViewModel, searchUserViewModel);
    }
}
