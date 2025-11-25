package use_case.groups;

public interface AddUserOutputBoundary {
    void prepareSuccessView(AddUserOutputData outputData);
    void prepareFailView(String error);
}