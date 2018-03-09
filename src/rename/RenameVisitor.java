package sharpfix.rename;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import edu.brown.cs.ivy.jcomp.JcompAst;
import edu.brown.cs.ivy.jcomp.JcompType;
import edu.brown.cs.ivy.jcomp.JcompSymbol;


public class RenameVisitor extends ASTVisitor
{
    ASTRewrite rw;
    NameMap name_map;

    RenameVisitor(ASTRewrite rw, NameMap name_map) {
	this.rw = rw;
	this.name_map = name_map;
    }

    public ASTRewrite getASTRewrite() { return rw; }

    @Override public boolean visit(SimpleName sname) {

	JcompType jctype = JcompAst.getJavaType(sname);
	if (jctype != null) { //Is this a type?
	    String new_name = name_map.getTypeName(jctype);
	    if (new_name == null) {
		new_name = name_map.getTypeName(sname.getIdentifier());
	    }
	    if (new_name == null) { return false; }
	    rw.set(sname, SimpleName.IDENTIFIER_PROPERTY, new_name, null);
	    return false;
	}
	else { //Is this a variable/method identifier?
	    JcompSymbol jcsymbol = JcompAst.getReference(sname);
	    String new_name = name_map.getSymbolName(jcsymbol);
	    if (new_name == null) {
		new_name = name_map.getSymbolName(sname.getIdentifier());
	    }
	    if (new_name == null) { return false; }
	    rw.set(sname, SimpleName.IDENTIFIER_PROPERTY, new_name, null);
	    return false;
	}
    }
}
				      
