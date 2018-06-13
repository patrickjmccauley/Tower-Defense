import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.swing.Timer;

import javax.imageio.ImageIO;
/**
 * The Mob class is used to create enemies and control their attributes.
 */
public class Mob implements Comparable<Mob> {
	
	// hp-dependent related variables
	private int hp = 100;
	private double slowSince = 0;
	private int maxHp = 100;
	private boolean remove = false;		
	// Movement variables
	private Timer timer = null;
	private Timer removeTimer = null;
	private int walkNum = 0;
	private double speed = 15;
	private double x, y;
	private double xmod = speed;
	private double ymod = 0;	
	private Direction direction = Direction.RIGHT;
	private TreeSet<Coordinate> traveled = new TreeSet<>();
	// Misc variables
	private static int scaling = 50;
	private static BufferedImage[] images = new BufferedImage[25];
	
	/**
	 * Constructor for a Mob. This will setup the initial Timer as well as the movement images
	 * 
	 * @param maxHp The maximum HP that the move will start with
	 * @param speed The speed of the mob (how many pixels / second will it travel?)
	 */
	public Mob(int maxHp, double speed, double yLoc) {
		this.maxHp = Math.max(maxHp, 0);
		this.hp = maxHp;
		this.speed = Math.max(speed, 0);
		x = 0;
		this.xmod = speed;
		y =  yLoc * scaling;
		this.timer = new Timer(100, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				walk();
			}
		});
		this.timer.start();
		
		// Initialize the BufferedImages to be used in animation
		if (images[0] == null) {
			try {
				for (int i = 0; i < images.length; i ++) {
					images[i] = ImageIO.read(new File("files/mob" + i + ".png"));
				}
			} catch (IOException e) {
				throw new IllegalArgumentException("Could not locate the mob movement images");
			}
		}
	}	
	
	/**
	 * Getter for the status of whether a mob should be removed or not. If this
	 * returns true, it should be removed as it is dead
	 */
	public boolean getRemoveStatus() {
		return remove;
	}
	
	/*
	 * Getter for speed of the mob
	 */
	public double getSpeed() {
		return speed;
	}
	
	/*
	 * Static setter for the general scaling of the game
	 */
	public static void setScaling(int s) {
		scaling = s;
	}
	
	/*
	 * Getter for current HP
	 */
	public int getHp() {
		return hp;
	}
	
	/*
	 * Getter for the maxmimum HP
	 */
	public int getMaxHp() {
		return maxHp;
	}
	
	/**
	 * This method determines which image is appropriate depending on which direction
	 * the mob is moving
	 * @return A BufferedImage initialized in the constructor from files in the "files/" folder
	 */
	public BufferedImage getImage() {
		int i = 0;
		switch (direction) {
			case RIGHT :
				i = 0;
				break;
			case DOWN :
				i = 6;
				break;
			case UP :
				i = 12;
				break;
			case LEFT :
				i = 18;
				break;
			case DEAD :
				return images[24];
			default:
				i = 0;
		}
		return images[walkNum + i];
	}
	
	// Getter for the location in Coordinate format
	public Coordinate getLocation() {
		return new Coordinate((int) (x / scaling), (int) (y / scaling));
	}
	
	// Getter for the x location in PIXELS
	public double getX() {
		return x;
	}
	
	// Getter for the y location in PIXELS
	public double getY() {
		return y;
	}
	
	/*
	 * This method advances the mob using the x & y modifiers and causes the walkNum to
	 * increment. walkNum drives the image shown for the mob for that current state
	 * of movement 
	 */
	public void walk() { 
		walkNum = (walkNum + 1) % 6;
		if (System.currentTimeMillis() - slowSince < 4000) {
			x += xmod / 2;
			y += ymod / 2;
		} else {
			x += xmod;
			y += ymod;
		}
		traveled.add(getLocation());
		
	}
	
	/**
	 * This method inflicts damage upon a mob from a given tower. It is called in the
	 * tower.attack(Mob m) method in the Tower class.
	 * 
	 * @param damage The amount of damage a tower deals
	 */
	public void inflictDamage(int damage, boolean slow) {
		hp = Math.max(hp - damage, 0);
		if (slow) {
			slowSince = System.currentTimeMillis();
		}
	}
	
	// Overridden compareTo method
	@Override
	public int compareTo(Mob m) {
		return (int) (this.x + this.y - (m.getX() + m.getY()));
	}
	
	/**
	 * This dictates which direction a mob is moving using the enums below this class.
	 * 
	 * @param d enum Direction (stored within this file)
	 */
	public void changeDirection(Direction d) {
		this.direction = d;
		switch (d) {
			case UP : 
				ymod = -speed;
				xmod = 0;
				break;
			case DOWN :
				ymod = speed;
				xmod = 0;
				break;
			case RIGHT :
				xmod = speed;
				ymod = 0;
				break;
			case LEFT :
				xmod = -speed;
				ymod = 0;
				break;
			case DEAD :
				xmod = 0;
				ymod = 0;
				// A timer will be started. After 3 seconds, remove that dead mob
				this.removeTimer = new Timer(3000, new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						remove = true;
					}
				});
				removeTimer.start();
				break;
			default :
				throw new IllegalArgumentException("Illegal movement enum passed to mob");
		}
	}
	
	// Getter for the xmod (x velocity)
	public double getXMod() {
		return xmod;
	}
	
	// Getter for ymod (y velocity)
	public double getYMod() {
		return ymod;
	}
	
	// Getter for current direction that the mob is traveling
	public Direction getDirection() {
		return direction;
	}
	
	/**
	 * This checks whether the mob has already traveled on a given space. This was added
	 * because mobs have a default mandate to move toward the right edge of the screen, unless blocked.
	 * When moving left, this can present an issue. This method helps prevent backtracking
	 * 
	 * @param c The coordinate to be checked against the set
	 * @return A boolean value representing whether the mob has previously been to a Coordinate
	 */
	public boolean hasTraveled(Coordinate c) {
		return traveled.contains(c);
	}
	
}

/**
 * This enum allows for a mob to have a number of different states
 */
enum Direction {
	UP, DOWN, LEFT, RIGHT, DEAD;
}