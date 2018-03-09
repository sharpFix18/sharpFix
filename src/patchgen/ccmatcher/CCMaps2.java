package sharpfix.patchgen.ccmatcher;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import org.eclipse.jdt.core.dom.ASTNode;

public class CCMaps2
{
    Map<ASTNode, List<ASTNode>> map1;
    Map<ASTNode, List<ASTNode>> map2;

    public CCMaps2() {
	map1 = new HashMap<ASTNode, List<ASTNode>>();
	map2 = new HashMap<ASTNode, List<ASTNode>>();
    }

    public void add(ASTNode s1, ASTNode s2) {
	List<ASTNode> matched_nodes1 = map1.get(s1);
	if (matched_nodes1 == null) {
	    matched_nodes1 = new ArrayList<ASTNode>();
	    map1.put(s1, matched_nodes1); }
	matched_nodes1.add(s2);
	
	List<ASTNode> matched_nodes2 = map2.get(s2);
	if (matched_nodes2 == null) {
	    matched_nodes2 = new ArrayList<ASTNode>();
	    map2.put(s2, matched_nodes2); }
	matched_nodes2.add(s1);
    }

    public Map<ASTNode, List<ASTNode>> getMap1() { return map1; }

    public Map<ASTNode, List<ASTNode>> getMap2() { return map2; }
    
    public List<ASTNode> getMatch1(ASTNode s) { return map1.get(s); }

    public List<ASTNode> getMatch2(ASTNode s) { return map2.get(s); }


    public String toString() {
	String s = "*** Code Component Matching Result ***\n";
	for (Map.Entry<ASTNode, List<ASTNode>> entry : map1.entrySet()) {
	    ASTNode s1 = entry.getKey();
	    List<ASTNode> s1_match_list = entry.getValue();
	    s += "-----------\n";
	    s += (s1==null) ? "null" : s1.toString();
	    s += "\n";
	    if (s1_match_list != null) {
		int k = 0;
		for (ASTNode s1_match : s1_match_list) {
		    s += "\nMatch " + k + ":\n";
		    s += s1_match.toString();
		    k++;
		}
		s += "\n";
	    }
	    else {
		s += "null\n";
	    }
	}
	return s;
    }
}
