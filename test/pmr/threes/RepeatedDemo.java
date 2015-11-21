package pmr.threes;

public class RepeatedDemo {
	
	public static void main(String[] args) {
		double bestScore = Double.MIN_VALUE;
		while (true) {
			State currState = Demo.doIter(args);
			double score = ThreesGame.getBoardScore(currState.getBoard());
			if (score > bestScore) {
				System.out.println("New high score found: " + score);
				System.out.println("Board\n: " + currState.getBoard());
				bestScore = score;
			}
		}
	}

}
