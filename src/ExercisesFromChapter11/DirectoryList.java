package ExercisesFromChapter11;

/**
 * This class is a program that will print out all the files in a directory specified
 * by the user. The user will be prompted to enter a directory and then all the files
 * in that directory will be output to standard output.
 */

import java.util.Scanner;
import java.io.*;

public class DirectoryList {

    private static Scanner streamToStandardInput;
    private static String directoryName;
    private static int numberOfFilesCounted = 0;

    public static void main( String[] args ) {
        createStream();
        getFileNameFromUser();
        printFilesInDirectory( new File( directoryName ) );
    }

    private static void createStream() {
        streamToStandardInput = new Scanner( System.in );
    }

    private static String getFileNameFromUser() {
        System.out.print( "Enter directory name: " );
        directoryName = streamToStandardInput.nextLine().trim();
        return directoryName;
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
        processSubdirectory( directory );
    }

    private static void processSubdirectory( File subDirectory ) {
        System.out.println( String.format( "Files in the directory %s : ", subDirectory.getName() ) );
        for ( int i = 0; i < subDirectory.listFiles().length; i++ ) {
            processDirectory( subDirectory.listFiles()[i] );
        }
    }

    private static void printFileName( File directory ) {
        numberOfFilesCounted++;
        System.out.println( String.format( "%d. File: %s ", numberOfFilesCounted, directory.getName() ) );
    }

}
