/***************************************************************************************
  CS540 - Section 2
  Homework Assignment 2: Game Playing

  TakeStones.java
  This is the main class that implements functions for Take Stones playing!
  ---------
 *Free to modify anything in this file, except the class name 
  	You are required:
  		- To keep the class name as TakeStones for testing
  		- Not to import any external libraries
  		- Not to include any packages 
 *Notice: To use this file, you should implement 4 methods below.

	@author: TA 
	@date: Feb 2017
 *****************************************************************************************/

import java.util.ArrayList;

public class TakeStones {

	final int WIN_SCORE = 100; // score of max winning game
	final int LOSE_SCORE = -100;// score of max losing game
	final int INFINITY = 1000; // infinity constant

	/**
	 * Class constructor.
	 */
	public TakeStones() {
	};

	/**
	 * This method is used to generate a list of successors
	 * 
	 * @param state
	 *            This is the current game state
	 * @return ArrayList<Integer> This is the list of state's successors
	 */
	public ArrayList<Integer> generate_successors(GameState state) {
		int lastMove = state.get_last_move(); // the last move
		int size = state.get_size(); // game size
		ArrayList<Integer> successors = new ArrayList<Integer>(); 
		// list of successors
		if (lastMove == -1) {
			for (int i = 1; i < size / 2.0; i ++) {
				if(i%2==1) successors.add(i);
			}
			return successors;
			/*At the first move, the first player must choose an odd-numbered 
			 * stone that is strictly less than n/2
			 * */
		}
		// TODO Add your code here
		for(int i =1;i<=size;i++){
			if (state.get_stone(i) == true) {
				if(lastMove%i==0||i%lastMove==0)successors.add(i);
			}
				
		}
		/*
		 *At subsequent moves, players alternate turns. The stone number that 
		 *a player can take must be a multiple or factor of the last move 
		 */
		return successors;
	}

	/**
	 * This method is used to evaluate a game state based on the given heuristic
	 * function
	 * 
	 * @param state
	 *            This is the current game state
	 * @return int This is the static score of given state
	 */
	public int evaluate_state(GameState state) {
		// if stone 1 is still available, score is 0
		if (state.get_stone(1))
			return 0;

		int lastMove = state.get_last_move();
		ArrayList<Integer> successors = generate_successors(state);
		int count=0;
		if (lastMove==1) {
			if (successors.size() % 2 != 0)
				return 5;
			else
				return -5;
			// TODO Add your code here
			/*If lastMove is 1, count the number of the possible successors.
			 * If the count is odd, return 5; otherwise, return -5
			 * */
		} else if (Helper.is_prime(lastMove)) {
			
			for(int i=0;i<=state.get_size();i++){
				if(state.get_stone(i)){
					if(i%lastMove==0) count++;
				}
			}
			if (count % 2 != 0)
				return 7;
			else
				return -7;
			// TODO Add your code here
			/*If lastMove is a prime, count the multiples of that prime in all
			 *  possible successors. If the count is odd, return 7;
			 *  otherwise, return -7.
			 * */
		} else {
			int largestPrime = Helper.get_largest_prime_factor(lastMove);
			
			for(int i=0;i<=state.get_size();i++){
				if(state.get_stone(i)){
					if(i%largestPrime==0) count++;
				}
			}
			if (count % 2 != 0)
				return 6;
			else
				return -6;
			// TODO Add your code here
			/*If lastMove is a composite number (i.e., not prime), find the 
			 * largest prime that can divide lastMove, count the multipliers of 
			 * that prime, including the prime number itself if it hasn¡¯t 
			 * already been taken, in all the possible successors. 
			 * If the count is odd, return 6; otherwise, return -6.
			 * */
		}
	}

