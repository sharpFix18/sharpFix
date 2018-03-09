package sharpfix.patchgen.ccmatcher;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import org.eclipse.jdt.core.dom.*;

/* ssFix's ccmatcher */
public class CCMatcher0
{
    private MatchLeaves leave_matcher;

    public CCMatcher0() { leave_matcher = new MatchLeaves(); }
    
    public CCMaps matchLeaves(List<ASTNode> node_list1, List<ASTNode> node_list2) {

	CCMaps ccmaps = new CCMaps();
	CCCollection cccollection1 = CCCollector.collect(node_list1);
	CCCollection cccollection2 = CCCollector.collect(node_list2);

	//Match leaf nodes
	List<PairVal<ASTNode, ASTNode>> leave_match_list = leave_matcher.bestMatch(cccollection1.getLeafNodeList(), cccollection2.getLeafNodeList());
	for (PairVal<ASTNode, ASTNode> leave_match : leave_match_list) {
	    ASTNode node1 = leave_match.getElem1();
	    ASTNode node2 = leave_match.getElem2();
	    if (node1 != null && node2 != null) {
		//If either is null, that node has no match.
		ccmaps.add(node1, node2);
	    }
	}

	return ccmaps;
    }

    public CCMaps match(List<ASTNode> node_list1, List<ASTNode> node_list2) {
	return match(node_list1, node_list2, false);
    }
    
    public CCMaps match(List<ASTNode> node_list1, List<ASTNode> node_list2, boolean print_scores) {

	CCMaps ccmaps = new CCMaps();
	CCCollection cccollection1 = CCCollector.collect(node_list1);
	CCCollection cccollection2 = CCCollector.collect(node_list2);

	//Match leaf nodes
	List<PairVal<ASTNode, ASTNode>> leave_match_list = leave_matcher.bestMatch(cccollection1.getLeafNodeList(), cccollection2.getLeafNodeList());
	for (PairVal<ASTNode, ASTNode> leave_match : leave_match_list) {
	    ASTNode node1 = leave_match.getElem1();
	    ASTNode node2 = leave_match.getElem2();
	    if (node1 != null && node2 != null) {
		//If either is null, that node has no match.
		ccmaps.add(node1, node2);
	    }
	}

	if (print_scores) {
	    for (PairVal<ASTNode, ASTNode> leave_match : leave_match_list) {
		System.out.println("String Similarity Score: " + leave_match.getValue());
		System.out.println(leave_match.getElem1());
		System.out.println(leave_match.getElem2());
		System.out.println();
	    }
	}
	
	//Match inner nodes
	MatchInnerNodes inner_node_matcher = new MatchInnerNodes(ccmaps);
	ccmaps = inner_node_matcher.firstMatch(cccollection1.getInnerNodeList(), cccollection2.getInnerNodeList(), print_scores);

	return ccmaps;
    }
}
