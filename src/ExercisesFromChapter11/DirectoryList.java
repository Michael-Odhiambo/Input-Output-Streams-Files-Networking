package ExercisesFromChapter11;

/**
 * This class is a program that will print out all the files in a directory specified
 * by the user. The user will be prompted to enter a directory and then all the files
 * in that directory will be output to standard output.
 */

import java.util.Scanner;
import java.io.*;

public class DirectoryList {

    private static Scanner streamToStandardOutput;
    private static String directoryName;

    public static void main( String[] args ) {
        streamToStandardOutput = new Scanner( System.in );
        System.out.print( "Enter directory path: " );
        directoryName = streamToStandardOutput.nextLine().trim();
        printFilesInDirectory( new File( directoryName ) );
    }

    private static void printFilesInDirectory( File directory ) {
        if ( directory.exists() )
            processDirectory( directory );
        else {
            System.out.println( "No such file or directory." );
            return;
        }
    }

    private static void processDirectory( File directory ) {
        if ( directory.isFile() ) {
            printFileName( directory );
            return;
        }
        System.out.println( String.format( "Files in the directory %s : ", directory.getName() ) );
        for ( int i = 0; i < directory.listFiles().length; i++ ) {
            processDirectory( directory.listFiles()[i] );
        }

    }

    private static void printFileName( File directory ) {
        System.out.println( "File: " + directory.getName() );
    }

}
