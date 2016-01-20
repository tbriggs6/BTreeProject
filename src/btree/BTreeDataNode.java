package btree;

public class BTreeDataNode<T> extends BTreeNode {

	T payload;
	
	public BTreeDataNode(BTreeInnerNode parent, T payload) {
		super(parent);

		this.payload = payload;
	}

	public String toString() { 
		return "[Data: " + payload.toString() + "]";

	}
	
	public T getPayload( )
	{
		return payload;
	}
	

	protected Object find(long key)
	{
		return payload;
	}
}
