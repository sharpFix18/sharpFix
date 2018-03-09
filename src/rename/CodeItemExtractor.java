package sharpfix.rename;

import java.util.List;
import java.util.ArrayList;
import org.eclipse.jdt.core.dom.*;
import edu.brown.cs.ivy.jcomp.JcompAst;
import edu.brown.cs.ivy.jcomp.JcompType;
import edu.brown.cs.ivy.jcomp.JcompSymbol;

/* Note: The target AST should be RESOLVED already. */

public class CodeItemExtractor
{
    /* All nodes from the node list should come from ONE method. */
    public CodeItem extract(List<ASTNode> node_list) {
	String class_name = null;
	String method_name = null;
	List<String> param_names = null;
	List<String> param_type_names = null;
	CodeItemVisitor civ = new CodeItemVisitor();
	
	int node_list_size = node_list.size();
	for (int i=0; i<node_list_size; i++) {
	    ASTNode ic_node = node_list.get(i);
	    if (i==0) {
		ClassMethodItems cmi = new ClassMethodItems(ic_node);
		class_name = cmi.getClassName();
		method_name = cmi.getMethodName();
		param_names = cmi.getParameterNames();
		param_type_names = cmi.getParameterTypeNames();
	    }
	    ic_node.accept(civ);
	}

	if (class_name == null ||
	    method_name == null ||
	    param_names == null ||
	    param_type_names == null) {
	    return new CodeItem();
	}
	else {
	    CodeItem ci = new CodeItem(class_name, method_name,
				       param_names, param_type_names,
				       civ.getMethodCallNames(),
				       civ.getTypeNames(),
				       civ.getVariableNames());
	    return ci;
	}
    }

    private class ClassMethodItems {
	String class_name;
	String method_name;
	List<String> param_names;
	List<String> param_type_names;	

	public ClassMethodItems(ASTNode node) {
	    class_name = null;
	    method_name = null;
	    param_names = new ArrayList<String>();
	    param_type_names = new ArrayList<String>();

	    AbstractTypeDeclaration atd = null;
	    MethodDeclaration md = null;
	    ASTNode par = node;
	    while (par != null) {
		if (par instanceof MethodDeclaration) {
		    md = (MethodDeclaration) par;
		    break;
		}
		par = par.getParent();
	    }
	    par = node;
	    while (par != null) {
		if (par instanceof AbstractTypeDeclaration) {
		    atd = (AbstractTypeDeclaration) par;
		    break;
		}
		par = par.getParent();
	    }

	    class_name = atd.getName().getIdentifier();
	    method_name = md.getName().getIdentifier();
	    List params = md.parameters();
	    for (Object param_obj : params) {
		SingleVariableDeclaration svd = (SingleVariableDeclaration) param_obj;
		param_names.add(svd.getName().getIdentifier());
		String svd_type_name = getTypeName(svd.getType());
		if (!("").equals(svd_type_name)) {
		    param_type_names.add(svd_type_name);
		}
	    }
	}

	private String getTypeName(Type type) {
	    if (type == null) { return ""; }
	    if (type instanceof ArrayType) {
		ArrayType at = (ArrayType) type;
		return getTypeName(at.getElementType());
	    }
	    else if (type instanceof ParameterizedType) {
		ParameterizedType pt = (ParameterizedType) type;
		return getTypeName(pt.getType());
	    }
	    else if (type instanceof PrimitiveType) {
		//We don't collect primitive types
		return "";
	    }
	    else if (type instanceof QualifiedType) {
		QualifiedType qt = (QualifiedType) type;
		return (getTypeName(qt.getQualifier()) + "." +
			qt.getName().getIdentifier());
	    }
	    else if (type instanceof SimpleType) {
		SimpleType st = (SimpleType) type;
		return st.toString();
	    }
	    else {
		return "";
	    }
	}

	public String getClassName() { return class_name; }

	public String getMethodName() { return method_name; }

	public List<String> getParameterNames() { return param_names; }

	public List<String> getParameterTypeNames() { return param_type_names; }
    }
    
