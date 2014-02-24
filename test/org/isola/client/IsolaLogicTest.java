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

	

	private VerifyMove move(int lastMovePlayerId, Map<String, Object> lastState, List<Operation> lastMove) {
		return new VerifyMove(playersInfo,
		// we never need to check the resulting state
		// (the server makes it, and the game doesn't have any hidden decisions such in Battleships)
		emptyState, 
		lastState, lastMove, lastMovePlayerId,
		//playerIdToNumberOfTokensInPot
		ImmutableMap.<Integer, Integer>of());
	}

	

	
	
	
	//================================legal tests==============================
	
	//------------------------ move tests ---------------------
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

		List<Operation> operations = ImmutableList.<Operation> of( 
				new SetTurn(rId), 
				new Set("14", R),
				new Set("03", W));

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
				new SetTurn(gId),
				new Set("52", G),
				new Set("63", W));

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
				new SetTurn(gId), 
				new Set("66", G),
				new Set("65", W));

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
				new SetTurn(rId), 
				new Set("05", R),
				new Set("06", W));

		assertMoveOk(move(rId, state, operations));
	}
	
	//------------------------ destroy tests ---------------------
	
	@Test
	public void test_destroy() { 
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
				new Delete("00"));

		assertMoveOk(move(gId, state, operations));
	}
	
	
	
	// ========================= Illegal tests ==========================
	
	//------------------------ move tests ---------------------
	
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
				new SetTurn(rId), 
				new Set("14", R),
				new Set("03", W));

		assertHacker(move(rId, state, operations));
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
				new SetTurn(gId), 
				new Set("65", G),
				new Set("66", W));
	

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
				new SetTurn(gId), 
				new Set("67", G),
				new Set("66", W));

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
				new SetTurn(rId), 
				new Set("70", R),
				new Set("60", W));

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
				new SetTurn(rId), 
				new Set("02", R),
				new Set("00", W));

		assertHacker(move(rId, state, operations));
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
				new SetTurn(gId), 
				new Set("33", G),
				new Set("43", W));

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
				new SetTurn(gId), 
				new Set("53", G),
				new Set("43", W));

		
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
				new Delete("18"));

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
				new Delete("53"));

		
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
				new Delete("33"));

		
		assertHacker(move(gId, state, operations));
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
				new Delete("43"));

		
		assertHacker(move(gId, state, operations));
	}
	
	//============================ End game ==============================
	@Test
	public void test_endgame_green_win() { // this is legal
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put(TURN, G)
				.put("line0", "-------")
				.put("line1", "-------")
				.put("line2", "XX-----")
				.put("line3", "RX-----")
				.put("line4", "XX-G---")
				.put("line5", "----X--")
				.put("line6", "-------")
				.build();
		 
		List<Operation> operations = ImmutableList.<Operation> of(
				new SetTurn(rId), 
				new EndGame(gId));

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
				new EndGame(rId)
				);

		assertHacker(move(rId, state, operations));
	}
}
