from engine.engine import Engine
import chess
import time
import math
import sys

maxDepth = 5

if len(sys.argv) > 1:
	maxDepth = int(sys.argv[1])

engine = Engine()

# test position 5
print("Evaluating test position 5 depth =", maxDepth)
pos5 = "rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8"
board = chess.Board(pos5)

# pure minimax
if maxDepth < 4:	
	startTime = time.time()
	engine.reset()
	engine.minimax(board, maxDepth, True, 0)
	endTime = time.time()

	print("minimax:", math.floor((endTime - startTime) * 1000), "ms", "Positions evaluated:", engine.num_positions)

# minimax with pruning
if maxDepth < 6:	
	startTime = time.time()
	engine.reset()
	engine.minimaxAB(board, maxDepth, -math.inf, math.inf, True, 0)
	endTime = time.time()

	print("minimax with pruning:", math.floor((endTime - startTime) * 1000), "ms", "Positions evaluated:", engine.num_positions)

# minimax with pruning and move ordering
startTime = time.time()
engine.reset()
engine.minimaxABO(board, maxDepth, -math.inf, math.inf, True, 0)
endTime = time.time()

print("minimax with pruning and ordering:", math.floor((endTime - startTime) * 1000), "ms", "Positions evaluated:", engine.num_positions)

# hashing
startTime = time.time()
engine.reset()
engine.zobrist_ABO(board, maxDepth, -math.inf, math.inf, True, 0)
endTime = time.time()

print("minimax with hashing:", math.floor((endTime - startTime) * 1000), "ms", "Positions evaluated:", engine.num_positions, "transpositions:", engine.transpositions)