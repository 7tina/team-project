package usecase.messaging.delete_m;

public interface DeleteMessageInputBoundary {
    void execute(DeleteMessageInputData inputData);
}