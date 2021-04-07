import sys
import chess

def generate(position, depth, moves, outputFile):
	if depth == 1:
		total = 0
		moveList = ""
		for ply in position.legal_moves:
			total += 1
			moveList += ply.uci() + " "
		output = moves + str(total) + " " + moveList + "\n"
		f = open(outputFile, "a+")
		f.write(output)
		f.close
		return
	else:
		for ply in position.legal_moves:
			position.push(ply)
			generate(position, depth - 1, moves + ply.uci() + " ", outputFile)
			position.pop()

def generateAllPlys(position, maxDepth, outputFile):
	for ply in range(1, maxDepth+1):
		generate(position, ply, "", outputFile)

# test positions
f = open("../testData/testPositions.txt", "r")
positions = f.read().splitlines()

start = int(sys.argv[1])

for index, position in enumerate(positions):
	if (index >= start - 1):
		print(position)
		fileName = "../testData/pos" + str(index + 1) + ".txt"	

		# clear output file
		f = open(fileName, "w+")
		f.truncate(0)
		f.close()

		generateAllPlys(chess.Board(position), 4, fileName)
