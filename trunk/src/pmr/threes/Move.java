package pmr.threes;

public interface Move {
	
	public State[] findEndStatesForSearch();

	public State[] findEndStatesForSim();
	
	//public Move getInstance(State previousState);

}
