package interface_adapter.search_user;

import use_case.search_user.SearchUserOutputBoundary;
import use_case.search_user.SearchUserOutputData;

public class SearchUserPresenter implements SearchUserOutputBoundary {
    private final SearchUserViewModel userSearchViewModel;

    public SearchUserPresenter(SearchUserViewModel userSearchViewModel) {
        this.userSearchViewModel = userSearchViewModel;
    }

    @Override
    public void prepareSuccessView(SearchUserOutputData outputData) {
        SearchUserState state = userSearchViewModel.getState();
        state.setSearchResults(outputData.getUsernames());
        state.setSearchError(null);
        userSearchViewModel.firePropertyChanged(); // Signal the view to update the list
    }

    @Override
    public void prepareFailView(String error) {
        SearchUserState state = userSearchViewModel.getState();
        state.setSearchResults(null);
        state.setSearchError(error);
        userSearchViewModel.firePropertyChanged(); // Signal the view to display the error
    }
}