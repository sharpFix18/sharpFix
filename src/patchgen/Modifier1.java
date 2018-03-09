package sharpfix.patchgen;

import edu.brown.cs.ivy.jcomp.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.text.edits.*;
import org.eclipse.jface.text.*;
import sharpfix.patchgen.ccmatcher.CCMaps;
import sharpfix.util.CodeToken;
import sharpfix.util.CodeTokenGenerator;


public class Modifier1
{
    private Replacer1 replacer;
    private Inserter inserter;
    private CodeTokenGenerator ctg;

    public Modifier1() {
	replacer = new Replacer1();
	inserter = new Inserter();
	ctg = new CodeTokenGenerator();
    }
    
    /* Note that all nodes need to be NORMALIZED. 
       Also note we assume node_list0 contains only the target node. */
    public List<Modification> modify(List<ASTNode> node_list0, List<ASTNode> node_list1, CCMaps ccmaps, AST ast0) {

	int node_list0_size = node_list0.size();
	if (node_list0_size != 1) { return new ArrayList<Modification>(); }
	
	List<Modification> rslt_list = new ArrayList<Modification>();
	Map<ASTNode,ASTNode> map0 = ccmaps.getMap1();
	Map<ASTNode,ASTNode> map1 = ccmaps.getMap2();
	Set<ASTNode> node_set0 = map0.keySet();
	Set<ASTNode> node_set1 = map1.keySet();

	//Replacement
	for (ASTNode node0 : node_set0) {
	    //==============
	    //System.err.println("CHECK HERE:");
	    //System.err.println(map0.get(node0));
	    //==============
	    ASTNode node1 = ASTNode.copySubtree(ast0, map0.get(node0));
	    List<Modification> mod_list0 = replacer.replaceWith(node0, node1, ast0);
	    for (Modification mod0 : mod_list0) {
		rslt_list.add(mod0);
	    }
	}

	//Guard insertion for matched exprs
	/*
	for (ASTNode node0 : node_set0) {
	    if (isAsExpression(node0)) {
		ASTNode node0_match = map0.get(node0);
		ASTNode node0_match_par = node0_match.getParent();
		if ((node0_match_par != null) && isAsExpression(node0_match_par)) {
		    ASTNode node1 = ASTNode.copySubtree(ast0, node0_match_par);
		    ASTRewrite rw0 = ASTRewrite.create(ast0);
		    NodeFinder nf = new NodeFinder(node0_match.toString());
		    node1.accept(nf);
		    ASTNode copied_node0_match = nf.getResult();
		    if (copied_node0_match != null) {
			rw0.replace(node0, node1, null);
			rw0.replace(copied_node0_match, node0, null);
			Modification mod0 = new Modification(rw0, "EXPRGUARD", ModificationSizeCalculator.calculate0(node0, node1, 0), ModificationSizeCalculator.calculate0(node0, node1, 1)); //this is NOT good!
			rslt_list.add(mod0);
		    }
		}
	    }
	}
	*/

	//If-condition insertion for matched stmts
	for (ASTNode node0 : node_set0) {
	    if (isAsStatement(node0)) {
		ASTNode node0_match = map0.get(node0);
		ASTNode node0_match_par = node0_match.getParent();
		if (node0_match_par instanceof ExpressionStatement) {
		    node0_match_par = node0_match_par.getParent(); }
		ASTNode node0_match_parpar = node0_match_par.getParent();
		if ((node0_match_par != null) &&
		    (node0_match_par instanceof Block) &&
		    (node0_match_parpar instanceof IfStatement)) {

		    //Currently we have two guarding rules:
		    //(1) Guarding the statemen itself.
		    //(2) Guarding the statement plus all the statements that come after it in the same block.
		    IfStatement node1 = (IfStatement) node0_match_parpar;
		    Expression node1_expr = node1.getExpression();

		    //For (1)
		    IfStatement newif0 = ast0.newIfStatement();
		    newif0.setExpression((Expression) ASTNode.copySubtree(ast0, node1_expr));
		    Block newblock0 = ast0.newBlock();
		    newif0.setThenStatement(newblock0);

		    //Copy the guarded statement (the statement for node0, could be not equal to node0) to the new block
		    List newblock_stmts0 = newblock0.statements();
		    ASTNode node0_par = node0.getParent();
		    boolean is_node0_stmtexpr = false;
		    if (node0_par instanceof ExpressionStatement) {
			is_node0_stmtexpr = true;
			node0_par = node0_par.getParent(); }
		    if (!(node0_par instanceof Block)) { continue; }
		    Block node0_block = (Block) node0_par;
		    List node0_blockstmt_objs = node0_block.statements();
		    int node0_blockstmt_objs_size = node0_blockstmt_objs.size();
		    int node0_index = (is_node0_stmtexpr) ? node0_blockstmt_objs.indexOf(node0.getParent()) : node0_blockstmt_objs.indexOf(node0);
		    if (node0_index == -1) { continue; }
		    
		    ASTRewrite rw0 = ASTRewrite.create(ast0);
		    ListRewrite lrw0 = rw0.getListRewrite(node0_block, Block.STATEMENTS_PROPERTY);
		    Statement node0_stmt = (Statement) node0_blockstmt_objs.get(node0_index);
		    newblock_stmts0.add((Statement) ASTNode.copySubtree(ast0, node0_stmt));
		    lrw0.replace(node0_stmt, (ASTNode) newif0, null);
		    //lrw0.remove(node0_stmt, null);
		    //lrw0.insertAt((ASTNode) newif, node0_index, null);
		    Modification mod0 = new Modification(rw0, "IFCONDGUARD", ModificationSizeCalculator.calculate0(null, node1_expr, 0), ModificationSizeCalculator.calculate0(null, node1_expr, 1));
		    rslt_list.add(mod0);

		    //For (2)
		    IfStatement newif1 = ast0.newIfStatement();
		    newif1.setExpression((Expression) ASTNode.copySubtree(ast0, node1_expr));
		    Block newblock1 = ast0.newBlock();
		    newif1.setThenStatement(newblock1);

		    //Copy the guarded statements to the new block
		    List newblock_stmts1 = newblock1.statements();
		    ASTRewrite rw1 = ASTRewrite.create(ast0);
		    ListRewrite lrw1 = rw1.getListRewrite(node0_block, Block.STATEMENTS_PROPERTY);
		    //Starting from node0_index, the statements are guarded
		    for (int i=node0_index; i<node0_blockstmt_objs_size; i++) {
			Statement blockstmt = (Statement) node0_blockstmt_objs.get(i);
			newblock_stmts1.add((Statement) ASTNode.copySubtree(ast0, blockstmt));
		    }

		    //Remove the guarded statements from the original block
		    for (int i=node0_index; i<node0_blockstmt_objs_size; i++) {
			ASTNode blockstmt_node = (ASTNode) node0_blockstmt_objs.get(i);
			lrw1.remove(blockstmt_node, null);
		    }

		    //Insert the new if-statement into the block
		    lrw1.insertLast((ASTNode) newif1, null);
		    Modification mod1 = new Modification(rw1, "IFCONDGUARD", ModificationSizeCalculator.calculate0(null, node1_expr, 0), ModificationSizeCalculator.calculate0(null, node1_expr, 1));
		    rslt_list.add(mod1);
		}
	    }
	}
	
	//Insertion
	for (ASTNode node0 : node_set0) { //Assuming only one node, so this loop should only iterate once!
	ASTNode node0_match = map0.get(node0);
	Statement node0_stmt = returnAsStatement(node0);
	Statement node0_match_stmt = returnAsStatement(node0_match);
	
	if (node0_stmt != null && node0_match_stmt != null) {
	    ASTNode node0_par = node0_stmt.getParent();
	    StructuralPropertyDescriptor node0_spd = node0_stmt.getLocationInParent();
	    ChildListPropertyDescriptor node0_clpd = null;
	    Statement candidate_a = null, candidate_b = null;
	    if (node0_spd != null && node0_spd.isChildListProperty()) {
		node0_clpd = (ChildListPropertyDescriptor) node0_spd;
		List node0_stmts = (List) node0_par.getStructuralProperty(node0_clpd);
		if (node0_stmts != null) {
		    int node0_index = node0_stmts.indexOf(node0_stmt);
		    if (node0_index-1 >= 0) {
			candidate_a = (Statement) node0_stmts.get(node0_index-1);
		    }
		    if (node0_index+1 < node0_stmts.size()) {
			candidate_b = (Statement) node0_stmts.get(node0_index+1);
		    }
		}
	    }
	    
	    ASTNode node0_match_par = node0_match_stmt.getParent();
	    StructuralPropertyDescriptor node0_match_spd = node0_match_stmt.getLocationInParent();
	    ChildListPropertyDescriptor node0_match_clpd = null;
	    Statement candidate0 = null, candidate1 = null;
	    if (node0_match_spd != null && node0_match_spd.isChildListProperty()) {
		node0_match_clpd = (ChildListPropertyDescriptor) node0_match_spd;
		List node0_match_stmts = (List) node0_match_par.getStructuralProperty(node0_match_clpd);
		if (node0_match_stmts != null) {
		    int node0_match_index = node0_match_stmts.indexOf(node0_match_stmt);
		    if (node0_match_index-1 >= 0) {
			candidate0 = (Statement) node0_match_stmts.get(node0_match_index-1);
		    }
		    if (node0_match_index+1 < node0_match_stmts.size()) {
			candidate1 = (Statement) node0_match_stmts.get(node0_match_index+1);
		    }
		}
	    }

	    //Check statement equivalence, if not equal, then insert
	    if (node0_clpd != null && candidate0 != null) {
		String s0 = getMatchString(candidate_a);
		String s1 = getMatchString(candidate0);
		if (!s0.equals(s1)) {
		    ASTRewrite rw0 = ASTRewrite.create(ast0);
		    ListRewrite lrw0 = rw0.getListRewrite(node0_par, node0_clpd);
		    ASTNode candidate0_cp = ASTNode.copySubtree(ast0, candidate0);
		    lrw0.insertBefore(candidate0_cp, node0_stmt, null);
		    Modification mod0 = new Modification(rw0, "INSERT", ModificationSizeCalculator.calculate0(null, candidate0, 0), ModificationSizeCalculator.calculate0(null, candidate0, 1));
		    rslt_list.add(mod0);
		}
	    }

	    if (node0_clpd != null && candidate1 != null) {
		String s0 = getMatchString(candidate_b);
		String s1 = getMatchString(candidate1);
		if (!s0.equals(s1)) {
		    ASTRewrite rw0 = ASTRewrite.create(ast0);
		    ListRewrite lrw0 = rw0.getListRewrite(node0_par, node0_clpd);
		    ASTNode candidate1_cp = ASTNode.copySubtree(ast0, candidate1);
		    lrw0.insertAfter(candidate1_cp, node0_stmt, null);
		    Modification mod0 = new Modification(rw0, "INSERT", ModificationSizeCalculator.calculate0(null, candidate1, 0), ModificationSizeCalculator.calculate0(null, candidate1, 1));
		    rslt_list.add(mod0);
		}
	    }
	}
	}

	//Replacing the whole method body
	ASTNode md0 = node_list0.get(0);
	while (md0 != null) {
	    if (md0 instanceof MethodDeclaration) { break; }
	    else { md0 = md0.getParent(); }
	}
	if (md0 != null) {
	    ASTNode md1 = null;
	    if (!node_list1.isEmpty()) { md1 = node_list1.get(0); }
	    while (md1 != null) {
		if (md1 instanceof MethodDeclaration) { break; }
		else { md1 = md1.getParent(); }
	    }
	    if (md1 != null) {
		Block md0_block = ((MethodDeclaration) md0).getBody();
		Block md1_block = ((MethodDeclaration) md1).getBody();
		ASTRewrite rw0 = ASTRewrite.create(ast0);
		ListRewrite lrw0 = rw0.getListRewrite(md0_block, Block.STATEMENTS_PROPERTY);
		List md0_block_stmts = md0_block.statements();
		List md1_block_stmts = md1_block.statements();
		for (Object md0_block_stmt : md0_block_stmts) {
		    lrw0.remove((ASTNode) md0_block_stmt, null);
		}
		for (Object md1_block_stmt : md1_block_stmts) {
		    lrw0.insertLast((ASTNode) md1_block_stmt, null);
		}
		Modification mod0 = new Modification(rw0, "METHODREPLACE", ModificationSizeCalculator.calculate0(md0_block, md1_block, 0), ModificationSizeCalculator.calculate0(md0_block, md1_block, 1));
		rslt_list.add(mod0);
	    }
	}
	
	return rslt_list;
    }

