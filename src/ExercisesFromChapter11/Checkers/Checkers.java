package ExercisesFromChapter11.Checkers;

import javafx.application.Application;
import javafx.scene.layout.BorderPane;
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

    private CheckersBoard checkersBoard = new CheckersBoard();

    public static void main( String[] args ) {
        launch( args );
    }

    public void start( Stage stage ) {
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
        System.out.println( "Getting piece to move." );
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
        System.out.println( "Highlighting piece." );
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
        //save.setOnAction( event -> saveCurrentState() );
        return save;
    }

    private MenuItem createLoadMenuItem() {
        MenuItem load = new MenuItem( "Load" );
        //load.setOnAction( event -> loadGame() );
        return load;
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
