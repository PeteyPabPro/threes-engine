/* Copyright (c) 2014 Peter Rimshnick
 * See the file license.txt for copying permission.
 */
package pmr.threes;

/** Represents move choice in search. A choice essentially consists of a move and a value of that move
 * @author Peter Rimshnick
 *
 */
public class Choice implements Comparable<Choice> {
	private final Move move;
	private final double value;	

	/** Default constructor.
	 * 
	 */
	private Choice(){
		move = null;
		value = Double.NEGATIVE_INFINITY;		
	};

	/** Creates choice with given move and value
	 * @param move
	 * @param value
	 */
	public Choice(Move move, double value){
		this.move = move;
		this.value = value;		
	}
	
	/** Sentinel value representing no choices
	 * @author Peter Rimshnick
	 *
	 */
	private static class EmptyChoice extends Choice {
		private EmptyChoice(){};
		
		@Override
		public boolean isEmptyChoice() {
			return true;
		}		
	}
	
	/** Singleton factory
	 * @return empty choice
	 */
	public static Choice getEmptyChoice(){
		return new EmptyChoice();
	}	

	/** Gets move associated with choice
	 * @return move of this choice
	 */
	public Move getMove() {
		return move;
	}

	/** Gets value associated with this choice
	 * @return values of this choice
	 */
	public double getValue() {
		return value;
	}
	
	/** Indicates whether this is an empty choice
	 * @return whether this choice is an {@link EmptyChoice} 
	 */
	public boolean isEmptyChoice(){
		return false;
	}
	
	@Override
	public String toString(){
		return "(" + move + "," + value +")";
	}
	
	@Override
	public int compareTo(Choice c){
		return Double.compare(value, c.value);
	}
   
}	
