
import javax.imageio.ImageIO;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

import javax.swing.Timer;

/**
 * This is the GUI class. It serves as the player's interface.
 */

public class Game implements Runnable {

	private GameCourt gamecourt = new GameCourt();
	
	JFrame frame = new JFrame("Tower Defense");
	
	public JPanel topLevel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		// Grouping on the right-hand side
		JPanel right = new JPanel();
		JPanel rightTop = new JPanel();
		JPanel rightBtm = new JPanel();
		right.setLayout(new GridLayout(2, 1));
		right.add(rightTop);
		right.add(rightBtm);
		rightTop.setLayout(new GridLayout(1, 1));
		rightBtm.setLayout(new GridLayout(2, 2));
		
		// Initializing the right-hand side icons
		BufferedImage basicIcon, rapidIcon, hidpsIcon, slowIcon, coinIcon, 
		scoreIcon, heartIcon;		
		try {
			basicIcon = ImageIO.read(new File("files/basic.png"));
			hidpsIcon = ImageIO.read(new File("files/hidps.png"));
			slowIcon = ImageIO.read(new File("files/slow.png"));
			rapidIcon = ImageIO.read(new File("files/rapid.png"));
			coinIcon = ImageIO.read(new File("files/coin.png"));
			scoreIcon = ImageIO.read(new File("files/trophy.png"));
			heartIcon = ImageIO.read(new File("files/heart.png"));
			
		} catch (IOException e) {
			throw new IllegalArgumentException("Could not locate the basic file");
		}		
		JButton basicTower = new JButton(new ImageIcon(basicIcon));
		JButton slowTower = new JButton(new ImageIcon(slowIcon));
		JButton hidpsTower = new JButton(new ImageIcon(hidpsIcon));
		JButton rapidTower = new JButton(new ImageIcon(rapidIcon));
		
		// Initializing and setting the tooltips for each tower
		Tower tempTower = new BasicTower(0, 0);
		String tempDesc = "";
		
		tempDesc = "<html><b>B<u>a</u>sic Tower </b><br><br>This is a basic " + 
			"tower. While cheap, it has limited damage, but can be<br> useful for " + 
			"blocking the enemy's path.<br><br>Range: " + tempTower.getRange() + 
			"<br>Cooldown: " + tempTower.getCoolDown() + " sec<br>Damage: " + 
		tempTower.getDamage() + "<br> Cost: " + Tower.getTowerCost("basic");
		basicTower.setToolTipText(tempDesc);
		
		tempTower = new RapidTower(0, 0);
		tempDesc = "<html><b><u>R</u>apid Tower </b><br><brThe " + 
				"rapid tower deals little damage in a short range, <br> but fires very quickly." + 
				"<br><br>Range: " + tempTower.getRange() + "<br>Cooldown: " + 
				tempTower.getCoolDown() + " sec<br>Damage: " + tempTower.getDamage() + 
				"<br> Cost: " + Tower.getTowerCost("rapid");
		rapidTower.setToolTipText(tempDesc);
		
		tempTower = new SlowTower(0, 0);
		tempDesc = "<html><b>Slo<u>w</u>Tower </b><br><br>This tower's attacks " + 
				"do little damage, but reduce<br> enemy movement speed " + 
				"<br><br>Range: " + tempTower.getRange() + "<br>Cooldown: " + 
				tempTower.getCoolDown() + " sec<br>Damage: " + tempTower.getDamage() + 
				"<br> Cost: " + Tower.getTowerCost("slow");
		slowTower.setToolTipText(tempDesc);
		
		tempTower = new HidpsTower(0, 0);
		tempDesc = "<html><b>Hi-<u>D</u>amage Tower </b><br><br>This tower deals " + 
				"the highest damage to a single target." + "<br><br>Range: " + 
				tempTower.getRange() + "<br>Cooldown: " + tempTower.getCoolDown() + 
				" sec<br>Damage: " + tempTower.getDamage() + "<br> Cost: " + 
				Tower.getTowerCost("hidps");
		hidpsTower.setToolTipText(tempDesc);
		
		// Adding in all of the right-hand side icons (towers)
		rightBtm.add(basicTower);		
		rightBtm.add(hidpsTower);
		rightBtm.add(slowTower);
		rightBtm.add(rapidTower);
		
