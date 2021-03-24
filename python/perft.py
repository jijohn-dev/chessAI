from engine.engine import minimax, minimaxAB
import chess
import time
import math

maxDepth = 2

print("Evaluating starting position depth =", maxDepth)
board= chess.Board()

startTime = time.time()
minimax(board, maxDepth, True, 0)
endTime = time.time()

print("minimax:", math.floor((endTime - startTime) * 1000), "ms")

startTime = time.time()
minimaxAB(board, maxDepth, -math.inf, math.inf, True, 0)
endTime = time.time()

print("minimax with pruning:", math.floor((endTime - startTime) * 1000), "ms")

# test position 5
maxDepth = 3
print("Evaluating test position 5 depth =", maxDepth)
board = chess.Board("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8")

startTime = time.time()
minimax(board, maxDepth, True, 0)
endTime = time.time()

print("minimax:", math.floor((endTime - startTime) * 1000), "ms")

startTime = time.time()
minimaxAB(board, maxDepth, -math.inf, math.inf, True, 0)
endTime = time.time()

print("minimax with pruning:", math.floor((endTime - startTime) * 1000), "ms")