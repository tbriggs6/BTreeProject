package btree;

import java.util.ArrayList;

/**
 * Basic B-Tree node, serving and both inner- and leaf- node.  
 * Implements basic node with set maximum degree.  The maximum
 * degree controls the number of keys and children that the node
 * can store.
 * 
 * Inserting a value into a B-Tree node when it is full causes it
 * to split itself into two nodes.  The median value is then inserted
 * into the parent node, or if there is no parent node, a new parent
 * node is created (and it then becomes the root of the tree). 
 * 
 * If the node is a leaf node, then the relationship between keys and
 * children is with degree 2:
 * 
 *  <table border=1>
 *  <tr><td>Keys:</td><td>key1</td><td>key2</td><td></td></tr>
 *  <tr><td>Children:</td><td>vals &leq; key1</td><td>vals &leq; key2</td>vals &gt; key2</td></tr>
 *  </table>
 * 
 * If the node is an inner node, then the relationship between keys and
 * children is with degree 2:
 * 
 * <table border=1>
 * <tr><td>Keys:</td><td>key1</td><td>key2</td><tr>
 * <tr><td>Children:</td><td>vals &gt; key1</td><td>data for key1</td><td>key1...key2</td><td>data for key2</td><td>&gt; key2</td></tr>
 * </table>
 * 
 * Note that for the inner-nodes, the children alternate between inner-nodes and data nodes, such that all odd nodes
 * are data pointers, and even are inner nodes.  The corresponds to the fact that each key represents a separating
 * value between ranges of numbers, but each key also carries its own data pointer.   So, child[0] is the node
 * that holds values less than key[0].  child[1] holds the data node for key[0].  child[2] holds  values that
 * fall between key[0] and key[1].  child[3] is the data node for key[1], and child[4] holds all values greater than
 * key[1].  For higher-degree nodes, the pattern would continue.
 * 
 * @author tbriggs
 * @version 0.0
 */
public class BTreeInnerNode extends BTreeNode {
	
	/**
	 * Determines whether this node is a leaf node or an inner node.  
	 * Only leaf nodes will have pointers to data nodes
	 */
	protected boolean leaf;
	
	
	/**
	 * The maximum degree of the node ( a constant )
	 */
	protected final int maxDegree;
	
	/**
	 * The keys stored in the node. 
	 */
	protected ArrayList<Long> keys;
	
	/**
	 * The children of this node.
	 */
	protected ArrayList<BTreeNode> children;
	
	/**
	 * B-Tree constructor, giving the leaf-neess, degree, and the parent 
	 * @param leaf the leafness of this node (true means leaf, false means inner node)
	 * @param maxDegree the degree of the node
	 * @param parent the parent of the node (may be null if root)
	 */
	public BTreeInnerNode(boolean leaf, int maxDegree, BTreeInnerNode parent)
	{
		// initialize the base node 
		super(parent);
		
		// store leaf and degree
		this.leaf = leaf;
		this.maxDegree = maxDegree;
		
		// initializes keys and children
		keys = new ArrayList<Long>( maxDegree );
		children = new ArrayList<BTreeNode>( maxDegree + 1);
	}
	

