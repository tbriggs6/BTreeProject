package btree;

public abstract class BTreeNode<K extends Comparable<K>, V>
{

	abstract boolean isFull( );
	
	abstract boolean isEmpty( );
	
	abstract NodeSplitResult split( );
	
}
