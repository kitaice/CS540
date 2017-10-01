import java.util.ArrayList;
import java.util.Iterator;
import java.util.PriorityQueue;

/**
 * A* algorithm search
 * 
 * You should fill the search() method of this class.
 */
public class AStarSearcher extends Searcher {

	/**
	 * Calls the parent class constructor.
	 * 
	 * @see Searcher
	 * @param maze initial maze.
	 */
	public AStarSearcher(Maze maze) {
		super(maze);
	}

	/**
	 * Main a-star search algorithm.
	 * 
	 * @return true if the search finds a solution, false otherwise.
	 */
	public boolean search() {

		// FILL THIS METHOD

		// explored list is a Boolean array that indicates if a state associated with a given position in the maze has already been explored. 
		boolean[][] explored = new boolean[maze.getNoOfRows()][maze.getNoOfCols()];
		boolean[][] inFrontier = new boolean[maze.getNoOfRows()][maze.getNoOfCols()];
		double[][] fVal= new double[maze.getNoOfRows()][maze.getNoOfCols()];
		// ...

		PriorityQueue<StateFValuePair> frontier = new PriorityQueue<StateFValuePair>();
		State start=new State(maze.getPlayerSquare(),null,0,0);
		frontier.add(new StateFValuePair(start,0));
		inFrontier[start.getX()][start.getY()]=true;
		fVal[start.getX()][start.getY()]=start.getGValue()+heuristic(start);
		// TODO initialize the root state and add
		// to frontier list
		// ...
		while (!frontier.isEmpty()) {
			// TODO return true if a solution has been found
			// TODO maintain the cost, noOfNodesExpanded (a.k.a. noOfNodesExplored),
			// maxDepthSearched, maxSizeOfFrontier during
			// the search
			// TODO update the maze if a solution found
			maxSizeOfFrontier=Math.max(maxSizeOfFrontier,frontier.size());

			StateFValuePair explore=frontier.poll();
			explored[explore.getState().getX()][explore.getState().getY()]=true;

			if(explore.getState().isGoal(maze)){
				maxDepthSearched=explore.getState().getDepth();
				cost=explore.getState().getGValue();
				for(int i=0;i<explored.length;i++){
					for(int j=0;j<explored[i].length;j++){
						if(explored[i][j]==true)noOfNodesExpanded+=1;
					}
				}
				State trace=explore.getState().getParent();
				while(trace!=null){
					if(trace.getX()!=start.getX() || trace.getY()!=start.getY()){
						maze.getMazeMatrix()[trace.getX()][trace.getY()]='.';
					}
					trace=trace.getParent();
				}
				return true;
			}
			ArrayList<State> successors=explore.getState().getSuccessors(explored, maze);
			
			for(State successor : successors){			
				double cost = successor.getGValue() + heuristic(successor);
				StateFValuePair newPair=new StateFValuePair(successor,cost);

				if(inFrontier[successor.getX()][successor.getY()]==false){
					 frontier.add(newPair);
					 inFrontier[successor.getX()][successor.getY()]=true;
					 fVal[successor.getX()][successor.getY()]=cost;	 
				}
				else if(fVal[successor.getX()][successor.getY()]<cost){
					 frontier.add(newPair);
					 fVal[successor.getX()][successor.getY()]=cost;	 
				}

			}

			// use frontier.poll() to extract the minimum stateFValuePair.
			// use frontier.add(...) to add stateFValue pairs
		}
		return false;
		// TODO return false if no solution
	}
	private double heuristic(State state){
		return Math.hypot(maze.getGoalSquare().X-state.getX(), maze.getGoalSquare().Y-state.getY());
	}
}
