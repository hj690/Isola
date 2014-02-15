package org.isola.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.isola.client.GameApi.EndGame;
import org.isola.client.GameApi.Operation;
import org.isola.client.GameApi.Set;
import org.isola.client.GameApi.VerifyMove;
import org.isola.client.GameApi.VerifyMoveDone;
import org.isola.client.Color;

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

	public static void checkMoveIsLegal(VerifyMove verifyMove) throws Exception {
		IsolaState laststate = gameApiStateToIsolatState(verifyMove.getLastState());
		Move lastmove = gameApiOperationToIsolaMove(verifyMove.getLastMove());
		Position destroy = gameApiOperationToIsolaDestroy(verifyMove.getLastMove());
		Position from = lastmove.getFrom();
		Position to = lastmove.getTo();
		
		
		//if move the right piece
		Color turn = laststate.getTurn();
		if(laststate.getPieceColor(from) != turn)
			throw new Exception("We have a hacker!");
		
		
		//if from position ok
		if(!from.is_in_board())
			throw new Exception("We have a hacker!");
		
		
		//is move to neighbor?
		if(!move_to_neighbor(from, to))
			throw new Exception("We have a hacker!");
		
		//if to position ok
		if(laststate.getPieceColor(to) != W)
			throw new Exception("We have a hacker!");
		
		//make move
		laststate.setPieceColor(to, turn);
		laststate.setPieceColor(from, W);
		
		//if destroy position ok
		if(!destroy.is_in_board() || laststate.getPieceColor(destroy) != W)
			throw new Exception("We have a hacker!");
		
		
		laststate.setPieceColor(destroy, B);
		
		//if end game
		if(!is_End_Game(laststate, verifyMove.getLastMove()))
			throw new Exception("We have a hacker!");

	}
	
	
	


	private static boolean move_to_neighbor(Position from, Position to) {
		if(!from.equals(to)){
			if(Math.abs(from.getRow() - to.getRow()) < 2 && Math.abs(from.getColumn() - to.getColumn()) < 2)
				return true;
		}
		return false;
	}

	private static Position gameApiOperationToIsolaDestroy(
			List<Operation> lastMove) {
		for(int i = 0; i < lastMove.size(); i++){
			Set set = (Set)lastMove.get(i);
			if(set.getKey() == DESTROY){
				String destroyStr = (String)set.getValue(); // eg. "11, 22"
				Position toBeDestroy = strToPosition(destroyStr);
				return toBeDestroy;
			}
		}
		return null;
	}

	private static Move gameApiOperationToIsolaMove(List<Operation> lastMove) {
		for(int i = 0; i < lastMove.size(); i++){
			Set set = (Set)lastMove.get(i);
			if(set.getKey() == MOVE){
				String moveStr = (String)set.getValue(); // eg. "11, 22"
				String[] move = moveStr.split("\\s*(=>|,|\\s)\\s*");
				Position from = strToPosition(move[0]);
				Position to = strToPosition(move[1]);
				Move lastmove = new Move(from, to);
				return lastmove;
			}
		}
		return null;
	}
	
	
	private static boolean is_End_Game(IsolaState laststate, List<Operation> lastmove) {
		Color turn = laststate.getTurn();
		int id = (turn == Color.R)? rId : gId;
		EndGame endgame = null;
		if(lastmove.size() == 4){
			endgame = (EndGame)lastmove.get(3);
		
		
			if(endgame != null && endgame.getPlayerIdToScore().get(Integer.toString(id)) == 1){
				Position position = laststate.getPlayerPosition(turn.getOppositeColor());
				if(laststate.can_move(position))
					return false;
			}
		
		}
		
		return true;

	}

	private static Position strToPosition(String positionStr) {
		int intPosition = Integer.parseInt(positionStr);
		return new Position((intPosition - intPosition % 10)/10, intPosition % 10);
	}

	@SuppressWarnings("unchecked")
	private static IsolaState gameApiStateToIsolatState(Map<String, Object> gameApiState) {
		String turnStr = (String)gameApiState.get(TURN);
		ArrayList<String> boardStr = new ArrayList<String>();
		boardStr.add((String)gameApiState.get("line0")); // line 0
		boardStr.add((String)gameApiState.get("line1")); // line 1
		boardStr.add((String)gameApiState.get("line2")); // line 2
		boardStr.add((String)gameApiState.get("line3")); // line 3
		boardStr.add((String)gameApiState.get("line4")); // line 4
		boardStr.add((String)gameApiState.get("line5")); // line 5
		boardStr.add((String)gameApiState.get("line6")); // line 6
	
		IsolaState state = new IsolaState(turnStr, boardStr);
		
		
		return state;
		
	}

}
