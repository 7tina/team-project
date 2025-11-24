package use_case.messaging.delete_m;

import data_access.FireBaseUserDataAccessObject;

import java.time.LocalDateTime;

public class DeleteMessageInteractor implements DeleteMessageInputBoundary {
    private final FireBaseUserDataAccessObject dao;
    private final DeleteMessageOutputBoundary presenter;

    public DeleteMessageInteractor(FireBaseUserDataAccessObject dao,
                                   DeleteMessageOutputBoundary presenter) {
        this.dao = dao;
        this.presenter = presenter;
    }

    @Override
    public void execute(DeleteMessageInputData input) {

        try {
            dao.deleteMessageById(input.getMessageId());

            DeleteMessageOutputData out = new DeleteMessageOutputData(
                    input.getMessageId(),
                    LocalDateTime.now(),
                    true,
                    null
            );
            presenter.prepareSuccessView(out);

        } catch (Exception e) {
            DeleteMessageOutputData out = new DeleteMessageOutputData(
                    input.getMessageId(),
                    LocalDateTime.now(),
                    false,
                    e.getMessage()
            );
            presenter.prepareFailView(out);
        }
    }
}