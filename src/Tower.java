import java.util.*;

/**
 * This is an abstract class with some of the functions that a Tower will use. 
 * It has subtypes that implement the more specific ones.
 */
public abstract class Tower {
	private Coordinate location;
	private int range = 1;
	
	/**
	 * Constructor for a Tower object
	 * 
	 * @param x The x location where the Tower will be placed
	 * @param y The y location where the Tower will be placed
	 * @param type The type of tower to be created (basic/slow/hidps/rapid)
	 */
	public Tower(int x, int y, int range) {
		location = new Coordinate(x, y);
		this.range = range;
	}
	
	/**
	 * Getter for the Coordinate location of a given tower
	 * 
	 * @return A Coordinate object of the tower's location
	 */
	public Coordinate getLocation() {
		return location;
	}
	
	public abstract long getLastFired();	
	public abstract int getRange();
	public abstract int getDamage();
	public abstract int getCoolDown();	
	public abstract boolean attack(Mob m);
	
	// Helper function to determine whether a mob is within a tower's range
	public boolean inRange(Mob m) {
		return Math.abs(m.getLocation().getX() - location.getX()) + 
				Math.abs(m.getLocation().getY() - location.getY()) <= range;
	}
	
	/**
	 * Getter for the type of tower (basic/slow/rapid/hidps)
	 * 
	 * @return A String representing the type of tower
	 */
	public abstract String getType();	
	public abstract boolean getSlow();
	
	// Getter for the cost of a given tower type
	public static int getTowerCost(String type) {
		switch (type) {
			case "slow" :
				return 75;
			case "basic" :
				return 60;
			case "rapid" :
				return 30;
			case "hidps" :
				return 100;
			default :
				return 0;
		}
	}
	
	// Overridden toString method
	@Override
	public String toString() {
		return "Tower type: " + getType() + "\tLocation: " + 
				location.toString();
	}
}
