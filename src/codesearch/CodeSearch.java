package sharpfix.codesearch;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import sharpfix.rename.RenameFactory;
import sharpfix.global.*;
import sharpfix.util.*;

public class CodeSearch
{
    private LocalCodeSearch lcs;

    public CodeSearch() { lcs = new LocalCodeSearch(); }
	
    public static void main(String[] args) {
	String bfpath = args[0];
	String bstmtloc = args[1];
	String idxfpath = args[2];
	int maxcandidates = Integer.parseInt(args[3]);
	boolean dorenaming = Boolean.parseBoolean(args[4]); //Find smaller chunks after doing renaming

	CodeSearch cs = new CodeSearch();
	List<String> rslt_list = cs.search(bfpath, bstmtloc, idxfpath, maxcandidates, dorenaming);
	for (String rslt : rslt_list) {
	    System.out.println(rslt);
	}
    }

    private List<String> search0(BuggyChunk bchunk, String bfpath, String bstmt_loc, String idxfpath, int max_candidates) {
	List<String> search_rslt_lines = new ArrayList<String>();
	List<String> lsearch_rslt_lines = new ArrayList<String>();
	List<String> gsearch_rslt_lines = new ArrayList<String>();

	CompilationUnit bcu = bchunk.getCompilationUnit();
	List<ASTNode> bnode_list = bchunk.getNodeList();
	ASTNode bnode = bnode_list.get(0);
	
	//Local Code Search using LCS1
	File idxf = new File(idxfpath);
	List<SearchItem> lsi_list = lcs.search(bnode, bfpath, bstmt_loc, idxf, 1);
	for (SearchItem lsi : lsi_list) {
	    lsearch_rslt_lines.add(lsi.getPathLoc()+","+lsi.getScore());
	}

	//Global Code Search using K3WMD
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
	}
	else {
	    CockerInvoker ci = new CockerInvoker();
	    gsearch_rslt_lines = ci.invoke(bfpath, bmd_loc, "kgram3wordmd", null, 300l); //When the last parameter is null, cocker doesn't write but only return the results
	}

	//Merge the results from local & global code search
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

