package ExercisesFromChapter11.Checkers;

public class CheckersPiece {


    private int rowOfPiece;
    private int columnOfPiece;
    private String pieceColor;
    private boolean pieceIsKing = false;
    private String kingType;

    public CheckersPiece( int row, int column, String color ) {
        rowOfPiece = row;
        columnOfPiece = column;
        pieceColor = color;
    }

    public int getRow() {
        return rowOfPiece;
    }

    public int getColumn() {
        return columnOfPiece;
    }

    public String getPieceColor() {
        return pieceColor;
    }

    public void setRowAndColumn( int row, int col ) {
        rowOfPiece = row;
        columnOfPiece = col;
    }

    public void setKing() {
        pieceIsKing = true;
    }

    public boolean isKing() {
        return pieceIsKing;
    }

    public String getKingType() {
        return String.format( "%sKing", pieceColor );
    }


}
