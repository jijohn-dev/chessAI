# Overview  
Chess AI project

python-chess used as reference for testing correctness of legal move generation.
  
Python implementation uses python-chess to handle all of the chess logic.  
Includes basic pygame gui for playing against the engine.  

# Java Engine Commands
play - start a game from the current position against the engine  
pos - load position from FEN string  
eval - print engine evaluation of current position  
test - run move generation and evaluation performance tests

# Test Data
generateData.py reads FEN positions from /testData/testPositions.txt and generates pos[#].txt for each position containing every subset of moves and the resulting
set of legal moves up to a default depth of 4 ply. Moves are in UCI format.  

Format:  
[moves played] [# legal moves] [list of legal moves]  

Example:  
rnbqkbnr/pppppppp/8/8/8/8/PPPPPPP/RNBQKBNR w KQkq - 1 0  
20 a2a3 b2b3 c2c3 d2d3 e2d3 ...  
a2a3 20 a7a6 b7b6 c7c6 ...  
b2b3 20 a7a6 b7b6 c7c6 ...  
