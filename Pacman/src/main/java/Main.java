import examples.commGhosts.POCommGhosts;
import pacman.Executor;
import entrants.pacman.naveenaswa.*;
//import examples.poPacMan.POPacMan;

/**
 * Created by pwillic on 06/05/2016.
 * 
 * Maintained and modified by Naveen from 07/15/2016
 */
public class Main {

    public static void main(String[] args) {

        Executor executor = new Executor(true, true);

        //executor.runGameTimed(new POPacMan(), new POCommGhosts(50), true);
        executor.runGameTimed(new BFSMyPacMan(), new POCommGhosts(50), true);
        //executor.runGameTimed(new GreedyBFSMyPacMan(), new POCommGhosts(50), true);
    }
}
