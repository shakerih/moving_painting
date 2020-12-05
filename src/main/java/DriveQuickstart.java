import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.client.http.FileContent;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;

public class DriveQuickstart {
    private static final String APPLICATION_NAME = "Google Drive API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = DriveQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public static void main(String... args) throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        // // Print the names and IDs for up to 10 files.
        // FileList result = service.files().list()
        //         .setPageSize(10)
        //         .setFields("nextPageToken, files(id, name)")
        //         .execute();
        // List<File> files = result.getFiles();
        // if (files == null || files.isEmpty()) {
        //     System.out.println("No files found.");
        // } else {
        //     System.out.println("Files:");
        //     for (File file : files) {
        //         System.out.printf("%s (%s)\n", file.getName(), file.getId());
        //     }
        // }


            //     string query = "mimeType!='application/vnd.google-apps.folder' and trashed = false and name = '" + "frame.png" + "'";
            // FilesResource.ListRequest req;
            // req = service.Files.List();
            // req.Q = query;
            // req.Fields = "files(id, name)";
            // var result = req.Execute();

            String pageToken = null;
            do {
              FileList result = service.files().list()
                  .setQ("mimeType='image/png' and name='frame2.png'")
                  .setSpaces("drive")
                  .setFields("nextPageToken, files(id, name)")
                  .setPageToken(pageToken)
                  .execute();
              for (File file : result.getFiles()) {

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                service.files().get(file.getId())
                .executeMediaAndDownloadTo(outputStream);

                java.io.File newFile = new java.io.File("C:/Users/omlette/Documents/moving_painting/moving_painting/data/example_images/current2.png");    
                OutputStream fos = new FileOutputStream(newFile);
                outputStream.writeTo(fos);
              }
              pageToken = result.getNextPageToken();
                    

            } while (pageToken != null);
            

            pageToken = null;
            do {
              FileList result = service.files().list()
                  .setQ("mimeType='image/png' and name='frame.png'")
                  .setSpaces("drive")
                  .setFields("nextPageToken, files(id, name)")
                  .setPageToken(pageToken)
                  .execute();
              for (File file : result.getFiles()) {
                System.out.printf("Found file: %s (%s)\n",
                    file.getName(), file.getId());
                service.files().delete(file.getId()).execute();
              }
              pageToken = result.getNextPageToken();
            

   //          if (result.getFiles().size() >0){
   // try {
   //    service.files().delete(result.getFiles().get(0).getId()).execute();
   //  } catch (IOException e) {
   //    System.out.println("An error occurred: " + e);
   //  }
   // }
    } while (pageToken != null);
        File fileMetadata = new File();
fileMetadata.setName("frame.png");
java.io.File filePath = new java.io.File("C:/Users/omlette/Documents/moving_painting/moving_painting/data/example_images/current.png");
FileContent mediaContent = new FileContent("image/png", filePath);
File file = service.files().create(fileMetadata, mediaContent)
    .setFields("id")
    .execute();
System.out.println("File ID: " + file.getId());



    }
}