    private List<ASTNode> getAllNestedStatementsWithoutBlocks(ASTNode node) {
	StmtNodeVisitor visitor = new StmtNodeVisitor();
	node.accept(visitor);
	return visitor.getStmtNodeList();
    }

    private List<ASTNode> getAllNestedStatementsWithoutBlocks(List<ASTNode> node_list) {
	List<ASTNode> rslt_list = new ArrayList<ASTNode>();
	for (ASTNode node : node_list) {
	    StmtNodeVisitor visitor = new StmtNodeVisitor();
	    node.accept(visitor);
	    List<ASTNode> node_list0 = visitor.getStmtNodeList();
	    for (ASTNode node0 : node_list0) {
		rslt_list.add(node0);
	    }
	}
	return rslt_list;
    }
    
    /*
      Wrap any body statement as a list of statement.

      Without normalization, the body statement of a compound statement could be
      a non-block statement. In that case, what is returned is a list containing
      only that statement. Otherwise, the return is the block's statement list.
     */
    /*    
    private List getStatementList(Statement stmt) {

	if (stmt instanceof Block) {
	    return ((Block) stmt).statements();
	} else {
	    List stmt_list = new ArrayList();
	    stmt_list.add(stmt);
	    return stmt_list;
	}
    }
    */  
    /*
    private boolean resolved() {

	//Create a new JrepSource object whose file content is "tcu"'s text
	JrepSource patch_jrs = 
	    new JrepSource(tjrs.getFilePath(), tjrs.getFileName(), tcu.toString());

	//Call Jcomp for Resolving
	JcompControl jcc = new JcompControl();
	Collection<JcompSource> jcs_set = new ArrayList<JcompSource>();
	jcs_set.add(patch_jrs);
        JcompProject jcp = jcc.getSemanticData(src_jar_path, jcs_set);
	jcp.resolve();
        List<JcompMessage> err_msg_list = jcp.getMessages();
        if (err_msg_list == null || err_msg_list.isEmpty()) { 
	    return true; 
	} else {
            if (print_resolve_errors) { 
		System.err.println("=======================");
		System.err.println("Resolve Error Messages:");
		for (JcompMessage jc_msg : err_msg_list) {
		    System.err.println("File: " + jc_msg.getSource().getFileName()
				       + ", Line: " + jc_msg.getLineNumber());
		    System.err.println(jc_msg.getText());
		}
		System.err.println("=======================");
	    }
            return false;
	}
    }
	*/

