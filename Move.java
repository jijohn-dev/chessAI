public class Move {
	public final int Start;
	public final int Target;

	public Move(int start, int target) {
		this.Start = start;
		this.Target = target;
	}

	public Move(String move) {
		int startFile = move.charAt(0) - 97;
		int startRank = move.charAt(1) - 49;
		int targetFile = move.charAt(2) - 97;
		int targetRank = move.charAt(3) - 49;
		Start = 8 * startRank + startFile;
		Target = 8 * targetRank + targetFile;
	}

	public String toSquare() {
		int startFileAscii = 97 + (Start % 8);
		char startFile = (char) startFileAscii;
		int startRank = Start / 8 + 1;

		int targetFileAscii = 97 + (Target % 8);
		char targetFile = (char) targetFileAscii;
		int targetRank = Target / 8 + 1;

		StringBuilder str = new StringBuilder();
		str.append(startFile);
		str.append(startRank);
		str.append(" ");
		str.append(targetFile);
		str.append(targetRank);

		return str.toString();
	}
}