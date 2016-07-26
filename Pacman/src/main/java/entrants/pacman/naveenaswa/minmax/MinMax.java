package entrants.pacman.naveenaswa.minmax;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;

import examples.commGhosts.POCommGhosts;
import pacman.controllers.PacmanController;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Constants.DM;
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

	public static int tick = 0;
	private static int PENALTY_SCORE = 500;
	private static int SAFE_DISTANCE = 2;

	@Override
	public MOVE getMove(Game game, long timeDue) {	

		tick++;	
		int currentIndex = game.getPacmanCurrentNodeIndex(); 
		Game gameCopy = game.copy();
		MOVE myMove = MOVE.NEUTRAL;
		int maxScore = Integer.MIN_VALUE;
		int compareScore;

		// for each move pacman can make.
		for(MOVE move : gameCopy.getPossibleMoves(currentIndex)){
			gameCopy.updatePacMan(move);
			int pillIndex = gameCopy.getPacmanCurrentNodeIndex();
			ArrayList<MOVE> moves = new ArrayList<MOVE>();
			moves.add(move);
			GameNode newNode = new GameNode(pillIndex,null,moves,gameCopy);
			compareScore = minmaxFunction(newNode, 4, false);
			compareScore = compareScore + tieBreaker(currentIndex, move, gameCopy);
			if(compareScore > maxScore){
				maxScore = compareScore;
				myMove = move;
			}	 
		}
		System.out.println("Move Made: "+myMove);
		//	if(tick == 2){
		//		System.exit(0);
		//	}

		return myMove;
	}

	// return the score based on the game state inside the 
	// node.
	private static int heuristicFunction(GameNode node) {

		int score = node.state.getScore();
		score = score * 5;

		int blinkyIndex = node.state.getGhostCurrentNodeIndex(Constants.GHOST.BLINKY);
		int inkyIndex = node.state.getGhostCurrentNodeIndex(Constants.GHOST.INKY);
		int pinkyIndex = node.state.getGhostCurrentNodeIndex(Constants.GHOST.PINKY);
		int sueIndex = node.state.getGhostCurrentNodeIndex(Constants.GHOST.SUE);

		int blinkyDistance = node.state.getManhattanDistance(node.pillIndex, blinkyIndex);
		int inkyDistance = node.state.getManhattanDistance(node.pillIndex, inkyIndex);
		int pinkyDistance = node.state.getManhattanDistance(node.pillIndex, pinkyIndex);
		int sueDistance = node.state.getManhattanDistance(node.pillIndex, sueIndex);

		if(blinkyDistance < SAFE_DISTANCE || inkyDistance < SAFE_DISTANCE 
				|| pinkyDistance < SAFE_DISTANCE || sueDistance < SAFE_DISTANCE){
			score = score - PENALTY_SCORE;
		}

		return score;

	}

	private int tieBreaker(int currentIndex, MOVE move, Game gameCopy) {

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
		int tie = 0;
		if(move == gameCopy.getNextMoveTowardsTarget(currentIndex, closestPill, DM.PATH)){
			tie = 100;
		}
		gameCopy.updatePacMan(move);

		if(move == gameCopy.getNextMoveTowardsTarget(gameCopy.getPacmanCurrentNodeIndex(), closestPill, DM.PATH)){
			tie = tie+100;
		}
		return tie;
	}

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

	private static ArrayList<GameNode> generatePacmanChildren(GameNode node) {

		ArrayList<GameNode> childGameNodes = new ArrayList<GameNode>();

		for(MOVE move : node.state.getPossibleMoves(node.pillIndex)){
			node.state.updatePacMan(move);
			int pillIndex = node.state.getPacmanCurrentNodeIndex();	
			ArrayList<MOVE> moves = new ArrayList<MOVE>();
			moves.addAll(node.action);
			moves.add(move);
			Game newCopy = node.state.copy();

			GameNode newNode = new GameNode(pillIndex,node,moves,newCopy);
			childGameNodes.add(newNode);
			try{
				GameView.addLines(newNode.state, Color.GREEN, newNode.pillIndex, newNode.state.getNeighbour(newNode.pillIndex, node.action.get(0)));
			}
			catch(Exception e){
				continue;
			}
		}
		return childGameNodes;
	}


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
						node.state.updateGhosts(ghostMoves);
						Game newCopy = node.state.copy();

						//int newScore = node.state.getScore();
						int pillIndex = node.state.getPacmanCurrentNodeIndex();	

						GameNode newNode = new GameNode(pillIndex,node,node.action,newCopy);
						//GameView.addLines(node.state, Color.GREEN, node.pillIndex, node.state.getNeighbour(node.pillIndex, node.action.get(0)));
						childGameNodes.add(newNode);
					}		
				}
			}
		}
		return childGameNodes;
	}

	private ArrayList<Integer> loadAvailablePills(Game gameCopy) {
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