	/**
	 * This method is used to get the best next move from the current state
	 * 
	 * @param state
	 *            This is the current game state
	 * @param depth
	 *            Current depth of search
	 * @param maxPlayer
	 *            True if player is Max Player; Otherwise, false
	 * @return int This is the number indicating chosen stone
	 */
	public int get_next_move(GameState state, int depth, boolean maxPlayer) {
		int move = -1; // the best next move
		int alpha = -INFINITY; // initial value of alpha
		int beta = INFINITY; // initial value of alpha
		
		// Getting successors of the given state
		ArrayList<Integer> successors = generate_successors(state);
		
		// TODO Add your code here


		// Check if depth is 0 or it is terminal state
		if (0 == depth || 0 == successors.size()) {
			state.log();
			Helper.log_alphabeta(alpha, beta);
			return move;
		}
		//return the move if reached the terminal state
		
		// TODO Add your code here

		if (maxPlayer == true) {

			int tempV = -INFINITY;
			for (Integer successor : successors) {
				GameState newState = new GameState(state);//get new state


				newState.remove_stone(successor);//update the last move
				int v =  alphabeta(newState, depth - 1, alpha, beta, false);

				if (v > tempV) {
					move = successor;//update the move 
					tempV = v;//update the smaller v
				} else if (v == tempV) {
					move = Math.min(successor, move);
					//update the move
				}
				if(v>=beta) return move;//prune the branch
				alpha = Math.max(alpha, v);
			}
		} else {
			//v = INFINITY;
			int tempV = INFINITY;
			for (Integer successor : successors) {
				GameState newState = new GameState(state);//get new state
				
				newState.remove_stone(successor);//update the last move
				
				int v = alphabeta(newState, depth - 1, alpha, beta, true);

				if (v < tempV) {
					move = successor;
					tempV = v;//update the larger v
				} else if (v == tempV) {
					move = Math.min(successor, move);//update the move
				}
				if(v<=alpha) return move;//prune the branch
				beta = Math.min(beta, v);
			}
		}
		// Print state and alpha, beta before return
		state.log();
		Helper.log_alphabeta(alpha, beta);
		return move;
	}

	/**
	 * This method is used to implement alpha-beta pruning for both 2 players
	 * 
	 * @param state
	 *            This is the current game state
	 * @param depth
	 *            Current depth of search
	 * @param alpha
	 *            Current Alpha value
	 * @param beta
	 *            Current Beta value
	 * @param maxPlayer
	 *            True if player is Max Player; Otherwise, false
	 * @return int This is the number indicating score of the best next move
	 */
	public int alphabeta(GameState state, int depth, int alpha, int beta,
			boolean maxPlayer) {
		ArrayList<Integer> successors = generate_successors(state);
		int v;
		if (successors.size()==0) {
			if (maxPlayer == true) {
				state.log();
				Helper.log_alphabeta(alpha, beta);
				return LOSE_SCORE;
				//if at game end and max player playing, return lose score	
			} else {
				state.log();
				Helper.log_alphabeta(alpha, beta);
				return WIN_SCORE;
				//if at game end and max player playing, return lose score
			}
		}
		if (depth == 0) {
			state.log();
			Helper.log_alphabeta(alpha, beta);
			return evaluate_state(state);
		}
		//if at terminal state, return the evaluation of the state
		if (maxPlayer == true) {
			v = -INFINITY;
			for (Integer successor : successors) {
				GameState newState = new GameState(state);
				//get new state
				newState.remove_stone(successor);//update the last move
				v = Math.max(v,
						alphabeta(newState, depth - 1, alpha, beta, !maxPlayer));
				
				if (v >= beta) {
					state.log();
					Helper.log_alphabeta(alpha, beta);
					return v;//prune
				}
				alpha = Math.max(alpha, v);
			}
		} else {
			v = INFINITY;
			for (Integer successor : successors) {
				GameState newState = new GameState(state);
				//get new state
				newState.remove_stone(successor);//update last move
				v = Math.min(v,
						alphabeta(newState, depth - 1, alpha, beta, true));
				if (v <= alpha) {
					state.log();
					Helper.log_alphabeta(alpha, beta);
					return v;//prune
				}
				beta = Math.min(beta, v);
			}
		}
		// TODO Add your code here
		// Print state and alpha, beta before return
		state.log();
		Helper.log_alphabeta(alpha, beta);
		return v;
	}

	/**
	 * This is the main method which makes use of addNum method.
	 * 
	 * @param args
	 *            A sequence of integer numbers, including the number of stones,
	 *            the number of taken stones, a list of taken stone and search
	 *            depth
	 * @return Nothing.
	 * @exception IOException
	 *                On input error.
	 * @see IOException
	 */
	public static void main(String[] args) {
		try {
			// Read input from command line
			int n = Integer.parseInt(args[0]); // the number of stones
			int nTaken = Integer.parseInt(args[1]); // the number of taken
													// stones

			// Initialize the game state
			GameState state = new GameState(n); // game state
			int stone;
			for (int i = 0; i < nTaken; i++) {
				stone = Integer.parseInt(args[i + 2]);
				state.remove_stone(stone);
			}

			int depth = Integer.parseInt(args[nTaken + 2]); // search depth
			// Process for depth being 0
			if (0 == depth)
				depth = n + 1;

			TakeStones player = new TakeStones(); // TakeStones Object
			boolean maxPlayer = (0 == (nTaken % 2));// Detect current player

			// Get next move
			int move = player.get_next_move(state, depth, maxPlayer);
			// Remove the chosen stone out of the board
			state.remove_stone(move);

			// Print Solution
			System.out.println("NEXT MOVE");
			state.log();

		} catch (Exception e) {
			System.out.println("Invalid input");
		}
	}
}