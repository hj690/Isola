package org.isola.client;

import static org.isola.client.Color.R;
import static org.isola.client.Color.G;
import static org.isola.client.Color.W;
import static org.isola.client.Color.B;

import java.util.ArrayList;
import java.util.List;

public class IsolaState {
	private Piece[][] board = new Piece[7][7];
	private Color turn;
	
	
	public IsolaState(){}
	
	public IsolaState( String turnStr, ArrayList<String> boardStr){
		this.turn = (turnStr == "R") ? R : G;
		for(int i = 0; i < 7; i++){ // row
			String line = boardStr.get(i);
			for(int j = 0; j < 7; j++){ // column
				switch(line.charAt(j)){
				case 'R':
					this.board[i][j] = new Piece(i, j, R);
					break;
				case 'G':
					this.board[i][j] = new Piece(i, j, G);
					break;
				case 'X':
					this.board[i][j] = new Piece(i, j, B);
					break;
				case '-':
					this.board[i][j] = new Piece(i, j, W);
					break;
					
				}
				
			}
		}
		
	}
	
	public void setTurn(Color turn){
		this.turn = turn;
	}
	
	public Color getTurn(){
		return turn;
	}
	
	public Piece[][] getBoard(){
		return board;
	}
	
	public void setPieceColor(int row, int column, Color color){
		this.board[row][column].setColor(color);
	}
	
	public void setPieceColor(Position position, Color color){
		this.board[position.getRow()][position.getColumn()].setColor(color);
	}
	
	public Color getPieceColor(int row, int column){
		return board[row][column].getColor();
	}
	
	public Color getPieceColor(Position position){
		return getPieceColor(position.getRow(), position.getColumn());
	}
	
	
	
	public Position getPlayerPosition(Color player){
		if(player == R || player == G){
			for(int i = 0; i < 7; i++)
				for(int j = 0; j < 7; j++){
					if(board[i][j].getColor() == player)
						return board[i][j].getPosition();
				}
		}
		return new Position(); //return (0,0) which is a invalid position
	} 
	
	
	public boolean can_move(Position position){
		int row = position.getRow();
		int column = position.getColumn();
		
		if(position.is_in_board()){
			if(row > 0 && board[row-1][column].getColor() == Color.W) //if can move up
				return true;
			if(row > 0 && column < 6 && board[row-1][column+1].getColor() == Color.W) //if can move rightup
				return true;
			if(column < 6 && board[row][column+1].getColor() == Color.W) //if can move right
				return true;
			if(row < 6 && column < 6 && board[row+1][column+1].getColor() == Color.W) //if can move rightdown
				return true;
			if(row < 6 && board[row+1][column].getColor() == Color.W) //if can move down
				return true;
			if(row < 6 && column > 0 && board[row+1][column-1].getColor() == Color.W) //if can move leftdown
				return true;
			if(column > 0 && board[row][column-1].getColor() == Color.W) //if can move left
				return true;
			if(row > 0 && column > 0 && board[row-1][column-1].getColor() == Color.W) //if can move leftup
				return true;
		}
		return false;
	}

}