    private class CodeItemVisitor extends ASTVisitor
    {
	List<String> method_call_names;
	List<String> type_names;
	List<String> var_names;

	public CodeItemVisitor() {
	    method_call_names = new ArrayList<String>();
	    type_names = new ArrayList<String>();
	    var_names = new ArrayList<String>();
	}

	public List<String> getMethodCallNames() { return method_call_names; }

	public List<String> getTypeNames() { return type_names; }

	public List<String> getVariableNames() { return var_names; }


	@Override public boolean visit(MethodInvocation mi) {
	    method_call_names.add(mi.getName().getIdentifier());
	    Expression caller = mi.getExpression();
	    if (caller != null) { visitCallerExpression(caller); }
	    List arg_objs = mi.arguments();
	    for (Object arg_obj : arg_objs) { visitExpression((Expression) arg_obj); }
	    return false;
	}

	@Override public boolean visit(SuperMethodInvocation smi) {
	    method_call_names.add(smi.getName().getIdentifier());
	    Name qname = smi.getQualifier();
	    if (qname != null) { visitName(qname); }
	    List arg_objs = smi.arguments();
	    for (Object arg_obj : arg_objs) { visitExpression((Expression) arg_obj); }
	    return false;
	}

	@Override public boolean visit(ArrayType at) {
	    visitType(at.getElementType());
	    return false;
	}

	@Override public boolean visit(ParameterizedType pt) {
	    visitType(pt.getType());
	    return false;
	}
	
	@Override public boolean visit(PrimitiveType pt) {
	    //We don't collect primitive types
	    return false;
	}

	@Override public boolean visit(QualifiedType qt) {
	    type_names.add(qt.getName().getIdentifier());
	    visitType(qt.getQualifier());
	    return false;
	}

	@Override public boolean visit(SimpleType st) {
	    type_names.add(st.getName().toString()); //st.getName() is in type Name.
	    return false;
	}

	@Override public boolean visit(UnionType ut) {
	    //We don't collect such type
	    return false;
	}

	@Override public boolean visit(WildcardType wct) {
	    //We don't collect such type
	    return false;
	}

	/* The SimpleName here shouldn't be method or type (in most cases),
	   since a method/type name should be covered in the above visit methods. */
	@Override public boolean visit(SimpleName sname) {
	    if (isTypeName(sname)) { type_names.add(sname.getIdentifier()); }
	    else { var_names.add(sname.getIdentifier()); }
	    return false;
	}

	@Override public boolean visit(QualifiedName qname) {
	    SimpleName sname0 = qname.getName();
	    Name name0 = qname.getQualifier();
	    if (isTypeName(sname0)) {
		//In this case (e.g., as org.apache.commons.io.FileUtils), we add sname0's identifier (FileUtils) to type names, and ignore the qualifiers (as the package name) 
		type_names.add(sname0.getIdentifier());
	    }
	    else {
		//In this case, we add sname0's identifier to var names, and visit its qualifier
		var_names.add(sname0.getIdentifier());
		visitExpression(name0);
	    }
	    return false;
	}
	
	private void visitCallerExpression(Expression expr) {
	    if (expr == null) { return; }
	    if (expr instanceof SimpleName) {
		SimpleName sname = (SimpleName) expr;
		String sname_str = sname.getIdentifier();
		if (isTypeName(sname)) { type_names.add(sname_str); }
		else { var_names.add(sname_str); }
	    }
	    else if (expr instanceof QualifiedName) {
		QualifiedName qname = (QualifiedName) expr;
		SimpleName sname0 = qname.getName();
		Name name0 = qname.getQualifier();
		if (isTypeName(sname0)) {
		    //In this case (e.g., as org.apache.commons.io.FileUtils), we add sname0's identifier (FileUtils) to type names, and ignore the qualifiers (as the package name) 
		    type_names.add(sname0.getIdentifier());
		}
		else {
		    //In this case, we add sname0's identifier to var names, and visit its qualifier
		    var_names.add(sname0.getIdentifier());
		    visitCallerExpression(name0);
		}
	    }
	    else {
		//In all other cases, we pass expr to visitExpression
		visitExpression(expr);
	    }
	}

