package entrants.pacman.naveenaswa.minmax;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;

import examples.commGhosts.POCommGhosts;
import pacman.controllers.PacmanController;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Constants.GHOST;
import pacman.game.Constants;
import pacman.game.Game;
import pacman.game.Constants.MOVE;

public class MinMax extends PacmanController {

	public static int tick = 0;

	@Override
	public MOVE getMove(Game game, long timeDue) {	

		tick++;
		System.out.println("TICK : "+tick);
		int currentIndex = game.getPacmanCurrentNodeIndex(); 
		Game gameCopy = game.copy();
		MOVE myMove = MOVE.NEUTRAL;

		int maxScore = Integer.MIN_VALUE;
		int compareScore;
		
		

		int blinkyIndex = gameCopy.getGhostCurrentNodeIndex(Constants.GHOST.BLINKY);
		int inkyIndex = gameCopy.getGhostCurrentNodeIndex(Constants.GHOST.INKY);
		int pinkyIndex = gameCopy.getGhostCurrentNodeIndex(Constants.GHOST.PINKY);
		int sueIndex = gameCopy.getGhostCurrentNodeIndex(Constants.GHOST.SUE);
		
		MOVE[] bmArray = gameCopy.getPossibleMoves(blinkyIndex, gameCopy.getGhostLastMoveMade(Constants.GHOST.BLINKY));
		ArrayList<MOVE> bmList = new ArrayList<MOVE>(Arrays.asList(bmArray));
		if(bmList.size() == 0){
			bmList.add(MOVE.NEUTRAL);
		}
		
		MOVE[] imArray = gameCopy.getPossibleMoves(inkyIndex, gameCopy.getGhostLastMoveMade(Constants.GHOST.INKY));
		ArrayList<MOVE> imList = new ArrayList<MOVE>(Arrays.asList(imArray));
		if(imList.size() == 0){
			imList.add(MOVE.NEUTRAL);
		}
		
		MOVE[] pmArray = gameCopy.getPossibleMoves(pinkyIndex, gameCopy.getGhostLastMoveMade(Constants.GHOST.PINKY));
		ArrayList<MOVE> pmList = new ArrayList<MOVE>(Arrays.asList(pmArray));
		if(pmList.size() == 0){
			pmList.add(MOVE.NEUTRAL);
		}
		
		MOVE[] smArray = gameCopy.getPossibleMoves(sueIndex, gameCopy.getGhostLastMoveMade(Constants.GHOST.SUE));
		ArrayList<MOVE> smList = new ArrayList<MOVE>(Arrays.asList(smArray));
		if(smList.size() == 0){
			smList.add(MOVE.NEUTRAL);
		}
		
//		System.out.println("blinky : "+blinkyIndex);
//		System.out.println(gameCopy.getGhostLastMoveMade(Constants.GHOST.BLINKY));
//		 MOVE[] moves = gameCopy.getPossibleMoves(blinkyIndex, gameCopy.getGhostLastMoveMade(Constants.GHOST.BLINKY));
//		 for(MOVE move : moves)
//			 System.out.println("can be moves to : "+move);
//		System.exit(0);
		// for each move pacman can make.
		for(MOVE move : gameCopy.getPossibleMoves(currentIndex)){
			//System.out.println("HERE1");
			//	for each move the Blinky ghost1 can make.
			for(MOVE moveG1 : bmList){
				//System.out.println("HERE2");
				//	for each move the Inky ghost2 can make.
				for(MOVE moveG2 : imList){
					//System.out.println("HERE3");
					//	for each move Pinky the ghost3 can make.
					for(MOVE moveG3 : pmList){
						//System.out.println("HERE4");
						//	for each move Sue the ghost4 can make.
						for(MOVE moveG4 : smList){
							//System.out.println("HERE5");

							EnumMap<GHOST, MOVE> ghostMoves = new EnumMap<GHOST, MOVE>(GHOST.class);
							ghostMoves.put(GHOST.BLINKY, moveG1);
							ghostMoves.put(GHOST.INKY, moveG2);
							ghostMoves.put(GHOST.PINKY, moveG3);
							ghostMoves.put(GHOST.SUE, moveG4);
							gameCopy.advanceGame(move, ghostMoves);
							//System.out.println("score :"+gameCopy.getScore());

							int pillIndex = gameCopy.getPacmanCurrentNodeIndex();
							ArrayList<MOVE> moves = new ArrayList<MOVE>();
							moves.add(move);

							GameNode newNode = new GameNode(pillIndex,null,moves,gameCopy);
							compareScore = minmaxFunction(newNode, 5, true);
							System.out.println("Compare Score: "+compareScore);
							//System.out.println("CompareScore: "+compareScore);
							if(compareScore > maxScore){
								maxScore = compareScore;
								myMove = move;
							}
						}		
					}
				}
			}
		}
		//System.out.println("MaxScore: "+maxScore);
		
//		if(tick == 1){
//			System.exit(0);
//		}

		return myMove;

	}


	public static int minmaxFunction(GameNode node, int depth, boolean maxPlayer){

		if (depth == 0 || node.state.gameOver())
			return heuristicFunction(node);

		//generate child nodes.
		ArrayList<GameNode> childGameNodes = generateChildren(node);
		//System.out.println("childGameNodes...: "+childGameNodes);
		
		if(maxPlayer){
			int bestScore = Integer.MIN_VALUE;
			for(GameNode childNode : childGameNodes){
				int newScore = minmaxFunction(childNode,depth-1,false);
				bestScore = Math.max(bestScore,newScore);
			}
			return bestScore;
		}
		else{
			int bestScore = Integer.MAX_VALUE;
			for(GameNode childNode : childGameNodes){
				int newScore = minmaxFunction(childNode,depth-1,true);
				bestScore = Math.min(bestScore,newScore);
			}
			return bestScore;
		}
	}

	private static ArrayList<GameNode> generateChildren(GameNode node) {
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
		
		
		// for each move pacman can make.
		for(MOVE move : node.state.getPossibleMoves(node.pillIndex)){
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
							node.state.advanceGame(move, ghostMoves);

							//int newScore = node.state.getScore();
							int pillIndex = node.state.getPacmanCurrentNodeIndex();
							ArrayList<MOVE> moves = new ArrayList<MOVE>();
							moves.addAll(node.action);
							moves.add(move);

							GameNode newNode = new GameNode(pillIndex,node,moves,node.state);
							childGameNodes.add(newNode);
						}		
					}
				}
			}
		}
		return childGameNodes;
	}





	// return the score based on the game state inside the 
	// node.
	private static int heuristicFunction(GameNode node) {
		return node.state.getScore();

	}
}