	/**
	 * Create a string representation of the tree node.  The string will
	 * indicate the leaf-ness of the node and show its keys.
	 */
	public String toString( )
	{
		StringBuffer buff = new StringBuffer( );
		if (leaf) {
			buff.append("[Leaf: " + keys.toString() + "]");
		}
		else {
			buff.append("[Inner: " + keys.toString() + "]");
		}
		return buff.toString();
	}
	
	
	/**
	 * Insert a new key/child pair.  This will recursively descend through the
	 * inner nodes to reach a leaf node.
	 * 
	 * @param key - the key to insert
	 * @param value - the chiild to insert
	 */
	public void insert(long key, BTreeNode value)
	{
		if (leaf)  insertLeaf(key, value);
		else insertInner(key,value);
		
	}
	
	
	/**
	 * Insert a key/value pair into a leaf node.
	 * @param key 	the key to insert
	 * @param value  	the value to insert
	 */
	private void insertLeaf(long key, BTreeNode value)
	{
		// find the location to insert the new key/value pair.
		// the location is the first place there the new key is
		// less than the stored keye
		int i = 0;
		for (i = 0; i < keys.size(); i++)
		{
			// if the item is already in the tree, abort
			if (key == keys.get(i)) 
				throw new BTreeException("Error - key " + key + " is already in the tree");

			// the first time the new key is less than stored key
			if (key < keys.get(i))
				break;
		}
		
		// add the key/value pair to the arrays
		keys.add(i, key);
		children.add(i,  value);
		value.parent = this;
		
		// if the resulting node is full then it must be split
		if (keys.size() == maxDegree)
			splitLeaf();
	}

	/**
	 * Insert a key/value pair into an inner node
	 * @param key  the new key value to insert
	 * @param value  the corresponding data value
	 */
	private void insertInner(long key, BTreeNode value)
	{
		// if the key is less than the first value, insert to the first child
		if (key <= keys.get(0))
		{
			// it is an error if we ever get as far as calling insert on a data node 
			BTreeInnerNode child = (BTreeInnerNode) children.get(0);
			child.insert(key,  value);
		}
	
		// find where the new value falls between two existing keys
		int i = 0;
		for (i = 1; i < keys.size(); i++)
		{
			// value falls between two keys
			if ((key > (long) keys.get(i-1)) && (key < (long) keys.get(i))) {
				break;
			}
		}
		
		// handle inserting at the end 
		if (i == keys.size()) {
			// handle insert at last child
			BTreeInnerNode child = (BTreeInnerNode) children.get( children.size() - 1); 
			child.insert(key,  value);
		}
		// handle inserting in the middle
		else {
			BTreeInnerNode child = (BTreeInnerNode) children.get( i * 2); 
			child.insert(key,  value);
		}
			
		// if the node is full, it must be split
		if (keys.size() == maxDegree+1)
			splitInner();
	}
	
	
	/**
	 * Split this B-Tree node.  If it is a leaf-node, turn it into an
	 * internal node, with two children (now leaves), and then be finished.
	 * 
	 * If it is already an internal node, it could become full.  In that case,
	 * it into one other child, inserting its new child into its parent. 
	 * 
	 */
	protected void split( )
	{
		if (leaf) splitLeaf( );
		else splitInner( );
	}
	
	/**
	 * Split this node into three parts - the current node remains as the left
	 * part of the split; the middle key/value; and a new right node with the
	 * right half of the node.  The middle and right part are added to the
	 * parent.  If the parent is now full, then it is split (recurisvely).
	 */
	private void splitLeaf( )
	{
		// find the middle position
		int midPos = keys.size() / 2;
		
		// get and remember the middle key / value
		long midKey = keys.get(midPos);
		BTreeNode midValue = children.get(midPos);
		
		// make a new "right" node (the sibling to this one) and 
		// copy values into it
		BTreeInnerNode sibling = new BTreeInnerNode( true, maxDegree, parent);
		for (int i = midPos+1; i < keys.size(); i++)
		{
			sibling.keys.add( this.keys.get(i));
			BTreeNode child = this.children.get(i);
			child.parent = sibling;
			sibling.children.add( child );
		}
		
		// remove copied values from this node
		for (int i = midPos, e = keys.size(); i < e; i++)
		{
			this.keys.remove(midPos);
			this.children.remove(midPos);
		}
		
		
		// make a new root (if necessary)
		if (parent == null) {
			this.parent = new BTreeInnerNode(false, maxDegree, null);
			parent.children.add(this);
			parent.children.add(midValue);
			parent.children.add(sibling);
			
			midValue.parent = parent;
			sibling.parent = parent;
			
			parent.keys.add(midKey);
		}
		// if the parent is not null, then it must absorb the new key/value and sibling
		// nodes (and possibly split)
		else {
			parent.absorbSplit(this, sibling, midKey, midValue);
		}
		
	}
	
