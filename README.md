# threes-engine

This project constitutes an engine for the smartphone game *Threes*. In other words, it attempts to calculate the optimal move given a current board. 

The driver class for the engine is `ThreesGame.java`. `Console.java` and `Demo.java` in the test folder provide examples of how to interact with the engine. `Console.java` provides an example of how one provide data to the engine and request an optimal move based on this board data (i.e. the actual realization of the random elements of the game, such as which new card appears, or where it gets inserted into the board). `Demo.java` actually simulates these random elements to create an automated demonstration of the engine's capabilities.

It currently evaluates board states based on three criteria (three!): board score (based on tile values), number of free cells, and number of potential matches in the board. These criteria are weighted together (with weights optimized by a custom simulated annealer) to get the projected value of a state. The expected value of future states at a specified depth is the objective function used to determine the best current move to make. 
