package btree;

import java.util.ArrayList;

/**
 * In-Memory representation of a B-Tree InnerNode.  
 * 
 * This is an internal class that should not be used outside the btree package.
 * 
 * This holds a series of pointers to either other inner-nodes or leaf-nodes.  
 * The class if fully-templated to allow user-defined Key & Value types.
 * 
 * @author tbriggs
 *
 * @param <K> - The key type of the key-value pair
 * @param <V> - The value type of the key-value pair
 */
class InnerNode<K extends Comparable<K>, V> extends BTreeNode<K,V> {

	ArrayList<K> keys;		// the list of keys
	ArrayList<BTreeNode<K,V>> children;	// the list of children
	
	final int maxEntries;	// the maximum number of entries (degree) of this node
	
	/**
	 * Construct an empty inner-node 
	 * @param maxEntries - the maximum degree for this node
	 */
	InnerNode(int maxEntries)
	{
		this.maxEntries = maxEntries;
		keys = new ArrayList<K>( );
		children = new ArrayList<BTreeNode<K,V>>( ); 
	}
	

	/**
	 * Find the chid-node that might contain this key.  This uses the B-Tree 
	 * separating values logic to scan through the keys to find the child that 
	 * falls in the range.  The first child (entry 0) points to all the values 
	 * less than the first key.  The last child (entry size-1) points to all the
	 * values that are greater than the last key.  Each child then points to 
	 * all the values that fall in-between pairs of separating values.
	 * 
	 * @param key the key to search for
	 * @return the child that might contain the key
	 */
	BTreeNode<K,V> getChildForKey(K key)
	{
		if (key.compareTo(keys.get(0)) < 0) 
			return children.get(0);
		
		for (int i = 0; i < keys.size()-1; i++)
		{
			if ((key.compareTo(keys.get(i)) >= 0) &&
				(key.compareTo(keys.get(i+1)) < 0))
				return children.get(i+1);
		}
		
		return children.get( children.size() - 1);
	}

	/**
	 * Predicate to test if the node is over-full (and needs to be split)
	 */
	@Override
	boolean isOverCapacity() {
		return (keys.size() > maxEntries);
	}

	/**
	 * Predicate to test if the node is full (but not over-full)
	 */
	@Override
	boolean isComplete( ) { 
		return (keys.size() == maxEntries);
	}
	
	/**
	 * Predicate to test if the node is empty
	 */
	@Override
	boolean isEmpty() {
		return (keys.size() == 0);
	}

	/**
	 * Add child node (with an associated key value) to this node.  The child could be 
	 * either a leaf- or inner-node.  The method maintains the separating-key property 
	 * of the tree, finding the proper place to insert the key/value pair.  If the new key
	 * is less than the existing keys, it will become the first key.  If the new key is
	 * greater than the existing keys, it will become the last key.  Otherwise, it will
	 * insert in-between the existing keys.
	 * 
	 * @param key They key of the node
	 * @param child The child to add
	 */
	void addChild(K key, BTreeNode<K,V> child)
	{
		if ((keys.size() == 0) || key.compareTo(keys.get(0))<0)
		{
			keys.add(0, key);
			children.add(0, child);
		}
		else 
		{
			int i;
			for (i = 1; i < keys.size(); i++)
				if ((key.compareTo(keys.get(i-1)) >= 0) &&
					(key.compareTo(keys.get(i)) < 0))
					break;
			
			keys.add(i, key);
			children.add(i+1, child);
		}
	}
	

	/**
	 * When a node becomes over-full, it is necessary to spit the node.  This method
	 * will split the target node into two parts, the smaller half will become a new node
	 * (and key), while the greater children (and keys) remain in this node.  This is a
	 * design feature that keeps at least half of the nodes in the same position on the disk. 
	 */
	@Override
	NodeSplitResult<K,V> split() {

		InnerNode<K,V> sibling = new InnerNode<K,V>( maxEntries );
		
		int midPos = keys.size() / 2;
		
		K midKey = keys.remove(midPos);
		
		while (keys.size() > midPos)
			sibling.keys.add( keys.remove(midPos));
		
		while (children.size() > midPos+1)
			sibling.children.add( children.remove(midPos+1));
		
		NodeSplitResult<K,V> result = new NodeSplitResult<K,V>( );
		result.key = midKey;
		result.sibling = sibling;
		
		return result;
	}
	
	
	/**
	 * A string representation of the InnerNode
	 */
	@Override
	public String toString( )
	{
		return "[Inner: " + keys.toString()  + "]";
	}


	/**
	 * Find the position of the given node in the list of children (or throw an exception)
	 * @param node - the node to find
	 * @return the index in the list of children of the given node
	 */
	int findNode(BTreeNode<K,V> node)
	{
		for (int i = 0; i < children.size(); i++)
		{
			if (children.get(i) == node) return i;
		}
		throw new RuntimeException("Node not found.");
	}
	
	/**
	 * Delete the indicated child from the list of children.
	 * @param child The child to remove
	 */
	void deleteChild(BTreeNode<K,V> child)
	{
		int i;
		for (i = 0; i < children.size(); i++)
		{
			
			if (children.get(i) == child)
				break;
		}
		
		if (i >= children.size()) 
			throw new RuntimeException("Error - child was not found!");
		
		children.remove(i);
		
		// remove the last child, remove the last key
		if (children.size() == 0) keys.clear();
		else if (i < keys.size()) 
			keys.remove(i);
		// else remove the corresponding key
		else keys.remove(i-1);
	}

	@Override
	K getMaxKey() {
		return children.get( children.size() - 1).getMaxKey();
	}
	
	@Override
	K getMinKey() {
		return children.get( 0 ).getMinKey();
	}
}
