package btree;

import java.util.ArrayList;

/**
 * Basic B-Tree node, serving and both inner- and leaf- node.  
 * 
 * 
 * @author tbriggs
 * @version 0.0
 */
public class BTreeInnerNode extends BTreeNode {

	
	protected boolean leaf;
	protected int maxDegree;
	
	protected ArrayList<Long> keys;
	protected ArrayList<BTreeNode> children;
	
	public BTreeInnerNode(boolean leaf, int maxDegree, BTreeInnerNode parent)
	{
		super(parent);
		
		this.leaf = leaf;
		this.maxDegree = maxDegree;
		
		keys = new ArrayList<Long>( maxDegree );
		children = new ArrayList<BTreeNode>( maxDegree + 1);
		if (!leaf)
			children.ensureCapacity((maxDegree*2)+1);
	}
	

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
	
	
	private void insertLeaf(long key, BTreeNode value)
	{
		int i = 0;
		for (i = 0; i < keys.size(); i++)
		{
			if (key == keys.get(i))
				return;
			
			if (key < keys.get(i))
				break;
		}
		
		keys.add(i, key);
		children.add(i,  value);
		value.parent = this;
		
		if (keys.size() == maxDegree)
			splitLeaf();
		
		if (leaf) 
			assert(keys.size() == children.size());
		else
			assert((keys.size() * 2) + 1 == children.size()); 

	}
	
	private void insertInner(long key, BTreeNode value)
	{
		// if the key is less than the first value, insert to the first child
		if (key <= keys.get(0))
		{
			// it is an error if we ever get as far as calling insert on a data node 
			BTreeInnerNode child = (BTreeInnerNode) children.get(0);
			child.insert(key,  value);
		}
	
		int i = 0;
		for (i = 1; i < keys.size(); i++)
		{
			// value falls between two keys
			if ((key > (long) keys.get(i-1)) && (key < (long) keys.get(i))) {
				break;
			}
		}
		
		if (i == keys.size()) {
			// handle insert at last child
			BTreeInnerNode child = (BTreeInnerNode) children.get( children.size() - 1); 
			child.insert(key,  value);
		}
		else {
			BTreeInnerNode child = (BTreeInnerNode) children.get( i * 2); 
			child.insert(key,  value);
		}
			
		if (keys.size() == maxDegree+1)
			splitInner();
		
		if (leaf) 
			assert(keys.size() == children.size());
		else
			assert((keys.size() * 2) + 1 == children.size()); 
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
	
	private void splitLeaf( )
	{
		int midPos = keys.size() / 2;
		
		long midKey = keys.get(midPos);
		BTreeNode midValue = children.get(midPos);
		
		BTreeInnerNode sibling = new BTreeInnerNode( true, maxDegree, parent);
		for (int i = midPos+1; i < keys.size(); i++)
		{
			sibling.keys.add( this.keys.get(i));
			BTreeNode child = this.children.get(i);
			child.parent = sibling;
			sibling.children.add( child );
		}
		
		for (int i = midPos, e = keys.size(); i < e; i++)
		{
			this.keys.remove(midPos);
			this.children.remove(midPos);
		}
		
		this.keys.ensureCapacity(maxDegree);
		this.children.ensureCapacity(maxDegree * 2 + 1);
		
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
		else {
			parent.absorbSplit(this, sibling, midKey, midValue);
		}
		
	}
	
	private void splitInner( )
	{
		int midPos = keys.size()/2;
		
		long midKey = keys.get(midPos);
		BTreeNode midValue = children.get( keys.size() );
		assert(midValue instanceof BTreeDataNode);
		
		BTreeInnerNode sibling = new BTreeInnerNode(false, maxDegree, this);
		
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

		// remove the middle item
		keys.remove(midPos);
		children.remove(2*midPos + 1);

		this.keys.ensureCapacity(maxDegree);
		this.children.ensureCapacity(maxDegree * 2 + 1);

		
		if (parent == null) {
			parent = new BTreeInnerNode(false, maxDegree, null);
			
			parent.keys.add(midKey);
			parent.children.add(this);
			parent.children.add( midValue );
			parent.children.add(sibling);
	
			midValue.parent = parent;
			sibling.parent = parent;
		}
		else {
			parent.absorbSplit(this, sibling, midKey, midValue);
		}
	}
	
	
	/**
	 * This is the magic bullet for the BTree-method
	 * @param child
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
		
		keys.ensureCapacity(i/2);
		children.ensureCapacity(i+2);
		
		keys.add(i/2, midKey);
		children.add(i+1, midChild);
		children.add(i+2, sibling);
	
		child.parent = this;
		sibling.parent = this;
		midChild.parent = this;
		
		if (keys.size() == maxDegree) {
			split( );
		}
		
		
	}
}
