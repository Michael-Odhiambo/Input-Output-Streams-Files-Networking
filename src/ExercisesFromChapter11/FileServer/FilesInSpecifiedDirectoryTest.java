package ExercisesFromChapter11.FileServer;

import java.io.*;

public class FilesInSpecifiedDirectoryTest {

    public static void main( String[] args ) {
        FilesInSpecifiedDirectory fileList = new FilesInSpecifiedDirectory( "/home/michael/Desktop" );

        for ( File file : fileList.getFiles() )
            System.out.println( file.getName() );


    }
}