	private boolean isTypeName(SimpleName sname) {
	    if (sname == null) { return false; }
	    String sname_str = sname.getIdentifier();
	    char[] sname_chars = sname_str.toCharArray();
	    int sname_chars_length = sname_chars.length;
	    if (sname_chars_length > 0) {
		boolean first_letter_upper_case = false;
		boolean contains_lower_case = false;
		first_letter_upper_case = Character.isLetter(sname_chars[0]) && Character.isUpperCase(sname_chars[0]);
		for (char sname_char : sname_chars) {
		    if (Character.isLowerCase(sname_char)) {
			contains_lower_case = true;
			break;
		    }
		}
		if (first_letter_upper_case && contains_lower_case) { return true; }
		else { return false; }
	    }
	    else {
		return false;
	    }
	}
	
	private boolean visitExpression(Expression expr) {
	    if (expr == null) { return false; }
	    //if (expr instanceof Annotation) { return visit((Annotation) expr); }
	    if (expr instanceof ArrayAccess) { return visit((ArrayAccess) expr); }
	    if (expr instanceof ArrayCreation) { return visit((ArrayCreation) expr); }
	    if (expr instanceof ArrayInitializer) { return visit((ArrayInitializer) expr); }
	    if (expr instanceof Assignment) { return visit((Assignment) expr); }
	    if (expr instanceof BooleanLiteral) { return visit((BooleanLiteral) expr); }
	    if (expr instanceof CastExpression) { return visit((CastExpression) expr); }
	    if (expr instanceof CharacterLiteral) { return visit((CharacterLiteral) expr); }
	    if (expr instanceof ClassInstanceCreation) { return visit((ClassInstanceCreation) expr); }
	    if (expr instanceof ConditionalExpression) { return visit((ConditionalExpression) expr); }
	    if (expr instanceof FieldAccess) { return visit((FieldAccess) expr); }
	    if (expr instanceof InfixExpression) { return visit((InfixExpression) expr); }
	    if (expr instanceof InstanceofExpression) { return visit((InstanceofExpression) expr); }
	    //if (expr instanceof LambdaExpression) { return visit((LambdaExpression) expr); }
	    if (expr instanceof MethodInvocation) { return visit((MethodInvocation) expr); }
	    //if (expr instanceof MethodReference) { return visit((MethodReference) expr); }
	    if (expr instanceof Name) { return visitName((Name) expr); }
	    if (expr instanceof NullLiteral) { return visit((NullLiteral) expr); }
	    if (expr instanceof NumberLiteral) { return visit((NumberLiteral) expr); }
	    if (expr instanceof ParenthesizedExpression) { return visit((ParenthesizedExpression) expr); }
	    if (expr instanceof PostfixExpression) { return visit((PostfixExpression) expr); }
	    if (expr instanceof PrefixExpression) { return visit((PrefixExpression) expr); }
	    if (expr instanceof StringLiteral) { return visit((StringLiteral) expr); }
	    if (expr instanceof SuperFieldAccess) { return visit((SuperFieldAccess) expr); }
	    if (expr instanceof SuperMethodInvocation) { return visit((SuperMethodInvocation) expr); }
	    if (expr instanceof ThisExpression) { return visit((ThisExpression) expr); }
	    if (expr instanceof TypeLiteral) { return visit((TypeLiteral) expr); }
	    if (expr instanceof VariableDeclarationExpression) { return visit((VariableDeclarationExpression) expr); }

	    return false;
	}

	private boolean visitName(Name name) {
	    if (name == null) { return false; }
	    if (name instanceof SimpleName) { return visit((SimpleName) name); }
	    if (name instanceof QualifiedName) { return visit((QualifiedName) name); }
	    return false;
	}

