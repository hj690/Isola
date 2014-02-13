package org.isola.client;

public enum Color {
	W, B, // available and unavailable piece
	R, G; // red player and green player
	public boolean isWhite(){
		return this == W;
	}
	public boolean isBlack(){
		return this == B;
	}
	public boolean isRed(){
		return this == R;
	}
	public boolean isGreen(){
		return this == G;
	}
	public Color getOppositeColor() {
	    return this == R ? G : R;
	  }
}
