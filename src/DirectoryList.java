/**
 * This program will list all the files in the directory specified by the user.
 */

import java.io.File;
import java.util.Scanner;

public class DirectoryList {

    public static void main( String[] args ) {

        String directoryName;  // Directory name entered by the user.
        File directory;  // File object referring to the directory.
        String[] files;  // Array of file names in the directory.
        Scanner scanner;  // For reading a line of input from the user.

        scanner = new Scanner( System.in );  // Scanner reads from standard input.

        System.out.print( "Enter a directory name: " );
        directoryName = scanner.nextLine().trim();
        directory = new File( directoryName );

        if ( directory.isDirectory() == false ) {
            if ( directory.exists() == false )
                System.out.println( "There is no such directory!" );
            else
                System.out.println( "That file is not a directory." );

        }
        else {
            files = directory.list();
            System.out.println( "Files in directory \" " + directory + " \": " );
            for ( int i = 0; i < files.length; i++ ) {
                System.out.println( "   " + files[i] );
            }
        }
    }
}
