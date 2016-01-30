package btree;

/**
 * A representation of a split node.  The split function is common to
 * all node types and is declared in the BTreeNode abstract class.  This 
 * is just a convience to return to values - the split key, and the new 
 * sibling node from a split operation
 *  
 * @author tbriggs
 * @see BTreeNode
 * 
 * @param <K>
 * @param <V>
 */
class NodeSplitResult<K extends Comparable<K>, V> 
{
	K key;
	BTreeNode<K,V> sibling;
}
