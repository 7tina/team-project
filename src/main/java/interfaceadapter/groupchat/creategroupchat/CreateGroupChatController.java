package interfaceadapter.groupchat.creategroupchat;

import interfaceadapter.create_chat.CreateChatController;
import usecase.create_chat.CreateChatInputBoundary;

public class CreateGroupChatController extends CreateChatController {

    public CreateGroupChatController(CreateChatInputBoundary createChatInputBoundary) {
        super(createChatInputBoundary);
    }

}