    private class StmtNodeVisitor extends ASTVisitor
    {
	List<ASTNode> rslt_list;
	public StmtNodeVisitor() {
	    rslt_list = new ArrayList<ASTNode>();
	}
	public List<ASTNode> getStmtNodeList() {
	    return rslt_list;
	}
	@Override public boolean preVisit2(ASTNode node) {
	    if (node instanceof Statement) {
		if (node instanceof Block) {}
		else { rslt_list.add(node); }
		return true;
	    }
	    else if (node instanceof Expression) {
		return false;
	    }
	    else {
		return true;
	    }
	}
    }

    private boolean isAsExpression(ASTNode node) {
	if (node instanceof Expression) {
	    ASTNode par = node.getParent();
	    if (par instanceof ExpressionStatement) { return false; }
	    return true;
	}
	return false;
    }
    
    private boolean isAsStatement(ASTNode node) {
	if (node instanceof Statement) { return true; }
	else if (node instanceof Expression) {
	    ASTNode par = node.getParent();
	    if (par instanceof ExpressionStatement) { return true; }
	}
	return false;
    }

    private Statement returnAsStatement(ASTNode node) {
	if (node instanceof Statement) {
	    return (Statement) node;
	}
	else if (node instanceof Expression) {
	    ASTNode par = node.getParent();
	    if (par instanceof ExpressionStatement) {
		return (Statement) par;
	    }
	}
	return null;
    }

    private String getMatchString(ASTNode node) {
	if (node == null) { return ""; }
	List<CodeToken> cts = ctg.getCTs(node, -1);
	StringBuilder sb = new StringBuilder();
	for (CodeToken ct : cts) {
	    sb.append(ct.getText());
	}
	return sb.toString().replaceAll("\\s+","");
    }
    
    
    private class NodeFinder extends ASTVisitor {
	private String target_str;
	private ASTNode target_node;

	public NodeFinder(String tstr) {
	    target_str = tstr;
	    target_node = null;
	}

	public ASTNode getResult() { return target_node; }
	
	@Override public boolean preVisit2(ASTNode node) {
	    if (node.toString().trim().equals(target_str.trim())) {
		target_node = node;
	    }
	    if (target_node != null) { return false; }
	    else { return true; }
	}
    }
}
