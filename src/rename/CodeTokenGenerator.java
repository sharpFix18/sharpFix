package sharpfix.rename;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import edu.brown.cs.ivy.jcomp.*;
import org.eclipse.jdt.core.dom.*;



public class CodeTokenGenerator
{
    protected List<CodeToken> append(List<CodeToken> list0, List<CodeToken> list1) {
	for (CodeToken ct1 : list1) { list0.add(ct1); }
	return list0;
    }

    public List<CodeToken> getCTs(List args, String delim_symbol) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
        int size = args.size();
        for (int i=0; i<size; i++) {
	    ASTNode arg_node = (ASTNode) args.get(i);
	    append(ct_list, getCTs(arg_node));
            if (i!=size-1) { ct_list.add(new CodeToken(delim_symbol)); }
	}
	return ct_list;
    }
    
    public List<CodeToken> getCTs(List args, String start_symbol,
				  String delim_symbol, String end_symbol) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	ct_list.add(new CodeToken(start_symbol));
	append(ct_list, getCTs(args, delim_symbol));
	ct_list.add(new CodeToken(end_symbol));
	return ct_list;
    }

    protected List<CodeToken> getCTsForVSN(SimpleName sn) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	ct_list.add(new CodeToken(sn));
	return ct_list;
    }

    protected List<CodeToken> getCTsForTSN(SimpleName tsn) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	ct_list.add(new CodeToken(tsn));
	return ct_list;
    }


    protected List<CodeToken> getCTsForTN(Name tn) {

	if (tn instanceof SimpleName) {
	    return getCTsForTSN((SimpleName) tn);
	}
	else {
	    QualifiedName tqn = (QualifiedName) tn;
	    Name tqnq = tqn.getQualifier();
	    List<CodeToken> ct_list = getCTsForTN(tqnq);
	    ct_list.add(new CodeToken("."));
	    SimpleName tqnn = tqn.getName();
	    List<CodeToken> tsn_ct_list = getCTsForTSN(tqnn);
	    append(ct_list, tsn_ct_list);
	    return ct_list;
	}
    }
    

    protected List<CodeToken> getCTsForST(SimpleType st) {

	Name stn = st.getName();
	return getCTsForTN(stn);
    }


    protected List<CodeToken> getCTsForMSN(SimpleName msn) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	ct_list.add(new CodeToken(msn));
	return ct_list;
    }


    protected List<CodeToken> getCTs(AnonymousClassDeclaration acd) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	ct_list.add(new CodeToken("{"));
	List<?> bd_list = acd.bodyDeclarations();
	for (Object bd_obj : bd_list) {
	    BodyDeclaration bd = (BodyDeclaration) bd_obj;
	    List<CodeToken> ct_list0 = getCTs(bd);
	    append(ct_list, ct_list0);
	}
	ct_list.add(new CodeToken("}"));
	return ct_list;
    }

    protected List<CodeToken> getCTs(ArrayAccess aa) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	ct_list.add(new CodeToken("["));
	Expression array_expr = aa.getArray();
	Expression index_expr = aa.getIndex();
	List<CodeToken> ct_list0 = getCTs(array_expr);
	append(ct_list, ct_list0);
	List<CodeToken> ct_list1 = getCTs(index_expr);
	append(ct_list, ct_list1);
	ct_list.add(new CodeToken("]"));
	return ct_list;
    }
    
    protected List<CodeToken> getCTs(ArrayCreation ac) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	List dimens = ac.dimensions();
	int dimens_size = dimens.size();
	ct_list.add(new CodeToken("new"));
	ArrayType at = ac.getType();
	append(ct_list, getCTs(at));
	for (int i=0; i<dimens_size; i++) {
	    Expression exp = (Expression) dimens.get(i);
	    ct_list.add(new CodeToken("["));
	    append(ct_list, getCTs(exp));
	    ct_list.add(new CodeToken("]"));
	}
	ArrayInitializer ai = ac.getInitializer();
	if (ai != null) {
	    append(ct_list, getCTs(ai));
	}
	return ct_list;
    }

    protected List<CodeToken> getCTs(ArrayInitializer ai) {

	return getCTs(ai.expressions(), "{", ",", "}");
    }

    protected List<CodeToken> getCTs(ArrayType at) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	Type elem_type = at.getElementType();
	append(ct_list, getCTs(elem_type));
	int dim = at.getDimensions();
	for (int i=0; i<dim; i++) {
	    ct_list.add(new CodeToken("["));
	    ct_list.add(new CodeToken("]"));
	}
	return ct_list;
	
	//Type component_type = at.getComponentType();
	//append(ct_list, getCTs(component_type));
	//ct_list.add(new CodeToken("["));
	//ct_list.add(new CodeToken("]"));
	//return ct_list;
    }

    protected List<CodeToken> getCTs(AssertStatement as) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	Expression exp = as.getExpression();
	Expression msg_exp = as.getMessage();
	ct_list.add(new CodeToken("assert"));
	append(ct_list, getCTs(exp));
	if (msg_exp != null) {
	    ct_list.add(new CodeToken(":"));
	    append(ct_list, getCTs(msg_exp));
	}
	ct_list.add(new CodeToken(";"));
	return ct_list;
    }    

    protected List<CodeToken> getCTs(Assignment agn) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	Expression lhs = agn.getLeftHandSide();
	Expression rhs = agn.getRightHandSide();
	String op_str = agn.getOperator().toString();
	append(ct_list, getCTs(lhs));
	ct_list.add(new CodeToken(op_str));
	append(ct_list, getCTs(rhs));
	return ct_list;
    }

    protected List<CodeToken> getCTs(Block block) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	if (block == null) { return ct_list; } //This is important!
	ct_list.add(new CodeToken("{"));
	List stmts = block.statements();
	for (Object stmt_obj : stmts) {
	    Statement stmt = (Statement) stmt_obj;
	    append(ct_list, getCTs(stmt));
	}
	ct_list.add(new CodeToken("}"));
	return ct_list;
    }

    protected List<CodeToken> getCTs(BooleanLiteral bl) {

	List<CodeToken> ct_list= new ArrayList<CodeToken>();
	ct_list.add(new CodeToken(bl.toString()));
	return ct_list;
    }

    protected List<CodeToken> getCTs(BreakStatement bs) {

	List<CodeToken> ct_list= new ArrayList<CodeToken>();
	ct_list.add(new CodeToken("break"));
	SimpleName sn = bs.getLabel();
	if (sn != null) {
	    append(ct_list, getCTs(sn));
	}
	ct_list.add(new CodeToken(";"));
	return ct_list;
    }

    protected List<CodeToken> getCTs(CastExpression ce) {

	List<CodeToken> ct_list= new ArrayList<CodeToken>();
	ct_list.add(new CodeToken("("));
	Type cast_type = ce.getType();
	append(ct_list, getCTs(cast_type));
	ct_list.add(new CodeToken(")"));
	Expression cast_exp = ce.getExpression();
	append(ct_list, getCTs(cast_exp));
	return ct_list;
    }

    protected List<CodeToken> getCTs(CatchClause cc) {

	List<CodeToken> ct_list= new ArrayList<CodeToken>();
	ct_list.add(new CodeToken("catch"));
	ct_list.add(new CodeToken("("));
	SingleVariableDeclaration svd = cc.getException();
	append(ct_list, getCTs(svd));
	ct_list.add(new CodeToken(")"));
	Block body_block = cc.getBody();
	append(ct_list, getCTs(body_block));
	return ct_list;
    }

    protected List<CodeToken> getCTs(CharacterLiteral cl) {

	List<CodeToken> ct_list= new ArrayList<CodeToken>();
	ct_list.add(new CodeToken(cl.toString()));
	return ct_list;
    }

    protected List<CodeToken> getCTs(ClassInstanceCreation cic) {

	List<CodeToken> ct_list= new ArrayList<CodeToken>();
	Expression exp = cic.getExpression();
	if (exp != null) {
	    append(ct_list, getCTs(exp));
	    ct_list.add(new CodeToken("."));
	}
	ct_list.add(new CodeToken("new"));
	Type cic_type = cic.getType();
	append(ct_list, getCTs(cic_type));
	append(ct_list, getCTs(cic.arguments(), "(", ",", ")"));
	return ct_list;
    }    

    protected List<CodeToken> getCTs(CompilationUnit cu) {

	List<CodeToken> ct_list= new ArrayList<CodeToken>();
	List<?> types = cu.types();
	int types_size = types.size();
	for (int i=0; i<types_size; i++) {
	    Object atd_obj = types.get(i);
	    AbstractTypeDeclaration atd = (AbstractTypeDeclaration) atd_obj;
	    append(ct_list, getCTs(atd));
	}
	return ct_list;
    }

    protected List<CodeToken> getCTs(ConditionalExpression ce) {

	List<CodeToken> ct_list= new ArrayList<CodeToken>();
	Expression e0 = ce.getExpression();
	Expression e1 = ce.getThenExpression();
	Expression e2 = ce.getElseExpression();	
	append(ct_list, getCTs(e0));
	ct_list.add(new CodeToken("?"));
	append(ct_list, getCTs(e1));
	ct_list.add(new CodeToken(":"));
	append(ct_list, getCTs(e2));
	return ct_list;
    }

    protected List<CodeToken> getCTs(ConstructorInvocation ci) {

	List<CodeToken> ct_list= new ArrayList<CodeToken>();
	ct_list.add(new CodeToken("this"));
	List arg_list = ci.arguments();
	append(ct_list, getCTs(arg_list, "(", ",", ")"));
	return ct_list;
    }

    protected List<CodeToken> getCTs(ContinueStatement cs) {

	List<CodeToken> ct_list= new ArrayList<CodeToken>();
	ct_list.add(new CodeToken("continue"));
	SimpleName sn = cs.getLabel();
	if (sn != null) {
	    append(ct_list, getCTs(sn));
	}
	ct_list.add(new CodeToken(";"));
	return ct_list;
    }

    protected List<CodeToken> getCTs(DoStatement do_stmt) {

	List<CodeToken> ct_list= new ArrayList<CodeToken>();
	Statement body_stmt = do_stmt.getBody();
	Expression exp = do_stmt.getExpression();
	ct_list.add(new CodeToken("do"));
	append(ct_list, getCTs(body_stmt));
	ct_list.add(new CodeToken("while"));
	ct_list.add(new CodeToken("("));
	append(ct_list, getCTs(exp));
	ct_list.add(new CodeToken(")"));
	return ct_list;
    }

    protected List<CodeToken> getCTs(EmptyStatement es) {

	List<CodeToken> ct_list= new ArrayList<CodeToken>();
	ct_list.add(new CodeToken(";"));
	return ct_list;
    }

    protected List<CodeToken> getCTs(EnhancedForStatement efs) {

	List<CodeToken> ct_list= new ArrayList<CodeToken>();
	ct_list.add(new CodeToken("for"));
	ct_list.add(new CodeToken("("));
	SingleVariableDeclaration svd = efs.getParameter();
	append(ct_list, getCTs(svd));
	ct_list.add(new CodeToken(":"));
	Expression exp = efs.getExpression();
	append(ct_list, getCTs(exp));	
	ct_list.add(new CodeToken(")"));
	Statement body_stmt = efs.getBody();
	append(ct_list, getCTs(body_stmt));
	return ct_list;
    }

    protected List<CodeToken> getCTs(EnumConstantDeclaration ecd) {

	SimpleName sn = ecd.getName();
	return getCTs(sn);
    }

    protected List<CodeToken> getCTs(EnumDeclaration enum_decl) {

	List<CodeToken> ct_list= new ArrayList<CodeToken>();
	ct_list.add(new CodeToken("enum"));
	ct_list.add(new CodeToken("{"));
	//Enum Constant Declarations
	List<?> ecd_list = enum_decl.enumConstants();
	for (Object ecd_obj : ecd_list) {
	    EnumConstantDeclaration ecd = (EnumConstantDeclaration) ecd_obj;
	    append(ct_list, getCTs(ecd));
	}
	//Other Body Declarations
	List<?> bd_list = enum_decl.bodyDeclarations();
	for (Object bd_obj : bd_list) {
	    BodyDeclaration bd = (BodyDeclaration) bd_obj;
	    append(ct_list, getCTs(bd));
	}
	ct_list.add(new CodeToken("}"));	
	return ct_list;
    }
    
    protected List<CodeToken> getCTs(ExpressionStatement es) {

	Expression exp = es.getExpression();
	List<CodeToken> ct_list = getCTs(exp);
	ct_list.add(new CodeToken(";"));
	return ct_list;
    }

    protected List<CodeToken> getCTs(FieldAccess fa) {

	Expression exp = fa.getExpression();
	SimpleName sn = fa.getName();
	List<CodeToken> ct_list = getCTs(exp);
	ct_list.add(new CodeToken("."));
	append(ct_list, getCTs(sn));
	return ct_list;
    }

    protected List<CodeToken> getCTs(FieldDeclaration fd) {

	Type fd_type = fd.getType();
	List<CodeToken> ct_list = getCTs(fd_type);
	List fgmt_obj_list = fd.fragments();
	append(ct_list, getCTs(fgmt_obj_list, ","));
	ct_list.add(new CodeToken(";"));
	return ct_list;
    }

    protected List<CodeToken> getCTs(ForStatement fs) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	ct_list.add(new CodeToken("for"));
	ct_list.add(new CodeToken("("));
	append(ct_list, getCTs(fs.initializers(), ","));
	ct_list.add(new CodeToken(";"));
	Expression cond_exp = fs.getExpression();
	append(ct_list, getCTs(cond_exp));
	ct_list.add(new CodeToken(";"));
	append(ct_list, getCTs(fs.updaters(), ","));
	ct_list.add(new CodeToken(")"));
	Statement body_stmt = fs.getBody();
	append(ct_list, getCTs(body_stmt));
	return ct_list;
    }

    protected List<CodeToken> getCTs(IfStatement if_stmt) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	ct_list.add(new CodeToken("if"));
	ct_list.add(new CodeToken("("));
	Expression if_exp = if_stmt.getExpression();
	append(ct_list, getCTs(if_exp));
	ct_list.add(new CodeToken(")"));
	Statement then_stmt = if_stmt.getThenStatement();
	append(ct_list, getCTs(then_stmt));
	Statement else_stmt = if_stmt.getElseStatement();
	if (else_stmt != null) {
	    ct_list.add(new CodeToken("else"));
	    append(ct_list, getCTs(else_stmt));
	}
	return ct_list;
    }       

    protected List<CodeToken> getCTs(ImportDeclaration import_decl) {

	return new ArrayList<CodeToken>();
    }

    protected List<CodeToken> getCTs(InfixExpression ie) {

	Expression lhs = ie.getLeftOperand();
	String ptn_op = ie.getOperator().toString();
	Expression rhs = ie.getRightOperand();
	List<CodeToken> ct_list = getCTs(lhs);
	ct_list.add(new CodeToken(ptn_op));
	append(ct_list, getCTs(rhs));
	if (ie.hasExtendedOperands()) {
	    List ex_oprand_obj_list = ie.extendedOperands();
	    for (Object ex_oprand_obj : ex_oprand_obj_list) {
		ct_list.add(new CodeToken(ptn_op));
		Expression ex_oprand = (Expression) ex_oprand_obj;
		append(ct_list, getCTs(ex_oprand));
	    }
	}
	return ct_list;
    }

    protected List<CodeToken> getCTs(Initializer init) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	ct_list.add(new CodeToken("static"));
	Block body_block = init.getBody();	
	append(ct_list, getCTs(body_block));
	return ct_list;
    }

    protected List<CodeToken> getCTs(InstanceofExpression ie) {

	Expression lopr = ie.getLeftOperand();
	Type ropr = ie.getRightOperand();
	List<CodeToken> ct_list = getCTs(lopr);
	ct_list.add(new CodeToken("instanceof"));
	append(ct_list, getCTs(ropr));
	return ct_list;
    }

    protected List<CodeToken> getCTs(LabeledStatement ls) {
	
	SimpleName sn = ls.getLabel();
	List<CodeToken> ct_list = getCTs(sn);
	ct_list.add(new CodeToken(":"));
	Statement body_stmt = ls.getBody();
	append(ct_list, getCTs(body_stmt));
	ct_list.add(new CodeToken(";"));
	return ct_list;
    }

    protected List<CodeToken> getCTs(MethodDeclaration md) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	Type return_type = md.getReturnType2();
	if (return_type != null) {
	    append(ct_list, getCTs(return_type));
	}
	SimpleName sn = md.getName();
	append(ct_list, getCTsForMSN(sn));
	append(ct_list, getCTs(md.parameters(), "(", ",", ")"));
	Block body_block = md.getBody();
	append(ct_list, getCTs(body_block)); 
	return ct_list;
    }

    protected List<CodeToken> getCTs(MethodInvocation mi) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	Expression exp = mi.getExpression();
	if (exp != null) {
	    append(ct_list, getCTs(exp));
	    ct_list.add(new CodeToken("."));
	}
	SimpleName sn = mi.getName();
	append(ct_list, getCTs(sn));
	append(ct_list, getCTs(mi.arguments(), "(", ",", ")"));
	return ct_list;
    }

    protected List<CodeToken> getCTs(NullLiteral nl) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	ct_list.add(new CodeToken(nl.toString()));
	return ct_list;
    }

    protected List<CodeToken> getCTs(NumberLiteral nl) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	ct_list.add(new CodeToken(nl.toString()));
	return ct_list;
    }

    protected List<CodeToken> getCTs(PackageDeclaration pd) {

	return new ArrayList<CodeToken>();
    }
    
    protected List<CodeToken> getCTs(ParameterizedType pt) {

	Type pt_type = pt.getType();
	return getCTs(pt_type);
    }

    protected List<CodeToken> getCTs(ParenthesizedExpression pe) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	ct_list.add(new CodeToken("("));
	Expression pe_exp = pe.getExpression();
	append(ct_list, getCTs(pe_exp));
	ct_list.add(new CodeToken(")"));
	return ct_list;
    }

    protected List<CodeToken> getCTs(PostfixExpression pe) {

	Expression pe_exp = pe.getOperand();
	List<CodeToken> ct_list = getCTs(pe_exp);
	ct_list.add(new CodeToken(pe.getOperator().toString()));
	return ct_list;
    } 

    protected List<CodeToken> getCTs(PrefixExpression pe) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	ct_list.add(new CodeToken(pe.getOperator().toString()));
	Expression pe_exp = pe.getOperand();
	append(ct_list, getCTs(pe_exp));
	return ct_list;
    }

    protected List<CodeToken> getCTs(PrimitiveType pt) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	ct_list.add(new CodeToken(pt.toString()));
	return ct_list;
    }

    protected List<CodeToken> getCTs(QualifiedName qn) {

	Name qn0 = qn.getQualifier();
	SimpleName qn1 = qn.getName();
	List<CodeToken> ct_list = getCTs(qn0);
	ct_list.add(new CodeToken("."));
	append(ct_list, getCTs(qn1));
	return ct_list;
    }

    protected List<CodeToken> getCTs(QualifiedType qt) {

	Type qt0 = qt.getQualifier();
	SimpleName qt1 = qt.getName();
	List<CodeToken> ct_list = getCTs(qt0);
	ct_list.add(new CodeToken("."));
	append(ct_list, getCTsForTSN(qt1));
	return ct_list;
    }

    protected List<CodeToken> getCTs(ReturnStatement rs) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	ct_list.add(new CodeToken("return"));
	Expression exp = rs.getExpression();
	if (exp != null) {
	    append(ct_list, getCTs(exp));
	}
	ct_list.add(new CodeToken(";"));
	return ct_list;
    }

    protected List<CodeToken> getCTs(SimpleName sn) {

	return getCTsForVSN(sn);
    }
    
    protected List<CodeToken> getCTs(SimpleType st) {

	return getCTsForST(st);
    }

    protected List<CodeToken> getCTs(SingleVariableDeclaration svd) {

	Type svd_type = svd.getType();
	SimpleName svd_sn = svd.getName();
	Expression init_exp = svd.getInitializer();
	List<CodeToken> ct_list = getCTs(svd_type);
	append(ct_list, getCTs(svd_sn));
	if (init_exp != null) {
	    ct_list.add(new CodeToken("="));
	    append(ct_list, getCTs(init_exp));
	}
	return ct_list;
    }

    protected List<CodeToken> getCTs(StringLiteral sl) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	ct_list.add(new CodeToken(sl.toString()));
	return ct_list;
    }

    protected List<CodeToken> getCTs(SuperConstructorInvocation sci) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	Expression sci_exp = sci.getExpression();
	if (sci_exp != null) {
	    append(ct_list, getCTs(sci_exp));
	    ct_list.add(new CodeToken("."));
	}
	ct_list.add(new CodeToken("super"));
	append(ct_list, getCTs(sci.arguments(), "(", ",", ")"));
	ct_list.add(new CodeToken(";"));
	return ct_list;
    }

    protected List<CodeToken> getCTs(SuperFieldAccess sfa) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	Expression qe = sfa.getQualifier();
	if (qe != null) {
	    append(ct_list, getCTs(qe));
	    ct_list.add(new CodeToken("."));
	}
	ct_list.add(new CodeToken("super"));
	ct_list.add(new CodeToken("."));
	SimpleName sn = sfa.getName();
	append(ct_list, getCTs(sn));
	return ct_list;
    }

    protected List<CodeToken> getCTs(SuperMethodInvocation smi) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	Expression qe = smi.getQualifier();
	if (qe != null) {
	    append(ct_list, getCTs(qe));
	    ct_list.add(new CodeToken("."));
	}
	ct_list.add(new CodeToken("super"));
	ct_list.add(new CodeToken("."));
	SimpleName sn = smi.getName();
	append(ct_list, getCTsForMSN(sn));
	append(ct_list, getCTs(smi.arguments(), "(", ",", ")"));
	return ct_list;
    }

    protected List<CodeToken> getCTs(SwitchCase sc) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	if (sc.isDefault()) {
	    ct_list.add(new CodeToken("default"));
	    ct_list.add(new CodeToken(":"));
	}
	else {
	    ct_list.add(new CodeToken("case"));
	    Expression exp = sc.getExpression();
	    if (exp != null) {
		append(ct_list, getCTs(exp));
	    }
	    ct_list.add(new CodeToken(":")); 
	}
	return ct_list;
    }

    protected List<CodeToken> getCTs(SwitchStatement ss) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	ct_list.add(new CodeToken("switch"));
	ct_list.add(new CodeToken("("));
	Expression ss_exp = ss.getExpression();
	append(ct_list, getCTs(ss_exp));
	ct_list.add(new CodeToken(")"));
	ct_list.add(new CodeToken("{"));
	List stat_obj_list = ss.statements();
	for (Object stat_obj : stat_obj_list) {
	    Statement stat = (Statement) stat_obj;
	    append(ct_list, getCTs(stat));
	}
	ct_list.add(new CodeToken("}"));
	return ct_list;
    }

    protected List<CodeToken> getCTs(SynchronizedStatement ss) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	ct_list.add(new CodeToken("synchronized"));
	ct_list.add(new CodeToken("("));
	Expression ss_exp = ss.getExpression();
	append(ct_list, getCTs(ss_exp));
	ct_list.add(new CodeToken(")"));
	Block body_block = ss.getBody();
	append(ct_list, getCTs(body_block));
	return ct_list;
    }

    protected List<CodeToken> getCTs(ThisExpression te) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	Name qn = te.getQualifier();
	if (qn != null) {
	    append(ct_list, getCTs(qn));
	    ct_list.add(new CodeToken("."));
	}
	ct_list.add(new CodeToken("this"));
	return ct_list;
    }

    protected List<CodeToken> getCTs(ThrowStatement ts) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	ct_list.add(new CodeToken("throw"));
	Expression ts_exp = ts.getExpression();
	append(ct_list, getCTs(ts_exp));
	ct_list.add(new CodeToken(";"));
	return ct_list;
    }

    protected  List<CodeToken> getCTs(TryStatement ts) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	ct_list.add(new CodeToken("try"));
	Block try_block = ts.getBody();
	append(ct_list, getCTs(try_block));
	List cc_list = ts.catchClauses();
	for (Object ccobj : cc_list) {
	    CatchClause cc = (CatchClause) ccobj;
	    append(ct_list, getCTs(cc));
	}
	Block finally_block = ts.getFinally();
	if (finally_block != null) {
	    ct_list.add(new CodeToken("finally"));
	    append(ct_list, getCTs(finally_block));
	}
	return ct_list;
    }

    protected List<CodeToken> getCTs(TypeDeclaration td) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	if (td.isInterface()) {
	    ct_list.add(new CodeToken("interface"));
	}
	else {
	    ct_list.add(new CodeToken("class"));
	}
	SimpleName sn = td.getName();
	append(ct_list, getCTs(sn));
	ct_list.add(new CodeToken("{"));
	List bd_obj_list = td.bodyDeclarations();
	for (Object bd_obj : bd_obj_list) {
	    BodyDeclaration bd = (BodyDeclaration) bd_obj;
	    append(ct_list, getCTs(bd));
	}
	ct_list.add(new CodeToken("}"));
	return ct_list;
    }

    protected List<CodeToken> getCTs(TypeLiteral tl) {

	Type tl_type = tl.getType();
	return getCTs(tl_type);
    }

    protected List<CodeToken> getCTs(TypeParameter tp) {

	SimpleName tp_sn = tp.getName();
	return getCTs(tp_sn);
    }

    protected List<CodeToken> getCTs(UnionType ut) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	List types = ut.types();
	int types_size = types.size();
	for (int i=0; i<types_size; i++) {
	    Type type = (Type) types.get(i);
	    if (i != 0) { ct_list.add(new CodeToken("|")); }
	    append(ct_list, getCTs(type));
	}
	return ct_list;
    }

    protected List<CodeToken> getCTs(VariableDeclarationExpression vde) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	Type vde_type = vde.getType();
	append(ct_list, getCTs(vde_type));
	List vdf_obj_list = vde.fragments();
	int vdf_obj_list_size = vdf_obj_list.size();
	for (int i=0; i<vdf_obj_list_size; i++) {
	    VariableDeclarationFragment vdf =
		(VariableDeclarationFragment) vdf_obj_list.get(i);
	    if (i != 0) { ct_list.add(new CodeToken(",")); }
	    append(ct_list, getCTs(vdf));
	}
	return ct_list;
    }
    
    protected List<CodeToken> getCTs(VariableDeclarationFragment vdf) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	SimpleName vdf_sn = vdf.getName();
	append(ct_list, getCTs(vdf_sn));
	Expression init_exp = vdf.getInitializer();
	if (init_exp != null) {
	    ct_list.add(new CodeToken("="));
	    append(ct_list, getCTs(init_exp));
	}
	return ct_list;
    }

    protected List<CodeToken> getCTs(VariableDeclarationStatement vds) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	Type vds_type = vds.getType();
	append(ct_list, getCTs(vds_type));
	List vdf_obj_list = vds.fragments();
	int vdf_obj_list_size = vdf_obj_list.size();
	for (int i=0; i<vdf_obj_list_size; i++) {
	    VariableDeclarationFragment vdf =
		(VariableDeclarationFragment) vdf_obj_list.get(i);
	    if (i != 0) { ct_list.add(new CodeToken(",")); }
	    append(ct_list, getCTs(vdf));
	}
	ct_list.add(new CodeToken(";"));
	return ct_list;
    }

    protected List<CodeToken> getCTs(WhileStatement ws) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	ct_list.add(new CodeToken("while"));
	ct_list.add(new CodeToken("("));
	Expression ws_exp = ws.getExpression();
	append(ct_list, getCTs(ws_exp));
	ct_list.add(new CodeToken(")"));
	Statement body_stmt = ws.getBody();
	append(ct_list, getCTs(body_stmt));
	return ct_list;
    }

    protected List<CodeToken> getCTs(WildcardType wct) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	ct_list.add(new CodeToken(wct.toString()));
	return ct_list;
    }


    public List<CodeToken> getCTs(ASTNode node) {

	if (node == null) { return new ArrayList<CodeToken>(); }

	int ntype = node.getNodeType();
	
        if (ntype == ASTNode.ANONYMOUS_CLASS_DECLARATION) {
           
            return getCTs((AnonymousClassDeclaration) node);
        }
    
        else if (ntype == ASTNode.ARRAY_ACCESS) {
    
            return getCTs((ArrayAccess) node);
        }
    
        else if (ntype == ASTNode.ARRAY_CREATION) {
    
            return getCTs((ArrayCreation) node);
        }
    
        else if (ntype == ASTNode.ARRAY_INITIALIZER) {
    
            return getCTs((ArrayInitializer) node);
        }
    
        else if (ntype == ASTNode.ARRAY_TYPE) {
    
            return getCTs((ArrayType) node);
        }
    
        else if (ntype == ASTNode.ASSERT_STATEMENT) {
    
            return getCTs((AssertStatement) node);
        }
    
        else if (ntype == ASTNode.ASSIGNMENT) {
    
            return getCTs((Assignment) node);
        }
    
        else if (ntype == ASTNode.BLOCK) {
    
            return getCTs((Block) node);
        }
    
        else if (ntype == ASTNode.BOOLEAN_LITERAL) {
    
            return getCTs((BooleanLiteral) node);
        }
    
        else if (ntype == ASTNode.BREAK_STATEMENT) { 
    
            return getCTs((BreakStatement) node);
        }
    
        else if (ntype == ASTNode.CAST_EXPRESSION) {
    
            return getCTs((CastExpression) node);
        }
    
        else if (ntype == ASTNode.CATCH_CLAUSE) {
    
            return getCTs((CatchClause) node);
        }
    
        else if (ntype == ASTNode.CHARACTER_LITERAL) { 
            
            return getCTs((CharacterLiteral) node);
        }
    
        else if (ntype == ASTNode.CLASS_INSTANCE_CREATION) {
    
            return getCTs((ClassInstanceCreation) node);
        }
        
        else if (ntype == ASTNode.COMPILATION_UNIT) {
    
            return getCTs((CompilationUnit) node);
        }
    
        else if (ntype == ASTNode.CONDITIONAL_EXPRESSION) {
    
            return getCTs((ConditionalExpression) node);
        }
    
        else if (ntype == ASTNode.CONSTRUCTOR_INVOCATION) {
    
            return getCTs((ConstructorInvocation) node);
        }
    
        else if (ntype == ASTNode.CONTINUE_STATEMENT) {
    
            return getCTs((ContinueStatement) node);
        }
    
        else if (ntype == ASTNode.DO_STATEMENT) {
    
            return getCTs((DoStatement) node);
        }
    
        else if (ntype == ASTNode.EMPTY_STATEMENT) { 
    
            return getCTs((EmptyStatement) node);
        }
    
        else if (ntype == ASTNode.ENHANCED_FOR_STATEMENT) {
    
            return getCTs((EnhancedForStatement) node);
        }
    
        else if (ntype == ASTNode.ENUM_CONSTANT_DECLARATION) {
            
            return getCTs((EnumConstantDeclaration) node);
        }
    
        else if (ntype == ASTNode.ENUM_DECLARATION) {
            
            return getCTs((EnumDeclaration) node);
        }
    
        else if (ntype == ASTNode.EXPRESSION_STATEMENT) {
    
            return getCTs((ExpressionStatement) node);
        }
    
        else if (ntype == ASTNode.FIELD_ACCESS) {
    
            return getCTs((FieldAccess) node);
        }
    
        else if (ntype == ASTNode.FIELD_DECLARATION) {
    
            return getCTs((FieldDeclaration) node);
        }
    
        else if (ntype == ASTNode.FOR_STATEMENT) {
    
            return getCTs((ForStatement) node);
        }
    
        else if (ntype == ASTNode.IF_STATEMENT) {
    
            return getCTs((IfStatement) node);
        }
    
        else if (ntype == ASTNode.IMPORT_DECLARATION) { 
    
            return getCTs((ImportDeclaration) node); 
        }
    
        else if (ntype == ASTNode.INFIX_EXPRESSION) {
    
            return getCTs((InfixExpression) node);
        }
    
        else if (ntype == ASTNode.INITIALIZER) {
            
            return getCTs((Initializer) node);
        }
    
        else if (ntype == ASTNode.INSTANCEOF_EXPRESSION) {
    
            return getCTs((InstanceofExpression) node);
        }
    
        else if (ntype == ASTNode.LABELED_STATEMENT) {
    
            return getCTs((LabeledStatement) node);
        }
    
        else if (ntype == ASTNode.METHOD_DECLARATION) {
    
            return getCTs((MethodDeclaration) node);
        }
    
        else if (ntype == ASTNode.METHOD_INVOCATION) {
    
            return getCTs((MethodInvocation) node);
        }
    
        else if (ntype == ASTNode.NULL_LITERAL) { 
    
            return getCTs((NullLiteral) node); 
        }
    
        else if (ntype == ASTNode.NUMBER_LITERAL) {
    
            return getCTs((NumberLiteral) node); 
        }
    
        else if (ntype == ASTNode.PACKAGE_DECLARATION) { 
    
            return getCTs((PackageDeclaration) node);
        }
    
        else if (ntype == ASTNode.PARAMETERIZED_TYPE) {
    
            return getCTs((ParameterizedType) node);
        }
    
        else if (ntype == ASTNode.PARENTHESIZED_EXPRESSION) {
    
            return getCTs((ParenthesizedExpression) node);
        }
    
        else if (ntype == ASTNode.POSTFIX_EXPRESSION) {
    
            return getCTs((PostfixExpression) node);
        }
    
        else if (ntype == ASTNode.PREFIX_EXPRESSION) {
    
            return getCTs((PrefixExpression) node);
        }
    
        else if (ntype == ASTNode.PRIMITIVE_TYPE) { 
            
            return getCTs((PrimitiveType) node);
        }
    
        else if (ntype == ASTNode.QUALIFIED_NAME) {
    
            return getCTs((QualifiedName) node);
        }
    
        else if (ntype == ASTNode.QUALIFIED_TYPE) {
    
            return getCTs((QualifiedType) node);
        }
    
        else if (ntype == ASTNode.RETURN_STATEMENT) {
    
            return getCTs((ReturnStatement) node);
        }
    
        else if (ntype == ASTNode.SIMPLE_NAME) { 
    
            return getCTsForVSN((SimpleName) node); 
        }
    
        else if (ntype == ASTNode.SIMPLE_TYPE) { 
    
            return getCTsForST((SimpleType) node); 
        }
    
        else if (ntype == ASTNode.SINGLE_VARIABLE_DECLARATION) {
    
            return getCTs((SingleVariableDeclaration) node);
        }
    
        else if (ntype == ASTNode.STRING_LITERAL) {
    
            return getCTs((StringLiteral) node);
        }
    
        else if (ntype == ASTNode.SUPER_CONSTRUCTOR_INVOCATION) {
    
            return getCTs((SuperConstructorInvocation) node);
        }
    
        else if (ntype == ASTNode.SUPER_FIELD_ACCESS) {
    
            return getCTs((SuperFieldAccess) node);
        }
    
        else if (ntype == ASTNode.SUPER_METHOD_INVOCATION) {
    
            return getCTs((SuperMethodInvocation) node);
        }
    
        else if (ntype == ASTNode.SWITCH_CASE) {
    
            return getCTs((SwitchCase) node);
        }
    
        else if (ntype == ASTNode.SWITCH_STATEMENT) {
    
            return getCTs((SwitchStatement) node);
        }
    
        else if (ntype == ASTNode.SYNCHRONIZED_STATEMENT) {
    
            return getCTs((SynchronizedStatement) node);
        }
    
        else if (ntype == ASTNode.THIS_EXPRESSION) {
    
            return getCTs((ThisExpression) node);
        }
    
        else if (ntype == ASTNode.THROW_STATEMENT) {
    
            return getCTs((ThrowStatement) node);
        }
    
        else if (ntype == ASTNode.TRY_STATEMENT) {
    
            return getCTs((TryStatement) node);
        }
    
        else if (ntype == ASTNode.TYPE_DECLARATION) {
    
            return getCTs((TypeDeclaration) node);
        }
    
        else if (ntype == ASTNode.TYPE_LITERAL) {
    
            return getCTs((TypeLiteral) node);
        }
    
        else if (ntype == ASTNode.TYPE_PARAMETER) { 
    
            TypeParameter tp = (TypeParameter) node;
            return getCTsForTSN(tp.getName());
        }
    
        else if (ntype == ASTNode.UNION_TYPE) {
    
            return getCTs((UnionType) node);
        }
    
        else if (ntype == ASTNode.VARIABLE_DECLARATION_EXPRESSION) {
    
            return getCTs((VariableDeclarationExpression) node);
        }
    
        else if (ntype == ASTNode.VARIABLE_DECLARATION_FRAGMENT) {
    
            return getCTs((VariableDeclarationFragment) node);
        }
    
        else if (ntype == ASTNode.VARIABLE_DECLARATION_STATEMENT) {
    
            return getCTs((VariableDeclarationStatement) node);
        }
    
        else if (ntype == ASTNode.WHILE_STATEMENT) {
    
            return getCTs((WhileStatement) node);
        }
    
        else if (ntype == ASTNode.WILDCARD_TYPE) { 
    
            return getCTs((WildcardType) node);
        }
    
        else {
            //I don't handle this
            return new ArrayList<CodeToken>();
        }
	
    }
}
