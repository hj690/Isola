package org.isola.graphics;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;

import org.isola.client.*;

public class IsolaDragController extends PickupDragController {
	  
	  private final IsolaPresenter presenter;
	  private Image image;
	  
	  public IsolaDragController(AbsolutePanel boundaryPanel, boolean allowDroppingOnBoundaryPanel, IsolaPresenter presenter) {
	    super(boundaryPanel, allowDroppingOnBoundaryPanel);
	    this.presenter = presenter;
	  }
	  
	  @Override
	  public void dragStart() {
	    super.dragStart();
	    saveSelectedWidgetsLocationAndStyle();
	       
	  }
	  

	}