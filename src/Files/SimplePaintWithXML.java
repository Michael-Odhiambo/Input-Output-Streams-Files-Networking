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

import org.w3c.dom.*;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.*;


public class SimplePaintWithXML extends Application {

    private Stage mainWindow;
    private Canvas canvas;
    Pane canvasHolder;
    BorderPane root;  // The root holds all the elements in this program.
    Scene scene;      // The scene holds the root.

    private final double CANVAS_WIDTH = 750;
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

    private PrintWriter streamToOutputFile;
    Document xmlRepresentationOfFileBeingOpened;
    private ArrayList<CurveData> curves = new ArrayList<>();


    /**
     * These variables are used to hold the background color and curves when a new file
     * is being opened.
     */
    private Color newBackgroundColor;
    private ArrayList<CurveData> newCurves = new ArrayList<>();

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
        open.setOnAction( event -> openXMLFileRepresentationOfTheImage() );
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


    /**
     * Save the user's image to a file in human-readable text format.
     */
    private void saveImageAsXmlFile() {
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
        if ( fileBeingEdited == null )  {
            noFileIsBeingEdited( fileDialog );
        }
        else
            fileIsBeingEdited( fileDialog );
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
        writeElement( streamToOutputFile, "<simplepaint version = \"1.0\">", 4 );
        writeElement( streamToOutputFile, String.format( "<background red = '" + currentBackgroundColor.getRed() + "' green = '" +
                currentBackgroundColor.getGreen() + "' blue = '" + currentBackgroundColor.getBlue() + "'/>" ), 8 );
        writeCurves( streamToOutputFile );
        writeElement( streamToOutputFile, "</simplepaint>", 4 );

    }


    private PrintWriter createStreamForWritingToFile ( File selectedFile ) throws IOException {
        return new PrintWriter( new FileWriter( selectedFile ) );
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

    private void setFileDialogTitle( FileChooser fileDialog, String fileDialogTitle ) {
        fileDialog.setTitle( fileDialogTitle );
    }


    private void writeCurves( PrintWriter out ) {
        for ( CurveData curve : curves ) {
            writeElement( out, String.format( "<Curve>"), 12 );
            writeElement( out, String.format( "<color red='" + curve.getColor().getRed() + "' green='" +
                    curve.getColor().getGreen() + "' blue='" + curve.getColor().getBlue() + "'/>"), 16 );
            writeElement( out, String.format( "<symmetric>" + curve.isSymmetric() + "</symmetric>" ), 20 );
            writePoints( out, curve );
            writeElement( out, String.format( "</Curve>" ), 12 );
        }
    }

    private void writePoints( PrintWriter out, CurveData curve ) {
        for ( Point2D point : curve.getPoints() ) {
            writeElement( out, String.format( "<point x='" + point.getX() + "' y='" + point.getY() + "'/>"), 24 );
        }
    }

    private void writeElement( PrintWriter out, String element, int indentationLevel ) {
        for ( int i = 0; i < indentationLevel; i++ )
            out.print( " " );
        out.println( element );
        out.flush();
    }

    private void closeOutputStream() {
        streamToOutputFile.close();
    }

    private void setFileBeingEdited( File selectedFile ) {
        fileBeingEdited = selectedFile;
        mainWindow.setTitle( "Paint with XML: " + fileBeingEdited.getName() );
    }

    //-----------------------------------------------------------------------------------------------

    /**
     * Read an image from a file into the drawing area. The format of the file must be the same as that used in the
     * saveImageAsXMLFile() method.
     */
    private void openXMLFileRepresentationOfTheImage() {
        FileChooser fileDialog = initializeFileDialog( "Select the file to be opened." );
        File selectedFile = fileDialog.showOpenDialog( mainWindow );
        openFileWhileCheckingErrors( selectedFile );

    }


    private void openFileWhileCheckingErrors( File selectedFile ) {
        try {
            openFile( selectedFile );
            drawSelectedFile();
        }
        catch ( Exception e ) {
            Alert errorAlert = new Alert( Alert.AlertType.ERROR, String.format("%s", e) );
            errorAlert.showAndWait();
            return;
        }
    }

    private void drawSelectedFile() {
        curves = newCurves;
        currentBackgroundColor = newBackgroundColor;
        redrawEntireImage();
    }


    private void openFile( File selectedFile ) throws Exception {
        if ( selectedFile == null )
            return;  // User did not select a file, i.e. cancelled.
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
            return xmlDocument;
        }
        catch ( Exception e ) {
            throw e;
        }
    }

