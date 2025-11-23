package use_case.search_user;

public class SearchUserInputData {
    private final String user;
    private final String query;

    public SearchUserInputData(String user, String query) {
        this.user = user;
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public String getUser() { return user; }
}