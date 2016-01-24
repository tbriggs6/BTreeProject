package btree;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class TestBTree {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testInsert() {

		BTree<Long,String> T = new BTree<Long, String>(3);
		T.insert(new Long(10), "Ten");
		T.insert(new Long(20), "Twenty");
		T.insert(new Long(30), "Thirty");
		
		T.insert(new Long(70), "Seventy");
		T.insert(new Long(60), "Sixty");
		
		assertTrue( T.root instanceof InnerNode);
		
		InnerNode<Long,String> root = (InnerNode<Long,String>) T.root;
		assertEquals(1, root.keys.size());
		assertEquals(30, (long) root.keys.get(0));
		
		assertEquals(2, root.children.size( ));
		assertTrue( root.children.get(0) instanceof LeafNode );
		assertTrue( root.children.get(1) instanceof LeafNode );

		LeafNode<Long, String> left = (LeafNode<Long, String>) root.children.get(0);
		LeafNode<Long, String> right = (LeafNode<Long, String>) root.children.get(1);
		
		assertEquals(2, left.children.size());
		assertEquals(10, (long) left.children.get(0).key);
		assertEquals(20, (long) left.children.get(1).key);
		
		assertEquals(3, right.children.size());
		assertEquals(30, (long) right.children.get(0).key);
		assertEquals(60, (long) right.children.get(1).key);
		assertEquals(70, (long) right.children.get(2).key);
	}
	
	@Test
	public void testInsert2() {

		BTree<Long,String> T = new BTree<Long, String>(3);
		T.insert(new Long(10), "Ten");
		T.insert(new Long(20), "Twenty");
		T.insert(new Long(30), "Thirty");
		
		T.insert(new Long(70), "Seventy");
		T.insert(new Long(60), "Sixty");
		T.insert(new Long(80),  "Eighty");
		
		assertTrue( T.root instanceof InnerNode);
		
		InnerNode<Long,String> root = (InnerNode<Long,String>) T.root;
		assertEquals(2, root.keys.size());
		assertEquals(30, (long) root.keys.get(0));
		assertEquals(70, (long) root.keys.get(1));
		
		assertEquals(3, root.children.size( ));
		assertTrue( root.children.get(0) instanceof LeafNode );
		assertTrue( root.children.get(1) instanceof LeafNode );
		assertTrue( root.children.get(2) instanceof LeafNode );

		LeafNode<Long, String> left = (LeafNode<Long, String>) root.children.get(0);
		LeafNode<Long, String> mid = (LeafNode<Long, String>) root.children.get(1);
		LeafNode<Long, String> right = (LeafNode<Long, String>) root.children.get(2);
		
		assertEquals(2, left.children.size());
		assertEquals(10, (long) left.children.get(0).key);
		assertEquals(20, (long) left.children.get(1).key);

		assertEquals(2, left.children.size());
		assertEquals(30, (long) mid.children.get(0).key);
		assertEquals(60, (long) mid.children.get(1).key);

		
		assertEquals(2, right.children.size());
		assertEquals(70, (long) right.children.get(0).key);
		assertEquals(80, (long) right.children.get(1).key);
	}

	@Test
	public void testInsert3() {

/*
        30         50        70

10  20    30  40    50 60     70 80
*/
		
		BTree<Long,String> T = new BTree<Long, String>(3);
		T.insert(new Long(10), "Ten");
		T.insert(new Long(20), "Twenty");
		T.insert(new Long(30), "Thirty");
		
		T.insert(new Long(70), "Seventy");
		T.insert(new Long(60), "Sixty");
		T.insert(new Long(80),  "Eighty");
		
		T.insert(new Long(50), "Fifty");
		T.insert(new Long(40), "Fourty");
		
		assertTrue( T.root instanceof InnerNode);
		
		InnerNode<Long,String> root = (InnerNode<Long,String>) T.root;
		assertEquals(3, root.keys.size());
		assertEquals(30, (long) root.keys.get(0));
		assertEquals(50, (long) root.keys.get(1));
		assertEquals(70, (long) root.keys.get(2));
		
		assertEquals(4, root.children.size( ));
		assertTrue( root.children.get(0) instanceof LeafNode );
		assertTrue( root.children.get(1) instanceof LeafNode );
		assertTrue( root.children.get(2) instanceof LeafNode );
		assertTrue( root.children.get(3) instanceof LeafNode );

		LeafNode<Long, String> child0 = (LeafNode<Long, String>) root.children.get(0);
		LeafNode<Long, String> child1 = (LeafNode<Long, String>) root.children.get(1);
		LeafNode<Long, String> child2 = (LeafNode<Long, String>) root.children.get(2);
		LeafNode<Long, String> child3 = (LeafNode<Long, String>) root.children.get(3);
		
		assertEquals(2, child0.children.size());
		assertEquals(10, (long) child0.children.get(0).key);
		assertEquals(20, (long) child0.children.get(1).key);

		assertEquals(2, child1.children.size());
		assertEquals(30, (long) child1.children.get(0).key);
		assertEquals(40, (long) child1.children.get(1).key);

		assertEquals(2, child2.children.size());
		assertEquals(50, (long) child2.children.get(0).key);
		assertEquals(60, (long) child2.children.get(1).key);
		
		assertEquals(2, child3.children.size());
		assertEquals(70, (long) child3.children.get(0).key);
		assertEquals(80, (long) child3.children.get(1).key);
	}
	
	@Test
	public void testInsert4() {

/*
 *                       60
 *   
        30         50                    70       

10  20    30  40    50 55         60 65     70 80
*/
		
		BTree<Long,String> T = new BTree<Long, String>(3);
		T.insert(new Long(10), "Ten");
		T.insert(new Long(20), "Twenty");
		T.insert(new Long(30), "Thirty");
		
		T.insert(new Long(70), "Seventy");
		T.insert(new Long(60), "Sixty");
		T.insert(new Long(80),  "Eighty");
		
		T.insert(new Long(50), "Fifty");
		T.insert(new Long(40), "Fourty");
		T.insert(new Long(55), "Fifty-Five");
		T.insert(new Long(65), "Sixty-Five");
		
		assertTrue( T.root instanceof InnerNode);
		
		InnerNode<Long,String> root = (InnerNode<Long,String>) T.root;
		assertEquals(1, root.keys.size());
		assertEquals(60, (long) root.keys.get(0));
		assertEquals(2, root.children.size());
		
		InnerNode<Long,String> left = (InnerNode<Long,String>) root.children.get(0);
		InnerNode<Long,String> right = (InnerNode<Long,String>) root.children.get(1);
		
		assertEquals(30, (long) left.keys.get(0));
		assertEquals(50, (long) left.keys.get(1));
		
		assertEquals(70, (long) right.keys.get(0));
		
		LeafNode<Long, String> child0 = (LeafNode<Long, String>) left.children.get(0);
		LeafNode<Long, String> child1 = (LeafNode<Long, String>) left.children.get(1);
		
		LeafNode<Long, String> child2 = (LeafNode<Long, String>) left.children.get(2);
		
		LeafNode<Long, String> child3 = (LeafNode<Long, String>) right.children.get(0);
		LeafNode<Long, String> child4 = (LeafNode<Long, String>) right.children.get(1);
		
		assertEquals(10, (long) child0.children.get(0).key);
		assertEquals(20, (long) child0.children.get(1).key);
		
		assertEquals(30, (long) child1.children.get(0).key);
		assertEquals(40, (long) child1.children.get(1).key);
		
		assertEquals(50, (long) child2.children.get(0).key);
		assertEquals(55, (long) child2.children.get(1).key);
		
		assertEquals(60, (long) child3.children.get(0).key);
		assertEquals(65, (long) child3.children.get(1).key);
		
		assertEquals(70, (long) child4.children.get(0).key);
		assertEquals(80, (long) child4.children.get(1).key);
		
	}
	
	
	@Test
	public void testFind( )
	{
		BTree<Long,String> T = new BTree<Long, String>(3);
		T.insert(new Long(10), "Ten");
		T.insert(new Long(20), "Twenty");
		T.insert(new Long(30), "Thirty");
		
		T.insert(new Long(70), "Seventy");
		T.insert(new Long(60), "Sixty");
		T.insert(new Long(80),  "Eighty");
		
		T.insert(new Long(50), "Fifty");
		T.insert(new Long(40), "Fourty");
		T.insert(new Long(55), "Fifty-Five");
		T.insert(new Long(65), "Sixty-Five");
		
		
		assertEquals("Ten", T.find(new Long(10)));
		assertEquals("Twenty", T.find(new Long(20)));
		assertEquals("Thirty", T.find(new Long(30)));
		assertEquals("Fourty", T.find(new Long(40)));
		assertEquals("Fifty", T.find(new Long(50)));
		assertEquals("Fifty-Five", T.find(new Long(55)));
		assertEquals("Sixty", T.find(new Long(60)));
		assertEquals("Sixty-Five", T.find(new Long(65)));
		assertEquals("Seventy", T.find(new Long(70)));
		assertEquals("Eighty", T.find(new Long(80)));
		
		try {
			Object n = T.find(new Long(0));
			fail("this should have fail");
		}
		catch(Throwable E)
		{
			;
		}
		
	}
}

