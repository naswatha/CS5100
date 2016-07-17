package entrants.pacman.naveenaswa;

import java.awt.List;
import java.util.ArrayList;
import java.util.HashMap;

import pacman.controllers.PacmanController;
import pacman.game.Constants.DM;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getMove() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., entrants.pacman.username).
 */
public class MyPacMan extends PacmanController {
    private MOVE myMove = MOVE.NEUTRAL;

    private static ArrayList<Integer> visitedPills = new ArrayList<Integer>();
    private static ArrayList<Integer> toBeVisitedPills = new ArrayList<Integer>();
    
    public MOVE getMove(Game game, long timeDue) {
        //Place your game logic here to play the game as Ms Pac-Man
    	
    	    	
    	int current = game.getPacmanCurrentNodeIndex();
    	// provides only pill indices for line of sight.
    	// that is if x or y co-ordiante of pacman and pill match
    	// and there is no obstacle.
        int[] observablePills = game.getPillIndices();
        int[] powerPills = game.getPowerPillIndices();
        int[] allPills = game.getCurrentMaze().pillIndices;    
        

        // get all observable pills
        // put these pills into toBeVisitedPills at the end of arraylist like queue
        // insert current pill to visited.
        
        // move pacman to the next pill available in the toBeVisitedPills 
        // get all observable pills
        // put these pills into toBeVisitedPills at the end of arraylist 
        	// check if the pills are already present in the toBeVisitedPills 
        	// if present ignore
        	// else insert at the end of the arraylist.
        // insert current pill to visited.
        
        
        for(int i = 0; i < observablePills.length; i++){
        	
        	toBeVisitedPills.add(observablePills[i]);
        }
        
        


        return myMove;
    	
        


        

        
    }
}