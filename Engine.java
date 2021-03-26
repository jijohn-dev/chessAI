import java.util.*;

public class Engine {
	public int maxDepth;

	public static int[] pieceValues = {0, 1, 3, 3, 5, 9, 0};

	public Engine(int depth) {
		this.maxDepth = depth;
	}

	public double eval(Position position, int mode) {
		if (mode == 0) {
			return minimax(position, maxDepth, position.toMove == 'w');
		}
		if (mode == 1) {
			return minimaxAB(position, maxDepth, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, position.toMove == 'w');
		}
		if (mode == 2) {

		}
		return 0;
	}

	private double minimax(Position position, int depth, boolean white) {
		if (depth == 0 || gameIsOver(position)) {
			return staticEval(position);
		}

		if (white) {
			double maxEval = Double.NEGATIVE_INFINITY;
			List<Move> legalMoves = Utils.generateLegalMoves(position);
			for (Move move : legalMoves) {
				position.makeMove(move);
				double eval = minimax(position, depth - 1, false);
				position.undoMove();
				maxEval = Math.max(eval, maxEval);				
			}
			return maxEval;
		}
		else {
			double minEval = Double.POSITIVE_INFINITY;
			List<Move> legalMoves = Utils.generateLegalMoves(position);
			for (Move move : legalMoves) {
				position.makeMove(move);
				double eval = minimax(position, depth - 1, true);
				position.undoMove();
				minEval = Math.min(eval, minEval);				
			}
			return minEval;
		}
	}

	private double minimaxAB(Position position, int depth, double alpha, double beta, boolean white) {
		if (depth == 0 || gameIsOver(position)) {
			return staticEval(position);
		}

		if (white) {
			double maxEval = Double.NEGATIVE_INFINITY;
			List<Move> legalMoves = Utils.generateLegalMoves(position);
			for (Move move : legalMoves) {
				position.makeMove(move);
				double eval = minimaxAB(position, depth - 1, alpha, beta, false);
				position.undoMove();
				maxEval = Math.max(eval, maxEval);
				alpha = Math.max(alpha, maxEval);
				if (beta <= alpha) {
					break;
				}
			}
			return maxEval;
		}
		else {
			double minEval = Double.POSITIVE_INFINITY;
			List<Move> legalMoves = Utils.generateLegalMoves(position);
			for (Move move : legalMoves) {
				position.makeMove(move);
				double eval = minimaxAB(position, depth - 1, alpha, beta, true);
				position.undoMove();
				minEval = Math.min(eval, minEval);
				beta = Math.min(beta, minEval);
				if (beta <= alpha) {
					break;
				}
			}
			return minEval;
		}
	}

	private boolean gameIsOver(Position position) {
		List<Move> legalMoves = Utils.generateLegalMoves(position);		
		return legalMoves.size() == 0;
	}

	private double staticEval(Position position) {
		double balance = 0.0;
		for (int i = 0; i < 64; i++) {
			if (Piece.isColor(position.at(i), 'w')) {
				balance += pieceValues[Piece.name(position.at(i))];
			}
			else if (Piece.isColor(position.at(i), 'b')) {
				balance -= pieceValues[Piece.name(position.at(i))];
			}
		}
		return balance;
	}
}