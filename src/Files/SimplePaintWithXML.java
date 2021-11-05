/**
 * Author: Michael Odhiambo.
 * Email: michaelallanodhiambo@gmail.com
 */

package Files;

import javafx.application.Application;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.layout.Pane;
import javafx.scene.layout.BorderPane;
import javafx.scene.input.MouseEvent;
import javafx.geometry.Point2D;
import java.util.ArrayList;


public class SimplePaintWithXML extends Application {

    private Stage mainWindow;
    private Canvas canvas;
    Pane canvasHolder;
    BorderPane root;  // The root holds all the elements in this program.
    Scene scene;      // The scene holds the root.

    private final double CANVAS_WIDTH = 650;
    private final double CANVAS_HEIGHT = 650;

    private Color[] availableColors = { Color.BLACK, Color.WHITE, Color.RED, Color.GREEN, Color.BLUE, Color.CYAN,
    Color.MAGENTA, Color.YELLOW, Color.GRAY, Color.BROWN, Color.PURPLE, Color.PINK, Color.ORANGE };

    private String[] colorNames = { "Black", "White", "Red", "Green", "Blue", "Cyan", "Magenta", "Yellow", "Gray",
    "Brown", "Purple", "Pink", "Orange" };

    Color currentDrawingColor;
    Color currentBackgroundColor;
    GraphicsContext drawingArea;
    boolean currentlyDrawing = false;
    CurveData currentCurve = null;

    private ArrayList<CurveData> curves = new ArrayList<>();

    public static void main( String[] args ) {
        launch( args );
    }

    public void start( Stage stage ) {
        setUpMainWindow( stage );

    }

    private void setUpMainWindow( Stage stage ) {
        setUpCanvas();
        setUpMenuAndScene();
        showMainWindow( stage );
    }

    private void setUpMenuAndScene() {
        root.setTop( createMenuBar() );
        setUpScene();

    }

    private MenuBar createMenuBar() {
        MenuBar menus = new MenuBar();
        menus.getMenus().addAll( createFileMenu(), createControlMenu(), createColorMenu(), createBackGroundColorMenu() );
        return menus;

    }

    private Menu createFileMenu() {
        Menu fileMenu = new Menu( "File" );
        createFileMenuItems( fileMenu );
        return fileMenu;

    }

    private Menu createControlMenu() {
        Menu controlMenu = new Menu( "Control" );
        createControlMenuItems( controlMenu );
        return controlMenu;


    }

    private Menu createColorMenu() {
        Menu colorMenu = new Menu( "Color" );
        createColorMenuItems( colorMenu );
        return colorMenu;
    }

    private Menu createBackGroundColorMenu() {
        Menu backgroundColorMenu = new Menu( "Background Color" );
        createBackgroundColorMenuItems( backgroundColorMenu );
        return backgroundColorMenu;
    }

    private void createFileMenuItems( Menu fileMenu ) {
        fileMenu.getItems().add( createNewMenuItem() );
        fileMenu.getItems().add( new SeparatorMenuItem() );
        fileMenu.getItems().add( createSaveMenuItem() );
        fileMenu.getItems().add( createOpenMenuItem() );
        fileMenu.getItems().add( new SeparatorMenuItem() );
        fileMenu.getItems().add( createSavePNGImageMenuItem() );
        fileMenu.getItems().add( createQuitMenuItem() );

    }

    private void createControlMenuItems( Menu controlMenu ) {
        controlMenu.getItems().add( createUndoMenuItem() );
        controlMenu.getItems().add( createClearMenuItem() );
        controlMenu.getItems().add( createUseSymmetryMenuItem() );

    }

    private void createColorMenuItems( Menu colorMenu ) {
        createColorGroup( colorMenu, "ColorMenu" );

    }

    private void createBackgroundColorMenuItems( Menu backgroundColorMenu ) {
        createColorGroup( backgroundColorMenu, "backgroundColorMenu" );

    }


    private MenuItem createNewMenuItem() {
        MenuItem newFile = new MenuItem( "New" );
        //newFile.setOnAction( event -> createNewFile() );
        return newFile;

    }

    private MenuItem createSaveMenuItem() {
        MenuItem save = new MenuItem( "Save" );
        //save.setOnAction( event -> saveDrawing() );
        return save;
    }

    private MenuItem createOpenMenuItem() {
        MenuItem open = new MenuItem( "Open" );
        //open.setOnAction( event -> openFile() );
        return open;
    }

    private MenuItem createSavePNGImageMenuItem() {
        MenuItem savePNGImage = new MenuItem( "Save PNG Image" );
        //savePNGImage.setOnAction( event -> saveDrawingAsPNGFile() );
        return savePNGImage;
    }

