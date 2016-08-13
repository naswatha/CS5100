package entrants.pacman.naveenaswa;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import pacman.controllers.PacmanController;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.GameView;


public class GreedyBFSPacman extends PacmanController {

	// contains all the pills that has been visited
	private static ArrayList<Integer> visitedPills = new ArrayList<Integer>();
	// contains all the available pills
	private static ArrayList<Integer> availablePills = new ArrayList<Integer>();

	@Override
	public MOVE getMove(Game game, long timeDue) {

		// get the current index of pacman
		int current = game.getPacmanCurrentNodeIndex(); 	
		visitedPills.add(current);
		updateAvailablePills(game);
		GameNode rootNode = new GameNode(0,current,new ArrayList<MOVE>(),0);
		ArrayList<GameNode> root = new ArrayList<GameNode>();
		root.add(rootNode);
		GameNode result = findGoalNode(root,game.copy(),current);
		//System.exit(0);
		return result.action.get(0);
	}

	// update the available pill based on the pacman movement.
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
	 * certain depth, and sort the game state with the heuristic function which is 
	 * either manhattan distance or shortest distance. And pick the best among those.
	 * @param : root : node bound in arraylist.
	 * @param : game : game state copy
	 * @param : sourceIndex : contains the current index of the pacman
	 * @return : GameNode : which contains the resultant path.
	 */
	private static GameNode findGoalNode(ArrayList<GameNode> root, Game game, int sourceIndex) {

		int depth = 0;
		// add current game node as visited.
		ArrayList<Integer> visitedPillIndex = new ArrayList<Integer>();
		// all pills of the maze
		ArrayList<Integer> allPillsList = loadAllPills(game);
		// add each move to move list excluding the neutral
		ArrayList<MOVE> moveList = loadMoves(game);
		// add the game nodes from each depth to the queue
		ArrayList<GameNode> gameNodeQueue = new ArrayList<GameNode>(); 
		gameNodeQueue.add(root.get(0));

		while(!gameNodeQueue.isEmpty() && depth < 40){
			// send nodes to my heuristic function and return me best node.
			// based on Manhattan distance
			ArrayList<GameNode> levelGameNodes = shortestHeuristic(gameNodeQueue,game,sourceIndex);
			// based on Shortest distance
			//ArrayList<GameNode> levelGameNodes = shortestHeuristic(gameNodeQueue,game,sourceIndex);
			gameNodeQueue = new ArrayList<GameNode>();
			depth++;
			// main queue where each of the children nodes are added.
			for(GameNode currentNode : levelGameNodes){

				visitedPillIndex.add(currentNode.pillIndex);
				for(MOVE move : moveList){
					//System.out.println(move);
					int nextPillIndex = game.getNeighbour(currentNode.pillIndex, move);
					//System.out.println(nextPillIndex);
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
							try{
								GameView.addLines(game, Color.GREEN, game.getPacmanCurrentNodeIndex(), game.getNeighbour(nextPillIndex, move));
							}
							catch(Exception e){
								continue;
							}
						}
						else{
							ArrayList<MOVE> action = new ArrayList<MOVE>();
							action.addAll(currentNode.action);
							action.add(move);
							int score = currentNode.score + 0;
							gameNodeQueue.add(new GameNode(depth, nextPillIndex,action,score));
//							try{
//								GameView.addLines(game, Color.GREEN, game.getPacmanCurrentNodeIndex(), game.getNeighbour(nextPillIndex, move));
//							}
//							catch(Exception e){
//								continue;
//							}

						}
					}
				}
			}				
		}
		// get node with the highest score;
		return maxScoreNode(gameNodeQueue);
	}

	/**
	 * @description: heuristic function to determine the best next game state 
	 * from the given game state. Calculates the shortest distance between the 
	 * source game state to the intermediate game state which has lowest distance.
	 * @param : gameNodeQueue : game states at maximum depth level
	 * @param : game : game copy
	 * @param : sourceIndex : current position of the pacman
	 * @return : maxNode : node with maximum score.
	 */
	private static ArrayList<GameNode> shortestHeuristic(ArrayList<GameNode> gameNodeQueue, Game game, int sourceIndex) {

		ArrayList<NodeSort> wrapper = new ArrayList<NodeSort>();
		for(GameNode node : gameNodeQueue){
			int manDist= game.getShortestPathDistance(node.pillIndex, sourceIndex);
			NodeSort newNode = new NodeSort(node, manDist);
			wrapper.add(newNode);
		}

		Collections.sort(wrapper, new Comparator<NodeSort>() {
			public int compare(NodeSort o1, NodeSort o2) {
				if (o1.getDistance() > o2.getDistance())
					return 1;
				if (o1.getDistance() == o2.getDistance())
					return 0;
				return -1;
			}
		});

		ArrayList<GameNode> sortedNodes = new ArrayList<GameNode>();
		for(NodeSort node: wrapper){
			sortedNodes.add(node.getNode());
		}
		return sortedNodes;
	}

	/**
	 * @description: heuristic function to determine the best next game state 
	 * from the given game state. Calculates the manhattan distance between the 
	 * source game state to the intermediate game state which has lowest distance.
	 * @param : gameNodeQueue : game states at maximum depth level
	 * @param : game : game copy
	 * @param : sourceIndex : current position of the pacman
	 * @return : maxNode : node with maximum score.
	 */
	private static ArrayList<GameNode> manhattanHeuristic(ArrayList<GameNode> gameNodeQueue, Game game, int sourceIndex) {

		ArrayList<NodeSort> wrapper = new ArrayList<NodeSort>();
		for(GameNode node : gameNodeQueue){
			int manDist= game.getManhattanDistance(node.pillIndex, sourceIndex);
			NodeSort newNode = new NodeSort(node, manDist);
			wrapper.add(newNode);
		}

		Collections.sort(wrapper, new Comparator<NodeSort>() {
			public int compare(NodeSort o1, NodeSort o2) {
				if (o1.getDistance() > o2.getDistance())
					return 1;
				if (o1.getDistance() == o2.getDistance())
					return 0;
				return -1;
			}
		});

		ArrayList<GameNode> sortedNodes = new ArrayList<GameNode>();
		for(NodeSort node: wrapper){
			sortedNodes.add(node.getNode());
		}
		return sortedNodes;
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


/**
 * @author Naveen
 * @description : Helper class to sort the gamestate according to their distance 
 * from the source to target  
 */
class NodeSort {
	public GameNode node;

	public int distance;

	public GameNode getNode() {
		return node;
	}

	public void setNode(GameNode node) {
		this.node = node;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}
	/**
	 * @description : constructor
	 */
	public NodeSort(GameNode node, int distance) {
		this.node = node;
		this.distance = distance;
	}


}