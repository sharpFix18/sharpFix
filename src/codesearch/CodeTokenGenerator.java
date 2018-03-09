package sharpfix.codesearch;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import org.eclipse.jdt.core.dom.*;


public class CodeTokenGenerator
{
    protected Map<ASTNode, Integer> property_map;

    public CodeTokenGenerator() {
	property_map = new HashMap<ASTNode, Integer>();
    }

    public CodeTokenGenerator(Map<ASTNode, Integer> property_map) {
	this.property_map = property_map;
    }

    public void setPropertyMap(Map<ASTNode, Integer> property_map) {
	this.property_map = property_map;
    }
    
    protected int getNewProp(ASTNode node, int default_prop) {
	Integer new_prop = property_map.get(node);
	if (new_prop != null) { return new_prop.intValue(); }
	else { return default_prop; }
    }

    protected List<CodeToken> append(List<CodeToken> list0, List<CodeToken> list1) {
	for (CodeToken ct1 : list1) { list0.add(ct1); }
	return list0;
    }

    public List<CodeToken> getCTs(List<?> args, String delim_symbol, int prop) {
    
        List<CodeToken> ct_list = new ArrayList<CodeToken>();
        int size = args.size();
        for (int i=0; i<size; i++) {
            ASTNode arg_node = (ASTNode) args.get(i);
            append(ct_list, getCTs(arg_node, getNewProp(arg_node, prop)));
            if (i!=size-1) { ct_list.add(new CodeToken(delim_symbol, prop)); }
        }
        return ct_list;
    }
    
    public List<CodeToken> getCTs(List<?> args, String start_symbol,
        			  String delim_symbol, String end_symbol,
        			  int prop) {
    
        List<CodeToken> ct_list = new ArrayList<CodeToken>();
        ct_list.add(new CodeToken(start_symbol, prop));
        append(ct_list, getCTs(args, delim_symbol, prop));
        ct_list.add(new CodeToken(end_symbol, prop));
        return ct_list;
    }

