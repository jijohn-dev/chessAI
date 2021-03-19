import java.util.*;

public class Position {
	Board board;	
	char toMove;
	CastlingRights castlingRights;
	int enPassantTarget;	
	int halfMoveCount;
	int moveCount;

	Stack<Board> stack;	
	Stack<CastlingRights> castlingStack;
	Stack<Integer> enPassStack;
	Stack<Integer> halfMoveStack;

	int whiteKing;
	int blackKing;

	public Position() {
		board = new Board();
		enPassantTarget = -1;
		toMove = 'w';
		castlingRights = new CastlingRights("KQkq");
		halfMoveCount = 0;
		moveCount = 1;

		stack = new Stack<>();
		castlingStack = new Stack<>();
		enPassStack = new Stack<>();
		halfMoveStack = new Stack<>();		

		whiteKing = 4;
		blackKing = 60;
	}

	public void set(int square, int val) {
		board.squares[square] = val;
	}

	public boolean equals(Position that) {
		if (this.toMove != that.toMove) return false;
		if (this.enPassantTarget != that.enPassantTarget) return false;
		if (this.halfMoveCount != that.halfMoveCount) return false;
		if (this.moveCount != that.moveCount) return false;
		
		for (int i = 0; i < 64; i++) {
			if (this.board.squares[i] != that.board.squares[i]) {
				return false;
			}
		}
		return true;
	}

	public void makeMove(Move move) {
		stack.push(board);
		board = new Board(board);		

		castlingStack.push(castlingRights);
		castlingRights = new CastlingRights(castlingRights);

		enPassStack.push(enPassantTarget);

		halfMoveStack.push(halfMoveCount);
		if (toMove == 'w') {
			// TODO: check for pawn move or capture			
			halfMoveCount++;
		}
		moveCount++;

		// update board
		int piece = board.squares[move.Start];
		set(move.Target, piece);
		set(move.Start, 0);				

		// castling
		if (castlingRights.whiteKingSide && move.Start == Board.E1 && move.Target == Board.G1) {
			set(move.Target - 1, Piece.Rook);
			set(Board.H1, Piece.Empty);
		}
		if (castlingRights.whiteQueenSide && move.Start == Board.E1 && move.Target == Board.C2) {
			set(move.Target + 1, Piece.Rook);
			set(Board.A1, Piece.Empty);
		}
		if (castlingRights.blackKingSide && move.Start == Board.E8 && move.Target == Board.G8) {
			set(move.Target - 1, Piece.Rook);
			set(Board.A8, Piece.Empty);
		}
		if (castlingRights.blackQueenSide && move.Start == Board.E8 && move.Target == Board.C8) {
			set(move.Target + 1, Piece.Rook);
			set(Board.H8, Piece.Empty);
		}	
		
		// update en passant target if applicable
		enPassantTarget = -1;
		if (Piece.name(piece) == Piece.Pawn) {
			// en passant
			if (move.Target == enPassantTarget) {
				int step = toMove == 'w' ? 1 : -1;
				set(enPassantTarget + step, Piece.Empty);
			}	
			else if (Math.abs(move.Target - move.Start) == 2 * MoveData.Up) {
				enPassantTarget = toMove == 'w' ? move.Start + MoveData.Up : move.Start + MoveData.Down;
			}
			else if (move.promotionChoice != '-') {
				set(move.Target, promotionInt(move.promotionChoice, toMove));
			}
		}

		// update castling rights and king position
		if (Piece.name(piece) == Piece.King) {
			if (toMove == 'w') {
				castlingRights.whiteKingSide = false;
				castlingRights.whiteQueenSide = false;
				whiteKing = move.Target;
			}
			else {
				castlingRights.blackKingSide = false;
				castlingRights.blackQueenSide = false;
				blackKing = move.Target;
			}
		}
		else if (Piece.name(piece) == Piece.Rook) {
			if (move.Start == Board.A1) {
				castlingRights.whiteKingSide = false;
			}
			else if (move.Start == Board.H1) {
				castlingRights.whiteQueenSide = false;
			}
			else if (move.Start == Board.A8) {
				castlingRights.blackKingSide = false;
			}
			else if (move.Start == Board.H8) {
				castlingRights.blackQueenSide = false;
			}
		}

		toMove = toMove == 'w' ? 'b' : 'w';
		setKings();
	}

	public void undoMove() {
		board = stack.pop();
		toMove = toMove == 'w' ? 'b' : 'w';
		castlingRights = castlingStack.pop();
		enPassantTarget = enPassStack.pop();
		halfMoveCount = halfMoveStack.pop();
		moveCount--;
		setKings();		
	}

	private void setKings() {
		for (int i = 0; i < 64; i++) {
			if (board.squares[i] == (Piece.King | Piece.White)) {
				whiteKing = i;
			} 
			if (board.squares[i] == (Piece.King | Piece.Black)) {
				blackKing = i;
			} 
		}
	}

	public static int promotionInt(char c, char pieceColor) {
		int color = pieceColor == 'w' ? Piece.White : Piece.Black; 
		if (c == 'q') return Piece.Queen | color;
		if (c == 'b') return Piece.Bishop | color;
		if (c == 'n') return Piece.Knight | color;
		if (c == 'r') return Piece.Rook | color;
		else return 0;
	} 

	public void loadFromFEN(String fen) {
		Map<Character, Integer> pieces = new HashMap<>();

		pieces.put('p', Piece.Pawn);
		pieces.put('b', Piece.Bishop);
		pieces.put('n', Piece.Knight);
		pieces.put('r', Piece.Rook);
		pieces.put('q', Piece.Queen);
		pieces.put('k', Piece.King);
		
		String[] fenArray = fen.split(" ");
		// get board position portion of FEN string
		String boardFen = fenArray[0];		

		// start from 8th rank
		int file = 0, rank = 7;

		for (int i = 0; i < boardFen.length(); i++) {
			char c = boardFen.charAt(i);

			// move to next rank
			if (c == '/') {
				file = 0;
				rank--;
			} else {
				// empty squares
				if (Character.isDigit(c)) {
					int n = Character.getNumericValue(c);
					for (int j = 0; j < n; j++) {
						board.squares[file + 8 * rank] = Piece.Empty;						
					}
				}
				else {
					int color = Character.isUpperCase(c) ? Piece.White : Piece.Black;
					board.squares[file + 8 * rank] = pieces.get(Character.toLowerCase(c)) | color;					
				}
				file++;
			}			
		}

		toMove = fenArray[1].charAt(0);
		castlingRights = new CastlingRights(fenArray[2]);

		if (fenArray[3].equals("-")) {
			enPassantTarget = -1;
		}
		else {
			enPassantTarget = Utils.squareToInt(fenArray[3]);
		}		

		halfMoveCount = Integer.parseInt(fenArray[4]);
		moveCount = Integer.parseInt(fenArray[5]);
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

	public static void main(String[] args) {
		Position position = new Position();
		position.loadFromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 1 0");

		List<Move> legalMoves = Utils.generateLegalMoves(position);

		int i = 1;
		for (Move move : legalMoves) {
			List<Move> moves = Utils.generateLegalMoves(position);
			System.out.println(i + ": " + move + " " + moves.size());
			position.makeMove(move);	
			position.undoMove();			 
			i++;
		}	
	}
}