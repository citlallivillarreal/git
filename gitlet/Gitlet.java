package gitlet;


import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/** Gitlet class at which is serializable and
 * handles the functionalities of gitlet.
 * @author Citlallic Gutierrez */

class Gitlet implements Serializable {
    /** Path to working directory. */
    private File cwd = new File(System.getProperty("user.dir"));
    /** Path to commits directory. */
    private File commits = Utils.join(".gitlet/", "Commits");
    /** Path to staged directory. */
    private File staged = Utils.join(".gitlet/", "Staged");
    /** Path to head pointers directory. */
    private File pointers = Utils.join(".gitlet/", "HEAD");
    /** Path to branch map directory. */
    private File branch = Utils.join(".gitlet/", "Branch Map");
    /** Path to commit map directory. */
    private File commitmap = Utils.join(".gitlet/", "Commit Map");
    /** Path to head commit file. */
    private File head = Utils.join(".gitlet/HEAD", "Head Commit");
    /** Path to head name file. */
    private File headname = Utils.join(".gitlet/HEAD", "Head Name");
    /** Path to commit map file. */
    private File commitmapmap = new File(".gitlet/Commit Map/Map");
    /** Path to branch map file. */
    private File branchmap = new File(".gitlet/Branch Map/Map");
    /** Path to remove map directory. */
    private File removemap = Utils.join(".gitlet/", "Remove Map");
    /** Path to remove map file. */
    private File removemapmap = new File(".gitlet/Remove Map/Map");
    /** Path to remove directory. */
    private File removed = Utils.join(".gitlet/", "Removed");

