import java.util.*;

public class Engine {
	public static final int MINIMAX = 0;
	public static final int MINIMAX_AB = 1;
	public static final int MINIMAX_ABO = 2;

	public static class Eval {
		double eval;
		int mateIn;

		Eval(double eval, int mateIn) {
			this.eval = eval;
			this.mateIn = mateIn;
		}

		boolean higher(Eval that) {
			if (this.eval > that.eval) {
				return true;
			}
			if (this.eval == Double.POSITIVE_INFINITY && that.eval == Double.POSITIVE_INFINITY) {
				return this.mateIn < that.mateIn;
			}
			if (this.eval == Double.NEGATIVE_INFINITY && that.eval == Double.NEGATIVE_INFINITY) {
				return this.mateIn > that.mateIn;
			}
			return false;
		}

		boolean lower(Eval that) {
			if (this.eval < that.eval) {
				return true;
			}
			if (this.eval == Double.NEGATIVE_INFINITY && that.eval == Double.NEGATIVE_INFINITY) {
				return this.mateIn < that.mateIn;
			}
			if (this.eval == Double.POSITIVE_INFINITY && that.eval == Double.POSITIVE_INFINITY) {
				return this.mateIn > that.mateIn;
			}
			return false;
		}

		public String toString() {
			if (eval == Double.POSITIVE_INFINITY) {
				return "mate in " + mateIn + " for white";
			}
			if (eval == Double.NEGATIVE_INFINITY) {
				return "mate in " + mateIn + " for black";
			}
			if (eval < 0) {
				return "-" + eval;
			}
			if (eval > 0) {
				return "+" + eval;
			}
			return "0.0";
		}
	}

	public static int[] pieceValues = {0, 1, 3, 3, 5, 9, 0};

	public int maxDepth;

	private Move bestMove;
	
	public Engine(int depth) {
		this.maxDepth = depth;
	}

	public Eval eval(Position position, int mode) {
		if (mode == MINIMAX) {
			return minimax(position, maxDepth, position.toMove == 'w');
		}
		if (mode == MINIMAX_AB) {
			return minimaxAB(position, maxDepth, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, position.toMove == 'w');
		}
		if (mode == MINIMAX_ABO) {
			return minimaxABO(position, maxDepth, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, position.toMove == 'w');
		}
		return new Eval(0.0, -1);
	}

	public Move computerMove(Position position) {
		Position positionCopy = new Position();		
		positionCopy.loadFromFEN(position.generateFEN());		
		minimaxABO(positionCopy, maxDepth, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, position.toMove == 'w');		
		return bestMove;
	}

	private Eval minimax(Position position, int depth, boolean white) {
		if (depth == 0 || gameIsOver(position)) {
			return staticEval(position);
		}

		if (white) {
			//double maxEval = Double.NEGATIVE_INFINITY;
			Eval maxEval = new Eval(Double.NEGATIVE_INFINITY, Integer.MAX_VALUE);
			List<Move> legalMoves = Utils.generateLegalMoves(position);
			for (Move move : legalMoves) {
				position.makeMove(move);
				Eval eval = minimax(position, depth - 1, false);
				position.undoMove();

				if (eval.higher(maxEval)) {
					maxEval = eval;
				}
			}
			return maxEval;
		}
		else {
			//double minEval = Double.POSITIVE_INFINITY;
			Eval minEval = new Eval(Double.POSITIVE_INFINITY, Integer.MAX_VALUE);
			List<Move> legalMoves = Utils.generateLegalMoves(position);
			for (Move move : legalMoves) {
				position.makeMove(move);
				Eval eval = minimax(position, depth - 1, true);
				position.undoMove();

				if (eval.lower(minEval)) {
					minEval = eval;
				}
			}
			return minEval;
		}
	}

	// minimax with alpha-beta pruning
	private Eval minimaxAB(Position position, int depth, double alpha, double beta, boolean whiteToPlay) {
		if (depth == 0 || gameIsOver(position)) {
			return staticEval(position);
		}

		if (whiteToPlay) {
			//double maxEval = Double.NEGATIVE_INFINITY;
			Eval maxEval = new Eval(Double.NEGATIVE_INFINITY, Integer.MAX_VALUE);
			List<Move> legalMoves = Utils.generateLegalMoves(position);
			for (Move move : legalMoves) {
				position.makeMove(move);
				Eval eval = minimaxAB(position, depth - 1, alpha, beta, false);
				position.undoMove();

				if (eval.higher(maxEval)) {
					maxEval = eval;
				}

				alpha = Math.max(alpha, maxEval.eval);
				if (beta <= alpha) {
					break;
				}
			}
			return maxEval;
		}
		else {
			//double minEval = Double.POSITIVE_INFINITY;
			Eval minEval = new Eval(Double.POSITIVE_INFINITY, Integer.MAX_VALUE);
			List<Move> legalMoves = Utils.generateLegalMoves(position);
			for (Move move : legalMoves) {
				position.makeMove(move);
				Eval eval = minimaxAB(position, depth - 1, alpha, beta, true);
				position.undoMove();

				if (eval.lower(minEval)) {
					minEval = eval;
				}

				beta = Math.min(beta, minEval.eval);
				if (beta <= alpha) {
					break;
				}
			}
			return minEval;
		}
	}

