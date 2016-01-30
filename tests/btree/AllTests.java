package btree;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ TestBTree.class, TestEntry.class, TestInnerNode.class, TestLeafNode.class })

public class AllTests {

}
