package btree;

import java.util.ArrayList;

class InnerNode<K extends Comparable<K>, V> extends BTreeNode<K,V> {

	ArrayList<K> keys;
	ArrayList<BTreeNode<K,V>> children;
	
	final int maxEntries;
	
	InnerNode(int maxEntries)
	{
		this.maxEntries = maxEntries;
		keys = new ArrayList<K>( );
		children = new ArrayList<BTreeNode<K,V>>( ); 
	}
	
	
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

	@Override
	boolean isFull() {
		return (keys.size() > maxEntries);
	}

	@Override
	boolean isEmpty() {
		return (keys.size() == 0);
	}

	void addChild(K key, BTreeNode<K,V> child)
	{
		if (key.compareTo(keys.get(0))<0)
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
	
	
	//   Keys:       K1      K2       K3
	//   Children  C1    C2      C3       C4
	//   midPos = 3/2 = 1
	//   midKey = K2
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
	
	public String toString( )
	{
		return "[Inner: " + keys.toString() + "]";
	}
	
}
