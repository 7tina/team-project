package interface_adapter.groupchat.removeuser;

import use_case.groups.removeuser.RemoveUserInputBoundary;
import use_case.groups.removeuser.RemoveUserInputData;

public class RemoveUserController {
    private final RemoveUserInputBoundary removeUserInteractor;

    public RemoveUserController(RemoveUserInputBoundary removeUserInteractor) {
        this.removeUserInteractor = removeUserInteractor;
    }

    public void execute(String chatId, String usernameToRemove) {
        RemoveUserInputData inputData = new RemoveUserInputData(chatId, usernameToRemove);
        removeUserInteractor.execute(inputData);
    }
}