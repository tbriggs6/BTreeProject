package btree;

/**
 * The exception thrown by all of the BTree methods.   
 * 
 * As the project evolves, this will allow better error handling, 
 * such that by re-classifying this as an Exception, then it must 
 * be caught.
 * 
 * @author tbriggs
 */
public class BTreeException extends RuntimeException {

	public BTreeException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public BTreeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public BTreeException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public BTreeException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public BTreeException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
