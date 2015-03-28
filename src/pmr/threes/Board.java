/* Copyright (c) 2014 Peter Rimshnick
 * See the file license.txt for copying permission.
 */
package pmr.threes;

import java.util.Arrays;

/** Board representation based on dense matrix
 * @author Peter Rimshnick
 *
 */
public class Board {

	private final int[][] board;

	/** Construct board from 2-d input array
	 * @param board
	 */
	public Board(int[][] board){
		//this.board = deepCopy(board);
		this.board = board;
	}

	/** Produces copy of board
	 * @param original board
	 * @return copy of board
	 */
	public static int[][] deepCopy(int[][] original){
		int[][] result = new int[original.length][original[0].length];
		for (int i = 0; i<original.length; i++){
			result[i] = Arrays.copyOf(original[i], original[i].length);
		}
		return result;
	}

	/** Returns underlying board. No defensive copy made for performance reasons
	 * @return underlying board array
	 */
	public int[][] getBoardArray() {
		//return deepCopy(board);
		return board;
	}
	
	@Override
	public boolean equals(Object o){
		return (o instanceof Board) && (Arrays.deepEquals(((Board)o).board, board));
	}
	
	@Override
	public int hashCode(){
		return Arrays.hashCode(board);
	}

	@Override
	public String toString(){
		StringBuilder s = new StringBuilder("");
		s.append("\n");
		for (int[] row: board){
			s.append("|");
			for (int n: row){
				s.append(String.format("%6d", n));
			}
			s.append("|\n");
		}
		return s.toString();
	}
	
	

}
