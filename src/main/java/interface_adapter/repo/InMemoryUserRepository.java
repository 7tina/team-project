package interface_adapter.repo;

import entity.Chat;
import goc.chat.entity.User;
import use_case.ports.UserRepository;
import use_case.search_user.SearchUserDataAccessInterface;  // Add this import

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryUserRepository implements UserRepository, SearchUserDataAccessInterface {  // Add SearchUserDataAccessInterface

    private final Map<String, User> users = new HashMap<>();
    private final Map<String, User> usersByUsername = new HashMap<>();

    @Override
    public User save(User user) {
        users.put(user.getId(), user);
        usersByUsername.put(user.getUsername(), user);
        return user;
    }

    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        if (username == null) {
            return Optional.empty();
        }

        String trimmed = username.trim();

        // Try exact match first (fast path)
        User user = usersByUsername.get(trimmed);
        if (user != null) {
            return Optional.of(user);
        }

        // Fall back to case-insensitive search
        return usersByUsername.entrySet().stream()
                .filter(entry -> entry.getKey().equalsIgnoreCase(trimmed))
                .map(Map.Entry::getValue)
                .findFirst();
    }

    @Override
    public List<String> searchUsers(String query) {
        if (query == null || query.trim().isEmpty()) {
            // Return all usernames if query is empty
            return new ArrayList<>(usersByUsername.keySet());
        }

        // Return usernames that contain the query (case-insensitive)
        String lowerQuery = query.toLowerCase();
        return usersByUsername.keySet().stream()
                .filter(username -> username.toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());
    }
}