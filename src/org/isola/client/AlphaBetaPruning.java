// Copyright 2012 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// //////////////////////////////////////////////////////////////////////////////

package org.isola.client;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.game_api.GameApi.Operation;
import org.game_api.GameApi.Set;

import com.google.common.collect.Lists;
import com.google.gwt.user.client.Timer;
import com.google.common.collect.Lists;

/**
 * http://en.wikipedia.org/wiki/Alpha-beta_pruning<br>
 * This algorithm performs both A* and alpha-beta pruning.<br>
 * The set of possible moves is maintained ordered by the current heuristic
 * value of each move. We first use depth=1, and update the heuristic value of
 * each move, then use depth=2, and so on until we get a timeout or reach
 * maximum depth. <br>
 * If a state has {@link TurnBasedState#whoseTurn} null (which happens in
 * backgammon when we should roll the dice), then I treat all the possible moves
 * with equal probabilities. <br>
 * 
 * @author yzibin@google.com (Yoav Zibin)
 */

public class AlphaBetaPruning {
	static class TimeoutException extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}

	static class MoveScore implements Comparable<MoveScore> {
		List<Operation> move;
		int score;

		@Override
		public int compareTo(MoveScore o) {
			return o.score - score; // sort DESC (best score first)
		}
	}

	private Heuristic heuristic;

	public AlphaBetaPruning(Heuristic heuristic) {
		this.heuristic = heuristic;
	}
	
	private Map<String,Object> addMove(Map<String, Object> state, List<Operation> move){
		Map<String,Object> ret = new HashMap<String, Object>(state);
		ret.put(((Set)move.get(0)).getKey(),"0");
		ret.put(((Set)move.get(1)).getKey(),((Set)move.get(1)).getValue());
		return ret;
	}

	public List<Operation> findBestMove(Map<String,Object> state, Color turn, int depth, Timer timer) {
		boolean isGreen = turn == Color.G ? true : false;
		// Do iterative deepening (A*), and slow get better heuristic values for
		// the states.
		List<MoveScore> scores = Lists.newArrayList();
		{
			Iterable<List<Operation>> possibleMoves = heuristic.getOrderedMoves(state, turn);
			for (List<Operation> move : possibleMoves) {
				MoveScore score = new MoveScore();
				score.move = move;
				score.score = Integer.MIN_VALUE;
				scores.add(score);
			}
		}

		try {
			for (int i = 0; i < depth; i++) {
				for (MoveScore moveScore : scores) {
					List<Operation> move = moveScore.move;
					int score = findMoveScore(addMove(state,move), turn, i,
							Integer.MIN_VALUE, Integer.MAX_VALUE, timer);
					if (!isGreen) {
						// the scores are from the point of view of the white,
						// so for black we need to switch.
						score = -score;
					}
					moveScore.score = score;
				}
				Collections.sort(scores); // This will give better pruning on
											// the next iteration.
			}
		} catch (TimeoutException e) {
			// OK, it should happen
		}

		Collections.sort(scores);
		return scores.get(0).move;
	}

	/**
	 * If we get a timeout, then the score is invalid.
	 */
	private int findMoveScore(Map<String,Object> state, Color turn, int depth, int alpha, int beta,Timer timer) throws TimeoutException {
		/*if (timer.didTimeout()) {
			throw new TimeoutException();
		}*/
		System.out.println("search!");
		//GameOver over = state.getGameOver();
		if (depth == 0 || AIHelper.checkOver(state)) {
			return heuristic.getStateValue(state);
		}
		Color color = turn;
		int scoreSum = 0;
		int count = 0;
		Iterable<List<Operation>> possibleMoves = heuristic.getOrderedMoves(state, color);
		for (List<Operation> move : possibleMoves) {
			count++;
			int childScore = findMoveScore(addMove(state,move), color, depth - 1,
					alpha, beta, timer);
			if (color == null) {
				scoreSum += childScore;
			} else if (color.equals("W")) {
				alpha = Math.max(alpha, childScore);
				if (beta <= alpha) {
					break;
				}
			} else {
				beta = Math.min(beta, childScore);
				if (beta <= alpha) {
					break;
				}
			}
		}
		return color == null ? scoreSum / count : color.equals("G") ? alpha : beta;
	}
}