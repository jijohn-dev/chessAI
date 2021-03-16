import java.util.*;

public class Utils {
	// generate list of positions reachable in one move
	static List<Position> generateNextPositions(Position position) {
		List<Position> positions = new ArrayList<>();

		List<Move> moves = generateLegalMoves(position);

		for (Move move : moves) {			
			Position newPosition = applyMove(position, move);
			positions.add(newPosition);
		}

		return positions;
	}

	// generate list of legal moves from position
	static List<Move> generateLegalMoves(Position pos) {
		List<Move> moves = new ArrayList<>();

		for (int i = 0; i < 64; i++) {
			if (pos.board.squares[i] != 0 && Piece.isColor(pos.board.squares[i], pos.toMove)) {
				int piece = pos.board.squares[i];
				int name = Piece.name(piece);
				if (name == Piece.Pawn) {
					int step = pos.toMove == 'w' ? MoveData.Up : MoveData.Down;
					// 1 square
					if (pos.board.squares[i + step] == 0) {
						moves.add(new Move(i, i + step));
					}

					// 2 squares
					if ((i < 16 && pos.toMove == 'w') || i > 47 && pos.toMove == 'b') {
						if (pos.board.squares[i + 2*step] == 0 && pos.board.squares[i + step] == 0) {
							moves.add(new Move(i, i + 2*step));
						}
					}					

					// capture
					if (MoveData.DistanceToEdge[i][3] > 0) {
						if (Piece.isColor(pos.board.squares[i + step + 1], Opposite(pos.toMove)) || i + step + 1 == pos.enPassantTarget) {
							moves.add(new Move(i, i + step + 1));
						} 
					}
					if (MoveData.DistanceToEdge[i][2] > 0) {
						if (Piece.isColor(pos.board.squares[i + step - 1], Opposite(pos.toMove)) || i + step - 1 == pos.enPassantTarget) {
							moves.add(new Move(i, i + step - 1));
						} 
					}					
				}
				else if (name == Piece.Knight) {					
					for (int offset : MoveData.KnightOffsets.get(i)) {
						if (!Piece.isColor(pos.board.squares[i + offset], pos.toMove)) {
							moves.add(new Move(i, i + offset));
						}	
					}
				}
				else {
					int dirStart = name == Piece.Bishop ? 4 : 0;
					int dirEnd = name == Piece.Rook ? 4 : 8;
					for (int dir = dirStart; dir < dirEnd; dir++) {
						int maxDist =  MoveData.DistanceToEdge[i][dir];
						int dist = name == Piece.King ? Math.min(1, maxDist) : maxDist;
						for (int steps = 1; steps <= dist; steps++) {
							int squareIdx = i + steps * MoveData.Offsets[dir];											
							if (!Piece.isColor(pos.board.squares[squareIdx], pos.toMove)) {
								moves.add(new Move(i, squareIdx));
								if (Piece.isColor(pos.board.squares[squareIdx], Opposite(pos.toMove))) {
									break;
								}
							}
							else {
								break;
							}
						}						
					}
				}
			}
		}

		// castling
		// TODO: castling through check
		if (pos.castlingRights.whiteKingSide) {
			if (pos.board.squares[5] == 0 && pos.board.squares[6] == 0) {
				moves.add(new Move(4, 6));
			}			
		}
		if (pos.castlingRights.whiteQueenSide) {
			if (pos.board.squares[3] == 0 && pos.board.squares[2] == 0) {
				moves.add(new Move(4, 2));
			}
		}
		if (pos.castlingRights.blackKingSide) {
			if (pos.board.squares[61] == 0 && pos.board.squares[62] == 0) {
				moves.add(new Move(60, 62));
			}
		}
		if (pos.castlingRights.blackKingSide) {
			if (pos.board.squares[59] == 0 && pos.board.squares[58] == 0) {
				moves.add(new Move(60, 58));
			}
		}

		// promoting

		// filter illegal moves
		List<Move> legalMoves = new ArrayList<>();
		for (Move move : moves) {
			Position newPos = applyMove(pos, move);
			if (!kingInCheck(newPos)) {
				legalMoves.add(move);
			}
		}

		return legalMoves;
	}

	static boolean kingInCheck(Position pos) {
		return false;
	}

	static Position applyMove(Position position, Move move) {
		Position newPosition = new Position();			
		newPosition.castlingRights = new CastlingRights(position.castlingRights);
		newPosition.moveCount = position.moveCount;

		for (int i = 0; i < 64; i++) {	
			newPosition.set(i, position.board.squares[i]);			
		}		

		newPosition.set(move.Target, position.board.squares[move.Start]);
		newPosition.set(move.Start, 0);

		// castling
		if (position.castlingRights.whiteKingSide && move.Start == 4) {
			newPosition.set(move.Start, Piece.Rook);
			newPosition.set(Board.bottomRight, Piece.Empty);
		}
		if (position.castlingRights.whiteQueenSide && move.Start == 4) {
			newPosition.set(move.Start, Piece.Rook);
			newPosition.set(Board.bottomLeft, Piece.Empty);
		}
		if (position.castlingRights.blackKingSide && move.Start == 60) {
			newPosition.set(move.Start, Piece.Rook);
			newPosition.set(Board.topLeft, Piece.Empty);
		}
		if (position.castlingRights.blackQueenSide && move.Start == 60) {
			newPosition.set(move.Start, Piece.Rook);
			newPosition.set(Board.topRight, Piece.Empty);
		}

		// en passant
		if (move.Target == position.enPassantTarget) {
			int step = position.toMove == 'w' ? 1 : -1;
			newPosition.set(position.enPassantTarget + step, Piece.Empty);
		}

		newPosition.toMove = position.toMove == 'w' ? 'b' : 'w';
		newPosition.halfMoveCount = position.halfMoveCount + 1;
		if (position.toMove == 'b') {
			newPosition.moveCount++;
		}

		int piece = position.board.squares[move.Start];
		
		// update en passant target if applicable
		newPosition.enPassantTarget = -1;
		if (Piece.name(piece) == Piece.Pawn) {
			if (Math.abs(move.Target - move.Start) == 2 * MoveData.Up) {
				newPosition.enPassantTarget = position.toMove == 'w' ? move.Start + MoveData.Up : move.Start + MoveData.Down;
			}
		}

		// update castling rights
		if (Piece.name(piece) == Piece.King) {
			if (position.toMove == 'w') {
				newPosition.castlingRights.whiteKingSide = false;
				newPosition.castlingRights.whiteQueenSide = false;
			}
			else {
				newPosition.castlingRights.blackKingSide = false;
				newPosition.castlingRights.blackQueenSide = false;
			}
		}
		else if (Piece.name(piece) == Piece.Rook) {
			if (move.Start == Board.bottomLeft) {
				newPosition.castlingRights.whiteKingSide = false;
			}
			else if (move.Start == Board.bottomRight) {
				newPosition.castlingRights.whiteQueenSide = false;
			}
			else if (move.Start == Board.topLeft) {
				newPosition.castlingRights.blackKingSide = false;
			}
			else if (move.Start == Board.topRight) {
				newPosition.castlingRights.blackQueenSide = false;
			}
		}

		return newPosition;
	}

	static char Opposite(char color) {
		return color == 'w' ? 'b' : 'w';
	}
}