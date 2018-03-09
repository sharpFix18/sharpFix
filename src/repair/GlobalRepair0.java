package sharpfix.repair;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.apache.commons.io.FileUtils;
import sharpfix.patchgen.*;
import sharpfix.util.ASTNodeLoader;
import sharpfix.util.ASTNodeFinder;
import sharpfix.global.*;
import sharpfix.codesearch.CockerInvoker;


public class GlobalRepair0
{
    RepairInfo ri;
    CockerInvoker ci;
    
    public GlobalRepair0(RepairInfo repair_info) {
	ri = repair_info;
	ci = new CockerInvoker();
    }
    
    public Patch repair(String bfpath, String bstmt_loc, String analmethod, String fix_dpath) {
	BuggyChunk bchunk = ChunkFactory.getBuggyChunk(bfpath, bstmt_loc);
	if (!bchunk.isValid()) {
	    System.err.println("Failed producing a chunk from "+bfpath+","+bstmt_loc);
	    return new Patch(null, false);
	}
	
	CompilationUnit bcu = bchunk.getCompilationUnit();
	List<ASTNode> bnode_list = bchunk.getNodeList();
	ASTNode bnode = bnode_list.get(0);
	String[] bcnms = getClassNameAndMethodSignature(bnode);

	//Code Search
	boolean use_cache = ri.usesearchcache;
	int max_candidates = ri.maxcandidates;
	String sharpfix_dpath = ri.sharpfixdpath;
	String search_rslt_fpath = fix_dpath + "/global_search_rslt";
	File search_rslt_f = new File(search_rslt_fpath);
	List<String> search_rslt_lines = null;
	if (use_cache && search_rslt_f.exists()) {
	    //Load search results
	    try { search_rslt_lines = FileUtils.readLines(search_rslt_f, (String) null); }
	    catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
	}
	else {
	    //Do code search
	    String bmd_loc = null;
	    int bmd_length_in_lines = -1;
	    ASTNode bnode_par = bnode;
	    while (bnode_par != null) {
		if (bnode_par instanceof MethodDeclaration) {
		    MethodDeclaration bmd = (MethodDeclaration) bnode_par;
		    int bmd_startpos = bmd.getStartPosition();
		    int bmd_startln = bcu.getLineNumber(bmd_startpos);
		    int bmd_endln = bcu.getLineNumber(bmd_startpos+bmd.getLength());
		    bmd_length_in_lines = bmd_endln - bmd_startln + 1;
		    bmd_loc = "slc:" + bcu.getLineNumber(bmd_startpos) + "," + bcu.getColumnNumber(bmd_startpos);
		    break;
		}
		bnode_par = bnode_par.getParent();
	    }
	    if (bmd_loc == null) {
		System.err.println("Cannot locate the parent method of the target suspicious statement.");
		return new Patch(null, false);
	    }
	    if (bmd_length_in_lines <= 100) {
		search_rslt_lines = ci.invoke(bfpath, bmd_loc, "kgram3wordmd", search_rslt_fpath, 3l);
	    }
	}
	if (search_rslt_lines == null) {
	    search_rslt_lines = new ArrayList<String>();
	}
	
	//Repair
	GlobalPatchGenerator pgen = new GlobalPatchGenerator(ri);
	Set<String> tested_pset = new HashSet<String>();
	
	int search_rslt_lines_size = search_rslt_lines.size();
	int cchunk_count = 0;
	int tested_num = 0;
	Patch rslt_patch = null;
	int candidate_ignore_flag = ri.cignoreflag;
	
	for (int i=0; i<search_rslt_lines_size; i++) {
	    if (cchunk_count > max_candidates) { break; }
	    String search_rslt_line = search_rslt_lines.get(i);
	    int split_index0 = search_rslt_line.indexOf(",");
	    int split_index1 = search_rslt_line.lastIndexOf(",");
	    String cfpath = search_rslt_line.substring(0, split_index0);
	    if (cfpath.startsWith("file://")) { cfpath = cfpath.substring(7); }
	    String cloc = search_rslt_line.substring(split_index0+1, split_index1);
	    String search_score = search_rslt_line.substring(split_index1+1);
	    System.out.println("Looking at Candidate " + i);
	    System.out.println(search_rslt_line);

	    CandidateChunk cchunk = ChunkFactory.getCandidateChunk(cfpath, cloc);
	    if (!cchunk.isValid()) {
		System.err.println("Failed in producing a valid chunk.");
		continue;
	    }
	    
	    CompilationUnit ccu = (CompilationUnit) cchunk.getCompilationUnit();
	    List<ASTNode> cnode_list = cchunk.getNodeList();
	    String[] ccnms = getClassNameAndMethodSignature(cnode_list.get(0));
	    if (candidate_ignore_flag == 0 &&
		(ccnms[0].equals(bcnms[0]) && ccnms[1].equals(bcnms[1]))) {
		System.err.println("Candidate Ignored.");
		continue;
	    }
	    if (candidate_ignore_flag == 1 && ccnms[0].equals(bcnms[0])) {
		System.err.println("Candidate Ignored.");
		continue;
	    }
	    if (candidate_ignore_flag == 2 && ccnms[1].equals(bcnms[1])) {
		System.err.println("Candidate Ignored.");
		continue;
	    }

	    cchunk_count += 1;
	    String fix_dpath0 = fix_dpath + "/globalrepair";
	    String fix_dpath1 = fix_dpath0 + "/c" + i;
	    File fix_d0 = new File(fix_dpath0);
	    File fix_d1 = new File(fix_dpath1);
	    if (!fix_d0.exists()) { fix_d0.mkdir(); }
	    if (!fix_d1.exists()) { fix_d1.mkdir(); }
	    Patch patch = pgen.generatePatch(bchunk, cchunk, fix_dpath1, tested_pset);

	    if (patch == null) {
		System.out.println("Candidate " +i+ " Failed.");
	    }
	    else if (patch.isCorrect()) {
		System.out.println("Found Plausible Patch at " + patch.getFilePath());
		System.out.println("Candidate Rank: " + cchunk_count);
		tested_num += patch.getTestedNum();
		rslt_patch = new Patch(patch.getFilePath(), true);
		break;
	    }
	    else {
		System.out.println("Candidate Chunk "+i+" Failed.");
		tested_num += patch.getTestedNum();
	    }
	}
	
	System.out.println("Tested Patch Num: " + tested_num);
	if (rslt_patch == null) { rslt_patch = new Patch(null, false); }
	rslt_patch.setTestedNum(tested_num);
	return rslt_patch;
    }

