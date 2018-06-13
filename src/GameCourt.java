import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import javax.swing.Timer;

import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * This class controls the state of the game and manages the
 * interplay between all other classes and the GUI
 */

public class GameCourt extends JComponent {
	
	private String mode = "empty"; // options: empty, selected, placement
	private TreeMap<Coordinate, Tower> towers = new TreeMap<>();
	private TreeSet<Mob> mobs = new TreeSet<>();
	private int scaling = 35;
	private String selectedTower = "";
	private Dimension gridSize = new Dimension(16, 9);
	private Coordinate selectedTowerLocation = null;
	private BufferedImage basicIcon, rapidIcon, hidpsIcon, slowIcon, 
		grassIcon, wallIcon, squareIcon;
	private Coordinate square = null;
	private int bank = 300;
	private int score = 0;
	private int lives = 50;
	private String debugLEFTRIGHT = "";
	private BufferedReader in = null;
	private int level = 1;
	private int[][] levels = new int[50][4];
	private String state = "running";
		
	/**
	 * Constructor for a gamecourt.
	 */
	public GameCourt() {
		for (int x = 0; x < gridSize.getWidth(); x++) {
			for (int y = 0; y < gridSize.getHeight(); y++) {
				if (y == 0 || y == gridSize.getHeight() - 1) {
					towers.put(new Coordinate(x, y), new WallTower(x, y));
				}
			}
		}
		// This try catch is reading in the image files needed
		try {
			basicIcon = ImageIO.read(new File("files/basic.png"));
			hidpsIcon = ImageIO.read(new File("files/hidps.png"));
			slowIcon = ImageIO.read(new File("files/slow.png"));
			rapidIcon = ImageIO.read(new File("files/rapid.png"));
			grassIcon = ImageIO.read(new File("files/grass.png"));
			wallIcon = ImageIO.read(new File("files/wall.png"));
			squareIcon = ImageIO.read(new File("files/highlight.png"));
		} catch (IOException e) {
			throw new IllegalArgumentException("Could not locate the basic file");
		} 	
		Mob.setScaling(this.scaling);
		// This try catch initializes the 2D array containing each level's information
		try {
			in = new BufferedReader(new FileReader("files/levels.txt"));
			String temp = in.readLine();
			String tempValue = "";
			int indexVal = 0;
			for (int i = 0; i < levels.length; i++) {
				temp = in.readLine();
				for (int j = 0; j < temp.length(); j++) {
					if (temp.charAt(j) == ';') {
						levels[i][indexVal] = Integer.parseInt(tempValue);
						tempValue = "";
						indexVal++;
					} else if (temp.charAt(j) == '|') {
						levels[i][indexVal] = Integer.parseInt(tempValue);
						tempValue = "";
						indexVal = 0;
					} else {
						tempValue += temp.charAt(j);
					}
				}
			}
		} catch (IOException e) {
			throw new IllegalArgumentException("Could not locate levels");
		}		
	}
	
	public BufferedImage getImage(String type) {
		switch (type) {
			case "basic" :
				return basicIcon;
			case "hidps" :
				return hidpsIcon;
			case "slow" :
				return slowIcon;
			case "rapid" :
				return rapidIcon;
			case "wall" :
				return wallIcon;
			case "grass" :
				return grassIcon;
			case "square" :
				return squareIcon;
			default :
				return null;
		}
	}
	
	// Getter for the current mode (empty/selected/placement)
	public String getMode() {
		return mode;
	}
	
	/**
	 * This method overrides the basic paintComponent method
	 */
	@Override
	public void paintComponent(Graphics g) {
		if (state.equals("running")) {
			super.paintComponent(g);
			g.setColor(Color.BLACK);
			paintArray(g);
		}
	}
	
	/**
	 * This method sets the preferred size of the gamecourt
	 */
	@Override
	public Dimension getPreferredSize() {
		return new Dimension((int) gridSize.getWidth() * scaling, 
				(int) gridSize.getHeight() * scaling);
	}
	
