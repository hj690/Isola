package org.isola.client;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.isola.client.GameApi.Operation;
import org.isola.client.GameApi.Set;
import org.isola.client.GameApi.Delete;
import org.isola.client.GameApi.SetTurn;
import org.isola.client.GameApi.EndGame;
import org.isola.client.GameApi.VerifyMove;
import org.isola.client.GameApi.VerifyMoveDone;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

@RunWith(JUnit4.class)
public class IsolaLogicTest {

	IsolaLogic isolalogic = new IsolaLogic();

	// to assert if move is ok
	private void assertMoveOk(VerifyMove verifyMove) {
		VerifyMoveDone verifyDone = IsolaLogic.verify(verifyMove);
		assertEquals(0, verifyDone.getHackerPlayerId());
	}

	// to assert if lastplayer is hacker
	private void assertHacker(VerifyMove verifyMove) {
		VerifyMoveDone verifyDone = IsolaLogic.verify(verifyMove);
		assertEquals(verifyMove.getLastMovePlayerId(),
				verifyDone.getHackerPlayerId());
	}

	private static final String PLAYER_ID = "playerId";
	private static final String TURN = "turn"; // turn of which player (either R
												// or G)
	private static final String R = "R"; // red hand
	private static final String G = "G"; // green hand
	private static final String W = "W";
	private static final String B = "B";
	private static final String MOVE = "move";
	private static final String DESTROY = "destroy";
	private final int rId = 11;
	private final int gId = 12;
	private final Map<String, Object> rInfo = ImmutableMap.<String, Object> of(
			PLAYER_ID, rId);
	private final Map<String, Object> gInfo = ImmutableMap.<String, Object> of(
			PLAYER_ID, gId);
	private final List<Map<String, Object>> playersInfo = ImmutableList.of(
			rInfo, gInfo);
	private final ImmutableMap<String, Object> emptyState = ImmutableMap
			.<String, Object> of();

	private VerifyMove move(int lastMovePlayerId,
			Map<String, Object> lastState, List<Operation> lastMove) {
		return new VerifyMove(playersInfo,
		// we never need to check the resulting state
		// (the server makes it, and the game doesn't have any hidden decisions
		// such in Battleships)
				emptyState, lastState, lastMove, lastMovePlayerId,
				// playerIdToNumberOfTokensInPot
				ImmutableMap.<Integer, Integer> of());
	}

	// ================================legal tests==============================
	/**
	 * operation order: SetTurn, set(fromPosition, W), set(toPosition, turn),
	 * set(destroyPosition, B), (optional)EndGame(playerId).
	 */

	@Test
	public void test_red_move_rightDown() {
		// last state
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put("03", R)
				.put("63", G)
				.build();

		List<Operation> operations = ImmutableList.<Operation> of(
				new SetTurn(gId), 
				new Set("03", W), 
				new Set("14", R), 
				new Set("11", B));

		assertMoveOk(move(rId, state, operations));
	}

	@Test
	public void test_green_move_upleft() {
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put("03", R)
				.put("63", G)
				.build();

		List<Operation> operations = ImmutableList.<Operation> of(
				new SetTurn(rId), 
				new Set("63", W), 
				new Set("52", G), 
				new Set("11", B));

		assertMoveOk(move(gId, state, operations));
	}

	@Test
	public void test_green_move_right() {
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put("06", R)
				.put("13", B)
				.put("15", B)
				.put("43", B)
				.put("53", B)
				.put("65", G)
				.build();

		List<Operation> operations = ImmutableList.<Operation> of(
				new SetTurn(rId), 
				new Set("65", W), 
				new Set("66", G), 
				new Set("11", B));

		assertMoveOk(move(gId, state, operations));
	}

	@Test
	public void test_red_move_left() {
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put("06", R)
				.put("13", B)
				.put("15", B)
				.put("43", B)
				.put("53", B)
				.put("66", G)
				.build();

		List<Operation> operations = ImmutableList.<Operation> of(
				new SetTurn(gId),
				new Set("06", W), 
				new Set("05", R), 
				new Set("11", B));

		assertMoveOk(move(rId, state, operations));
	}

	@Test
	public void test_green_destroy() {
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put("06", R)
				.put("13", B)
				.put("15", B)
				.put("43", B)
				.put("53", B)
				.put("60", G)
				.build();

		List<Operation> operations = ImmutableList.<Operation> of(
				new SetTurn(rId),
				new Set("60", W), 
				new Set("61", G), 
				new Set("00", B));

		assertMoveOk(move(gId, state, operations));
	}

	// ========================= Illegal tests ==========================

