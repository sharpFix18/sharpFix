package sharpfix.repair;

import java.util.List;
import java.util.ArrayList;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import fauloc.*;
import sharpfix.util.ASTNodeLoader;
import sharpfix.util.ASTNodeFinder;
import sharpfix.global.*;

public class Repair
{
    RepairInfo ri;
    Repair0 repair0;
    
    public Repair(RepairInfo repair_info) {
	ri = repair_info;
	repair0 = new Repair0(repair_info);
    }

    public Patch repair(String idxfpath, int idxflag, String analmethod, String fix_dpath) {
	File fauloc_f = new File(fix_dpath+"/fauloc_rslt");
	if (!fauloc_f.exists()) {
	    System.err.println("Fault localization file not available.");
	    return new Patch(null, false);
	}
	List<String> fauloc_lines = null;
	try { fauloc_lines = FileUtils.readLines(fauloc_f, (String) null); }
	catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
	if (fauloc_lines == null) { return new Patch(null, false); }

	String proj_src_dpath = ri.projsrcdpath;
	List<SuspItem1> rslt_list0 = GZoltar0011ResultReader.read(proj_src_dpath, fauloc_lines);
	List<SuspItem1> rslt_list = new ArrayList<SuspItem1>();
	
	int fauloc_flag = ri.faulocflag;
	String bfpath = ri.bfpath;
	String bstmtloc = ri.bstmtloc;
	if (fauloc_flag == 1 && (bfpath != null)) {
	    //Fauloc by the buggy Package
	    String bfctnt = null;
	    try { bfctnt = FileUtils.readFileToString(new File(bfpath), (String) null); }
	    catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
	    String bpname = getPackageName(bfctnt);
	    if (bpname != null) {
		for (SuspItem1 rslt0 : rslt_list0) {
		    if (bpname.equals(rslt0.getPackageName())) {
			rslt_list.add(rslt0);
		    }
		}
	    }
	}
	else if (fauloc_flag == 2 && (bfpath != null)) {
	    //Fauloc by the buggy Package & buggy Class
	    String bfctnt = null;
	    File bf = new File(bfpath);
	    try { bfctnt = FileUtils.readFileToString(bf, (String) null); }
	    catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
	    String bpname = getPackageName(bfctnt);
	    String bcname = bf.getName();
	    if (bcname.endsWith(".java")) {
		bcname = bcname.substring(0, bcname.length()-5);
	    }
	    if (bpname != null && bcname != null) {
		for (SuspItem1 rslt0 : rslt_list0) {
		    if (bpname.equals(rslt0.getPackageName()) &&
			bcname.equals(rslt0.getClassName())) {
			rslt_list.add(rslt0);
		    }
		}
	    }
	}
	else if (fauloc_flag == 3 && (bfpath != null) && (bstmtloc != null)) {
	    //Fauloc by the buggy Package, buggy Class, & buggy Method
	    String bfctnt = null;
	    File bf = new File(bfpath);
	    try { bfctnt = FileUtils.readFileToString(bf, (String) null); }
	    catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
	    String bpname = getPackageName(bfctnt);
	    String bcname = bf.getName();
	    if (bcname.endsWith(".java")) {
		bcname = bcname.substring(0, bcname.length()-5);
	    }
	    String bmloc = getMethodLoc(bfctnt, bstmtloc);
	    if (bpname != null && bcname != null && bmloc != null) {
		for (SuspItem1 rslt0 : rslt_list0) {
		    if (bpname.equals(rslt0.getPackageName()) &&
			bcname.equals(rslt0.getClassName()) &&
			bmloc.equals(rslt0.getMethodDeclarationLoc())) {
			rslt_list.add(rslt0);
		    }
		}
	    }
	}
	else {
	    rslt_list = rslt_list0;
	}

	int max_faultylines = ri.maxfaultylines;
	int rslt_list_size = rslt_list.size();
	int fault_stmt_count = 0;
	int tested_num = 0;
	Patch rslt_patch = null;
	for (int i=0; i<rslt_list_size; i++) {
	    if (fault_stmt_count >= max_faultylines) { break; }
	    SuspItem1 si = rslt_list.get(i);
	    String pname = si.getPackageName();
	    String cname = si.getClassName();
	    String bstmt_loc = si.getStatementLoc();
	    bfpath = proj_src_dpath + "/" + pname.replace(".","/") + "/" + cname + ".java";
	    float fault_score = si.getScore();
	    File bf = new File(bfpath);
	    if (!bf.exists()) {
		System.err.println("Skip "+si.toString());
		System.err.println("Cannot identify the buggy file.");
		continue;
	    }
	    fault_stmt_count += 1;
	    System.err.println("Looking at Faulty Statement " + (fault_stmt_count-1) + " (fscore: " + fault_score + ")");
	    System.err.println(bfpath+","+bstmt_loc);

	    String fix_dpath0 = fix_dpath+"/f"+i;
	    File fix_d0 = new File(fix_dpath0);
	    if (!fix_d0.exists()) { fix_d0.mkdir(); }
	    Patch patch = repair0.repair(bfpath, bstmt_loc, idxfpath, idxflag, analmethod, fix_dpath0);

	    if (patch != null) { tested_num += patch.getTestedNum(); }
	    System.out.println("Current Tested Patch Num: " + tested_num);
	    if (patch != null && patch.isCorrect()) {
		rslt_patch = new Patch(patch.getFilePath(), true);
		break;
	    }
	}

	System.out.println("Tested Patch Num: " + tested_num);
	if (rslt_patch == null) { rslt_patch = new Patch(null, false); }
	rslt_patch.setTestedNum(tested_num);
	return rslt_patch;
    }

    private String getPackageName(String bfctnt) {
	if (bfctnt == null) { return null; }
	String[] bflines = bfctnt.trim().split("\n");
	for (String bfline : bflines) {
	    String bfline0 = bfline.trim();
	    if (bfline0.startsWith("package")) {
		return bfline0.substring(7, bfline0.indexOf(";")).trim();
	    }
	}
	return null;
    }

    private String getMethodLoc(String bfctnt, String bstmtloc) {
	CompilationUnit bcu = (CompilationUnit) ASTNodeLoader.getASTNode(bfctnt);
	List<ASTNode> found_nodes = ASTNodeFinder.find(bcu, bstmtloc);
	if (found_nodes.isEmpty() || found_nodes.get(0)==null) { return null; }
	ASTNode found_node = found_nodes.get(0);
	MethodDeclaration bmd = null;
	ASTNode curr_node = found_node;
	while (curr_node != null) {
	    if (curr_node instanceof MethodDeclaration) {
		bmd = (MethodDeclaration) curr_node;
		break;
	    }
	    curr_node = curr_node.getParent();
	}
	if (bmd == null) { return null; }
	int bmd_startpos = bmd.getStartPosition();
	String bmdloc = "slc:" + bcu.getLineNumber(bmd_startpos) + "," + bcu.getColumnNumber(bmd_startpos);
	return bmdloc;
    }
}
