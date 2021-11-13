package Logging;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.Handler;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import java.io.IOException;

public class ProgrammaticConfigurationExample {

    private static Logger logger;
    private static Handler consoleHandler;
    private static Handler fileHandler;

    public static void main( String[] args ) {
        try {
            initLog();
        }
        catch ( IOException e ) {
            System.out.println( "Could not initialize log: " + e.getMessage() );
        }

        logger.info( "My logger is working." );
    }

    private static void initLog() throws IOException {
        Level logLevel = Level.FINEST;

        logger = logger.getLogger( "ProgrammaticConfigurationExample" );
        logger.setLevel( logLevel );
        logger.setUseParentHandlers( false );

        consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel( logLevel );
        logger.addHandler( consoleHandler );

        fileHandler = new FileHandler( "log.txt", false );
        fileHandler.setLevel( logLevel );
        fileHandler.setFormatter( new SimpleFormatter() );
        logger.addHandler( fileHandler );
    }

}