    /** Gitlet construct that initializes gitlet repository. */
    Gitlet() {
        File mainfolder = Utils.join(cwd, ".gitlet");
        if (!mainfolder .exists() && !mainfolder .isDirectory()) {
            if (mainfolder .mkdir()) {
                removed.mkdir(); removemap.mkdir();
                commits.mkdir();
                staged.mkdir(); pointers.mkdir();
                branch.mkdir(); commitmap.mkdir();
                Commit comt = new Commit(null, null, null, true);
                byte[] commitser = Utils.serialize(comt);
                String commitsha = Utils.sha1((Object) commitser);
                File commit = new File(".gitlet/Commits/" + commitsha);
                Utils.writeContents(commit, commitser);
                _head = commitsha;
                Utils.writeContents(head, _head);
                CommitFiles comfs = new CommitFiles(new
                        HashMap<String, String>());
                _commitmap = comfs.getcommitmap();
                byte[] comfsser = Utils.serialize(comfs);
                Utils.writeContents(commitmapmap, comfsser);
                RemoveFiles remfs = new RemoveFiles(new
                        HashMap<String, String>());
                _removemap = remfs.getremovemap();
                byte[] remfsser = Utils.serialize(remfs);
                Utils.writeContents(removemapmap, remfsser);
                Branch bra = new Branch(new HashMap<String, String>());
                _branchmap = bra.getbranchmap();
                _headname = "master";
                _branchmap.put(_headname, commitsha);
                Utils.writeContents(headname, _headname);
                byte[] bramapser = Utils.serialize(bra);
                Utils.writeContents(branchmap, bramapser);
            }

        }
    }
    /** Takes in a STRFILE and adds it to the staging area. */
    public void add(String strfile) {
        boolean notsame = true;
        File file = new File(strfile);
        if (!file.exists()) {
            Utils.message("File does not exist.");
            System.exit(0);
            throw new GitletException();
        }
        byte[] filecontents = Utils.readContents(file);
        String filecontsha = Utils.sha1(filecontents);
        String currcommitsha = Utils.readContentsAsString(head);
        File currcommitfile = new File(".gitlet/Commits/" + currcommitsha);
        Commit commit = Utils.readObject(currcommitfile, Commit.class);
        if (commit.getfiles() != null) {
            for (int i = 0; i < commit.getfiles().size(); i += 1) {
                if (commit.gethashfiles().containsKey(strfile)) {
                    String cfilecontsha = commit.gethashfiles().get(strfile);
                    if (cfilecontsha.equals(filecontsha)) {
                        notsame = false;
                    }
                }
            }
        }
        if (notsame) {
            CommitFiles comfils = Utils.readObject(commitmapmap,
                    CommitFiles.class);
            _commitmap = comfils.getcommitmap();
            _commitmap.put(filecontsha, strfile);
            File stagefile = new File(".gitlet/Staged/" + filecontsha);
            Utils.writeContents(stagefile, filecontents);
            CommitFiles comfils1 = new CommitFiles(_commitmap);
            byte [] comfilsser = Utils.serialize(comfils1);
            Utils.writeContents(commitmapmap, comfilsser);
        }
        RemoveFiles remof = Utils.readObject(removemapmap, RemoveFiles.class);
        _removemap = remof.getremovemap();
        if (_removemap.containsKey(filecontsha)) {
            _removemap.remove(filecontsha);
        }
        RemoveFiles rp = new RemoveFiles(_removemap);
        byte [] remofser = Utils.serialize(rp);
        Utils.writeContents(removemapmap, remofser);

    }
    /** Takes in a MSG and makes a commit. */
    void commit(String msg) {
        CommitFiles comf = Utils.readObject(commitmapmap, CommitFiles.class);
        _commitmap = comf.getcommitmap();
        Branch branchf = Utils.readObject(branchmap, Branch.class);
        _branchmap = branchf.getbranchmap();
        RemoveFiles remof = Utils.readObject(removemapmap, RemoveFiles.class);
        _removemap = remof.getremovemap();
        if (_commitmap.size() == 0 && _removemap.size() == 0) {
            Utils.message("No changes added to the commit.");
            System.exit(0); throw new GitletException();
        }
        if (msg.equals("")) {
            Utils.message("Please enter a commit message.");
            System.exit(0); throw new GitletException();
        }
        String parcommitsha = Utils.readContentsAsString(head);
        File parcommitfile = new File(".gitlet/Commits/" + parcommitsha);
        Commit parcommit = Utils.readObject(parcommitfile, Commit.class);
        ArrayList<String> filescurrcom = parcommit.getfiles();
        Commit currcommit = new Commit(parcommitsha, msg, filescurrcom, false);
        for (int i = 0; i < _commitmap.size(); i += 1) {
            String currsha = (String) _commitmap.keySet().toArray()[i];
            String currfil = _commitmap.get(currsha);
            if (currcommit.gethashfiles() != null) {
                if (!currcommit.gethashfiles().containsValue(currsha)) {
                    currcommit.addfilehash(currfil, currsha);
                    currcommit.addfilestr(currfil);
                }
            } else {
                currcommit.addfilehash(currfil, currsha);
                currcommit.addfilestr(currfil);
            }
        }
        for (int i = 0; i < currcommit.gethashfiles().size(); i += 1) {
            String currfilesha = (String) currcommit.
                    gethashfiles().keySet().toArray()[i];
            String currfile = currcommit.gethashfiles().get(currfilesha);
            if (_removemap.containsKey(currfilesha)) {
                currcommit.gethashfiles().remove(currfilesha);
                currcommit.getfiles().remove(currfile);
            }
        }
        RemoveFiles remmap = new RemoveFiles(_removemap);
        byte [] remmapser = Utils.serialize(remmap);
        Utils.writeContents(removemapmap, remmapser);
        byte[] commitser = Utils.serialize(currcommit);
        String commitsha = Utils.sha1(commitser);
        File commitfile = new File(".gitlet/Commits/" + commitsha);
        Utils.writeContents(commitfile, commitser);
        _commitmap.clear();
        CommitFiles cp = new CommitFiles(_commitmap);
        byte [] commitmapser = Utils.serialize(cp);
        Utils.writeContents(commitmapmap, commitmapser);
        _head = commitsha; Utils.writeContents(head, _head);
        _headname = Utils.readContentsAsString(headname);
        _branchmap.put(_headname, _head);
        Branch bmap = new Branch(_branchmap);
        byte [] branchmapser = Utils.serialize(bmap);
        Utils.writeContents(branchmap, branchmapser);
    }

