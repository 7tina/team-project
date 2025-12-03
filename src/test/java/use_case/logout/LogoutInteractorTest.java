package use_case.logout;

import dataaccess.InMemoryUserDataAccessObject;
import entity.UserFactory;
import entity.User;
import org.junit.jupiter.api.Test;
import usecase.logout.LogoutInputBoundary;
import usecase.logout.LogoutInteractor;
import usecase.logout.LogoutOutputBoundary;
import usecase.logout.LogoutOutputData;
import entity.ports.ChatRepository;
import entity.repo.InMemoryChatRepository;

import static org.junit.jupiter.api.Assertions.*;

class LogoutInteractorTest {

    @Test
    void successTest() {
        InMemoryUserDataAccessObject userRepository = new InMemoryUserDataAccessObject();

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

        ChatRepository chatRepo = new InMemoryChatRepository();

        LogoutInputBoundary interactor =
                new LogoutInteractor(userRepository, successPresenter, chatRepo);
        interactor.execute();
        assertNull(userRepository.getCurrentUsername());
    }

}