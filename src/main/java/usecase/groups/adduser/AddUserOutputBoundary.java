package usecase.groups.adduser;

public interface AddUserOutputBoundary {
    void prepareSuccessView(AddUserOutputData outputData);
    void prepareFailView(String error);
}