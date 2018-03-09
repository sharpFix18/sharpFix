package sharpfix.rename;

import java.util.List;
import java.util.ArrayList;
import org.eclipse.jdt.core.dom.*;
import edu.brown.cs.ivy.jcomp.*;

public class ComponentGetter
{
    public static List<ASTNode> getComponents(ASTNode node) {
	ComponentVisitor cpt_visitor = new ComponentVisitor();
	node.accept(cpt_visitor);
	return cpt_visitor.getComponents();
    }

    private static class ComponentVisitor extends ASTVisitor {
	List<ASTNode> cpt_list;

	public ComponentVisitor() { cpt_list = new ArrayList<ASTNode>(); }

	public List<ASTNode> getComponents() { return cpt_list; }

	public void preVisit(ASTNode node) {
	    if (node instanceof Statement) {
		if ((node instanceof Block) ||
		    (node instanceof LabeledStatement) ||
		    (node instanceof ExpressionStatement)){
		    //Do nothing.
		}
		else if ((node instanceof DoStatement) ||
		    (node instanceof EnhancedForStatement) ||
		    (node instanceof ForStatement) ||
		    (node instanceof IfStatement) ||
		    (node instanceof SwitchStatement) ||
		    (node instanceof SynchronizedStatement) ||
		    (node instanceof TryStatement) ||
		    (node instanceof TypeDeclarationStatement) ||
		    (node instanceof WhileStatement)) {
		    cpt_list.add(node);
		}
		else {
		    cpt_list.add(node);
		}
	    }
	    else if (node instanceof Expression) {
		if ((node instanceof Annotation) ||
		    (node instanceof ArrayInitializer)) {
		    //Do nothing.
		}
		else if (node instanceof ParenthesizedExpression) {
		    ParenthesizedExpression pe = (ParenthesizedExpression) node;
		    cpt_list.add(pe.getExpression());
		}
		else if (node instanceof SimpleName) {
		    //Do nothing.
		}
		else if (node instanceof QualifiedName) {
		    cpt_list.add(node);
		}
		else {
		    cpt_list.add(node);
		}
	    }
	}
    }
}
