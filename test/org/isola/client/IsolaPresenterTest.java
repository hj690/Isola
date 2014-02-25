package org.isola.client;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Map;

import org.isola.client.GameApi;
import org.isola.client.IsolaPresenter;
import org.isola.client.IsolaLogic;
import org.isola.client.IsolaPresenter.View;
import org.isola.client.GameApi.Container;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.isola.client.GameApi.Operation;
import org.isola.client.GameApi.SetTurn;
import org.isola.client.GameApi.UpdateUI;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class IsolaPresenterTest {

	/** The class under test. */
	private IsolaPresenter isolaPresenter;
	private final IsolaLogic isolaLogic = new IsolaLogic();
	private View mockView;
	private Container mockContainer;

	private static final String PLAYER_ID = "playerId";
	/*
	 * The entries used in the cheat game are: isCheater:yes, W, B, M, claim,
	 * C0...C51
	 */
	private final int viewerId = GameApi.VIEWER_ID;
	private final int rId = 11;
	private final int gId = 12;
	private final ImmutableList<Integer> playerIds = ImmutableList.of(rId, gId);
	private final ImmutableMap<String, Object> rInfo = ImmutableMap
			.<String, Object> of(PLAYER_ID, rId);
	private final ImmutableMap<String, Object> gInfo = ImmutableMap
			.<String, Object> of(PLAYER_ID, gId);
	private final ImmutableList<Map<String, Object>> playersInfo = ImmutableList
			.<Map<String, Object>> of(rInfo, gInfo);

	/**
	 * The interesting states that I'll test. all gameAPI state, not IsolaState
	 */

	private final ImmutableMap<String, Object> emptyState = ImmutableMap
			.<String, Object> of();
	Map<String, Object> initialState = ImmutableMap.<String, Object> builder()
			.put("line0", "---R---").put("line1", "-------")
			.put("line2", "-------").put("line3", "-------")
			.put("line4", "-------").put("line5", "-------")
			.put("line6", "---G---").build();
	Map<String, Object> normalState_redTurn = ImmutableMap
			.<String, Object> builder().put("line0", "--XXX--")
			.put("line1", "--XR---").put("line2", "--XX---")
			.put("line3", "--X----").put("line4", "----GX-")
			.put("line5", "---XX--").put("line6", "-------").build();
	Map<String, Object> normalState_greenTurn = ImmutableMap
			.<String, Object> builder().put("line0", "-------")
			.put("line1", "----R--").put("line2", "---XX--")
			.put("line3", "--X-X--").put("line4", "----GX-")
			.put("line5", "---XX--").put("line6", "-------").build();
	Map<String, Object> endState_redWin = ImmutableMap
			.<String, Object> builder().put("line0", "-------")
			.put("line1", "----R--").put("line2", "---XX--")
			.put("line3", "--XXXX-").put("line4", "---XGX-")
			.put("line5", "---XXX-").put("line6", "-------").build();
	Map<String, Object> endState_greenWin = ImmutableMap
			.<String, Object> builder().put("line0", "RX-----")
			.put("line1", "XX-----").put("line2", "-------")
			.put("line3", "---X---").put("line4", "---X---")
			.put("line5", "-----G-").put("line6", "-------").build();

	@Before
	public void runBefore() {
		mockView = Mockito.mock(View.class);
		mockContainer = Mockito.mock(Container.class);
		isolaPresenter = new IsolaPresenter(mockView, mockContainer);
		verify(mockView).setPresenter(isolaPresenter);
	}

	@After
	public void runAfter() {
		// This will ensure I didn't forget to declare any extra interaction the
		// mocks have.
		verifyNoMoreInteractions(mockContainer);
		verifyNoMoreInteractions(mockView);
	}

	@Test
	public void testEmptyStateForR() {
		isolaPresenter.updateUI(createUpdateUI(rId, 0, emptyState));
		verify(mockContainer)
				.sendMakeMove(isolaLogic.getMoveInitial(playerIds));
	}

	@Test
	public void testEmptyStateForG() {
		isolaPresenter.updateUI(createUpdateUI(gId, 0, emptyState));
	}

	@Test
	public void testEmptyStateForViewer() {
		isolaPresenter.updateUI(createUpdateUI(viewerId, 0, emptyState));
	}

	@Test
	public void testNormalStateForR() {
		UpdateUI updateUI = createUpdateUI(rId, rId, normalState_redTurn);
		IsolaState isolaState = IsolaLogic.gameApiStateToIsolatState(
				updateUI.getState(), playerIds, Color.R);

		isolaPresenter.updateUI(updateUI);

		verify(mockView).setPlayerState(normalState_redTurn);

		verify(mockView).selectPiece(Color.R);

		Position from = new Position(1, 3);
		isolaPresenter.pieceSelected(from);

		verify(mockView).selectMovePosition(from);

		Position to = new Position(1, 4);
		isolaPresenter.movePositionSelected(to);

		verify(mockView).chooseDestroy();

		Position destroy = new Position(0, 0);
		isolaPresenter.destroyPositionSelected(destroy);

		verify(mockContainer).sendMakeMove(
				IsolaPresenter.getOperations(isolaState, from, to, destroy));

	}

	@Test
	public void testNormalStateForG() {
		UpdateUI updateUI = createUpdateUI(gId, gId, normalState_greenTurn);
		IsolaState isolaState = IsolaLogic.gameApiStateToIsolatState(
				updateUI.getState(), playerIds, Color.G);

		isolaPresenter.updateUI(updateUI);

		verify(mockView).setPlayerState(normalState_greenTurn);

		verify(mockView).selectPiece(Color.G);

		Position from = new Position(4, 4);
		isolaPresenter.pieceSelected(from);

		verify(mockView).selectMovePosition(from);

		Position to = new Position(4, 3);
		isolaPresenter.movePositionSelected(to);

		verify(mockView).chooseDestroy();

		Position destroy = new Position(0, 4);
		isolaPresenter.destroyPositionSelected(destroy);

		verify(mockContainer).sendMakeMove(
				IsolaPresenter.getOperations(isolaState, from, to, destroy));
	}

	@Test
	public void testNormalStateForViewerTurnOfR() {
		UpdateUI updateUI = createUpdateUI(viewerId, rId, normalState_redTurn);
		isolaPresenter.updateUI(updateUI);
		verify(mockView).setViewerState(normalState_redTurn);
	}

	@Test
	public void testNormalStateForViewerTurnOfG() {
		UpdateUI updateUI = createUpdateUI(viewerId, gId, normalState_greenTurn);
		isolaPresenter.updateUI(updateUI);
		verify(mockView).setViewerState(normalState_greenTurn);
	}

	@Test
	public void testEndState_RedWin_ForG() {
		UpdateUI updateUI = createUpdateUI(gId, gId, endState_redWin);
		isolaPresenter.updateUI(updateUI);
		verify(mockView).setPlayerState(endState_redWin);
	}

	@Test
	public void testEndState_GreenWin_ForR() {
		UpdateUI updateUI = createUpdateUI(rId, rId, endState_greenWin);
		isolaPresenter.updateUI(updateUI);
		verify(mockView).setPlayerState(endState_greenWin);
	}

	@Test
	public void testEndState_GreenWin_ForViewer() {
		UpdateUI updateUI = createUpdateUI(viewerId, rId, endState_greenWin);
		isolaPresenter.updateUI(updateUI);
		verify(mockView).setViewerState(endState_greenWin);
	}

	private UpdateUI createUpdateUI(int yourPlayerId, int turnOfPlayerId,
			Map<String, Object> state) {
		// Our UI only looks at the current state
		// (we ignore: lastState, lastMovePlayerId,
		// playerIdToNumberOfTokensInPot)
		return new UpdateUI(yourPlayerId, playersInfo, state,
				emptyState, // we ignore lastState
				ImmutableList.<Operation> of(new SetTurn(turnOfPlayerId)), 0,
				ImmutableMap.<Integer, Integer> of());
	}

}
