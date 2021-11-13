package ExercisesFromChapter11.Checkers;

import java.util.logging.*;
import java.io.IOException;

public class CheckersLogger {

    private static Logger logger;
    private static Handler consoleHandler;
    private static Handler fileHandler;

    public CheckersLogger( String logFile ) {
        try {
            initializeLogger( logFile );
        }
        catch ( Exception e ) {
            System.out.println( e );
        }
    }


    private void initializeLogger( String className ) throws IOException {
        Level logLevel = Level.FINEST;
        logger = logger.getLogger( className );
        logger.setLevel( logLevel );
        logger.setUseParentHandlers( false );

        consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel( logLevel );
        logger.addHandler( consoleHandler );

        fileHandler = new FileHandler( "CheckersLog.txt", false );
        fileHandler.setLevel( logLevel );
        fileHandler.setFormatter( new SimpleFormatter() );
        logger.addHandler( fileHandler );
    }

    public void log( String message ) {
        logger.info( message );
    }
}
