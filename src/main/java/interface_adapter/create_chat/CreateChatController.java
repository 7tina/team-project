package interface_adapter.create_chat;

import use_case.create_chat.CreateChatInputBoundary;
import use_case.create_chat.CreateChatInputData;

import java.util.List;

public class CreateChatController {

    private final CreateChatInputBoundary createChatInputBoundary;

    public CreateChatController(CreateChatInputBoundary createChatInputBoundary) {
        this.createChatInputBoundary = createChatInputBoundary;
    }

    public void execute(String currentUserID, List<String> participantUsernames,
                        String groupName, boolean isGroupChat) {
        final CreateChatInputData createChatInputData = new CreateChatInputData(currentUserID,
                participantUsernames, groupName, isGroupChat);
        createChatInputBoundary.execute(createChatInputData);
    }
}
