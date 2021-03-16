import java.util.*;

public class Test {
	private static void ply(Position position, int depth, int[] result) {		
		List<Move> legalMoves = Utils.generateLegalMoves(position);
		if (depth == 0) return;		
		for (Move move : legalMoves) {
			result[result.length - depth]++;
			Position newPos = Utils.applyMove(position, move);
			ply(newPos, depth - 1, result);
		}
	}
	public static void main(String[] args) {
		// move notation constructor
		Move move = new Move("e2e4");
		assert move.Start == 12 && move.Target == 28;

		move = new Move("a8a1");
		assert move.Start == 56 && move.Target == 0;

		// generating legal moves
		Game game = new Game();
		
		int[] expectedMoves = { 20, 400, 8902, 197281, 4865609, 119060324 };		

		int maxDepth = 5;

		int[] result = new int[maxDepth];

		long startTime = System.nanoTime();
		ply(game.position, maxDepth, result);
		long endTime = System.nanoTime();

		for (int i = 0; i < maxDepth; i++) {
			System.out.println("Depth: " + (i+1) + " ply - Result: " + result[i] + " Expected: " + expectedMoves[i]);
		}

		System.out.print("Time elapsed: ");
		System.out.print((endTime - startTime) / 1000000);
		System.out.println("ms");

		String[] moves = {
			"e2e4", "g8f6",
			"d2d4", "f6e4"
		};

		game = new Game();

		for (int i = 0; i < moves.length + 1; i++) {
			List<Move> legalMoves = Utils.generateLegalMoves(game.position);
			System.out.println(legalMoves.size());
			if (i < moves.length) {
				game.position = Utils.applyMove(game.position, new Move(moves[i]));
				// game.position.board.printBoard();
			}
		}
	}
}