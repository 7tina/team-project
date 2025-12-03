package use_case.recent_chats;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.awt.*;
import java.time.Instant;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import dataaccess.InMemoryUserDataAccessObject;
import entity.Chat;
import entity.Message;
import entity.User;
import entity.UserFactory;
import entity.ports.ChatRepository;
import entity.ports.MessageRepository;
import entity.ports.UserRepository;
import entity.repo.InMemoryChatRepository;
import entity.repo.InMemoryMessageRepository;
import entity.repo.InMemoryUserRepository;
import usecase.recent_chat.*;

public class RecentChatsInteractorTest {
    private UserFactory userFactory = new UserFactory();

    @Test
    void successTest() {
        final RecentChatsInputData inputData = new RecentChatsInputData("Miles1");
        final RecentChatsUserDataAccessInterface dao = new InMemoryUserDataAccessObject();
        final MessageRepository messageRepository = new InMemoryMessageRepository();
        final UserRepository userRepository = new InMemoryUserRepository();
        final ChatRepository chatRepository = new InMemoryChatRepository();
        final User user1 = userFactory.create("Miles1", "123");
        final User user2 = userFactory.create("Miles2", "456");
        userRepository.save(user1);
        userRepository.save(user2);
        final Instant timestamp = Instant.now();
        final Message message = new Message("1", "test", "Miles1",
                null, "hi", timestamp);
        messageRepository.save(message);
        final Chat chat1 = new Chat("test1", "", Color.BLUE, Instant.now());
        chat1.addParticipant("Miles1");
        chat1.addParticipant("Miles2");
        final Chat chat2 = new Chat("test2", "", Color.BLUE, timestamp);
        chat2.addParticipant("Miles2");
        chat2.addParticipant("Miles1");
        chat2.addMessage("1");
        chatRepository.save(chat1);
        chatRepository.save(chat2);

        final RecentChatsOutputBoundary successPresenter = new RecentChatsOutputBoundary() {
            @Override
            public void prepareSuccessView(RecentChatsOutputData outputData) {
                final List<String> chatNames = outputData.getChatNames();
                final HashMap<String, String> nameToChatIds = outputData.getNameToChatIds();
                assertEquals(2, chatNames.size());
                assertEquals("Miles2", chatNames.get(0));
                assertEquals("Miles2(copy)", chatNames.get(1));
                assertEquals("test1", nameToChatIds.get("Miles2"));
                assertEquals("test2", nameToChatIds.get("Miles2(copy)"));
            }

            @Override
            public void prepareFailView(String error) {
                fail("Use case failure is unexpected.");
            }
            };

        final RecentChatsInputBoundary interactor = new RecentChatsInteractor(successPresenter,
                dao, messageRepository, userRepository, chatRepository);
        interactor.execute(inputData);
    }

    @Test
    void failureExceptionTest() {
        final RecentChatsInputData inputData = new RecentChatsInputData("Miles");
        final RecentChatsUserDataAccessInterface dao = new InMemoryUserDataAccessObject() {
            @Override
            public void updateChatRepository(String username) {
                final Stream<Integer> s = Stream.of(1,2,3);
                s.count();
                s.count();
            }
        };
        final MessageRepository messageRepository = new InMemoryMessageRepository();
        final UserRepository userRepository = new InMemoryUserRepository();
        final ChatRepository chatRepository = new InMemoryChatRepository();
        final User user1 = userFactory.create("Miles1", "123");
        final User user2 = userFactory.create("Miles2", "456");
        userRepository.save(user1);
        userRepository.save(user2);
        final Chat chat = new Chat("test1", "", Color.BLUE, Instant.now());
        chat.addParticipant("Miles1");
        chat.addParticipant("Miles2");
        chatRepository.save(chat);

        // This creates a successPresenter that tests whether the test case is as we expect.
        final RecentChatsOutputBoundary successPresenter = new RecentChatsOutputBoundary() {
            @Override
            public void prepareSuccessView(RecentChatsOutputData outputData) {
                // this should never be reached since the test case should fail
                fail("Use case success is unexpected.");
            }

            @Override
            public void prepareFailView(String error) {
                assertEquals("stream has already been operated upon or closed", error);
            }
        };

        final RecentChatsInputBoundary interactor = new RecentChatsInteractor(successPresenter,
                dao, messageRepository, userRepository, chatRepository);
        interactor.execute(inputData);
    }

