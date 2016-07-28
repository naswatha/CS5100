package entrants.pacman.naveenaswa.minmax;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import pacman.controllers.PacmanController;
import pacman.game.Constants.GHOST;
import pacman.game.Constants;
import pacman.game.Game;
import pacman.game.GameView;
import pacman.game.Constants.MOVE;

/**
 * @author Naveen
 * @description : Provides implementation for the MinMax algorihm on 
 * pacman game.
 * 
 * At each pacman position  
 */
public class MinMax extends PacmanController {

	// tick for debug purpose
	public static int tick = 0;
	// penalty score if ghost encountered.
	private static int PENALTY_SCORE = 1000;
	// establish safe distance from ghosts
	private static int SAFE_DISTANCE = 25;

	/**
	 * @description: central method responsible to get the best move which
	 * could be made from the available game states from minmax algorithm.
	 * @param : game : current game state
	 * @param : timedue :time till next move.
	 * @return : MOVE : the move that should be made by the pacman
	 */
	@Override
	public MOVE getMove(Game game, long timeDue) {	

		tick++;	
		int currentIndex = game.getPacmanCurrentNodeIndex(); 
		Game gameCopy = game.copy();
		MOVE myMove = MOVE.NEUTRAL;
		int maxScore = Integer.MIN_VALUE;
		int compareScore;

		// for each move pacman can make take the maximum score for pacman.
		for(MOVE move : gameCopy.getPossibleMoves(currentIndex)){
			Game newGame = gameCopy.copy();
			newGame.updatePacMan(move);
			newGame.updateGame();
			int pillIndex = gameCopy.getPacmanCurrentNodeIndex();
			ArrayList<MOVE> moves = new ArrayList<MOVE>();
			moves.add(move);
			GameNode newNode = new GameNode(pillIndex,null,moves,newGame);
			// call minmax function to get the minimum score for the ghosts moves.
			compareScore = minmaxFunction(newNode, 4, false);
			System.out.println("Compare Score: "+compareScore);
			if(compareScore > maxScore){
				maxScore = compareScore;
				myMove = move;
			}	 
		}
		System.out.println("Move Made: "+myMove);
		return myMove;
	}
	
	
	/**
	 * @description: 
	 * Recursively generate all the moves that pacman can make and pick the maximum score
	 * and generate all the moves that ghosts can make and pick the minimum score from
	 * those game states. Depth can be provided to mention how many nodes or game states 
	 * to be evaluated in order to get best score.
	 * @param : node : each move the pacman makes from first level.
	 * @param : depth : how many games states to be evaluated
	 * @param : maxPlayer : collect max score for pacman or ghost
	 * @return : bestScore : which contains the best score in order to avoid ghosts.
	 */
	public static int minmaxFunction(GameNode node, int depth, boolean maxPlayer){

		if (depth == 0 || node.state.gameOver())
			return heuristicFunction(node);

		if(maxPlayer){
			ArrayList<GameNode> childGameNodesPacman = generatePacmanChildren(node);
			int bestScore = Integer.MIN_VALUE;

			for(GameNode childNode : childGameNodesPacman){
				int newScore = minmaxFunction(childNode,depth-1,false);
				bestScore = Math.max(bestScore,newScore);
			}
			return bestScore;
		}
		else{
			ArrayList<GameNode> childGameNodesGhosts = generateGhostChildren(node);
			int bestScore = Integer.MAX_VALUE;
			for(GameNode childNode : childGameNodesGhosts){
				int newScore = minmaxFunction(childNode,depth-1,true);
				bestScore = Math.min(bestScore,newScore);
			}
			return bestScore;
		}
	}
	
	/**
	 * @description: 
	 * Generate all the nodes when pacman makes its move and update game state.
	 * @param : node : game node 
	 * @return : childGameNodes : game nodes generated when pacman makes move.
	 */
	private static ArrayList<GameNode> generatePacmanChildren(GameNode node) {

		ArrayList<GameNode> childGameNodes = new ArrayList<GameNode>();

		for(MOVE move : node.state.getPossibleMoves(node.pillIndex)){
			Game newGame = node.state.copy();
			newGame.updatePacMan(move);
			newGame.updateGame();
			int pillIndex = newGame.getPacmanCurrentNodeIndex();	
			ArrayList<MOVE> moves = new ArrayList<MOVE>();
			moves.addAll(node.action);
			moves.add(move);

			GameNode newNode = new GameNode(pillIndex,node,moves,newGame);
			childGameNodes.add(newNode);
			try{
				GameView.addLines(newGame, Color.GREEN, newNode.pillIndex, newGame.getNeighbour(newNode.pillIndex, node.action.get(0)));
			}
			catch(Exception e){
				continue;
			}
		}
		return childGameNodes;
	}

