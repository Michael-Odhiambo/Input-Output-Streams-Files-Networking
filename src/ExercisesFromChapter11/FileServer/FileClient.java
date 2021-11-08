package ExercisesFromChapter11.FileServer;

import java.io.*;
import java.net.*;

public class FileClient {

    private Socket connectionToServer;
    private BufferedReader streamForReadingIncomingMessage;
    private PrintWriter streamForWritingOutgoingMessage;
    private final String HANDSHAKE = "FileClient";


    private void connectToAvailableServer( String serverName, int portNumber ) throws Exception {
        setUpConnectionToServer( serverName, portNumber );
        sendMessageToServer( HANDSHAKE );
        verifyHandShakeFromServer();
    }

    private void setUpConnectionToServer( String serverName, int portNumber ) throws Exception {
        connectionToServer = new Socket( serverName, portNumber );
        System.out.println( String.format( "Connected to server: %s through port: %d ", serverName, portNumber ) );
        initializeReaderAndWriter();
    }

    private void initializeReaderAndWriter() throws Exception {
        streamForReadingIncomingMessage = new BufferedReader( new InputStreamReader( connectionToServer.getInputStream() ) );
        streamForWritingOutgoingMessage = new PrintWriter( connectionToServer.getOutputStream() );
    }

    private void sendMessageToServer( String message ) {
        streamForWritingOutgoingMessage.println( message );
    }

    private void verifyHandShakeFromServer() throws Exception {
        if ( !streamForReadingIncomingMessage.equals( "FileServer" ) ) {
            sendMessageToServer( "Server is not a File Server. Connection closed." );
            closeConnectionToServer();
        }
    }

    private void closeConnectionToServer() throws Exception {
        connectionToServer.close();
    }

}
