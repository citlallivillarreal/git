package gitlet;


import java.io.File;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Citlalli Villarreal
 */
public class Main {

    /** Pathway to Working Directory. */
    static final String CWD = System.getProperty("user.dir");
    /** Pathway to .gitlet version control system. */
    static final File CWDGITLET = new File(CWD + "/.gitlet/mygitlet");
    /** Commands for gitlet. */
    static final String[] COMMANDS = new String[] { "init", "add", "commit",
                                                    "rm", "log", "global-log",
                                                    "find",
                                                    "status", "checkout",
                                                    "branch",
                                                    "rm-branch", "reset",
                                                    "merge"};

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) {
        if (args.length < 1) {
            Utils.message("Please enter a command.");
            System.exit(0); throw new GitletException();
        } else {
            if (isCommand(args[0])) {
                if (args[0].equals("init")) {
                    helperinit(args);
                } else if (CWDGITLET.isDirectory() && CWDGITLET.exists()) {
                    File gitletpath = new File(CWDGITLET + "/activate gitlet");
                    gitlet = Utils.readObject(gitletpath, Gitlet.class);
                    if (args[0].equals("add")) {
                        helperadd(args);
                    } else if (args[0].equals("commit")) {
                        helpercommit(args);
                    } else if (args[0].equals("rm")) {
                        helperrm(args);
                    } else if (args[0].equals("log")) {
                        helperlog(args);
                    } else if (args[0].equals("global-log")) {
                        helperglolog(args);
                    } else if (args[0].equals("find")) {
                        helperfind(args);
                    } else if (args[0].equals("status")) {
                        helperstatus(args);
                    } else if (args[0].equals("checkout")) {
                        helpercheckout(args);
                    } else if (args[0].equals("branch")) {
                        if (args.length == 2) {
                            gitlet.branch(args[1]);
                        } else {
                            Utils.message("Incorrect operands.");
                            System.exit(0); throw new GitletException();
                        }
                    } else if (args[0].equals("rm-branch")) {
                        if (args.length == 2) {
                            gitlet.rmbranch(args[1]);
                        } else {
                            Utils.message("Incorrect operands.");
                            System.exit(0); throw new GitletException();
                        }
                    } else if (args[0].equals("reset")) {
                        if (args.length == 2) {
                            gitlet.reset(args[1]);
                        } else {
                            Utils.message("Incorrect operands.");
                            System.exit(0); throw new GitletException();
                        }
                    } else {
                        Utils.message("No command with that name exists.");
                        System.exit(0); throw new GitletException();
                    }
                } else {
                    Utils.message("Not in an initialized Gitlet directory.");
                    System.exit(0); throw new GitletException();
                }
            }
        }
    }

    /** Returns true if COMMAND is a valid command for gitlet. */
    static boolean isCommand(String command) {
        boolean iscommand = false;
        for (int i = 0; i < COMMANDS.length; i += 1) {
            if (command.equals(COMMANDS[i])) {
                iscommand = true;
                break;
            }
        }
        return iscommand;
    }
    /** Takes in a string of ARGS and helps with initialization of .gitlet. */
    static void helperinit(String... args) {
        if (args.length < 2) {
            if (!CWDGITLET.isDirectory() && !CWDGITLET.exists()) {
                gitlet = new Gitlet(); CWDGITLET.mkdir();
                File gitletpath = new File(CWDGITLET
                        + "/activate gitlet");
                byte[] git = Utils.serialize(gitlet);
                Utils.writeContents(gitletpath, git);
            } else {
                Utils.message("A Gitlet version-control system "
                        + "already exists in the "
                        + "current directory.");
                System.exit(0); throw new GitletException();
            }
        } else {
            Utils.message("Incorrect operands.");
            System.exit(0); throw new GitletException();
        }
    }
    /** Takes in a string of ARGS and helps with
     * adding files to staging area .gitlet. */
    static void helperadd(String... args) {
        if (args.length == 2) {
            gitlet.add(args[1]);
        } else if (args.length > 2) {
            for (int i = 1; i < args.length; i += 1) {
                gitlet.add(args[i]);
            }
        } else {
            Utils.message("Incorrect operands.");
            System.exit(0); throw new GitletException();
        }

    }
    /** Takes in a string of ARGS and helps with
     * commiting files in .gitlet. */
    static void helpercommit(String... args) {
        if (args.length == 2) {
            gitlet.commit(args[1]);
        } else {
            Utils.message("Incorrect operands.");
            System.exit(0); throw new GitletException();
        }
    }
    /** Takes in a string of ARGS and helps with
     * removing files in .gitlet. */
    static void helperrm(String... args) {
        if (args.length == 2) {
            gitlet.rm(args[1]);
        } else if (args.length > 2) {
            for (int i = 1; i < args.length; i += 1) {
                gitlet.rm(args[i]);
            }
        } else {
            Utils.message("Incorrect operands.");
            System.exit(0); throw new GitletException();
        }
    }
    /** Takes in a string of ARGS and helps with
     * displaying log for .gitlet. */
    static void helperlog(String... args) {
        if (args.length == 1) {
            gitlet.log();
        } else {
            Utils.message("Incorrect operands.");
            System.exit(0); throw new GitletException();
        }
    }
    /** Takes in a string of ARGS and helps with
     * displaying global log for .gitlet. */
    static void helperglolog(String... args) {
        if (args.length == 1) {
            gitlet.goballog();
        } else {
            Utils.message("Incorrect operands.");
            System.exit(0); throw new GitletException();
        }
    }
    /** Takes in a string of ARGS and helps with
     * finding if a file exist .gitlet. */
    static void helperfind(String... args) {
        if (args.length == 2) {
            gitlet.find(args[1]);
        } else {
            Utils.message("Incorrect operands.");
            System.exit(0); throw new GitletException();
        }
    }
    /** Takes in a string of ARGS and helps with
     * displaying the status .gitlet. */
    static void helperstatus(String... args) {
        if (args.length == 1) {
            gitlet.status();
        } else {
            Utils.message("Incorrect operands.");
            System.exit(0); throw new GitletException();
        }
    }
    /** Takes in a string of ARGS and helps with
     * checking out a commit .gitlet. */
    static void helpercheckout(String... args) {
        if (args.length == 3) {
            gitlet.checkout(args[2]);
        } else if (args.length == 4) {
            gitlet.checkout(args[1], args[3]);
        } else if (args.length == 2) {
            gitlet.checkoutbra(args[1]);
        } else {
            Utils.message("Incorrect operands.");
            System.exit(0); throw new GitletException();
        }
    }

    /** Systems gitlet repository. */
    private static Gitlet gitlet;
}


