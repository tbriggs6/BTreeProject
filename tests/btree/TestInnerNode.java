package btree;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class TestInnerNode {

	InnerNode<Long, String> node;
	
	@Before
	public void setUp() throws Exception {
		node = new InnerNode<Long, String>( 3 );
		node.keys.add(new Long(55));
		node.keys.add(new Long(75));
		
		LeafNode<Long, String> child1 = new LeafNode<Long,String>(3);
		child1.insert(new Long(10), "Ten");
		child1.insert(new Long(20), "Twenty");
		
		LeafNode<Long, String> child2 = new LeafNode<Long, String>(3);
		child2.insert(new Long(55), "Fifty-Five");
		child2.insert(new Long(60), "Sixty");
		
		LeafNode<Long, String> child3 = new LeafNode<Long, String>(3);
		child3.insert(new Long(100),"Hundred");
		
		node.children.add(child1);
		node.children.add(child2);
		node.children.add(child3);
	}

	@Test
	public void testGetChild() {
		
		BTreeNode<Long, String> child = node.getChildForKey(new Long(10));
		System.out.println(child.toString());
			
		child = node.getChildForKey(new Long(5));
		System.out.println(child.toString());
		
		child = node.getChildForKey(new Long(55));
		System.out.println(child.toString());
		
		child = node.getChildForKey(new Long(60));
		System.out.println(child.toString());
		
		child = node.getChildForKey(new Long(100));
		System.out.println(child.toString());
		
	}

}
