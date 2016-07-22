package entrants.pacman.naveenaswa;

import java.util.ArrayList;
import java.util.Comparator;
import pacman.game.Game;

import pacman.game.Constants.MOVE;

/**
 * @author Naveen
 * @description : Helper class to contain the game states
 *  
 */
public class GameNode{

	public int depth;
	public int pillIndex;
	public GameNode parent;
	public ArrayList<MOVE> action;
	public int score;
	public Game state;
	
	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public int getPillIndex() {
		return pillIndex;
	}

	public void setPillIndex(int pillIndex) {
		this.pillIndex = pillIndex;
	}

	public ArrayList<MOVE> getAction() {
		return action;
	}

	public void setAction(ArrayList<MOVE> action) {
		this.action = action;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
	/**
	 * @description : constructor
	 */
	public GameNode(int depth, int pillIndex, ArrayList<MOVE> action, int score){
		this.depth = depth;
		this.pillIndex = pillIndex;
		this.action = action;
		this.score = score;
	}
	
	/**
	 * @description : to string method for debug
	 */
	@Override
	public String toString() {
		return "GameNode [depth=" + depth + ", pillIndex=" + pillIndex
				+ ", action=" + action + ", score=" + score + "]";
	}
}


