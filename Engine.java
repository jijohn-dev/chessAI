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
			return minimaxABO(position, maxDepth, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, position.toMove == 'w');
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

	// minimax with alpha-beta pruning
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

	// minimax with alpha-beta pruning and move ordering
	private double minimaxABO(Position position, int depth, double alpha, double beta, boolean whiteToPlay) {
		if (depth == 0 || gameIsOver(position)) {
			return staticEval(position);
		}

		if (whiteToPlay) {
			double maxEval = Double.NEGATIVE_INFINITY;
			List<Move> legalMoves = Utils.generateLegalMoves(position);

			// order moves
			for (Move move : legalMoves) {
				move.score = scoreMove(position, move);
			}

			Collections.sort(legalMoves, new MoveComparator());

			for (Move move : legalMoves) {
				position.makeMove(move);
				double eval = minimaxABO(position, depth - 1, alpha, beta, false);
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

			// order moves
			for (Move move : legalMoves) {
				move.score = scoreMove(position, move);
			}

			Collections.sort(legalMoves, new MoveComparator());

			for (Move move : legalMoves) {
				position.makeMove(move);
				double eval = minimaxABO(position, depth - 1, alpha, beta, true);
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

	private int scoreMove(Position pos, Move move) {
		int score = 0;

		// capturing a piece with a piece of lower value
		if (pos.at(move.Target) != Piece.Empty) {
			int enemyValue = pieceValues[Piece.name(pos.at(move.Target))];
			int value = pieceValues[Piece.name(pos.at(move.Start))];
			score += enemyValue - value;
		}

		// promoting a pawn
		if (move.promotionChoice != '-') {
			if (move.promotionChoice == 'q') {
				score += 9;
			}
			else if (move.promotionChoice == 'r') {
				score += 5;
			}
			else {
				score += 3;
			}
		}

		// moving a piece to where it can be captured by an enemy pawn
		int step = Piece.isColor(Piece.name(pos.at(move.Start)), 'w') ? MoveData.Up : MoveData.Down;
		int enemyColor = step == MoveData.Up ? Piece.Black : Piece.White;
		if (MoveData.DistanceToEdge[move.Target][4] != 0) {
			if (pos.at(move.Target + MoveData.Left + step) == (Piece.Pawn | enemyColor)) {
				score -= pieceValues[Piece.name(pos.at(move.Start))];
			}
		}
		if (MoveData.DistanceToEdge[move.Target][5] != 0) {
			if (pos.at(move.Target + MoveData.Right + step) == (Piece.Pawn | enemyColor)) {
				score -= pieceValues[Piece.name(pos.at(move.Start))];
			}
		}

		return score;
	}

	private class MoveComparator implements Comparator<Move> {
		public int compare(Move a, Move b) {
			return b.score - a.score;
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