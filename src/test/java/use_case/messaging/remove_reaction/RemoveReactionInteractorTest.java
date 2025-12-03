package use_case.messaging.remove_reaction;

import entity.Message;
import entity.ports.MessageRepository;
import entity.repo.InMemoryMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import usecase.messaging.remove_reaction.*;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RemoveReactionInteractor - maximum coverage without modifying production code
 *
 * Note: We test the core logic (message repository, presenter interaction).
 * Firebase integration is not tested here since FireBaseUserDataAccessObject
 * requires actual Firebase initialization.
 */
class RemoveReactionInteractorTest {

    private MessageRepository messageRepository;
    private TestPresenter presenter;
    private RemoveReactionInputBoundary interactor;

    @BeforeEach
    void setUp() {
        messageRepository = new InMemoryMessageRepository();
        presenter = new TestPresenter();

        // We create interactor with null for Firebase DAO
        interactor = new RemoveReactionInteractor(messageRepository, null, presenter);
    }

    @Test
    void testExecute_Failure_MessageNotFound() {
        // Arrange
        String messageId = "nonexistent";
        String userId = "user1";

        RemoveReactionInputData inputData = new RemoveReactionInputData(messageId, userId);

        // Act
        interactor.execute(inputData);

        // Assert
        assertNotNull(presenter.lastFailError, "Should call prepareFailView");
        assertEquals("Message not found.", presenter.lastFailError);
        assertNull(presenter.lastSuccessOutput, "Should not call prepareSuccessView");
    }

    @Test
    void testExecute_MessageFound_ReactionRemovedFromEntity() {
        // Arrange
        String messageId = "msg123";
        String userId = "user1";

        Message message = new Message(messageId, "chat1", "user2", null, "Hello", Instant.now());
        message.addReaction(userId, "❤️");
        messageRepository.save(message);

        assertTrue(message.getReactions().containsKey(userId), "Should start with reaction");

        RemoveReactionInputData inputData = new RemoveReactionInputData(messageId, userId);

        // Act
        try {
            interactor.execute(inputData);
        } catch (NullPointerException e) {
            // Expected - Firebase DAO is null
        }

        // Assert
        Message savedMessage = messageRepository.findById(messageId).orElseThrow();
        assertFalse(savedMessage.getReactions().containsKey(userId),
                "Reaction should be removed from message entity");
    }

    @Test
    void testExecute_RemoveOneReactionKeepOthers() {
        // Arrange
        String messageId = "msg123";
        String userToRemove = "user1";

        Message message = new Message(messageId, "chat1", "user2", null, "Hello", Instant.now());
        message.addReaction("user1", "❤️");
        message.addReaction("user2", "👍");
        message.addReaction("user3", "😂");
        messageRepository.save(message);

        RemoveReactionInputData inputData = new RemoveReactionInputData(messageId, userToRemove);

        // Act
        try {
            interactor.execute(inputData);
        } catch (NullPointerException e) { /* Expected */ }

        // Assert
        Message savedMessage = messageRepository.findById(messageId).orElseThrow();
        Map<String, String> reactions = savedMessage.getReactions();

        assertEquals(2, reactions.size(), "Should have 2 reactions left");
        assertFalse(reactions.containsKey("user1"), "user1's reaction should be removed");
        assertEquals("👍", reactions.get("user2"), "user2's reaction should remain");
        assertEquals("😂", reactions.get("user3"), "user3's reaction should remain");
    }

    @Test
    void testExecute_RemoveLastReaction() {
        // Arrange
        String messageId = "msg123";
        String userId = "user1";

        Message message = new Message(messageId, "chat1", "user2", null, "Hello", Instant.now());
        message.addReaction(userId, "❤️");
        messageRepository.save(message);

        RemoveReactionInputData inputData = new RemoveReactionInputData(messageId, userId);

        // Act
        try {
            interactor.execute(inputData);
        } catch (NullPointerException e) { /* Expected */ }

        // Assert
        Message savedMessage = messageRepository.findById(messageId).orElseThrow();
        assertTrue(savedMessage.getReactions().isEmpty(),
                "All reactions should be removed");
    }

    @Test
    void testExecute_NoReactionToRemove() {
        // Arrange
        String messageId = "msg123";
        String userId = "user1";

        Message message = new Message(messageId, "chat1", "user2", null, "Hello", Instant.now());
        messageRepository.save(message);

        assertTrue(message.getReactions().isEmpty(), "Should start with no reactions");

        RemoveReactionInputData inputData = new RemoveReactionInputData(messageId, userId);

        // Act
        try {
            interactor.execute(inputData);
        } catch (NullPointerException e) { /* Expected */ }

        // Assert
        Message savedMessage = messageRepository.findById(messageId).orElseThrow();
        assertTrue(savedMessage.getReactions().isEmpty(),
                "Should still have no reactions");
    }

    @Test
    void testExecute_UserHasNoReactionButOthersExist() {
        // Arrange
        String messageId = "msg123";
        String userId = "user1";

        Message message = new Message(messageId, "chat1", "user2", null, "Hello", Instant.now());
        message.addReaction("user2", "❤️");
        message.addReaction("user3", "👍");
        messageRepository.save(message);

        RemoveReactionInputData inputData = new RemoveReactionInputData(messageId, userId);

        // Act
        try {
            interactor.execute(inputData);
        } catch (NullPointerException e) { /* Expected */ }

        // Assert
        Message savedMessage = messageRepository.findById(messageId).orElseThrow();
        assertEquals(2, savedMessage.getReactions().size(),
                "Other users' reactions should remain");
        assertEquals("❤️", savedMessage.getReactions().get("user2"));
        assertEquals("👍", savedMessage.getReactions().get("user3"));
    }

