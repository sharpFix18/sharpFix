package sharpfix.patchgen.ccmatcher;

import org.eclipse.jdt.core.dom.*;


public class NodeUtils
{
    public static Expression getCondition(ASTNode stmt) {
	if (stmt == null) { return null; }
	if (stmt instanceof IfStatement) {
	    IfStatement if_stmt = (IfStatement) stmt;
	    return if_stmt.getExpression();
	}
	else if (stmt instanceof DoStatement) {
	    DoStatement do_stmt = (DoStatement) stmt;
	    return do_stmt.getExpression();
	}
	else if (stmt instanceof ForStatement) {
	    ForStatement for_stmt = (ForStatement) stmt;
	    return for_stmt.getExpression();
	}
	else if (stmt instanceof SwitchStatement) {
	    SwitchStatement switch_stmt = (SwitchStatement) stmt;
	    return switch_stmt.getExpression();
	}
	else if (stmt instanceof SynchronizedStatement) {
	    SynchronizedStatement ss = (SynchronizedStatement) stmt;
	    return ss.getExpression();
	}
	else if (stmt instanceof WhileStatement) {
	    WhileStatement while_stmt = (WhileStatement) stmt;
	    return while_stmt.getExpression();
	}
	else {
	    return null;
	}
    } 
}
