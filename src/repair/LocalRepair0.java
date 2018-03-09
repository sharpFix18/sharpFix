package sharpfix.repair;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.apache.commons.io.FileUtils;
import sharpfix.codesearch.LocalCodeSearch;
import sharpfix.codesearch.SearchItem;
import sharpfix.patchgen.*;
import sharpfix.util.ASTNodeLoader;
import sharpfix.util.ASTNodeFinder;
import sharpfix.global.*;


public class LocalRepair0
{
    RepairInfo ri;
    
    public LocalRepair0(RepairInfo repair_info) {
	ri = repair_info;
    }
    
    public Patch repair(String bfpath, String bstmt_loc, String idxfpath, int idxflag, String fix_dpath) {
	BuggyChunk bchunk = ChunkFactory.getBuggyChunk(bfpath, bstmt_loc);
	if (!bchunk.isValid()) {
	    System.err.println("Failed producing a chunk from "+bfpath+","+bstmt_loc);
	    return new Patch(null, false);
	}
	CompilationUnit bcu = (CompilationUnit) bchunk.getCompilationUnit();
	List<ASTNode> bnode_list = bchunk.getNodeList();
	ASTNode bnode = bnode_list.get(0);

	//Code Search
	boolean use_cache = ri.usesearchcache;
	int max_candidates = ri.maxcandidates;
	String search_rslt_fpath = fix_dpath + "/local_search_rslt";
	File search_rslt_f = new File(search_rslt_fpath);
	File idxf = new File(idxfpath);
	LocalCodeSearch lcs = new LocalCodeSearch();
	List<String> search_rslt_lines = null;
	if (use_cache && search_rslt_f.exists()) {
	    //Load search results
	    try { search_rslt_lines = FileUtils.readLines(search_rslt_f, (String) null); }
	    catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
	}
	else {
	    //Do code search
	    search_rslt_lines = new ArrayList<String>();
	    List<SearchItem> si_list = lcs.search(bnode, bfpath, bstmt_loc, idxf, idxflag);
	    for (SearchItem si : si_list) {
		search_rslt_lines.add(si.getPathLoc()+","+si.getScore());
	    }
	    try { FileUtils.writeLines(search_rslt_f, search_rslt_lines);  }
	    catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
	}
	if (search_rslt_lines == null) {
	    search_rslt_lines = new ArrayList<String>();
	}
	
	//Repair
	LocalPatchGenerator pgen = new LocalPatchGenerator(ri);
	Set<String> tested_pset = new HashSet<String>();	
	int search_rslt_lines_size = search_rslt_lines.size();
	int iter_size = (search_rslt_lines_size <= max_candidates) ? search_rslt_lines_size : max_candidates;
	int cchunk_count = 0;
	int tested_num = 0;
	Patch rslt_patch = null;
	
	for (int i=0; i<iter_size; i++) {
	    String search_rslt_line = search_rslt_lines.get(i);
	    int split_index0 = search_rslt_line.indexOf(",");
	    int split_index1 = search_rslt_line.lastIndexOf(",");
	    String cfpath = search_rslt_line.substring(0, split_index0);
	    String cloc = search_rslt_line.substring(split_index0+1, split_index1);
	    String score = search_rslt_line.substring(split_index1+1);
	    System.out.println("Looking at Candidate " + i);
	    System.out.println(search_rslt_line);
	    cchunk_count += 1;

	    CandidateChunk cchunk = ChunkFactory.getCandidateChunk(cfpath, cloc);
	    if (!cchunk.isValid()) {
		System.err.println("Failed in producing a valid chunk.");
		continue;
	    }

	    String fix_dpath0 = fix_dpath + "/localrepair";
	    String fix_dpath1 = fix_dpath0 + "/c" + i;
	    File fix_d0 = new File(fix_dpath0);
	    File fix_d1 = new File(fix_dpath1);
	    if (!fix_d0.exists()) { fix_d0.mkdir(); }
	    if (!fix_d1.exists()) { fix_d1.mkdir(); }
	    Patch patch = pgen.generatePatch(bchunk, cchunk, fix_dpath1, tested_pset);
	    if (patch == null) {
		System.out.println("Candidate " + i + " Failed.");
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
}
