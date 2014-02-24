package org.isola.client;

import java.util.List;

import org.isola.client.Color;
import org.isola.client.GameApi.Container;
import org.isola.client.GameApi.Operation;
import org.isola.client.GameApi.Set;
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

	enum IsolaMessage {MOVE, DESTROY;}
	
	interface View {
		   
		    void setPresenter(IsolaPresenter isolaPresenter);

		    /** Sets the state for a viewer, i.e., not one of the players. */
		    void setViewerState(IsolaState isolastate); //arguements expected

		    /**
		     * Sets the state for a player (whether the player has the turn or not).
		     * The "declare cheater" button should be enabled only for CheaterMessage.IS_OPPONENT_CHEATING.
		     */
		    void setPlayerState(IsolaState isolastate);//arguments expected

		    void chooseMove(List<Position> available_Move_Positions);
		    void chooseDestroy(List<Position> available_Destroy_Positions);

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
		myColor = yourPlayerIndex == 0 ? Optional.of(Color.R)
		        : yourPlayerIndex == 1 ? Optional.of(Color.G) : Optional.<Color>absent();
		    
		if (updateUI.getState().isEmpty()) {
			// The R player sends the initial setup move.
			if (myColor.isPresent() && myColor.get().isRed()) {
		        sendInitialMove(playerIds);
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
			// container.sendMakeMove(..);
			return;
		}
		// Must be a player!
		Color myC = myColor.get();
		Color opponent = myC.getOppositeColor();
		
		view.setPlayerState(isolaState);
		
		if (isMyTurn()) {
			if (isolaState.can_move(myC)) {
				view.chooseMove(get_available_Move_Positions(isolaState, myC));
				view.chooseDestroy(get_available_Destroy_Positions(isolaState));
			} 
			else {// endGame
				endGame();
			}
		}
	}
	

	private List<Position> get_available_Destroy_Positions(
			IsolaState isolaState) {
		List<Position> positions = Lists.newArrayList();
		Position tmp = new Position();
		for(int i = 0; i < 7; i++)
			for(int j = 0; j < 7; j++){
				tmp = new Position(i, j);
				if(isolaState.getPieceColor(tmp) == Color.W)
					positions.add(tmp);
			}
		return positions;
	}

	private List<Position> get_available_Move_Positions(IsolaState isolaState, Color myC) {
		List<Position> positions = Lists.newArrayList();
		Position myPosition = isolaState.getPlayerPosition(myC);
		
		Position tmp = new Position(myPosition.getRow() - 1, myPosition.getColumn()); //up
		if(tmp.is_in_board() && isolaState.getPieceColor(tmp) == Color.W)
			positions.add(tmp);
		
		tmp = new Position(myPosition.getRow() - 1, myPosition.getColumn() + 1); //up right
		if(tmp.is_in_board() && isolaState.getPieceColor(tmp) == Color.W)
			positions.add(tmp);
		
		tmp = new Position(myPosition.getRow(), myPosition.getColumn() + 1); //right
		if(tmp.is_in_board() && isolaState.getPieceColor(tmp) == Color.W)
			positions.add(tmp);
		
		tmp = new Position(myPosition.getRow() + 1, myPosition.getColumn() + 1); //righ down
		if(tmp.is_in_board() && isolaState.getPieceColor(tmp) == Color.W)
			positions.add(tmp);
		
		tmp = new Position(myPosition.getRow() + 1, myPosition.getColumn()); // down
		if(tmp.is_in_board() && isolaState.getPieceColor(tmp) == Color.W)
			positions.add(tmp);
		
		tmp = new Position(myPosition.getRow() + 1, myPosition.getColumn() - 1); // left down
		if(tmp.is_in_board() && isolaState.getPieceColor(tmp) == Color.W)
			positions.add(tmp);
		
		tmp = new Position(myPosition.getRow(), myPosition.getColumn() - 1); // left
		if(tmp.is_in_board() && isolaState.getPieceColor(tmp) == Color.W)
			positions.add(tmp);
		
		tmp = new Position(myPosition.getRow() - 1, myPosition.getColumn() - 1); // up left
		if(tmp.is_in_board() && isolaState.getPieceColor(tmp) == Color.W)
			positions.add(tmp);
		
		return positions;
		
	}

	private boolean isMyTurn() {
		return myColor.isPresent() && myColor.get() == isolaState.getTurn();
	}
	
	private void sendInitialMove(List<Integer> playerIds) {
		container.sendMakeMove(isolaLogic.getMoveInitial(playerIds));
	}
	
	
	private void endGame() {
		// TODO Auto-generated method stub
		
	}
	
	

}