package usecase.groups.removeuser;

public interface RemoveUserOutputBoundary {
    void prepareSuccessView(RemoveUserOutputData outputData);
    void prepareFailView(String error);
}