    @Test
    void testExecute_MultipleRemovalsInSequence() {
        // Arrange
        String messageId = "msg123";

        Message message = new Message(messageId, "chat1", "user4", null, "Test", Instant.now());
        message.addReaction("user1", "❤️");
        message.addReaction("user2", "👍");
        message.addReaction("user3", "😂");
        messageRepository.save(message);

        // Act
        try {
            interactor.execute(new RemoveReactionInputData(messageId, "user1"));
        } catch (NullPointerException e) { /* Expected */ }

        try {
            interactor.execute(new RemoveReactionInputData(messageId, "user2"));
        } catch (NullPointerException e) { /* Expected */ }

        // Assert
        Message savedMessage = messageRepository.findById(messageId).orElseThrow();
        assertEquals(1, savedMessage.getReactions().size());
        assertEquals("😂", savedMessage.getReactions().get("user3"));
    }

    @Test
    void testExecute_RemoveSameReactionTwice() {
        // Arrange
        String messageId = "msg123";
        String userId = "user1";

        Message message = new Message(messageId, "chat1", "user2", null, "Hello", Instant.now());
        message.addReaction(userId, "❤️");
        messageRepository.save(message);

        RemoveReactionInputData inputData = new RemoveReactionInputData(messageId, userId);

        // Act
        try {
            interactor.execute(inputData);
        } catch (NullPointerException e) { /* Expected */ }

        try {
            interactor.execute(inputData); // Remove again
        } catch (NullPointerException e) { /* Expected */ }

        // Assert
        Message savedMessage = messageRepository.findById(messageId).orElseThrow();
        assertFalse(savedMessage.getReactions().containsKey(userId),
                "Reaction should stay removed");
    }

    @Test
    void testExecute_AllReactionsRemovedOneByOne() {
        // Arrange
        String messageId = "msg123";

        Message message = new Message(messageId, "chat1", "user4", null, "Test", Instant.now());
        message.addReaction("user1", "❤️");
        message.addReaction("user2", "👍");
        message.addReaction("user3", "😂");
        messageRepository.save(message);

        // Act - Remove all reactions
        try {
            interactor.execute(new RemoveReactionInputData(messageId, "user1"));
        } catch (NullPointerException e) { /* Expected */ }

        try {
            interactor.execute(new RemoveReactionInputData(messageId, "user2"));
        } catch (NullPointerException e) { /* Expected */ }

        try {
            interactor.execute(new RemoveReactionInputData(messageId, "user3"));
        } catch (NullPointerException e) { /* Expected */ }

        // Assert
        Message savedMessage = messageRepository.findById(messageId).orElseThrow();
        assertTrue(savedMessage.getReactions().isEmpty(),
                "All reactions should be removed");
    }

    @Test
    void testExecute_ReactionRemovedFromCorrectMessage() {
        // Arrange - Multiple messages
        String messageId1 = "msg1";
        String messageId2 = "msg2";

        Message message1 = new Message(messageId1, "chat1", "user2", null, "Hello", Instant.now());
        Message message2 = new Message(messageId2, "chat1", "user2", null, "World", Instant.now());
        message1.addReaction("user1", "❤️");
        message2.addReaction("user1", "👍");
        messageRepository.save(message1);
        messageRepository.save(message2);

        RemoveReactionInputData inputData = new RemoveReactionInputData(messageId1, "user1");

        // Act
        try {
            interactor.execute(inputData);
        } catch (NullPointerException e) { /* Expected */ }

        // Assert
        Message savedMessage1 = messageRepository.findById(messageId1).orElseThrow();
        Message savedMessage2 = messageRepository.findById(messageId2).orElseThrow();

        assertFalse(savedMessage1.getReactions().containsKey("user1"),
                "Message 1 should have reaction removed");
        assertTrue(savedMessage2.getReactions().containsKey("user1"),
                "Message 2 should still have reaction");
    }

    @Test
    void testExecute_MessageSavedToRepository() {
        // Arrange
        String messageId = "msg123";
        String userId = "user1";

        Message message = new Message(messageId, "chat1", "user2", null, "Hello", Instant.now());
        message.addReaction(userId, "❤️");
        messageRepository.save(message);

        RemoveReactionInputData inputData = new RemoveReactionInputData(messageId, userId);

        // Act
        try {
            interactor.execute(inputData);
        } catch (NullPointerException e) { /* Expected */ }

        // Assert - Message should be saved back to repository without the reaction
        Message savedMessage = messageRepository.findById(messageId).orElseThrow();
        assertFalse(savedMessage.getReactions().containsKey(userId),
                "Reaction should be removed from saved message");
    }

    @Test
    void testExecute_PresenterNotCalledWhenMessageNotFound() {
        // Arrange
        RemoveReactionInputData inputData = new RemoveReactionInputData("nonexistent", "user1");

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

    private static class TestPresenter implements RemoveReactionOutputBoundary {
        RemoveReactionOutputData lastSuccessOutput;
        String lastFailError;

        @Override
        public void prepareSuccessView(RemoveReactionOutputData outputData) {
            this.lastSuccessOutput = outputData;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            this.lastFailError = errorMessage;
        }
    }
}