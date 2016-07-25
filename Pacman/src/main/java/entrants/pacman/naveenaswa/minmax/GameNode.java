package entrants.pacman.naveenaswa.minmax;

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

	public int pillIndex;
	public ArrayList<MOVE> action;
	public int score;
	public Game state;
	public GameNode parent;

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
	
	public Game getState() {
		return state;
	}

	public void setState(Game state) {
		this.state = state;
	}

	public GameNode getParent() {
		return parent;
	}

	public void setParent(GameNode parent) {
		this.parent = parent;
	}

	
	/**
	 * @description : constructor
	 */
	public GameNode(int pillIndex,GameNode parent, ArrayList<MOVE> action, Game state){
		this.pillIndex = pillIndex;
		this.action = action;
		this.parent = parent;
		this.state = state;
	}
	
//	/**
//	 * @description : constructor
//	 */
//	public GameNode(int pillIndex,GameNode parent, ArrayList<MOVE> action, int score, Game state){
//		this.pillIndex = pillIndex;
//		this.action = action;
//		this.score = score;
//		this.parent = parent;
//		this.state = state;
//	}
//	
	/**
	 * @description : to string method for debug
	 */
	@Override
	public String toString() {
		return "GameNode [pillIndex=" + pillIndex
				+ ", action=" + action + ", score=" + score + ", state="
				+ state + ", parent=" + parent + "]";
	}

}


