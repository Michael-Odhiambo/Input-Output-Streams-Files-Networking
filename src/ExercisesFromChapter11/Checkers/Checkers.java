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
            aPieceIsCurrentlySelected = false;
        }
        else if ( checkersBoard.getPieceAt( currentlyClickedRow, currentlyClickedColumn ) != null ) {
            getPieceToBeMoved( currentlyClickedRow, currentlyClickedColumn );
        }
        drawBoard();

    }

    private void setCurrentlyClickedRowAndColumn( MouseEvent event ) {
        currentlyClickedColumn = (int)( event.getX() / 80 );
        currentlyClickedRow = (int)( event.getY() / 80 );
    }

    private void movePieceToToSelectedSquare() {
        CheckersMove move = new CheckersMove( pieceToMove.getRow(), pieceToMove.getColumn(), currentlyClickedRow, currentlyClickedColumn );
        if ( selectedPieceCanMove( move ) ) {
            checkersBoard.movePiece( pieceToMove, move );

        }
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
        if ( checkersBoard.getPieceAt( fromRow, fromCol ) != null ) {
            pieceToMove = checkersBoard.getPieceAt( fromRow, fromCol );
            aPieceIsCurrentlySelected = true;
        }

    }

    private void drawBoard() {
        drawCheckerBoardOnCanvas();
        drawPlayerPiecesOnTheBoard();
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
    }

    private void drawBluePiece( int row, int col ) {
        drawingArea.setFill( Color.BLUE );
        drawingArea.fillOval( 15 + col*80, 15 + row*80, 50, 50 );
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
        return controlMenu;

    }

    private MenuItem createSaveMenuItem() {
        MenuItem save = new MenuItem( "Save" );
        //save.setOnAction( event -> saveCurrentGameState() );
        return save;
    }

    private MenuItem createLoadMenuItem() {
        MenuItem load = new MenuItem( "Load" );
        //load.setOnAction( event -> loadGame() );
        return load;
    }
}