	/**
	 * This method repaints the gamecourt by iterating through the 2D array
	 * 
	 * @param g The Graphics context in which this will be painted
	 */
	public void paintArray(Graphics g) {
		for (int x = 0; x < gridSize.getWidth(); x++) {
			for (int y = 0; y < gridSize.getHeight(); y++) {
				Coordinate c = new Coordinate(x, y);
				// Drawing the grass and wall terrains
				if (y == 0 || y == gridSize.getHeight() - 1) {
					g.drawImage(getImage("wall"), x * scaling, 
							y * scaling, scaling, scaling, null);
				} else {
					g.drawImage(getImage("grass"), x * scaling, 
							y * scaling, scaling, scaling, null);
				}
				
				// This iterates over the towers, drawing any that exist. 
				// This will also call the 'attack' method from the Tower
				// class, which will check to see if any towers are in range
				// of a mob/not on cooldown, then subsequently attack.
				for (Map.Entry<Coordinate, Tower> entry : towers.entrySet()) {
					g.drawImage(getImage(entry.getValue().getType()), 
							entry.getKey().getX() * scaling, entry.getKey().getY() * scaling, 
							scaling, scaling, null);
					for (Mob m : mobs) {
						if (entry.getValue().attack(m)) {
							g.setColor(Color.RED);
							g.drawLine(entry.getValue().getLocation().getX(), 
									entry.getValue().getLocation().getY(), m.getLocation().getX(), 
									m.getLocation().getY());
							break;
						}
					}
				}
				// Draw the 'selection' highlighter
				if (mode.equals("selected")) {
					g.drawImage(getImage("square"), selectedTowerLocation.getX() * scaling, 
							selectedTowerLocation.getY() * scaling, scaling, scaling, null);
				}
				g.setColor(new Color(0, 0, 0, 30));
				g.drawRect(x * scaling, y * scaling, scaling - 3, scaling - 3); // Faded grid (for placement)
				// This iterates through the mobs. Any that are at zero HP will be 
				// removed from the clone set. Any that are
				// out of bounds will also be removed from the clone set. This also draws 
				// the HP bar. At the end the clone set becomes the live set
				TreeSet<Mob> tempMobs = new TreeSet<>(mobs);
				for (Mob m : mobs) {					
					if (m.getHp() <= 0) {
						if (m.getDirection() != Direction.DEAD) { 
							m.changeDirection(Direction.DEAD);
						}
						if (m.getRemoveStatus() && tempMobs.contains(m)) {
							tempMobs.remove(m);
							bank += 35;
							score += level * 2;
						}
					}
					advanceMob(m);
					// Draw the mob
					g.drawImage(m.getImage(),(int)  m.getX() - scaling / 2, 
							(int) m.getY() - scaling / 2, scaling - 8, scaling - 8, null);
					// Draw the black box behind the HP bar for live mobs
					if (m.getDirection() != Direction.DEAD) {
						g.setColor(Color.BLACK);
						g.fillRect((int) m.getX() - scaling / 2, 
								(int) m.getY() - m.getImage().getHeight() / 2, 
								m.getImage().getWidth() / 2, 5);
						// Draw the red HP bar
						g.setColor(Color.RED);
						g.fillRect((int) m.getX() - scaling / 2 + 1, 
								(int) m.getY() - m.getImage().getHeight() / 
								2 + 1, (int) ((double) ((double) m.getHp() / 
										(double) m.getMaxHp()) * (m.getImage().getWidth() / 2)) - 2, 3);
					}
					// Clean up any mobs outside the boundaries
					if (m.getLocation().getX() >= gridSize.getWidth() || m.getLocation().getX() < 0) {
						if (tempMobs.contains(m)) {
							lives--;
							tempMobs.remove(m);
						}						
					}
				}
				mobs = tempMobs; // Clone set becomes live set to prevent concurrent action error
				repaint();
			}
		}
	}
	
