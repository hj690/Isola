package org.isola.graphics;

import java.util.List;

import org.isola.client.Color;
import org.isola.client.IsolaPresenter;
import org.isola.client.Position;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;

public class IsolaDropController extends SimpleDropController {
	  private final Image image;
	  private final IsolaPresenter presenter;
	  private final IsolaGraphics view;
	  private final Position startPos;
	  private final Color turn;
	  List<Position> available_Move_Positions;
	  
	  public IsolaDropController(Image image, IsolaGraphics view, IsolaPresenter presenter, Position from, Color turn, List<Position> available_Move_Positions) {
	    super(image);
	    this.image = image;
	    this.presenter = presenter;
	    this.view = view;
	    this.startPos = from;
	    this.turn = turn;
	    this.available_Move_Positions = available_Move_Positions;
	    
	  }
	  
	  @Override
	  public void onDrop(DragContext context) {
	    Position dropper = getPosition((Image) context.draggable);
	    Position target = getPosition((Image) image);
	   
	    view.makeMoveUpdate(dropper, target, turn, false); // the last "false" means don't do animation
	    presenter.movePositionSelected(target);
	
	   
	  }
	  

	  @Override
	  public void onPreviewDrop(DragContext context) throws VetoDragException {
	    if (image == null) {
	      throw new VetoDragException();
	    }
	    super.onPreviewDrop(context);
	  }
	  
	  public Position getPosition(Image image) {
	    int top = image.getAbsoluteTop();
	    int left = image.getAbsoluteLeft();
	    int row = (image.getAbsoluteTop() / 60);
	    int col = (image.getAbsoluteLeft() / 60);
	 
	    return new Position(row, col);
	  }
	}