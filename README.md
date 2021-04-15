# Overview  
An ongoing journey down the rabbit hole of chess programming.  
  
Python implementation uses python-chess to handle all of the chess logic.  
Includes basic pygame gui for playing against the engine.  
  
Java implementation includes chess logic.
Board graphics are displayed when playing but moves must be entered in the command line.   
Currently able to generate correct number of positions from the starting position at a depth of 6 ply (119,060,324)
and exactly matches positions reached from test position 5 on the chess programming wiki at a depth of 5 ply (89,941,194 positions).  
I'm fairly confident in the current level of correctness.  

# Future Improvements
- Iterative deepening  
- More detailed static evaluation  
- Click and drag functionality for gui  
- Time controls  
- More efficient Java chess logic


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