	// Method to remove a tower from the TreeMap
	public void removeTower() {
		if (selectedTower.equals("wall")) {
			return;
		}
		bank += Tower.getTowerCost(selectedTower) / 2;
		towers.remove(selectedTowerLocation);
		clearContents();
		repaint();
	}
	
	/**
	 * This method clears the state of the game and repaints the gamecourt
	 *  - mode set to "empty"
	 *  - selectedTower set to ""
	 *  - selectedTowerLocation set to null
	 */
	public void clearContents() {
		mode = "empty";
		selectedTower = "";
		selectedTowerLocation = null;
		square = null;
		repaint();
	}
	
	/**
	 * This method allows users to pick a type of tower using a JButton on the GUI
	 * 
	 * @param type The type of tower that the user is picking (basic/slow/rapid/hidps)
	 */
	public void pickTower(String type) {
		mode = "placement";
		selectedTower = type;
		repaint();
	}
	
	/**
	 * This helper method allows users to add a tower to the collection of towers in the game 
	 * 
	 * @param c The Coordinate location of the tower to be placed (key)
	 * @param type The type of tower to be placed (basic/slow/rapid/hidps)
	 */
	public void placeTower(Coordinate c, String type) {
		if (Tower.getTowerCost(type) > bank) {
		} else {
			switch (type) {
				case "basic" :
					towers.put(c, new BasicTower(c.getX(), c.getY()));
					break;
				case "slow" :
					towers.put(c, new SlowTower(c.getX(), c.getY()));
					break;
				case "hidps" :
					towers.put(c, new HidpsTower(c.getX(), c.getY()));
					break;
				case "rapid" :
					towers.put(c, new RapidTower(c.getX(), c.getY()));
					break;
				case "wall" :
					towers.put(c, new WallTower(c.getX(), c.getY()));
					break;
				default :
					throw new IllegalArgumentException("Could not find the type of tower");
			}
			bank -= Tower.getTowerCost(type);
		}
		clearContents();
		repaint();
	}
	
	/**
	 * This method allows users to select or place a tower on the gameboard
	 * 
	 * @param x The x coordinate (scaled down by the scaling factor)
	 * @param y The y coordinate (scaled down by the scaling factor)
	 */
	public void selectTower(int x, int y) {
		Coordinate c = new Coordinate(x, y);
		if (mode.equals("placement")) {
			if (towers.containsKey(c) || c.getY() == 0 || c.getY() == (int) gridSize.getHeight() - 1) {
			} else {
				placeTower(c, selectedTower);
			}
		} else {
			if (towers.containsKey(c)) {
				selectedTower = towers.get(c).getType();
				selectedTowerLocation = c;
				mode = "selected";
			} else {
				clearContents();
			}
		}
		repaint();
	}
	
	/**
	 * This method returns a string of the type of tower that's been selected
	 * 
	 * @return A String indicating the type of tower currently selected 
	 */
	public String getTowerType() {
		return selectedTower;
	}
	
	// Returns the number of lives player has left
	public int getLives() {
		return lives;
	}
	
	/*
	 * Getter for the scaling factor variable (size of each block)
	 */
	public int getScalingFactor() {
		return scaling;
	}
	
	// Returns the player's cash reserve amount
	public int getBank() {
		return bank;
	}
	
	// Returns the player's score
	public int getScore() {
		return score;
	}	
	
	/**
	 * Creates a new mob
	 * @param maxHp The maximum Health the mob will start with
	 * @param speed The speed at which the mob moves
	 * @param yLoc The Y location (where on the left side will it spawn?)
	 */
	public void createMob(int maxHp, int speed, double yLoc) {
		mobs.add(new Mob(maxHp, speed, yLoc));
	}
	
