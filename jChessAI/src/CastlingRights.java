public class CastlingRights {
	public boolean whiteKingSide;
	public boolean whiteQueenSide;
	public boolean blackKingSide;
	public boolean blackQueenSide;

	public CastlingRights(String fen) {
		whiteKingSide = fen.charAt(0) == 'K';
		whiteQueenSide = fen.contains("Q");
		blackKingSide = fen.contains("k");
		blackQueenSide = fen.contains("q");
	}

	public CastlingRights(CastlingRights that) {
		this.whiteKingSide = that.whiteKingSide;
		this.whiteQueenSide = that.whiteQueenSide;
		this.blackKingSide = that.blackKingSide;
		this.blackQueenSide = that.blackQueenSide;
	}

	public String fen() {
		StringBuilder fen = new StringBuilder();
		if (whiteKingSide) {
			fen.append("K");
		}
		if (whiteQueenSide) {
			fen.append("Q");
		}
		if (blackKingSide) {
			fen.append("k");
		}
		if (blackQueenSide) {
			fen.append("q");
		}
		if (fen.length() == 0) {
			fen.append("-");
		}
		return fen.toString();
	}
}