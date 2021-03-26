public class Move {
	public final int Start;
	public final int Target;
	public final char promotionChoice;

	public Move(int start, int target) {
		this.Start = start;
		this.Target = target;
		promotionChoice = '-';
	}

	public Move(int start, int target, char promotionChoice) {
		this.Start = start;
		this.Target = target;
		this.promotionChoice = promotionChoice;
	}

	public Move() {
		Start = -1;
		Target = -1;
		promotionChoice = '-';
	}

	public Move(String move) {
		int startFile = move.charAt(0) - 97;
		int startRank = move.charAt(1) - 49;
		int targetFile = move.charAt(2) - 97;
		int targetRank = move.charAt(3) - 49;
		Start = 8 * startRank + startFile;
		Target = 8 * targetRank + targetFile;

		// promotion [frfrp]
		if (move.length() == 5) {
			promotionChoice = move.charAt(4);
		}
		else {
			promotionChoice = '-';
		}
	}

	public String toString() {
		int startFileAscii = 97 + (Start % 8);
		char startFile = (char) startFileAscii;
		int startRank = Start / 8 + 1;

		int targetFileAscii = 97 + (Target % 8);
		char targetFile = (char) targetFileAscii;
		int targetRank = Target / 8 + 1;

		StringBuilder str = new StringBuilder();
		str.append(startFile);
		str.append(startRank);		
		str.append(targetFile);
		str.append(targetRank);

		// promotion choice
		if (promotionChoice != '-') {
			str.append(promotionChoice);
		}

		return str.toString();
	}
}