    private String[] getClassNameAndMethodSignature(ASTNode tnode) {
	String cname = "";
	String msig = "";
	CompilationUnit cu = (CompilationUnit) tnode.getRoot();
	AbstractTypeDeclaration atd = null;
	MethodDeclaration md = null;
	ASTNode currnode = tnode;
	while (currnode != null) {
	    if ((md==null) && (currnode instanceof MethodDeclaration)) {
		md = (MethodDeclaration) currnode; //Find the first enclosing method
	    }
	    if (currnode instanceof AbstractTypeDeclaration) {
		atd = (AbstractTypeDeclaration) currnode; //Find the last enclosing class
	    }
	    currnode = currnode.getParent();
	}
	if (md != null) {
	    msig = getMethodSignatureString(md);
	}
	if (atd != null) {
	    cname = atd.getName().getIdentifier();
	    PackageDeclaration pd = cu.getPackage();
	    if (pd != null) { cname = pd.getName().toString()+"."+cname; }
	}
	if (cname.startsWith("org.apache.commons.lang3")) {
	    cname = cname.replace("org.apache.commons.lang3","org.apache.commons.lang");
	}
	else if (cname.startsWith("org.apache.commons.math3")) {
	    cname = cname.replace("org.apache.commons.math3","org.apache.commons.math");
	}
	return new String[] { cname, msig };
    }

    private String getMethodSignatureString(MethodDeclaration md) {
	String mname = md.getName().getIdentifier();
	String marg = null;
	List param_list = md.parameters();
	for (Object param_obj : param_list) {
	    SingleVariableDeclaration param_svd = (SingleVariableDeclaration) param_obj;
	    if (marg == null) { marg = param_svd.getType().toString(); }
	    else { marg += "$" + param_svd.getType().toString(); }
	}
	if (marg == null) { marg = ""; }
	return mname + "(" + marg + ")";
    }
}
