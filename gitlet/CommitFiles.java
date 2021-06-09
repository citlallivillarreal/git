package gitlet;

import java.io.Serializable;
import java.util.HashMap;

/** Class for representing the state of the staged files.
 * @author Citlallic Gutierrez */
public class CommitFiles implements Serializable {

    /** A hashmap mapping a file content sha to file name. */
    private HashMap<String, String> _commitmap;

    /** Takes in a HMP in order to construct a
     * hashmap mapping a file content sha to file name. */
    CommitFiles(HashMap<String, String> hmp) {
        _commitmap = hmp;
    }
    /** Returns the hashmap mapping a file content sha to file name. */
    HashMap<String, String> getcommitmap() {
        return _commitmap;
    }
}
