package interfaceadapter.search_user;

import java.util.List;

public class SearchUserState {
    private List<String> searchResults;
    private String searchError = "";
    private String createError = "";

    // Default constructor
    public SearchUserState() {

    }

    // Getters
    public List<String> getSearchResults() {
        return searchResults;
    }

    public String getSearchError() {

        return searchError;
    }

    public String getCreateError() {

        return createError;
    }

    // Setters
    public void setSearchResults(List<String> searchResults) {
        this.searchResults = searchResults;
    }

    public void setSearchError(String searchError) {

        this.searchError = searchError;
    }

    public void setCreateError(String createError) {

        this.createError = createError;
    }
}
