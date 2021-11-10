package ExercisesFromChapter11.Checkers;

import java.util.ArrayList;
import java.util.List;

public class CheckersBoard {

    private int BLUE_PIECE = 1;
    private int RED_PIECE = 2;
    private CheckersPiece[][] checkersBoard = new CheckersPiece[8][8];

    public CheckersBoard() {
        initializeBoard();
    }

    private void initializeBoard() {
        placeBluePiecesOnTheBoard();
        placeRedPiecesOnTheBoard();
    }

    private void placeBluePiecesOnTheBoard() {
        for ( int row = 5; row < 8; row++ ) {
            for ( int col = 0; col < 8; col++ ) {
                if ( row % 2 == col % 2 )
                    checkersBoard[row][col] = new CheckersPiece( row, col, "blue" );
            }
        }
    }

    private void placeRedPiecesOnTheBoard() {
        for ( int row = 0; row < 3; row++ ) {
            for ( int col = 0; col < 8; col++ ) {
                if ( row % 2 == col % 2 )
                    checkersBoard[row][col] = new CheckersPiece( row, col, "red" );
            }
        }
    }

    public CheckersPiece getPieceAt( int row, int col ) {
        if ( checkersBoard[row][col] != null )
            return checkersBoard[row][col];
        return null;
    }

    public void movePiece( CheckersPiece piece, CheckersMove move ) {
        removePieceFrom( piece.getRow(), piece.getColumn() );
        piece.setRowAndColumn( move.getToRow(), move.getToCol() );
        checkersBoard[move.getToRow()][move.getToCol()] = piece;

    }

    private void removePieceFrom( int row, int col ) {
        checkersBoard[row][col] = null;
    }

    private boolean canMove( CheckersPiece piece, int toRow, int toCol ) {
        if ( toRow < 0 || toRow > 7 || toCol < 0 || toCol > 7 )
            return false;  // Position is off the board.
        if ( checkersBoard[toRow][toCol] != null )
            return false;
        if ( piece.getPieceColor().equals("red") ) {
            if ( !piece.isKing() ) {
                if ( piece.getRow() > toRow )
                    return false;  // Regular red piece can only move up.
                return true;
            }
        }
        else {
            if ( piece.getPieceColor().equals( "blue" ) ) {
                if ( !piece.isKing() ) {
                    if ( piece.getRow() < toRow )
                        return false;  // Regular blue piece can only move down.
                    return true;
                }
            }
        }

        return true;  // A king can move in any direction despite its color.
    }

    public List<CheckersMove> getLegalMovesForPiece( CheckersPiece piece ) {
        int row = piece.getRow();
        int col = piece.getColumn();

        List<CheckersMove> moves = new ArrayList<>();
        if ( canMove( piece, row - 1, col - 1 ) )
            moves.add( new CheckersMove( row, col , row-1, col-1 ) );
        if ( canMove( piece, row+1, col-1 ) )
            moves.add( new CheckersMove( row, col, row+1, col-1 ) );
        if ( canMove( piece, row-1, col+1 ) )
            moves.add( new CheckersMove( row, col, row-1, col+1 ) );
        if ( canMove( piece, row+1, col+1 ) )
            moves.add( new CheckersMove( row, col, row+1, col+1 ) );

        return moves;

    }
}
