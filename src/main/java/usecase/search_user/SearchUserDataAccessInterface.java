package usecase.search_user;

import java.util.List;

public interface SearchUserDataAccessInterface {
    /**
     * Finds users whose usernames contain the given query string.
     * @param userId The userID string.
     * @param query The search string.
     * @return A list of matching usernames (String).
     */
    List<String> searchUsers(String userId, String query);
}
