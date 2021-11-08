package ExercisesFromChapter11.FileServer;

/**
 * This class represents a simple file server that makes a collection of files available for transmission
 * to clients. When the server is created, a directory needs to be specified in the server's constructor
 * which contains all the files that will be made available.
 */

import java.net.*;
import java.io.*;

public class FileServer {

    FilesInSpecifiedDirectory availableFilesList;
    private int listeningPort;
    private int defaultListeningPort = 1728;
    private ServerSocket listenerForConnection;
    private Socket connectionToClient;
    private BufferedReader streamForReadingIncomingMessage;
    private PrintWriter streamForWritingOutgoingMessage;
    private String messageReceived;
    private String messageSent;
    private String HANDSHAKE = "FileServer";

    public FileServer( String directoryContainingTheFiles, int listeningPort ) {
        initializeAvailableFiles( directoryContainingTheFiles );
        initializeListeningPort( listeningPort );

    }

    private void initializeAvailableFiles( String directoryContainingFiles ) {
        availableFilesList = new FilesInSpecifiedDirectory( directoryContainingFiles );
    }

    private void initializeListeningPort( Integer specifiedPort ) {
        if ( specifiedPort == null )
            this.listeningPort = defaultListeningPort;
        else
            this.listeningPort = specifiedPort;
    }

    public void start() throws Exception {
        listenForConnection();
        setUpConnection();

    }

    private void listenForConnection() throws Exception {
        listenerForConnection = new ServerSocket( listeningPort );
        System.out.println( String.format( "Listening on port %d ", listeningPort ) );
        connectionToClient = listenerForConnection.accept();
    }

    private void setUpConnection() throws Exception {
        streamForReadingIncomingMessage = new BufferedReader( new InputStreamReader( connectionToClient.getInputStream() ) );
        streamForWritingOutgoingMessage = new PrintWriter( connectionToClient.getOutputStream() );
        sendMessageToClient( HANDSHAKE );
        verifyHandshakeFromClient();

    }

    private void sendMessageToClient( String message ) {
        streamForWritingOutgoingMessage.println( message );
        streamForWritingOutgoingMessage.flush();
    }

    private void verifyHandshakeFromClient() throws Exception {
        messageReceived = streamForReadingIncomingMessage.readLine();
        if ( !messageReceived.equals( "FileClient" ) ) {
            sendMessageToClient( "Client is not a File Client. Connection closed." );
            closeConnectionToClient();
        }
    }

    public void processCommandFromClient( String command ) throws Exception {
        command = command.toLowerCase();
        if ( command.equals( "index" ) )
            displayAvailableFilesToClient();
        else if ( command.startsWith( "g" ) ) {
            getRequestedFile(command.substring(3).trim());
            closeConnectionToClient();
        }
        else {
            sendMessageToClient("ERROR. Connection lost.");
            closeConnectionToClient();
        }
    }

    private void displayAvailableFilesToClient() {
        for ( File file : availableFilesList.getFiles() )
            streamForWritingOutgoingMessage.println( file.getName() );
    }

    private void getRequestedFile( String fileName ) throws Exception {
        streamForWritingOutgoingMessage.println( "OK" );
        streamForWritingOutgoingMessage.println( availableFilesList.getSpecifiedFile( fileName ) );
    }

    private void closeConnectionToClient() throws Exception {
        connectionToClient.close();
    }
}
