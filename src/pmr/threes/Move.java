/* Copyright (c) 2014 Peter Rimshnick
 * See the file license.txt for copying permission.
 */
package pmr.threes;

/** Interface for Move. A Threes move can simply be up, down, left, or right.
 * @author Peter Rimshnick
 *
 */
public interface Move {
	
	/** Returns possible outcomes states for use with search. Only does monte-carlo selection of
	 * next hole card for performance reasons
	 * @return array of possible resulting states from move
	 */
	public State[] findEndStatesForSearch();

	/** Returns possible outcome states for use with simulation. Includes all possible states based
	 * on next hole card
	 * @return array of possible resulting states from move
	 */
	public State[] findEndStatesForSim();
	

}
