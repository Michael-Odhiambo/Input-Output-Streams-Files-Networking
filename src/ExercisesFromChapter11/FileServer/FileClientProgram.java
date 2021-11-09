package ExercisesFromChapter11.FileServer;


public class FileClientProgram {

    private static FileClient fileClient;
    private static String[] commandLineArguments;
    private static int serverListeningPort;
    private static String serverName;

    public static void main( String[] args ) {
        try {
            setCommandLineArguments( args );
            processCommandLineArguments();
            connectToServer();
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
}
