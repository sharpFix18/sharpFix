package sharpfix.rename;

import org.eclipse.jdt.core.dom.ASTNode;
import edu.brown.cs.ivy.jcomp.*;

public class PatternGenerator
{
    public static String getPattern(CodeToken ct) {
	ASTNode node = ct.getNode();
	if (node == null) {
	    return ct.getString();
	}
	JcompType jctype = JcompAst.getJavaType(node);
	if (jctype != null) {
	    if (isJDK(jctype)) { return node.toString(); }
	    else { return "$t$"; }
	}
	else {
	    JcompSymbol jcsymbol = JcompAst.getReference(node);
	    if (jcsymbol != null) {
		if (isJDK(jcsymbol)) { return node.toString(); }
		else {
		    if (jcsymbol.isMethodSymbol()) { return "$m$"; }
		    else { return "$v$"; }
		}
	    }
	    else {
		return node.toString();
	    }
	}
    }

    public static boolean isJDK(JcompType jctype) {

	if (jctype == null) { return false; }
	String full_name = jctype.getName();
	if (full_name == null) { return false; }
	else { return full_name.startsWith("java."); }
    }

    public static boolean isJDK(JcompSymbol jcsymbol) {

	if (jcsymbol == null) { return false; }
	String full_name = jcsymbol.getFullName();
	if (full_name == null) { return false; }
	else { return full_name.startsWith("java."); }
    }
}
