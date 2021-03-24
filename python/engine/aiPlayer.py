from .engine import minimax, minimaxAB, count_pieces
import math
import chess
import random

def computerMove(board, maxDepth, db):
	# if position exists in opening database
	# play random move from database
	if board.fullmove_number < 10:
		position = board.fen()
		if position in db:
			rand = (random.randrange(0, len(db[position])))
			print(len(db[position]) - 1)
			print(rand)
			return chess.Move.from_uci(db[position][rand])	

	# calculate best move
	eval, bestMove = minimaxAB(board, maxDepth, -math.inf, math.inf, board.turn == chess.WHITE, 0)
	print(eval)
	return bestMove