    private void checkVersionOfTheFile( Element rootNodeOfTheXMLDocument ) throws Exception {
        if ( !rootNodeOfTheXMLDocument.getNodeName().equals( "simplepaint" ) )
            throw new Exception( "File is not a simple paint file." );
        checkVersionNumber( rootNodeOfTheXMLDocument );
    }


    private void checkVersionNumber( Element rootNodeOfTheXMLDocument ) throws Exception {
        try {
            String version = rootNodeOfTheXMLDocument.getAttribute( "version" );
            double versionNumber = Double.parseDouble( version );
            if ( versionNumber > 1.0 )
                throw new Exception( "File requires a newer version of Paint." );
        }
        catch ( Exception e ) {
            throw e;
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
        if ( elementToProcess.getTagName().equals( "background" ) ) {
            newBackgroundColor = getNewBackgroundColor(elementToProcess);
        }
        else if ( elementToProcess.getTagName().equals( "Curve" ) ) {
            newCurves.add( processCurve( elementToProcess ) );
        }
    }


    private Color getNewBackgroundColor( Element backgroundColorElement ) throws Exception {
        try {
            double red = Double.parseDouble( backgroundColorElement.getAttribute( "red" ) );
            double green = Double.parseDouble( backgroundColorElement.getAttribute( "green" ) );
            double blue = Double.parseDouble( backgroundColorElement.getAttribute( "blue" ) );
            return Color.color( red, green, blue );
        }
        catch ( Exception e ) {
            throw new Exception(e);
        }
    }

    private CurveData processCurve( Element theCurve ) throws Exception {
        CurveData curve = createCurveAndInitializeKeyVariables();
        NodeList curveNodes = theCurve.getChildNodes();
        for ( int j = 0; j < curveNodes.getLength(); j++ ) {
            if ( curveNodes.item(j) instanceof Element )
                processCurveElement( curve, (Element) curveNodes.item(j) );
        }
        return curve;
    }

    private CurveData createCurveAndInitializeKeyVariables() {
        CurveData curve = new CurveData();
        curve.setColor( Color.BLACK );
        return curve;
    }

    private void processCurveElement( CurveData curve, Element curveElement ) throws Exception {
        if ( curveElement.getTagName().equals( "color" ) ) {
            curve.setColor( getCurveColor( curveElement ) );
        }
        else if ( curveElement.getTagName().equals( "point" ) ) {
            curve.addPoint( getCurvePoint( curveElement ) );
        }
        else if ( curveElement.getTagName().equals( "symmetric" ) ) {
            curve.setSymmetricProperty( getCurveSymmetricProperty( curveElement ) );
        }
    }


    private Color getCurveColor( Element curveElement ) throws Exception {
        try {
            double red = Double.parseDouble( curveElement.getAttribute( "red" ) );
            double green = Double.parseDouble( curveElement.getAttribute( "green" ) );
            double blue = Double.parseDouble( curveElement.getAttribute( "blue" ) );
            return Color.color( red, green, blue );
        }
        catch ( Exception e ) {
            throw new Exception( e );
        }
    }


    private Point2D getCurvePoint( Element curveElement ) throws Exception {
        try {
            double x = Double.parseDouble( curveElement.getAttribute( "x" ) );
            double y = Double.parseDouble( curveElement.getAttribute( "y" ) );
            return new Point2D( x, y );
        }
        catch ( Exception e ) {
            throw new Exception( e );
        }
    }


    private boolean getCurveSymmetricProperty( Element curveElement ) {
        String symmetric = curveElement.getTextContent();
        if ( symmetric.equals( "true" ) )
            return true;
        return false;
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
            drawingArea.setStroke( curve.getColor() );
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


    private void createColorRadioMenuItem( Menu menuTheColorBelongs, ToggleGroup groupTheColorBelongs, int indexPositionOfTheColor,
                                           String menu ) {
        createColor( menuTheColorBelongs, groupTheColorBelongs, indexPositionOfTheColor, menu );
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
        color.setSelected( true );
        currentBackgroundColor = availableColors[ (Integer) color.getUserData() ];
    }
    

    private void setSelectedBackgroundColor( RadioMenuItem color ) {
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
