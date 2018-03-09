package sharpfix.codesearch;

import org.eclipse.jdt.core.dom.*;
import java.util.List;
import java.util.ArrayList;

public class CodeComponentExtractor
{
    public static List<CodeComponent> getCodeComponents(CodeComponent cc) {
	return getCodeComponents(cc.getASTNode());
    }

    public static List<CodeComponent> getCodeComponents(ASTNode node) {
	if (node == null) { return new ArrayList<CodeComponent>(); }
	if (node instanceof ArrayAccess) {
	    return getCodeComponents((ArrayAccess) node);
	}
	else if (node instanceof ArrayCreation) {
	    return getCodeComponents((ArrayCreation) node);
	}
	else if (node instanceof ArrayInitializer) {
	    return getCodeComponents((ArrayInitializer) node);
	}
	else if (node instanceof ArrayType) {
	    return getCodeComponents((ArrayType) node);
	}
	else if (node instanceof Assignment) {
	    return getCodeComponents((Assignment) node);
	}
	else if (node instanceof Block) {
	    return getCodeComponents((Block) node);
	}
	else if (node instanceof EnumDeclaration) {
	    return getCodeComponents((EnumDeclaration) node);
	}
	else if (node instanceof MethodDeclaration) {
	    return getCodeComponents((MethodDeclaration) node);
	}
	else if (node instanceof FieldDeclaration) {
	    return getCodeComponents((FieldDeclaration) node);
	}
	else if (node instanceof Initializer) {
	    return getCodeComponents((Initializer) node);
	}
	else if (node instanceof EnumConstantDeclaration) {
	    return getCodeComponents((EnumConstantDeclaration) node);
	}
	else if (node instanceof BooleanLiteral) {
	    return getCodeComponents((BooleanLiteral) node);
	}
	else if (node instanceof BreakStatement) {
	    return getCodeComponents((BreakStatement) node);
	}
	else if (node instanceof CastExpression) {
	    return getCodeComponents((CastExpression) node);
	}
	else if (node instanceof CatchClause) {
	    return getCodeComponents((CatchClause) node);
	}
	else if (node instanceof CharacterLiteral) {
	    return getCodeComponents((CharacterLiteral) node);
	}
	else if (node instanceof ClassInstanceCreation) {
	    return getCodeComponents((ClassInstanceCreation) node);
	}
	else if (node instanceof CompilationUnit) {
	    return getCodeComponents((CompilationUnit) node);
	}
	else if (node instanceof ConditionalExpression) {
	    return getCodeComponents((ConditionalExpression) node);
	}
	else if (node instanceof ConstructorInvocation) {
	    return getCodeComponents((ConstructorInvocation) node);
	}
	else if (node instanceof ContinueStatement) {
	    return getCodeComponents((ContinueStatement) node);
	}
	else if (node instanceof DoStatement) {
	    return getCodeComponents((DoStatement) node);
	}
	else if (node instanceof EmptyStatement) {
	    return getCodeComponents((EmptyStatement) node);
	}
	else if (node instanceof EnhancedForStatement) {
	    return getCodeComponents((EnhancedForStatement) node);
	}
	else if (node instanceof ExpressionStatement) {
	    return getCodeComponents((ExpressionStatement) node);
	}
	else if (node instanceof FieldAccess) {
	    return getCodeComponents((FieldAccess) node);
	}
	else if (node instanceof ForStatement) {
	    return getCodeComponents((ForStatement) node);
	}
	else if (node instanceof IfStatement) {
	    return getCodeComponents((IfStatement) node);
	}
	else if (node instanceof ImportDeclaration) {
	    return getCodeComponents((ImportDeclaration) node);
	}
	else if (node instanceof InfixExpression) {
	    return getCodeComponents((InfixExpression) node);
	}
	else if (node instanceof InstanceofExpression) {
	    return getCodeComponents((InstanceofExpression) node);
	}
	else if (node instanceof LabeledStatement) {
	    return getCodeComponents((LabeledStatement) node);
	}
	else if (node instanceof MethodInvocation) {
	    return getCodeComponents((MethodInvocation) node);
	}
	else if (node instanceof NullLiteral) {
	    return getCodeComponents((NullLiteral) node);
	}
	else if (node instanceof NumberLiteral) {
	    return getCodeComponents((NumberLiteral) node);
	}
	else if (node instanceof PackageDeclaration) {
	    return getCodeComponents((PackageDeclaration) node);
	}
	else if (node instanceof ParameterizedType) {
	    return getCodeComponents((ParameterizedType) node);
	}
	else if (node instanceof ParenthesizedExpression) {
	    return getCodeComponents((ParenthesizedExpression) node);
	}
	else if (node instanceof PostfixExpression) {
	    return getCodeComponents((PostfixExpression) node);
	}
	else if (node instanceof PrefixExpression) {
	    return getCodeComponents((PrefixExpression) node);
	}
	else if (node instanceof PrimitiveType) {
	    return getCodeComponents((PrimitiveType) node);
	}
	else if (node instanceof QualifiedName) {
	    return getCodeComponents((QualifiedName) node);
	}
	else if (node instanceof QualifiedType) {
	    return getCodeComponents((QualifiedType) node);
	}
	else if (node instanceof ReturnStatement) {
	    return getCodeComponents((ReturnStatement) node);
	}
	else if (node instanceof SimpleName) {
	    return getCodeComponents((SimpleName) node);
	}
	else if (node instanceof SimpleType) {
	    return getCodeComponents((SimpleType) node);
	}
	else if (node instanceof SingleVariableDeclaration) {
	    return getCodeComponents((SingleVariableDeclaration) node);
	}
	else if (node instanceof StringLiteral) {
	    return getCodeComponents((StringLiteral) node);
	}
	else if (node instanceof SuperConstructorInvocation) {
	    return getCodeComponents((SuperConstructorInvocation) node);
	}
	else if (node instanceof SuperFieldAccess) {
	    return getCodeComponents((SuperFieldAccess) node);
	}
	else if (node instanceof SuperMethodInvocation) {
	    return getCodeComponents((SuperMethodInvocation) node);
	}
	else if (node instanceof SwitchCase) {
	    return getCodeComponents((SwitchCase) node);
	}
	else if (node instanceof SwitchStatement) {
	    return getCodeComponents((SwitchStatement) node);
	}
	else if (node instanceof SynchronizedStatement) {
	    return getCodeComponents((SynchronizedStatement) node);
	}
	else if (node instanceof ThisExpression) {
	    return getCodeComponents((ThisExpression) node);
	}
	else if (node instanceof ThrowStatement) {
	    return getCodeComponents((ThrowStatement) node);
	}
	else if (node instanceof TryStatement) {
	    return getCodeComponents((TryStatement) node);
	}
	else if (node instanceof TypeDeclaration) {
	    return getCodeComponents((TypeDeclaration) node);
	}
	else if (node instanceof TypeDeclarationStatement) {
	    return getCodeComponents((TypeDeclarationStatement) node);
	}
	else if (node instanceof TypeLiteral) {
	    return getCodeComponents((TypeLiteral) node);
	}
	else if (node instanceof TypeParameter) {
	    return getCodeComponents((TypeParameter) node);
	}
	else if (node instanceof VariableDeclarationExpression) {
	    return getCodeComponents((VariableDeclarationExpression) node);
	}
	else if (node instanceof VariableDeclarationFragment) {
	    return getCodeComponents((VariableDeclarationFragment) node);
	}
	else if (node instanceof VariableDeclarationStatement) {
	    return getCodeComponents((VariableDeclarationStatement) node);
	}
	else if (node instanceof WhileStatement) {
	    return getCodeComponents((WhileStatement) node);
	}
	else {
	    //Currently, I don't consider any of other types...
	    return new ArrayList<CodeComponent>();
	}
    }

