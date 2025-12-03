package use_case.accesschat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import dataaccess.InMemoryUserDataAccessObject;
import entity.Chat;
import entity.User;
import entity.UserFactory;
import entity.ports.ChatRepository;
import entity.ports.UserRepository;
import entity.repo.InMemoryChatRepository;
import entity.repo.InMemoryUserRepository;

import usecase.accesschat.*;

public class AccessChatInteractorTest {
    private UserFactory userFactory = new UserFactory();

    @Test
    void successTestTwoParticipants() {
        final AccessChatInputData inputData = new AccessChatInputData("Miles1", "chat1");
        final AccessChatDataAccessInterface dao = new InMemoryUserDataAccessObject();
        final UserRepository userRepository = new InMemoryUserRepository();
        final ChatRepository chatRepository = new InMemoryChatRepository();
        final User user1 = userFactory.create("Miles1", "123");
        final User user2 = userFactory.create("Miles2", "456");
        userRepository.save(user1);
        userRepository.save(user2);
        final Chat chat1 = new Chat("chat1", "", Color.BLUE, Instant.now());
        chat1.addParticipant("Miles1");
        chat1.addParticipant("Miles2");
        chatRepository.save(chat1);

        // This creates a successPresenter that tests whether the test case is as we expect.
        final AccessChatOutputBoundary successPresenter = new AccessChatOutputBoundary() {
            @Override
            public void prepareSuccessView(AccessChatOutputData outputData) {
                assertEquals(false, outputData.isGroupChat());
                assertEquals("chat1", outputData.getChatId());
                assertEquals("", outputData.getGroupName());
                assertEquals(new ArrayList<>(List.of("Miles1", "Miles2")), outputData.getUsers());
                assertEquals(new ArrayList<>(), outputData.getMessageIds());
                assertEquals("Miles1", outputData.getCurrentUserId());
            }

            @Override
            public void prepareFailView(String error) {
                fail("Use case failure is unexpected.");
            }
        };

        final AccessChatInputBoundary interactor = new AccessChatInteractor(dao, successPresenter,
                userRepository, chatRepository);
        interactor.execute(inputData);
    }

    @Test
    void groupChatSuccessTest() {
        final AccessChatInputData inputData = new AccessChatInputData("Miles1", "chat1");
        final AccessChatDataAccessInterface dao = new InMemoryUserDataAccessObject();
        final UserRepository userRepository = new InMemoryUserRepository();
        final ChatRepository chatRepository = new InMemoryChatRepository();
        final User user1 = userFactory.create("Miles1", "123");
        final User user2 = userFactory.create("Miles2", "456");
        final User user3 = userFactory.create("Miles3", "789");
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        final Chat chat1 = new Chat("chat1", "groupChat", Color.BLUE, Instant.now());
        chat1.addParticipant("Miles1");
        chat1.addParticipant("Miles2");
        chat1.addParticipant("Miles3");
        chatRepository.save(chat1);

        // This creates a successPresenter that tests whether the test case is as we expect.
        final AccessChatOutputBoundary successPresenter = new AccessChatOutputBoundary() {
            @Override
            public void prepareSuccessView(AccessChatOutputData outputData) {
                assertEquals(true, outputData.isGroupChat());
                assertEquals("chat1", outputData.getChatId());
                assertEquals("groupChat", outputData.getGroupName());
                assertEquals(new ArrayList<>(List.of("Miles1", "Miles2", "Miles3")), outputData.getUsers());
                assertEquals(new ArrayList<>(), outputData.getMessageIds());
                assertEquals("Miles1", outputData.getCurrentUserId());
            }

            @Override
            public void prepareFailView(String error) {
                fail("Use case failure is unexpected.");
            }
        };

        final AccessChatInputBoundary interactor = new AccessChatInteractor(dao, successPresenter,
                userRepository, chatRepository);
        interactor.execute(inputData);
    }

    @Test
    void failureUserNotFoundTest() {
        final AccessChatInputData inputData = new AccessChatInputData("Miles1", "chat1");
        final AccessChatDataAccessInterface dao = new InMemoryUserDataAccessObject();
        final UserRepository userRepository = new InMemoryUserRepository();
        final ChatRepository chatRepository = new InMemoryChatRepository();
        final User user = userFactory.create("Miles2", "456");
        userRepository.save(user);
        final Chat chat1 = new Chat("chat1", "", Color.BLUE, Instant.now());
        chat1.addParticipant("Miles1");
        chat1.addParticipant("Miles2");
        chatRepository.save(chat1);

        // This creates a successPresenter that tests whether the test case is as we expect.
        final AccessChatOutputBoundary successPresenter = new AccessChatOutputBoundary() {
            @Override
            public void prepareSuccessView(AccessChatOutputData outputData) {
                // this should never be reached since the test case should fail
                fail("Use case success is unexpected.");
            }

            @Override
            public void prepareFailView(String error) {
                assertEquals("Session error. Please log in again.", error);
            }
        };

        final AccessChatInputBoundary interactor = new AccessChatInteractor(dao, successPresenter,
                userRepository, chatRepository);
        interactor.execute(inputData);
    }

    @Test
    void failureChatNotFoundTest() {
        final AccessChatInputData inputData = new AccessChatInputData("Miles1", "chat2");
        final AccessChatDataAccessInterface dao = new InMemoryUserDataAccessObject();
        final UserRepository userRepository = new InMemoryUserRepository();
        final ChatRepository chatRepository = new InMemoryChatRepository();
        final User user1 = userFactory.create("Miles1", "123");
        final User user2 = userFactory.create("Miles2", "456");
        userRepository.save(user1);
        userRepository.save(user2);
        final Chat chat1 = new Chat("chat1", "", Color.BLUE, Instant.now());
        chat1.addParticipant("Miles1");
        chat1.addParticipant("Miles2");
        chatRepository.save(chat1);

        // This creates a successPresenter that tests whether the test case is as we expect.
        final AccessChatOutputBoundary successPresenter = new AccessChatOutputBoundary() {
            @Override
            public void prepareSuccessView(AccessChatOutputData outputData) {
                // this should never be reached since the test case should fail
                fail("Use case success is unexpected.");
            }

            @Override
            public void prepareFailView(String error) {
                assertEquals("Chat not found.", error);
            }
        };

        final AccessChatInputBoundary interactor = new AccessChatInteractor(dao, successPresenter,
                userRepository, chatRepository);
        interactor.execute(inputData);
    }

    @Test
    void testInputDataGetters() {
        final AccessChatInputData inputData = new AccessChatInputData("user123", "chat456");
        assertEquals("user123", inputData.getUserId());
        assertEquals("chat456", inputData.getChatId());
    }

    @Test
    void testOutputDataGetters() {
        final List<String> users = new ArrayList<>(List.of("user1", "user2"));
        final List<String> messageIds = new ArrayList<>(List.of("msg1", "msg2"));
        final AccessChatOutputData outputData = new AccessChatOutputData(
                true, "chat123", "Test Group", users, messageIds, "user1"
        );

        assertEquals(true, outputData.isGroupChat());
        assertEquals("chat123", outputData.getChatId());
        assertEquals("Test Group", outputData.getGroupName());
        assertEquals(users, outputData.getUsers());
        assertEquals(messageIds, outputData.getMessageIds());
        assertEquals("user1", outputData.getCurrentUserId());
    }
}