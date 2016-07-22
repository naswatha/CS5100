package entrants.pacman.naveenaswa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import pacman.controllers.PacmanController;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/**
 * @author Naveen
 * @description : Provides implementation for the Breadth First Search find
 * the goal state which can maximize the pacman score.
 * 
 * At each pacman position a tree of all possible game state are generated till
 * certain depth, traverse all the gamestate in the tree in BFS manner and select
 * the goal state based on maximum score.
 * 
 */
public class BFSPacman extends PacmanController {

	// contains all the pills that has been visited
	private static ArrayList<Integer> visitedPills = new ArrayList<Integer>();
	// contains all the available pills
	private static ArrayList<Integer> availablePills = new ArrayList<Integer>();

	/**
	 * @description: central method responsible to get the best move which
	 * could be made for the tree of game states generated.
	 * @param : game : current game state
	 * @param : timedue :time till next move.
	 * @return : MOVE : the move that should be made by the pacman
	 */
	@Override
	public MOVE getMove(Game game, long timeDue) {

		// get the current index of pacman
		int current = game.getPacmanCurrentNodeIndex(); 	
		// add current to visited.
		visitedPills.add(current);
		// update the available pills on this maze
		updateAvailablePills(game);
		GameNode rootNode = new GameNode(0,current,new ArrayList<MOVE>(),0);
		ArrayList<GameNode> root = new ArrayList<GameNode>();
		root.add(rootNode);
		GameNode result = findGoalNode(root,game.copy());
		return result.action.get(0);
	}

	/**
	 * @description: updates the available pills in the current maze
	 * @param : game : current game state.
	 */
	private static void updateAvailablePills(Game game) {

		int[]  allpills  = game.getCurrentMaze().pillIndices;
		for(int i = 0; i < allpills.length; i++){
			if(!visitedPills.contains(allpills[i])){
				availablePills.add(allpills[i]);
			}
		}
	}

	/**
	 * @description: 
	 * From pacman position a tree of all possible game state are generated till
	 * certain depth, traverse all the gamestate in the tree in BFS manner and select
	 * the goal state based on maximum score.
	 * @param : root : node bound in arraylist.
	 * @param : game : game state copy
	 * @return : GameNode : which contains the resultant path.
	 */
	private static GameNode findGoalNode(ArrayList<GameNode> root, Game game) {

		int depth = 0;
		// add current game node as visited.
		ArrayList<Integer> visitedPillIndex = new ArrayList<Integer>();
		// all pills of the maze
		ArrayList<Integer> allPillsList = loadAllPills(game);
		// add each move to move list excluding the neutral
		ArrayList<MOVE> moveList = loadMoves(game);
		// add the game nodes from each depth to the queue
		ArrayList<GameNode> gameNodeQueue = new ArrayList<GameNode>();
		// get the first node from the queue
		gameNodeQueue.add(root.get(0));
		// main queue where each of the children nodes are added.
		while(!gameNodeQueue.isEmpty() && depth < 40){

			depth++;
			ArrayList<GameNode> levelGameNodes = gameNodeQueue;
			gameNodeQueue = new ArrayList<GameNode>();
			// from each of the children node generate their children nodes .. nodes are game states.
			for(GameNode currentNode : levelGameNodes){
				
				visitedPillIndex.add(currentNode.pillIndex);
				//System.out.println("curent"+currentNode);
				for(MOVE move : moveList){
					int nextPillIndex = game.getNeighbour(currentNode.pillIndex, move);
					// to check if the pacman is not moving to the wall
					if(!visitedPillIndex.contains(nextPillIndex) && nextPillIndex >= 0){
						// if already visited then do not go that way
							// if the pill index is available to eat then increment score else add zero
							if(!visitedPills.contains(nextPillIndex) && allPillsList.contains(nextPillIndex)){
									ArrayList<MOVE> action = new ArrayList<MOVE>();
									action.addAll(currentNode.action);
									action.add(move);
									int score = currentNode.score + 1;
									gameNodeQueue.add(new GameNode(depth, nextPillIndex,action,score));								
							}
							else{
								ArrayList<MOVE> action = new ArrayList<MOVE>();
								action.addAll(currentNode.action);
								action.add(move);
								int score = currentNode.score + 0;
								gameNodeQueue.add(new GameNode(depth, nextPillIndex,action,score));
								
							}
						}
					}
			}				
		}
		// get node with the highest score;
		return maxScoreNode(gameNodeQueue);
	}

	/**
	 * @description: get the node with the maximum score, a sort function for nodes.
	 * @param : gameNodeQueue : game states at maximum depth level
	 * @return : maxNode : node with maximum score.
	 */
	private static GameNode maxScoreNode(ArrayList<GameNode> gameNodeQueue) {

		Collections.sort(gameNodeQueue, new Comparator<GameNode>() {
			public int compare(GameNode o1, GameNode o2) {
				if (o1.getScore() < o2.getScore())
					return 1;
				if (o1.getScore() == o2.getScore())
					return 0;
				return -1;
			}
		});

		GameNode maxNode = gameNodeQueue.get(0);

		return maxNode;
	}




	/**
	 * @description: load all the moves to the array list
	 * @param : game : game state
	 * @return : moveList : list of moves.
	 */
	private static ArrayList<MOVE> loadMoves(Game game) {
		ArrayList<MOVE> moveList = new ArrayList<MOVE>();
		moveList.add(MOVE.RIGHT);
		moveList.add(MOVE.LEFT);
		moveList.add(MOVE.UP);
		moveList.add(MOVE.DOWN);
		return moveList;
	}

	/**
	 * @description: load all the pills into array list.
	 * @param : game : game state
	 * @return : allPillsList : all pills of current maze
	 */
	private static ArrayList<Integer> loadAllPills(Game game) {
		// get all the available pills present in the maze irrespective of availability
		int[] allPills = game.getCurrentMaze().pillIndices;
		int[] allPowerPills = game.getCurrentMaze().powerPillIndices;
		ArrayList<Integer> allPillsList = new ArrayList<Integer>();
		for (int i = 0; i < allPills.length; i++) {
			allPillsList.add(allPills[i]);
		}
		for(int i = 0; i < allPowerPills.length; i++){
			allPillsList.add(allPowerPills[i]);
		}
		return allPillsList;
	}
}
