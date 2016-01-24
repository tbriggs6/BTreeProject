package btree;

import java.util.Stack;

public class BTree<K extends Comparable<K>, V> {

	BTreeNode<K, V> root;
	
	final int maxEntries;
	
	
	public BTree( int maxEntries )
	{
		this.maxEntries = maxEntries;
		root = null;
	}
	
	@SuppressWarnings("unchecked")
	public void insert(K key, V value) 
	{
		
		
		if (root == null) {
			LeafNode<K, V> l = new LeafNode<K,V>(this.maxEntries);
			l.insert(key, value);
			root = l;
			return;
		}
		
		BTreeNode<K,V> curr = root;
		LeafNode<K,V> leaf = null;
		
		Stack<InnerNode<K,V>> stack = new Stack<InnerNode<K,V>>( );

		
		while (! (curr instanceof LeafNode))
		{
			InnerNode<K,V> inner = (InnerNode<K,V>) curr;
			stack.push(inner);
			curr = inner.getChildForKey(key);
		}
		
		leaf = (LeafNode<K,V>) curr;
		leaf.insert(key, value);
		
		if (leaf.isFull())
		{
			NodeSplitResult<K, V> result = leaf.split();
			if (root == leaf) {
				InnerNode<K,V> newRoot = new InnerNode<K,V>(maxEntries );
				newRoot.children.add(0, leaf);
				newRoot.children.add(1, result.sibling);
				newRoot.keys.add(0, result.key);
				root = newRoot;
			}
			
			InnerNode<K,V> inner = null;
			while(! stack.isEmpty() )
			{
				inner = stack.pop();
				inner.addChild(result.key, result.sibling);
				
				if (!inner.isFull())
					break;
				
				result = inner.split();
				if (inner == root) {
					InnerNode<K,V> newRoot = new InnerNode<K,V>(maxEntries );
					newRoot.children.add(0, inner);
					newRoot.children.add(1, result.sibling);
					newRoot.keys.add(0, result.key);
					root = newRoot;
					break;
				}
			}
		}
	}
	
	V find(K key)
	{
		if (root == null) 
			throw new RuntimeException("Error - tree is empty");
		
		BTreeNode<K, V> curr = root;
		while (curr instanceof InnerNode)
		{
			InnerNode<K,V> inner = (InnerNode<K,V>) curr;
			curr = inner.getChildForKey(key);
		}
		
		LeafNode<K,V> leaf = (LeafNode<K,V>) curr;
		V value = leaf.find(key);
		return value;
	}
	
	
	public String toString( )
	{
		return root.toString();
	}
	
}
