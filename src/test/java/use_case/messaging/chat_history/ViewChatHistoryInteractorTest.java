package use_case.messaging.chat_history;

import entity.Chat;
import entity.Message;
import entity.ports.ChatRepository;
import entity.ports.MessageRepository;
import entity.ports.UserRepository;
import entity.repo.InMemoryChatRepository;
import entity.repo.InMemoryMessageRepository;
import entity.repo.InMemoryUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import usecase.messaging.view_history.ViewChatHistoryDataAccessInterface;
import usecase.messaging.view_history.ViewChatHistoryInputData;
import usecase.messaging.view_history.ViewChatHistoryInteractor;
import usecase.messaging.view_history.ViewChatHistoryOutputBoundary;
import usecase.messaging.view_history.ViewChatHistoryOutputData;

import java.awt.Color;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link ViewChatHistoryInteractor}.
 *
 * NOTE: 这一版只追求 “先能编译 + 跑过”，
 * Checkstyle 的 import 顺序 / final 之类可以之后再慢慢修。
 */
public class ViewChatHistoryInteractorTest {

    private static final String CHAT_WITH_MESSAGES_ID = "chat-with-messages";
    private static final String EMPTY_CHAT_ID = "empty-chat";
    private static final String UNKNOWN_CHAT_ID = "unknown-chat";

    private ChatRepository chatRepository;
    private MessageRepository messageRepository;
    private UserRepository userRepository;

    private ViewChatHistoryInteractor interactor;
    private CapturingPresenter presenter;

    @BeforeEach
    void setUp() {
        chatRepository = new InMemoryChatRepository();
        messageRepository = new InMemoryMessageRepository();
        userRepository = new InMemoryUserRepository();
        presenter = new CapturingPresenter();

        // 先往内存仓库里塞好数据
        populateChatsAndMessages();

        // dataAccess stub：Interactor 里会调，但这里我们什么也不做
        ViewChatHistoryDataAccessInterface dataAccessStub =
                new ViewChatHistoryDataAccessInterface() {
                    @Override
                    public void findChatMessages(String chatId,
                                                 List<String> userIds,
                                                 List<String> messageIds) {
                        // no-op
                    }
                };

        // ⚠️ 构造函数参数顺序必须和 Interactor 里完全一致
        interactor = new ViewChatHistoryInteractor(
                chatRepository,
                messageRepository,
                userRepository,
                presenter,
                dataAccessStub
        );
    }

    @Test
    void successChatHasMessages() {
        // userIds / messageIds 只是给 dataAccessStub 用，这里随便给一个合理的 List
        List<String> userIds = new ArrayList<>();
        userIds.add("u1");
        userIds.add("u2");

        List<String> messageIds = new ArrayList<>();
        messageIds.add("m1");
        messageIds.add("m2");

        ViewChatHistoryInputData inputData =
                new ViewChatHistoryInputData(CHAT_WITH_MESSAGES_ID, userIds, messageIds);

        interactor.execute(inputData);

        assertTrue(presenter.successCalled, "Should have called prepareSuccessView");
        assertFalse(presenter.failCalled, "Should NOT have called prepareFailView");
        assertNotNull(presenter.outputData, "Output data should not be null");

        List<String[]> messages = presenter.outputData.getMessages();
        assertEquals(2, messages.size(), "Should return 2 messages");

        // Interactor 里按时间从旧到新排序，因此 m1 在前面
        assertEquals("hello", messages.get(0)[2]);
        assertEquals("hi", messages.get(1)[2]);

        // reactions 目前没塞，应该是空
        Map<String, Map<String, String>> reactions = presenter.outputData.getReactions();
        assertTrue(reactions.isEmpty());
    }

    @Test
    void failNoMessagesInChat() {
        List<String> userIds = new ArrayList<>();
        userIds.add("u1");

        List<String> messageIds = new ArrayList<>();

        ViewChatHistoryInputData inputData =
                new ViewChatHistoryInputData(EMPTY_CHAT_ID, userIds, messageIds);

        interactor.execute(inputData);

        assertFalse(presenter.successCalled);
        assertTrue(presenter.failCalled, "Should call fail view when chat is empty");
        assertEquals("No messages in this chat: " + EMPTY_CHAT_ID, presenter.errorMessage);
    }

    @Test
    void failChatNotFound() {
        List<String> userIds = new ArrayList<>();
        List<String> messageIds = new ArrayList<>();

        ViewChatHistoryInputData inputData =
                new ViewChatHistoryInputData(UNKNOWN_CHAT_ID, userIds, messageIds);

        interactor.execute(inputData);

        assertFalse(presenter.successCalled);
        assertTrue(presenter.failCalled, "Should call fail view when chat not found");
        assertTrue(presenter.errorMessage.contains("Chat not found: " + UNKNOWN_CHAT_ID));
    }

    /**
     * 在内存仓库里构造：
     * - 一个有两条消息的 chat
     * - 一个没有消息的 chat
     */
    private void populateChatsAndMessages() {
        Instant t1 = Instant.parse("2024-01-01T10:00:00Z");
        Instant t2 = Instant.parse("2024-01-01T10:00:10Z");

        // 有消息的 chat
        Chat chatWithMessages = new Chat(
                CHAT_WITH_MESSAGES_ID,
                "Project Group",
                Color.BLUE,
                t2
        );
        chatWithMessages.getParticipantUserIds().add("u1");
        chatWithMessages.getParticipantUserIds().add("u2");
        chatWithMessages.getMessageIds().add("m1");
        chatWithMessages.getMessageIds().add("m2");
        chatRepository.save(chatWithMessages);

        // 空 chat（只有成员，没有 message）
        Chat emptyChat = new Chat(
                EMPTY_CHAT_ID,
                "Empty Group",
                Color.GRAY,
                t1
        );
        emptyChat.getParticipantUserIds().add("u1");
        chatRepository.save(emptyChat);

        // ====== 这里根据你的 Message 构造函数来改 ======
        // 如果 Message 的构造函数是：
        //   Message(String id, String chatId, String senderId, String content, Instant timestamp)
        // 就用下面这一版（5 个参数）：
        Message m1 = new Message("m1", CHAT_WITH_MESSAGES_ID, "u1", null, "hello", t1);
        Message m2 = new Message("m2", CHAT_WITH_MESSAGES_ID, "u2", null, "hi", t2);

        // 如果你的 Message 是 6 个参数（多一个 repliedMessageId），
        // 把上面两行改成：
        // Message m1 = new Message("m1", CHAT_WITH_MESSAGES_ID, "u1", "hello", t1, null);
        // Message m2 = new Message("m2", CHAT_WITH_MESSAGES_ID, "u2", "hi", t2, null);
        // （按你项目里真实的签名来）

        messageRepository.save(m1);
        messageRepository.save(m2);
    }

    /**
     * 捕获 Interactor 调用情况的 presenter stub。
     */
    private static final class CapturingPresenter
            implements ViewChatHistoryOutputBoundary {

        boolean successCalled;
        boolean failCalled;
        boolean noMessagesCalled;

        ViewChatHistoryOutputData outputData;
        String errorMessage;
        String noMessagesMessage;

        @Override
        public void prepareSuccessView(ViewChatHistoryOutputData outputData) {
            this.successCalled = true;
            this.outputData = outputData;
        }

        @Override
        public void prepareNoMessagesView(String chatId) {
            this.noMessagesCalled = true;
            this.noMessagesMessage = chatId;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            this.failCalled = true;
            this.errorMessage = errorMessage;
        }
    }
}
