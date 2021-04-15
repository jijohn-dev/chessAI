import java.util.*;

public class Position {
	Board board;	
	char toMove;
	CastlingRights castlingRights;
	int enPassantTarget;	
	int halfMoveCount;
	int moveCount;
	HashSet<Integer> pieces;

	Stack<Board> stack;	
	Stack<CastlingRights> castlingStack;
	Stack<Integer> enPassStack;
	Stack<Integer> halfMoveStack;
	Stack<HashSet<Integer>> piecesStack;

	int whiteKing;
	int blackKing;

	public Position() {
		board = new Board();
		enPassantTarget = -1;
		toMove = 'w';
		castlingRights = new CastlingRights("KQkq");
		halfMoveCount = 0;
		moveCount = 1;

		pieces = new HashSet<>();

		stack = new Stack<>();
		castlingStack = new Stack<>();
		enPassStack = new Stack<>();
		halfMoveStack = new Stack<>();
		piecesStack = new Stack<>();

		whiteKing = 4;
		blackKing = 60;

		loadFromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 1 0");
		setPieces();
	}

	public Position(String fen) {
		this();
		loadFromFEN(fen);
		pieces.clear();
		for (int i = 0; i < 64; i++) {
			if (at(i) != Piece.Empty) {
				pieces.add(i);
			}
		}
	}

	public void set(int square, int val) {
		board.squares[square] = val;
		if (val == Piece.Empty) {
			pieces.remove(square);
		}
		else {
			pieces.add(square);
		}
	}

	public int at(int square) {
		return board.squares[square];
	}

	public void setPieces() {
		for (int i = 0; i < 16; i++) {
			pieces.add(i);
			pieces.add(i + 48);
		}		
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

	public boolean checkmate() {
		List<Move> legalMoves = Utils.generateLegalMoves(this);
		if (legalMoves.size() == 0) {
			if (Utils.kingInCheck(this, this.toMove)) {
				return true;
			}
		}
		return false;
	}

	public boolean stalemate() {
		List<Move> legalMoves = Utils.generateLegalMoves(this);
		if (legalMoves.size() == 0) {
			if (!Utils.kingInCheck(this, this.toMove)) {
				return true;
			}
		}
		return false;
	}

	public void makeMove(Move move) {
		stack.push(board);
		board = new Board(board);		

		piecesStack.push(pieces);
		// HashSet<Integer> newPieces = new HashSet<>();
		// for (int piece : pieces) {
		// 	newPieces.add(piece);
		// }
		// pieces = newPieces;

		castlingStack.push(castlingRights);
		castlingRights = new CastlingRights(castlingRights);

		enPassStack.push(enPassantTarget);

		halfMoveStack.push(halfMoveCount);
		if (toMove == 'b') {
			// TODO: check for pawn move or capture			
			halfMoveCount++;
		}
		moveCount++;

		// update board
		int piece = board.squares[move.Start];
		int name = Piece.name(piece);
		set(move.Target, piece);
		set(move.Start, Piece.Empty);		
		
		// castling
		if (name == Piece.King) {
			if (move.Start == Board.E1 && move.Target == Board.G1) {
				set(move.Target - 1, Piece.Rook | Piece.White);
				set(Board.H1, Piece.Empty);
			}
			if (move.Start == Board.E1 && move.Target == Board.C1) {
				set(move.Target + 1, Piece.Rook | Piece.White);
				set(Board.A1, Piece.Empty);
			}
			if (move.Start == Board.E8 && move.Target == Board.G8) {
				set(move.Target - 1, Piece.Rook | Piece.Black);
				set(Board.H8, Piece.Empty);
			}
			if (move.Start == Board.E8 && move.Target == Board.C8) {
				set(move.Target + 1, Piece.Rook | Piece.Black);
				set(Board.A8, Piece.Empty);
			}	
		}		
		
		// update en passant target if applicable		
		if (Piece.name(piece) == Piece.Pawn) {
			// en passant
			int step = toMove == 'w' ? MoveData.Down : MoveData.Up;			
			if (move.Target == enPassantTarget) {		
				set(enPassantTarget + step, Piece.Empty);							
			}	
			else if (Math.abs(move.Target - move.Start) == 2 * MoveData.Up) {
				enPassantTarget = move.Start - step;
			}
			else if (move.promotionChoice != '-') {
				set(move.Target, Utils.promotionInt(move.promotionChoice, toMove));				
			}

			// reset en pass
			if (Math.abs(move.Target - move.Start) != 2 * MoveData.Up) {
				enPassantTarget = -1;
			}
		}	
		else {
			enPassantTarget = -1;
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
			if (move.Start == Board.H1) {
				castlingRights.whiteKingSide = false;
			}
			else if (move.Start == Board.A1) {
				castlingRights.whiteQueenSide = false;
			}
			else if (move.Start == Board.H8) {
				castlingRights.blackKingSide = false;
			}
			else if (move.Start == Board.A8) {
				castlingRights.blackQueenSide = false;
			}
		}

		toMove = toMove == 'w' ? 'b' : 'w';		
	}

	public void undoMove() {
		board = stack.pop();
		pieces = piecesStack.pop();
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
			} 
			else {
				// empty squares
				if (Character.isDigit(c)) {					
					int n = Character.getNumericValue(c);
					for (int j = 0; j < n; j++) {
						board.squares[file + 8 * rank] = Piece.Empty;	
						file++;					
					}
				}
				else {									
					int color = Character.isUpperCase(c) ? Piece.White : Piece.Black;
					board.squares[file + 8 * rank] = pieces.get(Character.toLowerCase(c)) | color;
					file++;					
				}
			}			
		}

		setKings();

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

		for (int i = 7; i >= 0; i--) {
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

			if (i > 0) {
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

	public void printBoard() {
		board.printBoard();
	}

	// public void printPieces() {
	// 	for (int piece : pieces) {
	// 		System.out.print(piece + " ");
	// 	}
	// 	System.out.println();
	// }

	public static void main(String[] args) {		
		Position mate = new Position("k1Q5/8/RK6/8/8/8/8/8 b - - 0 1");		
		assert mate.checkmate();
		assert !mate.stalemate();

		Position stale = new Position("k1K5/2Q5/8/8/8/8/8/8 b - - 0 1");		
		assert !stale.checkmate();
		assert stale.stalemate();
	}
}