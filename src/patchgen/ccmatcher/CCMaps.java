package sharpfix.patchgen.ccmatcher;

import java.util.Map;
import java.util.HashMap;
import org.eclipse.jdt.core.dom.ASTNode;

public class CCMaps
{
    Map<ASTNode, ASTNode> map1;
    Map<ASTNode, ASTNode> map2;

    public CCMaps() {
	map1 = new HashMap<ASTNode,ASTNode>();
	map2 = new HashMap<ASTNode,ASTNode>();
    }

    public void add(ASTNode s1, ASTNode s2) {
	map1.put(s1, s2);
	map2.put(s2, s1);
    }

    public Map<ASTNode, ASTNode> getMap1() { return map1; }

    public Map<ASTNode, ASTNode> getMap2() { return map2; }
    
    public ASTNode getMatch1(ASTNode s) { return map1.get(s); }

    public ASTNode getMatch2(ASTNode s) { return map2.get(s); }


    public String toString() {
	String s = "*** Code Component Matching Result ***\n";
	for (Map.Entry<ASTNode, ASTNode> entry : map1.entrySet()) {
	    ASTNode s1 = entry.getKey();
	    ASTNode s2 = entry.getValue();
	    s += "-----------\n";
	    s += (s1==null) ? "null" : s1.toString();
	    s += "\n";
	    s += (s2==null) ? "null" : s2.toString();
	    s += "\n";
	}
	return s;
    }
}