    private MenuItem createQuitMenuItem() {
        MenuItem quit = new MenuItem( "Quit" );
        //quit.setOnAction( event -> quit() );
        return quit;
    }

    private MenuItem createUndoMenuItem() {
        MenuItem undo = new MenuItem( "Undo" );
        //undo.setOnAction( event -> undoAction() );
        return undo;
    }

    private MenuItem createClearMenuItem() {
        MenuItem clear = new MenuItem( "Clear" );
        //clear.setOnAction( event -> clearDrawingArea() );
        return clear;
    }

    private MenuItem createUseSymmetryMenuItem() {
        MenuItem useSymmetry = new MenuItem( "Use Symmetry" );
        //useSymmetry.setOnAction( event -> useSymmetryWhileDrawing() );
        return useSymmetry;
    }

    private void createColorGroup( Menu colorMenu, String menu ) {
        ToggleGroup colorGroup = new ToggleGroup();

        for ( int i = 0; i < colorNames.length; i++ ) {
            createColorRadioMenuItem( colorMenu, colorGroup, i );
        }
        addListenerToTheColorToggleGroup( colorGroup, menu );
    }

    private void createColorRadioMenuItem( Menu colorMenu, ToggleGroup colorGroup, int specifiedIndexPosition ) {
        RadioMenuItem color = new RadioMenuItem( colorNames[ specifiedIndexPosition ] );
        colorMenu.getItems().add( color );
        color.setUserData( Integer.valueOf( specifiedIndexPosition ) );
        color.setToggleGroup( colorGroup );

        if ( specifiedIndexPosition == 0 )
            color.setSelected( true );
    }


    private void addListenerToTheColorToggleGroup( ToggleGroup colorGroup, String menu ) {
        colorGroup.selectedToggleProperty().addListener( ( event, oldValue, newValue )  -> {
            if ( newValue != null ) {
                if ( menu.equals( "backgroundColorMenu" ) )
                    currentBackgroundColor = availableColors[ (Integer) newValue.getUserData() ];
                else
                    currentDrawingColor = availableColors[ (Integer) newValue.getUserData() ];

            }
        } );
    }


    private void setUpCanvas() {
        canvas = new Canvas( CANVAS_WIDTH, CANVAS_HEIGHT );
        drawingArea = canvas.getGraphicsContext2D();
        setUpEventListenersOnTheCanvas();
        canvasHolder = new Pane( canvas );
        root = new BorderPane( canvasHolder );
    }

    private void setUpEventListenersOnTheCanvas() {
        canvas.setOnMousePressed( event -> respondToMousePressOnTheCanvas( event ) );
        canvas.setOnMouseDragged( event -> respondToMouseDraggedOnTheCanvas( event ) );
        canvas.setOnMouseReleased( event -> respondToMouseReleasedOnTheCanvas( event ) );

    }

    private void respondToMousePressOnTheCanvas( MouseEvent event ) {
        if ( currentlyDrawing )
            return;  // Ignore this mouse button press since the user is drawing.

        primeVariablesForDrawing( event );

    }

    private void respondToMouseDraggedOnTheCanvas( MouseEvent event ) {
        if ( !currentlyDrawing )
            return;  // The user isn't drawing.

        drawCurve( event );
    }

    private void respondToMouseReleasedOnTheCanvas( MouseEvent event ) {
        doneDrawing();

    }

    private void doneDrawing() {
        if ( currentlyDrawing ) {
            currentlyDrawing = false;
            if ( currentCurve.getPoints().size() > 1 )
                curves.add( currentCurve );
            currentCurve = null;
        }
    }

    private void drawCurve( MouseEvent event ) {
        Point2D currentPoint = new Point2D( event.getX(), event.getY() );
        Point2D previousPoint = currentCurve.getPoints().get( currentCurve.getPoints().size() - 1 );
        currentCurve.addPoint( currentPoint );

        drawSegment ( previousPoint, currentPoint );

    }

    private void drawSegment( Point2D from, Point2D to ) {
        drawingArea.strokeLine( from.getX(), from.getY(), to.getX(), to.getY() );
    }



    private void primeVariablesForDrawing( MouseEvent event ) {
        currentlyDrawing = true;
        drawingArea.setStroke( currentDrawingColor );
        currentCurve = new CurveData();
        currentCurve.setColor( currentDrawingColor );
        currentCurve.addPoint( new Point2D( event.getX(), event.getY() ) );

    }

    private void setUpScene() {
        scene = new Scene( root );
    }

    private void showMainWindow( Stage stage ) {
        mainWindow = stage;
        mainWindow.setScene( scene );
        mainWindow.setTitle( "Paint with XML" );
        mainWindow.show();
    }



}
