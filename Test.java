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

	private static List<String> readPositions() {
		Path test_path = Paths.get(System.getProperty("user.dir"), "testData/testPositions.txt");
		Charset charset = Charset.forName("ISO-8859-1");
		List<String> positions = new ArrayList<>();

		try {
			positions = Files.readAllLines(test_path, charset);
			return positions;		

		} catch (IOException e) {
			System.out.println(e);
		}

		return positions;
	}

	public static void main(String[] args) {
		int generationDepth = Integer.valueOf(args[0]);
		int numTests = Integer.valueOf(args[1]);
		int engineDepth = Integer.valueOf(args[2]);
		String minimaxVersions = args[3];

		long startTime;
		long endTime;

		// load test positions
		List<String> testPositions = readPositions();	
		System.out.println(testPositions.get(0));	

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

		// test position
		System.out.println("Test position 5 depth = " + maxDepth);
		Position test = new Position();

		test.loadFromFEN(testPositions.get(4));

		int[] nodes = { 44, 1486, 62379, 2103487, 89941194};	

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
		for (int i = 0; i < numTests; i++) {
			System.out.println("Testing move generation correctness from position " + (i+1));
			String outputFile = "testData/Pos" + (i+1) + ".txt";
			System.out.println(testPositions.get(i));
			moveGenerationTest(4, testPositions.get(i), outputFile);			
		}		

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