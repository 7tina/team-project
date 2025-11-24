package interface_adapter.groupchat;

import use_case.groups.RemoveUserInputBoundary;
import use_case.groups.RemoveUserInputData;

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