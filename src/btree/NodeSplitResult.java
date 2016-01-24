package btree;

class NodeSplitResult<K extends Comparable<K>, V> 
{
	K key;
	BTreeNode<K,V> sibling;
}
