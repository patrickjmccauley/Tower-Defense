/**
 * This is a subtype of Tower. It is simply a basic tower - med damage, med speed.
 */
public class BasicTower extends Tower {

	private int damage = 25;
	private int cooldown = 2000;
	private long lastFired = 0;
	private boolean slow = false;
	private static int range = 3;
	
	public BasicTower(int x, int y) {
		super(x, y, range);
	}
	
	// Getter for when it last fired
	public long getLastFired() {
		return lastFired;
	}
	
	// Attack method - this will deal damage to enemies within range
	public boolean attack(Mob m) {
		if (!inRange(m)) {
			return false;
		}
		if (System.currentTimeMillis() - lastFired >= cooldown) {
			m.inflictDamage(damage, slow);
			lastFired = System.currentTimeMillis();
			return true;
		}
		return false;
	}
	
	// Getter for range
	public int getRange() {
		return range;
	}
	
	// Getter for damage
	public int getDamage() {
		return damage;
	}
	// Getter for cooldown
	public int getCoolDown() {
		return cooldown / 1000;
	}
	
	// Getter for whether it enemies or not
	public boolean getSlow() {
		return slow;
	}
	
	// Getter for the type
	public String getType() {
		return "basic";
	}
}
