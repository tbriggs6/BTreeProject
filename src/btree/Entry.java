package btree;

/**
 * An entry for a leaf-node.
 * 
 * @author tbriggs
 *
 * @param <K>
 * @param <V>
 */
class Entry<K extends Comparable<K>,V> implements Comparable<Entry<K,V>>
{
	K key;
	V value;

	Entry(K key, V value)
	{
		this.key = key;
		this.value = value;
	}
	
	public String toString( ) {
		return "(" + key.toString() + "->" + value.toString() + ")";
	}

	@Override
	public int compareTo(Entry<K, V> o) {
		return key.compareTo(o.key);
	}
}
