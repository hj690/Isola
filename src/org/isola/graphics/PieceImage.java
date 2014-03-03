package org.isola.graphics;

import org.isola.client.Color;
import org.isola.client.Position;

/**
 * A representation of a piece image.
 */
public final class PieceImage {

	public final Color pieceColor;
	public final Position position;

	public PieceImage(Color color, Position position) {
		this.pieceColor = color;
		this.position = position;
	}

	public Color getColor(){
		return this.pieceColor;
	}
	
	public Position getPosition(){
		return this.position;
	}

	@Override
	public String toString() {
		switch (this.pieceColor) {
		case B:
			return "images/pieces/black.gif";
		case W:
			return "images/pieces/white.gif";
		case R:
			return "images/pieces/red.gif";
		case G:
			return "images/pieces/green.gif";
		default:
	        return "Forgot kind=" + pieceColor;
		}
	}
}
