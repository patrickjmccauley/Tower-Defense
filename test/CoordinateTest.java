import org.junit.Test;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import org.junit.Before;

class CoordinateTest {
		
		Coordinate c1, c2, c3, c4;
		
		
		@Before
		public void setupTests() {
			c1 = new Coordinate(0, 0);
			c2 = new Coordinate(1, 0);
			c3 = new Coordinate(0, 1);
			c4 = new Coordinate(0, 0);
		}
		
		@Test
		public void testEquality() {
			assertTrue(c1.equals(c4));
			assertFalse(c2.equals(c3));
		}
		
		@Test
		public void testNegative() {
			c3 = new Coordinate(-1, -1);
			assertTrue(c3.getX() == 0);
			assertTrue(c3.getY() == 0);
		}
		
		@Test
		public void testComparison() {
			assertEquals("0,0 coordinates are equal in comparison", c1.compareTo(c4), 0);
			assertEquals("Higher Y value is greater", c2.compareTo(c3), -1);
			assertEquals("Higher Y value is greater reverse", c3.compareTo(c2), 1);
		}
}
