package ExercisesFromChapter11.Checkers;

import java.io.*;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.layout.Pane;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class Checkers extends Application {

    private Stage mainWindow;
    private int CANVAS_SIZE = 640;
    private Canvas canvas;
    private GraphicsContext drawingArea;
    private int SQUARE_SIZE = 80;
    private Pane canvasHolder;
    private BorderPane paneAndMenuHolder;
    private boolean aPieceIsCurrentlySelected = false;
    private CheckersPiece pieceToMove;

    private int currentlyClickedRow;
    private int currentlyClickedColumn;

    private int previouslyClickedRow;
    private int previouslyClickedColumn;

    private String BLUE_PLAYER = "BLUE";
    private String RED_PLAYER = "RED";
    private String currentPlayer = RED_PLAYER;

    Document xmlRepresentationOfFileBeingOpened;

    private boolean gameIsBeingContinued = false;
    File gameBeingContinued;
    private String defaultNameOfGameFile = "CheckersGameFile.txt";

    private PrintWriter streamToOutputFile;

    private CheckersBoard checkersBoard = new CheckersBoard();

    private CheckersLogger checkersLogger = new CheckersLogger( "CheckersLog.txt" );

    public static void main( String[] args ) {
        launch( args );
    }

    public void start( Stage stage ) {
        checkersLogger.log( "The game has been started." );
        setUpMainWindow( stage );
        setUpMainWindowComponents();
        showMainWindow();
    }

    private void setUpMainWindow( Stage stage ) {
        setMainWindowAndTitle( stage );
    }

    private void setUpMainWindowComponents() {
        createCanvas();
        drawCheckerBoardOnCanvas();
        drawBoard();
        setUpBorderPane();

    }

    private void setMainWindowAndTitle( Stage stage ) {
        mainWindow = stage;
        mainWindow.setTitle( "Checkers" );
    }

    private void createCanvas() {
        canvas = new Canvas( CANVAS_SIZE, CANVAS_SIZE );
        drawingArea = canvas.getGraphicsContext2D();
        canvas.setOnMouseClicked( event -> handleMouseClickOnCanvas( event ) );
    }

    private void handleMouseClickOnCanvas( MouseEvent event ) {
        setCurrentlyClickedRowAndColumn( event );
        if ( aPieceIsCurrentlySelected ) {
            movePieceToToSelectedSquare();
        }
        else if ( checkersBoard.getPieceAt( currentlyClickedRow, currentlyClickedColumn ) != null ) {
            getPieceToBeMoved( currentlyClickedRow, currentlyClickedColumn );
        }
        drawBoard();

    }

    private void switchPlayer() {
        if ( previouslyClickedRow == currentlyClickedRow && previouslyClickedColumn == currentlyClickedColumn )
            return;
        currentPlayer = ( currentPlayer == RED_PLAYER ) ? BLUE_PLAYER : RED_PLAYER;
    }

    private void setCurrentlyClickedRowAndColumn( MouseEvent event ) {
        currentlyClickedColumn = (int)( event.getX() / 80 );
        currentlyClickedRow = (int)( event.getY() / 80 );
    }

    private void movePieceToToSelectedSquare() {
        CheckersMove move = new CheckersMove( pieceToMove.getRow(), pieceToMove.getColumn(), currentlyClickedRow, currentlyClickedColumn );
        if ( selectedPieceCanMove( move ) ) {
            checkersBoard.movePiece( pieceToMove, move );
            checkIfPieceIsToBeKing( move );
            if ( playerNeedsToContinueJumping( move ) )
                return;
            switchPlayer();
        }
        aPieceIsCurrentlySelected = false;
    }

    private boolean playerNeedsToContinueJumping( CheckersMove move ) {
        if ( !move.isJump() )
            return false;
        if ( checkersBoard.getJumpMovesForPiece( pieceToMove ).size() < 1 )
            return false;
        return true;
    }

    private void checkIfPieceIsToBeKing( CheckersMove move ) {
        if ( pieceToMove.getPieceColor().equals( "red" ) && move.getToRow() == 7 )
            checkersBoard.setKing( pieceToMove );
        else if ( pieceToMove.getPieceColor().equals( "blue" ) && move.getToRow() == 0 )
            checkersBoard.setKing( pieceToMove );
    }

    private boolean selectedPieceCanMove( CheckersMove moveToMake ) {
        for ( CheckersMove move : checkersBoard.getLegalMovesForPiece( pieceToMove ) ) {
            if ( move.getFromRow() == moveToMake.getFromRow() && move.getFromCol() == moveToMake.getFromCol() &&
            move.getToRow() == moveToMake.getToRow() && move.getToCol() == moveToMake.getToCol() )
                return true;

        }
        return false;
    }

    private void getPieceToBeMoved( int fromRow, int fromCol ) {
        checkersLogger.log( "Getting piece to move." );
        if ( checkersBoard.getPieceAt( fromRow, fromCol ) != null ) {
            if ( !checkersBoard.getPieceAt( fromRow, fromCol ).getPieceColor().equalsIgnoreCase( currentPlayer ) )
                return;
            previouslyClickedRow = fromRow;
            previouslyClickedColumn = fromCol;
            pieceToMove = checkersBoard.getPieceAt( fromRow, fromCol );
            aPieceIsCurrentlySelected = true;
        }
    }

    private void highlightPieceToMove() {
        checkersLogger.log( "Highlighting piece." );
        drawingArea.setStroke( Color.RED );
        drawingArea.setLineWidth( 5 );
        drawingArea.strokeRect( pieceToMove.getColumn()*80, pieceToMove.getRow()*80, SQUARE_SIZE, SQUARE_SIZE );
    }

    private void drawBoard() {
        drawCheckerBoardOnCanvas();
        drawPlayerPiecesOnTheBoard();
        if ( aPieceIsCurrentlySelected )
            highlightPieceToMove();
    }

    private void drawCheckerBoardOnCanvas() {
        for ( int row = 0; row < 8; row++ ) {
            for ( int col = 0; col < 8; col++ ) {
                if (row % 2 == col % 2)
                    drawingArea.setFill(Color.BLACK);
                else
                    drawingArea.setFill(Color.WHITE);
                drawingArea.fillRect( row*SQUARE_SIZE, col*SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE );
            }
        }
    }

    private void drawPlayerPiecesOnTheBoard() {
        for ( int row = 0; row < 8; row++ ) {
            for ( int col = 0; col < 8; col++ ) {
                if ( checkersBoard.getPieceAt( row, col ) != null )
                    drawPiece( row, col );
            }
        }
    }

    private void drawPiece( int row, int col ) {
        if ( checkersBoard.getPieceAt( row, col ).getPieceColor().equals( "red" ) )
            drawRedPiece( row, col );
        else if ( checkersBoard.getPieceAt( row, col ).getPieceColor().equals( "blue" ) )
            drawBluePiece( row, col );
    }

    private void drawRedPiece( int row, int col ) {
        drawingArea.setFill( Color.RED );
        drawingArea.fillOval( 15 + col*80, 15 + row*80, 50, 50 );
        if ( checkersBoard.getPieceAt( row, col ).isKing() )
            drawKing( row, col );
    }

    private void drawBluePiece( int row, int col ) {
        drawingArea.setFill( Color.BLUE );
        drawingArea.fillOval( 15 + col*80, 15 + row*80, 50, 50 );
        if ( checkersBoard.getPieceAt( row, col ).isKing() )
            drawKing( row, col );
    }

    private void drawKing( int row, int col ) {
        drawingArea.setStroke( Color.WHITE );
        drawingArea.setLineWidth( 2 );
        drawingArea.strokeText( "K", 35+col*80, 45+row*80 );
    }



    private void setUpBorderPane() {
        canvasHolder = new Pane( canvas );
        paneAndMenuHolder = new BorderPane( canvasHolder );
        paneAndMenuHolder.setTop( createMenuBar() );
    }

    private void showMainWindow() {
        mainWindow.setScene( new Scene( paneAndMenuHolder ) );
        mainWindow.show();
    }

    private MenuBar createMenuBar() {
        MenuBar menus = new MenuBar();
        menus.getMenus().addAll( createFileMenu(), createControlMenu() );
        return menus;
    }

    private Menu createFileMenu() {
        Menu fileMenu = new Menu( "File" );
        fileMenu.getItems().addAll( createSaveMenuItem(), createLoadMenuItem() );
        return fileMenu;
    }

    private Menu createControlMenu() {
        Menu controlMenu = new Menu( "Control" );
        controlMenu.getItems().addAll( createNewGameMenuItem(), createQuitMenuItem() );
        return controlMenu;

    }

    // ------------------------------------------------------------------------------------------------

    private MenuItem createSaveMenuItem() {
        MenuItem save = new MenuItem( "Save" );
        save.setOnAction( event -> saveCurrentGameState() );
        return save;
    }

    /**
     * Save the user's game to a file in human-readable text format.
     */
    private void saveCurrentGameState() {
        try {
            FileChooser fileDialog = initializeFileDialog( "Select the file to be saved" );
            File selectedFile = fileDialog.showSaveDialog( mainWindow );
            saveFile( selectedFile );
        }
        catch ( IOException e ) {
            Alert errorAlert = new Alert( Alert.AlertType.ERROR, "Sorry, but an error occurred while\n" +
                    "trying to save the file." + e );
            errorAlert.showAndWait();
            return;
        }
    }

    private FileChooser initializeFileDialog( String dialogTitle ) {
        FileChooser fileDialog = new FileChooser();
        initializeNameAndDirectoryOfTheFileDialog( fileDialog );
        setFileDialogTitle( fileDialog, dialogTitle );
        return fileDialog;

    }

    private void initializeNameAndDirectoryOfTheFileDialog( FileChooser fileDialog ) {
        if ( gameIsBeingContinued )  {
            fileIsBeingEdited( fileDialog );
        }
        else
            noFileIsBeingEdited( fileDialog );
    }

    private void fileIsBeingEdited( FileChooser fileDialog ) {
        // Get the file name and directory for the dialog from the file that is being edited.
        fileDialog.setInitialFileName( gameBeingContinued.getName() );
        fileDialog.setInitialFileName( String.format( "%s.png", gameBeingContinued.getName() ));
        fileDialog.setInitialDirectory( gameBeingContinued.getParentFile() );
    }

    private void noFileIsBeingEdited( FileChooser fileDialog ) {
        // No file is being edited. So set the file name in the dialog to "filename.txt" and set the
        // directory in the dialog to the user's home directory.
        fileDialog.setInitialFileName( defaultNameOfGameFile );
        fileDialog.setInitialDirectory( new File( System.getProperty( "user.home" ) ) );
    }

    private void saveFile( File selectedFile ) throws IOException {
        if ( selectedFile == null )
            return;  // User did not select a file i.e. cancelled.
        // At this point, the user has selected a file and if the file exists has confirmed that it is OK
        // to erase the existing file.
        streamToOutputFile = createStreamForWritingToFile( selectedFile );
        writeImageContentToFile();
        closeOutputStream();
        setFileBeingEdited( selectedFile );
    }

    private void writeImageContentToFile() {
        writeElement( streamToOutputFile, "<?xml version = \"1.0\"?>", 0 );
        writeElement( streamToOutputFile, "<Checkers version = \"1.0\">", 4 );
        writeElement( streamToOutputFile, String.format( "<CurrentlyClickedRow row = '" + currentlyClickedRow + "' />" ), 8 );
        writeElement( streamToOutputFile, String.format( "<CurrentlyClickedColumn column = '" + currentlyClickedColumn + "' />" ), 8 );
        writeElement( streamToOutputFile, String.format( "<PreviouslyClickedRow row = '" + previouslyClickedRow + "' />" ), 8 );
        writeElement( streamToOutputFile, String.format( "<PreviouslyClickedColumn column = '" + previouslyClickedColumn + "' />" ), 8 );
        writeElement( streamToOutputFile, String.format( "<CurrentPlayer player = '" + currentPlayer + "' />" ), 8 );
        writePieces( streamToOutputFile );
        writeElement( streamToOutputFile, String.format( "<PieceCurrentlySelected>%s</PieceCurrentlySelected>", aPieceIsCurrentlySelected ), 8 );
        writeElement( streamToOutputFile, "</Checkers>", 4 );
    }

    private PrintWriter createStreamForWritingToFile ( File selectedFile ) throws IOException {
        return new PrintWriter( new FileWriter( selectedFile ) );
    }

    private void closeOutputStream() {
        streamToOutputFile.close();
    }

    private void setFileBeingEdited( File selectedFile ) {
        gameBeingContinued = selectedFile;
    }

    private void setFileDialogTitle( FileChooser fileDialog, String fileDialogTitle ) {
        fileDialog.setTitle( fileDialogTitle );
    }

    private void writePieces( PrintWriter out ) {
        for ( int row = 0; row < 8; row++ ) {
            for ( int col = 0; col < 8; col++ ) {
                CheckersPiece piece = checkersBoard.getPieceAt( row, col );
                if ( piece != null ) {
                    writeElement( out, String.format( "<Piece>"), 12 );
                    writeElement( out, String.format( "<PieceRow row='" + piece.getRow() + "'/>"), 16 );
                    writeElement( out, String.format( "<PieceColumn column='" + piece.getColumn() + "'/>"), 16 );
                    writeElement( out, String.format( "<King>%s</King>", piece.isKing() ), 16 );
                    writeElement( out, String.format( "<PieceColor color='" + piece.getPieceColor() + "'/>" ), 16 );
                    writeElement( out, String.format( "</Piece>" ), 12 );
                }
            }
        }
    }

    private void writeElement( PrintWriter out, String element, int indentationLevel ) {
        for ( int i = 0; i < indentationLevel; i++ )
            out.print( " " );
        out.println( element );
        out.flush();
    }


    // ------------------------------------------------------------------------------------------------

    private MenuItem createLoadMenuItem() {
        MenuItem load = new MenuItem( "Load" );
        load.setOnAction( event -> loadPreviouslySavedGame() );
        return load;
    }

    private void loadPreviouslySavedGame() {
        FileChooser fileDialog = initializeFileDialog( "Select the file to be opened." );
        File selectedFile = fileDialog.showOpenDialog( mainWindow );
        openFileWhileCheckingErrors( selectedFile );
    }

    private void openFileWhileCheckingErrors( File selectedFile ) {
        try {
            if ( selectedFile == null )
                return;  // User has not selected a file.
            openFile( selectedFile );
            drawBoard();
        }
        catch ( Exception e ) {
            checkersLogger.log( e.toString() );
            Alert errorAlert = new Alert( Alert.AlertType.ERROR, String.format("%s", e) );
            errorAlert.showAndWait();
            return;
        }
    }

    private void openFile( File selectedFile ) throws Exception {
        checkersBoard.removeAllPieces();
        xmlRepresentationOfFileBeingOpened = createTreeRepresentationOfTheXMLFile( selectedFile );
        checkVersionOfTheFile( xmlRepresentationOfFileBeingOpened.getDocumentElement() );
        processChildNodes( xmlRepresentationOfFileBeingOpened.getDocumentElement().getChildNodes() );
        setFileBeingEdited( selectedFile );
    }

    private Document createTreeRepresentationOfTheXMLFile( File selectedFile ) throws Exception {
        try {
            Document xmlDocument;
            DocumentBuilder docReader = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            xmlDocument = docReader.parse( selectedFile );
            checkersLogger.log( "xml tree successfully created." );
            return xmlDocument;
        }
        catch ( Exception e ) {
            checkersLogger.log( "Error creating tree: " + e );
            throw e;
        }
    }

    private void checkVersionOfTheFile( Element rootNodeOfTheXMLDocument ) throws Exception {
        if ( !rootNodeOfTheXMLDocument.getNodeName().equals( "Checkers" ) )
            throw new Exception( "File is not a simple paint file." );
        checkersLogger.log( "Version of file passed." );
        checkVersionNumber( rootNodeOfTheXMLDocument );
    }

    private void checkVersionNumber( Element rootNodeOfTheXMLDocument ) {
        try {
            String version = rootNodeOfTheXMLDocument.getAttribute( "version" );
            double versionNumber = Double.parseDouble( version );
            if ( versionNumber > 1.0 )
                throw new Exception( "File requires a newer version of Paint." );
            checkersLogger.log( "Version number passed." );
        }
        catch ( Exception e ) {
            checkersLogger.log( "Error getting version number " + e );
        }
    }

    private void processChildNodes( NodeList childNodes ) throws Exception {
        for ( int i = 0; i < childNodes.getLength(); i++ ) {
            if ( childNodes.item(i) instanceof Element ) {
                processElement( (Element) childNodes.item(i) );
            }
        }
    }

    private void processElement( Element elementToProcess ) throws Exception {
        if ( elementToProcess.getTagName().equals( "CurrentlyClickedRow" ) )
            currentlyClickedRow = getCurrentlyClickedRow( elementToProcess );
        else if ( elementToProcess.getTagName().equals( "CurrentlyClickedColumn" ) )
            currentlyClickedColumn = getCurrentlyClickedColumn( elementToProcess );
        else if ( elementToProcess.getTagName().equals( "PreviouslyClickedRow" ) )
            previouslyClickedRow = getPreviouslyClickedRow( elementToProcess );
        else if ( elementToProcess.getTagName().equals( "PreviouslyClickedColumn" ) )
            previouslyClickedColumn = getPreviouslyClickedColumn( elementToProcess );
        else if ( elementToProcess.getTagName().equals( "Piece" ) )
            checkersBoard.addPiece( processPiece( elementToProcess ) );
        else if ( elementToProcess.getTagName().equals( "PieceCurrentlySelected" ) )
            setPieceCurrentlySelected( elementToProcess );
    }

    private void setPieceCurrentlySelected( Element elementToProcess ) {
        if ( elementToProcess.getTextContent().equals( "true" ) ) {
            aPieceIsCurrentlySelected = true;
            pieceToMove = checkersBoard.getPieceAt( currentlyClickedRow, currentlyClickedColumn );
        }
        else
            aPieceIsCurrentlySelected = false;
    }

    private CheckersPiece processPiece( Element thePiece ) {
        CheckersPiece piece = createPieceAndInitializeKeyVariables();
        NodeList pieceNodes = thePiece.getChildNodes();
        for ( int j = 0; j < pieceNodes.getLength(); j++ ) {
            if ( pieceNodes.item(j) instanceof Element )
                processPieceElement( piece, (Element) pieceNodes.item(j) );
        }
        return piece;
    }

    private CheckersPiece createPieceAndInitializeKeyVariables() {
        CheckersPiece piece = new CheckersPiece( 0, 0, null );
        return piece;
    }

    private int getCurrentlyClickedRow( Element currentlyClickedRowElement ) {
        try {
            int row = Integer.parseInt( currentlyClickedRowElement.getAttribute( "row" ) );
            checkersLogger.log( "Currently clicked row: " + row );
            return row;
        }
        catch ( Exception e ) {
            checkersLogger.log( "Error getting currently clicked row: " + e );
            throw e;
        }
    }

    private int getCurrentlyClickedColumn( Element currentlyClickedRowElement ) {
        try {
            int col = Integer.parseInt( currentlyClickedRowElement.getAttribute( "column" ) );
            checkersLogger.log( "Currently clicked col: " + col );
            return col;
        }
        catch ( Exception e ) {
            checkersLogger.log( "Error getting currently clicked col: " + e );
            throw e;
        }
    }

    private int getPreviouslyClickedRow( Element previouslyClickedRowElement ) {
        try {
            int row = Integer.parseInt( previouslyClickedRowElement.getAttribute( "row" ) );
            checkersLogger.log( "Previously clicked row: " + row );
            return row;
        }
        catch ( Exception e ) {
            checkersLogger.log( "Error getting previously clicked row " + e );
            throw e;
        }
    }

    private int getPreviouslyClickedColumn( Element previouslyClickedRowElement ) {
        try {
            int column = Integer.parseInt( previouslyClickedRowElement.getAttribute( "column" ) );
            checkersLogger.log( "Previously clicked column: " + column );
            return column;
        }
        catch ( Exception e ) {
            checkersLogger.log( "Error getting previously clicked column " + e );
            throw e;
        }
    }


    private void processPieceElement( CheckersPiece piece, Element pieceElement ) {
        if ( pieceElement.getTagName().equals( "PieceRow" ) ) {
            piece.setRowOfPiece( getRowNumber( pieceElement ) );
        }
        else if ( pieceElement.getTagName().equals( "PieceColumn" ) ) {
            piece.setColumnOfPiece( getColumnNumber( pieceElement ) );
        }
        else if ( pieceElement.getTagName().equals( "PieceColor" ) ) {
            piece.setPieceColor( getPieceColor( pieceElement ) );
        }
        else if ( pieceElement.getTagName().equals( "King" ) ) {
            if ( setPieceKingProperty( pieceElement ) )
                piece.setKing();
        }
    }

    private int getRowNumber( Element rowElement ) {
        try {
            String rowAttribute = rowElement.getAttribute( "row" );
            int row = Integer.parseInt( rowAttribute );
            checkersLogger.log("Gotten row number: " + row );
            return row;
        }
        catch ( Exception e ) {
            checkersLogger.log( "Error getting row number " + e );
            throw e;
        }
    }

    private int getColumnNumber( Element columnElement ) {
        try {
            String columnAttribute = columnElement.getAttribute( "column" );
            int column = Integer.parseInt( columnAttribute );
            checkersLogger.log( "Gotten column number: " + column );
            return column;
        }
        catch ( Exception e ) {
            checkersLogger.log( "Error getting column number: " + e );
            throw e;
        }
    }

    private String getPieceColor( Element colorElement ) {
        try {
            String color = colorElement.getAttribute( "color" );
            checkersLogger.log( "Gotten color: " + color );
            return color;
        }
        catch ( Exception e ) {
            checkersLogger.log( "Error getting piece color " + e );
            throw e;
        }
    }

    private boolean setPieceKingProperty( Element kingElement ) {
        try {
            String kingProperty = kingElement.getTextContent();
            if ( kingProperty.equals( "true" ) )
                return true;
            return false;
        }
        catch ( Exception e ) {
            checkersLogger.log( "Error getting king property " + e );
            throw e;
        }
    }

    // -------------------------------------------------------------------------------------------------
    private MenuItem createNewGameMenuItem() {
        MenuItem newGame = new MenuItem( "New Game" );
        newGame.setOnAction( event -> doNewGame() );
        return newGame;
    }

    private void doNewGame() {
        createNewCheckerBoard();
        initializeCurrentPlayer();
        initializeKeyVariables();
        drawBoard();
    }

    private void createNewCheckerBoard() {
        checkersBoard = new CheckersBoard();
    }

    private void initializeCurrentPlayer() {
        currentPlayer = RED_PLAYER;
    }

    private void initializeKeyVariables() {
        pieceToMove = null;
        aPieceIsCurrentlySelected = false;
    }

    // -------------------------------------------------------------------------------------------------

    private MenuItem createQuitMenuItem() {
        MenuItem quit = new MenuItem( "Quit" );
        quit.setOnAction( event -> quitGame() );
        return quit;
    }

    private void quitGame() {
        System.exit(1);
    }

}