	int i=0, j=0;
	int cchunk_count = 0;
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
		search_rslt_lines.add(lsearch_rslt_line);
		i += 1;
	    }
	    else {
		search_rslt_lines.add(gsearch_rslt_line);
		j += 1;
	    }

	    cchunk_count += 1;
	}

	return search_rslt_lines;
    }

    
    /* This method does not do any filtering (for any bug-fixed candidates or any
       redundant candidates). */
    public List<List<String>> search(String bfpath, String bstmt_loc, String idxfpath, int max_candidates) {
	List<List<String>> rslts = new ArrayList<List<String>>();
	BuggyChunk bchunk = ChunkFactory.getBuggyChunk(bfpath, bstmt_loc);
	if (!bchunk.isValid()) {
	    System.err.println("Failed producing a chunk from "+bfpath+","+bstmt_loc);
	    return rslts;
	}
	List<String> rslt_lines0 = search0(bchunk, bfpath, bstmt_loc, idxfpath, max_candidates);
	rslts.add(searchHelper(rslt_lines0, bchunk, false));
	rslts.add(searchHelper(rslt_lines0, bchunk, true));
	return rslts;
    }
    

    public List<String> search(String bfpath, String bstmt_loc, String idxfpath, int max_candidates, boolean do_renaming) {
	List<String> rslt_lines = new ArrayList<String>();
	BuggyChunk bchunk = ChunkFactory.getBuggyChunk(bfpath, bstmt_loc);
	if (!bchunk.isValid()) {
	    System.err.println("Failed producing a chunk from "+bfpath+","+bstmt_loc);
	    return rslt_lines;
	}
	//CompilationUnit bcu = bchunk.getCompilationUnit();
	//List<ASTNode> bnode_list = bchunk.getNodeList();
	//ASTNode bnode = bnode_list.get(0);

	//Get the results merged by local & global code search using LCS1 & K3WMD
	List<String> rslt_lines0 = search0(bchunk, bfpath, bstmt_loc, idxfpath, max_candidates);
	return searchHelper(rslt_lines0, bchunk, do_renaming);
    }

    public List<String> searchHelper(List<String> rslt_lines0, BuggyChunk bchunk, boolean do_renaming) {
	List<String> rslt_lines = new ArrayList<String>();

	//Create new results (finding smaller chunks from methods)
	int rslt_lines0_size = rslt_lines0.size();
	for (int i=0; i<rslt_lines0_size; i++) {
	    String rslt_line0 = rslt_lines0.get(i);
	    int split_i0 = rslt_line0.indexOf(",");
	    int split_i1 = rslt_line0.lastIndexOf(",");
	    String cfpath = rslt_line0.substring(0, split_i0);
	    if (cfpath.startsWith("/home/qx5/")) {
		rslt_lines.add(rslt_line0);
	    }
	    else if (cfpath.startsWith("file:///vol/")) {
		cfpath = cfpath.substring(7);
		String cloc = rslt_line0.substring(split_i0+1, split_i1);
		String score = rslt_line0.substring(split_i1+1);
		CandidateChunk cchunk0 = ChunkFactory.getCandidateChunk(cfpath, cloc); //This is supposed to be a method
		if (!cchunk0.isValid()) { continue; }
		CompilationUnit ccu0 = cchunk0.getCompilationUnit();

		if (!do_renaming) {
		    List<CandidateChunk> cchunks = getSmallerCandidateChunks(bchunk, cchunk0, 2); //Get two smaller cchunks
		    for (CandidateChunk cchunk : cchunks) {
			if (!cchunk.isValid()) { continue; }
			rslt_lines.add(cfpath+","+cchunk.getLoc()+","+score);
		    }
		}
		else {
		    CandidateChunk rcchunk0 = RenameFactory.getRenamedCandidateChunk(bchunk, cchunk0);
		    if (!rcchunk0.isValid()) { continue; }
		    List<CandidateChunk> rcchunks = getSmallerCandidateChunks(bchunk, rcchunk0, 2); //Get two smaller renamed cchunks
		    for (CandidateChunk rcchunk : rcchunks) {
			if (!rcchunk.isValid()) { continue; } //Shouldn't be executed
			List<TrackLoc> tracklocs = ASTNodeTracker.getTrackLocs(rcchunk.getNodeList().get(0));
			//We need to find the corresponding statements from the original, untranslated cchunk (i.e., the retrieved untranslated method)
			ASTNode cstmt_node = ASTNodeTracker.track(ccu0, tracklocs);
			if (cstmt_node != null) {
			    int cstmt_node_startpos = cstmt_node.getStartPosition();
			    String cstmt_node_loc = "slc:" + ccu0.getLineNumber(cstmt_node_startpos) + "," + ccu0.getColumnNumber(cstmt_node_startpos);
			    rslt_lines.add(cfpath+","+cstmt_node_loc+","+score);
			}
		    }
		}
	    }
	}

	return rslt_lines;
    }
	

    /* Get smaller chunks within cchunk. Each smaller chunk contains a single statement.
       Note that cchunk can be renamed chunk. In that case, the content of cchunk's 
       stored file is not translated, but the nodes contained in cchunk are translated.*/
    private List<CandidateChunk> getSmallerCandidateChunks(BuggyChunk bchunk, CandidateChunk cchunk, int number_of_chunks) {
	List<CandidateChunk> rslt_chunks = new ArrayList<CandidateChunk>();
	
	//Collect statements from cchunk (including nested statements)
	List<Statement> cstmt_list = Indexer.getStatements(cchunk.getNodeList());
	if (cstmt_list.isEmpty()) { return rslt_chunks; }

	//Compute the similarities between statement in bchunk & each statement in cchunk
	CompilationUnit ccu = cchunk.getCompilationUnit();
	ASTNode bnode = bchunk.getNodeList().get(0);
	List<String> bstmt_idx_strs = Indexer.getIndexStrings(bnode, 1); //Use idxflag=1
	int cstmt_list_size = cstmt_list.size();
	List<IndexScore> cis_list = new ArrayList<IndexScore>();
	for (int j=0; j<cstmt_list_size; j++) {
	    Statement cstmt = cstmt_list.get(j);
	    List<String> cstmt_idx_strs = null;
	    try { cstmt_idx_strs = Indexer.getIndexStrings(cstmt, 1); } //Use idxflag=1
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

	int cis_list_size = cis_list.size();
	int match_iter_size = (number_of_chunks <= cis_list_size) ? number_of_chunks : cis_list_size;
	for (int j=0; j<match_iter_size; j++) {
	    IndexScore cis = cis_list.get(j);
	    Statement cstmt = cstmt_list.get(cis.getIndex());
	    int cstmt_startpos = cstmt.getStartPosition();
	    String cstmt_loc = "slc:" + ccu.getLineNumber(cstmt_startpos) + "," + ccu.getColumnNumber(cstmt_startpos);
	    List<ASTNode> cnode_list0 = new ArrayList<ASTNode>();
	    cnode_list0.add(cstmt);
	    CandidateChunk cchunk0 = new CandidateChunk(cchunk.getFilePath(), cstmt_loc, cnode_list0);
	    rslt_chunks.add(cchunk0);
	}

	return rslt_chunks;
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
