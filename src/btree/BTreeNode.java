package btree;

public abstract class BTreeNode {

	protected BTreeInnerNode parent;
	
	public BTreeNode(BTreeInnerNode parent)
	{
		this.parent = parent;
	}
}
