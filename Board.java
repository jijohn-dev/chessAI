import java.util.*;

public class Board {
	public static final int bottomLeft = 0;
	public static final int bottomRight = 7;
	public static final int topLeft = 56;
	public static final int topRight = 63;
	
	public int[] squares;

	public Board() {
		squares = new int[65];
	}

	public void printBoard() {
		for (int i = 7; i >= 0; i--) {
			for (int j = 0; j < 8; j++) {
				System.out.print(intToChar(squares[8 * i + j]) + " ");
			}
			System.out.print('\n');
		}
	}

	public char charAt(int i) {
		return intToChar(squares[i]);
	}

	public char intToChar(int piece) {
		boolean white = false;

		if (piece > 16) {
			piece -= 16;
			white = true;
		}
		else if (piece > 0) {
			piece -= 8;
		}

		Map<Integer, Character> pieces = new HashMap<>();

		pieces.put(Piece.Empty,  '_');
		pieces.put(Piece.Pawn,   'p');
		pieces.put(Piece.Bishop, 'b');
		pieces.put(Piece.Knight, 'n');
		pieces.put(Piece.Rook,   'r');
		pieces.put(Piece.Queen,  'q');
		pieces.put(Piece.King,   'k');

		char c = pieces.get(piece);

		if (white) {
			c = Character.toUpperCase(c);
		}

		return c;
	}

	public void loadFromFEN(String fen) {
		Map<Character, Integer> pieces = new HashMap<>();

		pieces.put('p', Piece.Pawn);
		pieces.put('b', Piece.Bishop);
		pieces.put('n', Piece.Knight);
		pieces.put('r', Piece.Rook);
		pieces.put('q', Piece.Queen);
		pieces.put('k', Piece.King);

		// get board position portion of FEN string
		String board = fen.split(" ")[0];

		// start from 8th rank
		int file = 0, rank = 7;

		for (int i = 0; i < board.length(); i++) {
			char c = board.charAt(i);

			// move to next rank
			if (c == '/') {
				file = 0;
				rank--;
			} else {
				// empty squares
				if (Character.isDigit(c)) {
					int n = Character.getNumericValue(c);
					for (int j = 0; j < n; j++) {
						squares[file + 8 * rank] = Piece.Empty;						
					}
				}
				else {
					int color = Character.isUpperCase(c) ? Piece.White : Piece.Black;
					squares[file + 8 * rank] = pieces.get(Character.toLowerCase(c)) | color;					
				}
				file++;
			}			
		}
	}

	public static void main(String[] args) {
		String startFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";	

		Board board = new Board();

		board.loadFromFEN((startFEN));
		board.printBoard();
	}
}