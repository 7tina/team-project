package use_case.messaging.delete_m;

public interface DeleteMessageInputBoundary {
    void execute(DeleteMessageInputData inputData);
}