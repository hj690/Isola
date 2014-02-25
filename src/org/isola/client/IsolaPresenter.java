package org.isola.client;

import java.util.List;
import java.util.Map;

import org.isola.client.Color;
import org.isola.client.GameApi.Container;
import org.isola.client.GameApi.Operation;
import org.isola.client.GameApi.Set;
import org.isola.client.GameApi.SetTurn;
import org.isola.client.GameApi.EndGame;
import org.isola.client.GameApi.UpdateUI;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * The presenter that controls the cheat graphics. We use the MVP pattern: the
 * model is {@link IsolaState}, the view will have the cheat graphics and it
 * will implement {@link IsolaPresenter.View}, and the presenter is
 * {@link IsolaPresenter}.
 */

public class IsolaPresenter {

	enum IsolaMessage {
		MOVE, DESTROY;
	}

	private static final String R = "R"; // red hand
	private static final String G = "G"; // green hand
	private static final String W = "W";
	private static final String B = "B";
	private final static int rId = 11;
	private final static int gId = 12;

	interface View {

		void setPresenter(IsolaPresenter isolaPresenter);

		/** Sets the state for a viewer, i.e., not one of the players. */
		void setViewerState(Map<String, Object> gameApiState); // arguements
																// expected

		/**
		 * Sets the state for a player (whether the player has the turn or not).
		 * The "declare cheater" button should be enabled only for
		 * CheaterMessage.IS_OPPONENT_CHEATING.
		 */
		void setPlayerState(Map<String, Object> gameApiState); // arguments
																// expected

		void selectPiece(Color turn); // select red|green piece
		// void selectMovePosition(List<Position> available_Move_Positions);

		void selectMovePosition(Position from);

		// void chooseDestroy(List<Position> available_Destroy_Positions);
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

	private Position from, to, destroy;

	/** Updates the presenter and the view with the state in updateUI. */
	public void updateUI(UpdateUI updateUI) {
		List<Integer> playerIds = updateUI.getPlayerIds();
		int yourPlayerId = updateUI.getYourPlayerId();
		int yourPlayerIndex = updateUI.getPlayerIndex(yourPlayerId);

		// rId = 11 gId = 12
		myColor = yourPlayerIndex == 0 ? Optional.of(Color.R)
				: yourPlayerIndex == 1 ? Optional.of(Color.G) : Optional
						.<Color> absent();

		if (updateUI.getState().isEmpty()) {
			// The R player sends the initial setup move.
			if (myColor.isPresent() && myColor.get().isRed()) {
				sendInitialMove(playerIds);
			}
			return;
		}

		if (updateUI.isViewer()) {
			view.setViewerState(updateUI.getState());
			return;
		}
		if (updateUI.isAiPlayer()) {
			// TODO: implement AI in a later HW!
			// container.sendMakeMove(..);
			return;
		}

		Color turnOfColor = null;
		for (Operation operation : updateUI.getLastMove()) {
			if (operation instanceof SetTurn) {
				turnOfColor = Color.values()[playerIds
						.indexOf(((SetTurn) operation).getPlayerId())]; // get
																		// turn
																		// ID?
			}
		}
		isolaState = isolaLogic.gameApiStateToIsolatState(updateUI.getState(),
				playerIds, turnOfColor);

		// Must be a player!
		Color myC = myColor.get();

		view.setPlayerState(updateUI.getState());

		if (isMyTurn()) {
			if (isolaState.can_move(myC)) // make sure my piece can move
				view.selectPiece(myC);
		}
	}

	private void check(boolean val) {
		if (!val)
			throw new IllegalArgumentException();
	}

	void pieceSelected(Position position) {
		check(isMyTurn());
		from = position;
		// view.selectMovePosition(get_available_Move_Positions(isolaState,
		// from));
		view.selectMovePosition(from);
	}

	void movePositionSelected(Position position) {
		check(isMyTurn());
		to = position;
		// view.chooseDestroy(get_available_Destroy_Positions(isolaState));
		view.chooseDestroy();
	}

	void destroyPositionSelected(Position position) {
		destroy = position;
		makeMyMove(from, to, destroy);
	}

	private void makeMyMove(Position from, Position to, Position destroy) {
		List<Operation> operations = getOperations(isolaState, from, to,
				destroy);
		container.sendMakeMove(operations);
	}

	public static List<Operation> getOperations(IsolaState state,
			Position from, Position to, Position destroy) {
		Color myC = state.getTurn();
		Color opponent = myC.getOppositeColor();

		/**
		 * operation order: SetTurn, set(fromPosition, W), set(toPosition,
		 * turn), set(destroyPosition, B), (optional)EndGame(playerId).
		 */
		List<Operation> operations = Lists.newArrayList();
		operations.add(new SetTurn(opponent == Color.R ? rId : gId));
		operations.add(new Set(position_To_Str(from), W));
		operations.add(new Set(position_To_Str(to), (myC == Color.R ? R : G)));
		operations.add(new Set(position_To_Str(destroy), B));
		if (!state.can_move(opponent)) {
			operations.add(new EndGame(myC == Color.R ? rId : gId));
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

	public static List<Position> get_available_Move_Positions(
			IsolaState isolaState, Position from) {
		List<Position> positions = Lists.newArrayList();
		Position myPosition = from;

		Position tmp = new Position(myPosition.getRow() - 1,
				myPosition.getColumn()); // up
		if (tmp.is_in_board() && isolaState.getPieceColor(tmp) == Color.W)
			positions.add(tmp);

		tmp = new Position(myPosition.getRow() - 1, myPosition.getColumn() + 1); // up
																					// right
		if (tmp.is_in_board() && isolaState.getPieceColor(tmp) == Color.W)
			positions.add(tmp);

		tmp = new Position(myPosition.getRow(), myPosition.getColumn() + 1); // right
		if (tmp.is_in_board() && isolaState.getPieceColor(tmp) == Color.W)
			positions.add(tmp);

		tmp = new Position(myPosition.getRow() + 1, myPosition.getColumn() + 1); // righ
																					// down
		if (tmp.is_in_board() && isolaState.getPieceColor(tmp) == Color.W)
			positions.add(tmp);

		tmp = new Position(myPosition.getRow() + 1, myPosition.getColumn()); // down
		if (tmp.is_in_board() && isolaState.getPieceColor(tmp) == Color.W)
			positions.add(tmp);

		tmp = new Position(myPosition.getRow() + 1, myPosition.getColumn() - 1); // left
																					// down
		if (tmp.is_in_board() && isolaState.getPieceColor(tmp) == Color.W)
			positions.add(tmp);

		tmp = new Position(myPosition.getRow(), myPosition.getColumn() - 1); // left
		if (tmp.is_in_board() && isolaState.getPieceColor(tmp) == Color.W)
			positions.add(tmp);

		tmp = new Position(myPosition.getRow() - 1, myPosition.getColumn() - 1); // up
																					// left
		if (tmp.is_in_board() && isolaState.getPieceColor(tmp) == Color.W)
			positions.add(tmp);

		return positions;

	}

	private boolean isMyTurn() {
		return myColor.isPresent() && myColor.get() == isolaState.getTurn();
	}

	private void sendInitialMove(List<Integer> playerIds) {
		container.sendMakeMove(isolaLogic.getMoveInitial(playerIds));
	}

}