	/**
	 * @description: 
	 * Generate all the nodes when ghost makes its move and update game state.
	 * @param : node : game node 
	 * @return : childGameNodes : game nodes generated when ghosts makes move.
	 */
	private static ArrayList<GameNode> generateGhostChildren(GameNode node) {
		// generate all children nodes from that root node.
		// for each move the current node makes.
		// 		for each move the ghost1 can make.
		//			for each move the ghost2 can make.
		//				for each move the ghost3 can make.
		//					for each move the ghost4 can make.

		ArrayList<GameNode> childGameNodes = new ArrayList<GameNode>();

		int blinkyIndex = node.state.getGhostCurrentNodeIndex(Constants.GHOST.BLINKY);
		int inkyIndex = node.state.getGhostCurrentNodeIndex(Constants.GHOST.INKY);
		int pinkyIndex = node.state.getGhostCurrentNodeIndex(Constants.GHOST.PINKY);
		int sueIndex = node.state.getGhostCurrentNodeIndex(Constants.GHOST.SUE);

		MOVE[] bmArray = node.state.getPossibleMoves(blinkyIndex, node.state.getGhostLastMoveMade(Constants.GHOST.BLINKY));
		ArrayList<MOVE> bmList = new ArrayList<MOVE>(Arrays.asList(bmArray));
		if(bmList.size() == 0){
			bmList.add(MOVE.NEUTRAL);
		}

		MOVE[] imArray = node.state.getPossibleMoves(inkyIndex, node.state.getGhostLastMoveMade(Constants.GHOST.INKY));
		ArrayList<MOVE> imList = new ArrayList<MOVE>(Arrays.asList(imArray));
		if(imList.size() == 0){
			imList.add(MOVE.NEUTRAL);
		}

		MOVE[] pmArray = node.state.getPossibleMoves(pinkyIndex, node.state.getGhostLastMoveMade(Constants.GHOST.PINKY));
		ArrayList<MOVE> pmList = new ArrayList<MOVE>(Arrays.asList(pmArray));
		if(pmList.size() == 0){
			pmList.add(MOVE.NEUTRAL);
		}

		MOVE[] smArray = node.state.getPossibleMoves(sueIndex, node.state.getGhostLastMoveMade(Constants.GHOST.SUE));
		ArrayList<MOVE> smList = new ArrayList<MOVE>(Arrays.asList(smArray));
		if(smList.size() == 0){
			smList.add(MOVE.NEUTRAL);
		}
		//	for each move the Blinky ghost1 can make.
		for(MOVE moveG1 : bmList){
			//	for each move the Inky ghost2 can make.
			for(MOVE moveG2 : imList){
				//	for each move Pinky the ghost3 can make.
				for(MOVE moveG3 : pmList){
					//	for each move Sue the ghost4 can make.
					for(MOVE moveG4 : smList){

						EnumMap<GHOST, MOVE> ghostMoves = new EnumMap<GHOST, MOVE>(GHOST.class);
						ghostMoves.put(GHOST.BLINKY, moveG1);
						ghostMoves.put(GHOST.INKY, moveG2);
						ghostMoves.put(GHOST.PINKY, moveG3);
						ghostMoves.put(GHOST.SUE, moveG4);
						Game newGame = node.state.copy();
						newGame.updateGhosts(ghostMoves);
						newGame.updateGame();

						//int newScore = node.state.getScore();
						int pillIndex = newGame.getPacmanCurrentNodeIndex();	

						GameNode newNode = new GameNode(pillIndex,node,node.action,newGame);
						//GameView.addLines(node.state, Color.GREEN, node.pillIndex, node.state.getNeighbour(node.pillIndex, node.action.get(0)));
						childGameNodes.add(newNode);
					}		
				}
			}
		}
		return childGameNodes;
	}
	
