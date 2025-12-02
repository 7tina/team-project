package usecase.messaging.delete_m;

import java.time.LocalDateTime;

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

        String messageId = input.getMessageId();

        try {
            dao.deleteMessageById(messageId);

            DeleteMessageOutputData out = new DeleteMessageOutputData(
                    messageId,
                    LocalDateTime.now(),
                    true,
                    null
            );
            presenter.prepareSuccessView(out);

        } catch (Exception e) {
            DeleteMessageOutputData out = new DeleteMessageOutputData(
                    messageId,
                    LocalDateTime.now(),
                    false,
                    e.getMessage()
            );
            presenter.prepareFailView(out);
        }
    }
}