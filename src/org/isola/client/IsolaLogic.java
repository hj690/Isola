package org.isola.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.game_api.GameApi.Delete;
import org.game_api.GameApi.EndGame;
import org.game_api.GameApi.Operation;
import org.game_api.GameApi.Set;
import org.game_api.GameApi.SetTurn;
import org.game_api.GameApi.VerifyMove;
import org.game_api.GameApi.VerifyMoveDone;
import org.isola.client.Color;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.isola.client.Color.W;
import static org.isola.client.Color.B;
import static org.isola.client.Color.R;
import static org.isola.client.Color.G;

public class IsolaLogic {
	
	
	private static final String PLAYER_ID = "playerId";
	private static final String TURN = "turn"; // turn of which player (either R
												// or G)
	private static final String R = "R"; // red hand
	private static final String G = "G"; // green hand
	private static final String W = "W"; // red hand
	private static final String B = "B"; // green hand
	private static final String MOVE = "move";
	private static final String DESTROY = "destroy";
	private final static String rId = "42";
	private final static String gId = "43";
	
	
	public static VerifyMoveDone verify(VerifyMove verifyMove) {
		try {
			checkMoveIsLegal(verifyMove);
			return new VerifyMoveDone();
			} catch (Exception e) {
				return new VerifyMoveDone(verifyMove.getLastMovePlayerId(), e.getMessage());
		    }
	}
	
	 static void checkMoveIsLegal(VerifyMove verifyMove) {
		    // Checking the operations are as expected.
		    List<Operation> expectedOperations = getExpectedOperations(verifyMove);
		    List<Operation> lastMove = verifyMove.getLastMove();
		    check(expectedOperations.equals(lastMove), expectedOperations, lastMove);
		    // We use SetTurn, so we don't need to check that the correct player did the move.
		    // However, we do need to check the first move is done by the white player (and then in the
		    // first MakeMove we'll send SetTurn which will guarantee the correct player send MakeMove).
		    if (verifyMove.getLastState().isEmpty()) {
		      check(verifyMove.getLastMovePlayerId() == verifyMove.getPlayerIds().get(0));
		    }
		  }
	
	 @SuppressWarnings("unchecked")
	static List<Operation> getExpectedOperations(VerifyMove verifyMove) {
	    List<Operation> lastMove = verifyMove.getLastMove();
	    Map<String, Object> lastApiState = verifyMove.getLastState();
	    List<String> playerIds = verifyMove.getPlayerIds();
	    if (lastApiState.isEmpty()) {
	      return getMoveInitial(playerIds);
	    }
	    
	    String lastMovePlayerId = verifyMove.getLastMovePlayerId();
	    IsolaState laststate = gameApiStateToIsolatState(verifyMove.getLastState(), verifyMove.getPlayerIds(), 
	    		Color.values()[playerIds.indexOf(lastMovePlayerId)]);
	    
	    /**
	     * positions:{from, to, destroy}
	     */
	    List<Position> positions = Lists.newArrayList(); 
	    for(Operation operation : lastMove){
	    	if(operation instanceof Set){
	    		Set set = (Set) operation;
	    		positions.add(strToPosition(set.getKey()));
	    	}
	    }
	    
	    check(positions.size() == 3); // should be 3 positions: from, to, destroy
	    Position from = positions.get(0);
	    Position to = positions.get(1);
	    Position destroy = positions.get(2);
	    
	    
	    /**
	     * check the operatiosns count is 4 or 5
	     */
	    
	    check(lastMove.size() == 4 || lastMove.size() == 5);
	    
	    /**
	     * check move is legal
	     */
	    	
	    //if move the right piece
		Color turn = laststate.getTurn();
		check(laststate.getPieceColor(from) == turn);
			
		//if from position ok
		check(from.is_in_board());
			
		//is move to neighbor?
		check(move_to_neighbor(from, to));
			
		//if to position blank
		check(laststate.getPieceColor(to) == Color.W);
	    
		/**
		 * make move
		 */
	 
		check(((Set)lastMove.get(1)).getValue() == W);
		laststate.setPieceColor(from, Color.W);
		
		String turnStr = (String)((Set)lastMove.get(2)).getValue();
		check(turnStr == R || turnStr == G);
		check((turnStr == R ? Color.R:Color.G) == turn);
		laststate.setPieceColor(to, turn);
		
		/**
		 * check destroy position is legal
		 */
	 
	    //if destroy position ok
		check(destroy.is_in_board() && laststate.getPieceColor(destroy) == Color.W);
	    
		/**
		 * make destroy
		 */
		laststate.setPieceColor(destroy, Color.B);
		
		/**
		 * check if there is EndGame operation
		 */
		if(lastMove.size() == 5){
			EndGame endgame = (EndGame)lastMove.get(4);
			check(is_End_Game(laststate, endgame));
		}
		return verifyMove.getLastMove();
	  }


	private static boolean move_to_neighbor(Position from, Position to) {
		if(!from.equals(to)){
			if(Math.abs(from.getRow() - to.getRow()) < 2 && Math.abs(from.getColumn() - to.getColumn()) < 2)
				return true;
		}
		return false;
	}
	
	private static boolean is_End_Game(IsolaState laststate, EndGame endgame) {
		Color turn = laststate.getTurn();
		Color opponet = turn.getOppositeColor();
		check(!laststate.can_move(opponet));
		return true;

	}

	private static Position strToPosition(String positionStr) {
		int intPosition = Integer.parseInt(positionStr);
		return new Position((intPosition - intPosition % 10)/10, intPosition % 10);
	}

	@SuppressWarnings("unchecked")
	static IsolaState gameApiStateToIsolatState( Map<String, Object> gameApiState, List<String> playerIds, Color turn) {
		
		IsolaState state = new IsolaState(turn , gameApiState, playerIds);
		
		return state;
		
	}
	
	 static List<Operation> getMoveInitial(List<String> playerIds) {
		    String redPlayerId = playerIds.get(0);
		    List<Operation> operations = Lists.newArrayList();
		    
		    operations.add(new SetTurn(redPlayerId));
		    operations.add(new Set("03", R));
		    operations.add(new Set("63", G));
		  
		    return operations;
		  }

	
	 private static void check(boolean val, Object... debugArguments) {
		    if (!val) {
		      throw new RuntimeException("We have a hacker! debugArguments="
		          + Arrays.toString(debugArguments));
		    }
		  }
	
	
	

}
