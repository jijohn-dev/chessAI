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

	public static final int A3 = 16;
	public static final int B3 = 17;
	public static final int C3 = 18;
	public static final int D3 = 19;
	public static final int E3 = 20;
	public static final int F3 = 21;
	public static final int G3 = 22;
	public static final int H3 = 23;

	public static final int A4 = 24;
	public static final int B4 = 25;
	public static final int C4 = 26;
	public static final int D4 = 27;
	public static final int E4 = 28;
	public static final int F4 = 29;
	public static final int G4 = 30;
	public static final int H4 = 31;

	public static final int A5 = 32;
	public static final int B5 = 33;
	public static final int C5 = 34;
	public static final int D5 = 35;
	public static final int E5 = 36;
	public static final int F5 = 37;
	public static final int G5 = 38;
	public static final int H5 = 39;

	public static final int A6 = 40;
	public static final int B6 = 41;
	public static final int C6 = 42;
	public static final int D6 = 43;
	public static final int E6 = 44;
	public static final int F6 = 45;
	public static final int G6 = 46;
	public static final int H6 = 47;

	public static final int A7 = 48;
	public static final int B7 = 49;
	public static final int C7 = 50;
	public static final int D7 = 51;
	public static final int E7 = 52;
	public static final int F7 = 53;
	public static final int G7 = 54;
	public static final int H7 = 55;

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
		System.arraycopy(that.squares, 0, this.squares, 0, 64);
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
		boolean white = true;

		if (piece > 16) {
			piece -= 16;
			white = false;
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