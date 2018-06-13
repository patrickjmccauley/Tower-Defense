/**
 * This is a subtype of Tower. It slows enemies that it attacks.
 */
public class SlowTower extends Tower {

	private int damage = 20;
	private int cooldown = 1000;
	private long lastFired = 0;
	private boolean slow = true;
	private static int range = 4;
	
	public SlowTower(int x, int y) {
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
	
	// Getter for whether it slows enemies or not
	public boolean getSlow() {
		return slow;
	}
	
	// Getter for type
	public String getType() {
		return "slow";
	}
}

