package org.isola.client;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.isola.client.GameApi.Operation;
import org.isola.client.GameApi.Set;
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
	
	//to assert if move is ok
	private void assertMoveOk(VerifyMove verifyMove) {
		VerifyMoveDone verifyDone = IsolaLogic.verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
		}

	//to assert if lastplayer is hacker
	private void assertHacker(VerifyMove verifyMove) {
	    VerifyMoveDone verifyDone = IsolaLogic.verify(verifyMove);
	    assertEquals(verifyMove.getLastMovePlayerId(), verifyDone.getHackerPlayerId());
	  }
	
	
	private static final String PLAYER_ID = "playerId";
	private static final String TURN = "turn"; // turn of which player (either R
												// or G)
	private static final String R = "R"; // red hand
	private static final String G = "G"; // green hand
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

	

	private VerifyMove move(int lastMovePlayerId, Map<String, Object> lastState, List<Operation> lastMove) {
		return new VerifyMove(playersInfo,
		// we never need to check the resulting state
		// (the server makes it, and the game doesn't have any hidden decisions such in Battleships)
		emptyState, 
		lastState, lastMove, lastMovePlayerId,
		//playerIdToNumberOfTokensInPot
		ImmutableMap.<Integer, Integer>of());
	}

	
	@Test
	public void test_green_destroy_self() {
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put(TURN, G)
				.put("line0", "-------")
				.put("line1", "----X-X")
				.put("line2", "-------")
				.put("line3", "---R---")
				.put("line4", "---G---")
				.put("line5", "---X---")
				.put("line6", "-----XX")
				.build();
		 
		List<Operation> operations = ImmutableList.<Operation> of(
				new SetTurn(rId), 
				new Set(MOVE, "43, 44"), 
				new Set(DESTROY, "44"));

		
		assertHacker(move(gId, state, operations));
	}
	
	
	
	//=======================legal tests========================
	
	@Test
	public void test_red_move_rightDown() { 
		//last state
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put(TURN, R)
				.put("line0", "---R---")
				.put("line1", "-------")
				.put("line2", "-------")
				.put("line3", "-------")
				.put("line4", "-------")
				.put("line5", "-------")
				.put("line6", "---G---")
				.build();

		//lastmove
		List<Operation> operations = ImmutableList.<Operation> of( 
				new SetTurn(gId), 
				new Set(MOVE, "03, 14"), 
				new Set(DESTROY, "66"));

		assertMoveOk(move(rId, state, operations));
	}
	
	@Test
	public void test_green_move_upleft() { 
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put(TURN, G)
				.put("line0", "---R---")
				.put("line1", "-------")
				.put("line2", "-------")
				.put("line3", "-------")
				.put("line4", "-------")
				.put("line5", "-------")
				.put("line6", "---G---")
				.build();
	
		List<Operation> operations = ImmutableList.<Operation> of(
				new SetTurn(rId),  
				new Set(MOVE, "63, 52"), 
				new Set(DESTROY, "12"));

		assertMoveOk(move(gId, state, operations));
	}
	
	@Test
	public void test_green_move_right() {  
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put(TURN, G)
				.put("line0", "------R")
				.put("line1", "---X-X-")
				.put("line2", "-------")
				.put("line3", "-------")
				.put("line4", "---X---")
				.put("line5", "---XX--")
				.put("line6", "-----G-")
				.build();
	
		List<Operation> operations = ImmutableList.<Operation> of(
				new SetTurn(rId), 
				new Set(MOVE, "65, 66"), 
				new Set(DESTROY, "00"));

		assertMoveOk(move(gId, state, operations));
	}
	
	@Test
	public void test_red_move_left() {  
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put(TURN, R)
				.put("line0", "X-----R")
				.put("line1", "---X-X-")
				.put("line2", "-------")
				.put("line3", "-------")
				.put("line4", "---X---")
				.put("line5", "---XX--")
				.put("line6", "------G")
				.build();
	
		List<Operation> operations = ImmutableList.<Operation> of(
				new SetTurn(gId), 
				new Set(MOVE, "06, 05"), 
				new Set(DESTROY, "56"));

		assertMoveOk(move(rId, state, operations));
	}
	
	@Test
	public void test_green_move_upright() { 
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put(TURN, G)
				.put("line0", "------R")
				.put("line1", "---X-X-")
				.put("line2", "-------")
				.put("line3", "-------")
				.put("line4", "---X---")
				.put("line5", "---XX--")
				.put("line6", "G------")
				.build();
	
		List<Operation> operations = ImmutableList.<Operation> of(
				new SetTurn(rId), 
				new Set(MOVE, "60, 51"), 
				new Set(DESTROY, "00"));

		assertMoveOk(move(gId, state, operations));
	}
	
	// ========================= Illegal tests ==========================
	
	@Test
	public void test_red_move_notRedTurn() { 
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put(TURN, G)
				.put("line0", "---R---")
				.put("line1", "-------")
				.put("line2", "-------")
				.put("line3", "-------")
				.put("line4", "-------")
				.put("line5", "-------")
				.put("line6", "---G---")
				.build();

		List<Operation> operations = ImmutableList.<Operation> of(
				new SetTurn(gId), 
				new Set(MOVE, "03, 14"), 
				new Set(DESTROY, "66"));

		assertHacker(move(rId, state, operations));
	}
	
	@Test
	public void test_green_move_notGreenTurn() { 
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put(TURN, R)
				.put("line0", "------R")
				.put("line1", "---X-X")
				.put("line2", "-------")
				.put("line3", "-------")
				.put("line4", "---X---")
				.put("line5", "---XX--")
				.put("line6", "-----G-")
				.build();
		  
		List<Operation> operations = ImmutableList.<Operation> of(
				new SetTurn(rId), 
				new Set(MOVE, "65, 66"), 
				new Set(DESTROY, "11"));

		assertHacker(move(gId, state, operations));
	}
	
	@Test
	public void test_green_move_left_destroyed() {
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put(TURN, G)
				.put("line0", "------R")
				.put("line1", "----X-X")
				.put("line2", "-------")
				.put("line3", "-------")
				.put("line4", "---X---")
				.put("line5", "---X---")
				.put("line6", "-----XG")
				.build();
		  
		List<Operation> operations = ImmutableList.<Operation> of(
				new SetTurn(rId), 
				new Set(MOVE, "66, 65"), 
				new Set(DESTROY, "00"));

		assertHacker(move(gId, state, operations));
	}
	
	@Test
	public void test_green_move_outboard() {
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put(TURN, G)
				.put("line0", "------R")
				.put("line1", "----X-X")
				.put("line2", "-------")
				.put("line3", "-------")
				.put("line4", "---X---")
				.put("line5", "---X---")
				.put("line6", "-----XG")
				.build();
		  
		List<Operation> operations = ImmutableList.<Operation> of(
				new SetTurn(rId), 
				new Set(MOVE, "66, 67"), 
				new Set(DESTROY, "00"));

		assertHacker(move(gId, state, operations));
	}
	
	@Test
	public void test_red_move_outboard() {
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put(TURN, R)
				.put("line0", "-------")
				.put("line1", "----X-X")
				.put("line2", "-X-----")
				.put("line3", "---X---")
				.put("line4", "---X---")
				.put("line5", "-------")
				.put("line6", "R----XG")
				.build();
		  
		List<Operation> operations = ImmutableList.<Operation> of(
				new SetTurn(gId), 
				new Set(MOVE, "60, 70"), 
				new Set(DESTROY, "01"));

		assertHacker(move(rId, state, operations));
	}
	
	@Test
	public void test_red_move_toofar() {
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put(TURN, R)
				.put("line0", "R------")
				.put("line1", "----X-X")
				.put("line2", "-X-----")
				.put("line3", "---X---")
				.put("line4", "---X---")
				.put("line5", "-------")
				.put("line6", "-----XG")
				.build();
		  
		List<Operation> operations = ImmutableList.<Operation> of(
				new SetTurn(gId), 
				new Set(MOVE, "00, 02"), 
				new Set(DESTROY, "01"));

		assertHacker(move(rId, state, operations));
	}
	
	@Test
	public void test_green_move_toofar() {
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put(TURN, G)
				.put("line0", "-------")
				.put("line1", "----X-X")
				.put("line2", "-------")
				.put("line3", "---R---")
				.put("line4", "---G---")
				.put("line5", "---X---")
				.put("line6", "-----XX")
				.build();
		 
		List<Operation> operations = ImmutableList.<Operation> of(
				new SetTurn(rId), 
				new Set(MOVE, "43, 00"), 
				new Set(DESTROY, "11"));

		assertHacker(move(gId, state, operations));
	}
	
	@Test
	public void test_green_move_occupied() {
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put(TURN, G)
				.put("line0", "-------")
				.put("line1", "----X-X")
				.put("line2", "-------")
				.put("line3", "---R---")
				.put("line4", "---G---")
				.put("line5", "---X---")
				.put("line6", "-----XX")
				.build();
		
		List<Operation> operations = ImmutableList.<Operation> of(
				new SetTurn(rId), 
				new Set(MOVE, "43, 33"), 
				new Set(DESTROY, "11"));

		assertHacker(move(gId, state, operations));
	}
	
	@Test
	public void test_green_move_down_destroyed() {
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put(TURN, G)
				.put("line0", "-------")
				.put("line1", "----X-X")
				.put("line2", "-------")
				.put("line3", "---R---")
				.put("line4", "---G---")
				.put("line5", "---X---")
				.put("line6", "-----XX")
				.build();
	
		List<Operation> operations = ImmutableList.<Operation> of(
				new SetTurn(rId), 
				new Set(MOVE, "43, 53"), 
				new Set(DESTROY, "00"));

		
		assertHacker(move(gId, state, operations));
	}
	
	@Test
	public void test_red_destroy_outboard() {
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put(TURN, R)
				.put("line0", "R------")
				.put("line1", "----X-X")
				.put("line2", "-X-----")
				.put("line3", "---X---")
				.put("line4", "---X---")
				.put("line5", "-------")
				.put("line6", "-----XG")
				.build();
	 
		List<Operation> operations = ImmutableList.<Operation> of(
				new SetTurn(gId), 
				new Set(MOVE, "00, 01"), 
				new Set(DESTROY, "18"));

		assertHacker(move(rId, state, operations));
	}
	
	@Test
	public void test_green_destroy_destroyed() {
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put(TURN, G)
				.put("line0", "-------")
				.put("line1", "----X-X")
				.put("line2", "-------")
				.put("line3", "---R---")
				.put("line4", "---G---")
				.put("line5", "---X---")
				.put("line6", "-----XX")
				.build();
		 
		List<Operation> operations = ImmutableList.<Operation> of(
				new SetTurn(rId), 
				new Set(MOVE, "43, 44"), 
				new Set(DESTROY, "53"));

		
		assertHacker(move(gId, state, operations));
	}
	
	@Test
	public void test_green_destroy_occupied() {
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put(TURN, G)
				.put("line0", "-------")
				.put("line1", "----X-X")
				.put("line2", "-------")
				.put("line3", "---R---")
				.put("line4", "---G---")
				.put("line5", "---X---")
				.put("line6", "-----XX")
				.build();
		 
		List<Operation> operations = ImmutableList.<Operation> of(
				new SetTurn(rId), 
				new Set(MOVE, "43, 44"), 
				new Set(DESTROY, "33"));

		
		assertHacker(move(gId, state, operations));
	}
	
	
	//============================ End game ==============================
	@Test
	public void test_endgame_green_win() { // this is legal
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put(TURN, G)
				.put("line0", "-------")
				.put("line1", "-------")
				.put("line2", "X------")
				.put("line3", "RX-----")
				.put("line4", "XX-G---")
				.put("line5", "----X--")
				.put("line6", "-------")
				.build();
		 
		List<Operation> operations = ImmutableList.<Operation> of(
				new SetTurn(rId), 
				new Set(MOVE, "43, 52"), 
				new Set(DESTROY, "21"),
				new EndGame(gId)
				);

		assertMoveOk(move(gId, state, operations));
	}
	
	
	@Test
	public void test_endgame_red_win() { // this is illegal
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put(TURN, R)
				.put("line0", "-------")
				.put("line1", "-------")
				.put("line2", "X---R--")
				.put("line3", "GX-----")
				.put("line4", "XX-----")
				.put("line5", "-X--X--")
				.put("line6", "-------")
				.build();
	 
		List<Operation> operations = ImmutableList.<Operation> of(
				new SetTurn(gId), 
				new Set(MOVE, "24, 25"), 
				new Set(DESTROY, "22"),
				new EndGame(rId)
				);

		assertHacker(move(rId, state, operations));
	}
}