    /** Displays the log for gitlet. */
    void log() {
        boolean notinitial = true;
        _head =  Utils.readContentsAsString(head);
        while (notinitial) {
            File currcommitfile = new File(".gitlet/Commits/" + _head);
            Commit currentcommit = Utils.readObject(currcommitfile,
                    Commit.class);
            currentcommit.print(_head);
            _head = currentcommit.getpar();
            if (currentcommit.getpar() == null) {
                notinitial = false;
            }
        }
    }

    /** Takes in a FILESTR in order to updat, within
     * the working directory, a previous commited file. */
    void checkout(String filestr) {
        _head = Utils.readContentsAsString(head);
        File currcommitfile = new File(".gitlet/Commits/" + _head);
        Commit currentcommit = Utils.readObject(currcommitfile, Commit.class);
        HashMap<String, String> currhashfiles = currentcommit.gethashfiles();
        if (!currhashfiles.containsKey(filestr)) {
            Utils.message("File does not exist in that commit");
            System.exit(0);
            throw new GitletException();
        }
        for (int i = 0; i < currhashfiles.size(); i += 1) {
            String currfilestr = (String) currhashfiles.keySet().toArray()[i];
            if (currfilestr.equals(filestr)) {
                String currcommitsha = currentcommit.
                        gethashfiles().get(filestr);
                File filestgsha = new File(".gitlet/Staged/" + currcommitsha);
                byte [] filecontents = Utils.readContents(filestgsha);
                File filecurrfile = new File(filestr);
                Utils.writeContents(filecurrfile, filecontents);

            }

        }
    }
    /** Takes in COMMITID and FILESTR to update file in the working
     * directory to FILESTR which is within a particular commit. */
    void checkout(String commitid, String filestr) {
        if (commitid.length() < Utils.UID_LENGTH)  {
            String [] filecoms = commits.list();
            for (int i = 0; i < filecoms.length; i += 1) {
                String flcom = filecoms[i];
                if (flcom.contains(commitid)) {
                    commitid = flcom;
                }
            }
        }

        File currcommitfile = new File(".gitlet/Commits/" + commitid);
        Commit currentcommit = Utils.readObject(currcommitfile, Commit.class);
        HashMap<String, String> currhashfiles = currentcommit.gethashfiles();
        if (!currcommitfile.exists()) {
            Utils.message("No commit with that id exists.");
            System.exit(0);
            throw new GitletException();
        }
        if (!currhashfiles.containsKey(filestr)) {
            Utils.message("File does not exist in that commit");
            System.exit(0);
            throw new GitletException();
        }
        for (int i = 0; i < currhashfiles.size(); i += 1) {
            String currfilestr = (String) currhashfiles.keySet().toArray()[i];
            if (currfilestr.equals(filestr)) {
                String currcommitsha = currentcommit.
                        gethashfiles().get(filestr);
                File filestgsha = new File(".gitlet/Staged/" + currcommitsha);
                byte [] filecontents = Utils.readContents(filestgsha);
                File filecurrfile = new File(filestr);
                Utils.writeContents(filecurrfile, filecontents);
            }
        }
    }
    /** Takes in a BRANCHNAME and reverts it to the commit it points
     * to updating the working directory per the commit's state. */
    void checkoutbra(String branchname) {
        RemoveFiles remofs = Utils.readObject(removemapmap, RemoveFiles.class);
        CommitFiles comfs = Utils.readObject(commitmapmap, CommitFiles.class);
        _commitmap = comfs.getcommitmap(); _removemap = remofs.getremovemap();
        Branch curbh = Utils.readObject(branchmap, Branch.class);
        _headname = Utils.readContentsAsString(headname);
        _head = Utils.readContentsAsString(head);
        _branchmap = curbh.getbranchmap();
        if (!_branchmap.containsKey(branchname)) {
            Utils.message("No such branch exists.");
            System.exit(0); throw new GitletException();
        }
        if (_headname.equals(branchname)) {
            Utils.message("No need to checkout the current branch.");
            System.exit(0); throw new GitletException();
        }
        File prevcurrcomfil = new File(".gitlet/Commits/" + _head);
        Commit prevcom = Utils.readObject(prevcurrcomfil, Commit.class);
        HashMap<String, String> prevhf = prevcom.gethashfiles();
        _head = _branchmap.get(branchname);
        File currcomfil = new File(".gitlet/Commits/" + _head);
        Commit currentcommit = Utils.readObject(currcomfil, Commit.class);
        HashMap<String, String> currhfls = currentcommit.gethashfiles();
        for (int i = 0; i < prevhf.size(); i += 1) {
            for (int j = 0; j < currhfls.size(); j += 1) {
                String prevfilestr = (String) prevhf.keySet().toArray()[i];
                String currfilestr = (String) currhfls.keySet().toArray()[j];
                if (prevfilestr.equals(currfilestr)) {
                    String prefilesha = prevcom.gethashfiles().get(prevfilestr);
                    String currfilesha = currentcommit.
                            gethashfiles().get(currfilestr);
                    if (prefilesha.equals(currfilesha)) {
                        Utils.message("There is an untracked file in "
                                + "the way; delete it or add it first.");
                        System.exit(0); throw new GitletException();
                    }
                }
            }
        }
        for (int i = 0; i < prevhf.size(); i += 1) {
            String prevfilestr = (String) prevhf.keySet().toArray()[i];
            if (!currhfls.containsKey(prevfilestr)) {
                Utils.restrictedDelete(prevfilestr);
            }
        }
        for (int i = 0; i < currhfls.size(); i += 1) {
            String currfilestr = (String) currhfls.keySet().toArray()[i];
            String currcomsha = currentcommit.gethashfiles().get(currfilestr);
            File filestgsha = new File(".gitlet/Staged/" + currcomsha);
            byte[] fcons = Utils.readContents(filestgsha);
            File fe = new File(currfilestr); Utils.writeContents(fe, fcons);
        }
        _commitmap.clear(); _headname = branchname;
        byte [] comfser = Utils.serialize(comfs);
        byte [] remofser = Utils.serialize(remofs);
        Utils.writeContents(commitmapmap, comfser);
        Utils.writeContents(removemapmap, remofser);
        Utils.writeContents(head, _head);
        Utils.writeContents(headname, _headname);
    }

