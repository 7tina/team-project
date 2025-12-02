package usecase.groups.changegroupname;

public interface ChangeGroupNameOutputBoundary {
    void prepareSuccessView(ChangeGroupNameOutputData outputData);
    void prepareFailView(ChangeGroupNameOutputData outputData);
}