    @Test
    void nullTimestampsTest() {
        final RecentChatsInputData inputData = new RecentChatsInputData("Miles1");
        final RecentChatsUserDataAccessInterface dao = new InMemoryUserDataAccessObject();
        final MessageRepository messageRepository = new InMemoryMessageRepository();
        final UserRepository userRepository = new InMemoryUserRepository();
        final ChatRepository chatRepository = new InMemoryChatRepository();
        final User user1 = userFactory.create("Miles1", "123");
        final User user2 = userFactory.create("Miles2", "456");
        final User user3 = userFactory.create("Miles3", "789");
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        final Chat noChat = new Chat("no", "", Color.BLUE, Instant.now());
        noChat.addParticipant("Miles2");
        noChat.addParticipant("Miles3");
        chatRepository.save(noChat);
        final List<Chat> inputChats = new ArrayList<>();
        inputChats.add(new Chat("test1", "test1", Color.BLUE, null));
        inputChats.add(new Chat("test2", "test2", Color.BLUE, null));
        for (Chat chat : inputChats) {
            chat.addParticipant("Miles2");
            chat.addParticipant("Miles1");
            chat.addParticipant("Miles3");
            chatRepository.save(chat);
        }

        // This creates a successPresenter that tests whether the test case is as we expect.
        final RecentChatsOutputBoundary successPresenter = new RecentChatsOutputBoundary() {
            @Override
            public void prepareSuccessView(RecentChatsOutputData outputData) {
                final List<String> chatNames = outputData.getChatNames();
                final HashMap<String, String> nameToChatIds = outputData.getNameToChatIds();
                final List<String> expected = new ArrayList<>(
                        List.of("test2", "test1")
                );

                assertEquals(2, chatNames.size());
                assertEquals(expected, chatNames);
            }

            @Override
            public void prepareFailView(String error) {
                fail("Use case failure is unexpected.");
            }
        };

        final RecentChatsInputBoundary interactor = new RecentChatsInteractor(successPresenter,
                dao, messageRepository, userRepository, chatRepository);
        interactor.execute(inputData);
    }
    @Test
    void nullTimestampLeftTest() {
        final RecentChatsInputData inputData = new RecentChatsInputData("Miles1");
        final RecentChatsUserDataAccessInterface dao = new InMemoryUserDataAccessObject();
        final MessageRepository messageRepository = new InMemoryMessageRepository();
        final UserRepository userRepository = new InMemoryUserRepository();
        final ChatRepository chatRepository = new InMemoryChatRepository();
        final User user1 = userFactory.create("Miles1", "123");
        final User user2 = userFactory.create("Miles2", "456");
        final User user3 = userFactory.create("Miles3", "789");
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        final List<Chat> inputChats = new ArrayList<>();
        inputChats.add(new Chat("test1", "test1", Color.BLUE, Instant.now()));
        inputChats.add(new Chat("test2", "test2", Color.BLUE, null));
        for (Chat chat : inputChats) {
            chat.addParticipant("Miles2");
            chat.addParticipant("Miles1");
            chat.addParticipant("Miles3");
            chatRepository.save(chat);
        }

        // This creates a successPresenter that tests whether the test case is as we expect.
        final RecentChatsOutputBoundary successPresenter = new RecentChatsOutputBoundary() {
            @Override
            public void prepareSuccessView(RecentChatsOutputData outputData) {
                final List<String> chatNames = outputData.getChatNames();
                final HashMap<String, String> nameToChatIds = outputData.getNameToChatIds();
                final List<String> expected = new ArrayList<>(
                        List.of("test1", "test2")
                );

                assertEquals(2, chatNames.size());
                assertEquals(expected, chatNames);
            }

            @Override
            public void prepareFailView(String error) {
                fail("Use case failure is unexpected.");
            }
        };

        final RecentChatsInputBoundary interactor = new RecentChatsInteractor(successPresenter,
                dao, messageRepository, userRepository, chatRepository);
        interactor.execute(inputData);
    }
    @Test
    void nullTimestampsRightTest() {
        final RecentChatsInputData inputData = new RecentChatsInputData("Miles1");
        final RecentChatsUserDataAccessInterface dao = new InMemoryUserDataAccessObject();
        final MessageRepository messageRepository = new InMemoryMessageRepository();
        final UserRepository userRepository = new InMemoryUserRepository();
        final ChatRepository chatRepository = new InMemoryChatRepository();
        final User user1 = userFactory.create("Miles1", "123");
        final User user2 = userFactory.create("Miles2", "456");
        final User user3 = userFactory.create("Miles3", "789");
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        final List<Chat> inputChats = new ArrayList<>();
        inputChats.add(new Chat("test1", "test1", Color.BLUE, null));
        inputChats.add(new Chat("test2", "test2", Color.BLUE, Instant.now()));
        for (Chat chat : inputChats) {
            chat.addParticipant("Miles2");
            chat.addParticipant("Miles1");
            chat.addParticipant("Miles3");
            chatRepository.save(chat);
        }

        // This creates a successPresenter that tests whether the test case is as we expect.
        final RecentChatsOutputBoundary successPresenter = new RecentChatsOutputBoundary() {
            @Override
            public void prepareSuccessView(RecentChatsOutputData outputData) {
                final List<String> chatNames = outputData.getChatNames();
                final HashMap<String, String> nameToChatIds = outputData.getNameToChatIds();
                final List<String> expected = new ArrayList<>(
                        List.of("test2", "test1")
                );

                assertEquals(2, chatNames.size());
                assertEquals(expected, chatNames);
            }

            @Override
            public void prepareFailView(String error) {
                fail("Use case failure is unexpected.");
            }
        };

        final RecentChatsInputBoundary interactor = new RecentChatsInteractor(successPresenter,
                dao, messageRepository, userRepository, chatRepository);
        interactor.execute(inputData);
    }
}
