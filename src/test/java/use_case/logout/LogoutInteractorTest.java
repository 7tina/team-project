package use_case.logout;

import dataaccess.InMemoryUserDataAccessObject;
import entity.UserFactory;
import entity.User;
import entity.Chat;
import entity.ports.ChatRepository;
import org.junit.jupiter.api.Test;
import usecase.logout.LogoutInputBoundary;
import usecase.logout.LogoutInteractor;
import usecase.logout.LogoutOutputBoundary;
import usecase.logout.LogoutOutputData;
import entity.ports.ChatRepository;
import entity.repo.InMemoryChatRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;

class LogoutInteractorTest {

    @Test
    void successTest() {
        InMemoryUserDataAccessObject userRepository = new InMemoryUserDataAccessObject();
        FakeChatRepository chatRepository = new FakeChatRepository();

        // For the success test, we need to add Paul to the data access repository before we log in.
        UserFactory factory = new UserFactory();
        User user = factory.create("Paul", "password");
        userRepository.save(user);
        userRepository.setCurrentUsername("Paul");

        // This creates a successPresenter that tests whether the test case is as we expect.
        LogoutOutputBoundary successPresenter = new LogoutOutputBoundary() {
            @Override
            public void prepareSuccessView(LogoutOutputData user) {
                assertEquals("Paul", user.getUsername());
                assertNull(userRepository.getCurrentUsername());
            }
        };

        LogoutInputBoundary interactor = new LogoutInteractor(userRepository, successPresenter, chatRepository);
        interactor.execute();
        assertNull(userRepository.getCurrentUsername());
    }

    // Fake ChatRepository for testing
    static class FakeChatRepository implements ChatRepository {
        private final Map<String, Chat> chats = new HashMap<>();

        @Override
        public Chat save(Chat chat) {
            chats.put(chat.getId(), chat);
            return chat;
        }

        @Override
        public Optional<Chat> findById(String chatId) {
            return Optional.ofNullable(chats.get(chatId));
        }

        @Override
        public List<Chat> findAll() {
            return new ArrayList<>(chats.values());
        }

        @Override
        public void clear() {
            chats.clear();
        }
    }
}