	private boolean visitType(Type type) {
	    if (type == null) { return false; }
	    if (type instanceof ArrayType) { return visit((ArrayType) type); }
	    if (type instanceof ParameterizedType) { return visit((ParameterizedType) type); }
	    if (type instanceof PrimitiveType) { return visit((PrimitiveType) type); }
	    if (type instanceof QualifiedType) { return visit((QualifiedType) type); }
	    if (type instanceof SimpleType) { return visit((SimpleType) type); }
	    if (type instanceof UnionType) { return visit((UnionType) type); }
	    if (type instanceof WildcardType) { return visit((WildcardType) type); }
	    return false;
	}

	@Override public boolean visit(ArrayAccess aa) {
	    visitExpression(aa.getArray());
	    visitExpression(aa.getIndex());
	    return false;
	}

	@Override public boolean visit(ArrayCreation ac) {
	    visitType(ac.getType());
	    visitExpression(ac.getInitializer());
	    return false;
	}

	@Override public boolean visit(ArrayInitializer ai) {
	    List expr_objs = ai.expressions();
	    for (Object expr_obj : expr_objs) {
		visitExpression((Expression) expr_obj);
	    }
	    return false;
	}

	@Override public boolean visit(Assignment assign) {
	    visitExpression(assign.getLeftHandSide());
	    visitExpression(assign.getRightHandSide());
	    return false;
	}

	@Override public boolean visit(BooleanLiteral bl) { return false; }

	@Override public boolean visit(CastExpression ce) {
	    visitType(ce.getType());
	    visitExpression(ce.getExpression());
	    return false;
	}

	@Override public boolean visit(CharacterLiteral al) { return false; }

	@Override public boolean visit(ClassInstanceCreation cic) {
	    visitExpression(cic.getExpression());
	    visitType(cic.getType());
	    List arg_objs = cic.arguments();
	    for (Object arg_obj : arg_objs) {
		visitExpression((Expression) arg_obj);
	    }
	    return false;
	}

	@Override public boolean visit(ConditionalExpression ce) {
	    visitExpression(ce.getExpression());
	    visitExpression(ce.getThenExpression());
	    visitExpression(ce.getElseExpression());
	    return false;
	}

	@Override public boolean visit(FieldAccess fa) {
	    visitName(fa.getName());
	    visitExpression(fa.getExpression());
	    return false;
	}

	@Override public boolean visit(InfixExpression ie) {
	    visitExpression(ie.getLeftOperand());
	    visitExpression(ie.getRightOperand());
	    List opd_objs = ie.extendedOperands();
	    for (Object opd_obj : opd_objs) {
		visitExpression((Expression) opd_obj);
	    }
	    return false;
	}

	@Override public boolean visit(InstanceofExpression ie) {
	    visitExpression(ie.getLeftOperand());
	    visitType(ie.getRightOperand());
	    return false;
	}

	@Override public boolean visit(NullLiteral nl) { return false; }	

	@Override public boolean visit(NumberLiteral nl) { return false; }

	@Override public boolean visit(ParenthesizedExpression pe) {
	    visitExpression(pe.getExpression());
	    return false;
	}

	@Override public boolean visit(PostfixExpression pe) {
	    visitExpression(pe.getOperand());
	    return false;
	}

	@Override public boolean visit(PrefixExpression pe) {
	    visitExpression(pe.getOperand());
	    return false;
	}

	@Override public boolean visit(StringLiteral sl) { return false; }

	@Override public boolean visit(SuperFieldAccess sfa) {
	    visitName(sfa.getName());
	    visitName(sfa.getQualifier());
	    return false;
	}

	@Override public boolean visit(ThisExpression te) {
	    visitName(te.getQualifier());
	    return false;
	}

	@Override public boolean visit(TypeLiteral tl) {
	    visitType(tl.getType());
	    return false;
	}

	@Override public boolean visit(VariableDeclarationExpression vde) {
	    visitType(vde.getType());
	    List vdf_objs = vde.fragments();
	    for (Object vdf_obj : vdf_objs) {
		visit((VariableDeclarationFragment) vdf_obj);
	    }
	    return false;
	}

	@Override public boolean visit(VariableDeclarationFragment vdf) {
	    visitName(vdf.getName());
	    visitExpression(vdf.getInitializer());
	    return false;
	}
    }
}
