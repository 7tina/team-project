package interfaceadapter.accesschat;


import usecase.accesschat.AccessChatInputBoundary;
import usecase.accesschat.AccessChatInputData;

public class AccessChatController {

    private final AccessChatInputBoundary accessChatInputBoundary;

    /**
     * Constructs a AccessChatController with the given input boundary.
     *
     * @param accessChatInputBoundary the input boundary for accessing a chat
     */
    public AccessChatController(AccessChatInputBoundary accessChatInputBoundary) {
        this.accessChatInputBoundary = accessChatInputBoundary;
    }

    /**
     * Executes the access chat use case with the provided parameters.
     *
     * @param currentUserId          the ID of the user
     * @param chatId   the ID of the chat
     */
    public void execute(String currentUserId, String chatId) {
        final AccessChatInputData accessChatInputData =
                new AccessChatInputData(currentUserId, chatId);
        accessChatInputBoundary.execute(accessChatInputData);
    }
}
