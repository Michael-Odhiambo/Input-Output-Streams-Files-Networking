package ExercisesFromChapter11.FileServer;

import java.io.*;
import java.net.*;
import java.util.stream.Stream;

public class FileClient {

    private Socket connectionToServer;
    private BufferedReader streamForReadingIncomingMessage;
    private PrintWriter streamForWritingOutgoingMessage;
    private final String HANDSHAKE = "FileClient";
    private String serverName;
    private int serverListeningPortNumber;

    public FileClient( int listeningPort, String serverName ) {
        serverListeningPortNumber = listeningPort;
        this.serverName = serverName;
    }

    public void connectToAvailableServer() throws Exception {
        setUpConnectionToServer();
        verifyHandShakeFromServer();
        sendMessageToServer( HANDSHAKE );
        getHandShakeResponseFromServer();

    }

    private void setUpConnectionToServer() throws Exception {
        connectionToServer = new Socket( serverName, serverListeningPortNumber );
        System.out.println( String.format( "Connected to server: %s through port: %d ", serverName, serverListeningPortNumber ) );
        initializeReaderAndWriter();
    }

    private void initializeReaderAndWriter() throws Exception {
        streamForReadingIncomingMessage = new BufferedReader( new InputStreamReader( connectionToServer.getInputStream() ) );
        streamForWritingOutgoingMessage = new PrintWriter( connectionToServer.getOutputStream() );
    }

    private void sendMessageToServer( String message ) {
        System.out.println( String.format( "Sending message to server: %s", message ) );
        streamForWritingOutgoingMessage.println( message );
        streamForWritingOutgoingMessage.flush();
    }

    private void verifyHandShakeFromServer() throws Exception {
        if ( !streamForReadingIncomingMessage.readLine().equals( "FileServer" ) ) {
            sendMessageToServer( "Server is not a File Server. Connection closed." );
            closeConnectionToServer();
        }
        System.out.println( "Connection verified by client." );
    }

    public void closeConnectionToServer() throws Exception {
        connectionToServer.close();
    }

    private void getHandShakeResponseFromServer() throws Exception {
        System.out.println( streamForReadingIncomingMessage.readLine() );
    }

    public void sendClientRequestToServer( String request ) {
        sendMessageToServer( request );
    }

    public Stream<String> getServerReply() {
        Stream<String> lines = streamForReadingIncomingMessage.lines();
        System.out.println("Done getting server reply.");
        return lines;

    }


}
