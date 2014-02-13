package org.isola.client;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.isola.client.GameApi.Operation;
import org.isola.client.GameApi.Set;
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
		IsolaLogic.checkMoveIsLegal(verifyMove);
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
	private final int rId = 1;
	private final int gId = 2;
	private final Map<String, Object> rInfo = ImmutableMap.<String, Object> of(
			PLAYER_ID, rId);
	private final Map<String, Object> gInfo = ImmutableMap.<String, Object> of(
			PLAYER_ID, gId);
	private final List<Map<String, Object>> playersInfo = ImmutableList.of(
			rInfo, gInfo);
	private final ImmutableMap<String, Object> emptyState = ImmutableMap
			.<String, Object> of();

	

	private VerifyMove move(int lastMovePlayerId, Map<String, Object> lastState, List<Operation> lastMove) {
		return new VerifyMove(rId, playersInfo,
		// we never need to check the resulting state
		// (the server makes it, and the game doesn't have any hidden decisions such in Battleships)
		emptyState, 
		lastState, lastMove, lastMovePlayerId);
	}

	
	//================legal tests===============
	
	@Test
	public void test_red_move_rightDown() { 
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put(TURN, R)
				.put("line1", "---R---")
				.put("line2", "-------")
				.put("line3", "-------")
				.put("line4", "-------")
				.put("line5", "-------")
				.put("line6", "-------")
				.put("line7", "---G---")
				.build();

		List<Operation> operations = ImmutableList.<Operation> of(
				new Set(TURN,G), 
				new Set(MOVE, "14, 25"), 
				new Set(DESTROY, "77"));

		assertMoveOk(move(rId, state, operations));
	}
	
	@Test
	public void test_green_move_upleft() { 
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put(TURN, G)
				.put("line1", "---R---")
				.put("line2", "-------")
				.put("line3", "-------")
				.put("line4", "-------")
				.put("line5", "-------")
				.put("line6", "-------")
				.put("line7", "---G---")
				.build();
	
		List<Operation> operations = ImmutableList.<Operation> of(
				new Set(TURN,R), 
				new Set(MOVE, "74, 63"), 
				new Set(DESTROY, "23"));

		assertMoveOk(move(gId, state, operations));
	}
	
	@Test
	public void test_green_move_right() {  
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put(TURN, G)
				.put("line1", "------R")
				.put("line2", "---X-X")
				.put("line3", "-------")
				.put("line4", "-------")
				.put("line5", "---X---")
				.put("line6", "---XX--")
				.put("line7", "-----G-")
				.build();
	
		List<Operation> operations = ImmutableList.<Operation> of(
				new Set(TURN,R), 
				new Set(MOVE, "76, 77"), 
				new Set(DESTROY, "11"));

		assertMoveOk(move(gId, state, operations));
	}
	
	@Test
	public void test_red_move_left() {  
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put(TURN, R)
				.put("line1", "X-----R")
				.put("line2", "---X-X-")
				.put("line3", "-------")
				.put("line4", "-------")
				.put("line5", "---X---")
				.put("line6", "---XX--")
				.put("line7", "------G")
				.build();
	
		List<Operation> operations = ImmutableList.<Operation> of(
				new Set(TURN,G), 
				new Set(MOVE, "17, 16"), 
				new Set(DESTROY, "67"));

		assertMoveOk(move(rId, state, operations));
	}
	
	@Test
	public void test_green_move_upright() { 
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put(TURN, G)
				.put("line1", "------R")
				.put("line2", "---X-X")
				.put("line3", "-------")
				.put("line4", "-------")
				.put("line5", "---X---")
				.put("line6", "---XX--")
				.put("line7", "G------")
				.build();
	
		List<Operation> operations = ImmutableList.<Operation> of(
				new Set(TURN,R), 
				new Set(MOVE, "71, 62"), 
				new Set(DESTROY, "11"));

		assertMoveOk(move(gId, state, operations));
	}
	
	// ============ Illegal tests =======================
	
	@Test
	public void test_red_move_notRedTurn() { 
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put(TURN, G)
				.put("line1", "---R---")
				.put("line2", "-------")
				.put("line3", "-------")
				.put("line4", "-------")
				.put("line5", "-------")
				.put("line6", "-------")
				.put("line7", "---G---")
				.build();

		List<Operation> operations = ImmutableList.<Operation> of(
				new Set(TURN,G), 
				new Set(MOVE, "14, 25"), 
				new Set(DESTROY, "77"));

		assertHacker(move(rId, state, operations));
	}
	
	@Test
	public void test_green_move_notGreenTurn() { 
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put(TURN, R)
				.put("line1", "------R")
				.put("line2", "---X-X")
				.put("line3", "-------")
				.put("line4", "-------")
				.put("line5", "---X---")
				.put("line6", "---XX--")
				.put("line7", "-----G-")
				.build();
		  
		List<Operation> operations = ImmutableList.<Operation> of(
				new Set(TURN,R), 
				new Set(MOVE, "76, 77"), 
				new Set(DESTROY, "11"));

		assertHacker(move(gId, state, operations));
	}
	
	@Test
	public void test_green_move_left_destroyed() {
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put(TURN, G)
				.put("line1", "------R")
				.put("line2", "----X-X")
				.put("line3", "-------")
				.put("line4", "-------")
				.put("line5", "---X---")
				.put("line6", "---X---")
				.put("line7", "-----XG")
				.build();
		  
		List<Operation> operations = ImmutableList.<Operation> of(
				new Set(TURN,R), 
				new Set(MOVE, "77, 76"), 
				new Set(DESTROY, "11"));

		assertHacker(move(gId, state, operations));
	}
	
	@Test
	public void test_green_move_outboard() {
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put(TURN, G)
				.put("line1", "------R")
				.put("line2", "----X-X")
				.put("line3", "-------")
				.put("line4", "-------")
				.put("line5", "---X---")
				.put("line6", "---X---")
				.put("line7", "-----XG")
				.build();
		  
		List<Operation> operations = ImmutableList.<Operation> of(
				new Set(TURN,R), 
				new Set(MOVE, "77, 78"), 
				new Set(DESTROY, "11"));

		assertHacker(move(gId, state, operations));
	}
	
	@Test
	public void test_red_move_outboard() {
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put(TURN, R)
				.put("line1", "R------")
				.put("line2", "----X-X")
				.put("line3", "-X-----")
				.put("line4", "---X---")
				.put("line5", "---X---")
				.put("line6", "-------")
				.put("line7", "-----XG")
				.build();
		  
		List<Operation> operations = ImmutableList.<Operation> of(
				new Set(TURN,G), 
				new Set(MOVE, "11, 01"), 
				new Set(DESTROY, "12"));

		assertHacker(move(rId, state, operations));
	}
	
	@Test
	public void test_red_move_toofar() {
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put(TURN, R)
				.put("line1", "R------")
				.put("line2", "----X-X")
				.put("line3", "-X-----")
				.put("line4", "---X---")
				.put("line5", "---X---")
				.put("line6", "-------")
				.put("line7", "-----XG")
				.build();
		  
		List<Operation> operations = ImmutableList.<Operation> of(
				new Set(TURN,G), 
				new Set(MOVE, "11, 13"), 
				new Set(DESTROY, "12"));

		assertHacker(move(rId, state, operations));
	}
	
	@Test
	public void test_green_move_toofar() {
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put(TURN, G)
				.put("line1", "-------")
				.put("line2", "----X-X")
				.put("line3", "-------")
				.put("line4", "---R---")
				.put("line5", "---G---")
				.put("line6", "---X---")
				.put("line7", "-----XX")
				.build();
		 
		List<Operation> operations = ImmutableList.<Operation> of(
				new Set(TURN,R), 
				new Set(MOVE, "54, 11"), 
				new Set(DESTROY, "11"));

		assertHacker(move(gId, state, operations));
	}
	
	@Test
	public void test_green_move_occupied() {
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put(TURN, G)
				.put("line1", "-------")
				.put("line2", "----X-X")
				.put("line3", "-------")
				.put("line4", "---R---")
				.put("line5", "---G---")
				.put("line6", "---X---")
				.put("line7", "-----XX")
				.build();
		
		List<Operation> operations = ImmutableList.<Operation> of(
				new Set(TURN,R), 
				new Set(MOVE, "54, 44"), 
				new Set(DESTROY, "11"));

		assertHacker(move(gId, state, operations));
	}
	
	@Test
	public void test_green_move_down_destroyed() {
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put(TURN, G)
				.put("line1", "-------")
				.put("line2", "----X-X")
				.put("line3", "-------")
				.put("line4", "---R---")
				.put("line5", "---G---")
				.put("line6", "---X---")
				.put("line7", "-----XX")
				.build();
	
		List<Operation> operations = ImmutableList.<Operation> of(
				new Set(TURN,R), 
				new Set(MOVE, "54, 64"), 
				new Set(DESTROY, "11"));

		
		assertHacker(move(gId, state, operations));
	}
	
	@Test
	public void test_red_destroy_outboard() {
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put(TURN, R)
				.put("line1", "R------")
				.put("line2", "----X-X")
				.put("line3", "-X-----")
				.put("line4", "---X---")
				.put("line5", "---X---")
				.put("line6", "-------")
				.put("line7", "-----XG")
				.build();
	 
		List<Operation> operations = ImmutableList.<Operation> of(
				new Set(TURN,G), 
				new Set(MOVE, "11, 12"), 
				new Set(DESTROY, "18"));

		assertHacker(move(rId, state, operations));
	}
	
	@Test
	public void test_green_destroy_destroyed() {
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put(TURN, G)
				.put("line1", "-------")
				.put("line2", "----X-X")
				.put("line3", "-------")
				.put("line4", "---R---")
				.put("line5", "---G---")
				.put("line6", "---X---")
				.put("line7", "-----XX")
				.build();
		 
		List<Operation> operations = ImmutableList.<Operation> of(
				new Set(TURN,R), 
				new Set(MOVE, "54, 55"), 
				new Set(DESTROY, "64"));

		
		assertHacker(move(gId, state, operations));
	}
	
	@Test
	public void test_green_destroy_occupied() {
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put(TURN, G)
				.put("line1", "-------")
				.put("line2", "----X-X")
				.put("line3", "-------")
				.put("line4", "---R---")
				.put("line5", "---G---")
				.put("line6", "---X---")
				.put("line7", "-----XX")
				.build();
		 
		List<Operation> operations = ImmutableList.<Operation> of(
				new Set(TURN,R), 
				new Set(MOVE, "54, 55"), 
				new Set(DESTROY, "44"));

		
		assertHacker(move(gId, state, operations));
	}
	
	@Test
	public void test_green_destroy_self() {
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put(TURN, G)
				.put("line1", "-------")
				.put("line2", "----X-X")
				.put("line3", "-------")
				.put("line4", "---R---")
				.put("line5", "---G---")
				.put("line6", "---X---")
				.put("line7", "-----XX")
				.build();
		 
		List<Operation> operations = ImmutableList.<Operation> of(
				new Set(TURN,R), 
				new Set(MOVE, "54, 55"), 
				new Set(DESTROY, "55"));

		
		assertHacker(move(gId, state, operations));
	}
	
	//==================== End game ======================
	@Test
	public void test_endgame_green_win() {
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put(TURN, G)
				.put("line1", "-------")
				.put("line2", "-------")
				.put("line3", "X------")
				.put("line4", "RX-----")
				.put("line5", "XX-G---")
				.put("line6", "----X--")
				.put("line7", "-------")
				.build();
		 
		List<Operation> operations = ImmutableList.<Operation> of(
				new Set(TURN,R), 
				new Set(MOVE, "54, 63"), 
				new Set(DESTROY, "32"),
				new EndGame(gId)
				);

		assertMoveOk(move(gId, state, operations));
	}
	
	
	@Test
	public void test_endgame_red_win() {
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put(TURN, R)
				.put("line1", "-------")
				.put("line2", "-------")
				.put("line3", "X---R--")
				.put("line4", "GX-----")
				.put("line5", "XX-----")
				.put("line6", "-X--X--")
				.put("line7", "-------")
				.build();
	 
		List<Operation> operations = ImmutableList.<Operation> of(
				new Set(TURN,G), 
				new Set(MOVE, "35, 36"), 
				new Set(DESTROY, "32"),
				new EndGame(rId)
				);

		assertMoveOk(move(rId, state, operations));
	}
}
