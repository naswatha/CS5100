package entrants.pacman.naveenaswa;
 
 import java.util.HashSet;
 import java.util.LinkedHashSet;
 
 
 import pacman.controllers.PacmanController;
 import pacman.game.Constants.DM;
 import pacman.game.Constants.MOVE;
 import pacman.game.Game;
 
 /*
  * This is the class you need to modify for your entry. In particular, you need to
  * fill in the getMove() method. Any additional classes you write should either
  * be placed in this package or sub-packages (e.g., entrants.pacman.username).
  */
 public class BFSMyPacMan extends PacmanController {
     private MOVE myMove = MOVE.NEUTRAL;
     
     // contains all the pills that has been visited
     private static HashSet<Integer> visitedPills = new HashSet<Integer>();
     // contains all the pills that should be visited acts like a Queue in BFS.
     private static LinkedHashSet<Integer> toBeVisitedPills = new LinkedHashSet<Integer>();
     
     //Place your game logic here to play the game as Ms Pac-Man
     public MOVE getMove(Game game, long timeDue) {
         
     	// get the current index of pacman
     	int current = game.getPacmanCurrentNodeIndex();
     	// get all the observable pills from current pacman position.
         int[] observablePills = game.getPillIndices();
    
         // add current pill to visited pills
         visitedPills.add(current);
         
         // enqueue all observablePills to toBeVisited only if they are not already visited
         for(int i = 0; i < observablePills.length; i++){
         	if(!visitedPills.contains(observablePills[i])){
         		toBeVisitedPills.add(observablePills[i]);
         	}
         }
 
         // since pacman eats up all the pills on the way add those pill indices to visited.
         if(toBeVisitedPills.contains(current)){
         	toBeVisitedPills.remove(current);
         }
         
         // until pacman reaches target pill do not dequeue
         int targetPill = 0;
         for(Integer pill: toBeVisitedPills) {
         	targetPill = pill;
             break;
         }
         if(current == targetPill){
         	toBeVisitedPills.remove(targetPill);
         }
 
         return game.getNextMoveTowardsTarget(current,targetPill, DM.PATH);
     }
 }
 