import java.util.*;

public class Utils {
	// generate list of legal moves from position
	static List<Move> generateLegalMoves(Position pos) {
		List<Move> moves = new ArrayList<>();

		// for (int square : pos.pieces) {
		// 	if (pos.pieces.size() > 32) {
		// 		System.out.println("problem" + pos.pieces.size());
		// 	}
		// 	if (pos.at(square) != 0 && Piece.isColor(pos.at(square), pos.toMove)) {
		// 		int piece = pos.at(square);
		// 		int name = Piece.name(piece);
		// 		if (name == Piece.Pawn) {
		// 			int step = pos.toMove == 'w' ? MoveData.Up : MoveData.Down;
		// 			if (square + step >= 0 && square + step < 64) {
		// 				// 1 square
		// 				if (pos.board.squares[square + step] == 0) {
		// 					if ((pos.toMove == 'w' && square >= 48) || (pos.toMove == 'b' && square <= 15)) {
		// 						moves.add(new Move(square, square + step, 'q'));
		// 						moves.add(new Move(square, square + step, 'b'));
		// 						moves.add(new Move(square, square + step, 'n'));
		// 						moves.add(new Move(square, square + step, 'r'));
		// 					}
		// 					else {
		// 						moves.add(new Move(square, square + step));
		// 					}							
		// 				}
		// 				// 2 squares
		// 				if ((square < 16 && pos.toMove == 'w') || square > 47 && pos.toMove == 'b') {
		// 					if (pos.board.squares[square + 2*step] == 0 && pos.board.squares[square + step] == 0) {
		// 						moves.add(new Move(square, square + 2*step));
		// 					}
		// 				}	
		// 				// capture
		// 				// right
		// 				if (MoveData.DistanceToEdge[square][3] > 0) {
		// 					int target = square + step + 1;							
		// 					if (Piece.isColor(pos.board.squares[target], Opposite(pos.toMove))) {								
		// 						if ((pos.toMove == 'w' && square >= 48) || (pos.toMove == 'b' && square <= 15)) {
		// 							moves.add(new Move(square, target, 'q'));
		// 							moves.add(new Move(square, target, 'b'));
		// 							moves.add(new Move(square, target, 'n'));
		// 							moves.add(new Move(square, target, 'r'));
		// 						}
		// 						else {
		// 							moves.add(new Move(square, target));
		// 						}			
		// 					} 
		// 					else if (target == pos.enPassantTarget) {
		// 						if ((pos.toMove == 'w' && target > 32) || (pos.toMove == 'b' && target < 24)) {
		// 							moves.add(new Move(square, target));
		// 						}
		// 					}
		// 				}
		// 				// left
		// 				if (MoveData.DistanceToEdge[square][2] > 0) {
		// 					int target = square + step - 1;
		// 					if (Piece.isColor(pos.board.squares[target], Opposite(pos.toMove))) {
		// 						if ((pos.toMove == 'w' && square >= 48) || (pos.toMove == 'b' && square <= 15)) {
		// 							moves.add(new Move(square, target, 'q'));
		// 							moves.add(new Move(square, target, 'b'));
		// 							moves.add(new Move(square, target, 'n'));
		// 							moves.add(new Move(square, target, 'r'));
		// 						}
		// 						else {
		// 							moves.add(new Move(square, target));
		// 						}			
		// 					}
		// 					else if (target == pos.enPassantTarget) {
		// 						if ((pos.toMove == 'w' && target > 32) || (pos.toMove == 'b' && target < 24)) {
		// 							moves.add(new Move(square, target));
		// 						}
		// 					}
		// 				}	
		// 			}								
		// 		}
		// 		else if (name == Piece.Knight) {										
		// 			for (int offset : MoveData.KnightOffsets.get(square)) {												
		// 				if (!Piece.isColor(pos.board.squares[square + offset], pos.toMove)) {							
		// 					Move move = new Move(square, square + offset);
		// 					moves.add(move);							
		// 				}	
		// 			}					
		// 		}
		// 		else {
		// 			int dirStart = name == Piece.Bishop ? 4 : 0;
		// 			int dirEnd = name == Piece.Rook ? 4 : 8;
		// 			for (int dir = dirStart; dir < dirEnd; dir++) {
		// 				int maxDist =  MoveData.DistanceToEdge[square][dir];
		// 				int dist = name == Piece.King ? Math.min(1, maxDist) : maxDist;

		// 				for (int steps = 1; steps <= dist; steps++) {
		// 					int squareIdx = square + steps * MoveData.Offsets[dir];											
		// 					if (!Piece.isColor(pos.board.squares[squareIdx], pos.toMove)) {
		// 						moves.add(new Move(square, squareIdx));
		// 						if (Piece.isColor(pos.board.squares[squareIdx], Opposite(pos.toMove))) {
		// 							break;
		// 						}
		// 					}
		// 					else {
		// 						break;
		// 					}
		// 				}						
		// 			}
		// 		}
		// 	}
		// }

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
						// right
						if (MoveData.DistanceToEdge[i][3] > 0) {
							int target = i + step + 1;							
							if (Piece.isColor(pos.board.squares[target], Opposite(pos.toMove))) {								
								if ((pos.toMove == 'w' && i >= 48) || (pos.toMove == 'b' && i <= 15)) {
									moves.add(new Move(i, target, 'q'));
									moves.add(new Move(i, target, 'b'));
									moves.add(new Move(i, target, 'n'));
									moves.add(new Move(i, target, 'r'));
								}
								else {
									moves.add(new Move(i, target));
								}			
							} 
							else if (target == pos.enPassantTarget) {
								if ((pos.toMove == 'w' && target > 32) || (pos.toMove == 'b' && target < 24)) {
									moves.add(new Move(i, target));
								}
							}
						}
						// left
						if (MoveData.DistanceToEdge[i][2] > 0) {
							int target = i + step - 1;
							if (Piece.isColor(pos.board.squares[target], Opposite(pos.toMove))) {
								if ((pos.toMove == 'w' && i >= 48) || (pos.toMove == 'b' && i <= 15)) {
									moves.add(new Move(i, target, 'q'));
									moves.add(new Move(i, target, 'b'));
									moves.add(new Move(i, target, 'n'));
									moves.add(new Move(i, target, 'r'));
								}
								else {
									moves.add(new Move(i, target));
								}			
							}
							else if (target == pos.enPassantTarget) {
								if ((pos.toMove == 'w' && target > 32) || (pos.toMove == 'b' && target < 24)) {
									moves.add(new Move(i, target));
								}
							}
						}	
					}								
				}
				else if (name == Piece.Knight) {										
					for (int offset : MoveData.KnightOffsets.get(i)) {												
						if (!Piece.isColor(pos.board.squares[i + offset], pos.toMove)) {							
							Move move = new Move(i, i + offset);
							moves.add(move);							
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
		if (pos.toMove == 'w' && !underAttack(pos, pos.whiteKing, 'b')) {			
			if (pos.castlingRights.whiteKingSide && pos.at(Board.H1) == (Piece.Rook | Piece.White)) {
				if (pos.at(Board.F1) == Piece.Empty && pos.at(Board.G1) == Piece.Empty) {
					if (!underAttack(pos, Board.F1, 'b') && !underAttack(pos, Board.G1, 'b')) {
						moves.add(new Move(Board.E1, Board.G1));
					}				
				}			
			}
			if (pos.castlingRights.whiteQueenSide && pos.at(Board.A1) == (Piece.Rook | Piece.White)) {				
				if (pos.at(Board.B1) == Piece.Empty && pos.at(Board.C1) == Piece.Empty && pos.at(Board.D1) == Piece.Empty) {
					if (!underAttack(pos, Board.D1, 'b') && !underAttack(pos, Board.C1, 'b')) {
						moves.add(new Move(Board.E1, Board.C1));
					}				
				}
			}
		}	
		else if (pos.toMove == 'b' && !underAttack(pos, pos.blackKing, 'w')){
			if (pos.castlingRights.blackKingSide && pos.at(Board.H8) == (Piece.Rook | Piece.Black)) {
				if (pos.at(Board.F8) == Piece.Empty && pos.at(Board.G8) == Piece.Empty) {
					if (!underAttack(pos, Board.F8, 'w') && !underAttack(pos, Board.G8, 'w')) {
						moves.add(new Move(Board.E8, Board.G8));
					}
				}
			}
			if (pos.castlingRights.blackQueenSide && pos.at(Board.A8) == (Piece.Rook | Piece.Black)) {
				if (pos.at(Board.B8) == Piece.Empty && pos.at(Board.C8) == Piece.Empty && pos.at(Board.D8) == Piece.Empty) {
					if (!underAttack(pos, Board.D8, 'w') && !underAttack(pos, Board.C8, 'w')) {
						moves.add(new Move(Board.E8, Board.C8));
					}	
				}
			}
		}
		
		// filter illegal moves
		List<Move> legalMoves = new ArrayList<>();
		char color = pos.toMove;
		for (Move move : moves) {
			pos.makeMove(move);
			if (!kingInCheck(pos, color)) {
				legalMoves.add(move);
			}			
			pos.undoMove();
		}

		return legalMoves;
	}

	static boolean kingInCheck(Position pos, char color) {
		int king = color == 'w' ? pos.whiteKing : pos.blackKing;
		char attackingColor = color == 'w' ? 'b' : 'w';			
		return underAttack(pos, king, attackingColor);
	}

	static boolean underAttack(Position pos, int square, char attackingColor) {
		int pawnDirStart = pos.toMove == 'w' ? 6 : 4;
		for (int dir = 0; dir < 8; dir++) {
			int dist = MoveData.DistanceToEdge[square][dir];			
			for (int steps = 1; steps <= dist; steps++) {
				int squareIdx = square + steps * MoveData.Offsets[dir];											
				if (Piece.isColor(pos.board.squares[squareIdx], Opposite(attackingColor))) {
					break;
				}
				else if(Piece.isColor(pos.board.squares[squareIdx], attackingColor)) {
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
					else if (steps == 1 && name == Piece.King) {
						return true;
					}
					else {
						break;
					}
				}
			}						
		}

		// knights
		for (int offset : MoveData.KnightOffsets.get(square)) {
			if (Piece.isColor(pos.board.squares[square + offset], attackingColor)) {
				if (Piece.name(pos.board.squares[square + offset]) == Piece.Knight) {
					return true;
				}	
			}			
		}

		// pawns
		int step = attackingColor == 'w' ? MoveData.Down : MoveData.Up;
		int left = attackingColor == 'w' ? 7 : 4;
		int right = attackingColor == 'w' ? 6 : 5;
		if (MoveData.DistanceToEdge[square][left] > 0) {
			if (Piece.isColor(pos.board.squares[square + step - 1], attackingColor)) {
				if (Piece.name(pos.board.squares[square + step - 1]) == Piece.Pawn) {
					return true;
				}	
			}
		}
		if (MoveData.DistanceToEdge[square][right] > 0) {
			if (Piece.isColor(pos.board.squares[square + step + 1], attackingColor)) {
				if (Piece.name(pos.board.squares[square + step + 1]) == Piece.Pawn) {
					return true;
				}	
			}
		}		

		return false;
	}	

	public static int squareToInt(String square) {
		int file = square.charAt(0) - 97;
		int rank = square.charAt(1) - 49;
		return 8 * rank + file;
	}

	public static char Opposite(char color) {
		return color == 'w' ? 'b' : 'w';
	}

	public static boolean gameIsOver(Position position) {
		List<Move> legalMoves = Utils.generateLegalMoves(position);		
		return legalMoves.size() == 0;
	}

	public static int promotionInt(char c, char pieceColor) {
		int color = pieceColor == 'w' ? Piece.White : Piece.Black; 
		if (c == 'q') return Piece.Queen | color;
		if (c == 'b') return Piece.Bishop | color;
		if (c == 'n') return Piece.Knight | color;
		if (c == 'r') return Piece.Rook | color;
		else return 0;
	}

	public static void main(String[] args) {	

		
	}
}