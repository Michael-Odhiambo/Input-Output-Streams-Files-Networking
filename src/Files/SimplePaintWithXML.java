/**
 * Author: Michael Odhiambo.
 * Email: michaelallanodhiambo@gmail.com
 */

package Files;

import javafx.application.Application;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.layout.Pane;
import javafx.scene.layout.BorderPane;
import javafx.scene.input.MouseEvent;
import javafx.geometry.Point2D;
import java.util.ArrayList;

import java.io.*;


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

    private Color currentDrawingColor;
    private Color currentBackgroundColor;
    private GraphicsContext drawingArea;
    private boolean currentlyDrawing = false;
    private CurveData currentCurve = null;

    private String backgroundMenu = "backgroundMenu";
    private String colorMenu = "colorMenu";
    private boolean useSymmetryCheck;

    private String nameOfFileBeingEdited = "Untitled";
    private File fileBeingEdited = null;


    

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
        controlMenu.getItems().add( createUseSymmetryRadioMenuItem() );

    }

    private void createColorMenuItems( Menu theColorMenu ) {
        createColorGroup( theColorMenu, colorMenu );
        System.out.println( "Current Drawing color: " + currentDrawingColor );

    }

    private void createBackgroundColorMenuItems( Menu backgroundColorMenu ) {
        createColorGroup( backgroundColorMenu, backgroundMenu );

    }


    private MenuItem createNewMenuItem() {
        MenuItem newFile = new MenuItem( "New" );
        newFile.setOnAction( event -> createNewFile() );
        return newFile;

    }

    private MenuItem createSaveMenuItem() {
        MenuItem save = new MenuItem( "Save" );
        save.setOnAction( event -> saveImageAsXmlFile() );
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
        quit.setOnAction( event -> quit() );
        return quit;
    }

    private MenuItem createUndoMenuItem() {
        MenuItem undo = new MenuItem( "Undo" );
        undo.setOnAction( event -> undoAction() );
        return undo;
    }

    private MenuItem createClearMenuItem() {
        MenuItem clear = new MenuItem( "Clear" );
        clear.setOnAction( event -> clearDrawingArea() );
        return clear;
    }

    private MenuItem createUseSymmetryRadioMenuItem() {
        RadioMenuItem useSymmetry = new RadioMenuItem( "Use Symmetry" );
        useSymmetry.setSelected( false );
        //useSymmetry.setOnAction( event -> useSymmetryWhileDrawing() );
        return useSymmetry;
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

    private void respondToMouseReleasedOnTheCanvas() {
        doneDrawing();

    }

    private void undoAction() {
        removeRecentlyDrawnCurve();
        redrawEntireImage();
    }

    private void redrawEntireImage() {
        redrawCanvas();
    }

    private void redrawCanvas() {
        drawBackground();
        drawAllAvailableCurves();
    }

    private void drawAllAvailableCurves() {
        for ( CurveData curve : curves )
            redrawCurve( curve );

    }

    private void clearDrawingArea() {
        emptyCurvesList();
        redrawEntireImage();
    }


    private void createNewFile() {
        emptyCurvesList();
        redrawCanvas();
        reInitializeKeyVariables();

    }
    // --------------------------------------------------------------------------------------------------

    /**
     * Save the user's image to a file in human-readable text format.
     */
    private void saveImageAsXmlFile() {
        FileChooser fileDialog = initializeFileDialog( false );
        File selectedFile = fileDialog.showSaveDialog( mainWindow );

        try {
            saveFile( selectedFile );
        }
        catch ( IOException e ) {
            Alert errorAlert = new Alert( Alert.AlertType.ERROR, "Sorry, but an error occurred while\n" +
                    "trying to save the file." );
            errorAlert.showAndWait();
        }

    }


    private FileChooser initializeFileDialog( boolean isOpenFileDialog ) {
        FileChooser fileDialog = new FileChooser();
        initializeNameAndDirectoryOfTheFileDialog( fileDialog );
        setFileDialogTitle( fileDialog, isOpenFileDialog );

        return fileDialog;

    }

    private void initializeNameAndDirectoryOfTheFileDialog( FileChooser fileDialog ) {
        if ( fileBeingEdited == null )  {
            noFileIsBeingEdited( fileDialog );
        }
        else
            fileIsBeingEdited( fileDialog );
    }

    private void fileIsBeingEdited( FileChooser fileDialog ) {
        // Get the file name and directory for the dialog from the file that is being edited.
        fileDialog.setInitialFileName( fileBeingEdited.getName() );
        fileDialog.setInitialDirectory( fileBeingEdited.getParentFile() );
    }

    private void noFileIsBeingEdited( FileChooser fileDialog ) {
        // No file is being edited. So set the file name in the dialog to "filename.xml" and set the
        // directory in the dialog to the user's home directory.
        fileDialog.setInitialFileName( "filename.txt" );
        fileDialog.setInitialDirectory( new File( System.getProperty( "user.home" ) ) );
    }

    private void setFileDialogTitle( FileChooser fileDialog, boolean isOpenFileDialog ) {
        if ( isOpenFileDialog )
            fileDialog.setTitle( "Select file to be opened" );
        else
            fileDialog.setTitle( "Select file to be saved" );
    }

    private void saveFile( File selectedFile ) throws IOException {
        if ( selectedFile == null )
            return;  // User did not select a file i.e. cancelled.
        // At this point, the user has selected a file and if the file exists has confirmed that it is OK
        // to erase the existing file.
        PrintWriter out = createStreamForWritingToFile( selectedFile );
        writeImageContentToFile( out );
        out.close();
        fileBeingEdited = selectedFile;
    }

    private PrintWriter createStreamForWritingToFile ( File selectedFile ) throws IOException {
        PrintWriter out = new PrintWriter( new FileWriter( selectedFile ) );
        return out;
    }

    private void writeImageContentToFile( PrintWriter out ) {
        writeElement( out, "<?xml version = \"1.0\"?>", 0 );
        writeElement( out, "<Simplepaint version = \"1.0\">", 4 );
        writeElement( out, String.format( "<background red = '" + currentBackgroundColor.getRed() + "' green = '" +
                currentBackgroundColor.getGreen() + "' blue = '" + currentBackgroundColor.getBlue() + "'/>" ), 8 );

    }

    private void writeElement( PrintWriter out, String element, int indentationLevel ) {
        for ( int i = 0; i < indentationLevel; i++ )
            out.print( " " );
        out.println( element );
        out.flush();
    }

    //-----------------------------------------------------------------------------------------------


    private void reInitializeKeyVariables() {
        currentlyDrawing = false;
        currentCurve = null;
        nameOfFileBeingEdited = "Untitled";
    }

    private void quit() {
        System.exit(1);
    }
    private void removeRecentlyDrawnCurve() {
        if ( curves.size() > 0 )
            curves.remove( curves.remove( curves.size() - 1 ) );

    }

    private void emptyCurvesList() {
        curves = new ArrayList<>();
    }

    private void redrawCurve( CurveData curve ) {
        Point2D startingPoint = curve.getPoints().get(0);

        for ( int i = 1; i < curve.getPoints().size(); i++ ) {
            drawSegment( startingPoint, curve.getPoints().get(i) );
            startingPoint = curve.getPoints().get(i);
        }

    }


    private void drawBackground() {
        drawingArea.setFill( currentBackgroundColor );
        drawingArea.fillRect( 0, 0, CANVAS_WIDTH, CANVAS_HEIGHT );
    }


    private void createColorGroup( Menu colorMenu, String menu ) {
        ToggleGroup colorGroup = new ToggleGroup();

        for ( int i = 0; i < colorNames.length; i++ ) {
            createColorRadioMenuItem( colorMenu, colorGroup, i, menu );
        }
        addListenerToTheColorToggleGroup( colorGroup, menu );
    }


    private void createColorRadioMenuItem( Menu colorMenu, ToggleGroup colorGroup, int specifiedIndexPosition,
                                           String menu ) {
        createColor( colorMenu, colorGroup, specifiedIndexPosition, menu );
    }


    private RadioMenuItem createColor( Menu colorMenu, ToggleGroup colorGroup, int specifiedIndexPosition,
                                       String menu ) {
        RadioMenuItem color = new RadioMenuItem( colorNames[ specifiedIndexPosition ] );
        color.setUserData( Integer.valueOf( specifiedIndexPosition ) );
        color.setToggleGroup( colorGroup );
        setAsDefaultSelectedColor( color, specifiedIndexPosition, menu );
        colorMenu.getItems().add( color );

        return color;

    }


    private void setAsDefaultSelectedColor( RadioMenuItem color, int specifiedIndexPosition, String menu ) {
        if ( menu.equals( colorMenu ) && specifiedIndexPosition == 0 )
            setSelectedBackgroundColor( color );

        else if ( menu.equals( backgroundMenu ) && specifiedIndexPosition == 1 )
            setSelectedDrawingColor( color );

    }


    private void setSelectedDrawingColor( RadioMenuItem color ) {
        System.out.println( color.getUserData() );
        color.setSelected( true );
        currentBackgroundColor = availableColors[ (Integer) color.getUserData() ];
    }

    private void setSelectedBackgroundColor( RadioMenuItem color ) {
        System.out.println( color.getUserData() );
        color.setSelected( true );
        currentDrawingColor = availableColors[ (Integer) color.getUserData() ];
    }



    private void addListenerToTheColorToggleGroup( ToggleGroup colorGroup, String menu ) {
        colorGroup.selectedToggleProperty().addListener( ( event, oldValue, newValue )  -> {
            if ( newValue != null ) {
                if ( menu.equals( backgroundMenu ) ) {
                    currentBackgroundColor = availableColors[(Integer) newValue.getUserData()];
                    redrawEntireImage();
                }
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
        canvas.setOnMouseReleased( event -> respondToMouseReleasedOnTheCanvas() );

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
        Point2D currentPoint = new Point2D( event.getX()+0.5, event.getY()+0.5 );
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
        currentCurve.addPoint( new Point2D( event.getX()+0.5, event.getY()+0.5 ) );

    }

    private void setUpScene() {
        scene = new Scene( root );
    }

    private void showMainWindow( Stage stage ) {
        mainWindow = stage;
        mainWindow.setResizable( false );
        mainWindow.setScene( scene );
        mainWindow.setTitle( "Paint with XML: " + nameOfFileBeingEdited );
        mainWindow.show();
    }



}
