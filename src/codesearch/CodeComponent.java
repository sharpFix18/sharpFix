package sharpfix.codesearch;

import org.eclipse.jdt.core.dom.ASTNode;

public class CodeComponent
{
    ASTNode node;

    public CodeComponent(ASTNode n) {
	node = n;
    }

    public ASTNode getASTNode() { return node; }

    public String getStringWithoutSpace() {
	if (node == null) { return ""; }
	else { return node.toString().replaceAll("\\s+", ""); }
    }
}
