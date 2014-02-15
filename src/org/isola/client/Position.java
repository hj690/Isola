package org.isola.client;

public class Position {
	private int row;
	private int column;
	
	public Position(){ // wrong position
		this.row = 0;
		this.column = 0;
	}
	public Position(int row, int column){
		this.row = row;
		this.column = column;
	}
	
	public int getRow(){
		return this.row;
	}
	public int getColumn(){
		return this.column;
	}
	
	public void setRow(int row){
		this.row = row;
	}
	public void setColumn(int column){
		this.column = column;
	}
	
	public boolean is_in_board(){
		return (row >= 0 && row < 7 && column >= 0 && column < 7);
	}
}

