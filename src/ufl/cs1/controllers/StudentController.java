package ufl.cs1.controllers;

import game.controllers.DefenderController;
import game.models.Attacker;
import game.models.Defender;
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
		Node pacmanLocation = StudentController.this.currentGameState.getAttacker().getLocation();
		Defender ghost = StudentController.this.currentGameState.getDefender(ghostId);
		Attacker pacman = StudentController.this.currentGameState.getAttacker();


		if(pacmanLocation.isPowerPill() || ghost.getVulnerableTime() > 0)
			if (pacmanLocation.getPathDistance(ghost.getLocation()) < 30)  // tested between 0-100
				return ghost.getNextDir(pacmanLocation, false); // some movement away from pacman\
			else
				return ghost.getNextDir(pacmanLocation, true);
		return noAction;
	}

	//chase method
	private int chase(int ghostId) {
		int direction = noAction;
		Defender ghost = StudentController.this.currentGameState.getDefender(ghostId);
		Attacker pacman = StudentController.this.currentGameState.getAttacker();
		List<Node> powerUps = StudentController.this.currentGameState.getPowerPillList();

		Node pacmanLocationLast = StudentController.this.previousGameState.getAttacker().getLocation();
		Node pacmanLocation = pacman.getLocation();
		int pacDir = pacman.getDirection();

		if ((ghostId == Blinky || ghostId == Pinky) && pacman.getPossibleDirs(false).contains(pacDir)) {
			direction = ghost.getNextDir(pacmanLocation.getNeighbor(pacDir), true);
		}                 // aims in front of pacman

		if (ghostId == Clyde)   // aims behind pacman
			direction = ghost.getNextDir(pacmanLocationLast, true);

		if (ghostId == Inky)  // aim straight at pacman
			direction = ghost.getNextDir(pacmanLocation, true);

		if (ghostId == Blinky || ghostId == Pinky || ghostId == Clyde) {    // makes all ghosts but 1 flee when pacman is close to a power pill, and one goes towards him to force him to eat it.
			for (int i = 0; i < powerUps.size(); i++) {
				if (pacmanLocation.getPathDistance(powerUps.get(i)) < 20) {
					direction = ghost.getNextDir(pacmanLocation, false);
				}
			}
		}

		return direction;
	}

}