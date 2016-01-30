package btree;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class TestEntry {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
	
		Entry<Long, String> A = new Entry<Long, String>((long) 555, "Test 555");
		
		assertEquals(A.key, (Long) (long) 555);
		assertEquals(A.value, "Test 555");
		
	}

	@Test
	public void test2() {
	
		Entry<Long, String> A = new Entry<Long, String>((long) 555, "Test 555");
		Entry<Long, String> B = new Entry<Long, String>((long) 663, "Test 663");
		
		assertTrue(A.compareTo(B) < 0);
		assertTrue(B.compareTo(A) > 0);
		assertTrue(A.compareTo(A) == 0);
	}
	
	@Test
	public void testString() 
	{
		Entry<Long, String> A = new Entry<Long, String>((long) 555, "Test 555");
		assertEquals(A.toString(), "(555->Test 555)");
	}

}
