package edu.uky.ai.chess.ex;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import edu.uky.ai.chess.Agent;
import edu.uky.ai.chess.state.Bishop;
import edu.uky.ai.chess.state.Board;
import edu.uky.ai.chess.state.Knight;
import edu.uky.ai.chess.state.Outcome;
import edu.uky.ai.chess.state.Pawn;
import edu.uky.ai.chess.state.Piece;
import edu.uky.ai.chess.state.Player;
import edu.uky.ai.chess.state.Queen;
import edu.uky.ai.chess.state.Rook;
import edu.uky.ai.chess.state.State;

/**
 * @author Jon Crosse
 */
public class JCR249 extends Agent {
	
	/**
	 * Constructs a new random agent. You should change the string below from
	 * "Example" to your ID. You should also change the name of this class. In
	 * Eclipse, you can do that easily by right-clicking on this file
	 * (RandomAgent.java) in the Package Explorer and choosing Refactor > Rename.
	 */
	public JCR249() {
		super("JCR249");
	}

	private final Random random = new Random(0);
	
	@Override
	protected State chooseMove(State current) {
		
		// This list will hold all the children state (all possible next moves).
		ArrayList<State> children = new ArrayList<>();
		// Iterate through each child and put it in the list (as long as the
		// search budget hasn't been used up yet).
		Iterator<State> iterator = current.next().iterator();
		Result choice;
		while(!current.budget.hasBeenExhausted() && iterator.hasNext())
			children.add(iterator.next());
			choice = deepening(current, 5);
			if(choice.state == current) {
				return children.get(random.nextInt(children.size()));
			}else {
				return choice.state;
			}
		
	}

	private static int totalValue(Board board, Player player) {
		int total = 0;
		for(Piece piece : board)
			if(piece.player == Player.BLACK) 
				total -= pieceCost(piece);
			else 
				total += pieceCost(piece);
		return total;
	}
	
	private static int pieceCost(Piece piece) {
		if(piece instanceof Pawn)
			return 1;
		else if(piece instanceof Knight)
			return 3;
		else if(piece instanceof Bishop)
			return 3;
		else if(piece instanceof Rook)
			return 5;
		else if(piece instanceof Queen)
			return 9;
		// King
		else
			return 100;
	}

	public class Result {

		public State state;
		public double value;
		public double a;
		public double b;

		public Result(State state, double value, double a, double b) {
			this.state = state;
			this.value = value;
			this.a = a;
			this.b = b;
		}
	}
	
	private Result deepening(State current, int depth_limit) {
		Result best = new Result(current, 0, 0, 0);
		for (int depth = 1; depth <= depth_limit; depth++) {
			Result test;
			if (current.player.equals(Player.BLACK)) {
				test = getMin(current, depth, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
			}else {
				test = getMax(current, depth, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
			}
			if(!test.state.budget.hasBeenExhausted()) {
				if(test.state.outcome == Outcome.WHITE_WINS || test.state.outcome == Outcome.BLACK_WINS) {
					return test;
				}else {
					best = test;
				}	
			}else {
				return best;
			}
		}
		return best;
	}
	
	private Result getMax(State current, int distance, double alpha, double beta) {
		if (distance == 0) {
			return new Result(current, totalValue(current.board, current.player), alpha, beta);
		}
		distance -= 1;
		Result best = new Result(current, Double.NEGATIVE_INFINITY, alpha, beta);
		if (current.budget.hasBeenExhausted()){
			return new Result(current, -1, 0, 0);
		}
		
		State child = null;
		ArrayList<State> children = new ArrayList<>();
		Iterator<State> iterator = current.next().iterator();
		while(!current.budget.hasBeenExhausted() && iterator.hasNext()) {
			child = iterator.next();
			children.add(child);
		}
		for (State i: children) {
			double child_value = getMin(i, distance, alpha, beta).value;
			double score = board_eval(i);
			child_value += score;
			if (child_value > best.value) {
				best = new Result(i, child_value, alpha, beta);
			}
			if (best.value >= beta) {
				return best;
			}
			alpha = Math.max(alpha, best.value);
			if (current.budget.hasBeenExhausted()){
				return new Result(current, -1, 0, 0);
			}
		}
		return best;
	}
	
	private Result getMin(State current, int distance, double alpha, double beta) {
		if (distance == 0) {
			return new Result(current, totalValue(current.board, current.player), alpha, beta);
		}
		distance -= 1;
		Result best = new Result(current, Double.POSITIVE_INFINITY, alpha, beta);
		if (current.budget.hasBeenExhausted()){
			return new Result(current, -1, 0, 0);
		}
		
		State child = null;
		ArrayList<State> children = new ArrayList<>();
		Iterator<State> iterator = current.next().iterator();
		while(!current.budget.hasBeenExhausted() && iterator.hasNext()) {
			child = iterator.next();
			children.add(child);
		}
		
		for (State i: children) {
			double child_value = getMax(i, distance, alpha, beta).value;
			double score = board_eval(i);
			child_value -= score;
			if (child_value < best.value) {
				best = new Result(i, child_value, alpha, beta);
			}
			if (best.value <= alpha) {
				return best;
			}
			beta = Math.min(beta, best.value);
			if (current.budget.hasBeenExhausted()){
				return new Result(current, -1, 0, 0);
			}
		}
		return best;
	}

	private static double board_eval(State state) {
		double white_score = 0.0, black_score = 0.0;
		boolean found = false;
		if (state.check && state.movesUntilDraw == 0) {//draw
			if(state.player == Player.WHITE) {
				white_score += -100;
				found = true;
			}
			else {
				black_score += 100;
				found = true;
			}
		}
		else if (state.check && !state.check) { //stalemate
			if(state.player == Player.WHITE) {
				white_score += -100;
				found = true;
			}
			else {
				black_score += 100;
				found = true;
			}
		}
		else if (state.check && state.player == Player.BLACK && state.check) { // if white wins
			if(state.outcome == Outcome.WHITE_WINS) {
				white_score += 1000;
				found = true;
			}
			else if(state.outcome == null && captured(state)) {
				white_score += 100;
				found = true;
			}
			else return 0;
		}
		else if (state.check && state.player == Player.WHITE && state.check) { // if black wins
			if(state.outcome == Outcome.BLACK_WINS) {
				black_score += -1000;
				found = true;
			}
			else if(state.outcome == null && captured(state)) {
				black_score += -100;
				found = true;
			}
			else return 0;
		}
		if(found) {
			if(state.player == Player.BLACK)
				return white_score;
			else
				return black_score;
		}
		else
			return totalValue(state.board, Player.WHITE) - totalValue(state.board, Player.BLACK);
	}
	
	private static boolean captured(State state) {
		return state.board.countPieces() < state.previous.board.countPieces();
	}
}
