package org.isola.client;

public class Move {
	private Position from;
	private Position to;
	private Color color;
	
	public Move(Position from, Position to){
		this.from = from;
		this.to = to;
	}
	
	public Position getFrom(){
		return this.from;
	}

	public Position getTo(){
		return this.to;
	}
	
	public void setFrom(Position from){
		this.from = from;
	}
	
	public void setTo(Position to){
		this.to = to;
	}
	
}
