package org.isola.client;

import static org.isola.client.Color.W;

public class Piece {
	private Color color;
	private Position position;
	
	
	public Piece(int row, int column){
		this.position = new Position(row, column);
		color = W;
	}
	
	public void setColor(Color color){
		this.color = color;
	}
	
	public Color getColor(){
		return this.color;
	}
	
	public Position getPosition(){
		return this.position;
	}
	public int getRow(){
		return this.position.getRow();
	}
	
	public int getColumn(){
		return this.position.getColumn();
	}

	

}
