package entity.repo;

import entity.User;
import entity.ports.UserRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryUserRepository implements UserRepository {

    private final Map<String, User> users = new HashMap<>();

    @Override
    public Optional<User> findByUsername(String username) {
        return users.values().stream()
                .filter(u -> u.getName().equalsIgnoreCase(username))
                .findFirst();
    }

    @Override
    public User save(User user) {
        users.put(user.getName(), user);
        return user;
    }
}
