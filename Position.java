public class Position {
	Board board;
	int enPassantTarget;
	char toMove;
	CastlingRights castlingRights;
	int halfMoveCount;
	int moveCount;

	public Position() {
		this.board = new Board();
		enPassantTarget = -1;
		toMove = 'w';
		castlingRights = new CastlingRights("KQkq");
		halfMoveCount = 0;
		moveCount = 1;
	}

	public void set(int square, int val) {
		board.squares[square] = val;
	}

	public String generateFEN() {
		StringBuilder str = new StringBuilder();

		int emptyCount = 0;

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				char c = this.board.charAt(8 * i + j);

				if (c == '_') {
					emptyCount++;
				}
				else {
					if (emptyCount > 0) {
						str.append(String.valueOf(emptyCount));
					}
					emptyCount = 0;
					str.append(c);
				}
			}	

			if (emptyCount > 0) {
				str.append(String.valueOf(emptyCount));
				emptyCount = 0;
			}

			if (i < 7) {
				str.append("/");
			}
		}

		str.append(' ' + Character.toString(toMove));
		str.append(' ' + castlingRights.fen() + ' ' + enPassToString(enPassantTarget));
		str.append(' ' + String.valueOf(halfMoveCount) + ' ' + String.valueOf(moveCount));
		return str.toString();
	}

	private String enPassToString(int target) {
		char rank = ' '; 
		char file = ' ';
		if (target > 15) {
			rank = '7';
			target -= 40;
		}
		else {
			rank = '2';
		}

		int ascii = 97 + target - 8;
		file = (char) ascii;

		StringBuilder str = new StringBuilder();
		str.append(file);
		str.append(rank);
		return str.toString();
	}

	public void playMove(String move) {
		if (toMove == 'b') {
			moveCount++;
			toMove = 'w';
		}
		else {
			toMove = 'b';
		}
	}
}