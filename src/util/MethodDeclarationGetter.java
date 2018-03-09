package sharpfix.util;

import java.util.List;
import java.util.ArrayList;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.ASTVisitor;

public class MethodDeclarationGetter
{
    public static List<MethodDeclaration> getMethodDeclarations(CompilationUnit cu) {
	MethodDeclarationCollector mdc = new MethodDeclarationCollector();
	cu.accept(mdc);
	return mdc.getMethodDeclarationList();
    }

    private static class MethodDeclarationCollector extends ASTVisitor
    {
	List<MethodDeclaration> md_list;

	public MethodDeclarationCollector() {
	    md_list = new ArrayList<MethodDeclaration>();
	}

	public List<MethodDeclaration> getMethodDeclarationList() {
	    return md_list;
	}

	@Override public boolean visit(MethodDeclaration md) {
	    md_list.add(md);
	    return false;
	}
    }
    
}
