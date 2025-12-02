package interfaceadapter.groupchat.creategroupchat;

import interfaceadapter.ViewManagerModel;
import interfaceadapter.create_chat.CreateChatPresenter;
import interfaceadapter.messaging.ChatViewModel;

public class CreateGroupChatPresenter extends CreateChatPresenter {
    public CreateGroupChatPresenter(ViewManagerModel viewManagerModel,
                                    ChatViewModel chatViewModel) {
        super(viewManagerModel, chatViewModel);
    }
}
