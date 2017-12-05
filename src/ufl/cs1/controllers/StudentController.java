package ufl.cs1.controllers;
// This is luke
import game.controllers.DefenderController;
import game.models.Defender;
import game.models.Game;
import game.models.Node;

import java.util.List;

public final class StudentController implements DefenderController
{
	Game currentGameState;
	Game previousGameState;
	public void init(Game game) { }

	public void shutdown(Game game) { }

	public int[] update(Game game,long timeDue)
	{
		this.currentGameState = game;
		if (this.previousGameState == null){
			this.previousGameState = this.currentGameState;
		}
		int[] actions = new int[Game.NUM_DEFENDER];
		List<Defender> enemies = game.getDefenders();

		//Chooses a random LEGAL action if required. Could be much simpler by simply returning
		//any random number of all of the ghosts
		for(int i = 0; i < actions.length; i++)
		{
			Defender defender = enemies.get(i);
			List<Integer> possibleDirs = defender.getPossibleDirs();
			if (possibleDirs.size() != 0)
				actions[i]=possibleDirs.get(Game.rng.nextInt(possibleDirs.size()));
			else
				actions[i] = -1;
		}
		//gave the four ghosts individual methods and called them with this
		actions[0] = ghostOneAction(timeDue);
		actions[1] = ghostTwoAction(timeDue);
		actions[2] = ghostThreeAction(timeDue);
		actions[3] = ghostFourAction(timeDue);
		return actions;
	}
	//methods for each individual ghost, one can be a repeat because we are three people and not four.
	//we have to make each one different, but i made all the same to start just becacuse
	public int ghostOneAction(long timeMs){
		if (StudentController.this.currentGameState.getDefender(0).getLocation().getY() == 0){
			return lair(0);
		}
		else if (StudentController.this.currentGameState.getDefender(0).isVulnerable()){
			return frightened(0);
		}
		else{
			return chase(0);
		}
	}
	public int ghostTwoAction(long timeMs){
		if (StudentController.this.currentGameState.getDefender(1).isVulnerable()){
			return frightened(1);
		}
		else{
			return chase(1);
		}
	}
	public int ghostThreeAction(long timeMs){
		if (StudentController.this.currentGameState.getDefender(2).isVulnerable()){
			return frightened(2);
		}
		else{
			return chase(2);
		}
	}
	public int ghostFourAction(long timeMs){
		if (StudentController.this.currentGameState.getDefender(3).isVulnerable()){
			return frightened(3);
		}
		else{
			return chase(3);
		}
	}
	//lair method
	public int lair(int ghostId){
		if (StudentController.this.currentGameState.getDefender(ghostId).getLairTime() > 0)
			return -1;
		else
			return 0; // this is moving upwards
	}
	//frightened method
	public int frightened(int ghostId){
		Node target = StudentController.this.currentGameState.getAttacker().getLocation();
		if(StudentController.this.previousGameState.getAttacker().getLocation().isPowerPill() || StudentController.this.currentGameState.getDefender(ghostId).getVulnerableTime() > 0)
			return StudentController.this.currentGameState.getDefender(ghostId).getNextDir(target, false); // some movement away from Pacman
		return 2;
	}
	//chase method
	public int chase(int ghostId){
		int direction = 0;
		Node target = StudentController.this.currentGameState.getAttacker().getLocation();
		int pacDir = StudentController.this.currentGameState.getAttacker().getDirection();
		Node defenderLoc = StudentController.this.currentGameState.getDefender(ghostId).getLocation();
		int pacDirection = StudentController.this.currentGameState.getAttacker().getDirection();
		StudentController.this.currentGameState.getDefender(ghostId).getPossibleDirs();
		Game game = this.currentGameState;
		List<Defender> ghosts = game.getDefenders();
		Defender defender = ghosts.get(ghostId);
		List<Integer> possibleDirs = defender.getPossibleDirs();
		direction = StudentController.this.currentGameState.getDefender(ghostId).getNextDir(target, true);
		if (StudentController.this.currentGameState.getAttacker().getPossibleDirs(false).contains(pacDir)) {
			direction = StudentController.this.currentGameState.getDefender(ghostId).getNextDir(target.getNeighbor(pacDir), true);
		}
		/*
		int targetX = StudentController.this.currentGameState.getAttacker().getLocation().getX();
		int targetY = StudentController.this.currentGameState.getAttacker().getLocation().getY();
		int ghostX = defenderLoc.getX();
		int ghostY = defenderLoc.getY();
		int diffX = targetX - ghostX;
		int diffY = targetY - ghostY;
		//System.out.println(diffX);
		//System.out.println(diffY);
		//gets the next direction of the ghost based on the position of postition of pacman on a coordinate plane
		//it will find the x and y distance between the pacman and ghost and decide where to go based on possible moves
		if (diffX == 0){
			if (diffY > 0){
				direction = 0;
			}
			else{
				direction = 2;
			}
		}
		else if (diffY == 0){
			if (diffX > 0){
				direction = 1;
			}
			else{
				direction = 3;
			}
		}
		else{
			double ratio = diffY / diffX;
			if (ratio > 0) {
				//quadrant 1
				if (diffX > 0) {
					if (ratio > 1) {
						if (possibleDirs.contains(0)) {
							direction = 0;
						} else if (possibleDirs.contains(1)) {
							direction = 1;
						} else {
							direction = 3;
						}
					} else {
						if (possibleDirs.contains(1)) {
							direction = 1;
						} else if (possibleDirs.contains(0)) {
							direction = 0;
						} else {
							direction = 2;
						}
					}
				}
				//quadrant 3
				else {
					if (ratio > 1) {
						if (possibleDirs.contains(2)) {
							direction = 2;
						} else if (possibleDirs.contains(3)) {
							direction = 3;
						} else {
							direction = 1;
						}
					} else {
						if (possibleDirs.contains(3)) {
							direction = 3;
						} else if (possibleDirs.contains(2)) {
							direction = 2;
						} else {
							direction = 0;
						}
					}
				}
			} else {
				//quadrant 4
				if (diffX > 0) {
					if (ratio < -1) {
						if (possibleDirs.contains(2)) {
							direction = 2;
						} else if (possibleDirs.contains(1)) {
							direction = 1;
						} else {
							direction = 3;
						}
					} else {
						if (possibleDirs.contains(1)) {
							direction = 1;
						} else if (possibleDirs.contains(2)) {
							direction = 2;
						} else {
							direction = 3;
						}
					}
				}
				//quadrant 2
				else {
					if (ratio < -1) {
						if (possibleDirs.contains(0)) {
							direction = 0;
						} else if (possibleDirs.contains(3)) {
							direction = 3;
						} else {
							direction = 1;
						}
					} else {
						if (possibleDirs.contains(3)) {
							direction = 3;
						} else if (possibleDirs.contains(0)) {
							direction = 0;
						} else {
							direction = 1;
						}
					}
				}

			}
		}*/
		return direction;


	}
	//enum of states, havent used yet
	enum states{
		frightened,
		lair,
		chase;
		private states(){

		}
	}

}