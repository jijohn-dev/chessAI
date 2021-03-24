import java.util.*;
import java.nio.file.Files;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

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

	private static void moveGenerationTest(int maxPly, String fen, String file) {		
		Path test_path = Paths.get(System.getProperty("user.dir"), file);
		Charset charset = Charset.forName("ISO-8859-1");
		try {
			List<String> data = Files.readAllLines(test_path, charset);
			
			int curLine = 0;
			int numLines = 1;

			for (int ply = 1; ply <= maxPly; ply++) {
				System.out.print("ply " + ply + ": ");
				int total = 0;

				for (int line = 0; line < numLines; line++) {
					Position pos = new Position(fen);					
					String[] lineData = data.get(curLine).split(" ");

					// play moves if any
					StringBuilder moveList = new StringBuilder("");
					for (int i = 0; i < ply-1; i++) {
						moveList.append(lineData[i]);
						moveList.append(" ");
						pos.makeMove(new Move(lineData[i]));
					}
					
					int numMoves = Integer.valueOf(lineData[ply - 1]);
					total += numMoves;
					List<Move> legalMoves = Utils.generateLegalMoves(pos);

					// check for illegal moves
					for (Move move : legalMoves) {
						boolean found = false;
						for (int i = 0; i < numMoves; i++) {
							if (lineData[i + ply].equals(move.toString())) {
								found = true;
							}
						}
						if (!found) {					
							System.out.println(moveList.toString());
							System.out.println("Illegal move: " + move);
							pos.board.printBoard();
							System.out.println();
							return;							
						}
					}

					// check for missing moves					
					for (int i = 0; i < numMoves; i++) {
						String move = lineData[i + ply];
						boolean found = false;
						for (Move m : legalMoves) {
							if (m.toString().equals(move)) {
								found = true;
							}
						}
						if (!found) {
							System.out.println(moveList.toString());								
							System.out.println("Missing move: " + move);
							pos.board.printBoard();
							System.out.println();
							return;								
						}
					}
					
					curLine++;
				}	
				System.out.println("passed");			
				numLines = total;
			}		
			
			System.out.println();

		} catch (IOException e) {
			System.out.println(e);
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
		System.out.println();

		// match legal moves
		System.out.println("Testing move generation correctness from start");
		moveGenerationTest(4, "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 1 0", "testDataStart.txt");		

		// test position
		System.out.println("Test position 5 depth = " + testPosDepth);
		Position test = new Position();

		String pos5 = "rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8";
		test.loadFromFEN(pos5);

		int[] nodes = { 44, 1486, 62379, 2103487, 89941194};		

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
		System.out.println();

		// move generation correctness
		String pos2 = "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 1 20";
		System.out.println("Testing move generation correctness from position 2");
		moveGenerationTest(4, pos2, "testDataPos2.txt");

		String pos3 = "8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 20";
		System.out.println("Testing move generation correctness from position 3");
		moveGenerationTest(4, pos3, "testDataPos3.txt");

		System.out.println("Testing move generation correctness from position 5");
		moveGenerationTest(4, pos5, "testDataPos5.txt");		

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