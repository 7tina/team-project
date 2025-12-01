package interface_adapter.create_chat;

import java.util.List;

import use_case.create_chat.CreateChatInputBoundary;
import use_case.create_chat.CreateChatInputData;

/**
 * Controller for handling the create chat use case.
 */
public class CreateChatController {

    private final CreateChatInputBoundary createChatInputBoundary;

    /**
     * Constructs a CreateChatController with the given input boundary.
     *
     * @param createChatInputBoundary the input boundary for creating a chat
     */
    public CreateChatController(CreateChatInputBoundary createChatInputBoundary) {
        this.createChatInputBoundary = createChatInputBoundary;
    }

    /**
     * Executes the create chat use case with the provided parameters.
     *
     * @param currentUserId          the ID of the user creating the chat
     * @param participantUsernames   the list of usernames participating in the chat
     * @param groupName              the name of the group chat (if applicable)
     * @param isGroupChat            true if it is a group chat, false otherwise
     */
    public void execute(String currentUserId, List<String> participantUsernames,
                        String groupName, boolean isGroupChat) {
        final CreateChatInputData createChatInputData =
                new CreateChatInputData(currentUserId, participantUsernames, groupName, isGroupChat);
        createChatInputBoundary.execute(createChatInputData);
    }
}
