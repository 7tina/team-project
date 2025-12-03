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

    public DeleteMessageInteractor(DeleteMessageDataAccessInterface dao,
                                   DeleteMessageOutputBoundary presenter) {
        this.dao = dao;
        this.presenter = presenter;
    }

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
        catch (IllegalArgumentException | IllegalStateException ex) {
            final DeleteMessageOutputData out = new DeleteMessageOutputData(
                    messageId,
                    LocalDateTime.now(),
                    false,
                    ex.getMessage()
            );
            presenter.prepareFailView(out);
        }
    }
}
