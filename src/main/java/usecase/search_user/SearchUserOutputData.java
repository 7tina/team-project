package usecase.search_user;

import java.util.List;

public class SearchUserOutputData {
    private final List<String> usernames;

    public SearchUserOutputData(List<String> usernames) {
        this.usernames = usernames;
    }

    public List<String> getUsernames() {
        return usernames;
    }
}
