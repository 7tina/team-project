package usecase.search_user;

public interface SearchUserOutputBoundary {
    void prepareSuccessView(SearchUserOutputData outputData);
    void prepareFailView(String error);
}