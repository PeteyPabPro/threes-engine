/* Copyright (c) 2014 Peter Rimshnick
 * See the file license.txt for copying permission.
 */
package pmr.threes;

import static pmr.threes.ThreesGame.oneAndTwo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/** Abstract skeleton for {@link Move}. Provides methods common to most implementations.
 * @author Peter Rimshnick
 *
 */
public abstract class AbstractMove implements Move {

	protected final State previousState;

	/** Constructs {@link Move} with link to previous {@link State}
	 * @param previousState Previous state before {@link Move}
	 */
	public AbstractMove(State previousState){
		this.previousState = previousState;
	}	

	
	/* (non-Javadoc)
	 * @see pmr.threes.Move#findEndStatesForSearch()
	 */
	@Override
	public State[] findEndStatesForSearch() {		
		List<State> step1States = getStep1States(previousState);
		List<State> newStates;
		if (step1States.size()!=1 || step1States.get(0)!=previousState) {
			newStates = updateHoleCardForSearch(step1States);
		}
		else newStates = new ArrayList<State>(Arrays.asList(new State[]{previousState}));		
		return newStates.toArray(new State[newStates.size()]);
	}

	/* (non-Javadoc)
	 * @see pmr.threes.Move#findEndStatesForSim()
	 */
	@Override
	public State[] findEndStatesForSim() {		
		List<State> step1States = getStep1States(previousState);
		List<State> newStates;
		if (step1States.size()!=1 || step1States.get(0)!=previousState) {
			newStates = updateHoleCardForSim(step1States);
		}
		else newStates = new ArrayList<State>(Arrays.asList(new State[]{previousState}));		
		return newStates.toArray(new State[newStates.size()]);
	}

	/** Copies array
	 * @param fromArray
	 * @param toArray
	 */
	protected static void copyInto(int[] fromArray, int[] toArray){
		System.arraycopy(fromArray, 0, toArray, 0, fromArray.length);
	}

	/** Copies array board rep
	 * @param board
	 * @return copy of board rep
	 */
	protected static int[][] copyBoard(int[][] board){
		return Board.deepCopy(board);
	}

	/** Returns version of card stack with given card removed
	 * @param stack cards not played yet
	 * @param h card to be removed
	 * @return new version of stack rep with given card removed
	 */
	protected static Map<HoleCard, Integer> subtractKey(Map<HoleCard, Integer> stack, HoleCard h){
		Map<HoleCard, Integer> newMap = new HashMap<HoleCard, Integer>(stack);
		int count = stack.get(h);
		assert count>0;
		if (count==1) {
			newMap.remove(h);			
		}
		else {
			newMap.put(h, count-1);		
		}
		return newMap;
	}

	/** Removes random card from deck
	 * @param stack deck of remaining cards
	 * @return random card to be next {@link HoleCard}
	 */
	protected static HoleCard getRandomCard(Map<HoleCard, Integer> stack){
		List<HoleCard> expanded = new ArrayList<HoleCard>();
		for (Entry<HoleCard, Integer> e: stack.entrySet()){
			for (int i = 0; i<e.getValue(); i++){
				expanded.add(e.getKey());
			}
		}
		Collections.shuffle(expanded);
		return expanded.get(0);
	}

	/** Given new possible states stemming from move only, create states based on possible hole cards
	 * Uses monte-carlo selection of new state, rather than creating all possible states,
	 * which limits space size for search and makes problem tractable
	 * @param step1States possible states based on current move choice and new card insertion
	 * @return monte-carlo creation of new states based on random selection of hole card
	 */
	protected List<State> updateHoleCardForSearch(List<State> step1States){
		List<State> step2States = new ArrayList<State>();		
		for (State s: step1States){			
			HoleCard h = getRandomCard(s.getCardStack());
			step2States.add(new State(s.getBoard(), subtractKey(s.getCardStack(),h), h, s.getProbability()));

		}
		return step2States;
	}

	/** Given new possible states stemming from move only, create states based on possible hole cards
	 * Creates all possible states, unlike {@link updateHoleCardForSearch} which chooses one
	 * possible state per original state via monte-carlo 
	 * @param step1States possible states based on current move choice and new card insertion
	 * @return all possible new states based on different values of hole card
	 */
	protected List<State> updateHoleCardForSim(List<State> step1States){
		List<State> step2States = new ArrayList<State>();		
		for (State s: step1States){
			int cardCount = 0;
			for (HoleCard h: s.getCardStack().keySet()) {
				cardCount+=s.getCardStack().get(h);
			}
			for (Entry<HoleCard, Integer> e: s.getCardStack().entrySet()){				
				HoleCard h = e.getKey();
				step2States.add(new State(s.getBoard(), subtractKey(s.getCardStack(),h), h, (s.getProbability() * e.getValue())/cardCount));
			}
		}
		return step2States;
	}

	

	/** Does a left combination operation
	 * @param row row to be left combined
	 * @return new version of row after combination
	 */
	protected static int[] combineLeft(int[] row) {
		if (row.length==1) return row;
		int[] newRow = new int[row.length];
		if (row[0]!=0 && row[0]==row[1] && row[0]>2) {
			newRow[0] = row[0]*2;
			shiftLeft(row, newRow,2);
			return newRow;
		}
		else if (row[0]!=0 && oneAndTwo(row[0],row[1])){
			newRow[0] = 3;
			shiftLeft(row, newRow,2);
			return newRow;
		}
		else if (row[0]==0){
			shiftLeft(row, newRow, 1);
			return newRow;
		}
		else {
			newRow[0] = row[0];
			int[] rest = combineLeft(Arrays.copyOfRange(row, 1, row.length));
			for (int i = 0; i < rest.length; i++){
				newRow[i+1] = rest[i];
			}
			return newRow;
		}
	}

	
	/** Makes left shifted copy of row
	 * @param row original row
	 * @param newRow new row
	 * @param start start index of copy
	 */
	protected static void shiftLeft(int[] row, int[] newRow, int start){
		for (int i = start; i<row.length; i++){
			newRow[i-1]=row[i];
		}
		newRow[row.length-1] = 0;		
	}

	/** Does a right combination operation
	 * @param row row to be right combined
	 * @return new version of row after combination
	 */
	protected static int[] combineRight(int[] row) {		
		return reverse(combineLeft(reverse(row)));
	}

	/** Makes right shifted copy of row
	 * @param row original row
	 * @param newRow new row
	 * @param start start index of copy
	 */
	protected static void shiftRight(int[] row, int[] newRow, int start){
		for (int i = start; i>0; i--){
			newRow[i+1]=row[i];
		}
		newRow[0] = 0;		
	}		

	/** Simple array reversal
	 * @param array
	 * @return reversal of array
	 */
	protected static int[] reverse(int[] array){
		int[] copy = new int[array.length];
		for (int i = 0; i<array.length; i++){
			copy[array.length-1-i] = array[i];
		}
		return copy;
	}
	
	/** Produces transpose of matrix
	 * @param matrix
	 * @return transpose of matrix
	 */
	protected static int[][] transpose(int[][] matrix){
		int[][] newMatrix = new int[matrix[0].length][matrix.length];
		for (int i = 0; i<matrix.length;i++){
			for (int j = 0; j<matrix[0].length; j++){
				newMatrix[j][i] = matrix[i][j];
			}
		}		
		return newMatrix;
	}

	/** Returns set of possible states based on move and insertion of hole card into board only
	 * @param state start state
	 * @return list of new possible states
	 */
	protected abstract List<State> getStep1States(State state);
	protected abstract Board getNewBoard(State state);




}
