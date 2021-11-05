package Files;

import javafx.scene.paint.Color;
import java.util.List;
import javafx.geometry.Point2D;
import java.util.ArrayList;

/**
 * This class represents a curve in the SimplePaintWithXML program.
 */
class CurveData {

    private Color curveColor;
    private boolean isSymmetric;
    private List<Point2D> curvePoints;

    public CurveData() {
        isSymmetric = false;
        curveColor = null;
        curvePoints = new ArrayList<>();
    }

    boolean isSymmetric() {
        return isSymmetric;
    }

    List<Point2D> getPoints() {
        return curvePoints;
    }

    Color getColor() {
        return curveColor;
    }

    void setColor( Color newColor ) {
        curveColor = newColor;
    }

    void setSymmetricProperty( boolean newValue ) {
        isSymmetric = newValue;
    }

    void addPoint( Point2D point ) {
        curvePoints.add( point );
    }

}
