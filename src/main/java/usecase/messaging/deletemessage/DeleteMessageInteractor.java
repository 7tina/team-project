package usecase.messaging.deletemessage;

import java.time.LocalDateTime;

/**
 * Interactor for the delete message use case.
 *
 * <p>
 * It delegates deletion to the Data Access layer, constructs an output data
 * object, and notifies the presenter.
 */
public class DeleteMessageInteractor implements DeleteMessageInputBoundary {

    private final DeleteMessageDataAccessInterface dao;
    private final DeleteMessageOutputBoundary presenter;

    /**
     * Constructs a {@code DeleteMessageInteractor}.
     *
     * @param dao       data access interface for deleting messages
     * @param presenter output boundary for presenting results
     */
    public DeleteMessageInteractor(DeleteMessageDataAccessInterface dao,
                                   DeleteMessageOutputBoundary presenter) {
        this.dao = dao;
        this.presenter = presenter;
    }

    /**
     * Executes the delete message use case.
     *
     * <p>
     * On success: calls {@code prepareSuccessView}.
     * On failure: calls {@code prepareFailView}.
     *
     * @param input input data containing the message ID and user ID
     */
    @Override
    public void execute(DeleteMessageInputData input) {
        final String messageId = input.getMessageId();

        try {
            dao.deleteMessageById(messageId);

            final DeleteMessageOutputData out = new DeleteMessageOutputData(
                    messageId,
                    LocalDateTime.now(),
                    true,
                    null
            );
            presenter.prepareSuccessView(out);

        }
        catch (Exception e) {
            final DeleteMessageOutputData out = new DeleteMessageOutputData(
                    messageId,
                    LocalDateTime.now(),
                    false,
                    e.getMessage()
            );
            presenter.prepareFailView(out);
        }
    }
}
