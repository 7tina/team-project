package interfaceadapter.groupchat.removeuser;

import usecase.groups.removeuser.RemoveUserInputBoundary;
import usecase.groups.removeuser.RemoveUserInputData;

public class RemoveUserController {
    private final RemoveUserInputBoundary removeUserInteractor;

    public RemoveUserController(RemoveUserInputBoundary removeUserInteractor) {
        this.removeUserInteractor = removeUserInteractor;
    }

    public void execute(String chatId, String usernameToRemove, String currentUserId) {
        final RemoveUserInputData inputData = new RemoveUserInputData(
                chatId, usernameToRemove, currentUserId);
        removeUserInteractor.execute(inputData);
    }
}