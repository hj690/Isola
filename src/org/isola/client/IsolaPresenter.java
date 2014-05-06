package org.isola.client;

import java.util.List;
import java.util.Map;

import org.isola.client.Color;
import org.isola.graphics.ImageAnimation;
import org.game_api.GameApi.Container;
import org.game_api.GameApi.Operation;
import org.game_api.GameApi.Set;
import org.game_api.GameApi.SetTurn;
import org.game_api.GameApi.EndGame;
import org.game_api.GameApi.UpdateUI;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.media.client.Audio;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.DragHandlerAdapter;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;

/**
 * The presenter that controls the cheat graphics. We use the MVP pattern: the
 * model is {@link IsolaState}, the view will have the cheat graphics and it
 * will implement {@link IsolaPresenter.View}, and the presenter is
 * {@link IsolaPresenter}.
 */

public class IsolaPresenter {

	private static final String R = "R"; // red hand
	private static final String G = "G"; // green hand
	private static final String W = "W";
	private static final String B = "B";
	private final static String rId = "42";
	private final static String gId = "43";
	static String opponentPlayerId;
	
	public interface View {

		void setPresenter(IsolaPresenter isolaPresenter);

		/** Sets the state for a viewer, i.e., not one of the players. */
		void setViewerState(Map<String, Object> gameApiState); // arguements expected

		/** 
		 * Sets the state for a player (whether the player has the turn or not).
		 * The "declare cheater" button should be enabled only for
		 * CheaterMessage.IS_OPPONENT_CHEATING.
		 */
		void setPlayerState(Map<String, Object> gameApiState); // arguments expected

	//	void selectPiece(Color color, Position position); // select red|green piece
		void selectMovePosition(Color turnOfColor, Position from, List<Position> available_Move_Positions, UpdateUI updateUI);

		//void selectMovePosition(Position from);

		void chooseDestroy(List<Position> available_Destroy_Positions);
		//void chooseDestroy()

		
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

	private Position from, to, destroy;

	/** Updates the presenter and the view with the state in updateUI. */
	public void updateUI(UpdateUI updateUI) {
		List<String> playerIds = updateUI.getPlayerIds();
		String yourPlayerId = updateUI.getYourPlayerId();
		
		int yourPlayerIndex = updateUI.getPlayerIndex(yourPlayerId);
		int opponentPlayerIndex = 1 - yourPlayerIndex;
		
		// get my color
		myColor = yourPlayerIndex == 0 ? Optional.of(Color.R)
					: yourPlayerIndex == 1 ? Optional.of(Color.G) : Optional.<Color> absent();
						
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
				turnOfColor = Color.values()[playerIds.indexOf(((SetTurn) operation).getPlayerId())];
			}
		}
		if (-1 != yourPlayerIndex){
			opponentPlayerId = updateUI.getPlayerIds().get(opponentPlayerIndex);
		}	
			
		isolaState = isolaLogic.gameApiStateToIsolatState(updateUI.getState(), playerIds, turnOfColor);
		
		
		
		if (updateUI.isViewer()) {
			view.setViewerState(updateUI.getState());
			return;
		}
		if (updateUI.isAiPlayer()) {
			if (!isMyTurn())
				return;

			Heuristic gameHeuristic = new Heuristic();
			AlphaBetaPruning pruning = new AlphaBetaPruning(gameHeuristic);
			List<Operation> moves = pruning.findBestMove(updateUI.getState(), turnOfColor, 4, new Timer(){
				public void run(){
					System.out.println("timer start");
				}
			});
			String fromStr = ((Set)moves.get(0)).getKey();
			String toStr = ((Set)moves.get(1)).getKey();
			String destoryStr = ((Set)moves.get(2)).getKey();
			
			makeMyMove( strToPos(fromStr) , strToPos(toStr),  strToPos(destoryStr));

			// TODO: implement AI in a later HW!
			//container.sendMakeMove(..);
			return;
		}

		
		

		// Must be a player!
		Color myC = myColor.get();

		view.setPlayerState(updateUI.getState());

