package interface_adapter.create_chat;

import use_case.create_chat.CreateChatInputBoundary;
import use_case.create_chat.CreateChatInputData;

public class CreateChatController {

    private final CreateChatInputBoundary createChatInputBoundary;

    public CreateChatController(CreateChatInputBoundary createChatInputBoundary) {
        this.createChatInputBoundary = createChatInputBoundary;
    }

    public void execute(String currentUserID, String targetUserID) {
        final CreateChatInputData createChatInputData = new CreateChatInputData(currentUserID, targetUserID);
        createChatInputBoundary.execute(createChatInputData);
    }
}
