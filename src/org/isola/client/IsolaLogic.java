package org.isola.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.isola.client.GameApi.Delete;
import org.isola.client.GameApi.EndGame;
import org.isola.client.GameApi.Operation;
import org.isola.client.GameApi.Set;
import org.isola.client.GameApi.SetTurn;
import org.isola.client.GameApi.VerifyMove;
import org.isola.client.GameApi.VerifyMoveDone;
import org.isola.client.Color;

import com.google.common.collect.ImmutableList;
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
	private final static int rId = 11;
	private final static int gId = 12;
	
	
	
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
	    List<Integer> playerIds = verifyMove.getPlayerIds();
	    if (lastApiState.isEmpty()) {
	      return getMoveInitial(playerIds);
	    }
	    
	    int lastMovePlayerId = verifyMove.getLastMovePlayerId();
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
	static IsolaState gameApiStateToIsolatState(
			Map<String, Object> gameApiState, List<Integer> playerIds, Color turn) {
//		String turnStr = (String)gameApiState.get(TURN);
//		Color turn = (turnStr == R)? Color.R : Color.G;
		ArrayList<String> boardStr = new ArrayList<String>();
		boardStr.add((String)gameApiState.get("line0")); // line 0
		boardStr.add((String)gameApiState.get("line1")); // line 1
		boardStr.add((String)gameApiState.get("line2")); // line 2
		boardStr.add((String)gameApiState.get("line3")); // line 3
		boardStr.add((String)gameApiState.get("line4")); // line 4
		boardStr.add((String)gameApiState.get("line5")); // line 5
		boardStr.add((String)gameApiState.get("line6")); // line 6
	
		IsolaState state = new IsolaState(turn , boardStr, playerIds);
		
		
		return state;
		
	}
	
	 static List<Operation> getMoveInitial(List<Integer> playerIds) {
		    int redPlayerId = playerIds.get(0);
		    int greenPlayerId = playerIds.get(1);
		    List<Operation> operations = Lists.newArrayList();
		    
		    // set turn
		    operations.add(new SetTurn(redPlayerId));
		    
		    // set initial board pieces
		    for (int row = 0; row < 7; row++)
		    	for (int column = 0; column < 7; column++){
		    		if(row == 0 && column == 3){//red piece
		    			operations.add(new Set(Integer.toString(row)+Integer.toString(column), R));
		    		}
		    		else if(row == 6 && column == 3){//green piece
		    			operations.add(new Set(Integer.toString(row)+Integer.toString(column), G));
		    		}
		    		else //white piece
		    			operations.add(new Set(Integer.toString(row)+Integer.toString(column), W));
		    	}
		  
		    return operations;
		  }

	
	 private static void check(boolean val, Object... debugArguments) {
		    if (!val) {
		      throw new RuntimeException("We have a hacker! debugArguments="
		          + Arrays.toString(debugArguments));
		    }
		  }
	
	
	

}
