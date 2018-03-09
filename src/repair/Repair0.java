package sharpfix.repair;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.apache.commons.io.FileUtils;
import sharpfix.util.ASTNodeLoader;
import sharpfix.util.ASTNodeFinder;
import sharpfix.codesearch.LocalCodeSearch;
import sharpfix.codesearch.SearchItem;
import sharpfix.global.*;
import sharpfix.patchgen.LocalPatchGenerator;
import sharpfix.patchgen.GlobalPatchGenerator;
import sharpfix.patchgen.GPatch;
import sharpfix.codesearch.CockerInvoker;
import sharpfix.rename.RenameFactory;

public class Repair0
{
    RepairInfo ri;
    CockerInvoker ci;

    public Repair0(RepairInfo repair_info) {
	ri = repair_info;
	ci = new CockerInvoker();
    }

    public Patch repair(String bfpath, String bstmt_loc, String idxfpath, int idxflag, String analmethod, String fix_dpath) {
	BuggyChunk bchunk = ChunkFactory.getBuggyChunk(bfpath, bstmt_loc);
	if (!bchunk.isValid()) {
	    System.err.println("Failed producing a chunk from "+bfpath+","+bstmt_loc);
	    return new Patch(null, false);
	}
	//File bf = new File(bfpath);
	CompilationUnit bcu = bchunk.getCompilationUnit();
	List<ASTNode> bnode_list = bchunk.getNodeList();
	ASTNode bnode = bnode_list.get(0);
	String[] bcnms = getClassNameAndMethodSignature(bnode);

	//Do code search
	boolean use_cache = ri.usesearchcache;
	int max_candidates = ri.maxcandidates;
	String sharpfix_dpath = ri.sharpfixdpath;
	String lsearch_rslt_fpath = fix_dpath+"/local_search_rslt";
	String gsearch_rslt_fpath = fix_dpath+"/global_search_rslt";
	File lsearch_rslt_f = new File(lsearch_rslt_fpath);
	File gsearch_rslt_f = new File(gsearch_rslt_fpath);
	File idxf = new File(idxfpath);
	LocalCodeSearch lcs = new LocalCodeSearch();
	List<String> lsearch_rslt_lines = null;
	List<String> gsearch_rslt_lines = null;
	if (use_cache && lsearch_rslt_f.exists()) {
	    try { lsearch_rslt_lines = FileUtils.readLines(lsearch_rslt_f, (String)null); }
	    catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
	}
	else {
	    //Do local code search
	    lsearch_rslt_lines = new ArrayList<String>();
	    List<SearchItem> si_list = lcs.search(bnode, bfpath, bstmt_loc, idxf, idxflag);
	    for (SearchItem si : si_list) {
		lsearch_rslt_lines.add(si.getPathLoc()+","+si.getScore());
	    }
	    try { FileUtils.writeLines(lsearch_rslt_f, lsearch_rslt_lines);  }
	    catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
	}

	if (use_cache && gsearch_rslt_f.exists()) {
	    try { gsearch_rslt_lines = FileUtils.readLines(gsearch_rslt_f, (String)null); }
	    catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
	}
	else {
	    //Do cocker code search
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
		    bmd_loc = "slc:" + bmd_startln + "," + bcu.getColumnNumber(bmd_startpos);
		    break;
		}
		bnode_par = bnode_par.getParent();
	    }
	    if (bmd_loc == null) {
		System.err.println("Cannot locate the parent method of the target suspicious statement.");
		return new Patch(null, false);
	    }
	    if (bmd_length_in_lines <= 200) {
		gsearch_rslt_lines = ci.invoke(bfpath, bmd_loc, "kgram3wordmd", gsearch_rslt_fpath, 3l);
	    }
	}

	if (lsearch_rslt_lines == null) {
	    lsearch_rslt_lines = new ArrayList<String>();
	}
	if (gsearch_rslt_lines == null) {
	    gsearch_rslt_lines = new ArrayList<String>();
	}
	
	int lsearch_rslt_lines_size = lsearch_rslt_lines.size();
	int gsearch_rslt_lines_size = gsearch_rslt_lines.size();
	float lsearch_highest_score = -1, gsearch_highest_score = -1;
	if (lsearch_rslt_lines_size > 0) {
	    String lsearch_rslt_line0 = lsearch_rslt_lines.get(0);
	    int split_index1 = lsearch_rslt_line0.lastIndexOf(",");
	    lsearch_highest_score = Float.parseFloat(lsearch_rslt_line0.substring(split_index1+1).trim());
	}
	if (gsearch_rslt_lines_size > 0) {
	    String gsearch_rslt_line0 = gsearch_rslt_lines.get(0);
	    int split_index1 = gsearch_rslt_line0.lastIndexOf(",");
	    gsearch_highest_score = Float.parseFloat(gsearch_rslt_line0.substring(split_index1+1).trim());
	}

	//Initialize the patch generators
	Set<String> tested_pset = new HashSet<String>();
	LocalPatchGenerator lpgen = new LocalPatchGenerator(ri);
	GlobalPatchGenerator gpgen = new GlobalPatchGenerator(ri);

	//Repair using local & global candidates
	int i=0, j=0;
	int candidate_ignore_flag = ri.cignoreflag;
	int cchunk_count = 0;
	int tested_num = 0;
	Patch rslt_patch = null;
	while (i<lsearch_rslt_lines_size || j<gsearch_rslt_lines_size) {
	    if (cchunk_count >= max_candidates) { break; }
	    String lsearch_rslt_line = null, gsearch_rslt_line = null;
	    int lsplit_index1 = -1, gsplit_index1 = -1;
	    float lpscore = -1, gpscore = -1;
	    if (i<lsearch_rslt_lines_size) {
		lsearch_rslt_line = lsearch_rslt_lines.get(i);
		lsplit_index1 = lsearch_rslt_line.lastIndexOf(",");
		lpscore = Float.parseFloat(lsearch_rslt_line.substring(lsplit_index1+1).trim()) / lsearch_highest_score;
	    }
	    if (j<gsearch_rslt_lines_size) {
		gsearch_rslt_line = gsearch_rslt_lines.get(j);
		gsplit_index1 = gsearch_rslt_line.lastIndexOf(",");
		gpscore = Float.parseFloat(gsearch_rslt_line.substring(gsplit_index1+1).trim()) / gsearch_highest_score;
	    }
	    
	    if (lpscore >= gpscore) {
		//Do Local Repair
		int lsplit_index0 = lsearch_rslt_line.indexOf(",");
		String lcfpath = lsearch_rslt_line.substring(0,lsplit_index0);
		String lcloc = lsearch_rslt_line.substring(lsplit_index0+1,lsplit_index1);
		System.out.println("Looking at Local Candidate " + i + " (pscore: " + lpscore+ ")");
		System.out.println(lsearch_rslt_line);

		//Note that we don't use "BuggyChunk bchunk = new BuggyChunk(bfpath, bstmtloc, bnode_list)" because bnode_list is unresolved
		CandidateChunk cchunk = ChunkFactory.getCandidateChunk(lcfpath, lcloc);
		if (!cchunk.isValid()) {
		    i += 1;
		    System.err.println("Failed in producing a valid chunk.");
		    continue;
		}
		cchunk_count += 1;
		
		CandidateChunk rcchunk = RenameFactory.getRenamedCandidateChunk(bchunk, cchunk);
		if (!rcchunk.isValid()) {
		    System.err.println("Renaming failure, so use the original cchunk instead.");
		    rcchunk = cchunk;
		}
		String fix_dpath0 = fix_dpath + "/repair";
		String fix_dpath1 = fix_dpath0 + "/c" + cchunk_count;
		File fix_d0 = new File(fix_dpath0);
		File fix_d1 = new File(fix_dpath1);
		if (!fix_d0.exists()) { fix_d0.mkdir(); }
		if (!fix_d1.exists()) { fix_d1.mkdir(); }
		Patch patch = lpgen.generatePatch(bchunk, rcchunk, fix_dpath1, tested_pset);
		if (patch == null) {
		    System.out.println("Local Candidate " + i + " Failed.");
		}
		else if (patch.isCorrect()) {
		    System.out.println("Found Plausible Patch at " + patch.getFilePath());
		    System.out.println("Candidate Rank: " + cchunk_count);
		    System.out.println("Modification Type: " + patch.getModificationType());
		    System.out.println("Patch Height Size: " + patch.getHeightSize());
		    System.out.println("Patch String Similarity: " + patch.getStringSimilarity());
		    tested_num += patch.getTestedNum();
		    rslt_patch = new Patch(patch.getFilePath(), true);
		    break;
		}
		else {
		    System.out.println("Local Candidate Chunk "+i+" Failed.");
		    tested_num += patch.getTestedNum();
		}

		i += 1;
	    }
	    else {
		//Do Global Repair
		int gsplit_index0 = gsearch_rslt_line.indexOf(",");
		String gcfpath = gsearch_rslt_line.substring(0,gsplit_index0);
		if (gcfpath.startsWith("file://")) { gcfpath = gcfpath.substring(7); }
		String gcloc = gsearch_rslt_line.substring(gsplit_index0+1,gsplit_index1);
		System.out.println("Looking at Global Candidate " + j + " (pscore: "+ gpscore +")");
		System.out.println(gsearch_rslt_line);

		//Load the chunk
		CandidateChunk cchunk = ChunkFactory.getCandidateChunk(gcfpath, gcloc);
		if (!cchunk.isValid()) {
		    j += 1;
		    System.err.println("Failed in producing a valid chunk.");
		    continue;
		}
		CompilationUnit gccu = (CompilationUnit) cchunk.getCompilationUnit();
		List<ASTNode> gcnode_list = cchunk.getNodeList();

		//Ignore candidates that are from the bug-fixed versions
		String[] gccnms = getClassNameAndMethodSignature(gcnode_list.get(0));
		if (candidate_ignore_flag == 0 &&
		    (gccnms[0].equals(bcnms[0]) && gccnms[1].equals(bcnms[1]))) {
		    System.err.println("Candidate Ignored.");
		    j += 1;
		    continue;
		}
		if (candidate_ignore_flag == 1 && gccnms[0].equals(bcnms[0])) {
		    System.err.println("Candidate Ignored.");
		    j += 1;
		    continue;
		}
		if (candidate_ignore_flag == 2 && gccnms[1].equals(bcnms[1])) {
		    System.err.println("Candidate Ignored.");
		    j += 1;
		    continue;
		}
		
		CandidateChunk rcchunk = RenameFactory.getRenamedCandidateChunk(bchunk, cchunk);
		if (!rcchunk.isValid()) {
		    System.err.println("Renaming failure, so use the original cchunk instead.");
		    rcchunk = cchunk;
		}
		
		String fix_dpath0 = fix_dpath + "/repair";
		String fix_dpath1 = fix_dpath0 + "/c" + cchunk_count;
		File fix_d0 = new File(fix_dpath0);
		File fix_d1 = new File(fix_dpath1);
		if (!fix_d0.exists()) { fix_d0.mkdir(); }
		if (!fix_d1.exists()) { fix_d1.mkdir(); }
		GPatch gpatch = gpgen.generatePatch0(bchunk, rcchunk, fix_dpath1, tested_pset, false, false);
		cchunk_count += gpatch.getNumOfUsedCandidates(); //Increase by the number of used candidates. Could 0, 1, or more.
		Patch patch = gpatch.getPatch();
		
		if (patch == null) {
		    System.out.println("Global Candidate " +j+ " Failed.");
		}
		else if (patch.isCorrect()) {
		    System.out.println("Found Plausible Patch at " + patch.getFilePath());
		    System.out.println("Candidate Rank: " + cchunk_count);
		    System.out.println("Modification Type: " + patch.getModificationType());
		    System.out.println("Patch Height Size: " + patch.getHeightSize());
		    System.out.println("Patch String Similarity: " + patch.getStringSimilarity());
		    tested_num += patch.getTestedNum();
		    rslt_patch = new Patch(patch.getFilePath(), true);
		    break;
		}
		else {
		    System.out.println("Global Candidate Chunk "+j+" Failed.");
		    tested_num += patch.getTestedNum();
		}

		j += 1;
	    }
	}

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
