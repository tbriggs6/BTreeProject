package btree;

import java.util.ArrayList;

/**
 * An implementation of the B-Tree leaf-node.  A leaf-node is the bottom of the tree
 * and holds keys and pointers to the data items.  
 * 
 * This is an in-memory implementation of the leaf-node, so it will always hold a 
 * reference to the key and value pair.  This allows the implementation of the disk
 * implementation to manage whether keys/values are stored in-node or as pointers to
 * other disk blocks (allowing LOBs).
 *  
 * @author tbriggs
 *
 * @param <K> The key type
 * @param <V> The value type
 */
class LeafNode<K extends Comparable<K>, V> extends BTreeNode<K,V> {

	static final boolean ENFORCE_UNIQUE_KEYS = true;
	
	int maxEntries;			// the max degree
	ArrayList<Entry<K,V>> children;		// the list of children
	
	
	/**
	 * Constructor for an empty leaf node
	 * @param maxEntries
	 */
	LeafNode(int maxEntries )
	{
		this.maxEntries = maxEntries;
		children = new ArrayList<Entry<K,V>>( );
	}

	/**
	 * Insert the given key/value pair into the leaf node.   Conditionally
	 * enforces uniqueness depending on the configuration {@link #ENFORCE_UNIQUE_KEYS}
	 * 
	 * @param key The key to add
	 * @param value The associated value to add
	 */
	void insert(K key, V value)
	{
		if (children.size() > maxEntries)
			throw new RuntimeException("Node is full.");
		
		int i;
		for (i = 0; i < children.size(); i++)
		{
			if (key.compareTo(children.get(i).key) < 0) break;
		}
		
		
		if (ENFORCE_UNIQUE_KEYS && (i < children.size()) && (children.get(i).key.compareTo(key) == 0))
			throw new RuntimeException("Error - The key is already found in the tree");
		
		children.add(i, new Entry<K,V>( key, value));
	}

	
	/**
	 * Delete a key/value from this leaf node 
	 * @param key The key to delete
	 * @return true if the key was found, false otherwise
	 */
	boolean delete(K key)
	{
		int i;
		for (i = 0; i < children.size(); i++)
		{
			if (key.compareTo(children.get(i).key) == 0) {
				children.remove(i);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Find the first value associated with the given key in this leaf node
	 * @param key The key to find
	 * @return The found value (or an exception if not found)
	 */
	V find(K key)
	{
		for (int i = 0; i < children.size(); i++)
		{
			if (key.compareTo(children.get(i).key) == 0) {
				return children.get(i).value;
			}
		}
		
		throw new RuntimeException("Error - key was not found in this node. ");
	}
	
	/**
	 * Predicate to test if the node is over capacity
	 */
	@Override
	boolean isOverCapacity( ) { return children.size() > maxEntries; }

	/**
	 * Predicate to test if the node is complete
	 */
	@Override
	boolean isComplete( ) { return children.size() == maxEntries; }
	
	/**
	 * Predicate to test if the node is empty
	 */
	@Override
	boolean isEmpty( ) { return children.size() == 0; }
	
	/**
	 * Retrieve the first child
	 * @return
	 */
	Entry<K,V> extractFirst( )
	{
		return children.remove(0);
	}
	
	/**
	 * Retrieve the last child
	 * @return
	 */
	Entry<K,V> extractLast( )
	{
		return children.remove( children.size() - 1);
	}
	
	/**
	 * Provide a string representation of this node.
	 */
	@Override
	public String toString( )
	{
		return "[Leaf: " + children.toString() + "]";
	}
	
	/**
	 * Split the leaf node in half, leaving the greater half and creating a new
	 * leaf-node containing the lesser half (and the split-key).  
	 * @return The result of the split
	 */
	NodeSplitResult<K,V> split( )
	{
		LeafNode<K,V> sibling = new LeafNode<K,V>( maxEntries );
		
		int midPos = children.size() / 2;
		K key = children.get(midPos).key;
		
		int j = 0;
		while (children.size() > midPos)
		{
			sibling.children.add(j++, children.remove(midPos));
		}
		
		NodeSplitResult<K, V> result = new NodeSplitResult<K,V>( );
		result.key = key;
		result.sibling = sibling;
		
		return result;
	}

	/**
	 * Get the maximum key for this node
	 */
	@Override
	K getMaxKey() {
		return children.get( children.size() - 1).key;
	}
	
	/**
	 * Get the minimum key for this node
	 */
	@Override
	K getMinKey() {
		return children.get(0).key;
	}
}
