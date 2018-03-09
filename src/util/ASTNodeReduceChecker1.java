package sharpfix.util;

import java.util.List;
import java.util.ArrayList;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Expression;

public class ASTNodeReduceChecker1
{
    public static boolean isReduced(ASTNode node) {
	ReduceVisitor rv = new ReduceVisitor();
	node.accept(rv);
	return rv.isReduced();
    }

    private static class ReduceVisitor extends ASTVisitor
    {
	boolean is_reduced;

	public ReduceVisitor() { is_reduced = false; }

	public boolean isReduced() { return is_reduced; }
	
	@Override public boolean visit(InfixExpression ie) {
	    String op_str = ie.getOperator().toString();
	    Expression le = ie.getLeftOperand();
	    Expression re = ie.getRightOperand();
	    if (op_str.equals("-") || op_str.equals("/") ||
		op_str.equals("%") || op_str.equals("==") ||
		op_str.equals("<") || op_str.equals(">") ||
		op_str.equals("<=") || op_str.equals(">=") ||
		op_str.equals("!=")) {
		if (le.toString().equals(re.toString())) { is_reduced = true; }
	    }
	    else if (op_str.equals("&&")) {
		if (le.toString().equals("false")) { is_reduced = true; }
		if (re.toString().equals("false")) { is_reduced = true; }
	    }
	    else if (op_str.equals("||")) {
		if (le.toString().equals("true")) { is_reduced = true; }
		if (re.toString().equals("true")) { is_reduced = true; }
	    }
	    return true;
	}
    }
}
