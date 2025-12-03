package use_case.messaging.add_reaction;

import entity.Message;
import entity.ports.MessageRepository;
import entity.repo.InMemoryMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import usecase.messaging.add_reaction.*;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AddReactionInteractor - maximum coverage without modifying production code
 *
 * Note: We test the core logic (message repository, presenter interaction).
 * Firebase integration is not tested here since FireBaseUserDataAccessObject
 * requires actual Firebase initialization.
 */
class AddReactionInteractorTest {

    private MessageRepository messageRepository;
    private TestPresenter presenter;
    private AddReactionInputBoundary interactor;

    @BeforeEach
    void setUp() {
        messageRepository = new InMemoryMessageRepository();
        presenter = new TestPresenter();

        // We create interactor with null for Firebase DAO
        // This allows us to test the logic up to the Firebase call
        interactor = new AddReactionInteractor(messageRepository, null, presenter);
    }

    @Test
    void testExecute_Failure_MessageNotFound() {
        // Arrange
        String messageId = "nonexistent";
        String userId = "user1";
        String emoji = "❤️";

        AddReactionInputData inputData = new AddReactionInputData(messageId, userId, emoji);

        // Act
        interactor.execute(inputData);

        // Assert
        assertNotNull(presenter.lastFailError, "Should call prepareFailView");
        assertEquals("Message not found.", presenter.lastFailError);
        assertNull(presenter.lastSuccessOutput, "Should not call prepareSuccessView");
    }

    @Test
    void testExecute_MessageFound_ReactionAddedToEntity() {
        // Arrange
        String messageId = "msg123";
        String userId = "user1";
        String emoji = "❤️";

        Message message = new Message(messageId, "chat1", "user2", null, "Hello", Instant.now());
        messageRepository.save(message);

        AddReactionInputData inputData = new AddReactionInputData(messageId, userId, emoji);

        // Act
        try {
            interactor.execute(inputData);
        } catch (NullPointerException e) {
            // Expected - Firebase DAO is null
        }

        // Assert - Even though Firebase call failed, the message entity should be updated
        Message savedMessage = messageRepository.findById(messageId).orElseThrow();
        assertTrue(savedMessage.getReactions().containsKey(userId),
                "Reaction should be added to message entity");
        assertEquals(emoji, savedMessage.getReactions().get(userId),
                "Correct emoji should be stored");
    }

    @Test
    void testExecute_UpdateExistingReaction() {
        // Arrange
        String messageId = "msg123";
        String userId = "user1";
        String oldEmoji = "❤️";
        String newEmoji = "😂";

        Message message = new Message(messageId, "chat1", "user2", null, "Hello", Instant.now());
        message.addReaction(userId, oldEmoji);
        messageRepository.save(message);

        AddReactionInputData inputData = new AddReactionInputData(messageId, userId, newEmoji);

        // Act
        try {
            interactor.execute(inputData);
        } catch (NullPointerException e) {
            // Expected
        }

        // Assert
        Message savedMessage = messageRepository.findById(messageId).orElseThrow();
        assertEquals(newEmoji, savedMessage.getReactions().get(userId),
                "Old reaction should be replaced with new one");
        assertNotEquals(oldEmoji, savedMessage.getReactions().get(userId));
    }

    @Test
    void testExecute_MultipleUsersReact() {
        // Arrange
        String messageId = "msg123";
        Message message = new Message(messageId, "chat1", "user3", null, "Hello", Instant.now());
        messageRepository.save(message);

        AddReactionInputData input1 = new AddReactionInputData(messageId, "user1", "❤️");
        AddReactionInputData input2 = new AddReactionInputData(messageId, "user2", "👍");

        // Act
        try {
            interactor.execute(input1);
        } catch (NullPointerException e) { /* Expected */ }

        try {
            interactor.execute(input2);
        } catch (NullPointerException e) { /* Expected */ }

        // Assert
        Message savedMessage = messageRepository.findById(messageId).orElseThrow();
        assertEquals(2, savedMessage.getReactions().size(),
                "Should have reactions from 2 users");
        assertEquals("❤️", savedMessage.getReactions().get("user1"));
        assertEquals("👍", savedMessage.getReactions().get("user2"));
    }

