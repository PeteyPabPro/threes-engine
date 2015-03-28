/* Copyright (c) 2014 Peter Rimshnick
 * See the file license.txt for copying permission.
 */
package pmr.threes;

/** Used as sentinel to represent end of portion of game tree to be searched
 * @author Peter Rimshnick
 *
 */
public class NullMove implements Move {
	
	private State state;
	
	/** Constructs null move from given state
	 * @param state
	 */
	public NullMove(State state){
		this.state = state;
	}

	@Override
	public State[] findEndStatesForSearch() {
		return new State[]{state};
	}

	@Override
	public State[] findEndStatesForSim() {
		return new State[]{state};
	}	
}
