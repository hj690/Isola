package org.isola.client;

import static org.isola.client.Color.R;
import static org.isola.client.Color.G;

public class IsolaState {
	private Piece[][] board = new Piece[8][8]; // only position 11 - 77 are used
	private Color turn;
	public Color getTurn(){
		return turn;
	}
	public void setTurn(Color turn){
		this.turn = turn;
	}
	
	public Piece[][] getBoard(){
		return board;
	}
	
	public Position getPlayerPosition(Color player){
		if(player == R || player == G){
			for(int i = 1; i < 8; i++)
				for(int j = 1; j < 8; j++){
					if(board[i][j].getColor() == player)
						return board[i][j].getPosition();
				}
		}
		return new Position(); //return (0,0) which is a invalid position
	} 

}
