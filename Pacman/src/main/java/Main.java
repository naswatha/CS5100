import examples.commGhosts.POCommGhosts;
import pacman.Executor;
import pacman.controllers.examples.RandomGhosts;
import entrants.pacman.naveenaswa.*;
//import examples.poPacMan.POPacMan;
import entrants.pacman.naveenaswa.minmax.MinMax;

/**
 * Created by pwillic on 06/05/2016.
 * 
 * Maintained and modified by Naveen from 07/15/2016
 */
public class Main {

    public static void main(String[] args) {

        Executor executor = new Executor(false, true);

        // executor.runGameTimed(new POPacMan(), new POCommGhosts(50), true);
        // executor.runGameTimed(new BFSPacman(), new POCommGhosts(50), true);
        // executor.runGameTimed(new GreedyBFSPacman(), new POCommGhosts(50), true);
         executor.runGame(new MinMax(), new POCommGhosts(50), true, 40);
    }
}
