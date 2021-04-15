public class Piece {
	public static final int Empty  = 0;
	public static final int Pawn   = 1;
	public static final int Bishop = 2;
	public static final int Knight = 3;
	public static final int Rook   = 4;
	public static final int Queen  = 5;
	public static final int King   = 6;

	public static final int White = 8;
	public static final int Black = 16;

	public static boolean isColor(int piece, char color) {
		if (piece == 0) return false;
		char pieceColor = piece <= 16 ? 'w' : 'b';
		return pieceColor == color;
	}

	public static int name(int piece) {
		return piece > 16 ? piece - 16 : piece - 8;
	}
}