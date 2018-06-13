import java.util.*;
/**
 * This class is used to create a coordinate on a GameCourt. 
 */
public class Coordinate implements Comparable<Coordinate> {
	private int x, y;
	String temp = "";
	
	/**
	 * Constructor for a Coordinate object
	 * 
	 * @param x The x location
	 * @param y The y location
	 */
	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	// Getter for x location
	public int getX() {
		return x;
	}
	
	// Getter for y location
	public int getY() {
		return y;
	}
	
	/**
	 * String method returning coordinate in following format:
	 * @return "(x, y)"
	 */
	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}	
	
	/**
	 * Equivalence test. A coordinate is equal if and only if both its x and y
	 * locations are the same
	 * 
	 * @param c Coordinate to be evaluated against
	 * @return A boolean indicating whether the two are equal
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Coordinate)) {
			return false;
		}
		Coordinate c = (Coordinate) o;
		return this.x == c.getX() && this.y == c.getY();
	}
	
	/**
	 * Comparison between two strings.
	 * 
	 * @param c Coordinate to be compared to
	 * @return If the strings are equal, a 0. If the x & y coordinates sum to the same amount, 1. Otherwise -1 
	 */
	@Override
	public int compareTo(Coordinate c) {
		return this.toString().compareTo(c.toString());
	}
}
