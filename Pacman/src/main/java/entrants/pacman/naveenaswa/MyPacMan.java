package entrants.pacman.naveenaswa;

import pacman.controllers.PacmanController;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getMove() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., entrants.pacman.username).
 */
public class MyPacMan extends PacmanController {
    private MOVE myMove = MOVE.NEUTRAL;

    public MOVE getMove(Game game, long timeDue) {
        //Place your game logic here to play the game as Ms Pac-Man
    	
    	// Should always be possible as we are PacMan
        int current = game.getPacmanCurrentNodeIndex();
        //System.out.println("Current Position"+current);
        int[] pills = game.getPillIndices();
        int[] powerPills = game.getPowerPillIndices();
        
        System.out.println("Pill indices");
        for(int i = 0;i < pills.length; i++){
        	System.out.println(+pills[i]);
        }
        

        return myMove;
    }
}