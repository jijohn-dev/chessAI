import java.util.List;
import java.util.Scanner;

public class Game {
	public static void main(String[] args) {
		Position game = new Position();
		game.printBoard();

		Engine engine = new Engine(4);

		Scanner input = new Scanner(System.in);

		// choose color
		System.out.print("Choose color [white/black]: ");
		String color = input.next();
		char player = color.equals("white") ? 'w' : 'b';

		while (!Utils.gameIsOver(game)) {
			if (game.toMove == player) {
				System.out.print("Move: ");
				String cmd = input.next();
				if (cmd.equals("quit")) {
					break;
				}
				Move move = new Move(cmd);
				List<Move> legalMoves = Utils.generateLegalMoves(game);
				boolean legal = false;
				for (Move legalMove : legalMoves) {
					if (move.toString().equals(legalMove.toString())) {
						legal = true;
					}
				}
				if (legal) {
					game.makeMove(move);
					game.printBoard();
				}
				else {
					System.out.println("illegal move ");
				}			
			}
			else {
				System.out.println("Computer moving");
				List<Move> legalMoves = Utils.generateLegalMoves(game);
				Move bestMove = new Move();
				double minEval = Double.POSITIVE_INFINITY;
				for (Move move : legalMoves) {
					game.makeMove(move);
					double eval = engine.eval(game, 1);
					if (eval < minEval) {
						minEval = eval;
						bestMove = move;
					}
					game.undoMove();
				}
				game.makeMove(bestMove);
				System.out.println(bestMove);
				game.printBoard();
			}
		}	

		input.close();
	}
}