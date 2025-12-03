package use_case.messaging.delete_m;

import org.junit.jupiter.api.Test;
import usecase.messaging.deletemessage.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DeleteMessageInteractorTest {

    // -------- Fake DAO --------
    static class FakeDao implements DeleteMessageDataAccessInterface {
        String deletedId = null;
        boolean shouldThrow = false;

        @Override
        public void deleteMessageById(String messageId) {
            if (shouldThrow) throw new RuntimeException("boom");
            deletedId = messageId;
        }
    }

    // -------- Fake Presenter --------
    static class FakePresenter implements DeleteMessageOutputBoundary {
        DeleteMessageOutputData successData = null;
        DeleteMessageOutputData failData = null;

        @Override
        public void prepareSuccessView(DeleteMessageOutputData outputData) {
            successData = outputData;
        }

        @Override
        public void prepareFailView(DeleteMessageOutputData output) {
            failData = output;
        }
    }

    @Test
    void execute_success_callsDaoAndSuccessPresenter() {
        FakeDao dao = new FakeDao();
        FakePresenter presenter = new FakePresenter();
        DeleteMessageInteractor interactor = new DeleteMessageInteractor(dao, presenter);

        DeleteMessageInputData input =
                new DeleteMessageInputData("msg-123", "user-1");

        assertEquals("user-1", input.getCurrentUserId());

        interactor.execute(input);

        // DAO called with correct id
        assertEquals("msg-123", dao.deletedId);

        // success presenter called
        assertNotNull(presenter.successData);
        assertTrue(presenter.successData.isSuccess());
        assertEquals("msg-123", presenter.successData.getMessageId());

        assertNotNull(presenter.successData.getDeletionTime());

        // fail presenter not called
        assertNull(presenter.failData);
    }

    @Test
    void execute_fail_whenDaoThrows_callsFailPresenter() {
        FakeDao dao = new FakeDao();
        dao.shouldThrow = true;
        FakePresenter presenter = new FakePresenter();
        DeleteMessageInteractor interactor = new DeleteMessageInteractor(dao, presenter);

        DeleteMessageInputData input =
                new DeleteMessageInputData("msg-999", "user-1");

        interactor.execute(input);

        // DAO attempted delete
        assertNull(dao.deletedId); // because it threw before setting

        // fail presenter called
        assertNotNull(presenter.failData);
        assertFalse(presenter.failData.isSuccess());
        assertEquals("msg-999", presenter.failData.getMessageId());
        assertNotNull(presenter.failData.getFailReason());
        assertEquals("boom", presenter.failData.getFailReason());

        // success presenter not called
        assertNull(presenter.successData);
    }

    @Test
    void outputData_convenienceConstructor_setsSuccessTrueAndReasonNull() {
        LocalDateTime time = LocalDateTime.now();
        DeleteMessageOutputData output = new DeleteMessageOutputData("test-id", time);

        assertTrue(output.isSuccess());
        assertEquals("test-id", output.getMessageId());
        assertEquals(time, output.getDeletionTime());
        assertNull(output.getFailReason());
    }
}