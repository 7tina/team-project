package interfaceadapter.search_user;

import usecase.search_user.SearchUserOutputBoundary;
import usecase.search_user.SearchUserOutputData;

public class SearchUserPresenter implements SearchUserOutputBoundary {
    private final SearchUserViewModel userSearchViewModel;

    public SearchUserPresenter(SearchUserViewModel userSearchViewModel) {
        this.userSearchViewModel = userSearchViewModel;
    }

    @Override
    public void prepareSuccessView(SearchUserOutputData outputData) {
        final SearchUserState state = userSearchViewModel.getState();
        state.setSearchResults(outputData.getUsernames());
        state.setSearchError(null);
        // Signal the view to update the list
        userSearchViewModel.firePropertyChanged();
    }

    @Override
    public void prepareFailView(String error) {
        final SearchUserState state = userSearchViewModel.getState();
        state.setSearchResults(null);
        state.setSearchError(error);
        // Signal the view to display the error
        userSearchViewModel.firePropertyChanged();
    }
}
