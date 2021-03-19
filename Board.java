import java.util.*;

public class Board {
	public static final int A1 = 0;
	public static final int B1 = 1;
	public static final int C1 = 2;
	public static final int D1 = 3;
	public static final int E1 = 4;
	public static final int F1 = 5;
	public static final int G1 = 6;
	public static final int H1 = 7;
	public static final int A2 = 8;
	public static final int B2 = 9;
	public static final int C2 = 10;
	public static final int D2 = 11;
	public static final int E2 = 12;
	public static final int F2 = 13;
	public static final int G2 = 14;
	public static final int H2 = 15;
	public static final int A8 = 56;
	public static final int B8 = 57;
	public static final int C8 = 58;
	public static final int D8 = 59;
	public static final int E8 = 60;
	public static final int F8 = 61;
	public static final int G8 = 62;
	public static final int H8 = 63;
	
	public int[] squares;

	public Board() {
		squares = new int[64];
	}

	public Board(Board that) {
		this.squares = new int[64];
		for (int i = 0; i < 64; i++) {
			this.squares[i] = that.squares[i];
		}
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
}