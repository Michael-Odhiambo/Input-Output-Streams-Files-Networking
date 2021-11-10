package ExercisesFromChapter11.FileServer;

import java.io.*;

public class FileServerProgram {

    private static FileServer fileServer;
    private static String[] commandLineArguments;
    private static File directoryContainingServerFiles;
    private static int serverListeningPort;

    public static void main( String[] args ) {
        try {
            setCommandLineArguments( args );
            processCommandLineArguments();
            startServer();
            getClientRequests();
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
        getDirectoryContainingServerFiles();
        getServerListeningPort();
    }

    private static void startServer() throws Exception {
        System.out.println( "Starting server." );
        fileServer = new FileServer( directoryContainingServerFiles, serverListeningPort );
        fileServer.start();

    }

    private static void verifyAllArgumentAreProvided() throws Exception {
        if ( commandLineArguments.length < 2 )
            throw new Exception("Usage: java <DirectoryContainingFiles> <ListeningPort> ");
    }

    private static void getDirectoryContainingServerFiles() throws Exception {
        if ( new File( commandLineArguments[0].trim() ).exists() )
            directoryContainingServerFiles = new File( commandLineArguments[0].trim() );
        else
            throw new Exception( String.format( "No such file or directory: %s", commandLineArguments[0].trim() ) );
    }

    private static void getServerListeningPort() {
        try {
            serverListeningPort = Integer.parseInt( commandLineArguments[1] );
        }
        catch ( Exception e ) {
            throw e;
        }
    }

    private static void getClientRequests() throws Exception {
        System.out.println();
        System.out.println( "Waiting for client request..." );
        processClientRequest( fileServer.getUserMessageFromInputStream() );
        closeConnectionWithClient();
    }

    private static void processClientRequest( String request ) throws Exception {
        System.out.println( String.format( "Received request \"%s\" from client.", request ) );
        System.out.println( "Processing request." );
        System.out.println();
        fileServer.processRequestFromClient( request );
    }

    private static void closeConnectionWithClient() throws Exception {
        fileServer.closeConnectionToClient();
    }

}