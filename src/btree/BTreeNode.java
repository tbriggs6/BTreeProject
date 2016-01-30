package btree;

public abstract class BTreeNode<K extends Comparable<K>, V>
{

	abstract boolean isOverCapacity( );
	
	abstract boolean isComplete( );
	
	abstract boolean isEmpty( );
	
	abstract NodeSplitResult split( );
	
	abstract K getMaxKey( );
	abstract K getMinKey( );
}
