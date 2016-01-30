package btree;

import java.util.HashMap;
import java.util.Stack;


/**
 * Implementation of a B-Tree data structure with a variable node degree.
 *
 *   B-Trees store key/value pairs in a balanced tree-structure, allowing efficient
 *   storage and retrieval of the key/values.  As an in-memory structure, there are 
 *   better choices, such as Java's native {@link HashMap}.  The reason is that the
 *   B-Tree imposes a higher cost to maintain the balanced nature of the tree, causing
 *   structural changes to be a little less efficient than other structures.  But, the 
 *   goal of the B-Tree is to back the tree with a disk-file.  The nodes of the B-Tree
 *   will be kept on a disk - file, and the consequence of this is that nodes migrate
 *   in the tree far fewer times than in other structures.  For example, the AVL-Tree
 *   style rotations would be very inefficient on a disk file.  
 * 
 *   The B-Tree structure implemented here keeps all leaf-nodes at the bottom-most
 *   layer of the tree.  This means that the keys for a given value may appear in 
 *   the inner node several times, and that to find a value for a key requires a 
 *   full tree traversal.  This was a strategic decision to allow for a more efficient
 *   implementation of the B-Tree backed by a disk file.  It allow clean separation of
 *   node sizes for inner-, leaf-, and data- blocks on the disk.  
 *
 *   This version is NOT yet implemented with a disk-file.  The first implementation is to 
 *   create the B-Tree in memory, which allows for easier testing and debugging.  It is also
 *   instructful to keep the implementation without the B-Tree + Disk checked into the
 *   code repository, allowing alternate implementations.
 *   
 * @author Tom Briggs
 * @version 1.0
 * @since 1.0
 *
 * @param <K> - A Comparable type for the key of the key-value pair
 * @param <V> - The value for the key-value pair for this type of B-Tree
 */
public class BTree<K extends Comparable<K>, V> {

	BTreeNode<K, V> root;	// the root of the tree
	
	final int maxEntries;	// the maximum number of entries in the nodes
	
	
	/**
	 * Construct an empty B-Tree with the indicated number of entries (degree) of the nodes
	 *
	 * @param maxEntries - The degree (maximum number of entries in the nodes)
	 */
	public BTree( int maxEntries )
	{
		this.maxEntries = maxEntries;
		root = null;
	}

	/**
	 * Insert a key/value pair into the B-Tree.  The keys must be unique.
	 * Inserting a key that already exists will cause the insertion to fail
	 * and generate an exception.  
	 * 
	 * @param key - they key to add
	 * @param value - the value to add
	 */
	public void insert(K key, V value) 
	{
		insert(key, value, root);
	}
	
	/**
	 * Insert a key/value pair into the B-Tree at a given starting node.
	 * 
	 * This is an internal method and should only be called in the B-Tree  
	 * @param key	- they key to insert into the tree
	 * @param value - the associated value
	 * @param node - the starting node (typically the root)
	 */
	private void insert(K key, V value, BTreeNode<K,V> node)
	{
		// if the tree is empty, then the root node will be null
		// so create a new root node containing this K-V pair
		if (root == null) {
			LeafNode<K, V> l = new LeafNode<K,V>(this.maxEntries);
			l.insert(key, value);
			root = l;
			return;
		}
		
		// start a descent into the tree at the given starting node
		BTreeNode<K,V> curr = node;
		LeafNode<K,V> leaf = null;
		
		// use the local stack to avoid recursive calling overhead
		Stack<InnerNode<K,V>> stack = new Stack<InnerNode<K,V>>( );

		// traverse the inner-nodes, following the separating keys
		while (! (curr instanceof LeafNode))
		{
			InnerNode<K,V> inner = (InnerNode<K,V>) curr;
			stack.push(inner);
			curr = inner.getChildForKey(key);
		}
		
		// we've landed at the leaf node, insert the K/V pair
		leaf = (LeafNode<K,V>) curr;
		leaf.insert(key, value);
		
		// the leaf is not over-capacity, so we're done now
		if (!leaf.isOverCapacity()) return;
		
		// this could over-fill the leaf, so check and handle that
		// split the leaf into two nodes, a "least" and "greatest" half
		// insert the new node into the containing parent
		NodeSplitResult<K, V> result = leaf.split();
		
		// there was no containing parent (root was this leaf), so make
		// a new inner node containing the two new children
		if (node == leaf) {
			InnerNode<K,V> newRoot = new InnerNode<K,V>(maxEntries );
			newRoot.children.add(0, leaf);
			newRoot.children.add(1, result.sibling);
			newRoot.keys.add(0, result.key);
			root = newRoot;
			
			return;
		}
		
		// there was a history, so start unwinding the stack until we can insert
		// the last split without over-filling
		InnerNode<K,V> inner = null;
		while(! stack.isEmpty() )
		{
			inner = stack.pop();
			inner.addChild(result.key, result.sibling);
			
			// we find a node that was not over-full
			if (!inner.isOverCapacity())
				return;
			
			// putting the previous split value into this node left it 
			// over-full, so this node needs to be split, and the new value
			// sent up to its parent (or promote it to root)
			result = inner.split();
			if (inner == node) {
				InnerNode<K,V> newRoot = new InnerNode<K,V>(maxEntries );
				newRoot.children.add(0, inner);
				newRoot.children.add(1, result.sibling);
				newRoot.keys.add(0, result.key);
				root = newRoot;
				
				return;
			} // end if inner == node
		} // end stack unwind
	} // end insert
	
	
	/**
	 * Search the B-Tree for the indicated key and return the associated value.
	 *  
	 * @param key - the key to find
	 * @return
	 */
	public V find(K key)
	{
		// if the root is empty, throw an error
		if (root == null) 
			throw new RuntimeException("Error - tree is empty");
		
		// start at the root and following the separating values to a leaf
		BTreeNode<K, V> curr = root;
		while (curr instanceof InnerNode)
		{
			InnerNode<K,V> inner = (InnerNode<K,V>) curr;
			curr = inner.getChildForKey(key);
		}
		
		// scan the leaf for the key, leaf.find throws an exception is 
		// the keys is not found
		LeafNode<K,V> leaf = (LeafNode<K,V>) curr;
		V value = leaf.find(key);
		
		return value;
	}
	
	

