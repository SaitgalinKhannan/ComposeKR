import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.FileList
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader


class DriveQuickstartK {

    private val APPLICATION_NAME = "Google Drive API Java Quickstart"

    /**
     * Global instance of the JSON factory.
     */
    private val JSON_FACTORY: JsonFactory = GsonFactory.getDefaultInstance()

    /**
     * Directory to store authorization tokens for this application.
     */
    private val TOKENS_DIRECTORY_PATH = "1087292975333-f38u9birn5er3lrjl67llp0f4a7rmame.apps.googleusercontent.com"

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private val SCOPES = listOf(DriveScopes.DRIVE_METADATA_READONLY)
    private val CREDENTIALS_FILE_PATH = "/credentials.json"

    @Throws(IOException::class)
    fun getCredentials(HTTP_TRANSPORT: NetHttpTransport): Credential? {
        // Load client secrets.
        val `in` = DriveQuickstartK::class.java.getResourceAsStream(CREDENTIALS_FILE_PATH)
            ?: throw FileNotFoundException("Resource not found: $CREDENTIALS_FILE_PATH")

        val clientSecrets: GoogleClientSecrets = GoogleClientSecrets.load(JSON_FACTORY, InputStreamReader(`in`))

        val flow =
            GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES
            )
                .setDataStoreFactory(
                    FileDataStoreFactory(
                        File(
                            TOKENS_DIRECTORY_PATH
                        )
                    )
                )
                .setAccessType("offline")
                .build()
        val receiver = LocalServerReceiver.Builder().setPort(8888).build()

        return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
    }
}

fun main(args: Array<String>) {

    val APPLICATION_NAME = "Google Drive API Java Quickstart"
    val JSON_FACTORY: JsonFactory = GsonFactory.getDefaultInstance()
    val TOKENS_DIRECTORY_PATH = "1087292975333-f38u9birn5er3lrjl67llp0f4a7rmame.apps.googleusercontent.com"

    val HTTP_TRANSPORT: NetHttpTransport = GoogleNetHttpTransport.newTrustedTransport()
    val service: Drive = Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, DriveQuickstartK().getCredentials(HTTP_TRANSPORT))
        .setApplicationName(APPLICATION_NAME)
        .build()

    // Print the names and IDs for up to 10 files.
    val result: FileList = service.files().list()
        .setPageSize(10)
        .setFields("nextPageToken, files(id, name)")
        .execute()
    val files: MutableList<com.google.api.services.drive.model.File>? = result.getFiles()
    if (files == null || files.isEmpty()) {
        println("No files found.")
    } else {
        println("Files:")
        for (file in files) {
            System.out.printf("%s (%s)\n", file.name, file.getId())
        }
    }
}