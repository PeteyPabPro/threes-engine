/* Copyright (c) 2014 Peter Rimshnick
 * See the file license.txt for copying permission.
 */
package pmr.threes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/** Represents instance of game. Handles evaluation, optimal move selection, and other
 * high level concepts in Threes 
 * @author Peter Rimshnick
 *
 */
public class ThreesGame {

	private final int depth;
	final double boardWeight;
	final double freeCellWeight;
	final double matchableWeight;
	private static final Random rand = new Random(0);
	private static final double EPSILON = .0000001;
	private static final int THREAD_DEPTH = 3;
	private ExecutorService threadPool;

	/** Construct instance with default parameters
	 * 
	 */
	public ThreesGame(){
		depth = 5;
		boardWeight = .33;
		freeCellWeight = .33;
		matchableWeight = .33;
	}

	/** Construct instance with given depth, default other parameters
	 * @param depth max depth of tree to be searched
	 */
	public ThreesGame(int depth){
		this.depth = depth;
		boardWeight = .33;
		freeCellWeight = .33;
		matchableWeight = .33;
	}

	/** Constructs instance with given parameters
	 * @param depth max depth of tree to be searched
	 * @param boardWeight weight of board score in evaluation criteria
	 * @param freeCellWeight weight given to outstanding empty cells in evaluation criteria
	 * @param matchableWeight weight given to having potential matches in evaluation critiera
	 */
	public ThreesGame(int depth, double boardWeight, double freeCellWeight, double matchableWeight){
		this.depth = depth;
		this.boardWeight = boardWeight;
		this.freeCellWeight = freeCellWeight;
		this.matchableWeight = matchableWeight;
	}	

	/** Finds best move given a state
	 * @param start state
	 * @return {@link Choice} object containing info on best move found
	 */
	public Choice findBestMove(State start){		
		threadPool = Executors.newCachedThreadPool();
		Choice bestMove = findBestMove(start, start, depth, THREAD_DEPTH); 
		threadPool.shutdown();
		return bestMove;		
	}
	
	/** Internal method used to find best move. Uses concurrent, bounded, depth-first search.
	 * @param root root of tree
	 * @param s current state to be evaluated
	 * @param depth how deep current search is
	 * @param threadDepth how much deeper to go before we stop concurrency
	 * @return best move found
	 */
	private Choice findBestMove(final State root, final State s, final int depth, final int threadDepth) {		
		if (depth>0){
			Move[] options = {new Left(s), new Right(s), new Up(s), new Down(s)};
			Choice emptyChoice = Choice.getEmptyChoice();
			List<Choice> choices = new ArrayList<Choice>();
			if (threadDepth>0){
				List<Future<Choice>> futures = new ArrayList<Future<Choice>>();
				for (final Move m: options){
					Future<Choice> future = threadPool.submit(new Callable<Choice>(){
						@Override
						public Choice call() throws Exception {
							return evaluateMove(root, s, m, depth, threadDepth);
						}						
					});
					futures.add(future);
				}
				for (Future<Choice> future: futures){
					Choice c = null;
					try {
						c = future.get();
					} catch (InterruptedException | ExecutionException e) {
						throw new RuntimeException(e);
					}
					if (c!=null) choices.add(c);
				}
			}
			else {
				for (Move m: options){
					Choice c = null;					
					c = evaluateMove(root, s, m, depth,0);
					if (c!=null) choices.add(c);				
				}
			}			
			if (choices.isEmpty()) return emptyChoice;
			Collections.sort(choices);
			return choices.get(choices.size()-1);
		}
		else return new Choice(new NullMove(s), evaluateState(root, s));

	}

	/** Evaluates given move choice
	 * @param root root of tree
	 * @param s current state
	 * @param m proposed move
	 * @param depth how deep search has gone so far
	 * @param threadDepth how much deeper we go before we stop concurrency
	 * @return {@link Choice} object representing move and its value
	 */
	private Choice evaluateMove(State root, State s, Move m, int depth, int threadDepth){
		double avg = 0;				
		State[] endStates = m.findEndStatesForSearch();
		if (endStates.length==1 && endStates[0]==s) return null;
		for (State mState: endStates){
			avg += findBestMove(root, mState, depth-1, threadDepth-1).getValue();					
		}
		return new Choice(m,avg);		
	}

	/** Evaluates state based on various heuristics
	 * @param root root state of search
	 * @param state current state
	 * @return score of current state
	 */
	private double evaluateState(State root, State state){
		double boardScore = boardWeight<EPSILON? 0 : getBoardScore(root.getBoard())/getBoardScore(state.getBoard());
		int freeCellScore = freeCellWeight<EPSILON? 0 : getFreeCellScore(state.getBoard());
		int matchableScore = matchableWeight<EPSILON? 0 : getMatchableScore(state.getBoard());
		double score = (boardScore*boardWeight + freeCellScore*freeCellWeight + matchableScore*matchableWeight) * state.getProbability();
		return score;
	}

