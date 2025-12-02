package interfaceadapter.groupchat.removeuser;

import usecase.groups.removeuser.RemoveUserInputBoundary;
import usecase.groups.removeuser.RemoveUserInputData;

public class RemoveUserController {
    private final RemoveUserInputBoundary removeUserInteractor;

    public RemoveUserController(RemoveUserInputBoundary removeUserInteractor) {
        this.removeUserInteractor = removeUserInteractor;
    }

    /**
     * Removes a user from a specified chat by delegating the operation to the interactor.
     * @param chatId the ID of the chat from which the user should be removed
     * @param usernameToRemove the username of the user to remove from the chat
     */
    public void execute(String chatId, String usernameToRemove) {
        final RemoveUserInputData inputData = new RemoveUserInputData(chatId, usernameToRemove);
        removeUserInteractor.execute(inputData);
    }
}
