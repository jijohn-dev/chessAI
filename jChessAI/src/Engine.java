import java.util.*;

public class Engine {
	public int maxDepth;

	public static int[] pieceValues = {0, 1, 3, 3, 5, 9, 0};

	private Move bestMove;
	
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

	public Move computerMove(Position position) {
		Position positionCopy = new Position();		
		positionCopy.loadFromFEN(position.generateFEN());		
		minimaxABO(positionCopy, maxDepth, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, position.toMove == 'w');		
		return bestMove;
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
		if (gameIsOver(position)) {
			if (position.checkmate()) {
				return whiteToPlay ? -100 : 100;
			}
			if (position.stalemate()) {
				return 0.0;
			}			
		}
		if (depth == 0) {
			// capture search
			return staticEval(position);
		}

		if (whiteToPlay) {
			double maxEval = Double.NEGATIVE_INFINITY;
			List<Move> legalMoves = Utils.generateLegalMoves(position);

			// order moves
			for (Move move : legalMoves) {
				move.score = scoreMove(position, move);
			}

			legalMoves.sort(new MoveComparator());

			for (Move move : legalMoves) {			
				position.makeMove(move);							
				double eval = minimaxABO(position, depth - 1, alpha, beta, false);
				position.undoMove();	

				if (eval > maxEval) {
					maxEval = eval;
					if (depth == maxDepth) {
						bestMove = move;
					}
				}				

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

			legalMoves.sort(new MoveComparator());

			for (Move move : legalMoves) {
				position.makeMove(move);
				double eval = minimaxABO(position, depth - 1, alpha, beta, true);
				position.undoMove();
				
				if (eval < minEval) {					
					minEval = eval;
					if (depth == maxDepth) {
						bestMove = move;
					}
				}				

				beta = Math.min(beta, minEval);				
				if (beta <= alpha) {					
					break;
				}
			}
			return minEval;
		}
	}

	private static int scoreMove(Position pos, Move move) {
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
		if (Piece.name(pos.at(move.Start)) != Piece.Pawn) {			
			int step = Piece.isColor(Piece.name(pos.at(move.Start)), 'w') ? MoveData.Up : MoveData.Down;
			int enemyColor = step == MoveData.Up ? Piece.Black : Piece.White;
			if (MoveData.DistanceToEdge[move.Target][4] != 0) {
				if (pos.at(move.Target + MoveData.Left + step) == (Piece.Pawn | enemyColor)) {
					score -= pieceValues[Piece.name(pos.at(move.Start))];
				}
			}
			else if (MoveData.DistanceToEdge[move.Target][5] != 0) {
				if (pos.at(move.Target + MoveData.Right + step) == (Piece.Pawn | enemyColor)) {
					score -= pieceValues[Piece.name(pos.at(move.Start))];
				}
			}
		}
		return score;
	}

	private static class MoveComparator implements Comparator<Move> {
		public int compare(Move a, Move b) {
			return b.score - a.score;
		}
	}

	private boolean gameIsOver(Position position) {
		List<Move> legalMoves = Utils.generateLegalMoves(position);		
		return legalMoves.size() == 0;
	}

	private static double staticEval(Position position) {
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

	private static void testEvalutation(String fen) {
		Engine engine = new Engine(3);

		Position pos = new Position(fen);
		pos.printBoard();

		double evalABO = engine.eval(pos, 2);
		double balance = Engine.staticEval(pos);

		System.out.println("ABO evaluation: " + evalABO);
		System.out.println("Best move : " + engine.bestMove);
		System.out.println("Static evaluation: " + balance);
	}

	public static void main(String[] args) {
		Position pos = new Position("r1bqkb1r/pppppppp/n7/4P3/2B5/5Q2/PPPP1PP/RNB1KBNR b KQkq - 0 1");

		testEvalutation(pos.generateFEN());

		pos.makeMove(new Move("h8g8"));
		pos.makeMove(new Move("f3f7"));

		System.out.println(Utils.generateLegalMoves(pos).size());
	}
}