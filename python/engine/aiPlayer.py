from .engine import minimax
import math
import chess

def computerMove(board, maxDepth):
	# if position exists in opening database
	# play random move from database

	# calculate best move
	eval, bestMove = minimax(board, maxDepth, -math.inf, math.inf, board.turn == chess.WHITE)
	return bestMove