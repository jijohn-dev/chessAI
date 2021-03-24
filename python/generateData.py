import chess

board = chess.Board("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8")

# 1 ply
moveTotal = 0
moveList = ""
for ply in board.legal_moves:
	moveTotal += 1
	moveList += ply.uci() + " "

print(moveTotal, moveList)

# 2 ply
for ply1 in board.legal_moves:
	moveTotal = 0
	moveList = ""
	board.push(ply1)	
	for ply2 in board.legal_moves:
		moveTotal += 1
		moveList += ply2.uci() + " "	
	board.pop()	 
	print(ply1.uci(), moveTotal, moveList)

# 3 ply
for ply1 in board.legal_moves:	
	board.push(ply1)	
	for ply2 in board.legal_moves:
		moveTotal = 0
		moveList = ""
		board.push(ply2)
		for ply3 in board.legal_moves:
			moveTotal += 1
			moveList += ply3.uci() + " "
		print(ply1.uci(), ply2.uci(), moveTotal, moveList)
		board.pop()
	board.pop()	 

# 4 ply
for ply1 in board.legal_moves:	
	board.push(ply1)	
	for ply2 in board.legal_moves:		
		board.push(ply2)
		for ply3 in board.legal_moves:
			moveTotal = 0
			moveList = ""
			board.push(ply3)
			for ply4 in board.legal_moves:
				moveTotal += 1
				moveList += ply4.uci() + " "
			print(ply1.uci(), ply2.uci(), ply3.uci(), moveTotal, moveList)
			board.pop()
		board.pop()
	board.pop()	

