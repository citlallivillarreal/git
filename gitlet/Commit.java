package gitlet;

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/** Commit class for constructing and accessing
 *  particular attributes of a commit.
 *  @author Citlalli Villarreal*/
class Commit implements Serializable {
    /** Costructs a commit given a PARENT, MESSAGE, FILES, and INITIAL. */
    Commit(String parent, String message,
           ArrayList<String> files, boolean initial) {
        _parent = parent;
        _message = message;
        _files = files;
        if (_files == null) {
            _files = new ArrayList<String>();
        }
        _hashfiles = new HashMap<>();
        if (initial && message == null) {
            Date date = Date.from(Instant.EPOCH);
            DateFormat pe = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");
            _timestamp = pe.format(date);
            _message = "initial commit";
        } else {
            Date date = Date.from(Instant.now());
            DateFormat pe = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");
            _timestamp = pe.format(date);
        }
        if (_files != null) {
            for (int i = 0; i < _files.size(); i += 1) {
                String filestr = _files.get(i);
                File file = new File(filestr);
                byte [] filecont = Utils.readContents(file);
                String filecontsha1 = Utils.sha1(filecont);
                _hashfiles.put(filestr, filecontsha1);
            }
        }
    }

    /** Returns a list of files for commit. */
    ArrayList<String> getfiles() {
        return _files;
    }
    /** Returns the parent of the commit. */
    String getpar() {
        return _parent;
    }
    /** Returns the message of the commit. */
    String getmsg() {
        return _message;
    }
    /** Returns the timestamp of the commit. */
    String gettime() {
        return _timestamp;
    }
    /** Returns a hashmap mapping commit's files name to files content. */
    HashMap<String, String> gethashfiles() {
        return _hashfiles;
    }
    /** Takes in FILE and SHA1 in order to put it in the
     * hashmap mapping commit's files name to files content. */
    void addfilehash(String file, String sha1) {
        _hashfiles.put(file, sha1);
    }
    /** Takes in FILE and adds it to commits list of file names. */
    void addfilestr(String file) {
        _files.add(file);
    }
    /** Printing of the commit per it's SHA1 on the terminal. */
    void print(String sha1) {
        System.out.println("===");
        System.out.println("commit " + sha1);
        System.out.println("Date: " + gettime());
        System.out.println(getmsg());
        System.out.println();

    }

    /** String representing the parent's commit id. */
    private final String _parent;
    /** String representing the commit's message. */
    private String _message;
    /** Array list of strings representing the files with the commit. */
    private ArrayList<String> _files;
    /** String representing the timestamp of the commit. */
    private final String _timestamp;
    /** A hashmap mapping commit's files name to files content. */
    private HashMap<String, String> _hashfiles;
}
