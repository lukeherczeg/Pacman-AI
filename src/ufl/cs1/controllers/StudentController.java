package ufl.cs1.controllers;

import game.controllers.DefenderController;
import game.models.Game;
import game.models.Node;

import java.util.List;

public final class StudentController implements DefenderController
{
	// Up - Right - Down - Left -> 0 - 1 - 2 - 3
	// Blinky - Pinky - Clyde - Inky -> 0 - 1 - 2 - 3

	private Game currentGameState;
	private Game previousGameState;

	private final int noAction = -1;
	int up = 0;
    int right = 1;
    int down = 2;
    int left = 3;

	private final int Blinky = 0;
	private final int Pinky = 1;
	private final int Clyde = 2;
	private final int Inky = 3;
	
	public void init(Game game) { }

	public void shutdown(Game game) { }

	public int[] update(Game game,long timeDue)
	{
		this.currentGameState = game;
		if (this.previousGameState == null){
			this.previousGameState = this.currentGameState;
		}
		int[] actions = new int[Game.NUM_DEFENDER];

		//gave the four ghosts individual methods and called them with this
		actions[Blinky] = ghostOneAction(timeDue);
		actions[Pinky] = ghostTwoAction(timeDue);
		actions[Clyde] = ghostThreeAction(timeDue);
		actions[Inky] = ghostFourAction(timeDue);

		this.previousGameState = this.currentGameState;
		return actions;
	}
	//methods for each individual ghost, one can be a repeat because we are three people and not four.
	//we have to make each one different, but i made all the same to start just becacuse
	private int ghostOneAction(long timeMs){
		if(StudentController.this.currentGameState.getDefender(0).isVulnerable()){
			return frightened(Blinky);
		}
		else{
			return chase(Blinky);
		}
	}
	private int ghostTwoAction(long timeMs){
		if (StudentController.this.currentGameState.getDefender(1).isVulnerable()){
			return frightened(Pinky);
		}
		else{
			return chase(Pinky);
		}
	}
	private int ghostThreeAction(long timeMs){
		if (StudentController.this.currentGameState.getDefender(2).isVulnerable()){
			return frightened(Clyde);
		}
		else{
			return chase(Clyde);
		}
	}
	private int ghostFourAction(long timeMs){
		if (StudentController.this.currentGameState.getDefender(3).isVulnerable()){
			return frightened(Inky);
		}
		else{
			return chase(Inky);
		}
	}

	//frightened method for when powerpill is eaten. Once ghosts get far enough away, they will come back since they don't want to be too far to hunt him when the frightened state ends
	private int frightened(int ghostId){
		Node pacman = StudentController.this.currentGameState.getAttacker().getLocation();
		if(StudentController.this.previousGameState.getAttacker().getLocation().isPowerPill() || StudentController.this.currentGameState.getDefender(ghostId).getVulnerableTime() > 0)
			if (pacman.getPathDistance(StudentController.this.currentGameState.getDefender(ghostId).getLocation()) < 30)  // tested between 0-100
				return StudentController.this.currentGameState.getDefender(ghostId).getNextDir(pacman, false); // some movement away from pacman\
			else
				return StudentController.this.currentGameState.getDefender(ghostId).getNextDir(pacman, true);
		return noAction;
	}

	//chase method
	private int chase(int ghostId) {
		int direction = noAction;

		Node pacmanLast = StudentController.this.previousGameState.getAttacker().getLocation();
		Node pacman = StudentController.this.currentGameState.getAttacker().getLocation();
		int pacDir = StudentController.this.currentGameState.getAttacker().getDirection();

		if ((ghostId == Blinky || ghostId == Pinky) && StudentController.this.currentGameState.getAttacker().getPossibleDirs(false).contains(pacDir)) {
			direction = StudentController.this.currentGameState.getDefender(ghostId).getNextDir(pacman.getNeighbor(pacDir), true);
		}                 // aims in front of pacman

		if (ghostId == Clyde)   // aims behind pacman
			direction = StudentController.this.currentGameState.getDefender(ghostId).getNextDir(pacmanLast, true);

		if (ghostId == Inky)  // aim straight at pacman
			direction = StudentController.this.currentGameState.getDefender(ghostId).getNextDir(pacman, true);

		if (ghostId == Blinky || ghostId == Pinky || ghostId == Clyde) {    // makes all ghosts but 1 flee when pacman is close to a power pill, and one goes towards him to force him to eat it.
			List<Node> powerups = StudentController.this.currentGameState.getPowerPillList();
			for (int i = 0; i < powerups.size(); i++) {
				if (pacman.getPathDistance(powerups.get(i)) < 20) {
					direction = StudentController.this.currentGameState.getDefender(ghostId).getNextDir(pacman, false);
				}
			}
		}

		return direction;
	}

}