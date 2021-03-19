import java.util.*;

public class Game {
	public static final String startFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
	
	Position position;
	Stack<Board> history;	

	public void loadFromFEN(String fen) {
		position.loadFromFEN(fen);
	}

	public Game() {
		position = new Position();
		loadFromFEN(startFEN);
		history = new Stack<Board>();
	}
}