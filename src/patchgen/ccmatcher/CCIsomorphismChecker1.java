package sharpfix.patchgen.ccmatcher;

import org.eclipse.jdt.core.dom.*;


public class CCIsomorphismChecker1
{
    private static boolean isStatementExpression(ASTNode node) {
	if (node instanceof Expression) {
	    ASTNode par = node.getParent();
	    if (par instanceof ExpressionStatement) {
		return true;
	    }
	}
	return false;
    }

    private static boolean isLoop(ASTNode node) {
	if (node instanceof DoStatement) { return true; }
	if (node instanceof EnhancedForStatement) { return true; }
	if (node instanceof ForStatement) { return true; }
	if (node instanceof WhileStatement) { return true; }
	return false;
    }
    
    public static boolean isomorphic(ASTNode node1, ASTNode node2) {
	if (node1==null || node2==null) { return false; }
	if ((node1 instanceof Statement) && (node2 instanceof Statement)) {
	    if (isLoop(node1) && isLoop(node2)) { return true; }
	    else { return (node1.getNodeType()==node2.getNodeType()); }
	}
	else if (node1 instanceof Statement) {
	    if (isStatementExpression(node2)) {
		if (node1 instanceof ExpressionStatement) { return true; }
		else if ((node1 instanceof VariableDeclarationStatement) &&
			 ((node2 instanceof Assignment) ||
			  (node2 instanceof VariableDeclarationExpression))) {
		    return true;
		}
	    }
	    return false;
	}
	else if (node2 instanceof Statement) {
	    if (isStatementExpression(node1)) {
		if (node2 instanceof ExpressionStatement) { return true; }
		else if ((node2 instanceof VariableDeclarationStatement) &&
			 ((node1 instanceof Assignment) ||
			  (node1 instanceof VariableDeclarationExpression))) {
		    return true;
		}
	    }
	    return false;
	}
	else {
	    if (isStatementExpression(node1) && isStatementExpression(node2)) {
		return true;
	    }
	    else {
		return (node1.getNodeType()==node2.getNodeType());
	    }
	}
    }
}
