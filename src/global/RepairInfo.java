package sharpfix.global;

public class RepairInfo
{
    public int faulocflag;
    public int repairflag;
    public String bfpath;
    public String bstmtloc;
    public String bugid;
    public String dependjpath;
    public String projdpath;
    public String projsrcdpath;
    public String projbuilddpath;
    public String projtestbuilddpath;
    public String tsuitefpath;
    public String failedtestcases;
    public String outputdpath;
    public String sharpfixdpath;
    public String cockerdpath;
    public int maxfaultylines;
    public int maxcandidates;
    public String analysismethod;
    public String faulocfpath;
    public boolean usesearchcache;
    public int cignoreflag;
    public boolean useextendedcodebase;
    public boolean runparallel;
    public boolean deletefailedpatches;
    public int paralleltesting;
    public int maxvalidatepatch;
    public int maxglobalstatement;
    public boolean savetestingoutput;
    public String outputbugiddpath; //This is a deeper level directory under outputdpath, and shouldn't be set by the user

    public RepairInfo() {
	faulocflag = -1;
	repairflag = -1;
	bfpath = null;
	bstmtloc = null;
	bugid = null;
	dependjpath = null;
	projdpath = null;
	projsrcdpath = null;
	projbuilddpath = null;
	projtestbuilddpath = null;
	tsuitefpath = null;
	failedtestcases = null;
	outputdpath = null;
	sharpfixdpath = "/home/qx5/sharpfix";
	cockerdpath = "/home/qx5/cocker";
	maxfaultylines = 30;
	maxcandidates = 100;
	analysismethod = "lcs1";
	faulocfpath = null;
	usesearchcache = false;
	cignoreflag = 2;
	useextendedcodebase = false;
	runparallel = true;
	deletefailedpatches = false;
	paralleltesting = 4;
	maxvalidatepatch = 50;
	maxglobalstatement = 2;
	savetestingoutput = true;
	outputbugiddpath = null;
    }
}
