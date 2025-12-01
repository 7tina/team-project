package data_access;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;

public final class FirebaseClientProvider {

    private static Firestore instance;

    private FirebaseClientProvider() {
    }

    /**
     * Returns the singleton Firestore instance.
     * @return the Firestore instance
     * @throws RuntimeException if the Firestore client cannot be initialized
     */
    public static synchronized Firestore getFirestore() {
        if (instance == null) {
            try (InputStream serviceAccount =
                         new FileInputStream("src/main/resources/serviceAccountKey.json")) {

                final GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);

                final FirestoreOptions options = FirestoreOptions.newBuilder()
                        .setCredentials(credentials)
                        .setProjectId("gochat-1c0fc")
                        .build();

                instance = options.getService();
            }
            catch (IOException ex) {
                throw new RuntimeException("Failed to init Firestore", ex);
            }
        }
        return instance;
    }
}
