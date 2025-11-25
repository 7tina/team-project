package use_case.groups;

public interface RemoveUserOutputBoundary {
    void prepareSuccessView(RemoveUserOutputData outputData);
    void prepareFailView(String error);
}