    protected List<CodeToken> getCTsForVSN(SimpleName sn, int prop) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	ct_list.add(new CodeToken(sn.getIdentifier(), getNewProp(sn, prop)));
	return ct_list;
    }

    protected List<CodeToken> getCTsForTSN(SimpleName tsn, int prop) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	ct_list.add(new CodeToken(tsn.getIdentifier(), getNewProp(tsn, prop)));
	return ct_list;
    }


    protected List<CodeToken> getCTsForTN(Name tn, int prop) {

	if (tn instanceof SimpleName) {
	    return getCTsForTSN((SimpleName) tn, getNewProp(tn, prop));
	}
	else {
	    QualifiedName tqn = (QualifiedName) tn;
	    Name tqnq = tqn.getQualifier();
	    List<CodeToken> ct_list = getCTsForTN(tqnq, getNewProp(tqnq, prop));
	    ct_list.add(new CodeToken(".", prop));
	    SimpleName tqnn = tqn.getName();
	    List<CodeToken> tsn_ct_list = getCTsForTSN(tqnn, getNewProp(tqnn, prop));
	    append(ct_list, tsn_ct_list);
	    return ct_list;
	}
    }
    

    protected List<CodeToken> getCTsForST(SimpleType st, int prop) {

	Name stn = st.getName();
	return getCTsForTN(stn, getNewProp(stn, prop));
    }


    protected List<CodeToken> getCTsForMSN(SimpleName msn, int prop) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	ct_list.add(new CodeToken(msn.getIdentifier(), getNewProp(msn, prop)));
	return ct_list;
    }


    protected List<CodeToken> getCTs(AnonymousClassDeclaration acd, int prop) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	ct_list.add(new CodeToken("{", prop));
	List<?> bd_list = acd.bodyDeclarations();
	for (Object bd_obj : bd_list) {
	    BodyDeclaration bd = (BodyDeclaration) bd_obj;
	    List<CodeToken> ct_list0 = getCTs(bd, getNewProp(bd, prop));
	    append(ct_list, ct_list0);
	}
	ct_list.add(new CodeToken("}", prop));
	return ct_list;
    }

    protected List<CodeToken> getCTs(ArrayAccess aa, int prop) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	ct_list.add(new CodeToken("[", prop));
	Expression array_expr = aa.getArray();
	Expression index_expr = aa.getIndex();
	List<CodeToken> ct_list0 = getCTs(array_expr, getNewProp(array_expr, prop));
	append(ct_list, ct_list0);
	List<CodeToken> ct_list1 = getCTs(index_expr, getNewProp(index_expr, prop));
	append(ct_list, ct_list1);
	ct_list.add(new CodeToken("]", prop));
	return ct_list;
    }
    
    protected List<CodeToken> getCTs(ArrayCreation ac, int prop) {
    
        List<CodeToken> ct_list = new ArrayList<CodeToken>();
        List<?> dimens = ac.dimensions();
        int dimens_size = dimens.size();
        ct_list.add(new CodeToken("new", prop));
        ArrayType at = ac.getType();
        append(ct_list, getCTs(at, getNewProp(at, prop)));
        for (int i=0; i<dimens_size; i++) {
            Expression exp = (Expression) dimens.get(i);
            ct_list.add(new CodeToken("[", prop));
            append(ct_list, getCTs(exp, getNewProp(exp, prop)));
            ct_list.add(new CodeToken("]", prop));
        }
        ArrayInitializer ai = ac.getInitializer();
        if (ai != null) {
            append(ct_list, getCTs(ai, getNewProp(ai, prop)));
        }
        return ct_list;
    }

    protected List<CodeToken> getCTs(ArrayInitializer ai, int prop) {

	return getCTs(ai.expressions(), "{", ",", "}", prop);
    }

    protected List<CodeToken> getCTs(ArrayType at, int prop) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	Type component_type = at.getComponentType();
	append(ct_list, getCTs(component_type, getNewProp(component_type, prop)));
	ct_list.add(new CodeToken("[", prop));
	ct_list.add(new CodeToken("]", prop));
	return ct_list;
    }

    protected List<CodeToken> getCTs(AssertStatement as, int prop) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	Expression exp = as.getExpression();
	Expression msg_exp = as.getMessage();
	ct_list.add(new CodeToken("assert", prop));
	append(ct_list, getCTs(exp, getNewProp(exp, prop)));
	if (msg_exp != null) {
	    ct_list.add(new CodeToken(":"));
	    append(ct_list, getCTs(msg_exp, getNewProp(msg_exp, prop)));
	}
	ct_list.add(new CodeToken(";", prop));
	return ct_list;
    }    

    protected List<CodeToken> getCTs(Assignment agn, int prop) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	Expression lhs = agn.getLeftHandSide();
	Expression rhs = agn.getRightHandSide();
	String op_str = agn.getOperator().toString();
	append(ct_list, getCTs(lhs, getNewProp(lhs, prop)));
	ct_list.add(new CodeToken(op_str, prop));
	append(ct_list, getCTs(rhs, getNewProp(rhs, prop)));
	return ct_list;
    }

    protected List<CodeToken> getCTs(Block block, int prop) {
    
        List<CodeToken> ct_list = new ArrayList<CodeToken>();
        if (block == null) { return ct_list; } //This is important!
        ct_list.add(new CodeToken("{", prop));
        List<?> stmts = block.statements();
        for (Object stmt_obj : stmts) {
            Statement stmt = (Statement) stmt_obj;
            append(ct_list, getCTs(stmt, getNewProp(stmt, prop)));
        }
        ct_list.add(new CodeToken("}", prop));
        return ct_list;
    }

    protected List<CodeToken> getCTs(BooleanLiteral bl, int prop) {

	List<CodeToken> ct_list= new ArrayList<CodeToken>();
	ct_list.add(new CodeToken(bl.toString(), prop));
	return ct_list;
    }

    protected List<CodeToken> getCTs(BreakStatement bs, int prop) {

	List<CodeToken> ct_list= new ArrayList<CodeToken>();
	ct_list.add(new CodeToken("break", prop));
	SimpleName sn = bs.getLabel();
	if (sn != null) {
	    append(ct_list, getCTs(sn, getNewProp(sn, prop)));
	}
	ct_list.add(new CodeToken(";", prop));
	return ct_list;
    }

    protected List<CodeToken> getCTs(CastExpression ce, int prop) {

	List<CodeToken> ct_list= new ArrayList<CodeToken>();
	ct_list.add(new CodeToken("(", prop));
	Type cast_type = ce.getType();
	append(ct_list, getCTs(cast_type, getNewProp(cast_type, prop)));
	ct_list.add(new CodeToken(")", prop));
	Expression cast_exp = ce.getExpression();
	append(ct_list, getCTs(cast_exp, getNewProp(cast_exp, prop)));
	return ct_list;
    }

    protected List<CodeToken> getCTs(CatchClause cc, int prop) {

	List<CodeToken> ct_list= new ArrayList<CodeToken>();
	ct_list.add(new CodeToken("catch", prop));
	ct_list.add(new CodeToken("(", prop));
	SingleVariableDeclaration svd = cc.getException();
	append(ct_list, getCTs(svd, getNewProp(svd, prop)));
	ct_list.add(new CodeToken(")", prop));
	Block body_block = cc.getBody();
	append(ct_list, getCTs(body_block, getNewProp(body_block, prop)));
	return ct_list;
    }

    protected List<CodeToken> getCTs(CharacterLiteral cl, int prop) {

	List<CodeToken> ct_list= new ArrayList<CodeToken>();
	ct_list.add(new CodeToken(cl.toString(), prop));
	return ct_list;
    }

    protected List<CodeToken> getCTs(ClassInstanceCreation cic, int prop) {

	List<CodeToken> ct_list= new ArrayList<CodeToken>();
	Expression exp = cic.getExpression();
	if (exp != null) {
	    append(ct_list, getCTs(exp, getNewProp(exp, prop)));
	    ct_list.add(new CodeToken(".", prop));
	}
	ct_list.add(new CodeToken("new", prop));
	Type cic_type = cic.getType();
	append(ct_list, getCTs(cic_type, getNewProp(cic_type, prop)));
	append(ct_list, getCTs(cic.arguments(), "(", ",", ")", prop));
	return ct_list;
    }    

    protected List<CodeToken> getCTs(CompilationUnit cu, int prop) {

	List<CodeToken> ct_list= new ArrayList<CodeToken>();
	List<?> types = cu.types();
	int types_size = types.size();
	for (int i=0; i<types_size; i++) {
	    Object atd_obj = types.get(i);
	    AbstractTypeDeclaration atd = (AbstractTypeDeclaration) atd_obj;
	    append(ct_list, getCTs(atd, getNewProp(atd, prop)));
	}
	return ct_list;
    }

    protected List<CodeToken> getCTs(ConditionalExpression ce, int prop) {

	List<CodeToken> ct_list= new ArrayList<CodeToken>();
	Expression e0 = ce.getExpression();
	Expression e1 = ce.getThenExpression();
	Expression e2 = ce.getElseExpression();	
	append(ct_list, getCTs(e0, getNewProp(e0, prop)));
	ct_list.add(new CodeToken("?", prop));
	append(ct_list, getCTs(e1, getNewProp(e1, prop)));
	ct_list.add(new CodeToken(":", prop));
	append(ct_list, getCTs(e2, getNewProp(e2, prop)));
	return ct_list;
    }

    protected List<CodeToken> getCTs(ConstructorInvocation ci, int prop) {
    
        List<CodeToken> ct_list= new ArrayList<CodeToken>();
        ct_list.add(new CodeToken("this", prop));
        List<?> arg_list = ci.arguments();
        append(ct_list, getCTs(arg_list, "(", ",", ")", prop));
        return ct_list;
    }

    protected List<CodeToken> getCTs(ContinueStatement cs, int prop) {
    
        List<CodeToken> ct_list= new ArrayList<CodeToken>();
        ct_list.add(new CodeToken("continue", prop));
        SimpleName sn = cs.getLabel();
        if (sn != null) {
            append(ct_list, getCTs(sn, getNewProp(sn, prop)));
        }
        ct_list.add(new CodeToken(";", prop));
        return ct_list;
    }

    protected List<CodeToken> getCTs(DoStatement do_stmt, int prop) {

	List<CodeToken> ct_list= new ArrayList<CodeToken>();
	Statement body_stmt = do_stmt.getBody();
	Expression exp = do_stmt.getExpression();
	ct_list.add(new CodeToken("do", prop));
	append(ct_list, getCTs(body_stmt, getNewProp(body_stmt, prop)));
	ct_list.add(new CodeToken("while", prop));
	ct_list.add(new CodeToken("(", prop));
	append(ct_list, getCTs(exp, getNewProp(exp, prop)));
	ct_list.add(new CodeToken(")", prop));
	return ct_list;
    }

    protected List<CodeToken> getCTs(EmptyStatement es, int prop) {

	List<CodeToken> ct_list= new ArrayList<CodeToken>();
	ct_list.add(new CodeToken(";", prop));
	return ct_list;
    }

    protected List<CodeToken> getCTs(EnhancedForStatement efs, int prop) {

	List<CodeToken> ct_list= new ArrayList<CodeToken>();
	ct_list.add(new CodeToken("for", prop));
	ct_list.add(new CodeToken("(", prop));
	SingleVariableDeclaration svd = efs.getParameter();
	append(ct_list, getCTs(svd, getNewProp(svd, prop)));
	ct_list.add(new CodeToken(":", prop));
	Expression exp = efs.getExpression();
	append(ct_list, getCTs(exp, getNewProp(exp, prop)));	
	ct_list.add(new CodeToken(")", prop));
	Statement body_stmt = efs.getBody();
	append(ct_list, getCTs(body_stmt, getNewProp(body_stmt, prop)));
	return ct_list;
    }

    protected List<CodeToken> getCTs(EnumConstantDeclaration ecd, int prop) {

	SimpleName sn = ecd.getName();
	return getCTs(sn, getNewProp(sn, prop));
    }

    protected List<CodeToken> getCTs(EnumDeclaration enum_decl, int prop) {

	List<CodeToken> ct_list= new ArrayList<CodeToken>();
	ct_list.add(new CodeToken("enum", prop));
	ct_list.add(new CodeToken("{", prop));
	//Enum Constant Declarations
	List<?> ecd_list = enum_decl.enumConstants();
	for (Object ecd_obj : ecd_list) {
	    EnumConstantDeclaration ecd = (EnumConstantDeclaration) ecd_obj;
	    append(ct_list, getCTs(ecd, getNewProp(ecd, prop)));
	}
	//Other Body Declarations
	List<?> bd_list = enum_decl.bodyDeclarations();
	for (Object bd_obj : bd_list) {
	    BodyDeclaration bd = (BodyDeclaration) bd_obj;
	    append(ct_list, getCTs(bd, getNewProp(bd, prop)));
	}
	ct_list.add(new CodeToken("}", prop));	
	return ct_list;
    }
    
    protected List<CodeToken> getCTs(ExpressionStatement es, int prop) {

	Expression exp = es.getExpression();
	List<CodeToken> ct_list = getCTs(exp, getNewProp(exp, prop));
	ct_list.add(new CodeToken(";", prop));
	return ct_list;
    }

    protected List<CodeToken> getCTs(FieldAccess fa, int prop) {

	Expression exp = fa.getExpression();
	SimpleName sn = fa.getName();
	List<CodeToken> ct_list = getCTs(exp, getNewProp(exp, prop));
	ct_list.add(new CodeToken(".", prop));
	append(ct_list, getCTs(sn, getNewProp(sn, prop)));
	return ct_list;
    }

    protected List<CodeToken> getCTs(FieldDeclaration fd, int prop) {
    
        Type fd_type = fd.getType();
        List<CodeToken> ct_list = getCTs(fd_type, getNewProp(fd_type, prop));
        List<?> fgmt_obj_list = fd.fragments();
        append(ct_list, getCTs(fgmt_obj_list, ",", prop));
        ct_list.add(new CodeToken(";", prop));
        return ct_list;
    }

    protected List<CodeToken> getCTs(ForStatement fs, int prop) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	ct_list.add(new CodeToken("for", prop));
	ct_list.add(new CodeToken("(", prop));
	append(ct_list, getCTs(fs.initializers(), ",", prop));
	ct_list.add(new CodeToken(";", prop));
	Expression cond_exp = fs.getExpression();
	append(ct_list, getCTs(cond_exp, getNewProp(cond_exp, prop)));
	ct_list.add(new CodeToken(";", prop));
	append(ct_list, getCTs(fs.updaters(), ",", prop));
	ct_list.add(new CodeToken(")", prop));
	Statement body_stmt = fs.getBody();
	append(ct_list, getCTs(body_stmt, getNewProp(body_stmt, prop)));
	return ct_list;
    }

    protected List<CodeToken> getCTs(IfStatement if_stmt, int prop) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	ct_list.add(new CodeToken("if", prop));
	ct_list.add(new CodeToken("(", prop));
	Expression if_exp = if_stmt.getExpression();
	append(ct_list, getCTs(if_exp, getNewProp(if_exp, prop)));
	ct_list.add(new CodeToken(")", prop));
	Statement then_stmt = if_stmt.getThenStatement();
	append(ct_list, getCTs(then_stmt, getNewProp(then_stmt, prop)));
	Statement else_stmt = if_stmt.getElseStatement();
	if (else_stmt != null) {
	    ct_list.add(new CodeToken("else", prop));
	    append(ct_list, getCTs(else_stmt, getNewProp(else_stmt, prop)));
	}
	return ct_list;
    }       

    protected List<CodeToken> getCTs(ImportDeclaration import_decl, int prop) {

	return new ArrayList<CodeToken>();
    }

    protected List<CodeToken> getCTs(InfixExpression ie, int prop) {
    
        Expression lhs = ie.getLeftOperand();
        String ptn_op = ie.getOperator().toString();
        Expression rhs = ie.getRightOperand();
        List<CodeToken> ct_list = getCTs(lhs, getNewProp(lhs, prop));
        ct_list.add(new CodeToken(ptn_op, prop));
        append(ct_list, getCTs(rhs, getNewProp(rhs, prop)));
        if (ie.hasExtendedOperands()) {
            List<?> ex_oprand_obj_list = ie.extendedOperands();
            for (Object ex_oprand_obj : ex_oprand_obj_list) {
        	ct_list.add(new CodeToken(ptn_op, prop));
        	Expression ex_oprand = (Expression) ex_oprand_obj;
        	append(ct_list, getCTs(ex_oprand, getNewProp(ex_oprand, prop)));
            }
        }
        return ct_list;
    }

    protected List<CodeToken> getCTs(Initializer init, int prop) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	ct_list.add(new CodeToken("static", prop));
	Block body_block = init.getBody();	
	append(ct_list, getCTs(body_block, getNewProp(body_block, prop)));
	return ct_list;
    }

    protected List<CodeToken> getCTs(InstanceofExpression ie, int prop) {

	Expression lopr = ie.getLeftOperand();
	Type ropr = ie.getRightOperand();
	List<CodeToken> ct_list = getCTs(lopr, getNewProp(lopr, prop));
	ct_list.add(new CodeToken("instanceof", prop));
	append(ct_list, getCTs(ropr, getNewProp(ropr, prop)));
	return ct_list;
    }

    protected List<CodeToken> getCTs(LabeledStatement ls, int prop) {
	
	SimpleName sn = ls.getLabel();
	List<CodeToken> ct_list = getCTs(sn, getNewProp(sn, prop));
	ct_list.add(new CodeToken(":", prop));
	Statement body_stmt = ls.getBody();
	append(ct_list, getCTs(body_stmt, getNewProp(body_stmt, prop)));
	ct_list.add(new CodeToken(";", prop));
	return ct_list;
    }

    protected List<CodeToken> getCTs(MethodDeclaration md, int prop) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	Type return_type = md.getReturnType2();
	if (return_type != null) {
	    append(ct_list, getCTs(return_type, getNewProp(return_type, prop)));
	}
	SimpleName sn = md.getName();
	append(ct_list, getCTsForMSN(sn, getNewProp(sn, prop)));
	append(ct_list, getCTs(md.parameters(), "(", ",", ")", prop));
	Block body_block = md.getBody();
	append(ct_list, getCTs(body_block, getNewProp(body_block, prop))); 
	return ct_list;
    }

    protected List<CodeToken> getCTs(MethodInvocation mi, int prop) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	Expression exp = mi.getExpression();
	if (exp != null) {
	    append(ct_list, getCTs(exp, getNewProp(exp, prop)));
	    ct_list.add(new CodeToken(".", prop));
	}
	SimpleName sn = mi.getName();
	append(ct_list, getCTs(sn, getNewProp(sn, prop)));
	append(ct_list, getCTs(mi.arguments(), "(", ",", ")", prop));
	return ct_list;
    }

    protected List<CodeToken> getCTs(NullLiteral nl, int prop) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	ct_list.add(new CodeToken(nl.toString(), prop));
	return ct_list;
    }

    protected List<CodeToken> getCTs(NumberLiteral nl, int prop) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	ct_list.add(new CodeToken(nl.toString(), prop));
	return ct_list;
    }

    protected List<CodeToken> getCTs(PackageDeclaration pd, int prop) {

	return new ArrayList<CodeToken>();
    }
    
    protected List<CodeToken> getCTs(ParameterizedType pt, int prop) {

	Type pt_type = pt.getType();
	return getCTs(pt_type, getNewProp(pt_type, prop));
    }

    protected List<CodeToken> getCTs(ParenthesizedExpression pe, int prop) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	ct_list.add(new CodeToken("(", prop));
	Expression pe_exp = pe.getExpression();
	append(ct_list, getCTs(pe_exp, getNewProp(pe_exp, prop)));
	ct_list.add(new CodeToken(")", prop));
	return ct_list;
    }

    protected List<CodeToken> getCTs(PostfixExpression pe, int prop) {

	Expression pe_exp = pe.getOperand();
	List<CodeToken> ct_list = getCTs(pe_exp, getNewProp(pe_exp, prop));
	ct_list.add(new CodeToken(pe.getOperator().toString(), prop));
	return ct_list;
    } 

    protected List<CodeToken> getCTs(PrefixExpression pe, int prop) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	ct_list.add(new CodeToken(pe.getOperator().toString(), prop));
	Expression pe_exp = pe.getOperand();
	append(ct_list, getCTs(pe_exp, getNewProp(pe_exp, prop)));
	return ct_list;
    }

    protected List<CodeToken> getCTs(PrimitiveType pt, int prop) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	ct_list.add(new CodeToken(pt.toString(), prop));
	return ct_list;
    }

    protected List<CodeToken> getCTs(QualifiedName qn, int prop) {

	Name qn0 = qn.getQualifier();
	SimpleName qn1 = qn.getName();
	List<CodeToken> ct_list = getCTs(qn0, getNewProp(qn0, prop));
	ct_list.add(new CodeToken(".", prop));
	append(ct_list, getCTs(qn1, getNewProp(qn1, prop)));
	return ct_list;
    }

    protected List<CodeToken> getCTs(QualifiedType qt, int prop) {

	Type qt0 = qt.getQualifier();
	SimpleName qt1 = qt.getName();
	List<CodeToken> ct_list = getCTs(qt0, getNewProp(qt0, prop));
	ct_list.add(new CodeToken(".", prop));
	append(ct_list, getCTsForTSN(qt1, getNewProp(qt1, prop)));
	return ct_list;
    }

    protected List<CodeToken> getCTs(ReturnStatement rs, int prop) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	ct_list.add(new CodeToken("return", prop));
	Expression exp = rs.getExpression();
	if (exp != null) {
	    append(ct_list, getCTs(exp, getNewProp(exp, prop)));
	}
	ct_list.add(new CodeToken(";", prop));
	return ct_list;
    }

    protected List<CodeToken> getCTs(SimpleName sn, int prop) {

	return getCTsForVSN(sn, prop);
    }
    
    protected List<CodeToken> getCTs(SimpleType st, int prop) {

	return getCTsForST(st, prop);
    }

    protected List<CodeToken> getCTs(SingleVariableDeclaration svd, int prop) {

	Type svd_type = svd.getType();
	SimpleName svd_sn = svd.getName();
	Expression init_exp = svd.getInitializer();
	List<CodeToken> ct_list = getCTs(svd_type, getNewProp(svd_type, prop));
	append(ct_list, getCTs(svd_sn, getNewProp(svd_sn, prop)));
	if (init_exp != null) {
	    ct_list.add(new CodeToken("=", prop));
	    append(ct_list, getCTs(init_exp, getNewProp(init_exp, prop)));
	}
	return ct_list;
    }

    protected List<CodeToken> getCTs(StringLiteral sl, int prop) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	ct_list.add(new CodeToken(sl.toString(), prop));
	return ct_list;
    }

    protected List<CodeToken> getCTs(SuperConstructorInvocation sci, int prop) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	Expression sci_exp = sci.getExpression();
	if (sci_exp != null) {
	    append(ct_list, getCTs(sci_exp, getNewProp(sci_exp, prop)));
	    ct_list.add(new CodeToken(".", prop));
	}
	ct_list.add(new CodeToken("super", prop));
	append(ct_list, getCTs(sci.arguments(), "(", ",", ")", prop));
	ct_list.add(new CodeToken(";", prop));
	return ct_list;
    }

    protected List<CodeToken> getCTs(SuperFieldAccess sfa, int prop) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	Expression qe = sfa.getQualifier();
	if (qe != null) {
	    append(ct_list, getCTs(qe, getNewProp(qe, prop)));
	    ct_list.add(new CodeToken(".", prop));
	}
	ct_list.add(new CodeToken("super", prop));
	ct_list.add(new CodeToken(".", prop));
	SimpleName sn = sfa.getName();
	append(ct_list, getCTs(sn, getNewProp(sn, prop)));
	return ct_list;
    }

    protected List<CodeToken> getCTs(SuperMethodInvocation smi, int prop) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	Expression qe = smi.getQualifier();
	if (qe != null) {
	    append(ct_list, getCTs(qe, getNewProp(qe, prop)));
	    ct_list.add(new CodeToken(".", prop));
	}
	ct_list.add(new CodeToken("super", prop));
	ct_list.add(new CodeToken(".", prop));
	SimpleName sn = smi.getName();
	append(ct_list, getCTsForMSN(sn, getNewProp(sn, prop)));
	append(ct_list, getCTs(smi.arguments(), "(", ",", ")", prop));
	return ct_list;
    }

    protected List<CodeToken> getCTs(SwitchCase sc, int prop) {
    
        List<CodeToken> ct_list = new ArrayList<CodeToken>();
        if (sc.isDefault()) {
            ct_list.add(new CodeToken("default", prop));
            ct_list.add(new CodeToken(":", prop));
        }
        else {
            ct_list.add(new CodeToken("case", prop));
            Expression exp = sc.getExpression();
            if (exp != null) {
        	append(ct_list, getCTs(exp, getNewProp(exp, prop)));
            }
            ct_list.add(new CodeToken(":", prop)); 
        }
        return ct_list;
    }

    protected List<CodeToken> getCTs(SwitchStatement ss, int prop) {
    
        List<CodeToken> ct_list = new ArrayList<CodeToken>();
        ct_list.add(new CodeToken("switch", prop));
        ct_list.add(new CodeToken("(", prop));
        Expression ss_exp = ss.getExpression();
        append(ct_list, getCTs(ss_exp, getNewProp(ss_exp, prop)));
        ct_list.add(new CodeToken(")", prop));
        ct_list.add(new CodeToken("{", prop));
        List<?> stat_obj_list = ss.statements();
        for (Object stat_obj : stat_obj_list) {
            Statement stat = (Statement) stat_obj;
            append(ct_list, getCTs(stat, getNewProp(stat, prop)));
        }
        ct_list.add(new CodeToken("}", prop));
        return ct_list;
    }

    protected List<CodeToken> getCTs(SynchronizedStatement ss, int prop) {
    
        List<CodeToken> ct_list = new ArrayList<CodeToken>();
        ct_list.add(new CodeToken("synchronized", prop));
        ct_list.add(new CodeToken("(", prop));
        Expression ss_exp = ss.getExpression();
        append(ct_list, getCTs(ss_exp, getNewProp(ss_exp, prop)));
        ct_list.add(new CodeToken(")", prop));
        Block body_block = ss.getBody();
        append(ct_list, getCTs(body_block, getNewProp(body_block, prop)));
        return ct_list;
    }

    protected List<CodeToken> getCTs(ThisExpression te, int prop) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	Name qn = te.getQualifier();
	if (qn != null) {
	    append(ct_list, getCTs(qn, getNewProp(qn, prop)));
	    ct_list.add(new CodeToken(".", prop));
	}
	ct_list.add(new CodeToken("this", prop));
	return ct_list;
    }

    protected List<CodeToken> getCTs(ThrowStatement ts, int prop) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	ct_list.add(new CodeToken("throw", prop));
	Expression ts_exp = ts.getExpression();
	append(ct_list, getCTs(ts_exp, getNewProp(ts_exp, prop)));
	ct_list.add(new CodeToken(";", prop));
	return ct_list;
    }

    protected  List<CodeToken> getCTs(TryStatement ts, int prop) {
    
        List<CodeToken> ct_list = new ArrayList<CodeToken>();
        ct_list.add(new CodeToken("try", prop));
        Block try_block = ts.getBody();
        append(ct_list, getCTs(try_block, getNewProp(try_block, prop)));
        List<?> cc_list = ts.catchClauses();
        for (Object ccobj : cc_list) {
            CatchClause cc = (CatchClause) ccobj;
            append(ct_list, getCTs(cc, getNewProp(cc, prop)));
        }
        Block finally_block = ts.getFinally();
        if (finally_block != null) {
            ct_list.add(new CodeToken("finally", prop));
            append(ct_list, getCTs(finally_block, getNewProp(finally_block, prop)));
        }
        return ct_list;
    }

    protected List<CodeToken> getCTs(TypeDeclaration td, int prop) {
    
        List<CodeToken> ct_list = new ArrayList<CodeToken>();
        if (td.isInterface()) {
            ct_list.add(new CodeToken("interface", prop));
        }
        else {
            ct_list.add(new CodeToken("class", prop));
        }
        SimpleName sn = td.getName();
        append(ct_list, getCTs(sn, getNewProp(sn, prop)));
        ct_list.add(new CodeToken("{", prop));
        List<?> bd_obj_list = td.bodyDeclarations();
        for (Object bd_obj : bd_obj_list) {
            BodyDeclaration bd = (BodyDeclaration) bd_obj;
            append(ct_list, getCTs(bd, getNewProp(bd, prop)));
        }
        ct_list.add(new CodeToken("}", prop));
        return ct_list;
    }

    protected List<CodeToken> getCTs(TypeLiteral tl, int prop) {

	Type tl_type = tl.getType();
	return getCTs(tl_type, getNewProp(tl_type, prop));
    }

    protected List<CodeToken> getCTs(TypeParameter tp, int prop) {
    
        SimpleName tp_sn = tp.getName();
        return getCTs(tp_sn, getNewProp(tp_sn, prop));
    }

    protected List<CodeToken> getCTs(UnionType ut, int prop) {
    
        List<CodeToken> ct_list = new ArrayList<CodeToken>();
        List<?> types = ut.types();
        int types_size = types.size();
        for (int i=0; i<types_size; i++) {
            Type type = (Type) types.get(i);
            if (i != 0) { ct_list.add(new CodeToken("|", prop)); }
            append(ct_list, getCTs(type, getNewProp(type, prop)));
        }
        return ct_list;
    }

    protected List<CodeToken> getCTs(VariableDeclarationExpression vde, int prop) {
    
        List<CodeToken> ct_list = new ArrayList<CodeToken>();
        Type vde_type = vde.getType();
        append(ct_list, getCTs(vde_type, getNewProp(vde_type, prop)));
        List<?> vdf_obj_list = vde.fragments();
        int vdf_obj_list_size = vdf_obj_list.size();
        for (int i=0; i<vdf_obj_list_size; i++) {
            VariableDeclarationFragment vdf =
        	(VariableDeclarationFragment) vdf_obj_list.get(i);
            if (i != 0) { ct_list.add(new CodeToken(",", prop)); }
            append(ct_list, getCTs(vdf, getNewProp(vdf, prop)));
        }
        return ct_list;
    }
    
    protected List<CodeToken> getCTs(VariableDeclarationFragment vdf, int prop) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	SimpleName vdf_sn = vdf.getName();
	append(ct_list, getCTs(vdf_sn, getNewProp(vdf_sn, prop)));
	Expression init_exp = vdf.getInitializer();
	if (init_exp != null) {
	    ct_list.add(new CodeToken("=", prop));
	    append(ct_list, getCTs(init_exp, getNewProp(init_exp, prop)));
	}
	return ct_list;
    }

    protected List<CodeToken> getCTs(VariableDeclarationStatement vds, int prop) {
    
        List<CodeToken> ct_list = new ArrayList<CodeToken>();
        Type vds_type = vds.getType();
        append(ct_list, getCTs(vds_type, getNewProp(vds_type, prop)));
        List<?> vdf_obj_list = vds.fragments();
        int vdf_obj_list_size = vdf_obj_list.size();
        for (int i=0; i<vdf_obj_list_size; i++) {
            VariableDeclarationFragment vdf =
        	(VariableDeclarationFragment) vdf_obj_list.get(i);
            if (i != 0) { ct_list.add(new CodeToken(",", prop)); }
            append(ct_list, getCTs(vdf, getNewProp(vdf, prop)));
        }
        ct_list.add(new CodeToken(";", prop));
        return ct_list;
    }

    protected List<CodeToken> getCTs(WhileStatement ws, int prop) {
    
        List<CodeToken> ct_list = new ArrayList<CodeToken>();
        ct_list.add(new CodeToken("while", prop));
        ct_list.add(new CodeToken("(", prop));
        Expression ws_exp = ws.getExpression();
        append(ct_list, getCTs(ws_exp, getNewProp(ws_exp, prop)));
        ct_list.add(new CodeToken(")", prop));
        Statement body_stmt = ws.getBody();
        append(ct_list, getCTs(body_stmt, getNewProp(body_stmt, prop)));
        return ct_list;
    }

    protected List<CodeToken> getCTs(WildcardType wct, int prop) {

	List<CodeToken> ct_list = new ArrayList<CodeToken>();
	ct_list.add(new CodeToken(wct.toString(), prop));
	return ct_list;
    }


    public List<CodeToken> getCTs(ASTNode node, int prop) {
    
        if (node == null) { return new ArrayList<CodeToken>(); }
    
        int ntype = node.getNodeType();
        
        if (ntype == ASTNode.ANONYMOUS_CLASS_DECLARATION) {
           
            return getCTs((AnonymousClassDeclaration) node, prop);
        }
    
        else if (ntype == ASTNode.ARRAY_ACCESS) {
    
            return getCTs((ArrayAccess) node, prop);
        }
    
        else if (ntype == ASTNode.ARRAY_CREATION) {
    
            return getCTs((ArrayCreation) node, prop);
        }
    
        else if (ntype == ASTNode.ARRAY_INITIALIZER) {
    
            return getCTs((ArrayInitializer) node, prop);
        }
    
        else if (ntype == ASTNode.ARRAY_TYPE) {
    
            return getCTs((ArrayType) node, prop);
        }
    
        else if (ntype == ASTNode.ASSERT_STATEMENT) {
    
            return getCTs((AssertStatement) node, prop);
        }
    
        else if (ntype == ASTNode.ASSIGNMENT) {
    
            return getCTs((Assignment) node, prop);
        }
    
        else if (ntype == ASTNode.BLOCK) {
    
            return getCTs((Block) node, prop);
        }
    
        else if (ntype == ASTNode.BOOLEAN_LITERAL) {
    
            return getCTs((BooleanLiteral) node, prop);
        }
    
        else if (ntype == ASTNode.BREAK_STATEMENT) { 
    
            return getCTs((BreakStatement) node, prop);
        }
    
        else if (ntype == ASTNode.CAST_EXPRESSION) {
    
            return getCTs((CastExpression) node, prop);
        }
    
        else if (ntype == ASTNode.CATCH_CLAUSE) {
    
            return getCTs((CatchClause) node, prop);
        }
    
        else if (ntype == ASTNode.CHARACTER_LITERAL) { 
            
            return getCTs((CharacterLiteral) node, prop);
        }
    
        else if (ntype == ASTNode.CLASS_INSTANCE_CREATION) {
    
            return getCTs((ClassInstanceCreation) node, prop);
        }
        
        else if (ntype == ASTNode.COMPILATION_UNIT) {
    
            return getCTs((CompilationUnit) node, prop);
        }
    
        else if (ntype == ASTNode.CONDITIONAL_EXPRESSION) {
    
            return getCTs((ConditionalExpression) node, prop);
        }
    
        else if (ntype == ASTNode.CONSTRUCTOR_INVOCATION) {
    
            return getCTs((ConstructorInvocation) node, prop);
        }
    
        else if (ntype == ASTNode.CONTINUE_STATEMENT) {
    
            return getCTs((ContinueStatement) node, prop);
        }
    
        else if (ntype == ASTNode.DO_STATEMENT) {
    
            return getCTs((DoStatement) node, prop);
        }
    
        else if (ntype == ASTNode.EMPTY_STATEMENT) { 
    
            return getCTs((EmptyStatement) node, prop);
        }
    
        else if (ntype == ASTNode.ENHANCED_FOR_STATEMENT) {
    
            return getCTs((EnhancedForStatement) node, prop);
        }
    
        else if (ntype == ASTNode.ENUM_CONSTANT_DECLARATION) {
            
            return getCTs((EnumConstantDeclaration) node, prop);
        }
    
        else if (ntype == ASTNode.ENUM_DECLARATION) {
            
            return getCTs((EnumDeclaration) node, prop);
        }
    
        else if (ntype == ASTNode.EXPRESSION_STATEMENT) {
    
            return getCTs((ExpressionStatement) node, prop);
        }
    
        else if (ntype == ASTNode.FIELD_ACCESS) {
    
            return getCTs((FieldAccess) node, prop);
        }
    
        else if (ntype == ASTNode.FIELD_DECLARATION) {
    
            return getCTs((FieldDeclaration) node, prop);
        }
    
        else if (ntype == ASTNode.FOR_STATEMENT) {
    
            return getCTs((ForStatement) node, prop);
        }
    
        else if (ntype == ASTNode.IF_STATEMENT) {
    
            return getCTs((IfStatement) node, prop);
        }
    
        else if (ntype == ASTNode.IMPORT_DECLARATION) { 
    
            return getCTs((ImportDeclaration) node, prop); 
        }
    
        else if (ntype == ASTNode.INFIX_EXPRESSION) {
    
            return getCTs((InfixExpression) node, prop);
        }
    
        else if (ntype == ASTNode.INITIALIZER) {
            
            return getCTs((Initializer) node, prop);
        }
    
        else if (ntype == ASTNode.INSTANCEOF_EXPRESSION) {
    
            return getCTs((InstanceofExpression) node, prop);
        }
    
        else if (ntype == ASTNode.LABELED_STATEMENT) {
    
            return getCTs((LabeledStatement) node, prop);
        }
    
        else if (ntype == ASTNode.METHOD_DECLARATION) {
    
            return getCTs((MethodDeclaration) node, prop);
        }
    
        else if (ntype == ASTNode.METHOD_INVOCATION) {
    
            return getCTs((MethodInvocation) node, prop);
        }
    
        else if (ntype == ASTNode.NULL_LITERAL) { 
    
            return getCTs((NullLiteral) node, prop); 
        }
    
        else if (ntype == ASTNode.NUMBER_LITERAL) {
    
            return getCTs((NumberLiteral) node, prop); 
        }
    
        else if (ntype == ASTNode.PACKAGE_DECLARATION) { 
    
            return getCTs((PackageDeclaration) node, prop);
        }
    
        else if (ntype == ASTNode.PARAMETERIZED_TYPE) {
    
            return getCTs((ParameterizedType) node, prop);
        }
    
        else if (ntype == ASTNode.PARENTHESIZED_EXPRESSION) {
    
            return getCTs((ParenthesizedExpression) node, prop);
        }
    
        else if (ntype == ASTNode.POSTFIX_EXPRESSION) {
    
            return getCTs((PostfixExpression) node, prop);
        }
    
        else if (ntype == ASTNode.PREFIX_EXPRESSION) {
    
            return getCTs((PrefixExpression) node, prop);
        }
    
        else if (ntype == ASTNode.PRIMITIVE_TYPE) { 
            
            return getCTs((PrimitiveType) node, prop);
        }
    
        else if (ntype == ASTNode.QUALIFIED_NAME) {
    
            return getCTs((QualifiedName) node, prop);
        }
    
        else if (ntype == ASTNode.QUALIFIED_TYPE) {
    
            return getCTs((QualifiedType) node, prop);
        }
    
        else if (ntype == ASTNode.RETURN_STATEMENT) {
    
            return getCTs((ReturnStatement) node, prop);
        }
    
        else if (ntype == ASTNode.SIMPLE_NAME) { 
    
            return getCTsForVSN((SimpleName) node, prop); 
        }
    
        else if (ntype == ASTNode.SIMPLE_TYPE) { 
    
            return getCTsForST((SimpleType) node, prop); 
        }
    
        else if (ntype == ASTNode.SINGLE_VARIABLE_DECLARATION) {
    
            return getCTs((SingleVariableDeclaration) node, prop);
        }
    
        else if (ntype == ASTNode.STRING_LITERAL) {
    
            return getCTs((StringLiteral) node, prop);
        }
    
        else if (ntype == ASTNode.SUPER_CONSTRUCTOR_INVOCATION) {
    
            return getCTs((SuperConstructorInvocation) node, prop);
        }
    
        else if (ntype == ASTNode.SUPER_FIELD_ACCESS) {
    
            return getCTs((SuperFieldAccess) node, prop);
        }
    
        else if (ntype == ASTNode.SUPER_METHOD_INVOCATION) {
    
            return getCTs((SuperMethodInvocation) node, prop);
        }
    
        else if (ntype == ASTNode.SWITCH_CASE) {
    
            return getCTs((SwitchCase) node, prop);
        }
    
        else if (ntype == ASTNode.SWITCH_STATEMENT) {
    
            return getCTs((SwitchStatement) node, prop);
        }
    
        else if (ntype == ASTNode.SYNCHRONIZED_STATEMENT) {
    
            return getCTs((SynchronizedStatement) node, prop);
        }
    
        else if (ntype == ASTNode.THIS_EXPRESSION) {
    
            return getCTs((ThisExpression) node, prop);
        }
    
        else if (ntype == ASTNode.THROW_STATEMENT) {
    
            return getCTs((ThrowStatement) node, prop);
        }
    
        else if (ntype == ASTNode.TRY_STATEMENT) {
    
            return getCTs((TryStatement) node, prop);
        }
    
        else if (ntype == ASTNode.TYPE_DECLARATION) {
    
            return getCTs((TypeDeclaration) node, prop);
        }
    
        else if (ntype == ASTNode.TYPE_LITERAL) {
    
            return getCTs((TypeLiteral) node, prop);
        }
    
        else if (ntype == ASTNode.TYPE_PARAMETER) { 
    
            TypeParameter tp = (TypeParameter) node;
            return getCTsForTSN(tp.getName(), prop);
        }
    
        else if (ntype == ASTNode.UNION_TYPE) {
    
            return getCTs((UnionType) node, prop);
        }
    
        else if (ntype == ASTNode.VARIABLE_DECLARATION_EXPRESSION) {
    
            return getCTs((VariableDeclarationExpression) node, prop);
        }
    
        else if (ntype == ASTNode.VARIABLE_DECLARATION_FRAGMENT) {
    
            return getCTs((VariableDeclarationFragment) node, prop);
        }
    
        else if (ntype == ASTNode.VARIABLE_DECLARATION_STATEMENT) {
    
            return getCTs((VariableDeclarationStatement) node, prop);
        }
    
        else if (ntype == ASTNode.WHILE_STATEMENT) {
    
            return getCTs((WhileStatement) node, prop);
        }
    
        else if (ntype == ASTNode.WILDCARD_TYPE) { 
    
            return getCTs((WildcardType) node, prop);
        }
    
        else {
            //I don't handle this
            return new ArrayList<CodeToken>();
        }
        
    }
}