    public static List<CodeComponent> getCodeComponents(ArrayAccess aa) {
	if (aa == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	cc_list0.add(new CodeComponent(aa));
	addTo(cc_list0, getCodeComponents(aa.getArray()));
	addTo(cc_list0, getCodeComponents(aa.getIndex()));
	return cc_list0;
    }

    public static List<CodeComponent> getCodeComponents(ArrayCreation ac) {
	if (ac == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	cc_list0.add(new CodeComponent(ac));
	addTo(cc_list0, getCodeComponents(ac.getType()));
	addTo(cc_list0, getCodeComponents(ac.getInitializer()));
	List dim_objs = ac.dimensions();
	for (Object dim_obj : dim_objs) {
	    addTo(cc_list0, getCodeComponents((ASTNode) dim_obj));
	}
	return cc_list0;
    }

    public static List<CodeComponent> getCodeComponents(ArrayInitializer ai) {
	if (ai == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	cc_list0.add(new CodeComponent(ai));
	List expr_objs = ai.expressions();
	for (Object expr_obj : expr_objs) {
	    addTo(cc_list0, getCodeComponents((ASTNode) expr_obj));
	}
	return cc_list0;
    }
    
    public static List<CodeComponent> getCodeComponents(ArrayType at) {
	if (at == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	cc_list0.add(new CodeComponent(at));
	addTo(cc_list0, getCodeComponents(at.getElementType()));
	return cc_list0;
    }
    
    public static List<CodeComponent> getCodeComponents(Assignment assign) {
	if (assign == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	cc_list0.add(new CodeComponent(assign));
	addTo(cc_list0, getCodeComponents(assign.getLeftHandSide()));
	addTo(cc_list0, getCodeComponents(assign.getRightHandSide()));
	return cc_list0;
    }
    
    public static List<CodeComponent> getCodeComponents(Block block) {
	if (block == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	//Here, I don't add the block itself.
	List stmt_objs = block.statements();
	for (Object stmt_obj : stmt_objs) {
	    addTo(cc_list0, getCodeComponents((Statement) stmt_obj));
	}
	return cc_list0;
    }

    public static List<CodeComponent> getCodeComponents(EnumDeclaration ed) {
	if (ed == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	//Here, I don't add the enum declaration itself.
	List ec_objs = ed.enumConstants();
	List sitype_objs = ed.superInterfaceTypes();
	for (Object ec_obj : ec_objs) {
	    addTo(cc_list0, getCodeComponents((EnumConstantDeclaration) ec_obj));
	}
	for (Object sitype_obj : sitype_objs) {
	    addTo(cc_list0, getCodeComponents((ASTNode) sitype_obj));
	}
	return cc_list0;
    }

    public static List<CodeComponent> getCodeComponents(MethodDeclaration md) {
	if (md == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	//Here, I don't add the method declaration itself.
	addTo(cc_list0, getCodeComponents(md.getName()));
	addTo(cc_list0, getCodeComponents(md.getReturnType2()));
	List param_objs = md.parameters();
	for (Object param_obj : param_objs) {
	    addTo(cc_list0, getCodeComponents((ASTNode) param_obj));
	}	
	List ex_objs = md.thrownExceptions();
	for (Object ex_obj : ex_objs) {
	    addTo(cc_list0, getCodeComponents((ASTNode) ex_obj));
	}
	addTo(cc_list0, getCodeComponents(md.getBody()));
	return cc_list0;
    }

    public static List<CodeComponent> getCodeComponents(FieldDeclaration fd) {
	if (fd == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	cc_list0.add(new CodeComponent(fd));
	addTo(cc_list0, getCodeComponents(fd.getType()));
	List fgt_objs = fd.fragments();
	for (Object fgt_obj : fgt_objs) {
	    addTo(cc_list0, getCodeComponents((ASTNode) fgt_obj));
	}
	return cc_list0;
    }

    public static List<CodeComponent> getCodeComponents(Initializer init) {
	if (init == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	//Here I don't add the initializer itself.
	addTo(cc_list0, getCodeComponents(init.getBody()));
	return cc_list0;
    }
    
    public static List<CodeComponent> getCodeComponents(EnumConstantDeclaration ecd) {
	if (ecd == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	cc_list0.add(new CodeComponent(ecd));
	addTo(cc_list0, getCodeComponents(ecd.getName()));
	List arg_objs = ecd.arguments();
	for (Object arg_obj : arg_objs) {
	    addTo(cc_list0, getCodeComponents((ASTNode) arg_obj));
	}
	return cc_list0;
    }

    public static List<CodeComponent> getCodeComponents(BooleanLiteral bl) {
	if (bl == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	cc_list0.add(new CodeComponent(bl));
	return cc_list0;
    }

    public static List<CodeComponent> getCodeComponents(BreakStatement bs) {
	if (bs == null) { return new ArrayList<CodeComponent>(); }	
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	cc_list0.add(new CodeComponent(bs));
	addTo(cc_list0, getCodeComponents(bs.getLabel()));
	return cc_list0;
    }

    public static List<CodeComponent> getCodeComponents(CastExpression ce) {
	if (ce == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	cc_list0.add(new CodeComponent(ce));
	addTo(cc_list0, getCodeComponents(ce.getType()));
	addTo(cc_list0, getCodeComponents(ce.getExpression()));
	return cc_list0;
    }

    public static List<CodeComponent> getCodeComponents(CatchClause cc) {
	if (cc == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	cc_list0.add(new CodeComponent(cc));
	addTo(cc_list0, getCodeComponents(cc.getException()));
	addTo(cc_list0, getCodeComponents(cc.getBody()));
	return cc_list0;
    }

    public static List<CodeComponent> getCodeComponents(CharacterLiteral cl) {
	if (cl == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	cc_list0.add(new CodeComponent(cl));
	return cc_list0;
    }

    public static List<CodeComponent> getCodeComponents(ClassInstanceCreation cic) {
	if (cic == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	cc_list0.add(new CodeComponent(cic));
	addTo(cc_list0, getCodeComponents(cic.getExpression()));
	addTo(cc_list0, getCodeComponents(cic.getType()));
	List arg_objs = cic.arguments();
	for (Object arg_obj : arg_objs) {
	    addTo(cc_list0, getCodeComponents((ASTNode) arg_obj));
	}
	List targ_objs = cic.typeArguments();
	for (Object targ_obj : targ_objs) {
	    addTo(cc_list0, getCodeComponents((ASTNode) targ_obj));
	}
	return cc_list0;
    }

    /* Currently, I don't handle this. */
    public static List<CodeComponent> getCodeComponents(CompilationUnit cu) {
	if (cu == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	return cc_list0;
    }
    
    public static List<CodeComponent> getCodeComponents(ConditionalExpression ce) {
	if (ce == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	cc_list0.add(new CodeComponent(ce));
	addTo(cc_list0, getCodeComponents(ce.getExpression()));
	addTo(cc_list0, getCodeComponents(ce.getThenExpression()));
	addTo(cc_list0, getCodeComponents(ce.getElseExpression()));
	return cc_list0;
    }
    
    public static List<CodeComponent> getCodeComponents(ConstructorInvocation ci) {
	if (ci == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	cc_list0.add(new CodeComponent(ci));
	List arg_objs = ci.arguments();
	for (Object arg_obj : arg_objs) {
	    addTo(cc_list0, getCodeComponents((ASTNode) arg_obj));
	}
	List targ_objs = ci.typeArguments();
	for (Object targ_obj : targ_objs) {
	    addTo(cc_list0, getCodeComponents((ASTNode) targ_obj));
	}
	return cc_list0;
    }
    
    public static List<CodeComponent> getCodeComponents(ContinueStatement cs) {
	if (cs == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	cc_list0.add(new CodeComponent(cs));
	addTo(cc_list0, getCodeComponents(cs.getLabel()));
	return cc_list0;
    }
    
    public static List<CodeComponent> getCodeComponents(DoStatement ds) {
	if (ds == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	cc_list0.add(new CodeComponent(ds));
	addTo(cc_list0, getCodeComponents(ds.getExpression()));
	addTo(cc_list0, getCodeComponents(ds.getBody()));
	return cc_list0;
    }
    
    public static List<CodeComponent> getCodeComponents(EmptyStatement es) {
	if (es == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	return cc_list0;
    }
    
    public static List<CodeComponent> getCodeComponents(EnhancedForStatement efs) {
	if (efs == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	cc_list0.add(new CodeComponent(efs));
	addTo(cc_list0, getCodeComponents(efs.getExpression()));
	addTo(cc_list0, getCodeComponents(efs.getParameter()));
	addTo(cc_list0, getCodeComponents(efs.getBody()));
	return cc_list0;
    }
    
    public static List<CodeComponent> getCodeComponents(ExpressionStatement es) {
	if (es == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	addTo(cc_list0, getCodeComponents(es.getExpression()));
	return cc_list0;
    }
    
    public static List<CodeComponent> getCodeComponents(FieldAccess fa) {
	if (fa == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	cc_list0.add(new CodeComponent(fa));
	addTo(cc_list0, getCodeComponents(fa.getExpression()));
	addTo(cc_list0, getCodeComponents(fa.getName()));
	return cc_list0;
    }
    
    public static List<CodeComponent> getCodeComponents(ForStatement fs) {
	if (fs == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	cc_list0.add(new CodeComponent(fs));
	List init_objs = fs.initializers();
	List upt_objs = fs.updaters();
	for (Object init_obj : init_objs) {
	    addTo(cc_list0, getCodeComponents((ASTNode) init_obj));
	}
	addTo(cc_list0, getCodeComponents(fs.getExpression()));
	for (Object upt_obj : upt_objs) {
	    addTo(cc_list0, getCodeComponents((ASTNode) upt_obj));
	}
	addTo(cc_list0, getCodeComponents(fs.getBody()));
	return cc_list0;
    }
    
    public static List<CodeComponent> getCodeComponents(IfStatement is) {
	if (is == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	cc_list0.add(new CodeComponent(is));
	addTo(cc_list0, getCodeComponents(is.getExpression()));
	addTo(cc_list0, getCodeComponents(is.getThenStatement()));
	addTo(cc_list0, getCodeComponents(is.getElseStatement()));
	return cc_list0;
    }

    public static List<CodeComponent> getCodeComponents(ImportDeclaration id) {
	if (id == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	cc_list0.add(new CodeComponent(id));
	addTo(cc_list0, getCodeComponents(id.getName()));
	return cc_list0;
    }

    public static List<CodeComponent> getCodeComponents(InfixExpression ie) {
	if (ie == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	cc_list0.add(new CodeComponent(ie));
	addTo(cc_list0, getCodeComponents(ie.getLeftOperand()));
	addTo(cc_list0, getCodeComponents(ie.getRightOperand()));
	List exopr_objs = ie.extendedOperands();
	for (Object exopr_obj : exopr_objs) {
	    addTo(cc_list0, getCodeComponents((ASTNode) exopr_obj));
	}
	return cc_list0;
    }

    public static List<CodeComponent> getCodeComponents(InstanceofExpression ie) {
	if (ie == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	cc_list0.add(new CodeComponent(ie));
	addTo(cc_list0, getCodeComponents(ie.getLeftOperand()));
	addTo(cc_list0, getCodeComponents(ie.getRightOperand()));
	return cc_list0;
    }

    public static List<CodeComponent> getCodeComponents(LabeledStatement ls) {
	if (ls == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	cc_list0.add(new CodeComponent(ls));
	addTo(cc_list0, getCodeComponents(ls.getLabel()));
	addTo(cc_list0, getCodeComponents(ls.getBody()));
	return cc_list0;
    }

    public static List<CodeComponent> getCodeComponents(MethodInvocation mi) {
	if (mi == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	cc_list0.add(new CodeComponent(mi));
	addTo(cc_list0, getCodeComponents(mi.getExpression()));
	addTo(cc_list0, getCodeComponents(mi.getName()));
	List arg_objs = mi.arguments();
	for (Object arg_obj : arg_objs) {
	    addTo(cc_list0, getCodeComponents((ASTNode) arg_obj));
	}
	return cc_list0;
    }

    public static List<CodeComponent> getCodeComponents(NullLiteral nl) {
	if (nl == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	cc_list0.add(new CodeComponent(nl));
	return cc_list0;
    }

    public static List<CodeComponent> getCodeComponents(NumberLiteral nl) {
	if (nl == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	cc_list0.add(new CodeComponent(nl));
	return cc_list0;
    }

    public static List<CodeComponent> getCodeComponents(PackageDeclaration pd) {
	if (pd == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	cc_list0.add(new CodeComponent(pd));
	addTo(cc_list0, getCodeComponents(pd.getName()));
	return cc_list0;
    }

    public static List<CodeComponent> getCodeComponents(ParameterizedType pt) {
	if (pt == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	cc_list0.add(new CodeComponent(pt));
	addTo(cc_list0, getCodeComponents(pt.getType()));
	List targ_objs = pt.typeArguments();
	for (Object targ_obj : targ_objs) {
	    addTo(cc_list0, getCodeComponents((ASTNode) targ_obj));
	}
	return cc_list0;
    }

    public static List<CodeComponent> getCodeComponents(ParenthesizedExpression pe) {
	if (pe == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	addTo(cc_list0, getCodeComponents(pe.getExpression()));
	return cc_list0;
    }

    public static List<CodeComponent> getCodeComponents(PostfixExpression pe) {
	if (pe == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	cc_list0.add(new CodeComponent(pe));
	addTo(cc_list0, getCodeComponents(pe.getOperand()));
	return cc_list0;
    }

    public static List<CodeComponent> getCodeComponents(PrefixExpression pe) {
	if (pe == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	cc_list0.add(new CodeComponent(pe));
	addTo(cc_list0, getCodeComponents(pe.getOperand()));
	return cc_list0;
    }

    /* I don't collect primitive types. */
    public static List<CodeComponent> getCodeComponents(PrimitiveType pt) {
	if (pt == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	return cc_list0;
    }

    public static List<CodeComponent> getCodeComponents(QualifiedName qn) {
	if (qn == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	cc_list0.add(new CodeComponent(qn));
	addTo(cc_list0, getCodeComponents(qn.getQualifier()));
	addTo(cc_list0, getCodeComponents(qn.getName()));
	return cc_list0;
    }

    public static List<CodeComponent> getCodeComponents(QualifiedType qt) {
	if (qt == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	cc_list0.add(new CodeComponent(qt));
	addTo(cc_list0, getCodeComponents(qt.getQualifier()));
	addTo(cc_list0, getCodeComponents(qt.getName()));
	return cc_list0;
    }

    public static List<CodeComponent> getCodeComponents(ReturnStatement rs) {
	if (rs == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	cc_list0.add(new CodeComponent(rs));
	addTo(cc_list0, getCodeComponents(rs.getExpression()));
	return cc_list0;
    }

    public static List<CodeComponent> getCodeComponents(SimpleName sn) {
	if (sn == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	cc_list0.add(new CodeComponent(sn));
	return cc_list0;
    }

    public static List<CodeComponent> getCodeComponents(SimpleType st) {
	if (st == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	addTo(cc_list0, getCodeComponents(st.getName()));
	return cc_list0;
    }

    public static List<CodeComponent> getCodeComponents(SingleVariableDeclaration svd) {
	if (svd == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	cc_list0.add(new CodeComponent(svd));
	addTo(cc_list0, getCodeComponents(svd.getType()));
	addTo(cc_list0, getCodeComponents(svd.getName()));
	addTo(cc_list0, getCodeComponents(svd.getInitializer()));
	return cc_list0;
    }

    public static List<CodeComponent> getCodeComponents(StringLiteral sl) {
	if (sl == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	cc_list0.add(new CodeComponent(sl));
	return cc_list0;
    }

    public static List<CodeComponent> getCodeComponents(SuperConstructorInvocation sci) {
	if (sci == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	cc_list0.add(new CodeComponent(sci));
	addTo(cc_list0, getCodeComponents(sci.getExpression()));
	List arg_objs = sci.arguments();
	for (Object arg_obj : arg_objs) {
	    addTo(cc_list0, getCodeComponents((ASTNode) arg_obj));
	}
	List targ_objs = sci.typeArguments();
	for (Object targ_obj : targ_objs) {
	    addTo(cc_list0, getCodeComponents((ASTNode) targ_obj));
	}
	return cc_list0;
    }

    public static List<CodeComponent> getCodeComponents(SuperFieldAccess sfa) {
	if (sfa == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	cc_list0.add(new CodeComponent(sfa));
	addTo(cc_list0, getCodeComponents(sfa.getQualifier()));
	addTo(cc_list0, getCodeComponents(sfa.getName()));
	return cc_list0;
    }

    public static List<CodeComponent> getCodeComponents(SuperMethodInvocation smi) {
	if (smi == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	cc_list0.add(new CodeComponent(smi));
	addTo(cc_list0, getCodeComponents(smi.getQualifier()));
	addTo(cc_list0, getCodeComponents(smi.getName()));
	List arg_objs = smi.arguments();
	for (Object arg_obj : arg_objs) {
	    addTo(cc_list0, getCodeComponents((ASTNode) arg_obj));
	}
	List targ_objs = smi.typeArguments();
	for (Object targ_obj : targ_objs) {
	    addTo(cc_list0, getCodeComponents((ASTNode) targ_obj));
	}
	return cc_list0;
    }

    public static List<CodeComponent> getCodeComponents(SwitchCase sc) {
	if (sc == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	cc_list0.add(new CodeComponent(sc));
	addTo(cc_list0, getCodeComponents(sc.getExpression()));
	return cc_list0;
    }

    public static List<CodeComponent> getCodeComponents(SwitchStatement ss) {
	if (ss == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	cc_list0.add(new CodeComponent(ss));
	addTo(cc_list0, getCodeComponents(ss.getExpression()));
	List stmt_objs = ss.statements();
	for (Object stmt_obj : stmt_objs) {
	    addTo(cc_list0, getCodeComponents((ASTNode) stmt_obj));
	}
	return cc_list0;
    }

    public static List<CodeComponent> getCodeComponents(SynchronizedStatement ss) {
	if (ss == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	cc_list0.add(new CodeComponent(ss));
	addTo(cc_list0, getCodeComponents(ss.getExpression()));
	addTo(cc_list0, getCodeComponents(ss.getBody()));
	return cc_list0;
    }

    public static List<CodeComponent> getCodeComponents(ThisExpression te) {
	if (te == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	cc_list0.add(new CodeComponent(te));
	addTo(cc_list0, getCodeComponents(te.getQualifier()));
	return cc_list0;
    }

    public static List<CodeComponent> getCodeComponents(ThrowStatement ts) {
	if (ts == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	cc_list0.add(new CodeComponent(ts));
	addTo(cc_list0, getCodeComponents(ts.getExpression()));
	return cc_list0;
    }

    public static List<CodeComponent> getCodeComponents(TryStatement ts) {
	if (ts == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	cc_list0.add(new CodeComponent(ts));
	addTo(cc_list0, getCodeComponents(ts.getBody()));
	List cc_objs = ts.catchClauses();
	for (Object cc_obj : cc_objs) {
	    addTo(cc_list0, getCodeComponents((CatchClause) cc_obj));
	}
	addTo(cc_list0, getCodeComponents(ts.getFinally()));
	return cc_list0;
    }

    /* Currently, I don't handle this. */
    public static List<CodeComponent> getCodeComponents(TypeDeclaration td) {
	if (td == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	return cc_list0;
    }

    /* Currently, I don't handle this. */
    public static List<CodeComponent> getCodeComponents(TypeDeclarationStatement tds) {
	if (tds == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	return cc_list0;
    }

    public static List<CodeComponent> getCodeComponents(TypeLiteral tl) {
	if (tl == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	cc_list0.add(new CodeComponent(tl));
	addTo(cc_list0, getCodeComponents(tl.getType()));
	return cc_list0;
    }

    public static List<CodeComponent> getCodeComponents(TypeParameter tp) {
	if (tp == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	cc_list0.add(new CodeComponent(tp));
	addTo(cc_list0, getCodeComponents(tp.getName()));
	List tbound_objs = tp.typeBounds();
	for (Object tbound_obj : tbound_objs) {
	    addTo(cc_list0, getCodeComponents((ASTNode) tbound_obj));
	}
	return cc_list0;
    }

    public static List<CodeComponent> getCodeComponents(VariableDeclarationExpression vde) {
	if (vde == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	cc_list0.add(new CodeComponent(vde));
	addTo(cc_list0, getCodeComponents(vde.getType()));
	List fgt_objs = vde.fragments();
	for (Object fgt_obj : fgt_objs) {
	    addTo(cc_list0, getCodeComponents((ASTNode) fgt_obj));
	}
	return cc_list0;
    }
    
    public static List<CodeComponent> getCodeComponents(VariableDeclarationFragment vdf) {
	if (vdf == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	cc_list0.add(new CodeComponent(vdf));
	addTo(cc_list0, getCodeComponents(vdf.getInitializer()));
	addTo(cc_list0, getCodeComponents(vdf.getName()));
	return cc_list0;
    }

    public static List<CodeComponent> getCodeComponents(VariableDeclarationStatement vds) {
	if (vds == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	cc_list0.add(new CodeComponent(vds));
	addTo(cc_list0, getCodeComponents(vds.getType()));
	List vdf_objs = vds.fragments();
	for (Object vdf_obj : vdf_objs) {
	    addTo(cc_list0, getCodeComponents((VariableDeclarationFragment) vdf_obj));
	}
	return cc_list0;
    }

    public static List<CodeComponent> getCodeComponents(WhileStatement ws) {
	if (ws == null) { return new ArrayList<CodeComponent>(); }
	List<CodeComponent> cc_list0 = new ArrayList<CodeComponent>();
	cc_list0.add(new CodeComponent(ws));
	addTo(cc_list0, getCodeComponents(ws.getExpression()));
	addTo(cc_list0, getCodeComponents(ws.getBody()));
	return cc_list0;
    }
    
    private static void addTo(List<CodeComponent> cc_list0, List<CodeComponent> cc_list1) {
	for (CodeComponent cc1 : cc_list1) { cc_list0.add(cc1); }
    }
}
