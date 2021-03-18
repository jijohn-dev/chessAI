import chess
import math

def minimax(position, depth, alpha, beta, white):
	if depth == 0 or position.is_game_over():
		return staticEval(position), 0

	bestMove = None

	if white:
		maxEval = -math.inf
		moves = position.legal_moves
		for move in moves:
			position.push(move)
			childEval, best = minimax(position, depth - 1, alpha, beta, False)
			position.pop()
			if (childEval > maxEval):
				maxEval = childEval
				bestMove = move
			alpha = max(alpha, maxEval)
			if beta <= alpha:
				break			
		return maxEval, bestMove
	else:
		minEval = math.inf
		moves = position.legal_moves
		for move in moves:
			position.push(move)
			childEval, best = minimax(position, depth - 1, alpha, beta, True)
			position.pop()
			if childEval < minEval:
				minEval = childEval
				bestMove = move			
			beta = min(beta, minEval)
			if beta <= alpha:
				break			
		return minEval, bestMove

def staticEval(position):
	# mate
	if position.is_checkmate():
		if position.turn == chess.WHITE:
			return -math.inf
		else:
			return math.inf

	if position.is_stalemate():
		return 0
	
	# material balance
	total = 0
	pieces = [chess.PAWN, chess.KNIGHT, chess.BISHOP, chess.ROOK, chess.QUEEN]
	values = [100, 300, 300, 500, 900]

	for piece, value in zip(pieces, values):
		total += len(position.pieces(piece, chess.WHITE)) * value
		total -= len(position.pieces(piece, chess.BLACK)) * value

	return total