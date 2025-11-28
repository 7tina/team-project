package use_case.groups.changegroupname;

public interface ChangeGroupNameOutputBoundary {
    void prepareSuccessView(ChangeGroupNameOutputData outputData);
    void prepareFailView(ChangeGroupNameOutputData outputData);
}