	// Advances the game to the next level
	public void nextLevel() {
		if (level >= levels.length) {
			mode = "win";
			return;
		}
		
		for (int i = 0; i < levels[level - 1][1]; i++) {
			createMob(levels[level - 1][3], levels[level - 1][2], 
					1.0 + Math.random() * (gridSize.getHeight() - 2));
		}
		level = levels[level][0];
	}
		
	/**
	 * The advance method controls movement for the mobs. Depending on a variety of factors, this will determine
	 * whether a mob should change its direction
	 * 
	 * @param m The mob to be evaluated
	 */
	public void advanceMob(Mob m) {
		// original represents the mob's starting location
		Coordinate original = new Coordinate((int) m.getX() / 
				scaling, (int) m.getY() / scaling); // Original location of the mob
		// If the next space the mob would occupy is not a tower, has not been visited before, 
		// and the mob isn't traveling
		// LEFT, change direction to RIGHT
		if (!towers.containsKey(new Coordinate((int) (m.getX() + m.getSpeed()) / 
				scaling, (int) m.getY() / scaling)) && m.getDirection() != Direction.LEFT && 
				!m.hasTraveled(new Coordinate((int) (m.getX() + m.getSpeed()) / scaling, 
				(int) m.getY() / scaling))) {
			m.changeDirection(Direction.RIGHT);
		} else {
			// Otherwise, check to see if the next space will be occupied
			// temp represents where the mob would go if unimpeded
			Coordinate temp = new Coordinate((int) (m.getX() + m.getXMod()) / 
					scaling, (int) (m.getY() + m.getYMod()) / scaling);
			if (towers.containsKey(temp)) { // Next space is blocked 
				if (m.getDirection() == Direction.LEFT ||
						m.getDirection() == Direction.RIGHT) { // While moving laterally...
					// Preference here is to go up, all things being equal...
					// Can it move up? should it?
					if (!towers.containsKey(new Coordinate(original.getX(), 
							original.getY() - 1)) && original.getY() - 1 > 0 &&
							!m.hasTraveled(new Coordinate(original.getX(), 
									original.getY() - 1))) {
						for (int y = original.getY(); y > 0; y--) {
							if (!towers.containsKey(new Coordinate(original.getX() + 1, y))) {
								m.changeDirection(Direction.UP);
								return;
							}
						}
					}
					// Can the mob move down? If so, should it?
					if (!towers.containsKey(new Coordinate(original.getX(), original.getY() + 1)) && 
							original.getY() + 1 < gridSize.getHeight() - 1 &&
							!m.hasTraveled(new Coordinate(original.getX(), original.getY() + 1))) {
						for (int y = original.getY(); y < gridSize.getHeight() - 1; y++) {
							if (!towers.containsKey(new Coordinate(original.getX() + 1, y))) {
								m.changeDirection(Direction.DOWN);
								return;
							}
						}
					} 
				} else {
					// If the mob is moving north-south, preference is always to move right if possible
					if (!towers.containsKey(new Coordinate(original.getX() + 1, original.getY())) && 
							!m.hasTraveled(new Coordinate(original.getX() + 1, original.getY()))) {
						m.changeDirection(Direction.RIGHT);
						return;
					} else if (!towers.containsKey(new Coordinate(original.getX() - 1, original.getY())) && 
							original.getX() > 0 &&
							!m.hasTraveled(new Coordinate(original.getX() - 1, original.getY()))) {
						m.changeDirection(Direction.LEFT);
						return;
					} else {
						throw new IllegalArgumentException("Somehow, can't move left-right but should be able to");
					}
				}
			}
			// If the mob is unimpeded by a tower, but is approaching a wall, then move to the right
			if (m.getDirection() == Direction.DOWN && m.getLocation().getY() + 1 >= gridSize.getHeight() - 1) {
				m.changeDirection(Direction.RIGHT);
			}
		}	    
	}
	
	// This method returns the current level. If the player has won the game, it will return 999
	public int getLevel() {
		if (mode.equals("win")) {
			return 999;
		}
		return level;
	}
	
}
