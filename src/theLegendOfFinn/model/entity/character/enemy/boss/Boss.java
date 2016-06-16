package theLegendOfFinn.model.entity.character.enemy.boss;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import theLegendOfFinn.model.entity.character.enemy.EnemyCharacter;
import theLegendOfFinn.model.gameData.Grid;
import theLegendOfFinn.model.gameData.Map;
import theLegendOfFinn.model.utils.Position;

/**
 * Final enemy in the game.
 * 
 * @author LCDPCJL
 *
 */
public class Boss extends EnemyCharacter {
	private static final long serialVersionUID = 1L;

	// probando
	// private static final int RESTING_TIME = 1000;

	// cambiar dsp
	private static final Position BOSS_POSITION = new Position(Map.CELL_SIZE * 5, Map.CELL_SIZE * 5);
	private static final int BOSS_VELOCITY = 3;
	private static final int BOSS_MAX_HP = 10;
	private static final int BOSS_ATTACK = 5;
	private static final int BOSS_HP_BOUNTY = 0;
	private static final long TELEPORT_DELAY = 1500;

	private List<BossProjectile> projectiles;
	private int lastAction = Boss.MOVING;
	private Random moveRandomizer;

	private Position nextPosition;

	public Boss() {
		super(BOSS_POSITION, BOSS_VELOCITY, BOSS_MAX_HP, BOSS_ATTACK, BOSS_HP_BOUNTY);
		projectiles = new ArrayList<>();
		moveRandomizer = new Random();
	}

	// revisar el uso del timer.
	public void act(Grid grid) {
		long now = System.currentTimeMillis();
		if (getTimer().attackTimePassed(now)) {
			
			getTimer().updateLastAttackTime(now);
			
			if (lastAction == Boss.MOVING) {
				tryToAttack();
				lastAction = Boss.ATTACKING;
			} else if (lastAction == Boss.BOSS_ATTACK) {
				tryToIdle();
				lastAction = Boss.IDLE;
			} else {
				tryToMove(Direction.randomDirection(), grid);
				lastAction = Boss.MOVING;
			}
		}
	}

	/**
	 * If it's possible, it attacks.
	 */
	public void tryToAttack() {
		if (state == IDLE) {
			attack();
		}
	}

	// do nothing..?
	public void tryToIdle() {

	}

	// cambiar dsp
	public void attack() {
		for (Direction direction : Direction.values()) {
			Position projPosition = getProjectileSpawnPosition(direction);
			if (projPosition.withinBoundaries())
				projectiles.add(new BossProjectile(projPosition, direction));
		}
	}

	public void tryToMove(Direction direction, Grid grid) {
		if (state == IDLE) {
			state = MOVING;

			do {
				int x = moveRandomizer.nextInt(grid.getWidth()) * Map.CELL_SIZE;
				int y = moveRandomizer.nextInt(grid.getHeight()) * Map.CELL_SIZE;
				nextPosition = new Position(x, y);
			} while (!grid.isFreePosition(nextPosition));

			grid.freePosition(getPosition());


			Timer teleportTimer = new Timer();
			TimerTask task = new TimerTask() {
				public void run() {
					move(nextPosition);
					state = IDLE;
				}
			};
			teleportTimer.schedule(task, TELEPORT_DELAY);
		}
	}

	/**
	 * Teleports the boss to position.
	 * @param position the position to which the boss will teleport.
	 */
	private void move(Position position) {
		getPosition().setX(position.getX());
		getPosition().setY(position.getY());
	}
	/**
	 * Given a direction, it returns the position where a projectile should be spawned.
	 * This position is one square away from the boss.
	 * @param direction
	 * @return the specified position.
	 */
	private Position getProjectileSpawnPosition(Direction direction) {
		int bossX = getPosition().getX();
		int bossY = getPosition().getY();
		int x = 0, y = 0;

		switch (direction) {
		case UP:
			x = bossX;
			y = bossY + 1;
			break;
		case RIGHT:
			x = bossX + 1;
			y = bossY;
			break;
		case DOWN:
			x = bossX;
			y = bossY - 1;
			break;
		case LEFT:
			x = bossX - 1;
			y = bossY;
			break;
		}

		return new Position(x, y);
	}

	/**
	 * Returns the list of projectiles currently active.
	 * @return the list of projectiles.
	 */
	public List<BossProjectile> getProjectiles() {
		return projectiles;
	}
}