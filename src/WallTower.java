import java.awt.BorderLayout;

import javax.swing.JLabel;

/**
 * This is a subtype of Tower. It serves to make up the borders on the north and south fronts of the gameboard.
 */
public class WallTower extends Tower {

	private int damage = 0;
	private int cooldown = 0;
	private long lastFired = 0;
	private boolean slow = false;
	
	// Constructor
	public WallTower(int x, int y) {
		super(x, y, 0);
	}
	
	// Getter for the last time a tower has fired
	public long getLastFired() {
		return lastFired;
	}
	
	// Attack method. In this case, the tower doesn't attack
	public boolean attack(Mob m) {
		return false;
	}
	
	// Getter for range
	public int getRange() {
		return 0;
	}

	// Getter for cooldown
	public int getCoolDown() {
		return cooldown / 1000;
	}
	
	// Getter for damage
	public int getDamage() {
		return 0;
	}
	
	// Getter for whether it enemies or not
	public boolean getSlow() {
		return false;
	}
	
	// Getter for the type
	public String getType() {
		return "wall";
	}		
}