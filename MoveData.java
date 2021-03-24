import java.util.*;

public class MoveData {
	public static final int Up = 8;
	public static final int Down = -8;
	public static final int Left = -1;
	public static final int Right = 1;

	public static final int UpLeft = 7;
	public static final int UpRight = 9;
	public static final int DownLeft = -9;
	public static final int DownRight = -7;

	public static final int[] Offsets = {8, -8, -1, 1, 7, 9, -7, -9};
	public static final List<List<Integer>> KnightOffsets = new ArrayList<List<Integer>>();
	public static final int[][] DistanceToEdge = new int[64][8];

	// {6, 15, 17, 10, -6, -15, -17, -10}

	static {
		for (int rank = 0; rank < 8; rank++) {
			for (int file = 0; file < 8; file++) {
				int up = 7 - rank;
				int down = rank;
				int left = file;
				int right = 7 - file;

				int square = 8 * rank + file;

				DistanceToEdge[square][0] = up;
				DistanceToEdge[square][1] = down;
				DistanceToEdge[square][2] = left;
				DistanceToEdge[square][3] = right;
				DistanceToEdge[square][4] = Math.min(up, left);
				DistanceToEdge[square][5] = Math.min(up, right);
				DistanceToEdge[square][6] = Math.min(down, right);				
				DistanceToEdge[square][7] = Math.min(down, left);

				List<Integer> offsets = new ArrayList<Integer>();

				if (up > 1) {
					if (right > 0) {
						offsets.add(17);
					}
					if (left > 0) {
						offsets.add(15);
					}
				}
				if (right > 1) {
					if (up > 0) {
						offsets.add(10);
					}
					if (down > 0) {
						offsets.add(-6);
					}
				}
				if (down > 1) {
					if (right > 0) {
						offsets.add(-15);
					}
					if (left > 0) {
						offsets.add(-17);
					}
				}
				if (left> 1) {
					if (up > 0) {
						offsets.add(6);
					}
					if (down > 0) {
						offsets.add(-10);
					}
				}

				KnightOffsets.add(offsets);
			}
		}
	}

	public static void main(String[] args) {
		System.out.println(KnightOffsets.get(13));
	}
}