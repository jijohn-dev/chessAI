import java.util.*;

public class Utils {
	// generate list of legal moves from position
	static List<Move> generateLegalMoves(Position pos) {
		List<Move> moves = new ArrayList<>();

		for (int i = 0; i < 64; i++) {
			if (pos.board.squares[i] != 0 && Piece.isColor(pos.board.squares[i], pos.toMove)) {
				int piece = pos.board.squares[i];
				int name = Piece.name(piece);
				if (name == Piece.Pawn) {
					int step = pos.toMove == 'w' ? MoveData.Up : MoveData.Down;
					if (i + step >= 0 && i + step < 64) {
						// 1 square
						if (pos.board.squares[i + step] == 0) {
							if ((pos.toMove == 'w' && i >= 48) || (pos.toMove == 'b' && i <= 15)) {
								moves.add(new Move(i, i + step, 'q'));
								moves.add(new Move(i, i + step, 'b'));
								moves.add(new Move(i, i + step, 'n'));
								moves.add(new Move(i, i + step, 'r'));
							}
							else {
								moves.add(new Move(i, i + step));
							}							
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
								if ((pos.toMove == 'w' && i >= 48) || (pos.toMove == 'b' && i <= 15)) {
									moves.add(new Move(i, i + step, 'q'));
									moves.add(new Move(i, i + step, 'b'));
									moves.add(new Move(i, i + step, 'n'));
									moves.add(new Move(i, i + step, 'r'));
								}
								else {
									moves.add(new Move(i, i + step));
								}			
							} 
						}
						if (MoveData.DistanceToEdge[i][2] > 0) {
							if (Piece.isColor(pos.board.squares[i + step - 1], Opposite(pos.toMove)) || i + step - 1 == pos.enPassantTarget) {
								if ((pos.toMove == 'w' && i >= 48) || (pos.toMove == 'b' && i <= 15)) {
									moves.add(new Move(i, i + step, 'q'));
									moves.add(new Move(i, i + step, 'b'));
									moves.add(new Move(i, i + step, 'n'));
									moves.add(new Move(i, i + step, 'r'));
								}
								else {
									moves.add(new Move(i, i + step));
								}			
							} 
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
			if (pos.board.squares[5] == 0 && pos.board.squares[6] == Piece.Empty) {
				moves.add(new Move(4, 6));
			}			
		}
		if (pos.castlingRights.whiteQueenSide) {
			if (pos.board.squares[3] == 0 && pos.board.squares[2] == Piece.Empty) {
				moves.add(new Move(4, 2));
			}
		}
		if (pos.castlingRights.blackKingSide) {
			if (pos.board.squares[61] == 0 && pos.board.squares[62] == Piece.Empty) {
				moves.add(new Move(60, 62));
			}
		}
		if (pos.castlingRights.blackKingSide) {
			if (pos.board.squares[59] == 0 && pos.board.squares[58] == Piece.Empty) {
				moves.add(new Move(60, 58));
			}
		}

		// TODO: promoting

		// filter illegal moves
		List<Move> legalMoves = new ArrayList<>();
		for (Move move : moves) {
			pos.makeMove(move);
			if (!kingInCheck(pos)) {
				legalMoves.add(move);
			}
			pos.undoMove();
		}

		return legalMoves;
	}

	static boolean kingInCheck(Position pos) {
		int king = pos.toMove == 'w' ? pos.whiteKing : pos.blackKing;	
		return underAttack(pos, king);
	}

	static boolean underAttack(Position pos, int square) {
		int pawnDirStart = pos.toMove == 'w' ? 6 : 4;
		for (int dir = 0; dir < 8; dir++) {
			int dist =  MoveData.DistanceToEdge[square][dir];			
			for (int steps = 1; steps <= dist; steps++) {
				int squareIdx = square + steps * MoveData.Offsets[dir];											
				if (Piece.isColor(pos.board.squares[squareIdx], pos.toMove)) {
					break;
				}
				else if(Piece.isColor(pos.board.squares[squareIdx], Opposite(pos.toMove))) {
					int name = Piece.name(pos.board.squares[squareIdx]);
					if (name == Piece.Queen) {
						return true;
					}
					else if (name == Piece.Bishop && dir > 3) {
						return true;
					}
					else if (name == Piece.Rook && dir < 4) {
						return true;
					}
					else if (steps == 1 && name == Piece.Pawn && (dir == pawnDirStart || dir == pawnDirStart + 1)) {
						return true;
					}
				}
			}						
		}
		for (int offset : MoveData.KnightOffsets.get(square)) {
			if (Piece.isColor(pos.board.squares[square + offset], Opposite(pos.toMove))) {
				if (Piece.name(pos.board.squares[square + offset]) == Piece.Knight) {
					return true;
				}	
			}			
		}
		return false;
	}	

	static int squareToInt(String square) {
		int file = square.charAt(0) - 97;
		int rank = square.charAt(1) - 49;
		return 8 * rank + file;
	}

	static char Opposite(char color) {
		return color == 'w' ? 'b' : 'w';
	}
}