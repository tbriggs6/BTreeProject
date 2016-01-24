package btree;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class TestLeafNode {

	LeafNode<Long, String> node;
	
	@Before
	public void setUp() throws Exception {
		
		node = new LeafNode(3);
		node.insert(new Long(555), "Test1");
		node.insert(new Long(666), "Test2");
		node.insert(new Long(777), "Test3");
	}

	@Test
	public void testConstructor() {

		LeafNode leaf = new LeafNode( 3 );
		assertEquals(leaf.maxEntries, 3);
		assertTrue(leaf.children.size() == 0);
		assertTrue(leaf.isEmpty());
		assertFalse(leaf.isFull());
	}

	
	@Test
	public void testInsert( ) {
		
		LeafNode leaf = new LeafNode( 3);
		
		leaf.insert(new Long(555), "Test1");
		assertTrue(leaf.children.size() == 1);
		assertFalse(leaf.isEmpty());
		assertFalse(leaf.isFull());
		
		leaf.insert(new Long(666), "Test2");
		assertTrue(leaf.children.size() == 2);
		assertFalse(leaf.isFull());
		
		leaf.insert(new Long(777), "Test3");
		assertTrue(leaf.children.size() == 3);
		assertTrue(leaf.isFull());
	}
	
	@Test
	public void testInsert2( ) {
		
		LeafNode leaf = new LeafNode( 3);
		
		leaf.insert(new Long(555), "Test1");
		leaf.insert(new Long(666), "Test2");
		leaf.insert(new Long(777), "Test3");
		try {
			leaf.insert(new Long(888), "Test4");
			fail("This should have failed");
		}
		catch(Throwable E)
		{
			
		}
	}
	
	@Test
	public void testString( ) {
		LeafNode leaf = new LeafNode( 3);
		
		leaf.insert(new Long(555), "Test1");
		leaf.insert(new Long(666), "Test2");
		leaf.insert(new Long(777), "Test3");
		
		assertEquals(leaf.toString(), "[Leaf: [(555->Test1), (666->Test2), (777->Test3)]]");
	}
	
	@Test
	public void testExtract( ) {
		Entry<Long, String> last = node.extractLast();
		assertEquals(last.key, (Long) ((long) 777));
		
		Entry<Long, String> first = node.extractFirst();
		assertEquals(first.key, (Long) ((long) 555));
	}
	
	@Test
	public void testDelete( ) {
		node.delete(new Long(555));
		
		Entry<Long, String> first = node.extractFirst();
		assertEquals(first.key, (Long) ((long) 666));
	}
	
	
	
	@Test
	public void testSplit( ) {
		NodeSplitResult<Long, String> result = node.split();
		assertEquals( (Long)(long)(666), result.key);
		
		LeafNode<Long,String> leaf = (LeafNode<Long,String>) result.sibling;
		assertEquals(leaf.children.size(), 2);
	}
}
