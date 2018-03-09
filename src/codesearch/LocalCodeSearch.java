package sharpfix.codesearch;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;
import java.util.Collections;
import java.util.Arrays;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import sharpfix.util.*;

public class LocalCodeSearch
{
    private Map<String, TokenCollection> loadIndex(File index_f) {
	Map<String, TokenCollection> c3_map = new HashMap<String, TokenCollection>();
	String index_fctnt = null;
	try { index_fctnt = FileUtils.readFileToString(index_f); }
	catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
	if (index_fctnt == null) { return c3_map; }
	String[] stmt_index_strs = index_fctnt.split(Indexer.CCSEP);
	int stmt_index_strs_size = stmt_index_strs.length;

	for (int i=0; i<stmt_index_strs_size; i++) {
	    String stmt_index_str = stmt_index_strs[i].trim();
	    if ("".equals(stmt_index_str)) { continue; }
	    String[] stmt_index_items = stmt_index_str.split("\n");
	    if (stmt_index_items.length <= 1) { continue; } //Can be an empty statement
	    String fpath_loc = stmt_index_items[0];
	    String all_tk_str0 = stmt_index_items[1].trim();
	    if ("".equals(all_tk_str0)) { continue; }
	    List<String> tk_strs0 = Arrays.asList(all_tk_str0.split(" "));
	    TokenCollection tkc = new TokenCollection(tk_strs0);
	    c3_map.put(fpath_loc, tkc);
	}
	return c3_map;
    }

    public List<SearchItem> search(File qf, String qloc, File idx_f, int index_flag) {
	Map<String, TokenCollection> c3_map = loadIndex(idx_f);
	String qfpath = qf.getAbsolutePath();
	ASTNode root = ASTNodeLoader.getASTNode(qf);
	List<ASTNode> found_nodes = ASTNodeFinder.find((CompilationUnit) root, qloc);
	ASTNode target_node = found_nodes.get(0);
	TokenCollection c3 = getQueryTokenCollection(target_node, index_flag);
	return search(qfpath+","+qloc, c3, c3_map);
    }

    //This method is actually used by LocalRepair0
    public List<SearchItem> search(ASTNode tnode, String qfpath, String qloc, File idx_f, int index_flag) {
	Map<String, TokenCollection> c3_map = loadIndex(idx_f);
	TokenCollection c3 = getQueryTokenCollection(tnode, index_flag);
	return search(qfpath+","+qloc, c3, c3_map);
    }
        
    public List<SearchItem> search(String qfpathloc, TokenCollection qc3, Map<String, TokenCollection> c3_map) {
	List<SearchItem> si_list = new ArrayList<SearchItem>();
	for (Map.Entry<String, TokenCollection> entry : c3_map.entrySet()) {
	    String fpathloc = entry.getKey();
	    TokenCollection c3 = entry.getValue();
	    float score = computeSimilarity(qc3, c3);
	    si_list.add(new SearchItem(fpathloc, score));
	}

	//Can use a priority queue instead of sorting things
	Collections.sort(si_list, new Comparator<SearchItem>() {
		@Override public int compare(SearchItem si1, SearchItem si2) {
		    Float f1 = (Float) si1.getScore();
		    Float f2 = (Float) si2.getScore();
		    return f2.compareTo(f1);
		}
	    });
	
	return si_list;
    }

    private TokenCollection getQueryTokenCollection(ASTNode target_node, int index_flag) {
	List<String> tk_strs = new ArrayList<String>();

	if (index_flag == 0) {
	    List<CodeComponent> cc_list = CodeComponentExtractor.getCodeComponents(target_node);
	    for (CodeComponent cc : cc_list) {
		tk_strs.add(cc.getStringWithoutSpace());
	    }
	}
	else if (index_flag == 1) {
	    tk_strs = ConceptWordExtractor.extract(target_node.toString(), false, false); //do not filter stop, short, long, and keywords words
	}
	else if (index_flag == 2) {	
	    tk_strs = ConceptTokenExtractor.extract(target_node); //do not filter stop, short, long, and keywords words
	}
	
	return new TokenCollection(tk_strs);
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
	return (2.0f * match_count) / (tk_strs0_size + tk_strs1_size);
    }

    
}