	/**
	 * Delete a key/value pair from the B-Tree.  
	 * 
	 * All leaf-nodes are at the same level, so we start by finding the containing leaf node and
	 * deleting the key/value from it.  If the leaf-node is not empty, then it can remain and the
	 * delete operation is finished (there is a short-circuit test for this condition).
	 * 
	 * If the leaf-node becomes empty, then it must be removed from its inner-node parent.  That means
	 * the parent can become empty (an empty inner-node contains either 0 or 1 child).  A 0-entry inner 
	 * node is actually an empty root-inner node, and is treated specially.  the 1-entry inner node
	 * is the general case.  Since the parent exists, it must have at least two children (the node that
	 * we are deleting from that just became empty, leaving just one sibling).  
	 * 
	 * We find the sibling, and "rotate" the remaining subtree into the sibling, adjusting its keys.  
	 * If this does not over-fill the sibling, then we delete the parent and promote the sibling.  If the
	 * sibling becomes over-full, then we have to split the sibling and the result of the split becomes the 
	 * new parent node, which updates the node we were about to delete.   
	 * 
	 * This is one of the most complex B-Tree operations - and certainly one of the most difficult to 
	 * implement efficiently.  A recursive solution exists, but will quickly degrade performance, especially
	 * if the node size is small and the height of the tree is large.  The recursive implementation will 
	 * eat into the available program stack.  Instead, a local Stack ADT is used to capture the path 
	 * that is followed to leaf, and then we can unwind the stack as the deletion progresses.
	 * 
	 * Ultimately, this deletion operation ust preserve the B-Tree properties, maintaining the balanced 
	 * nature of the tre.
	 * 
	 * @param key the key to delete
	 * @return true if the node was found and deleted, false otherwise
	 */
	public boolean delete(K key)
	{
		// create a stack containing the path to the leaf node
		Stack<BTreeNode<K, V>> stack = descendToLeaf(key);
		
		LeafNode<K,V> leaf = (LeafNode<K,V>) stack.pop();
		
		// the node was not found, so return false
		if (!leaf.delete(key)) return false;

		// leaf node is still viable, so just return
		if (!leaf.isEmpty()) return true;
		
		BTreeNode<K,V> prevNode = leaf;
		BTreeNode<K,V> currNode = null;
		while (stack.size() > 0)
		{
			if (!prevNode.isEmpty()) break;
			
			currNode = stack.pop();
			InnerNode<K,V> inner = (InnerNode<K,V>) currNode;
			
			// the previous node was a leaf node that became empty
			// remove it from its parent
			if(prevNode instanceof LeafNode) {
				inner.deleteChild(prevNode);
				prevNode = inner;
				currNode = null;
			}
			
			// the previous delete killed an inner node
			// absorb its last remaining child into its sibling
			// and then handle collateral damage
			else {
				InnerNode<K,V> empty = (InnerNode<K,V>) prevNode;

				// find the position of the note that will be deleted from the parent
				int posn = inner.findNode(empty);
				inner.deleteChild(empty);
				
				BTreeNode<K,V> remain = empty.children.get(0);
				InnerNode<K,V> sibling = (InnerNode<K,V>) inner.children.get(posn); 
				if (posn == 0) 
					sibling.addChild(sibling.getMinKey(), remain);
							
				else
					sibling.addChild(remain.getMinKey(), remain);
				
				// if the sibling becomes over-capacity, then we split it,
				// allowing this node to remain.
				if (sibling.isOverCapacity())
				{
					NodeSplitResult<K, V> result = sibling.split();
					inner.addChild(result.key, result.sibling);
				}
				
				// adjust the root to remaining sibling
				if ((inner == root) && (inner.isEmpty()))
					root = sibling;
			}
			
		}
		
		// update the root node as necessary
		if ((currNode == null) && (prevNode != null) && (prevNode instanceof InnerNode) && (prevNode.isEmpty()))
			root = ((InnerNode<K,V>)prevNode).children.get(0);
		
		if ((currNode == null) && (prevNode != null) && (prevNode instanceof LeafNode) && (prevNode.isEmpty()))
			root = null;
		
		return true;
	}

	/**
	 * Descend through the inner-nodes to find the leaf-node that should contains the given key, returning a stack 
	 * that includes the leaf and all of its parent inner-nodes.
	 * 
	 * @param key - the leaf node
	 * @return
	 */
	private Stack<BTreeNode<K,V>> descendToLeaf(K key) {
		Stack<BTreeNode<K,V>> path = new Stack<BTreeNode<K,V>>( );
		BTreeNode<K,V> curr = root;
		while ( curr instanceof InnerNode)
		{
			InnerNode<K,V> inner = (InnerNode<K,V>) curr;
			path.push(inner);
			curr = inner.getChildForKey(key);
		}
		
		// push the leaf node onto the path
		path.push(curr);
		
		return path;
	}

	/**
	 * Provide a string representation of the B-Tree
	 */
	@Override
	public String toString( )
	{
		return root.toString();
	}
	
}
