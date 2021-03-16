import java.util.*;

public class Engine {
	public int maxDepth;

	public Engine(int depth) {
		this.maxDepth = depth;
	}

	public double eval(Position position) {
		return minimax(position, maxDepth, position.toMove == 'w');
	}

	private double minimax(Position position, int depth, boolean white) {
		if (depth == 0 || gameIsOver(position)) {
			return staticEval(position);
		}

		if (white) {
			double maxEval = Double.NEGATIVE_INFINITY;
			List<Position> children = Utils.generateNextPositions(position);
			for (Position g : children) {
				double eval = minimax(g, depth - 1, false);
				maxEval = Math.max(eval, maxEval);
			}
			return maxEval;
		}
		else {
			double minEval = Double.POSITIVE_INFINITY;
			List<Position> children = Utils.generateNextPositions(position);
			for (Position g : children) {
				double eval = minimax(g, depth - 1, true);
				minEval = Math.min(eval, minEval);
			}
			return minEval;
		}
	}

	private boolean gameIsOver(Position position) {
		List<Move> legalMoves = Utils.generateLegalMoves(position);		
		return legalMoves.size() == 0;
	}

	private double staticEval(Position position) {
		return 0.0;
	}
}