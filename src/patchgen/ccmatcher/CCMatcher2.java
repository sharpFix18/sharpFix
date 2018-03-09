package sharpfix.patchgen.ccmatcher;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import org.eclipse.jdt.core.dom.*;
import sharpfix.util.ConceptWordExtractor;
import sharpfix.util.ConceptTokenExtractor;

/* Component matching based on conceptual tokens, i.e., words (including stop, short, long words) + symbols. Best matching is used for one side (tchunk). In other words, for each component in tchunk, we find its best match in rcchunk. (One component in rcchunk can have multiple matches.) */
public class CCMatcher2
{
    public CCMaps match(List<ASTNode> node_list0, List<ASTNode> node_list1) {
	CCCollection collect0 = CCCollector.collect(node_list0);
	CCCollection collect1 = CCCollector.collect(node_list1);

	List<ASTNode> all_node_list0 = new ArrayList<ASTNode>();
	List<ASTNode> all_node_list1 = new ArrayList<ASTNode>();
	List<ASTNode> inner_node_list0 = collect0.getInnerNodeList();
	List<ASTNode> leaf_node_list0 = collect0.getLeafNodeList();
	List<ASTNode> inner_node_list1 = collect1.getInnerNodeList();
	List<ASTNode> leaf_node_list1 = collect1.getLeafNodeList();
	for (ASTNode inner_node0 : inner_node_list0) { all_node_list0.add(inner_node0); }
	for (ASTNode leaf_node0 : leaf_node_list0) { all_node_list0.add(leaf_node0); }
	for (ASTNode inner_node1 : inner_node_list1) { all_node_list1.add(inner_node1); }
	for (ASTNode leaf_node1 : leaf_node_list1) { all_node_list1.add(leaf_node1); }

	int size0 = all_node_list0.size();
	int size1 = all_node_list1.size();
	CCMaps ccmaps = new CCMaps();	
	List<PairVal<Integer, Integer>> pv_list = new ArrayList<PairVal<Integer, Integer>>();
	for (int i=0; i<size0; i++) {
	    ASTNode node0 = all_node_list0.get(i);
	    TokenCollection tc0 = new TokenCollection(ConceptTokenExtractor.extract(node0));
	    double best_score = -1;
	    int best_j = -1;
	    for (int j=0; j<size1; j++) {
		ASTNode node1 = all_node_list1.get(j);
		if (!CCIsomorphismChecker1.isomorphic(node0, node1)) {
		    continue;
		}
		TokenCollection tc1 = new TokenCollection(ConceptTokenExtractor.extract(node1));
		double score = computeSimilarity(tc0, tc1);
		if (score > 0 && score > best_score) {
		    best_score = score;
		    best_j = j;
		}
	    }
	    if (best_j != -1) {
		ccmaps.add(node0, all_node_list1.get(best_j));
	    }
	}

	return ccmaps;
    }

    private float computeSimilarity(TokenCollection tc0, TokenCollection tc1) {
	List<String> tk_strs0 = tc0.getTokenStrings();
	List<String> tk_strs1 = tc1.getTokenStrings();
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
	//===============
	/*
	System.out.println("Match Count: " + match_count);
	System.out.println("TC0 Size: " + tk_strs0_size);
	System.out.println("TC1 Size: " + tk_strs1_size);
	*/
	//===============	
	
	return (2.0f * match_count) / (tk_strs0_size + tk_strs1_size);
    }
    
    private class TokenCollection {
	private List<String> tk_strs;
	public TokenCollection(List<String> tk_strs) { this.tk_strs = tk_strs; }
	public List<String> getTokenStrings() { return tk_strs; }
	public String toString() {
	    String s = null;
	    for (String tk_str : tk_strs) {
		if (s == null) { s = tk_str; }
		else { s += " " + tk_str; }
	    }
	    return s;
	}
    }
}
