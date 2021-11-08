package ExercisesFromChapter11.FileServer;

/**
 * This class represents a list of files contained in a directory specified in
 * its constructor. It is meant to be used in the FileServe program.
 */

import java.io.*;
import java.util.ArrayList;

public class FilesInSpecifiedDirectory {

    File directory;
    ArrayList<File> directoryList = new ArrayList<>();


    public FilesInSpecifiedDirectory( String specifiedDirectoryName ) {
        this.directory = new File( specifiedDirectoryName );
        addFilesInDirectoryToDirectoryList();
    }

    private void addFilesInDirectoryToDirectoryList() {
        processDirectory( this.directory );
    }

    private void processDirectory( File directory ) {
        if ( directory.isFile() ) {
            directoryList.add(directory);
            return;
        }
        processSubdirectory( directory );

    }

    private void processSubdirectory( File subDirectory ) {
        for ( int i = 0; i < subDirectory.listFiles().length; i++ )
            processDirectory( subDirectory.listFiles()[i] );
    }

    public ArrayList<File> getFiles() {
        return directoryList;
    }

    public File getSpecifiedFile( String nameOfSpecifiedFile ) {
        for ( File file : directoryList ) {
            if ( file.getName().equals( new File( nameOfSpecifiedFile ).getName() ) )
                return file;
        }
        return null;
    }


}

