import chess

# create openings database
def generate_db():
	db_board = chess.Board()	

	openings = [
		["e2e4", "e7e5", "g1f3", "b8c6", "f1c4"], # italian
		["e2e4", "c7c6", "d2d4", "d7d5"], # caro-kann
		["d2d4", "d7d5", "c1f4", "g8f6", "g1f3"] # london
	]

	db = {}

	for opening in openings:
		for move in opening:
			pos = db_board.fen()
			if pos in db:
				if move not in db[pos]:
					db[pos].append(move)
			else:
				db[pos] = [move]
			db_board.push(chess.Move.from_uci(move))
		db_board = chess.Board()

	return db