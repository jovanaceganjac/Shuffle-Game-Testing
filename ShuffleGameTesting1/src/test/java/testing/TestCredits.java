package testing;
import shuff.Credits;
import static org.junit.jupiter.api.Assertions.*;  
import org.junit.jupiter.api.Test;


public class TestCredits {
	
	 @Test
	    void testConstructorAndPrepareTimeString() {
	        Credits c = new Credits(125, 50, 2); // 2 min 5 sec
	        assertEquals(125, c.getTimeInSeconds());
	        assertEquals(2, c.getLevel());
	        assertEquals(50, c.getMoves());
	        assertEquals("2:5", c.getTime());
	    }

	    @Test
	    void testGetPlayerInitiallyEmpty() {
	        Credits c = new Credits(100, 10, 1);
	        assertEquals("", c.getPlayer(), "Player should be empty string initially");
	    }

	    @Test
	    void testSetAndGetPlayer() {
	        Credits c = new Credits(100, 10, 1);
	        c.setPlayer("Alice");
	        assertEquals("Alice", c.getPlayer());
	    }

	    @Test
	    void testGetTimeWithZeroSeconds() {
	        Credits c = new Credits(0, 10, 1);
	        assertEquals("", c.getTime(), "Time string should be empty if seconds is 0");
	    }

	    @Test
	    void testCompareDifferentSeconds() {
	        Credits c1 = new Credits(100, 10, 1);
	        Credits c2 = new Credits(200, 5, 1);
	        assertTrue(c1.compareTo(c2) < 0);
	        assertTrue(c2.compareTo(c1) > 0);
	    }

	    @Test
	    void testCompareSameSecondsDifferentMoves() {
	        Credits c1 = new Credits(100, 5, 1);
	        Credits c2 = new Credits(100, 10, 1);
	        assertTrue(c1.compareTo(c2) < 0);
	        assertTrue(c2.compareTo(c1) > 0);
	    }

	    @Test
	    void testCompareSameSecondsAndMovesDifferentPlayer() {
	        Credits c1 = new Credits(100, 10, 1);
	        c1.setPlayer("Alice");
	        Credits c2 = new Credits(100, 10, 1);
	        c2.setPlayer("Bob");
	        assertTrue(c1.compareTo(c2) < 0);
	        assertTrue(c2.compareTo(c1) > 0);
	    }

	    @Test
	    void testCompareEquals() {
	        Credits c1 = new Credits(100, 10, 1);
	        c1.setPlayer("Alice");
	        Credits c2 = new Credits(100, 10, 1);
	        c2.setPlayer("Alice");
	        assertEquals(0, c1.compareTo(c2));
	    }

	    @Test
	    void testCompareDoubleMethod() {
	        Credits c = new Credits(0, 0, 1);
	        assertEquals(1, c.compare(5.0, 3.0));
	        assertEquals(-1, c.compare(2.0, 3.0));
	        assertEquals(0, c.compare(3.0, 3.0));
	    }

	    @Test
	    void testCompareIntMethod() {
	        Credits c = new Credits(0, 0, 1);
	        assertEquals(1, c.compare(5, 3));
	        assertEquals(-1, c.compare(2, 3));
	        assertEquals(0, c.compare(3, 3));
	    }

}
