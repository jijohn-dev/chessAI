import chess
import pygame
import math
import os

from engine.aiPlayer import computerMove
from engine.db import generate_db

# load spritesheet
ss = pygame.image.load(os.path.join("img", "spritesheet.png"))

# set sprite positions
sprite_x = {chess.PAWN: 0, chess.BISHOP: 100, chess.KNIGHT: 200, chess.ROOK: 300, chess.KING: 400, chess.QUEEN: 500}
sprite_y = {chess.WHITE: 0, chess.BLACK: 100}

pieces = [chess.PAWN, chess.BISHOP, chess.KNIGHT, chess.ROOK, chess.KING, chess.QUEEN]
colors = [chess.WHITE, chess.BLACK]

# board colors
LIGHT = (239, 247, 211)
DARK = (112, 162, 76)
HIGHLIGHTED = (151, 45, 45)

# font
pygame.font.init()
mainFont = pygame.font.Font(os.path.join("fonts", "slkscr.ttf"), 24)

# test positions
test1 = "8/8/8/8/KQ6/8/k7/8 b - - 0 50"
test_checkmate = "4K3/8/8/q7/k7/8/8/8 b - - 0 50"

class State:
	def __init__(self):
		self.board = chess.Board(test_checkmate)
		self.highlighted_square = -1
		self.piece_selected = False
		self.promotion_menu_open = False
		self.promotion_move = ""

def squareToRect(square):
	return pygame.Rect(100 * (square % 8), 700 - 100 * math.floor(square / 8), 100, 100)

def coordsToSquare(x, y):
	return math.floor(x / 100) + 8 * (7 - math.floor(y / 100))	


def redraw_gameWindow(state):
	global win

	# draw board		
	for f in range(8):
		for r in range(8):
			color = LIGHT
			if ((f + r) % 2 == 1):
				color = DARK			
			pygame.draw.rect(win, color, (100*f, 100*r, 100, 100))	

	# highlighted square
	if (state.highlighted_square > -1):
		pygame.draw.rect(win, HIGHLIGHTED, squareToRect(state.highlighted_square))

	# draw pieces
	for color in colors:
		for piece in pieces:
			squares = state.board.pieces(piece, color)
			for square in squares:
				rect = pygame.Rect(sprite_x[piece], sprite_y[color], 100, 100)
				win.blit(ss, (100 * (square % 8), 700 - 100 * math.floor(square / 8)), rect)

	# draw promotion menu
	if state.promotion_menu_open:
		win.blit(ss, (700, 0), pygame.Rect(sprite_x[chess.QUEEN], sprite_y[chess.WHITE], 100, 100))
		win.blit(ss, (700, 100), pygame.Rect(sprite_x[chess.BISHOP], sprite_y[chess.WHITE], 100, 100))
		win.blit(ss, (700, 200), pygame.Rect(sprite_x[chess.KNIGHT], sprite_y[chess.WHITE], 100, 100))
		win.blit(ss, (700, 300), pygame.Rect(sprite_x[chess.ROOK], sprite_y[chess.WHITE], 100, 100))

	# draw text
	msg = mainFont.render("chess", True, (255, 255, 255))
	win.blit(msg, (810, 10))
	
	pygame.display.update()

def main():

	clock = pygame.time.Clock()
	run = True

	# initialize chess game
	state = State()	

	# generate openings database
	db = generate_db()

	while run:
		clock.tick(30)
		redraw_gameWindow(state)	

		# computer move
		if state.board.turn == chess.BLACK and not state.board.is_game_over():
			print("computer moving")
			move = computerMove(state.board, 5, db)			
			if move == None:
				print("no move returned")
			state.board.push(move)	
			state.highlighted_square = move.to_square
			if state.board.is_checkmate():
				print("checkmate")
			if state.board.is_stalemate():
				print("stalemate")			

		for event in pygame.event.get():
			if event.type == pygame.QUIT:
				run = False
				pygame.quit()

			if event.type == pygame.MOUSEMOTION:
				pass

			if event.type == pygame.MOUSEBUTTONDOWN:
				x, y = event.pos	

				# handle promotion menu choice
				if state.promotion_menu_open:
					if x > 700 and y < 400:
						state.promotion_menu_open = False
						choices = ["q", "b", "n", "r"]
						choice = choices[math.floor(y / 100)]
						move = state.promotion_move + choice
						if state.board.is_legal(chess.Move.from_uci(move)) and state.board.turn == chess.WHITE:			 
								state.board.push(chess.Move.from_uci(move))
								if state.board.is_checkmate():
									print("checkmate")
								if state.board.is_stalemate():
									print("stalemate")
								state.piece_selected = False

				if x < 800:	
					clicked_square = coordsToSquare(x, y)

					if state.piece_selected:					
						if clicked_square != state.highlighted_square:
							move = chess.square_name(state.highlighted_square) + chess.square_name(clicked_square)
							print(move)
							# promotion
							if state.board.piece_at(state.highlighted_square).piece_type == chess.PAWN and clicked_square > 55:
								# show promotion menu
								state.promotion_menu_open = True
								state.promotion_move = move
							# play move if legal
							if state.board.is_legal(chess.Move.from_uci(move)) and state.board.turn == chess.WHITE:			 
								state.board.push(chess.Move.from_uci(move))
								if state.board.is_checkmate():
									print("checkmate")
								if state.board.is_stalemate():
									print("stalemate")
								state.piece_selected = False

							elif state.board.piece_at(clicked_square) != None:								
								state.piece_selected = True	
							else:
								state.piece_selected = False					

					elif state.board.piece_at(clicked_square) != None:
						state.piece_selected = True

					else:
						state.piece_selected = False

					state.highlighted_square = clicked_square

width = 1000
height = 800
win = pygame.display.set_mode((width, height))
pygame.display.set_caption("Chess")
main()