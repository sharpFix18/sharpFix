package sharpfix.patchgen;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.HashSet;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import sharpfix.codesearch.Indexer;
import sharpfix.util.*;
import sharpfix.global.*;

public class GlobalPatchGenerator
{
    private LocalPatchGenerator lpgen;
    private int max_global_statement;

    public GlobalPatchGenerator(RepairInfo ri) {
	lpgen = new LocalPatchGenerator(ri);
	max_global_statement = ri.maxglobalstatement;
    }
    
    /* NOTE: The candidate chunk should already be !RENAMED! */

    /*
    public Patch generatePatch(String bfpath, String bstmt_loc, String cfpath, String cloc, String fix_dpath, Set<String> tested_pset) {
	return generatePatch(bfpath, bstmt_loc, cfpath, cloc, fix_dpath, tested_pset, false, false);
    }

    public Patch generatePatch(String bfpath, String bstmt_loc, String cfpath, String cloc, String fix_dpath, Set<String> tested_pset, boolean print_ccmatching, boolean print_patches) {
	BuggyChunk bchunk = ChunkFactory.getBuggyChunk(bfpath, bstmt_loc);
	CandidateChunk cchunk = ChunkFactory.getCandidateChunk(cfpath, cloc);
	if (!bchunk.isValid()) {
	    System.err.println("Failed producing a chunk from "+bfpath+","+bstmt_loc);
	    return new Patch(null, false);
	}
	if (!cchunk.isValid()) {
	    System.err.println("Failed producing a chunk from "+cfpath+","+cloc);
	    return new Patch(null, false);
	}
	return generatePatch(bchunk, cchunk, fix_dpath, tested_pset, print_ccmatching, print_patches);
    }
    */

    public Patch generatePatch(BuggyChunk bchunk, CandidateChunk cchunk, String fix_dpath, Set<String> tested_pset) {
	return generatePatch0(bchunk, cchunk, fix_dpath, tested_pset, false, false).getPatch();
    }
    
    public Patch generatePatch(BuggyChunk bchunk, CandidateChunk cchunk, String fix_dpath, Set<String> tested_pset, boolean print_ccmatching, boolean print_patches) {
	return generatePatch0(bchunk, cchunk, fix_dpath, tested_pset, print_ccmatching, print_patches).getPatch();
    }

    public GPatch generatePatch0(BuggyChunk bchunk, CandidateChunk cchunk, String fix_dpath, Set<String> tested_pset, boolean print_ccmatching, boolean print_patches) {

	//Collect statements from cchunk (including nested statements)
	List<Statement> cstmt_list = Indexer.getStatements(cchunk.getNodeList());
	if (cstmt_list.isEmpty()) {
	    return new GPatch(new Patch(null, false), 0);
	}

	//Compute the similarities between statement in bchunk & each statement in cchunk
	//CompilationUnit ccu = (CompilationUnit) cstmt_list.get(0).getRoot();
	CompilationUnit ccu = cchunk.getCompilationUnit();
	ASTNode bnode = bchunk.getNodeList().get(0);
	List<String> bstmt_idx_strs = Indexer.getIndexStrings(bnode, 1); //Use idxflag=1
	int cstmt_list_size = cstmt_list.size();
	List<IndexScore> cis_list = new ArrayList<IndexScore>();
	for (int j=0; j<cstmt_list_size; j++) {
	    Statement cstmt = cstmt_list.get(j);
	    List<String> cstmt_idx_strs = null;
	    try { cstmt_idx_strs = Indexer.getIndexStrings(cstmt, 1); }  //Use idxflag=1
	    catch (Throwable t) {}
	    if (cstmt_idx_strs == null) { continue; }
	    float match_score = computeSimilarity(bstmt_idx_strs, cstmt_idx_strs);
	    if (match_score > 0) { cis_list.add(new IndexScore(j, match_score)); }
	}

	Collections.sort(cis_list, new Comparator<IndexScore>() {
		@Override public int compare(IndexScore is0, IndexScore is1) {
		    Float f1 = (Float) is0.getScore();
		    Float f2 = (Float) is1.getScore();
		    return f2.compareTo(f1);
		}
	    });

	//Look at the best matched statements in cchunk	for repair
	boolean found_patch = false;
	int cis_list_size = cis_list.size();
	int match_iter_size = (max_global_statement <= cis_list_size) ? max_global_statement : cis_list_size;
	int tested_patch_num = 0;
	for (int j=0; j<match_iter_size; j++) {
	    IndexScore cis = cis_list.get(j);
	    Statement cstmt = cstmt_list.get(cis.getIndex());
	    int cstmt_startpos = cstmt.getStartPosition();
	    String cstmt_loc = "slc:" + ccu.getLineNumber(cstmt_startpos) + "," + ccu.getColumnNumber(cstmt_startpos);
	    List<ASTNode> cnode_list0 = new ArrayList<ASTNode>();
	    cnode_list0.add(cstmt);

	    //We look at only one statement for building bchunk & cchunk
	    CandidateChunk cchunk0 = new CandidateChunk(cchunk.getFilePath(), cstmt_loc, cnode_list0);

	    String fix_dpath2 = fix_dpath + "/m" + j;
	    File fix_d2 = new File(fix_dpath2);
	    if (!fix_d2.exists()) { fix_d2.mkdir(); }
	    Patch patch = lpgen.generatePatch(bchunk, cchunk0, fix_dpath2, tested_pset, print_ccmatching, print_patches);
	    if (patch != null) {
		tested_patch_num += patch.getTestedNum();
		if (patch.isCorrect()) {
		    patch.setTestedNum(tested_patch_num);
		    return new GPatch(patch, j+1);
		}
	    }
	}

	Patch failed_patch = new Patch(null, false);
	failed_patch.setTestedNum(tested_patch_num);
	return new GPatch(failed_patch, match_iter_size);
    }
    
    private float computeSimilarity(List<String> tk_strs0, List<String> tk_strs1) {
	int tk_strs0_size = tk_strs0.size();
	int tk_strs1_size = tk_strs1.size();
	boolean[] matched_arr_b = new boolean[tk_strs1_size];
	int match_count = 0;
	for (int i=0; i<tk_strs0_size; i++) {
	    String tk_str0 = tk_strs0.get(i);
	    for (int j=0; j<tk_strs1_size; j++) {
		if (matched_arr_b[j]) { continue; }
		String tk_str1 = tk_strs1.get(j);
		if (tk_str0.equals(tk_str1)) {
		    match_count += 1;
		    matched_arr_b[j] = true;
		    break;
		}
	    }
	}
	return (2.0f * match_count) / (tk_strs0_size + tk_strs1_size);
    }
    
    private class IndexScore {
	int index;
	float score;
	
	public IndexScore(int i, float s) {
	    index = i;
	    score = s;
	}
	
	public int getIndex() { return index; }
	
	public float getScore() { return score; }
    }
}
