package sharpfix.rename;

import org.eclipse.jdt.core.dom.ASTNode;

public class CodeToken
{
    ASTNode node;
    String str;
    String ptn;

    public CodeToken(ASTNode node) {
	this.node = node;
	str = node.toString();
	ptn = PatternGenerator.getPattern(this);
    }

    public CodeToken(String str) {
	node = null;
	this.str = str;
	this.ptn = str; //str is supposed to be a primitive symbol like "="
    }

    public ASTNode getNode() {
	return node;
    }
    
    public String getString() {
	return str;
    }

    public String getPatternString() {
	return ptn;
    }
}
