/* Copyright (c) 2014 Peter Rimshnick
 * See the file license.txt for copying permission.
 */
package pmr.threes;

/** Represents hole card with definite value
 * @author Peter Rimshnick
 *
 */
public class RegularHoleCard implements HoleCard {

	final int value;

	/** Constructs hole card with given value
	 * @param value
	 */
	public RegularHoleCard(int value){
		this.value = value;
	}

	@Override
	public boolean equals(Object o){
		return (o instanceof RegularHoleCard) && ((RegularHoleCard)o).value==value;
	}

	@Override
	public int hashCode(){
		return value;
	}

	@Override
	public String toString(){
		return value+"";
	}

}
