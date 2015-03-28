/* Copyright (c) 2014 Peter Rimshnick
 * See the file license.txt for copying permission.
 */
package pmr.threes;

import java.util.List;
import java.util.ArrayList;

/** Represents hole card which can take on multiple values, so is yet undetermined
 * @author Peter Rimshnick
 *
 */
public class AmbiguousHoleCard implements HoleCard {
	
	private final List<Integer> possibleAdditions;

	/** Constructs object with possible values
	 * @param possibleAdditions all possible values for this card
	 */
	public AmbiguousHoleCard(List<Integer> possibleAdditions) {
		this.possibleAdditions = possibleAdditions;
	}
	
	/** Returns list of possible values for this hole card
	 * @return list of possible values
	 */
	public List<Integer> getPossibleAdditions(){
		return new ArrayList<Integer>(possibleAdditions);
	}
	
	@Override
	public String toString(){
		return "+";
	}

}
