import java.util.*;

public class Game {
	public static final String startFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
	
	Position position;
	Stack<Board> history;	

	public Game() {
		position = new Position();
		position.board.loadFromFEN(startFEN);
		history = new Stack<Board>();
	}
}