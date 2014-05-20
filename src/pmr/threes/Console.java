package pmr.threes;

import java.util.Scanner;
import java.util.Vector;

public class Console {
	
	public static void main(String[] args){
		boolean ok = false;
		Scanner scanner = null;
		State start = null;
		int[][] board;
		HoleCard holeCard = null;
		while(!ok) {
			System.out.println("Enter 4x4 starting board({x11 x12 x13 x14},{x21 x22 x23 x24}..), blank=0: ");
			scanner = new Scanner(System.in);
			String boardString = scanner.nextLine();		
			board = readBoard(boardString);
			System.out.println("Enter hole card:");
			String hole = scanner.nextLine();
			if (hole.equalsIgnoreCase("+")){
				holeCard = new AmbiguousHoleCard(ThreesGame.getPossibleAdditions(board));
				start = new State(board,holeCard);				
			}
			else {
				int holeInt = Integer.parseInt(hole);
				holeCard = new RegularHoleCard(holeInt);
				start = new State(board, (RegularHoleCard)holeCard);
			}			
			System.out.println("Start state: " + start);
			System.out.println("Enter y if ok, n if not:");
			String choice = scanner.nextLine();
			if (choice.equalsIgnoreCase("y")) ok = true;			
		}
		
		int depth = 5;
		ThreesGame game = new ThreesGame(depth,.39,.47,.14);
		Choice currChoice;		
		Vector<State> states = new Vector<State>();
		int step = 0;
		State state = start;
		while(true){
			states.add(state);
			currChoice = game.findBestMove(state);
			if (currChoice.isEmptyChoice()) break;			
			System.out.println("Make this move: " + currChoice + "\n");
			
			Move m = currChoice.getMove();
			if (!(m instanceof AbstractMove)){
				throw new RuntimeException("Move not recognized: "+m);
			}
			if ((m instanceof Right) || (m instanceof Left)) {
				System.out.println("Which row did hole card enter (1-4):?");				
			}
			if ((m instanceof Up) || (m instanceof Down)) {
				System.out.println("Which column did hole card enter (1-4):?");				
			}
			else assert false;			
			int entrance = Integer.parseInt(scanner.nextLine());
			
			if (holeCard instanceof AmbiguousHoleCard){
				System.out.println("What was the last hole card?:");
				String hole = scanner.nextLine();
				holeCard = new RegularHoleCard(Integer.parseInt(hole));
				state = new State(state.getBoard(), holeCard);				
			}
			
			System.out.println("What is new hole card? (b to go back):");
			String choice = scanner.nextLine();
			if (choice.equalsIgnoreCase("b")) state = states.get(states.size()-2);
			else {				
				Board newBoard = ((AbstractMove)m).getNewBoard(state);
				newBoard = updateBoard((AbstractMove)m,newBoard,(RegularHoleCard)holeCard,entrance);
				if (choice.equals("+")){
					holeCard = new AmbiguousHoleCard(ThreesGame.getPossibleAdditions(newBoard.getBoardArray()));					
				}
				else {
					holeCard = new RegularHoleCard(Integer.parseInt(choice));					
				}
				state = new State(newBoard, holeCard);
				
			}
			
			System.out.println("\nStep= " + step++ + " Current state: " + state);			
		}
		scanner.close();
	}

	private static Board updateBoard(AbstractMove m, Board b, RegularHoleCard h, int position) {
		int[][] array = Board.deepCopy(b.getBoardArray());
		if (m instanceof Left){
			array[position-1][array.length-1] = h.value;
		}
		if (m instanceof Right){
			array[position-1][0] = h.value;
		}
		if (m instanceof Up){
			array[array.length-1][position-1] = h.value;
		}
		if (m instanceof Down){
			array[0][position-1] = h.value;
		}
		return new Board(array);
	}

	private static int[][] readBoard(String boardString) {
		int[][] board = new int[4][4];
		String[] rows = boardString.split(",");
		assert rows.length==3;
		for (int i=0; i<rows.length; i++){
			String row = rows[i];
			String nums = row.split("[{}]")[1];
			String[] numArray = nums.split(" ");
			assert numArray.length==3;
			for (int j = 0; j<numArray.length; j++){
				board[i][j] = Integer.parseInt(numArray[j]);
			}
		}
		return board;
	}
	
	

}
