package org.isola.client;

import static org.isola.client.Color.R;
import static org.isola.client.Color.G;
import static org.isola.client.Color.W;
import static org.isola.client.Color.B;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableList;

public class IsolaState {
	private Piece[][] board = new Piece[7][7];
	private Color turn;
	private final List<Integer> playerIds;

	
	public IsolaState(Color turn, Map<String, Object> gameApiState, List<Integer> playerIds) {
		this.turn = turn;
		this.playerIds = playerIds;
		for (int i = 0; i < 7; i++) { // row
			for (int j = 0; j < 7; j++) { // column
					this.board[i][j] = new Piece(i, j, W);
				}
			}
		Iterator it = gameApiState.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry entry = (Map.Entry) it.next(); 
			String pos = (String)entry.getKey();
			String color = (String)entry.getValue();
			int posInt = Integer.parseInt(pos);
			Position p = new Position((posInt - posInt%10)/10, posInt%10);
			Color c = Color.W;
			switch (color){
			case "R":
				c = Color.R;
				break;
			case "G":
				c = Color.G;
				break;
			case "B":
				c = Color.B;
				break;
			case "W":
				c = Color.W;
				break;
			default:
				break;
			}
			this.board[p.getRow()][p.getColumn()] = new Piece(p.getRow(), p.getColumn(), c);
			
		}
		

	}

	public void setTurn(Color turn) {
		this.turn = turn;
	}

	public Color getTurn() {
		return turn;
	}

	public Piece[][] getBoard() {
		return board;
	}

	public Piece getPiece(int row, int column){
		return this.board[row][column];
	}
	
	public Piece getPiece(Position position){
		return getPiece(position.getRow(), position.getColumn());
	}
	
	public void setPieceColor(int row, int column, Color color) {
		this.board[row][column].setColor(color);
	}

	public void setPieceColor(Position position, Color color) {
		this.board[position.getRow()][position.getColumn()].setColor(color);
	}

	public Color getPieceColor(int row, int column) {
		return board[row][column].getColor();
	}

	public Color getPieceColor(Position position) {
		return getPieceColor(position.getRow(), position.getColumn());
	}

	public Position getPlayerPosition(Color player) {
		if (player == R || player == G) {
			for (int i = 0; i < 7; i++)
				for (int j = 0; j < 7; j++) {
					if (board[i][j].getColor() == player)
						return board[i][j].getPosition();
				}
		}
		return new Position(); // return (0,0) which is a invalid position
	}

	public boolean can_move(Position position) {
		int row = position.getRow();
		int column = position.getColumn();

		if (position.is_in_board()) {
			if (row > 0 && board[row - 1][column].getColor() == Color.W) // if
																			// can
																			// move
																			// up
				return true;
			if (row > 0 && column < 6
					&& board[row - 1][column + 1].getColor() == Color.W) // if
																			// can
																			// move
																			// rightup
				return true;
			if (column < 6 && board[row][column + 1].getColor() == Color.W) // if
																			// can
																			// move
																			// right
				return true;
			if (row < 6 && column < 6
					&& board[row + 1][column + 1].getColor() == Color.W) // if
																			// can
																			// move
																			// rightdown
				return true;
			if (row < 6 && board[row + 1][column].getColor() == Color.W) // if
																			// can
																			// move
																			// down
				return true;
			if (row < 6 && column > 0
					&& board[row + 1][column - 1].getColor() == Color.W) // if
																			// can
																			// move
																			// leftdown
				return true;
			if (column > 0 && board[row][column - 1].getColor() == Color.W) // if
																			// can
																			// move
																			// left
				return true;
			if (row > 0 && column > 0
					&& board[row - 1][column - 1].getColor() == Color.W) // if
																			// can
																			// move
																			// leftup
				return true;
		}
		return false;
	}

	public boolean can_move(Color turn) {
		return can_move(getPlayerPosition(turn));
	}

}
