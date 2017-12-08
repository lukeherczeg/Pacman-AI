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
	// Blinky(Red) - Pinky(Pink) - Clyde(Orange) - Inky(Blue) -> 0 - 1 - 2 - 3

	private Game currentGameState;
	private Game previousGameState;

	private final int noAction = -1;
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
			this.previousGameState = game;
		}
		int[] actions = new int[Game.NUM_DEFENDER];

		actions[Blinky] = ghostAction(Blinky);
		actions[Pinky] = ghostAction(Pinky);
		actions[Clyde] = ghostAction(Clyde);
		actions[Inky] = ghostAction(Inky);

		this.previousGameState = game;
		return actions;
	}
	//each individual ghost, one can be a repeat because we are three people and not four.
	//Variations can be found in the chase method, where we use if statements for each ghost's id

	private int ghostAction(int ghostID){
		return StudentController.this.currentGameState.getDefender(ghostID).isVulnerable() ? frightened(ghostID) : chase(ghostID);
	}

	//frightened method for when powerpill is eaten. Once ghosts get far enough away, they will come back since they don't want to be too far to hunt him when the frightened state ends
	private int frightened(int ghostId){
		Node pacmanLocation = StudentController.this.currentGameState.getAttacker().getLocation();
		Defender ghost = StudentController.this.currentGameState.getDefender(ghostId);

		if(ghost.getVulnerableTime() > 0) // ternary operator for movement away from pacman unless significantly far.
			return (pacmanLocation.getPathDistance(ghost.getLocation()) < 90) ? (ghost.getNextDir(pacmanLocation, false)) : (ghost.getNextDir(pacmanLocation, true));

		return noAction;
	}

	//chase method for moving towards pacman
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
		}                      // aims in front of pacman

		if (ghostId == Clyde)  // aims behind pacman
			direction = ghost.getNextDir(pacmanLocationLast, true);

		if (ghostId == Inky)  // aim straight at pacman
			direction = ghost.getNextDir(pacmanLocation, true);

		if (ghostId == Blinky || ghostId == Pinky || ghostId == Clyde) {    // makes all ghosts but one flee when pacman is close to a power pill, and one goes towards him to force him to eat it.
			for (int i = 0; i < powerUps.size(); i++) {
				if (pacmanLocation.getPathDistance(powerUps.get(i)) < 20) {
					direction = ghost.getNextDir(pacmanLocation, false);
				}
			}
		}
		return direction;
	}
}