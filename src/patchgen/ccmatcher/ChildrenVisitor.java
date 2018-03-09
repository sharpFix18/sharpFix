package sharpfix.patchgen.ccmatcher;

import org.eclipse.jdt.core.dom.*;
import java.util.Set;
import java.util.HashSet;

public class ChildrenVisitor extends ASTVisitor
{
    Set<ASTNode> node_set;

    public ChildrenVisitor() {
	node_set = new HashSet<ASTNode>();
    }

    public Set<ASTNode> getNodeSet() {
	return node_set;
    }
    
    @Override public boolean preVisit2(ASTNode node) {
	if (node instanceof Statement) {
	    if (node instanceof Block) {
		//Do nothing, we don't collect any blocks.
	    }
	    else if (node instanceof ExpressionStatement) {
		//Do nothing, later we will collect the inner expression.
	    }
	    else {
		node_set.add(node);
	    }
	}
	else if (node instanceof Expression) {
	    if ((node instanceof BooleanLiteral) ||
		(node instanceof CharacterLiteral) ||
		(node instanceof Name) ||
		(node instanceof NullLiteral) ||
		(node instanceof NumberLiteral) ||
		(node instanceof StringLiteral) ||
		(node instanceof TypeLiteral)) {
		//Do nothing
		return false;
	    }
	    else if (node instanceof ParenthesizedExpression) {
		//Do nothing, later we will collect the inner expression.
	    }
	    else if (node instanceof ParameterizedType) {
		//Do nothing, later we will collect the inner type.
	    }
	    else {
		node_set.add(node);
	    }
	}
	return true;
    }
}
