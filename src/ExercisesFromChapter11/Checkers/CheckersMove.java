package ExercisesFromChapter11.Checkers;

public class CheckersMove {

    private int fromRow;
    private int fromCol;
    private int toRow;
    private int toCol;

    public CheckersMove( int fromRow, int fromCol, int toRow, int toCol ) {
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
    }

    public int getFromRow() {
        return this.fromRow;
    }

    public int getFromCol() {
        return this.fromCol;
    }

    public int getToRow() {
        return this.toRow;
    }

    public int getToCol() {
        return this.toCol;
    }

    public boolean isJump() {
        return Math.abs( this.toRow - this.fromRow ) == 2;
    }
}
