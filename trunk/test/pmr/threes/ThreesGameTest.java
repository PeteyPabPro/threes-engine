package pmr.threes;

import java.util.Arrays;

import org.junit.Test;

import static org.junit.Assert.*;
import static pmr.threes.AbstractMove.*;

public class ThreesGameTest {
	
	
	public void testPickMove(){
		int[][] board = {{12,3,2,1},{48,2,12,3},{96,6,96,2},{3,24,3,3}};
		RegularHoleCard regularHoleCard = new RegularHoleCard(1);
		State start = new State(board, regularHoleCard);		
		int depth = 4;
		ThreesGame game = new ThreesGame(depth,0,1,0);
		long startTime = System.currentTimeMillis();
		Choice bestChoice = game.findBestMove(start);
		double duration = (double)(System.currentTimeMillis()-startTime)/1000;
		System.out.println("Original Board: " + start);
		System.out.println("Choice: " + bestChoice);
		System.out.println("Duration: " +duration + " seconds");
		System.out.println("Max Depth: " + depth);
		System.out.println("Resulting Possible Boards: " + Arrays.deepToString(bestChoice.getMove().findEndStatesForSearch()));		
		
	}
	
	@Test
	public void testPlayGame(){
		int[][] board = {{0, 0, 0, 2},{3, 48,48, 12},{1, 3,384, 0},{2, 0, 0, 3}};
		RegularHoleCard regularHoleCard = new RegularHoleCard(2);
		State state = new State(board, regularHoleCard);	
		int depth = 5;
		ThreesGame game = new ThreesGame(depth,.39,.47,.14);
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
	
	
	public void testMoves(){
		int[][] board = {{0, 0, 0, 3},{2, 2,1, 1},{3, 0,0, 3},{2, 0, 3, 1}};
		RegularHoleCard regularHoleCard = new RegularHoleCard(3);
		State start = new State(board, regularHoleCard);	
		
		System.out.println("Original Board: " + start);
		System.out.println("Resulting Possible Boards Left: " + Arrays.deepToString(new Left(start).findEndStatesForSearch()));
		System.out.println("Resulting Possible Boards Right: " + Arrays.deepToString(new Right(start).findEndStatesForSearch()));
		System.out.println("Resulting Possible Boards Up: " + Arrays.deepToString(new Up(start).findEndStatesForSearch()));
		System.out.println("Resulting Possible Boards Down: " + Arrays.deepToString(new Down(start).findEndStatesForSearch()));
	}
	
	@Test
	public void testCombineLeft(){
		assertEquals(Arrays.toString(new int[]{1,1,3,0}),Arrays.toString(combineLeft(new int[]{1,0,1,3})));
		assertEquals(Arrays.toString(new int[]{3,1,3,0}), Arrays.toString(combineLeft(new int[]{1,2,1,3})));
		assertEquals(Arrays.toString(new int[]{3,3,3,0}),Arrays.toString(combineLeft(new int[]{1,2,3,3})));
		assertEquals(Arrays.toString(new int[]{6,3,1,0}),Arrays.toString(combineLeft(new int[]{3,3,3,1})));
		assertEquals(Arrays.toString(new int[]{12,3,3,0}),Arrays.toString(combineLeft(new int[]{12,3,2,1})));
	}
	
	@Test
	public void testCombineRight(){
		assertEquals(Arrays.toString(new int[]{0,3,1,1}),Arrays.toString(combineRight(new int[]{3,1,0,1})));
		assertEquals(Arrays.toString(new int[]{0,3,1,3}), Arrays.toString(combineRight(new int[]{3,1,2,1})));
		assertEquals(Arrays.toString(new int[]{0,3,3,3}),Arrays.toString(combineRight(new int[]{3,3,2,1})));
		assertEquals(Arrays.toString(new int[]{0,1,3,6}),Arrays.toString(combineRight(new int[]{1,3,3,3})));
		assertEquals(Arrays.toString(new int[]{0,3,24,6}),Arrays.toString(combineRight(new int[]{3,24,3,3})));
	}
	
	
	public void testBoardScore(){
		System.out.println("\nBoard Test");
		int[][] boardArray = {{1,1,3,2},{1,48,6,3},{6,1,12,2},{2,24,6,3}};
		Board board = new Board(boardArray);
		double score = ThreesGame.getBoardScore(board);
		System.out.println("Board: " + board + " Score: " + score);
		assertEquals(387, score,.0001);
	}
	

}
