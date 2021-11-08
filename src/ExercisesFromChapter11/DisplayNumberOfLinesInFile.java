package ExercisesFromChapter11;

/**
 * This is a program to display the number of lines in a file or files specified as command line arguments.
 * Since a byte stream is used, files of any type can be passed to the program.
 */

import java.io.*;

public class DisplayNumberOfLinesInFile {

    public static void main( String[] args ) {
        try {
            processFileList( args );
        }
        catch ( Exception e ) {
            System.out.println( e );
        }

    }

    private static void processFileList( String[] fileList ) throws Exception {
        if ( fileList.length < 1 )
            throw new Exception( "Specify one or more files." );
        for ( String fileName : fileList )
            processFile( new File( fileName ) );
    }

    private static void processFile( File file ) {
        try {
            BufferedReader streamForReadingFromFile = new BufferedReader( new FileReader( file ) );
            int numberOfLinesInFile = 0;
            while ( streamForReadingFromFile.readLine() != null )
                numberOfLinesInFile++;
            System.out.println( String.format( "Number of lines in file: %s is %d", file.getName(), numberOfLinesInFile ) );
        }
        catch ( Exception e ) {
            System.out.println( String.format( "Error while trying to process the file: %s, %s", file.getName(), e ) );
        }
    }
}
