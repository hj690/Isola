//test
package org.isola.client;

import java.util.List;

import org.isola.client.GameApi.Container;
import org.isola.client.GameApi.Operation;
import org.isola.client.GameApi.SetTurn;
import org.isola.client.GameApi.UpdateUI;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * The presenter that controls the cheat graphics.
 * We use the MVP pattern:
 * the model is {@link IsolaState},
 * the view will have the cheat graphics and it will implement {@link IsolaPresenter.View},
 * and the presenter is {@link IsolaPresenter}.
 */


public class IsolaPresenter {
	
//	enum CheaterMessage {
//	    INVISIBLE, IS_OPPONENT_CHEATING, WAS_CHEATING, WAS_NOT_CHEATING;
//	  }
	
	 interface View {
		   
		    void setPresenter(IsolaPresenter isolaPresenter);

		    /** Sets the state for a viewer, i.e., not one of the players. */
		    void setViewerState(IsolaState isolastate); //arguements expected

		    /**
		     * Sets the state for a player (whether the player has the turn or not).
		     * The "declare cheater" button should be enabled only for CheaterMessage.IS_OPPONENT_CHEATING.
		     */
		    void setPlayerState();//arguments expected

		    void chooseMove();
		    void chooseDestroy();

		  }

		  private final IsolaLogic isolaLogic = new IsolaLogic();
		  private final View view;
		  private final Container container;
		  /** A viewer doesn't have a color. */
		  private Optional<Color> myColor;
		  private IsolaState isolaState;

		  public IsolaPresenter(View view, Container container) {
		    this.view = view;
		    this.container = container;
		    view.setPresenter(this);
		  }

		  /** Updates the presenter and the view with the state in updateUI. */
		  public void updateUI(UpdateUI updateUI) {
		    List<Integer> playerIds = updateUI.getPlayerIds();
		    int yourPlayerId = updateUI.getYourPlayerId();
		    int yourPlayerIndex = updateUI.getPlayerIndex(yourPlayerId);
		    
		    //rId = 11   gId = 12
		    myColor = yourPlayerIndex == 11 ? Optional.of(Color.R)
		        : yourPlayerIndex == 12 ? Optional.of(Color.G) : Optional.<Color>absent();
		    
		    if (updateUI.getState().isEmpty()) {
		      // The R player sends the initial setup move.
		      if (myColor.isPresent() && myColor.get().isRed()) {
//		        sendInitialMove(playerIds);
		      }
		      return;
		    }
		    Color turnOfColor = null;
		    for (Operation operation : updateUI.getLastMove()) {
		      if (operation instanceof SetTurn) {
		        turnOfColor = Color.values()[playerIds.indexOf(((SetTurn) operation).getPlayerId())]; //get turn ID?
		      }
		    }
		    isolaState = isolaLogic.gameApiStateToIsolatState(updateUI.getState(), playerIds);

		    if (updateUI.isViewer()) {
		      view.setViewerState(isolaState);
		      return;
		    }
		    if (updateUI.isAiPlayer()) {
		      // TODO: implement AI in a later HW!
		      //container.sendMakeMove(..);
		      return;
		    }
		  
		  }

}