package sharpfix.codesearch;

import java.io.File;

public class D4JIndexMain
{
    private static String D4J_BUGS_DIR = "/home/qx5/defects4j_bugs";
    
    public static void main(String[] args) {
	String bugid = args[0];
	int index_flag = Integer.parseInt(args[1]);
	if (bugid.startsWith("Chart_") || bugid.startsWith("Closure_") ||
	    bugid.startsWith("Lang_") || bugid.startsWith("Math_") ||
	    bugid.startsWith("Time_")) {

	    String projid = bugid.substring(0, bugid.indexOf("_")).toLowerCase();
	    String proj_dpath = D4J_BUGS_DIR + "/bugs/" + projid + "s" + "/projs/" + bugid + "_buggy";
	    String src_dpath = getD4jSourceDirPath(proj_dpath);
	    String index_dpath = D4J_BUGS_DIR + "/index/index_flag_" + index_flag;
	    File index_dir = new File(index_dpath);
	    if (!index_dir.exists()) { index_dir.mkdirs(); }
	    String index_fpath = index_dpath + "/" + bugid;
	    File src_dir = new File(src_dpath);
	    File index_f = new File(index_fpath);
	    Indexer indexer = new Indexer(index_flag);
	    indexer.index(src_dir, index_f);
	}
	else {
	    System.err.println("Invalid D4J Bug ID: " + bugid);
	}
    }

    public static String getD4jSourceDirPath(String proj_dpath) {

	File dir0 = new File(proj_dpath+"/src/org");
	if (dir0.exists()) { return proj_dpath+"/src"; }

	File dir1 = new File(proj_dpath+"/src/main/java/org");
	if (dir1.exists()) { return proj_dpath+"/src/main/java"; }

	File dir2 = new File(proj_dpath+"/src/java/org");
	if (dir2.exists()) { return proj_dpath+"/src/java"; }

	File dir3 = new File(proj_dpath+"/source/org");
	if (dir3.exists()) { return proj_dpath+"/source"; }

	File dir4 = new File(proj_dpath+"/source/main/java/org");
	if (dir4.exists()) { return proj_dpath+"/source/main/java"; }

	File dir5 = new File(proj_dpath+"/source/java/org");
	if (dir5.exists()) { return proj_dpath+"/source/java"; }

	File dir6 = new File(proj_dpath+"/src/com");
	if (dir6.exists()) { return proj_dpath+"/src"; }

	return null;
    }
}
