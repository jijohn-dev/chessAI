import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;

public class Game {

	private class BoardPanel extends JPanel {
		private static final long serialVersionUID = 1;
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					Color color = (i + j) % 2 == 0 ? Color.decode("#eff7d3") : Color.decode("#70a24c");
					g.setColor(color);
					g.fillRect(100 * j, 100 * i, 100, 100);
				}
			}

			// draw pieces
			for (int i = 0; i < 64; i++) {
				if (board.at(i) != Piece.Empty) {
					int pieceIdx = (Piece.name(board.at(i)) - 1);
					if (pieceIdx == 4) {
						pieceIdx = 5;
					}
					else if (pieceIdx == 5) {
						pieceIdx = 4;
					}

					int sprX = 100 * pieceIdx;
					int sprY = Piece.isColor(board.at(i), 'w') ? 0 : 100; 				
					
					Image sprite = spritesheet.getSubimage(sprX, sprY, 100, 100);
					g.drawImage(sprite, 100 * (i % 8), 700 - 100 * (i / 8), this);
				}			
			}
		}
	}

	private final JFrame gameFrame;
	private static final Dimension OUTER_FRAME_DIMENSION = new Dimension(900, 900);

	private BufferedImage spritesheet;

	private Position board;
	private final Engine engine;

	private final Scanner input = new Scanner(System.in);

	public Game(int depth) {
		board = new Position();
		engine = new Engine(depth);		
		
		try {
//			System.out.println("Working Directory = " + System.getProperty("user.dir"));
			spritesheet = ImageIO.read(new File(("../img/spritesheet.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// GUI
		gameFrame = new JFrame("chess");
		//gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		BoardPanel boardPanel = new BoardPanel();
		gameFrame.getContentPane().add(boardPanel);

		gameFrame.pack();
		gameFrame.setSize(OUTER_FRAME_DIMENSION);
		gameFrame.setVisible(true);
	}

	// start game with custom position
	public Game(int depth, Position pos) {
		this(depth);
		board = pos;
	}

	public void play() {
		board.printBoard();
		// choose color
		System.out.print("Choose color [white/black]: ");
		String color = input.next();
		char player = color.equals("white") ? 'w' : 'b';

		Openings book = new Openings();
		List<String> moves = new ArrayList<>();

		// game loop
		while (!Utils.gameIsOver(board)) {
			if (board.toMove == player) {
				System.out.print("Move: ");
				String cmd = input.next();
				if (cmd.equals("quit")) {
					gameFrame.dispose();
					break;
				}

				if (cmd.length() < 4 || cmd.length() > 5) {
					System.out.println("Invalid move");
					continue;
				}

				Move move = new Move(cmd);
				List<Move> legalMoves = Utils.generateLegalMoves(board);
				boolean legal = false;
				for (Move legalMove : legalMoves) {
					if (move.toString().equals(legalMove.toString())) {
						legal = true;
					}
				}
				if (legal) {
					board.makeMove(move);
					moves.add(move.toString());
					board.printBoard();					
					gameFrame.repaint();
				}
				else {
					System.out.println("illegal move ");
				}			
			}
			else {
				System.out.println("Computer moving");

				Move move;
				if (board.moveCount < 5) {
					String moveString = book.getMove(moves);
					if (moveString.equals("")) {
						move = engine.computerMove(board);
					}
					else {
						move = new Move(book.getMove(moves));
					}
				}
				else {
					move = engine.computerMove(board);
				}

				board.makeMove(move);
				moves.add(move.toString());
				System.out.println(move);
				board.printBoard();
				gameFrame.repaint();
			}
		}

		if (board.checkmate()) {
			System.out.println("Checkmate");
		}
		else if (board.stalemate()) {
			System.out.println("Stalemate");
		}

		System.out.print("--Press enter to continue--");
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		gameFrame.dispose();

		//input.close();
	}
}