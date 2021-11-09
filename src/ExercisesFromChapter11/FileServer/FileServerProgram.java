package ExercisesFromChapter11.FileServer;

import java.io.*;

public class FileServerProgram {

    private static FileServer fileServer;
    private static String[] commandLineArguments;
    private static File directoryContainingServerFiles;
    private static int serverListeningPort;
    private static String serverName;

    public static void main( String[] args ) {
        try {
            setCommandLineArguments( args );
            processCommandLineArguments();
            startServer();
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
        getServerName();
    }

    private static void startServer() throws Exception {
        System.out.println( "Starting server." );
        fileServer = new FileServer( directoryContainingServerFiles, serverListeningPort );
        fileServer.start();

    }

    private static void verifyAllArgumentAreProvided() throws Exception {
        if ( commandLineArguments.length < 3 )
            throw new Exception("Usage: java <DirectoryContainingFiles> <ListeningPort> <ServerName> ");
    }

    private static void getDirectoryContainingServerFiles() throws Exception {
        if ( new File( commandLineArguments[0].trim() ).exists() )
            directoryContainingServerFiles = new File( commandLineArguments[0].trim() );
        else
            throw new Exception( String.format( "No such file or directory: %s", commandLineArguments[0].trim() ) );
    }

    private static void getServerListeningPort() throws Exception {
        try {
            serverListeningPort = Integer.parseInt( commandLineArguments[1] );
        }
        catch ( Exception e ) {
            throw e;
        }
    }

    private static void getServerName() {
        serverName = commandLineArguments[2].trim();
    }
}
