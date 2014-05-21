/* Copyright (c) 2014 Peter Rimshnick
 * See the file license.txt for copying permission.
 */
package pmr.threes;

public class Demo {
	
	public static void main (String[] args){
		int depth = (args.length==0?5:Integer.parseInt(args[0]));
		int[][] board = {{0,1,3,3},{3,2,0,1},{2,0,0,1},{0,0,0,1}};
		RegularHoleCard holeCard = new RegularHoleCard(2);
		State state = new State(board, holeCard);	
		ThreesGame game = new ThreesGame(depth,.1,.9,0);
		Choice currChoice = null;
		int step = 0;
		while(true) {
			long start = System.currentTimeMillis();
			currChoice = game.findBestMove(state);
			if (currChoice.isEmptyChoice()) break;
			state = new State(ThreesGame.pickState(currChoice.getMove().findEndStatesForSim()));
			double time = (double)(System.currentTimeMillis()-start)/1000;
			System.out.println("Step: " + step++);
			System.out.println("Last Move: " + currChoice.getMove() + " ("+String.format("%4.2f", time)+" sec)");
			System.out.println("Current state: " + state);
		}		
		System.out.println("Final state: " + state + "Score: " + ThreesGame.getBoardScore(state.getBoard()));
		
	}

}