	/** Finds score of board
	 * @param board
	 * @return score of given board
	 */
	public static double getBoardScore(Board board) {
		double score = 0;
		for (int[] row: board.getBoardArray()){
			for (int cell: row){
				if (cell>2) score += Math.pow(3, Math.log(cell/3)/Math.log(2)+1);  //3^(log_2(cell/3)+1) = 3^((log(cell/3)/log(2))+1)
			}
		}
		return score;
	}

	/** Counts number of free cells in board
	 * @param board
	 * @return number of free cells in board
	 */
	private int getFreeCellScore(Board board) {
		int freeCount = 0;
		for (int[] row: board.getBoardArray()){
			for (int cell: row){
				if (cell==0) freeCount ++;
			}
		}
		return freeCount;
	}

	/** Counts number of matched cells in board
	 * @param board
	 * @return number of matched cells in board
	 */
	private int getMatchableScore(Board board) {
		int[][] boardArray = board.getBoardArray();
		int score = 0;
		for (int i = 0; i < boardArray.length; i++){
			for (int j = 0; j < boardArray[0].length; j++){
				if (i>0 && canMatch(boardArray[i][j], boardArray[i-1][j])) score++;
				if (i<boardArray.length-1 && canMatch(boardArray[i][j], boardArray[i+1][j])) score++;
				if (j>0 && canMatch(boardArray[i][j], boardArray[i][j-1])) score++;
				if (j<boardArray.length-1 && canMatch(boardArray[i][j], boardArray[i][j+1])) score++;				
			}
		}
		return score;
	}

	/** Determines whether two cells can be matched based on value
	 * @param int1 value of cell 1
	 * @param int2 value of cell 2
	 * @return true if cells can be matched
	 */
	static boolean canMatch(int int1, int int2) {
		return (int1==int2 && int1>=3) || oneAndTwo(int1, int2);
	}
	
	/** Determines if one cell is 1 and the other is 2
	 * @param a cell 1 value
	 * @param b cell 2 value
	 * @return true if one cell has value 1 and the other has value 2
	 */
	protected static boolean oneAndTwo(int a, int b){
		return (a==2 && b==1) || (a==1 && b==2);
	}	

	/** Generates random card stack based on available cards left given board
	 * @param board current board
	 * @return random card stack given current board
	 */
	public static Map<HoleCard,Integer> generateCardStack(Board board) {
		//Just generate one at random, don't split state space
		Map<HoleCard, Integer> stack = new HashMap<HoleCard,Integer>();
		stack.put(new RegularHoleCard(3),4);
		stack.put(new RegularHoleCard(2),4);
		stack.put(new RegularHoleCard(1),4);

		if (rand.nextDouble()<.5){
			List<Integer> possibleAdditions = getPossibleAdditions(board.getBoardArray());
			if (possibleAdditions.size()>0) {
				stack.put(new AmbiguousHoleCard(possibleAdditions),1);			
			}
		}
		return stack;
	}
	
	/** Finds possible hole card values given current board. Hole card values cannot exceed
	 * 1/8th of current max card on board.
	 * @param boardArray
	 * @return
	 */
	public static List<Integer> getPossibleAdditions(int[][] boardArray){
		int maxCard = 1;
		for (int[] row: boardArray){
			for (int cell: row){
				if (cell>maxCard) maxCard = cell;
			}
		}
		List<Integer> possibleAdditions = new ArrayList<Integer>();
		for (int i = 3; i<=maxCard/8; i*=2){
			possibleAdditions.add(i);
		}
		return possibleAdditions;
	}

	/** Picks a random state from a list based on their probabilities
	 * @param endStates possible states
	 * @return random state based on given distribution
	 */
	public static State pickState(State[] endStates){
		double prob = 0;
		for (State s: endStates){
			prob += s.getProbability();
		}
		assert Math.abs(prob-1)<.001;
		List<State> stateList = Arrays.asList(endStates);
		Collections.sort(stateList, new Comparator<State>(){
			@Override
			public int compare(State s1, State s2){
				return Double.compare(s2.getProbability(), s1.getProbability()); //ascending
			}
		});
		double random = rand.nextDouble();
		double sum = 0;
		int i = -1;
		do {
			sum += stateList.get(++i).getProbability();
		}
		while ((random > sum) && i<stateList.size()-1);
		return stateList.get(i);
	}


}