    /**Takes a FILESTR and removes it
     * from the working directory. */
    void rm(String filestr) {
        CommitFiles comfs = Utils.readObject(commitmapmap, CommitFiles.class);
        _commitmap = comfs.getcommitmap();
        RemoveFiles remfs = Utils.readObject(removemapmap, RemoveFiles.class);
        _removemap = remfs.getremovemap();
        _head = Utils.readContentsAsString(head);
        File currcomfil = new File(".gitlet/Commits/" + _head);
        Commit currcommit = Utils.readObject(currcomfil, Commit.class);
        ArrayList<String> currcomfiles = currcommit.getfiles();
        HashMap<String, String> currcomhashfls = currcommit.gethashfiles();
        if (_commitmap.containsValue(filestr)) {
            for (int i = 0; i < _commitmap.size(); i += 1) {
                String filecontsha = (String) _commitmap.keySet().toArray()[i];
                String strfile = _commitmap.get(filecontsha);
                if (filestr.equals(strfile)) {
                    _commitmap.remove(filecontsha);
                    break;
                }
            }
        } else if (currcomhashfls.containsKey(filestr)) {
            String filecontsha = currcomhashfls.get(filestr);
            _removemap.put(filecontsha, filestr);
            Utils.restrictedDelete(filestr);
        } else {
            Utils.message("No reason to remove file.");
            System.exit(0);
            throw new GitletException();
        }
        byte [] comfser = Utils.serialize(comfs);
        Utils.writeContents(commitmapmap, comfser);
        byte [] remfser = Utils.serialize(remfs);
        Utils.writeContents(removemapmap, remfser);
        Utils.writeContents(head, _head);
    }
    /** Displays the global log in terminal. */
    void goballog() {
        String [] filestrlst = commits.list();
        for (int i = 0; i < filestrlst.length; i += 1) {
            String filestr = filestrlst[i];
            File file = new File(".gitlet/Commits/" + filestr);
            Commit currcommit = Utils.readObject(file, Commit.class);
            currcommit.print(filestr);
        }

    }
    /** Finds a particular commit by it's MSG and
     * displays commit id in terminal. */
    void find(String msg) {
        boolean msgnotfound = true;
        String [] filestrlst = commits.list();
        for (int i = 0; i < filestrlst.length; i += 1) {
            String filestr = filestrlst[i];
            File file = new File(".gitlet/Commits/" + filestr);
            Commit currcommit = Utils.readObject(file, Commit.class);
            String currcommitmsg = currcommit.getmsg();
            if (currcommitmsg.equals(msg)) {
                msgnotfound = false;
                System.out.println(filestr);
            }
        }
        if (msgnotfound) {
            Utils.message("Found no commit with that message.");
            System.exit(0);
            throw new GitletException();
        }
    }
    /** Displays the status of gitlet in the terminal. */
    void status() {
        Branch currbranch = Utils.readObject(branchmap, Branch.class);
        _branchmap = currbranch.getbranchmap();
        _head = Utils.readContentsAsString(head);
        _headname = Utils.readContentsAsString(headname);
        System.out.println("=== Branches ===");
        System.out.println("* " + _headname);
        Object [] branamess =  _branchmap.keySet().toArray();
        Arrays.sort(branamess);
        for (int i = 0; i < branamess.length; i += 1) {
            Object currbraname = branamess[i];
            if (!currbraname.equals(_headname)) {
                System.out.println(currbraname);
            }
        }
        System.out.println(" ");
        System.out.println("=== Staged Files ===");
        CommitFiles currcomfls = Utils.readObject(commitmapmap,
                CommitFiles.class);
        _commitmap = currcomfls.getcommitmap();
        Object [] stgfiles = _commitmap.values().toArray();
        Arrays.sort(stgfiles);
        for (int i = 0; i < stgfiles.length; i += 1) {
            System.out.println(stgfiles[i]);
        }
        System.out.println(" ");
        System.out.println("=== Removed Files ===");
        RemoveFiles currremfls = Utils.readObject(removemapmap,
                RemoveFiles.class);
        _removemap = currremfls.getremovemap();
        Object [] remfls = _removemap.values().toArray();
        Arrays.sort(remfls);
        for (int i = 0; i < remfls.length; i += 1) {
            System.out.println(remfls[i]);
        }
        System.out.println(" ");
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println(" ");
        System.out.println("=== Untracked Files ===");
    }

