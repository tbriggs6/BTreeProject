package btree;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class testBTreeInnerNode {

	@Test
	public void testBTreeNode() {
		
		BTreeInnerNode node = new BTreeInnerNode( true, 4, null );
		assertEquals(null, node.parent);
		assertEquals(true, node.leaf);
		assertEquals(4, node.maxDegree);
		assertEquals(0, node.keys.size());
		assertEquals(0, node.children.size());
	}

	@Test
	public void testInsert() {
		BTreeInnerNode node = new BTreeInnerNode( true, 4, null );
		BTreeDataNode data = new BTreeDataNode<String>(node, "Testing");
		
		node.insert(10, data);
		
		assertEquals(null, node.parent);
		assertEquals(true, node.leaf);
		assertEquals(4, node.maxDegree);
		assertEquals(1, node.keys.size());
		assertEquals(1, node.children.size());
		
		
		assertEquals(10, (long) node.keys.get(0));
	
		BTreeDataNode<String> actual = (BTreeDataNode<String>) node.children.get(0);
		assertEquals(node, actual.parent);
		
		String a = actual.payload;
		assertEquals("Testing", a);
	}
	
	@Test
	public void testInsert2() {
		BTreeInnerNode node = new BTreeInnerNode( true, 4, null );
		BTreeDataNode data0 = new BTreeDataNode<String>(node, "Testing");
		BTreeDataNode data1 = new BTreeDataNode<String>(node, "Still Testing");
		
		node.insert(10, data0);
		node.insert(5, data1);
		
		
		assertEquals(null, node.parent);
		assertEquals(true, node.leaf);
		assertEquals(4, node.maxDegree);
		assertEquals(2, node.keys.size());
		assertEquals(2, node.children.size());
		
		
		assertEquals(5, (long) node.keys.get(0));
	
		BTreeDataNode<String> actual = (BTreeDataNode<String>) node.children.get(0);
		assertEquals(node, actual.parent);
		
		String a = actual.payload;
		assertEquals("Still Testing", a);
	}
	
	@Test
	public void testInsert3( ) {
		BTreeInnerNode node = new BTreeInnerNode(true, 3, null );
		node.insert(10, new BTreeDataNode<String>(node, "ten"));
		node.insert(20, new BTreeDataNode<String>(node, "twenty"));
		
		// should force a split
		node.insert(30, new BTreeDataNode<String>(node, "thirty"));
		
		// after the split...
		BTreeInnerNode root = node.parent;
		
		assertFalse(root.leaf);
		assertEquals(1, root.keys.size());
		assertEquals(3, root.children.size());
		
		BTreeNode left = root.children.get(0);
		BTreeNode data = root.children.get(1);
		BTreeNode right = root.children.get(2);
		
		assertTrue(left instanceof BTreeInnerNode);
		assertTrue(right instanceof BTreeInnerNode);
		assertTrue(data instanceof BTreeDataNode);
		
		assertFalse(left.parent == null);
		assertFalse(data.parent == null);
		assertFalse(right.parent == null);
		
		assertEquals(20, (long) root.keys.get(0));
		assertEquals("twenty", ((BTreeDataNode) data).payload);
		
		assertEquals(true, ((BTreeInnerNode) left).leaf);
		assertEquals(1, ((BTreeInnerNode) left).keys.size());
		assertEquals(1, ((BTreeInnerNode) left).children.size());
		assertEquals(10, (long) ((BTreeInnerNode) left).keys.get(0));
		assertEquals("ten", ((BTreeDataNode)(((BTreeInnerNode) left).children.get(0))).payload);
		
		assertEquals(true, ((BTreeInnerNode) right).leaf);
		assertEquals(1, ((BTreeInnerNode) right).keys.size());
		assertEquals(1, ((BTreeInnerNode) right).children.size());
		assertEquals(30, (long) ((BTreeInnerNode) right).keys.get(0));
		assertEquals("thirty", ((BTreeDataNode)(((BTreeInnerNode) right).children.get(0))).payload);
	}
	
	@Test
	public void testInsert4( ) {
		
		BTreeInnerNode root = new BTreeInnerNode(true, 3, null );
		root.insert(10, new BTreeDataNode<String>(root, "ten"));
		root.insert(20, new BTreeDataNode<String>(root, "twenty"));
		root.insert(30, new BTreeDataNode<String>(root, "thirty"));
	
		// inserting 30 caused a split - which gives a new root
		root = root.parent;
		
		// the root node should have been split (see testInsert3),
		// so this insert will be the first "insert inner" operation...
		root.insert(40,  new BTreeDataNode<String>(root, "fourty"));
	
		// this should look like a root with 3 children - a data node and two leaf nodes
		assertEquals(3, root.children.size());
		
		// this test is really interested in the right child - which now has 2 children
		BTreeInnerNode right = (BTreeInnerNode) root.children.get(2);
		assertEquals(2, right.keys.size());
		assertEquals(30, (long) right.keys.get(0));
		assertEquals(40, (long) right.keys.get(1));
		
		assertEquals("thirty", ((BTreeDataNode)(((BTreeInnerNode) right).children.get(0))).payload);
		assertEquals("fourty", ((BTreeDataNode)(((BTreeInnerNode) right).children.get(1))).payload);
		
	}
	
	@Test
	public void testInsert5( ) {
		
		BTreeInnerNode root = new BTreeInnerNode(true, 3, null );
		root.insert(10, new BTreeDataNode<String>(root, "ten"));
		root.insert(20, new BTreeDataNode<String>(root, "twenty"));
		root.insert(30, new BTreeDataNode<String>(root, "thirty"));
		root = root.parent;
		
		// the root node should have been split (see testInsert4),
		// and the right node should have two children - this will
		// split that and force an update to the root node
		root.insert(40,  new BTreeDataNode<String>(root, "fourty"));
		if (root.parent != null) root = root.parent;
		
		root.insert(50,  new BTreeDataNode<String>(root, "fifty"));
		if (root.parent != null) root = root.parent;
		
		
		// now interrogate the structure
		assertTrue(root.parent == null);
		assertFalse(root.leaf);
		assertEquals(2, root.keys.size());
		assertEquals(20, (long) root.keys.get(0));
		assertEquals(40, (long) root.keys.get(1));
		
		assertEquals(5, root.children.size());
		assertTrue( root.children.get(0) instanceof BTreeInnerNode);
		assertTrue( root.children.get(1) instanceof BTreeDataNode);
		assertTrue( root.children.get(2) instanceof BTreeInnerNode);
		assertTrue( root.children.get(3) instanceof BTreeDataNode);
		assertTrue( root.children.get(4) instanceof BTreeInnerNode);
		
		BTreeInnerNode left = (BTreeInnerNode) root.children.get(0);
		assertEquals(root, left.parent);
		assertEquals(1, left.keys.size());
		assertEquals(10,  (long) left.keys.get(0));
		
		BTreeInnerNode mid = (BTreeInnerNode) root.children.get(2);
		assertEquals(root, mid.parent);
		assertEquals(1, mid.keys.size());
		assertEquals(30, (long) mid.keys.get(0));
		
		BTreeInnerNode right = (BTreeInnerNode) root.children.get(4);
		assertEquals(root, right.parent);
		assertEquals(1, right.keys.size());
		assertEquals(50, (long) right.keys.get(0));
		
	}
	
	@Test
	public void testInsert6( ) {
		
		long keys[] = { 10, 20, 30, 40, 50, 60, 25, 35, 45, 55, 65, 75, 85 };
		
		BTreeInnerNode root = new BTreeInnerNode(true, 3, null);
		for (int i = 0; i < keys.length; i++)
		{
			root.insert(keys[i], new BTreeDataNode<Long>(root, keys[i]));
			
			// detect split roots
			if (root.parent != null) root = root.parent;
		}
		
		assertEquals(2, root.keys.size());
		assertEquals(30, (long) root.keys.get(0));
		assertEquals(50, (long) root.keys.get(1));
		
		for (BTreeNode child : root.children)
			assertEquals(root, child.parent);
		
		BTreeInnerNode node20 = (BTreeInnerNode) root.children.get(0);
		BTreeDataNode node30 = (BTreeDataNode) root.children.get(1);
		BTreeInnerNode node40 = (BTreeInnerNode) root.children.get(2);
		BTreeDataNode node50 = (BTreeDataNode) root.children.get(3);
		BTreeInnerNode nodeGT = (BTreeInnerNode) root.children.get(4);
		
		assertEquals(30, (long) node30.getPayload());
		assertEquals(50, (long) node50.getPayload());
		
		assertEquals(1, node20.keys.size());
		assertEquals(1, node40.keys.size());
		assertEquals(2, nodeGT.keys.size());
		
		for (BTreeNode child : node20.children)
			assertEquals(node20, child.parent);
		
		BTreeInnerNode node10 = (BTreeInnerNode) node20.children.get(0);
		BTreeDataNode data20 = (BTreeDataNode) node20.children.get(1);
		BTreeInnerNode node25 = (BTreeInnerNode) node20.children.get(2);
		
		assertEquals(20, (long) data20.payload);
		
		BTreeDataNode data10 = (BTreeDataNode) node10.children.get(0);
		assertEquals(10, (long) data10.payload);
		
		BTreeDataNode data25 = (BTreeDataNode) node25.children.get(0);
		assertEquals(25, (long) data25.payload);

		for (BTreeNode child : node40.children)
			assertEquals(node40, child.parent);
		
		BTreeInnerNode node35 = (BTreeInnerNode) node40.children.get(0);
		BTreeDataNode data40 = (BTreeDataNode) node40.children.get(1);
		BTreeInnerNode node45 = (BTreeInnerNode) node40.children.get(2);
	
		assertEquals(35, (long) ((BTreeDataNode)(node35.children.get(0))).payload);
		assertEquals(40, (long) data40.getPayload());
		assertEquals(45, (long) ((BTreeDataNode)(node45.children.get(0))).payload);
	
		for (BTreeNode child : nodeGT.children)
			assertEquals(nodeGT, child.parent);
		
		BTreeInnerNode node55 = (BTreeInnerNode) nodeGT.children.get(0);
		BTreeDataNode data60 = (BTreeDataNode) nodeGT.children.get(1);
		BTreeInnerNode node65 = (BTreeInnerNode) nodeGT.children.get(2);
		BTreeDataNode data75 = (BTreeDataNode) nodeGT.children.get(3);
		BTreeInnerNode node85 = (BTreeInnerNode) nodeGT.children.get(4);
		
		assertEquals(55, (long) ((BTreeDataNode)(node55.children.get(0))).payload);
		assertEquals(60, (long) data60.getPayload());
		assertEquals(65, (long) ((BTreeDataNode)(node65.children.get(0))).payload);
		assertEquals(75, (long) data75.getPayload());
		assertEquals(85, (long) ((BTreeDataNode)(node85.children.get(0))).payload);
		
	}
}
