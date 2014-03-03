package org.isola.graphics;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface PieceImages extends ClientBundle{
	
	  @Source("images/pieces/green.gif")
	  ImageResource green();

	  @Source("images/pieces/red.gif")
	  ImageResource red();

	  @Source("images/pieces/black.jpg")
	  ImageResource black();
	  
	  @Source("images/pieces/white.jpg")
	  ImageResource white();
	  
}
