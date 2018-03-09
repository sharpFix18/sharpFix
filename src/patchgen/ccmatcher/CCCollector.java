package sharpfix.patchgen.ccmatcher;

import java.util.List;
import org.eclipse.jdt.core.dom.*;



public class CCCollector
{
    public static CCCollection collect(List<ASTNode> node_list) {

	CCVisitor ccvisitor = new CCVisitor();
	for (ASTNode node : node_list) {
	    node.accept(ccvisitor);
	}
	return ccvisitor.getCCCollection();
    }

    private static class CCVisitor extends ASTVisitor {

	CCCollection cc_collection;

	public CCVisitor() { cc_collection = new CCCollection(); }

	public CCCollection getCCCollection() { return cc_collection; }

	//This is used for leaf node collection which is in pre-order
	@Override public boolean preVisit2(ASTNode node) {

	    if (node instanceof Statement) {
		if ((node instanceof Block) ||
		    (node instanceof ForStatement) ||
		    (node instanceof IfStatement) ||
		    (node instanceof SwitchStatement) ||
		    (node instanceof SynchronizedStatement) ||
		    (node instanceof TryStatement) ||
		    (node instanceof TypeDeclarationStatement) ||
		    (node instanceof WhileStatement)) {
		    //Do nothing
		}
		else if (node instanceof ExpressionStatement) {
		    //Do nothing, later we will collect the inner expression.
		}
		else {
		    cc_collection.addLeafNode(node);
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
		    cc_collection.addLeafNode(node);
		}
	    }

	    return true;
	}

	//This is used for inner node collection which is in post-order	
	@Override public void postVisit(ASTNode node) {
	    if ((node instanceof ForStatement) ||
		(node instanceof IfStatement) ||
		(node instanceof SwitchStatement) ||
		(node instanceof SynchronizedStatement) ||
		(node instanceof TryStatement) ||
		(node instanceof TypeDeclarationStatement) ||
		(node instanceof WhileStatement)) {

		cc_collection.addInnerNode(node);
	    }
	}
    }
}
