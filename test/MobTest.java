import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

class MobTest {

	Mob m1 = new Mob(100, 5, 3);
	Mob m2 = new Mob(1000, 5, 3);
	Mob m3 = new Mob(0, 0, 3);

	@Test
	void testDirection() {
		assertTrue(m1.getDirection() == Direction.RIGHT);
		m1.changeDirection(Direction.LEFT);
		assertTrue(m1.getDirection() == Direction.LEFT);
	}
	
	@SuppressWarnings("deprecation")
	@Test
	void testHpRelated() throws Exception {
		assertEquals(m2.getHp(), 1000);
		assertEquals(m2.getMaxHp(), 1000);
		m2.inflictDamage(25, true);
		assertEquals(m2.getHp(), 975);
		assertEquals(m2.getSpeed() / 2, m2.getXMod());
		m3.changeDirection(Direction.DEAD);
		assertFalse(m3.getRemoveStatus());
		TimeUnit.SECONDS.sleep(4);
		assertTrue(m3.getRemoveStatus());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	void testLocationRelated() {
		assertEquals(m1.getSpeed(), 5.0);
		assertEquals(m1.getLocation(), new Coordinate(0, 3));
		assertTrue(m1.getLocation().getX() == 0);
		assertTrue(m1.getLocation().getY() == 3);
		assertEquals(m1.getXMod(), m1.getSpeed());
		assertTrue(m1.getYMod() == 0);
		m1.changeDirection(Direction.UP);
		assertEquals(m1.getYMod(), -m1.getSpeed());
		assertTrue(m1.getXMod() == 0);
		m2.walk();
		assertTrue(m2.hasTraveled(new Coordinate(0, 3)));
	}
}
