package use_case.search_user;

public class SearchUserInputData {
    private final String query;
    private final String currentUsername;

    public SearchUserInputData(String query, String currentUsername) {
        this.query = query;
        this.currentUsername = currentUsername;
    }

    public String getQuery() {
        return query;
    }

    public String getCurrentUsername() {
        return currentUsername;
    }
}