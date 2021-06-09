package gitlet;

import java.io.Serializable;
import java.util.HashMap;

/** A branch class representing the branch pointers in gitlet.
 * @author Citlalli Villarreal */
public class Branch implements Serializable {
    /** A hashmap mapping a branch name to commit id. */
    private HashMap<String, String> _branchmap;

    /** Takes in a BRMAP in order to construct a
     * hashmap mapping a branch name to commit id. */
    Branch(HashMap<String, String> brmap) {
        _branchmap = brmap;
    }

    /** Returns the hashmap mapping a branch name to commit id. */
    HashMap<String, String> getbranchmap() {
        return _branchmap;
    }
}
