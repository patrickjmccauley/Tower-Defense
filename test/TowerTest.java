import org.junit.Test;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

class TowerTest {

	Tower basic = new BasicTower(1, 3);
	Tower rapid = new RapidTower(1, 3);
	Tower wall = new WallTower(1, 3);
	Tower hidps = new HidpsTower(1, 3);
	Tower slow = new SlowTower(1, 3);

	@Test
	void testMetaData() {
		assertEquals(basic.getType(), "basic");
		assertEquals(wall.getType(), "wall");
		assertEquals(rapid.getType(), "rapid");
		assertEquals(slow.getType(), "slow");
		assertEquals(hidps.getType(), "hidps");
		
		assertEquals(basic.getDamage(), 25);
		assertEquals(hidps.getDamage(), 50);
		assertEquals(slow.getDamage(), 20);
		assertEquals(rapid.getDamage(), 15);
		assertEquals(wall.getDamage(), 0);
		
		assertFalse(basic.getSlow());
		assertFalse(hidps.getSlow());
		assertFalse(rapid.getSlow());
		assertTrue(slow.getSlow());
		assertFalse(wall.getSlow());
		
		assertEquals(basic.getRange(), 3);
		assertEquals(hidps.getRange(), 3);
		assertEquals(slow.getRange(), 4);
		assertEquals(rapid.getRange(), 2);
		assertEquals(wall.getRange(), 0);
		
		assertEquals(Tower.getTowerCost("rapid"), 30);
		assertEquals(Tower.getTowerCost("slow"), 75);
		assertEquals(Tower.getTowerCost("hidps"), 100);
		assertEquals(Tower.getTowerCost("basic"), 60);
		assertEquals(Tower.getTowerCost("wall"), 0);
		
		assertEquals(basic.getLocation(), new Coordinate(1, 3));
	}
	
	@Test
	void testAttacks() {
		Mob m1 = new Mob(100, 3, 5);
		basic.attack(m1);
		assertTrue(m1.getHp() == 75);
		
		Mob m2too = new Mob(100, 3, 4);
		Mob m2 = new Mob(100, 3, 5);
		rapid.attack(m2);
		rapid.attack(m2too);
		assertEquals(m2.getHp(), 85);
		assertEquals(m2too.getHp(), 100);
		
		Mob m3 = new Mob(100, 3, 5);
		slow.attack(m3);
		assertTrue(m3.getHp() == 80);
		
		Mob m4 = new Mob(100, 3, 5);
		hidps.attack(m4);
		assertTrue(m4.getHp() == 50);
		
		Mob m5 = new Mob(100, 3, 5);
		wall.attack(m5);
		assertTrue(m5.getHp() == 100);
		
		
	}

}
