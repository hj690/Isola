package org.isola.client;

//Copyright 2012 Google Inc.
//
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//http://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
////////////////////////////////////////////////////////////////////////////////

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.game_api.GameApi.Operation;
import org.game_api.GameApi.Set;
import org.game_api.GameApi.SetTurn;
import org.isola.client.Color;
import static org.isola.client.Color.B;
import static org.isola.client.Color.W;
import static org.isola.client.Color.R;
import static org.isola.client.Color.G;

import com.google.common.collect.Lists;

/**
 * A heuristic that assigns values for states, and to determine the order in
 * which we visit the next states. <br>
 * A heuristic is used in {@link AlphaBetaPruning} to explore the state tree.
 * 
 * @author yzibin@google.com (Yoav Zibin)
 */
public class Heuristic {
	private final static String rId = "42";
	private final static String gId = "43";
	/**
	 * What is the value of the state for the white player. Higher value means
	 * the white has a better position. When the white wins you can return
	 * {@link Integer#MAX_VALUE}, and when the white loses you can return
	 * {@link Integer#MIN_VALUE}.
	 */
	int getStateValue(Map<String, Object> state) {
		IsolaState board = new IsolaState(state);
		Position wp = board.getPlayerPosition(Color.G);
		int score = 0;
		for (int i = wp.getRow() - 1; i < wp.getRow() + 2; i++)
			for (int j = wp.getColumn() - 1; j < wp.getColumn() + 2; j++) {
				if (i >= 0 && i <= 6 && j >= 0 && j <= 6) {
					if (board.getPieceColor(i, j) == W)
						score++;
					else
						score--;
				}

			}

		return score;

	}

	/**
	 * Returns the order in which we should explore the next states. An optimal
	 * heuristic would return the best state (for the current player) first,
	 * e.g., if it is the white turn then it should return first a state which
	 * is the best for white. For instance, in chess and checkers you should
	 * return first states where a piece was captured, because a capturing move
	 * has the potential of being better than a simple move without capture. <br>
	 * Moreover, the heuristic can decide not to even return some possible moves
	 * if they are obviously "bad", or if they are duplicates (e.g., in
	 * backgammon for a 5-6 roll, we return both 5->6, 6->5, or directly moving
	 * 11 points. If the 3 options result in the same final state, then it is
	 * enough to consider just one of the moves). Another example is the
	 * doubling-cube move that we should not consider in our simple AIs. <br>
	 * The return type is {@code Iterable} and not {@code List} because we might
	 * not need all the next states due to pruning. For example, if alpha-beta
	 * pruning cut of a branch we can stop iterating over the next states.<br>
	 */
	public Iterable<List<Operation>> getOrderedMoves(Map<String,Object> state, Color turnC){
		ArrayList<List<Operation>> orderedMoves = new ArrayList<List<Operation>>();
		IsolaState board = new IsolaState(state);
		Color turn = turnC;
		Color turnOp = turnC == Color.G? Color.R : Color.G; 
		Position from = board.getPlayerPosition(turn);
		Position oppoPos = board.getPlayerPosition(turnOp);
		Position to = new Position();
		Position destory = new Position();
		if(board.can_move(turn)){
		    if(board.getPieceColor(from.getRow() - 1, from.getColumn()) == W)
		    	to = new Position(from.getRow() - 1, from.getColumn());
		    else if(board.getPieceColor(from.getRow() - 1, from.getColumn() - 1) == W)
		    	to = new Position(from.getRow() - 1, from.getColumn() - 1);
		    else if(board.getPieceColor(from.getRow(), from.getColumn() - 1) == W)
		    	to = new Position(from.getRow(), from.getColumn() - 1);
		    else if(board.getPieceColor(from.getRow() + 1, from.getColumn() - 1) == W)
		    	to = new Position(from.getRow() + 1, from.getColumn() - 1);
		    else if(board.getPieceColor(from.getRow() + 1, from.getColumn()) == W)
		    	to = new Position(from.getRow() + 1, from.getColumn());
		    else if(board.getPieceColor(from.getRow() + 1, from.getColumn() + 1) == W)
		    	to = new Position(from.getRow() + 1, from.getColumn() + 1);
		    else if(board.getPieceColor(from.getRow(), from.getColumn() + 1) == W)
		    	to = new Position(from.getRow(), from.getColumn() + 1);
		    else if(board.getPieceColor(from.getRow() - 1, from.getColumn() + 1) == W)
		    	to = new Position(from.getRow() - 1, from.getColumn() + 1);
		}
		
		if(board.can_move(turnOp)){
		    if(board.getPieceColor(oppoPos.getRow() - 1, oppoPos.getColumn()) == W)
		    	destory = new Position(oppoPos.getRow() - 1, oppoPos.getColumn());
		    else if(board.getPieceColor(oppoPos.getRow() - 1, oppoPos.getColumn() - 1) == W)
		    	destory = new Position(oppoPos.getRow() - 1, oppoPos.getColumn() - 1);
		    else if(board.getPieceColor(oppoPos.getRow(), oppoPos.getColumn() - 1) == W)
		    	destory = new Position(oppoPos.getRow(), oppoPos.getColumn() - 1);
		    else if(board.getPieceColor(oppoPos.getRow() + 1, oppoPos.getColumn() - 1) == W)
		    	destory = new Position(oppoPos.getRow() + 1, oppoPos.getColumn() - 1);
		    else if(board.getPieceColor(oppoPos.getRow() + 1, oppoPos.getColumn()) == W)
		    	destory = new Position(oppoPos.getRow() + 1, oppoPos.getColumn());
		    else if(board.getPieceColor(oppoPos.getRow() + 1, oppoPos.getColumn() + 1) == W)
		    	destory = new Position(oppoPos.getRow() + 1, oppoPos.getColumn() + 1);
		    else if(board.getPieceColor(oppoPos.getRow(), oppoPos.getColumn() + 1) == W)
		    	destory = new Position(oppoPos.getRow(), oppoPos.getColumn() + 1);
		    else if(board.getPieceColor(oppoPos.getRow() - 1, oppoPos.getColumn() + 1) == W)
		    	destory = new Position(oppoPos.getRow() - 1, oppoPos.getColumn() + 1);
		}
					
		List<Operation> oper = getMove(from, to, destory, turn);
		orderedMoves.add(oper);
		return orderedMoves;
	}
	
	private List<Operation> getMove(Position from, Position to, Position destroy, Color turn){
		List<Operation> operation = Lists.newArrayList();
		operation.add(new Set(position_To_Str(from), "W"));
		operation.add(new Set(position_To_Str(to), (turn == R ? "R" : "G")));
		operation.add(new Set(position_To_Str(destroy), "B"));
		
		return operation;
	}

	private String position_To_Str(Position position) {
		return Integer.toString(position.getRow())
				+ Integer.toString(position.getColumn());
	}
}