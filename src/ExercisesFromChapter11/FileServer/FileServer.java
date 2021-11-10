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

    public FileServer( File directoryContainingTheFiles, int listeningPort ) {
        initializeAvailableFiles( directoryContainingTheFiles );
        initializeListeningPort( listeningPort );

    }

    private void initializeAvailableFiles( File directoryContainingFiles ) {
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
        System.out.println( String.format( "Listening on port %d ", listenerForConnection.getLocalPort() ) );
        connectionToClient = listenerForConnection.accept();
        System.out.println( "Connection accepted." );
        listenerForConnection.close();
    }

    private void setUpConnection() throws Exception {
        System.out.println( "Server is setting up connection." );
        streamForReadingIncomingMessage = new BufferedReader( new InputStreamReader( connectionToClient.getInputStream() ) );
        streamForWritingOutgoingMessage = new PrintWriter( connectionToClient.getOutputStream() );
        sendMessageToClient( HANDSHAKE );
        verifyHandshakeFromClient();
    }

    private void sendMessageToClient( String message ) {
        System.out.println( String.format( "Server sending message: %s", message ) );
        streamForWritingOutgoingMessage.println( message );
        streamForWritingOutgoingMessage.flush();
        System.out.println( "Done sending message." );
    }

    private void verifyHandshakeFromClient() throws Exception {
        if ( !streamForReadingIncomingMessage.readLine().equals( "FileClient" ) ) {
            sendMessageToClient( "Client is not a File Client. Connection closed." );
            closeConnectionToClient();
            return;
        }
        System.out.println( "Connection verified by server." );
        sendMessageToClient( "Connection verified." );
    }

    public void processRequestFromClient( String command ) throws Exception {
        if ( command.equalsIgnoreCase( "index" ) )
            displayAvailableFilesToClient();
        else if ( command.startsWith( "get" ) ) {
            System.out.println();
            System.out.println( "Get request received." );
            System.out.println( String.format( "Getting the file: %s", command.substring(3).trim() ) );
            System.out.println();
            getRequestedFile( command.substring(3).trim() );
        }
        else {
            sendMessageToClient("ERROR. Connection lost.");
        }
    }

    private void displayAvailableFilesToClient() throws Exception {
        for ( File file : availableFilesList.getFiles() )
            streamForWritingOutgoingMessage.println( file.getName() );
        streamForWritingOutgoingMessage.flush();

        if ( streamForWritingOutgoingMessage.checkError() )
            throw new Exception( "Error while trying to send data" );
    }

    private void getRequestedFile( String fileName ) throws Exception {
        streamForReadingIncomingMessage = new BufferedReader( new InputStreamReader( connectionToClient.getInputStream() ) );
        streamForWritingOutgoingMessage = new PrintWriter( connectionToClient.getOutputStream() );

        System.out.println( "ok" );
        System.out.println();
        streamForWritingOutgoingMessage.println( "OK" );
        streamForWritingOutgoingMessage.println( availableFilesList.getSpecifiedFile( fileName ).getName() );
        streamForWritingOutgoingMessage.flush();  // MAKE SURE THE MESSAGE IS SENT.
    }

    private void closeConnectionToClient() throws Exception {
        connectionToClient.close();
    }

    public String getUserMessageFromInputStream() throws Exception {
        return streamForReadingIncomingMessage.readLine().trim();
    }
}