	// minimax with alpha-beta pruning and move ordering
	private Eval minimaxABO(Position position, int depth, double alpha, double beta, boolean whiteToPlay) {
		if (gameIsOver(position)) {
			Eval eval = new Eval(0.0, Integer.MAX_VALUE);
			if (position.checkmate()) {
				eval.eval = whiteToPlay ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
				eval.mateIn = (int) Math.ceil((maxDepth - depth) / 2.0);
				return eval;
			}
			if (position.stalemate()) {
				return eval;
			}			
		}
		if (depth == 0) {
			// TODO capture search
			return staticEval(position);
		}

		if (whiteToPlay) {
			Eval maxEval = new Eval(Double.NEGATIVE_INFINITY, 0);
			List<Move> legalMoves = Utils.generateLegalMoves(position);

			// order moves
			for (Move move : legalMoves) {
				move.score = scoreMove(position, move);
			}

			legalMoves.sort(new MoveComparator());

			for (Move move : legalMoves) {			
				position.makeMove(move);							
				Eval eval = minimaxABO(position, depth - 1, alpha, beta, false);
				position.undoMove();	

				if (eval.higher(maxEval)) {
					maxEval = eval;
					if (depth == maxDepth) {
						bestMove = move;
					}
				}				

				alpha = Math.max(alpha, maxEval.eval);
				if (beta <= alpha) {
					break;
				}
			}
			return maxEval;
		}
		else {
			Eval minEval = new Eval(Double.POSITIVE_INFINITY, 0);
			List<Move> legalMoves = Utils.generateLegalMoves(position);

			// order moves
			for (Move move : legalMoves) {
				move.score = scoreMove(position, move);
			}

			legalMoves.sort(new MoveComparator());

			for (Move move : legalMoves) {
				position.makeMove(move);
				Eval eval = minimaxABO(position, depth - 1, alpha, beta, true);
				position.undoMove();
				
				if (eval.lower(minEval)) {
					minEval = eval;
					if (depth == maxDepth) {
						bestMove = move;
					}
				}

				beta = Math.min(beta, minEval.eval);
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
		int piece = Piece.name(pos.at(move.Start));
		if (Piece.name(pos.at(move.Start)) != Piece.Pawn) {			
			int step = Piece.isColor(piece, 'w') ? MoveData.Up : MoveData.Down;
			int enemyColor = step == MoveData.Up ? Piece.Black : Piece.White;
			if (MoveData.DistanceToEdge[move.Target][4] != 0) {
				if (pos.at(move.Target + MoveData.Left + step) == (Piece.Pawn | enemyColor)) {
					score -= pieceValues[piece];
				}
			}
			else if (MoveData.DistanceToEdge[move.Target][5] != 0) {
				if (pos.at(move.Target + MoveData.Right + step) == (Piece.Pawn | enemyColor)) {
					score -= pieceValues[piece];
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

	private static Eval staticEval(Position position) {
		Eval eval = new Eval(0.0, Integer.MAX_VALUE);
		double balance = 0.0;
		
		for (int i = 0; i < 64; i++) {
			if (Piece.isColor(position.at(i), 'w')) {
				balance += pieceValues[Piece.name(position.at(i))];				
			}
			else if (Piece.isColor(position.at(i), 'b')) {
				balance -= pieceValues[Piece.name(position.at(i))];				
			}
		}		
		eval.eval = balance;
		return eval;
	}

	private static void testEvaluation(String fen) {
		Engine engine = new Engine(4);

		Position pos = new Position(fen);
		pos.printBoard();

		Eval evalABO = engine.eval(pos, MINIMAX_ABO);
		Eval balance = Engine.staticEval(pos);

		System.out.println("ABO evaluation: " + evalABO);
		System.out.println("Best move: " + engine.bestMove);
		System.out.println("Static evaluation: " + balance.eval);
	}

	public static void main(String[] args) {
		testEvaluation("r1bqkb1r/pppppppp/n7/4P3/2B5/5Q2/PPPP1PP/RNB1KBNR b KQkq - 0 1");

		// mate in 2
		testEvaluation("4k3/8/8/R7/1R/8/7K/8 w - - 0 1");

		// mate in 1 for white, black to play
		testEvaluation("4k3/1R6/8/R7/8/8/7K/8 b - - 1 1");

		// mate in 1 for white, white to play
		testEvaluation("3k4/1R6/8/R7/8/8/7K/8 w - - 2 2");
	}
}