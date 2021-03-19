from .engine import minimax
import math
import chess
import random

def computerMove(board, maxDepth, db):
	# if position exists in opening database
	# play random move from database
	if board.fullmove_number < 10:
		position = board.fen()
		if position in db:
			rand = (random.randint(0, len(db[position]) - 1))
			return chess.Move.from_uci(db[position][rand])

	# calculate best move
	eval, bestMove = minimax(board, maxDepth, -math.inf, math.inf, board.turn == chess.WHITE)
	return bestMove