	/**
	 * Split the inner node into a left part (this node), a key/value payload, and a 
	 * right (sibling) part.  The key/value and sibling are inserted into the parent,
	 * possibly splitting the parent (recursively).
	 */
	private void splitInner( )
	{
		// get and remember the middle position, key, and values
		int midPos = keys.size()/2;
		
		long midKey = keys.get(midPos);
		BTreeNode midValue = children.get( keys.size() );
		
		// create a new sibling node
		BTreeInnerNode sibling = new BTreeInnerNode(false, maxDegree, this);
		
		// copy keys and children into the sibling
		int numKeys = maxDegree / 2;
		int numChildren = numKeys*2 + 1;
		
		while (sibling.keys.size() < numKeys)
		{			
			sibling.keys.add( keys.remove( midPos+1));
		}
		
		while (sibling.children.size() < numChildren)
		{
			BTreeNode child = children.remove(2 * (midPos+1));
			child.parent = sibling;
			sibling.children.add(child);
		}

		// remove copied keys & children from this node
		keys.remove(midPos);
		children.remove(2*midPos + 1);


		// make a new parent node (if this is root)
		if (parent == null) {
			parent = new BTreeInnerNode(false, maxDegree, null);
			
			parent.keys.add(midKey);
			parent.children.add(this);
			parent.children.add( midValue );
			parent.children.add(sibling);
	
			midValue.parent = parent;
			sibling.parent = parent;
		}
		// absorb the new nodes into the parent, and split it if necessary
		else {
			parent.absorbSplit(this, sibling, midKey, midValue);
		}
	}
	
	
	/**
	 * absorb split is the critical method for the B-Tree node.  when a child node becomes full it
	 * pushes values up into its parent.  When this happens, the parent node can become full with the
	 * new values, and then it must split, which can create new values that go into its parent, which 
	 * can also split, etc.  until the root splits and creates a new root.
	 * 
	 * @param child the child that generated the split
	 * @param sibling the new sibling of the child that split
	 * @param midKey the middle key of the child that split
	 * @param midChiild the middle payload of the child that split
	 */
	private void absorbSplit(BTreeInnerNode child, BTreeInnerNode sibling, long midKey, BTreeNode midChild)
	{
		// the child node being absorbed was once populous, but 
		// now has a single key (with data) and two other children
		int i = 0;
		for (i = 0; i < children.size(); i++ )
		{
			if (children.get(i) == child) break;
		}

		// add the key/value and children
		keys.add(i/2, midKey);
		children.add(i+1, midChild);
		children.add(i+2, sibling);
	
		child.parent = this;
		sibling.parent = this;
		midChild.parent = this;
		
		// if the node is full - then split
		if (keys.size() == maxDegree) {
			split( );
		}
	}
	
	/**
	 * Find the value associated with the given key
	 */
	protected Object find(long key)
	{
		if (leaf) {
			for (int i = 0; i < keys.size(); i++)
			{
				long childKey = keys.get(i);
				if (key == childKey) {
					BTreeNode child = children.get(i);
					if (child instanceof BTreeDataNode) return ((BTreeDataNode) child).payload;
					else return child.find(key);
				}
			}
			return null;
		}
		else {
			if (key < keys.get(0)) 
				return children.get(0).find(key);
			
			if (key == keys.get(0))
				return children.get(1).find(key);
			
			for (int i = 1; i < keys.size(); i++)
			{
				if (key == keys.get(i)) 
					return children.get((2 * i) + 1).find(key);
				
				if ((key > keys.get(i-1)) && (key < keys.get(i)))
				{
					return children.get(2 * i).find(key);
				}
			}
			
			int lastChild = 2 * keys.size();
			if (keys.get( keys.size()-1) == key) 
				return children.get(lastChild-1).find(key);
			
			return children.get( lastChild ).find(key);
		}
	}

	
}