	@Test
	public void test_green_move_left_destroyed() {
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put("06", R)
				.put("65", B)
				.put("66", G)
				.build();

		List<Operation> operations = ImmutableList.<Operation> of(
				new SetTurn(rId), 
				new Set("66", W), 
				new Set("65", G), 
				new Set("33", B));

		assertHacker(move(gId, state, operations));
	}

	@Test
	public void test_green_move_outboard() {
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put("06", R)
				.put("65", B)
				.put("66", G)
				.build();

		List<Operation> operations = ImmutableList.<Operation> of(
				new SetTurn(gId), 
				new Set("66", W), 
				new Set("67", G), 
				new Set("33", B));

		assertHacker(move(gId, state, operations));
	}

	@Test
	public void test_red_move_outboard() {
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put("60", R)
				.put("65", B)
				.put("66", G)
				.build();

		List<Operation> operations = ImmutableList.<Operation> of(
				new SetTurn(gId), 
				new Set("60", W), 
				new Set("70", R), 
				new Set("11", B));

		assertHacker(move(rId, state, operations));
	}

	@Test
	public void test_red_move_toofar() {
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put("00", R)
				.put("65", B)
				.put("66", G)
				.build();

		List<Operation> operations = ImmutableList.<Operation> of(
				new SetTurn(gId), 
				new Set("00", W), 
				new Set("02", R), 
				new Set("63", B));

		assertHacker(move(rId, state, operations));
	}

	@Test
	public void test_green_move_occupied() {
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put("33", R)
				.put("65", B)
				.put("43", G)
				.build();

		List<Operation> operations = ImmutableList.<Operation> of(
				new SetTurn(rId),
				new Set("43", W), 
				new Set("33", G), 
				new Set("23", B));

		assertHacker(move(gId, state, operations));
	}

	@Test
	public void test_green_move_down_destroyed() {
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put("33", R)
				.put("53", B)
				.put("43", G)
				.build();

		List<Operation> operations = ImmutableList.<Operation> of(
				new SetTurn(rId),
				new Set("43", W),
				new Set("53", G), 
				new Set("00", B));

		assertHacker(move(gId, state, operations));
	}

	@Test
	public void test_red_destroy_outboard() {
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put("00", R)
				.put("53", B)
				.put("43", G)
				.build();

		List<Operation> operations = ImmutableList.<Operation> of(
				new SetTurn(gId), 
				new Set("00", W), 
				new Set("01", R), 
				new Set("67", B));

		assertHacker(move(rId, state, operations));
	}

	@Test
	public void test_green_destroy_destroyed() {
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put("66", B)
				.put("53", R)
				.put("43", G)
				.build();

		List<Operation> operations = ImmutableList.<Operation> of(
				new SetTurn(rId), 
				new Set("43", W), 
				new Set("44", G), 
				new Set("66", B));

		assertHacker(move(gId, state, operations));
	}

	@Test
	public void test_green_destroy_occupied() {
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put("66", B)
				.put("33", R)
				.put("43", G)
				.build();

		List<Operation> operations = ImmutableList.<Operation> of(
				new SetTurn(rId), 
				new Set("43", W), 
				new Set("44", G), 
				new Set("33", B));

		assertHacker(move(gId, state, operations));
	}

	@Test
	public void test_green_destroy_self() {
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put("66", B)
				.put("33", R)
				.put("43", G)
				.build();

		List<Operation> operations = ImmutableList.<Operation> of(
				new SetTurn(gId), 
				new Set("43", W), 
				new Set("44", G), 
				new Set("44", B));

		assertHacker(move(gId, state, operations));
	}

	// ============================ End game ==============================
	@Test
	public void test_endgame_green_win() { // this is legal
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put("20", B)
				.put("31", B)
				.put("40", B)
				.put("41", B)
				.put("30", R)
				.put("43", G)
				.build();

		List<Operation> operations = ImmutableList.<Operation> of(
				new SetTurn(rId), 
				new Set("43", W), 
				new Set("44", G), 
				new Set("21", B),
				new EndGame(gId));

		assertMoveOk(move(gId, state, operations));
	}

	@Test
	public void test_endgame_red_win() { // this is illegal
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put("20", B)
				.put("31", B)
				.put("40", B)
				.put("41", B)
				.put("30", G)
				.put("43", R)
				.build();

		List<Operation> operations = ImmutableList.<Operation> of(
				new SetTurn(gId), 
				new Set("24", W), 
				new Set("23", R), 
				new Set("22", B),
				new EndGame(rId));

		assertHacker(move(rId, state, operations));
	}
}
