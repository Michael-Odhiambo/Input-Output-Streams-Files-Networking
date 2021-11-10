package ExercisesFromChapter11.FileServer;

import java.util.Scanner;
import java.util.stream.Stream;

public class FileClientProgram {

    private static FileClient fileClient;
    private static String[] commandLineArguments;
    private static int serverListeningPort;
    private static String serverName;
    private static String clientRequest;
    private static Scanner getClientRequest;

    public static void main( String[] args ) {
        try {
            setCommandLineArguments( args );
            processCommandLineArguments();
            connectToServer();
            sendRequestToServer();
            fileClient.closeConnectionToServer();
        }
        catch ( Exception e ) {
            System.out.println( e );
        }
    }

    private static void setCommandLineArguments( String[] arguments ) {
        commandLineArguments = arguments;
    }

    private static void processCommandLineArguments() throws Exception {
        verifyAllArgumentAreProvided();
        getServerListeningPort();
        getServerName();
    }

    private static void connectToServer() throws Exception {
        fileClient = new FileClient( serverListeningPort, serverName );
        fileClient.connectToAvailableServer();

    }

    private static void verifyAllArgumentAreProvided() throws Exception {
        if ( commandLineArguments.length < 2 )
            throw new Exception("Usage: java <ListeningPort> <ServerName> ");
    }

    private static void getServerListeningPort() {
        try {
            serverListeningPort = Integer.parseInt( commandLineArguments[0] );
        }
        catch ( Exception e ) {
            throw e;
        }
    }

    private static void getServerName() {
        serverName = commandLineArguments[1].trim();
    }

    private static void sendRequestToServer() {
        getClientRequest = new Scanner( System.in );

        System.out.println( "Input request: " );
        clientRequest = getClientRequest.nextLine().trim();
        fileClient.sendClientRequestToServer( clientRequest );
        System.out.println( String.format( "Sent request %s to server.", clientRequest ) );
        System.out.println( "Waiting for reply from server." );

        Stream<String> lines = fileClient.getServerReply();
        lines.forEachOrdered( message -> System.out.println( message ) );
        System.out.println( "Done processing reply" );
        System.out.println();

    }
}
