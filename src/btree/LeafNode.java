package btree;

import java.util.ArrayList;

class LeafNode<K extends Comparable<K>, V> extends BTreeNode<K,V> {

	int maxEntries;
	ArrayList<Entry<K,V>> children;
	
	
	LeafNode(int maxEntries )
	{
		this.maxEntries = maxEntries;
		children = new ArrayList<Entry<K,V>>( );
	}
	
	@SuppressWarnings("unchecked")
	void insert(K key, V value)
	{
		if (children.size() > maxEntries)
			throw new RuntimeException("Node is full.");
		
		int i;
		for (i = 0; i < children.size(); i++)
		{
			if (key.compareTo(children.get(i).key) < 0) break;
		}
		
		children.add(i, new Entry<K,V>( key, value));
	}

	@SuppressWarnings("unchecked")
	void delete(K key)
	{
		int i;
		for (i = 0; i < children.size(); i++)
		{
			if (key.compareTo(children.get(i).key) == 0) {
				children.remove(i);
				return;
			}
		}
	}
	
	@SuppressWarnings("unchecked")
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
	
	boolean isFull( ) { return children.size() > maxEntries; }
	
	boolean isEmpty( ) { return children.size() == 0; }
	
	Entry<K,V> extractFirst( )
	{
		return children.remove(0);
	}
	
	Entry<K,V> extractLast( )
	{
		return children.remove( children.size() - 1);
	}
	
	public String toString( )
	{
		return "[Leaf: " + children.toString() + "]";
	}
	
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
	
}