		// Action Listener for all tower build buttons		
		basicTower.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gamecourt.pickTower("basic");
				selectedTower(gamecourt.getTowerType(), rightTop);
			}
		});	
		hidpsTower.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gamecourt.pickTower("hidps");
				selectedTower(gamecourt.getTowerType(), rightTop);
			}
		});	
		slowTower.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gamecourt.pickTower("slow");
				selectedTower(gamecourt.getTowerType(), rightTop);
			}
		});
		rapidTower.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gamecourt.pickTower("rapid");
				selectedTower(gamecourt.getTowerType(), rightTop);
			}
		});
		
		// This is where the layout is actually setup 
		panel.add(right, BorderLayout.EAST);
		panel.add(gamecourt, BorderLayout.CENTER);
		gamecourt.setBorder(BorderFactory.createLineBorder(Color.black));
		
		// ===============================
		// ========= KEYBINDINGS =========
		// ===============================
		
		// Keybinding for clearing the selected tower
		frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke("ESCAPE"), "clearFocus");
		frame.getRootPane().getActionMap().put("clearFocus", 
				new HandleSelection(rightTop));

		// Delete a tower
		frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke("DELETE"), "removeTower"); 
		frame.getRootPane().getActionMap().put("removeTower", 
				new DeleteTower(rightTop));
		
		// Select a basic tower
		frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke("A"), "basicTower"); 
		frame.getRootPane().getActionMap().put("basicTower", 
				new TowerKeySelection("basic", rightTop));
		
		// hidps
		frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke("D"), "hidpsTower"); 
		frame.getRootPane().getActionMap().put("hidpsTower", 
				new TowerKeySelection("hidps", rightTop));
		
		// rapid
		frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke("R"), "rapidTower"); 
		frame.getRootPane().getActionMap().put("rapidTower", 
				new TowerKeySelection("rapid", rightTop));
		
		// slow
		frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke("W"), "slowTower"); 
		frame.getRootPane().getActionMap().put("slowTower", 
				new TowerKeySelection("slow", rightTop));
		
		// Mouse listener to determine where a click has occurred and select that square,
		// or place an already selected tower type
		panel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				gamecourt.selectTower(e.getX() / gamecourt.getScalingFactor(), 
						e.getY() / gamecourt.getScalingFactor());
				selectedTower(gamecourt.getTowerType(), rightTop);
			}
		});				
		
		// The bottom menu bar of buttons & labels
		JButton instructions, beginNextRound, reset;
		instructions = new JButton("Instructions");
		beginNextRound = new JButton("Begin Next Level");
		reset = new JButton("New Game");
		JPanel btmBar = new JPanel(new GridLayout(1, 5));
		btmBar.add(instructions);
		btmBar.add(beginNextRound);
		panel.add(btmBar, BorderLayout.SOUTH);
		JPanel goldPanel = new JPanel(new GridLayout(1, 2));
		JPanel livesPanel = new JPanel(new GridLayout(1, 2));
		JPanel scorePanel = new JPanel(new GridLayout(1, 2));
		goldPanel.add(new JLabel(new ImageIcon(coinIcon)));
		livesPanel.add(new JLabel(new ImageIcon(heartIcon)));
		scorePanel.add(new JLabel(new ImageIcon(scoreIcon)));
		btmBar.add(goldPanel);
		btmBar.add(scorePanel);
		btmBar.add(livesPanel);
		JPanel level = new JPanel(new GridLayout(1, 2));
		btmBar.add(level);
		level.add(new JLabel("Lvl:"));
		JLabel levelAmt = new JLabel("" + gamecourt.getLevel());
		btmBar.add(reset);
		JLabel scoreAmt = new JLabel("" + gamecourt.getScore());
		JLabel bankAmt = new JLabel("" + gamecourt.getBank());
		JLabel livesAmt = new JLabel("" + gamecourt.getLives());
		goldPanel.add(bankAmt);
		scorePanel.add(scoreAmt);
		livesPanel.add(livesAmt);
		level.add(levelAmt);
		
		// Action listener to draw the instructions window
		instructions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				instructions();
				panel.repaint();
			}
		});
		
		// Action listener to reset the game
		reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.remove(gamecourt);
				gamecourt = new GameCourt();
				panel.add(gamecourt, BorderLayout.CENTER);
			}
		});
		
		// Timer to update the score & bank amounts every .2 seconds
		Timer bankScoreTimer = new Timer(200, 
				new UpdateScoreBank(scoreAmt, bankAmt, livesAmt, levelAmt, panel));
		bankScoreTimer.start();
		
		beginNextRound.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gamecourt.nextLevel();
			}
		});	
		
		return panel;
	}
	
	// Inner class to handle the clearing of a selected tower
	private class HandleSelection extends AbstractAction {
		private JPanel panel = null;
		
		public HandleSelection(JPanel panel) {
			gamecourt.clearContents();
			this.panel = panel;
		}
		
		public void actionPerformed(ActionEvent e) {
			gamecourt.clearContents();
			selectedTower(gamecourt.getTowerType(), panel);
		}
	}
	
	// Inner class to handle deleting a selected tower
	private class DeleteTower extends AbstractAction {
		private JPanel panel = null;
		
		public DeleteTower(JPanel panel) {
			gamecourt.clearContents();
			this.panel = panel;
		}
		
		public void actionPerformed(ActionEvent e) {
			if (gamecourt.getMode().equals("selected")) {
				gamecourt.removeTower();
				selectedTower(gamecourt.getTowerType(), panel);
			}
		}
	}
	
	// Inner class to update the score, level, lives, and bank indicators
	private class UpdateScoreBank implements ActionListener {
		private JLabel score, bank, lives, level;
		private JPanel panel;
		
		public UpdateScoreBank(JLabel score, JLabel bank, JLabel lives, 
				JLabel level, JPanel centerPanel) {
			this.score = score;
			this.bank = bank;
			this.lives = lives;
			this.level = level;
			panel = centerPanel;
		}
		
		public void actionPerformed(ActionEvent e) {
			bank.setText("" + gamecourt.getBank());
			lives.setText("" + gamecourt.getLives());
			score.setText("" + gamecourt.getScore());
			level.setText("" + gamecourt.getLevel());
			bank.repaint();
			lives.repaint();
			score.repaint();
			level.repaint();
			if (Integer.parseInt(lives.getText()) <= 0) {
				panel.remove(gamecourt);
				panel.add(new JLabel("<html><font size=36>YOU LOST!"), BorderLayout.CENTER);
			} else if (gamecourt.getLevel() == 999) {
				panel.remove(gamecourt);
				panel.add(new JLabel("<html><font size=36>YOU WON! SCORE: " + 
				gamecourt.getScore()), BorderLayout.CENTER);
			}
		}
	}
	
	// Inner class to deal with selecting a towwer using a hotkey
	private class TowerKeySelection extends AbstractAction {
		private String myType = "";
		private JPanel panel = null;
		
		
		public TowerKeySelection(String type, JPanel panel) {
			this.myType = type;
			this.panel = panel;
		}
		
		public void actionPerformed(ActionEvent e) {
			gamecourt.pickTower(myType);
			selectedTower(gamecourt.getTowerType(), panel);
		}
	}
	
	/**
	 * This method 'selects' a given tower, displaying it in the top right of the screen
	 * 
	 * @param type The type of tower that has been selected
	 * @param panel The JPanel in which this will be drawn (top right)
	 */
	public void selectedTower(String type, JPanel panel) {
		BufferedImage image = null;
		if (!type.equals("") && !type.equals("wall")) {
			try {
				image = ImageIO.read(new File("files/" + type + "_large.png"));
			} catch (IOException e) {
				throw new IllegalArgumentException("Cannot find large " + 
				"version of selected tower");
			}
		}
		JLabel label;
		if (image != null) {
			label = new JLabel(new ImageIcon(image));
		} else {
			label = new JLabel();
		}
		panel.removeAll();
		panel.add(label);
		panel.revalidate();
		panel.repaint();
		gamecourt.setFocusable(true);
	}
	
	// The instructions panel is generated using this method
	public void instructions() {	
		String instructionStr = "<html><p><b><font size=12>Tower Defense<br></font></b>" + 
				"The objective of this game is to make it through all 50 levels. <br>" + 
				"You begin with 50 lives and a budget of $300. Once you have built up sufficient <br>" +
				"defenses, you will need to press the <b>\"Begin Next Level\"</b> button. <br>" +
				"Each level, enemies will spawn at random intervals along the left wall and try <br>" +
				"to make it all the way across your gameboard. If they reach the other side, you <br>" +
				"will lose a life. Conversely, if you destroy them, you will gain points and more <br>" +
				"money. The game gets harder as the levels increase. Good luck.<br><br>" + 
				"Notes: You may 'sell' towers by selecting them and pressing 'delete'<br>" +
				"You will find a description of each tower type by hovering over the button<br>" + 
				"Towers can be built with the hotkeys underlined in their descriptions";		
		JOptionPane.showMessageDialog(frame, instructionStr);
	}
	
	// Run method
	public void run() {
		frame.add(topLevel());
		
		frame.setPreferredSize(new Dimension(745, 375));
		frame.setMaximumSize(frame.getPreferredSize());
		frame.setResizable(false);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
	
	// Main method
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Game());
	}
	
}