    @Test
    void testExecute_EmptyReactionsMapInitially() {
        // Arrange
        String messageId = "msg123";
        Message message = new Message(messageId, "chat1", "user2", null, "Test", Instant.now());
        messageRepository.save(message);

        assertTrue(message.getReactions().isEmpty(), "Should start with no reactions");

        AddReactionInputData inputData = new AddReactionInputData(messageId, "user1", "🔥");

        // Act
        try {
            interactor.execute(inputData);
        } catch (NullPointerException e) { /* Expected */ }

        // Assert
        Message savedMessage = messageRepository.findById(messageId).orElseThrow();
        assertFalse(savedMessage.getReactions().isEmpty());
        assertEquals(1, savedMessage.getReactions().size());
        assertEquals("🔥", savedMessage.getReactions().get("user1"));
    }

    @Test
    void testExecute_MessageSavedToRepository() {
        // Arrange
        String messageId = "msg123";
        String userId = "user1";
        String emoji = "❤️";

        Message message = new Message(messageId, "chat1", "user2", null, "Hello", Instant.now());
        messageRepository.save(message);

        AddReactionInputData inputData = new AddReactionInputData(messageId, userId, emoji);

        // Act
        try {
            interactor.execute(inputData);
        } catch (NullPointerException e) { /* Expected */ }

        // Assert - Message should be saved back to repository with reaction
        Message savedMessage = messageRepository.findById(messageId).orElseThrow();
        assertTrue(savedMessage.getReactions().containsKey(userId));
        assertEquals(emoji, savedMessage.getReactions().get(userId));
    }

    @Test
    void testExecute_SameUserChangesReaction() {
        // Arrange
        String messageId = "msg123";
        String userId = "user1";

        Message message = new Message(messageId, "chat1", "user2", null, "Test", Instant.now());
        messageRepository.save(message);

        // Act - User changes reaction multiple times
        try {
            interactor.execute(new AddReactionInputData(messageId, userId, "❤️"));
        } catch (NullPointerException e) { /* Expected */ }

        try {
            interactor.execute(new AddReactionInputData(messageId, userId, "😂"));
        } catch (NullPointerException e) { /* Expected */ }

        try {
            interactor.execute(new AddReactionInputData(messageId, userId, "🔥"));
        } catch (NullPointerException e) { /* Expected */ }

        // Assert
        Message savedMessage = messageRepository.findById(messageId).orElseThrow();
        assertEquals(1, savedMessage.getReactions().size(),
                "Should still have only 1 reaction (same user)");
        assertEquals("🔥", savedMessage.getReactions().get(userId),
                "Should have the latest emoji");
    }

    @Test
    void testExecute_ReactionAddedToCorrectMessage() {
        // Arrange - Multiple messages
        String messageId1 = "msg1";
        String messageId2 = "msg2";

        Message message1 = new Message(messageId1, "chat1", "user2", null, "Hello", Instant.now());
        Message message2 = new Message(messageId2, "chat1", "user2", null, "World", Instant.now());
        messageRepository.save(message1);
        messageRepository.save(message2);

        AddReactionInputData inputData = new AddReactionInputData(messageId1, "user1", "❤️");

        // Act
        try {
            interactor.execute(inputData);
        } catch (NullPointerException e) { /* Expected */ }

        // Assert - Only message1 should have reaction
        Message savedMessage1 = messageRepository.findById(messageId1).orElseThrow();
        Message savedMessage2 = messageRepository.findById(messageId2).orElseThrow();

        assertTrue(savedMessage1.getReactions().containsKey("user1"),
                "Message 1 should have the reaction");
        assertTrue(savedMessage2.getReactions().isEmpty(),
                "Message 2 should have no reactions");
    }

    @Test
    void testExecute_PresenterNotCalledWhenMessageNotFound() {
        // Arrange
        AddReactionInputData inputData = new AddReactionInputData("nonexistent", "user1", "❤️");

        presenter.lastSuccessOutput = null;
        presenter.lastFailError = null;

        // Act
        interactor.execute(inputData);

        // Assert
        assertNull(presenter.lastSuccessOutput,
                "prepareSuccessView should NOT be called");
        assertNotNull(presenter.lastFailError,
                "prepareFailView SHOULD be called");
    }

    // ------------------ Test Presenter ------------------

    private static class TestPresenter implements AddReactionOutputBoundary {
        AddReactionOutputData lastSuccessOutput;
        String lastFailError;

        @Override
        public void prepareSuccessView(AddReactionOutputData outputData) {
            this.lastSuccessOutput = outputData;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            this.lastFailError = errorMessage;
        }
    }
}
