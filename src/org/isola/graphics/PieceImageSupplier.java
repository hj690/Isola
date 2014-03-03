package org.isola.graphics;

import org.isola.graphics.PieceImage;
import org.isola.graphics.PieceImages;
import com.google.gwt.resources.client.ImageResource;

public class PieceImageSupplier {
	private final PieceImages pieceImages;
	
	public PieceImageSupplier(PieceImages pieceImages){
		this.pieceImages = pieceImages;
	}
	
	public ImageResource getResource(PieceImage pieceImage) {
	    switch (pieceImage.pieceColor) {
	      case B:
	        return getBlackPiece();
	      case W:
	        return getWhitePiece();
	      case R:
	        return getRedPiece();
	      case G:
	        return getGreenPiece();
	      default:
	        throw new RuntimeException("Forgot kind=" + pieceImage.getColor());
	    }
	  }

	private ImageResource getGreenPiece() {
		return pieceImages.green();
	}

	private ImageResource getRedPiece() {
		return pieceImages.red();
	}

	private ImageResource getWhitePiece() {
		return pieceImages.white();
	}

	private ImageResource getBlackPiece() {
		return pieceImages.black();
	}
	
}
