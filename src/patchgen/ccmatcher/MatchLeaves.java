package sharpfix.patchgen.ccmatcher;

import java.util.List;
import org.eclipse.jdt.core.dom.ASTNode;



public class MatchLeaves
{
    //public final static double f = 0.6; //according to Fluri's paper.
    public final static double f = 0.2;

    public List<PairVal<ASTNode, ASTNode>> bestMatch(List<ASTNode> leaf_list1, List<ASTNode> leaf_list2) {

	CompareFunction<ASTNode> cf = new LeafComparator();
	MatchHelper<ASTNode> leaf_matcher = new BestMatchHelper<ASTNode>();
	List<PairVal<ASTNode, ASTNode>> leaf_pv_list =
	    leaf_matcher.getMatchListWithNons(leaf_list1, leaf_list2, cf, f);
	return leaf_pv_list;
    }
    
    public double match(ASTNode node1, ASTNode node2) {

	//Check null
	if (node1==null || node2==null) { return -1; }

	//Check isomorphism
	if (!CCIsomorphismChecker0.isomorphic(node1, node2)) { return -1; }

	//Bigram similarity
	return MatchNodeValues.match(node1, node2, 2);
    }

    private class LeafComparator implements CompareFunction<ASTNode>
    {
	@Override public double compare(ASTNode node1, ASTNode node2) {
	    return match(node1, node2);
	}
    }

}
