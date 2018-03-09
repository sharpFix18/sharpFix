package sharpfix.patchgen.ccmatcher;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Expression;


public class MatchInnerNodes
{
    CCMaps maps;

    public MatchInnerNodes(CCMaps maps) {
	this.maps = maps;
    }

    public CCMaps firstMatch(List<ASTNode> inner_node_list1, List<ASTNode> inner_node_list2) {
	return firstMatch(inner_node_list1, inner_node_list2, false);
    }
    
    public CCMaps firstMatch(List<ASTNode> inner_node_list1, List<ASTNode> inner_node_list2, boolean print_scores) {
	for (ASTNode inner_node1 : inner_node_list1) {
	    //inner_node1 is already matched
	    if (maps.getMatch1(inner_node1) != null) { continue; }
	    for (ASTNode inner_node2 : inner_node_list2) {
		//inner_node2 is already matched
		if (maps.getMatch2(inner_node2) != null) { continue; }
		Triple<Double,Double,Boolean> rslt_triple = match(inner_node1, inner_node2);
		if (print_scores) {
		    System.out.println("Dice Coefficient Score: " + rslt_triple.getElem1());
		    //System.out.println("Value Score: " + rslt_triple.getElem2());
		    System.out.println(inner_node1);
		    System.out.println(inner_node2);
		    System.out.println();
		}

		boolean matched = rslt_triple.getElem3().booleanValue();
		if (matched) { break; } 
	    }
	}
	return maps;
    }

    public Triple<Double,Double,Boolean> match(ASTNode inner_node1, ASTNode inner_node2) {
	if (!CCIsomorphismChecker0.isomorphic(inner_node1, inner_node2)) {
	    return new Triple<Double,Double,Boolean>(-1.0,-1.0,false);
	}

	ChildrenVisitor cv1 = new ChildrenVisitor();
	ChildrenVisitor cv2 = new ChildrenVisitor();
	inner_node1.accept(cv1);
	inner_node2.accept(cv2);
	//dice coefficient
	Set<ASTNode> node_set1 = cv1.getNodeSet();
	Set<ASTNode> node_set2 = cv2.getNodeSet();
	int size1 = node_set1.size();
	int size2 = node_set2.size();
	int common = 0;
	for (ASTNode c1 : node_set1) {
	    ASTNode c1_match = maps.getMatch1(c1);
	    if (c1_match != null && node_set2.contains(c1_match)) {
		common += 1;
	    }
	}
	double dice_coefficient = 0;
	if (size1!=0 && size2!=0) {
	    dice_coefficient = (2.0*common)/(size1+size2);
	}

	//value score
	double value_score = 0;
	Expression e1 = NodeUtils.getCondition(inner_node1);
	Expression e2 = NodeUtils.getCondition(inner_node2);
	if (e1 == null && e2 == null) { value_score = 1; }
	else if (e1 != null && e2 == null) { value_score = 0; }
	else if (e1 == null && e2 != null) { value_score = 0; }
	else {
	    value_score = MatchNodeValues.match(e1, e2, 2);
	}

	//Be careful of the thresholds
	boolean matched = false;

	/*
	if (dice_coefficient >= 0.8) {
	    matched = true;
	    maps.add(inner_node1, inner_node2);
	}
	else if (size1 > 4 && size2 > 4) {
	    //if (dice_coefficient >= 0.6 && value_score >= 0.4) {
	    if (dice_coefficient >= 0.6) {
		matched = true;
		maps.add(inner_node1, inner_node2);
	    }
	}
	else {
	    //if (dice_coefficient >= 0.4 && value_score >= 0.4) {
	    if (dice_coefficient >= 0.4) {
		matched = true;
		maps.add(inner_node1, inner_node2);
	    }
        }
	*/
	
	if (dice_coefficient >= 0.4) {
	    matched = true;
	    maps.add(inner_node1, inner_node2);
	}

	return new Triple<Double,Double,Boolean>(dice_coefficient,value_score,matched);
    }
    
}
