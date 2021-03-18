import chess

board = chess.Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 1 0")

firstMoves = board.legal_moves

for ply1 in firstMoves:
	print(ply1)
	board.push(ply1)
	secondMoves = board.legal_moves
	for ply2 in secondMoves:
		print(ply2)
		board.push(ply2)
		print(sum(1 for ply in board.legal_moves))
		board.pop()
	board.pop()