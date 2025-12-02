package interfaceadapter.search_user;

import java.util.List;

public class SearchUserState {
    private List<String> searchResults = null;
    private String searchError = null;

    // Default constructor
    public SearchUserState() {}

    // Getters
    public List<String> getSearchResults() {
        return searchResults;
    }

    public String getSearchError() {
        return searchError;
    }

    // Setters
    public void setSearchResults(List<String> searchResults) {
        this.searchResults = searchResults;
    }

    public void setSearchError(String searchError) {
        this.searchError = searchError;
    }
}