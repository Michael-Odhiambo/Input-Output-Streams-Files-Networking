package ExercisesFromChapter11.Checkers;

import java.util.ArrayList;
import java.util.List;

public class CheckersBoard {

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
        if ( move.isJump() )
            makeJumpMove( piece, move );
        else
            makeOrdinaryMove( piece, move );
    }

    public void makeJumpMove( CheckersPiece piece, CheckersMove move ) {
        int rowBeingJumped = ( move.getFromRow() + move.getToRow() ) / 2;
        int columnBeingJumped = ( move.getFromCol() + move.getToCol() ) / 2;
        removePieceFrom( rowBeingJumped, columnBeingJumped );
        removePieceFrom( piece.getRow(), piece.getColumn() );
        piece.setRowAndColumn( move.getToRow(), move.getToCol() );
        checkersBoard[move.getToRow()][move.getToCol()] = piece;
    }

    private void makeOrdinaryMove( CheckersPiece piece, CheckersMove move ) {
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
                    return false;  // Regular red piece can only move down.
                return true;
            }
        }
        else {
            if ( piece.getPieceColor().equals( "blue" ) ) {
                if ( !piece.isKing() ) {
                    if ( piece.getRow() < toRow )
                        return false;  // Regular blue piece can only move up.
                    return true;
                }
            }
        }

        return true;  // A king can move in any direction despite its color.
    }

    private boolean canJump( CheckersPiece piece, int middRow, int middColumn, int toRow, int toColumn ) {
        if ( toRow < 0 || toRow > 7 || toColumn < 0 || toColumn > 7 )
            return false;  // Position is off the board.
        if ( getPieceAt( toRow, toColumn ) != null )
            return false;  // Position to move to is occupied.
        if ( getPieceAt( middRow, middColumn ) == null )
            return false;  // No piece to jump.

        if ( piece.getPieceColor().equals( "red" ) ) {
            if ( !getPieceAt( middRow, middColumn ).getPieceColor().equals( "blue" ) &&
                    !getPieceAt( middRow, middColumn ).getKingType().equals( "blueKing" ) )
                return false;  // No blue piece to jump.

            if ( !piece.isKing() ) {
                if ( piece.getRow() > toRow )
                    return false;  // Regular red piece can only move down.
            }
        }
        else {
            if ( piece.getPieceColor().equals( "blue" ) ) {
                if ( !getPieceAt( middRow, middColumn ).getPieceColor().equals( "red" ) &&
                !getPieceAt( middRow, middColumn ).getKingType().equals( "redKing" ) )
                    return false;  // No red piece to jump.

                if ( !piece.isKing() ) {
                    if ( piece.getRow() < toRow )
                        return false;  // Regular blue piece can only move up.
                }
            }
        }
        return true;
    }

    public List<CheckersMove> getLegalMovesForPiece( CheckersPiece piece ) {
        ArrayList<CheckersMove> legalMoves = getJumpMovesForPiece( piece );
        if ( legalMoves.size() < 1 )
            legalMoves = getOrdinaryMoveForPiece( piece );
        return legalMoves;
    }

    private ArrayList<CheckersMove> getOrdinaryMoveForPiece( CheckersPiece piece ) {
        int row = piece.getRow();
        int col = piece.getColumn();

        ArrayList<CheckersMove> moves = new ArrayList<>();
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

    private ArrayList<CheckersMove> getJumpMovesForPiece( CheckersPiece piece ) {
        int row = piece.getRow();
        int col = piece.getColumn();

        ArrayList<CheckersMove> jumps = new ArrayList<>();
        if ( canJump( piece, row-1, col-1, row-2, col-2 ) )
            jumps.add( new CheckersMove( row, col, row-2, col-2 ) );
        if ( canJump( piece, row+1, col-1, row+2, col-2 ) )
            jumps.add( new CheckersMove( row, col, row+2, col-2 ) );
        if ( canJump( piece, row-1, col+1, row-2, col+2 ) )
            jumps.add( new CheckersMove( row, col, row-2, col+2 ) );
        if ( canJump( piece, row+1, col+1, row+2, col+2 ) )
            jumps.add( new CheckersMove( row, col, row+2, col+2 ) );

        return jumps;
    }

    public void setKing( CheckersPiece piece ) {
        piece.setKing();


    }
}
