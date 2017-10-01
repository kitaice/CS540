import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Breadth-First Search (BFS)
 * 
 * You should fill the search() method of this class.
 */
public class BreadthFirstSearcher extends Searcher {

	/**
	 * Calls the parent class constructor.
	 * 
	 * @see Searcher
	 * @param maze initial maze.
	 */
	public BreadthFirstSearcher(Maze maze) {
		super(maze);
	}

	/**
	 * Main breadth first search algorithm.
	 * 
	 * @return true if the search finds a solution, false otherwise.
	 */
	public boolean search() {
		// FILL THIS METHOD
		// explored list is a 2D Boolean array that indicates if a state 
		//associated with a given position in the maze has already been explored.
		boolean[][] explored = new boolean[maze.getNoOfRows()][maze.getNoOfCols()];
	
		boolean[][] frontier = new boolean[maze.getNoOfRows()][maze.getNoOfCols()];
		// ...
		// Queue implementing the Frontier list
		LinkedList<State> queue = new LinkedList<State>();
		State start=new State(maze.getPlayerSquare(),null,0,0);
		queue.add(start);
		frontier[start.getX()][start.getY()]=true;
		while (!queue.isEmpty()) {
			// TODO return true if find a solution
			// TODO maintain the cost, noOfNodesExpanded (a.k.a. noOfNodesExplored),
			// maxDepthSearched, maxSizeOfFrontier during
			// the search
			maxSizeOfFrontier=Math.max(maxSizeOfFrontier,queue.size());

			State explore=queue.pop();
			explored[explore.getX()][explore.getY()]=true;
			
			if(explore.isGoal(maze)){
				maxDepthSearched=explore.getDepth();
				cost=explore.getGValue();
				for(int i=0;i<explored.length;i++){
					for(int j=0;j<explored[i].length;j++){
						if(explored[i][j]==true) noOfNodesExpanded+=1;
					}
				}
				State trace=explore.getParent();
				while(trace!=null){
					 if(trace.getX()!=start.getX() || trace.getY()!=start.getY()){
						 maze.getMazeMatrix()[trace.getX()][trace.getY()]='.';
					 }
					 trace=trace.getParent();
				}
		
				return true;
			}
			ArrayList<State>successors=explore.getSuccessors(explored, maze);
		
			while(!successors.isEmpty()){
				State successor=successors.remove(0);
				if(frontier[successor.getX()][successor.getY()]==false) queue.add(successor);
				frontier[successor.getX()][successor.getY()]=true;
			}

			// TODO update the maze if a solution found
			// use queue.pop() to pop the queue.
			// use queue.add(...) to add elements to queue
		}
		return false;
		// TODO return false if no solution
	}
}