		if (isMyTurn()) {
			if (isolaState.can_move(myC)){
				from = isolaState.getPlayerPosition(myC);
				view.selectMovePosition(turnOfColor, from, get_available_Move_Positions(isolaState, from), updateUI);
			}
			else
				Window.alert("You lose!");
				
			
		}
	}

	private Position strToPos(String str) {
		int posInt = Integer.parseInt(str);
		Position p = new Position((posInt - posInt%10)/10, posInt%10);
		return p;
	}

	private void check(boolean val) {
		if (!val)
			throw new IllegalArgumentException();
	}

	public void movePositionSelected(Position position) {

		check(isMyTurn());
		to = position;
		upDateState(from, to);
		view.chooseDestroy(get_available_Destroy_Positions(isolaState));
		//view.chooseDestroy();
	}

	private void upDateState(Position from, Position to) {
		isolaState.setPieceColor(to, isolaState.getPieceColor(from));
		isolaState.setPieceColor(from, Color.W);
	
}

	public void destroyPositionSelected(Position position) {
		destroy = position;
		makeMyMove(from, to, destroy);
	}

	private void makeMyMove(Position from, Position to, Position destroy) {
		List<Operation> operations = getOperations(isolaState, from, to,
				destroy);
		container.sendMakeMove(operations);
	}

	public static List<Operation> getOperations(IsolaState state, Position from, Position to, Position destroy) {
		Color myC = state.getTurn();
		Color opponent = myC.getOppositeColor();

		/**
		 * operation order: SetTurn, set(fromPosition, W), set(toPosition,
		 * turn), set(destroyPosition, B), (optional)EndGame(playerId).
		 */
		List<Operation> operations = Lists.newArrayList();
		operations.add(new SetTurn(opponentPlayerId));
		operations.add(new Set(position_To_Str(from), W));
		operations.add(new Set(position_To_Str(to), (myC == Color.R ? R : G)));
		operations.add(new Set(position_To_Str(destroy), B));
		
		//emulate the move and test if game end
		state.setPieceColor(from, Color.W);
		state.setPieceColor(to, myC);
		state.setPieceColor(destroy, Color.B);
		
		if (!state.can_move(opponent)) {
			operations.add(new EndGame(myC == Color.R ? rId : gId));
			Window.alert("You win!");
		}
		return operations;
	}

	private static String position_To_Str(Position position) {
		return Integer.toString(position.getRow())
				+ Integer.toString(position.getColumn());
	}

	public List<Position> get_available_Destroy_Positions(IsolaState isolaState) {
		List<Position> positions = Lists.newArrayList();
		Position tmp = new Position();
		for (int i = 0; i < 7; i++)
			for (int j = 0; j < 7; j++) {
				tmp = new Position(i, j);
				if (isolaState.getPieceColor(tmp) == Color.W)
					positions.add(tmp);
			}
		return positions;
	}

	public List<Position> get_available_Move_Positions(IsolaState isolaState, Position from) {
		List<Position> positions = Lists.newArrayList();
		int row = from.getRow();
		int col = from.getColumn();

		Position tmp = new Position(row - 1, col); // up
		if (tmp.is_in_board() && isolaState.getPieceColor(tmp) == Color.W)
			positions.add(tmp);

		tmp = new Position(row - 1, col + 1); // up right
		if (tmp.is_in_board() && isolaState.getPieceColor(tmp) == Color.W)
			positions.add(tmp);

		tmp = new Position(row, col + 1); // right
		if (tmp.is_in_board() && isolaState.getPieceColor(tmp) == Color.W)
			positions.add(tmp);

		tmp = new Position(row + 1, col + 1); // right down
		if (tmp.is_in_board() && isolaState.getPieceColor(tmp) == Color.W)
			positions.add(tmp);

		tmp = new Position(row + 1, col); // down
		if (tmp.is_in_board() && isolaState.getPieceColor(tmp) == Color.W)
			positions.add(tmp);

		tmp = new Position(row + 1, col - 1); // left down
		if (tmp.is_in_board() && isolaState.getPieceColor(tmp) == Color.W)
			positions.add(tmp);

		tmp = new Position(row, col - 1); // left
		if (tmp.is_in_board() && isolaState.getPieceColor(tmp) == Color.W)
			positions.add(tmp);

		tmp = new Position(row - 1, col - 1); // up left
		if (tmp.is_in_board() && isolaState.getPieceColor(tmp) == Color.W)
			positions.add(tmp);

		return positions;

	}

	private boolean isMyTurn() {
		return myColor.isPresent() && myColor.get() == isolaState.getTurn();
	}

	private void sendInitialMove(List<String> playerIds) {
		container.sendMakeMove(isolaLogic.getMoveInitial(playerIds));
	}
	
	public void DoAnimation(Image img, int a, int b, int x, int y, Image source, Audio pieceDrop) {
		ImageAnimation ia = new ImageAnimation(img, a, b, x, y, source, pieceDrop);
		ia.run(500);
        
	}
	
 

}