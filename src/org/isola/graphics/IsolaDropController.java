package org.isola.graphics;

import java.util.List;

import org.game_api.GameApi.UpdateUI;
import org.isola.client.Color;
import org.isola.client.IsolaPresenter;
import org.isola.client.Position;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;
import com.google.gwt.media.client.Audio;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;

public class IsolaDropController extends SimpleDropController {
	  private final Image image;
	  private final IsolaPresenter presenter;
	  private final IsolaGraphics view;
	  private final Position startPos;
	  private final Color turn;
	  private final UpdateUI updateUI;
	  List<Position> available_Move_Positions;
	  private final Audio pieceDrop;
	  
	  public IsolaDropController(Image image, IsolaGraphics view, IsolaPresenter presenter, Position from, Color turn, List<Position> available_Move_Positions, UpdateUI updateUI, Audio pieceDrop) {
	    super(image);
	    this.image = image;
	    this.presenter = presenter;
	    this.view = view;
	    this.startPos = from;
	    this.turn = turn;
	    this.available_Move_Positions = available_Move_Positions;
	    this.updateUI = updateUI;
	    this.pieceDrop = pieceDrop;
	  }
	  
	  @Override
	  public void onDrop(DragContext context) {
	    Position target = getPosition((Image) image);
	   
	    if(includePos(available_Move_Positions, target)){
	    	if(pieceDrop != null)
	    		//pieceDrop.play();
	    	view.makeMoveUpdate(startPos, target, turn, false); // the last "false" means don't do animation
	    	presenter.movePositionSelected(target);
	    }
	    else{
	    	presenter.updateUI(updateUI);
//		    view.setIllegalTarget(target);
//		    view.moveDropperBack(startPos, turn, available_Move_Positions);	    	
	    }
	
	   
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
	  
	  private boolean includePos(List<Position> available_Move_Positions, Position target){
		  for(Position p: available_Move_Positions){
			  if(p.getColumn() == target.getColumn() && p.getRow() == target.getRow())
				  return true;
		  }
		  return false;
	  }
	}