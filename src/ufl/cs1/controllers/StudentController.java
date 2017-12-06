package ufl.cs1.controllers;
// This is luke, fellows.
/*
* We have to make the frightened method and optimize, based on the distance of the path ---- ----- ---- --- -- right here
* //pacman.getPathDistance(StudentController.this.currentGameState.getDefender(ghostId).getLocation());  can be used as a comparison for the frightened method
* so that the ghosts run TOWARDS him anytime they are too far away
*
* Also, we have to make it so that one ghost attacks and the other runs when pacman's proximity to a powerpill is
* less than a certain number, once again optimize that.
*
* Probably, put that code in the chase function, with an if statement for the ghost you want to affect (doesn't matter just let us know)
* I have an example in chase()
*
*
 */
import game.controllers.DefenderController;
import game.models.Defender;
import game.models.Game;
import game.models.Node;

import java.util.List;

public final class StudentController implements DefenderController
{

	// Up - Right - Down - Left -> 0 - 1 - 2 - 3
	Game currentGameState;
	Game previousGameState;
	int up = 0; int right = 1; int down = 2; int left = 3; int neutral = -1;

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

		//gave the four ghosts individual methods and called them with this
		actions[0] = ghostOneAction(timeDue);
		actions[1] = ghostTwoAction(timeDue);
		actions[2] = ghostThreeAction(timeDue);
		actions[3] = ghostFourAction(timeDue);

		this.previousGameState = this.currentGameState;
		return actions;
	}
	//methods for each individual ghost, one can be a repeat because we are three people and not four.
	//we have to make each one different, but i made all the same to start just becacuse
	private int ghostOneAction(long timeMs){
		if (StudentController.this.currentGameState.getDefender(0).getLocation().getY() == 0) {
			return lair(0);
		}
		if(StudentController.this.currentGameState.getDefender(0).isVulnerable()){
			return frightened(0);
		}
		else{
			return chase(0);
		}
	}
	private int ghostTwoAction(long timeMs){
		if (StudentController.this.currentGameState.getDefender(1).isVulnerable()){
			return frightened(1);
		}
		else{
			return chase(1);
		}
	}
	private int ghostThreeAction(long timeMs){
		if (StudentController.this.currentGameState.getDefender(2).isVulnerable()){
			return frightened(2);
		}
		else{
			return chase(2);
		}
	}
	private int ghostFourAction(long timeMs){
		if (StudentController.this.currentGameState.getDefender(3).isVulnerable()){
			return frightened(3);
		}
		else{
			return chase(3);
		}
	}

	//frightened method
	private int frightened(int ghostId){
		Node pacman = StudentController.this.currentGameState.getAttacker().getLocation();
		if(StudentController.this.previousGameState.getAttacker().getLocation().isPowerPill() || StudentController.this.currentGameState.getDefender(ghostId).getVulnerableTime() > 0)
			if(pacman.getPathDistance(StudentController.this.currentGameState.getDefender(ghostId).getLocation()) < 30)  // tested between 0-100
			return StudentController.this.currentGameState.getDefender(ghostId).getNextDir(pacman, false); // some movement away from pacman
		return neutral;
	}

	private int lair(int ghostId)
	{
		if(StudentController.this.currentGameState.getDefender(ghostId).getLairTime() > 0)
			return neutral;
		else
			return up;
	}

	//chase method
	private int chase(int ghostId) {
		int direction = neutral;

		Node pacmanLast = StudentController.this.previousGameState.getAttacker().getLocation();
		Node pacman = StudentController.this.currentGameState.getAttacker().getLocation();
		int pacDir = StudentController.this.currentGameState.getAttacker().getDirection();
		StudentController.this.currentGameState.getDefender(ghostId).getPossibleDirs();
		Game game = this.currentGameState;
		List<Defender> ghosts = game.getDefenders();
		Defender defender = ghosts.get(ghostId);
		List<Integer> possibleDirs = defender.getPossibleDirs();

		if ((ghostId == 0 || ghostId == 1) && StudentController.this.currentGameState.getAttacker().getPossibleDirs(false).contains(pacDir)) {
			direction = StudentController.this.currentGameState.getDefender(ghostId).getNextDir(pacman.getNeighbor(pacDir), true);
		}                 // aims in front of pacman

		if (ghostId == 2)   // aims behind pacman
			direction = StudentController.this.currentGameState.getDefender(ghostId).getNextDir(pacmanLast, true);

		if (ghostId == 3)  // aim straight at pacman
			direction = StudentController.this.currentGameState.getDefender(ghostId).getNextDir(pacman, true);

		if (ghostId == 0 || ghostId == 1 || ghostId == 2) {
			List<Node> powerups = StudentController.this.currentGameState.getPowerPillList();
			for (int i = 0; i < powerups.size(); i++) {
				if (pacman.getPathDistance(powerups.get(i)) < 20) {
					direction = StudentController.this.currentGameState.getDefender(ghostId).getNextDir(pacman, false);
				}
			}
		/*if(ghostId == 0)
			System.out.println(pacman.getPathDistance(StudentController.this.currentGameState.getDefender(ghostId).getLocation()));*/
		}
			return direction;


		//enum of states, havent used yet
	}

}