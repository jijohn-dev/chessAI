import chess

board = chess.Board()

print(len(board.pieces(chess.PAWN, chess.WHITE)))
print(board.legal_moves)