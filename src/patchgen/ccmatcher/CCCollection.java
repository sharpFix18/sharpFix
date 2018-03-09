package sharpfix.patchgen.ccmatcher;

import java.util.List;
import java.util.ArrayList;
import org.eclipse.jdt.core.dom.ASTNode;


public class CCCollection
{
    List<ASTNode> leaf_node_list;
    List<ASTNode> inner_node_list;
    //List<ASTNode> stmt_list;


    public CCCollection() {
	
	leaf_node_list = new ArrayList<ASTNode>();
        inner_node_list = new ArrayList<ASTNode>();
        //stmt_list = new ArrayList<ASTNode>();
    }

    public List<ASTNode> getLeafNodeList() { return leaf_node_list; }

    public List<ASTNode> getInnerNodeList() { return inner_node_list; }

    public void addLeafNode(ASTNode node) {

	if (node == null) { return; }
	leaf_node_list.add(node);
	//stmt_list.add(astmt);
    }

    public void addInnerNode(ASTNode node) {

	if (node == null) { return; }
	inner_node_list.add(node);
	//stmt_list.add(cstmt);
    }

    /*
    public void addStmt(Statement stmt) {

	if (stmt == null) { return; }
	if (ASTUtils.isCompoundStatement(stmt)) { inner_node_list.add(stmt); }
	else { leaf_list.add(stmt); }
	stmt_list.add(stmt);
    }
    */
}
