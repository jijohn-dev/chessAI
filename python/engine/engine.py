import chess
import math

def minimax(position, depth, white_to_move, ply):
	if depth == 0 or position.is_game_over():
		return staticEval(position, ply), None

	bestMove = None

	if white_to_move:
		maxEval = -math.inf
		moves = position.legal_moves
		for move in moves:			
			position.push(move)
			childEval, best = minimax(position, depth - 1, False, ply + 1)
			position.pop()
			if childEval > maxEval or childEval == -math.inf:
				maxEval = childEval
				bestMove = move					
		return maxEval, bestMove
	else:
		minEval = math.inf
		moves = position.legal_moves
		for move in moves:			
			position.push(move)
			childEval, best = minimax(position, depth - 1, True, ply + 1)
			position.pop()
			if childEval < minEval or childEval == math.inf:
				minEval = childEval
				bestMove = move	
		return minEval, bestMove

def minimaxAB(position, depth, alpha, beta, white_to_move, ply):
	if depth == 0 or position.is_game_over():
		return staticEval(position, ply), None

	bestMove = None

	if white_to_move:
		maxEval = -math.inf
		moves = position.legal_moves
		for move in moves:			
			position.push(move)
			childEval, best = minimaxAB(position, depth - 1, alpha, beta, False, ply + 1)
			position.pop()
			if childEval > maxEval or childEval == -math.inf:
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
			childEval, best = minimaxAB(position, depth - 1, alpha, beta, True, ply + 1)
			position.pop()
			if childEval < minEval or childEval == math.inf:
				minEval = childEval
				bestMove = move			
			beta = min(beta, minEval)
			if beta <= alpha:
				break			
		return minEval, bestMove

def staticEval(position, ply):
	total = 0

	# mate
	if position.is_checkmate():
		if position.turn == chess.WHITE:
			return -10000 + 500 * ply
		else:
			return 10000 - 500 * ply

	if position.is_stalemate():
		return 0
	
	# material balance	
	pieces = [chess.PAWN, chess.KNIGHT, chess.BISHOP, chess.ROOK, chess.QUEEN]
	values = [100, 300, 300, 500, 900]

	for piece, value in zip(pieces, values):
		total += len(position.pieces(piece, chess.WHITE)) * value
		total -= len(position.pieces(piece, chess.BLACK)) * value

	# get number of pieces (excluding king)
	# enemy_color = chess.BLACK if position.turn == chess.WHITE else chess.WHITE	
	# num_pieces = count_pieces(position, position.turn)
	# num_enemy_pieces = count_pieces(position, enemy_color)

	# king distance to edge and proximity
	# if num_pieces == 0:
	# 	king = position.king(position.turn)
	# 	dist = dist_to_edge(king)
	# 	prox = distance(king, position.king(enemy_color))
	# 	if position.turn == chess.WHITE:
	# 		total += dist * 10
	# 		total += prox * 10
	# 	else :
	# 		total += -dist * 10
	# 		total += -prox * 10

	# # enemy king distance to edge and proximity
	# if num_enemy_pieces == 0:
	# 	enemy_king = position.king(enemy_color)
	# 	dist = dist_to_edge(enemy_king)
	# 	prox = distance(enemy_king, position.king(position.turn))
	# 	if position.turn == chess.WHITE:
	# 		total += -dist * 10
	# 		total += -prox * 10
	# 	else :
	# 		total += dist * 10
	# 		total += prox * 10

	return total

def dist_to_edge(square):
	r = math.floor(square / 8)
	f = square % 8	
	return min(r, 7-r, f, 7-f)

def distance(a, b):
	ra = math.floor(a / 8)
	fa = a % 8
	rb = math.floor(b / 8)
	fb = b % 8
	return min(abs(ra - rb), abs(fa - fb))


def count_pieces(position, color):	
	total = len(position.pieces(chess.PAWN, color))
	total += len(position.pieces(chess.BISHOP, color))
	total += len(position.pieces(chess.KNIGHT, color))
	total += len(position.pieces(chess.ROOK, color))
	total += len(position.pieces(chess.QUEEN, color))	
	return total