    /** Takes in BRANCHNAME and makes a new branch for gitlet. */
    void branch(String branchname) {
        _head = Utils.readContentsAsString(head);
        Branch currbranch = Utils.readObject(branchmap, Branch.class);
        _branchmap = currbranch.getbranchmap();
        if (_branchmap.containsKey(branchname)) {
            Utils.message("A branch with that name already exists.");
            System.exit(0);
            throw new GitletException();
        }
        _branchmap.put(branchname, _head);
        byte [] currbraser = Utils.serialize(currbranch);
        Utils.writeContents(branchmap, currbraser);
    }

    /** Takes in BRANCHNAME and removes branch pointer from gitlet. */
    void rmbranch(String branchname) {
        _headname = Utils.readContentsAsString(headname);
        Branch currbranch = Utils.readObject(branchmap, Branch.class);
        _branchmap = currbranch.getbranchmap();
        if (!_branchmap.containsKey(branchname))  {
            Utils.message("A branch with that name does not exist.");
            System.exit(0);
            throw new GitletException();
        } else if (_headname.equals(branchname)) {
            Utils.message("Cannot remove the current branch.");
            System.exit(0);
            throw new GitletException();
        } else {
            _branchmap.remove(branchname);
        }
        byte [] currbraser = Utils.serialize(currbranch);
        Utils.writeContents(branchmap, currbraser);
    }
    /** Resets working directory back to a particular commit per COMMITID. */
    void reset(String commitid) {
        File currcommitfile = new File(".gitlet/Commits/" + commitid);
        if (!currcommitfile.exists()) {
            Utils.message("No commit with that id exists.");
            System.exit(0); throw new GitletException();
        }
        File prevcommitfile = new File(".gitlet/Commits/" + _head);
        Commit prevcommit = Utils.readObject(prevcommitfile, Commit.class);
        HashMap<String, String> prevcomhashmp = prevcommit.gethashfiles();
        Commit currcommit = Utils.readObject(currcommitfile, Commit.class);
        HashMap<String, String> currcomhashmp = currcommit.gethashfiles();
        for (int i = 0; i < prevcomhashmp.size(); i += 1) {
            for (int j = 0; j < currcomhashmp.size(); j += 1) {
                String prevfstr = (String) prevcomhashmp.keySet().toArray()[i];
                String currfstr = (String) currcomhashmp.keySet().toArray()[j];
                if (prevfstr.equals(currfstr)) {
                    String prefilesha = prevcommit.gethashfiles().get(prevfstr);
                    String currfsha = currcommit.gethashfiles().get(currfstr);
                    if (prefilesha.equals(currfsha)) {
                        Utils.message("There is an untracked file "
                                + "in the way; delete it or add it first.");
                        System.exit(0); throw new GitletException();
                    }
                }

            }

        }
        if (commitid.length() < Utils.UID_LENGTH)  {
            String [] filecoms = commits.list();
            for (int i = 0; i < filecoms.length; i += 1) {
                String flcom = filecoms[i];
                if (flcom.contains(commitid)) {
                    commitid = flcom;
                }
            }
        }

        for (int i = 0; i < currcomhashmp.size(); i += 1) {
            String currfilestr = (String) currcomhashmp.keySet().toArray()[i];
            String currcommitsha = currcommit.gethashfiles().get(currfilestr);
            File filestgsha = new File(".gitlet/Staged/" + currcommitsha);
            byte [] filecontents = Utils.readContents(filestgsha);
            File filecurrfile = new File(currfilestr);
            Utils.writeContents(filecurrfile, filecontents);
        }
        _head = Utils.readContentsAsString(head);
        for (int i = 0; i < prevcomhashmp.size(); i += 1) {
            String prevfilestr = (String) prevcomhashmp.keySet().toArray()[i];
            if (!currcomhashmp.containsKey(prevfilestr)) {
                Utils.restrictedDelete(prevfilestr);
            }
        }
        _head = commitid; Utils.writeContents(head, _head);
        CommitFiles comfs = Utils.readObject(commitmapmap, CommitFiles.class);
        _commitmap = comfs.getcommitmap(); _commitmap.clear();
        byte [] comfser = Utils.serialize(comfs);
        Utils.writeContents(commitmapmap, comfser);
    }



    /** A hashmap mapping a file content sha to file name. */
    private HashMap<String, String> _commitmap;
    /** A hashmap mapping a branch name to commit id. */
    private HashMap<String, String> _branchmap;
    /** A hashmap mapping a file content sha to file name. */
    private HashMap<String, String> _removemap;
    /** Representing the most active commit. */
    private String _head;
    /** Representing the name of the most active commit. */
    private String _headname;


}
