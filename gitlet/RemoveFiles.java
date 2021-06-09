package gitlet;

import java.io.Serializable;
import java.util.HashMap;

/** Class for representing the state of the currently removed files.
 * @author Citlallic Gutierrez*/
public class RemoveFiles implements Serializable {

    /** A hashmap mapping a file content sha to file name. */
    private HashMap<String, String> _removemap;

    /** Takes in a HMP in order to construct a
     * hashmap mapping a file content sha to file name. */
    RemoveFiles(HashMap<String, String> hmp) {
        _removemap = hmp;
    }

    /** Returns the hashmap mapping a file content sha to file name. */
    HashMap<String, String> getremovemap() {
        return _removemap;
    }
}
