import java.util.*;

public class Test {
	private static void ply(Position position, int depth, int[] result) {
		if (depth == 0) return;			
		List<Move> legalMoves = Utils.generateLegalMoves(position);
		result[result.length - depth] += legalMoves.size();	 			
		for (Move move : legalMoves) {						
			position.makeMove(move);
			ply(position, depth - 1, result);
			position.undoMove();
		}
	}
	public static void main(String[] args) {
		int generationDepth = Integer.valueOf(args[0]);
		int testPosDepth = Integer.valueOf(args[1]);
		int engineDepth = Integer.valueOf(args[2]);
		String minimaxVersions = args[3];

		long startTime;
		long endTime;

		// move notation constructor
		Move move = new Move("e2e4");
		assert move.Start == 12 && move.Target == 28;

		move = new Move("a8a1");
		assert move.Start == 56 && move.Target == 0;

		// generating legal moves		
		System.out.println("Move generation depth = " + generationDepth);
		Game game = new Game();
		
		int[] expectedMoves = { 20, 400, 8902, 197281, 4865609, 119060324 };		

		int maxDepth = generationDepth;

		int[] result = new int[maxDepth];

		startTime = System.nanoTime();
		ply(game.position, maxDepth, result);
		endTime = System.nanoTime();

		for (int i = 0; i < maxDepth; i++) {
			System.out.println("Depth: " + (i+1) + " ply - Result: " + result[i] + " Expected: " + expectedMoves[i]);
		}

		System.out.print("Time elapsed: ");
		System.out.print((endTime - startTime) / 1000000);
		System.out.println("ms");

		// test position
		System.out.println("Test position 5 depth = " + testPosDepth);
		Position test = new Position();

		String pos5 = "rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8";
		test.loadFromFEN(pos5);

		int[] nodes = { 44, 1486, 62379, 2103487};		

		maxDepth = testPosDepth;
		result = new int[maxDepth];

		startTime = System.nanoTime();
		ply(test, maxDepth, result);
		endTime = System.nanoTime();

		for (int i = 0; i < maxDepth; i++) {
			System.out.println("Depth: " + (i+1) + " ply - Result: " + result[i] + " Expected: " + nodes[i]);
		}

		System.out.print("Time elapsed: ");
		System.out.print((endTime - startTime) / 1000000);
		System.out.println("ms");

		// Engine evaluation
		System.out.println("Engine evaluation depth = " + engineDepth);		
		maxDepth = engineDepth;
		Engine engine = new Engine(maxDepth);

		double res;

		if (minimaxVersions.contains("m")) {
			startTime = System.nanoTime();
			res = engine.eval(test, 0);
			endTime = System.nanoTime();

			System.out.println("Minimax:                           Evalution: " + res + " Time: " + (endTime - startTime) / 1000000 + "ms");
		}

		if (minimaxVersions.contains("p")) {
			startTime = System.nanoTime();
			res = engine.eval(test, 1);
			endTime = System.nanoTime();

			System.out.println("Minimax with pruning:              Evalution: " + res + " Time: " + (endTime - startTime) / 1000000 + "ms");
		}

		if (minimaxVersions.contains("o")) {
			startTime = System.nanoTime();
			res = engine.eval(test, 2);
			endTime = System.nanoTime();

			System.out.println("Minimax with pruning and ordering: Evalution: " + res + " Time: " + (endTime - startTime) / 1000000 + "ms");
		}	
		
		// 3 ply move generation test
	}
}