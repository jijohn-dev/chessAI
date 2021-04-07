import chess
import math

class Engine:
	pieces = [None, chess.PAWN, chess.KNIGHT, chess.BISHOP, chess.ROOK, chess.QUEEN, chess.KING]
	values = [0, 100, 300, 300, 500, 900, 0]
	
	def __init__(self):
		self.num_positions = 0
		self.transpositions = 0
		self.zobrist_hash = {}

	def reset(self):
		self.num_positions = 0

	def minimax(self, position, depth, white_to_move, ply):
		if depth == 0 or position.is_game_over():
			return staticEval(position, ply), None

		bestMove = None

		if white_to_move:
			maxEval = -math.inf
			moves = position.legal_moves
			for move in moves:
				self.num_positions += 1	
				position.push(move)
				childEval, best = self.minimax(position, depth - 1, False, ply + 1)
				position.pop()
				if childEval > maxEval or childEval == -math.inf:
					maxEval = childEval
					bestMove = move					
			return maxEval, bestMove
		else:
			minEval = math.inf
			moves = position.legal_moves
			for move in moves:
				self.num_positions += 1	
				position.push(move)
				childEval, best = self.minimax(position, depth - 1, True, ply + 1)
				position.pop()
				if childEval < minEval or childEval == math.inf:
					minEval = childEval
					bestMove = move	
			return minEval, bestMove

	def minimaxAB(self, position, depth, alpha, beta, white_to_move, ply):
		if depth == 0 or position.is_game_over():
			return staticEval(position, ply), None

		bestMove = None

		if white_to_move:
			maxEval = -math.inf
			moves = position.legal_moves
			for move in moves:		
				self.num_positions += 1	
				position.push(move)
				childEval, best = self.minimaxAB(position, depth - 1, alpha, beta, False, ply + 1)
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
				self.num_positions += 1	
				position.push(move)
				childEval, best = self.minimaxAB(position, depth - 1, alpha, beta, True, ply + 1)
				position.pop()
				if childEval < minEval or childEval == math.inf:
					minEval = childEval
					bestMove = move			
				beta = min(beta, minEval)
				if beta <= alpha:
					break			
			return minEval, bestMove

	# minimax with alpha beta pruning and move ordering
	def minimaxABO(self, position, depth, alpha, beta, white_to_move, ply):
		if depth == 0 or position.is_game_over():
			return staticEval(position, ply), None

		bestMove = None

		# create list of legal moves sorted by prospective evaluation
		moves = []
		for move in position.legal_moves:
			move.score = score_move(position, move)
			moves.append(move)

		moves.sort(key=lambda move : move.score, reverse=True)

		if white_to_move:
			maxEval = -math.inf		
			for move in moves:
				self.num_positions += 1				
				position.push(move)
				childEval, best = self.minimaxABO(position, depth - 1, alpha, beta, False, ply + 1)
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
			for move in moves:	
				self.num_positions += 1			
				position.push(move)
				childEval, best = self.minimaxABO(position, depth - 1, alpha, beta, True, ply + 1)
				position.pop()
				if childEval < minEval or childEval == math.inf:
					minEval = childEval
					bestMove = move			
				beta = min(beta, minEval)
				if beta <= alpha:
					break			
			return minEval, bestMove	

	# minimax with alpha beta pruning, move ordering and zobrist hashing
	def zobrist_ABO(self, position, depth, alpha, beta, white_to_move, ply):
		if depth == 0 or position.is_game_over():
			# check hash table for eval
			transposition = position.fen()[0:-3]
			if transposition in self.zobrist_hash:
				self.transpositions += 1
				return self.zobrist_hash[transposition], None
			static = staticEval(position, ply)
			self.zobrist_hash[transposition] = static			
			return static, None			

		bestMove = None

		# create list of legal moves sorted by prospective evaluation
		moves = []
		for move in position.legal_moves:
			move.score = score_move(position, move)
			moves.append(move)

		moves.sort(key=lambda move : move.score, reverse=True)

		if white_to_move:
			maxEval = -math.inf		
			for move in moves:
				self.num_positions += 1				
				position.push(move)
				childEval, best = self.zobrist_ABO(position, depth - 1, alpha, beta, False, ply + 1)
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
			for move in moves:	
				self.num_positions += 1			
				position.push(move)
				childEval, best = self.zobrist_ABO(position, depth - 1, alpha, beta, True, ply + 1)
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
	for i in range(1, 6):
		total += len(position.pieces(Engine.pieces[i], chess.WHITE)) * Engine.values[i]
		total -= len(position.pieces(Engine.pieces[i], chess.BLACK)) * Engine.values[i]

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

# return list of moves sorted by prospective evaluation
def score_move(board, move):	
	score = 0
	piece_type = board.piece_type_at(move.from_square)
	capture_piece_type = board.piece_type_at(move.to_square)

	# capturing a piece with a piece of lower value
	if capture_piece_type != None:
		score += Engine.values[capture_piece_type] - Engine.values[piece_type]

	# promoting a pawn
	if move.promotion != None:
		score += Engine.values[move.promotion]

	# moving a piece to where it can be captured by a pawn
	if board.is_attacked_by(not board.color_at(move.from_square), move.to_square):
		attackers = board.attackers(not board.color_at(move.from_square), move.to_square)
		for square in attackers:
			if board.piece_type_at(square) == chess.PAWN:
				score -= Engine.values[piece_type]
	return score

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