	/**
	 * @description: 
	 * Heuristic here is based on two factors.
	 * 1) game.getScore() which is generated as pacman eats up the next available pill.
	 * -- Score has been factored to be multiple of 50 to break the tie when both alternative
	 *	movements between pacman indexes provides same score.
	 * 2) distance between the pacman and each of the ghosts.
	 * -- If pacman is at a distance less than SAFE_DISTANCE, score for that particular will
	 * be penalized.
	 * @param : node : each move the pacman makes from first level.
	 * @return : bestScore : which contains the best score in order to avoid ghosts.
	 */
	private static int heuristicFunction(GameNode node) {

		Game game = node.state;
		int score = game.getScore();
		score = score * 50;

		score = score - tieBreaker(game.getPacmanCurrentNodeIndex(),game);

		int blinkyIndex = game.getGhostCurrentNodeIndex(Constants.GHOST.BLINKY);
		int inkyIndex = game.getGhostCurrentNodeIndex(Constants.GHOST.INKY);
		int pinkyIndex = game.getGhostCurrentNodeIndex(Constants.GHOST.PINKY);
		int sueIndex = game.getGhostCurrentNodeIndex(Constants.GHOST.SUE);

		int blinkyDistance = game.getManhattanDistance(node.pillIndex, blinkyIndex);
		int inkyDistance = game.getManhattanDistance(node.pillIndex, inkyIndex);
		int pinkyDistance = game.getManhattanDistance(node.pillIndex, pinkyIndex);
		int sueDistance = game.getManhattanDistance(node.pillIndex, sueIndex);

		if(blinkyDistance < SAFE_DISTANCE || inkyDistance < SAFE_DISTANCE 
				|| pinkyDistance < SAFE_DISTANCE || sueDistance < SAFE_DISTANCE){
			score = score - PENALTY_SCORE;
		}
		else if(game.getGhostEdibleTime(GHOST.BLINKY) > 0 && game.getGhostEdibleTime(GHOST.INKY) > 0 &&
				game.getGhostEdibleTime(GHOST.PINKY) > 0 && game.getGhostEdibleTime(GHOST.SUE) > 0){
			score = score + PENALTY_SCORE;
		}
		if(game.wasPacManEaten()){
			score = 0;
		}
		return score;

	}

	/**
	 * @description: 
	 * to break the tie when both alternative movements between pacman indexes provides 
	 * same score.
	 * @param : currentIndex : pacman current index
	 * @param : gameCopy : game state to be evaluated.
	 * @return : nearestPillDist : distance to nearest pill with respect to pacman
	 */
	private static int tieBreaker(int currentIndex, Game gameCopy) {

		ArrayList<Integer> allAvailablePills = loadAvailablePills(gameCopy);
		int closestPill = 0;
		int minDist = Integer.MAX_VALUE;
		for(Integer pill : allAvailablePills){
			int distance = gameCopy.getShortestPathDistance(currentIndex, pill);
			if(distance < minDist){
				minDist = distance;
				closestPill = pill;
			}
		}
		int nearestPillDist = gameCopy.getShortestPathDistance(gameCopy.getPacmanCurrentNodeIndex(), closestPill);
		return nearestPillDist;
	}

	
	/**
	 * @description: 
	 * load all the available pills on the current maze.
	 * @param : gameCopy : game state to be evaluated.
	 * @return : targets : arraylist containing all the available pills
	 */
	private static ArrayList<Integer> loadAvailablePills(Game gameCopy) {
		int[] pills = gameCopy.getPillIndices();
		int[] powerPills = gameCopy.getPowerPillIndices();

		ArrayList<Integer> targets = new ArrayList<Integer>();

		for (int i = 0; i < pills.length; i++) {
			//check which pills are available
			Boolean pillStillAvailable = gameCopy.isPillStillAvailable(i);
			if (pillStillAvailable == null) continue;
			if (gameCopy.isPillStillAvailable(i)) {
				targets.add(pills[i]);
			}
		}

		for (int i = 0; i < powerPills.length; i++) {            //check with power pills are available
			Boolean pillStillAvailable = gameCopy.isPillStillAvailable(i);
			if (pillStillAvailable == null) continue;
			if (gameCopy.isPowerPillStillAvailable(i)) {
				targets.add(powerPills[i]);
			}
		}
		return targets;
	}

}