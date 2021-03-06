/* Copyright (c) 2014 Peter Rimshnick
 * See the file license.txt for copying permission.
 */
package pmr.threes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Represents up move
 * @author Peter Rimshnick
 *
 */
public class Up extends AbstractMove {

	public Up(State previousState) {
		super(previousState);		
	}

	@Override
	protected List<State> getStep1States(State startState) {
		//Update board. Up = transpose(right)
		int[][] startBoard = transpose(startState.getBoard().getBoardArray());
		int[][] newBoard = new int[startBoard.length][startBoard.length];
		List<Integer> modifiedRows = new ArrayList<Integer>();
		for (int i = 0; i<startBoard.length; i++){
			int[] row = startBoard[i];
			int[] newRow = combineLeft(row);
			if (!Arrays.equals(row, newRow)) modifiedRows.add(i);
			copyInto(newRow, newBoard[i]);
		}		
		//if modified rows is empty, don't branch
		if (modifiedRows.isEmpty()) return new ArrayList<State>(Arrays.asList(new State[]{startState}));

		List<State> step1States = new ArrayList<State>(); //temporary states
		HoleCard holeCard = startState.getHoleCard();
		List<Integer> holeValues = new ArrayList<Integer>();
		if (holeCard instanceof RegularHoleCard){
			holeValues.add(((RegularHoleCard)holeCard).value);
		}
		else {
			holeValues.addAll(((AmbiguousHoleCard)holeCard).getPossibleAdditions());
		}
		for (int value: holeValues) {
			for (int i: modifiedRows){
				int[][] modBoard = copyBoard(newBoard);
				modBoard[i][newBoard.length-1] = value; 
				step1States.add(new State(transpose(modBoard), startState.getCardStack(), null, startState.getProbability()* (double)1/(modifiedRows.size()*holeValues.size())));
			}
		}

		return step1States;		
	}

	@Override
	protected Board getNewBoard(State startState) {
		//Update board. Up = transpose(right)
		int[][] startBoard = transpose(startState.getBoard().getBoardArray());
		int[][] newBoard = new int[startBoard.length][startBoard.length];		
		for (int i = 0; i<startBoard.length; i++){
			int[] row = startBoard[i];
			int[] newRow = combineLeft(row);			
			copyInto(newRow, newBoard[i]);
		}		

		return new Board(transpose(newBoard));		
	}

	@Override
	public String toString(){
		return "U";
	}





}
