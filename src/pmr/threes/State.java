/* Copyright (c) 2014 Peter Rimshnick
 * See the file license.txt for copying permission.
 */
package pmr.threes;

import java.util.Arrays;
import java.util.Map;

/** Represents state of game. This includes the current board, the hole card, the remaining stack
 * of possible hole cards, and the probability of this state
 * @author Peter Rimshnick
 *
 */
public class State {
	
	private final Board board;
	private final double probability;
	private final Map<HoleCard, Integer> cardStack;
	private final HoleCard holeCard;
	
	private static final double EPSILON = .00000001;
		
	/** Constructs state
	 * @param boardArray current board in array form
	 * @param cardStack current card deck
	 * @param holeCard current hole card
	 * @param probability probability of state
	 */
	public State(int[][] boardArray, Map<HoleCard, Integer> cardStack, HoleCard holeCard, double probability){
		this.board = new Board(boardArray);
		if (cardStack.isEmpty()) {
			this.cardStack = ThreesGame.generateCardStack(board);		
		}
		else this.cardStack = cardStack;
		this.holeCard = holeCard;
		this.probability = probability;
	}
	
	/** Constructs state. Probability assumed to be 1.0. Generates random card stack.
	 * @param boardArray current board in array form
	 * @param holeCard current hole card
	 */
	public State(int[][] boardArray, HoleCard holeCard){
		this.board = new Board(boardArray);		
		this.probability = 1.0;
		this.cardStack = ThreesGame.generateCardStack(board);
		this.holeCard = holeCard;
	}
	
	/** Constructs state
	 * @param boardArray current board 
	 * @param cardStack current card deck
	 * @param holeCard current hole card
	 * @param probability probability of state
	 */
	public State(Board board, Map<HoleCard, Integer> cardStack, HoleCard holeCard, double probability){
		this.board = board;
		if (cardStack.isEmpty()) {
			this.cardStack = ThreesGame.generateCardStack(board);		
		}
		else this.cardStack = cardStack;
		this.holeCard = holeCard;
		this.probability = probability;
	}
	
	/** Constructs state. Probability assumed to be 1.0. Generates random card stack.
	 * @param board current board
	 * @param holeCard current hole card
	 */
	public State(Board board, HoleCard holeCard){
		this.board = board;		
		this.probability = 1.0;
		this.cardStack = ThreesGame.generateCardStack(board);
		this.holeCard = holeCard;
	}
	
	
	/** Constructs state based on given state, but sets probability to 1.0
	 * @param newRoot state to act as new root (i.e. probability=1.0)s
	 */
	public State(State newRoot){
		this.board = newRoot.getBoard();
		this.probability = 1.0;
		this.cardStack = newRoot.getCardStack();
		this.holeCard = newRoot.getHoleCard();
	}
	
	/** Gets board
	 * @return board
	 */
	public Board getBoard(){ return board; };
	
	/** Gets probability of state
	 * @return probability of this state
	 */
	double getProbability() {return probability;}
	
	
	/** Gets card stack
	 * @return card stack of state
	 */
	Map<HoleCard, Integer> getCardStack() { return cardStack; };
	
	/** Gets hole card of state
	 * @return hole card
	 */
	HoleCard getHoleCard() { return holeCard; }
	
	@Override
	public boolean equals(Object o){
		if (!(o instanceof State)) return false;
		State s = (State)o;
		return s.board.equals(board) && s.cardStack.equals(cardStack) && s.holeCard.equals(holeCard) && Math.abs((s.probability-probability)/probability)<EPSILON;
	}
	
	@Override
	public int hashCode(){
		return Arrays.hashCode(board.getBoardArray());
	}
	
	@Override
	public String toString(){
		return board.toString() + "HoleCard: " + holeCard + " Prob: " + String.format("%3.2e\n", probability);
	}
	

}
