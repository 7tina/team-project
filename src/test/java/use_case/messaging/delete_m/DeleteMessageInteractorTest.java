package use_case.messaging.delete_m;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeleteMessageInteractorTest {

    // ---- Fake DAO ----
    private static class FakeDao implements DeleteMessageDataAccessInterface {
        String deletedId = null;
        boolean shouldThrow = false;

        @Override
        public void deleteMessageById(String messageId) {
            if (shouldThrow) throw new RuntimeException("boom");
            deletedId = messageId;
        }
    }

    // ---- Fake Presenter ----
    private static class FakePresenter implements DeleteMessageOutputBoundary {
        DeleteMessageOutputData successData = null;
        DeleteMessageOutputData failData = null;

        @Override
        public void prepareSuccessView(DeleteMessageOutputData data) {
            successData = data;
        }

        @Override
        public void prepareFailView(DeleteMessageOutputData data) {
            failData = data;
        }
    }

    @Test
    void execute_handlesSuccessAndFailure() {
        // ========== success case ==========
        FakeDao dao = new FakeDao();
        FakePresenter presenter = new FakePresenter();
        DeleteMessageInteractor interactor = new DeleteMessageInteractor(dao, presenter);

        interactor.execute(new DeleteMessageInputData("msg-2", "user-1"));

        assertEquals("msg-2", dao.deletedId);
        assertNotNull(presenter.successData);
        assertTrue(presenter.successData.isSuccess());
        assertEquals("msg-2", presenter.successData.getDeletedMessageId());
        assertNull(presenter.failData);

        // ========== failure case ==========
        dao.shouldThrow = true;
        presenter.successData = null;
        presenter.failData = null;

        interactor.execute(new DeleteMessageInputData("msg-3", "user-1"));

        assertNull(presenter.successData);
        assertNotNull(presenter.failData);
        assertFalse(presenter.failData.isSuccess());
        assertEquals("msg-3", presenter.failData.getDeletedMessageId());
        assertNotNull(presenter.failData.getFailReason());
    }
}
