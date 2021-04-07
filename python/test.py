import chess

board = chess.Board()

print(len(board.pieces(chess.PAWN, chess.WHITE)))
print(board.legal_moves)

print(board.fen())
print(board.fen()[0:-3])