package sharpfix.codesearch;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Block;
import java.util.List;
import java.util.ArrayList;
import sharpfix.util.*;
import org.apache.commons.io.FileUtils;
import java.io.File;

public class Indexer
{
    public static final String CCSEP = "<<<!!!>>>";

    int index_flag;
    
    public Indexer(int index_flag) { this.index_flag = index_flag; }

    public void index(File src, File index_f) {
	String s = null;
	if (src.isDirectory()) { s = indexDirectory(src); }
	else if (src.isFile()) {
	    String src_path = src.getPath();	    
	    if (src_path.endsWith(".java")) {
		s = indexFile(src);
	    }
	}
	if (s != null) {
	    try { FileUtils.writeStringToFile(index_f, s, (String) null); }
	    catch (Throwable t) {
		System.err.println(t);
		t.printStackTrace();
	    }
	}
    }
    
    public String indexDirectory(File src_dir) {
	File[] src_files = src_dir.listFiles();
	int src_files_size = src_files.length;
	StringBuilder sb = new StringBuilder();
	for (int i=0; i<src_files_size; i++) {
	    File src_file = src_files[i];
	    if (src_file.isDirectory()) {
		String index_str = indexDirectory(src_file);
		if ("".equals(index_str.trim())) {}
		else {
		    if (i!=0) { sb.append(CCSEP+"\n"); }
		    sb.append(index_str);
		}
	    }
	    else if (src_file.isFile()) {
		String src_fpath = src_file.getPath();
		if (src_fpath.endsWith(".java")) {
		    String index_str = indexFile(src_file);
		    if ("".equals(index_str.trim())) {}
		    else {
			if (i!=0) { sb.append(CCSEP+"\n"); }
			sb.append(index_str);
		    }
		}
	    }
	}
	return sb.toString();
    }

    private String indexFile(File src_f) {
	StringBuilder sb = new StringBuilder();
	ASTNode root = ASTNodeLoader.getResolvedASTNode(src_f);
	String src_fpath = src_f.getAbsolutePath();
	CompilationUnit cu = (CompilationUnit) root;
	List<MethodDeclaration> mds = MethodDeclarationGetter.getMethodDeclarations(cu);
	int mds_size = mds.size();
	for (int i=0; i<mds_size; i++) {
	    MethodDeclaration md = mds.get(i);
	    List<Statement> stmts = getStatements(md);
	    int stmts_size = stmts.size();
	    for (int j=0; j<stmts_size; j++) {
		if (i!=0 || j!=0) { sb.append(CCSEP+"\n"); }
		Statement stmt = stmts.get(j);
		int stmt_pos = stmt.getStartPosition();
		sb.append(src_fpath); sb.append(",");
		sb.append("slc:");
		sb.append(cu.getLineNumber(stmt_pos));
		sb.append(",");
		sb.append(cu.getColumnNumber(stmt_pos));
		sb.append("\n");

		List<String> index_strs = getIndexStrings(stmt, index_flag);
		int index_strs_size = index_strs.size();
		for (int k=0; k<index_strs_size; k++) {
		    if (k != 0) { sb.append(" "); }
		    sb.append(index_strs.get(k));
		}
		sb.append("\n");
	    }
	}
	return sb.toString();
    }

    public static List<String> getIndexStrings(ASTNode node, int idxflag) {
	List<String> index_strs = new ArrayList<String>();
	if (node == null) { return index_strs; }
	if (idxflag == 0) {
	    List<CodeComponent> node_ccs = CodeComponentExtractor.getCodeComponents(node);
	    for (CodeComponent node_cc : node_ccs) {
		index_strs.add(node_cc.getStringWithoutSpace());
	    }
	}
	else if (idxflag == 1) {
	    index_strs = ConceptWordExtractor.extract(node.toString(), false, false); //do not filter stop, short, or long words
	}
	else if (idxflag == 2) {
	    index_strs = ConceptTokenExtractor.extract(node);
	}
	return index_strs;
    }

    public static List<Statement> getStatements(ASTNode node) {
	if (node == null) { return new ArrayList<Statement>(); }
	StatementCollector sc = new StatementCollector();
	node.accept(sc);
	return sc.getStatementList();
    }

    public static List<Statement> getStatements(List<ASTNode> nodes) {
	if (nodes == null) { return new ArrayList<Statement>(); }
	StatementCollector sc = new StatementCollector();
	for (ASTNode node : nodes) {
	    if (node != null) { node.accept(sc); }
	}
	return sc.getStatementList();
    }
    
    private static class StatementCollector extends ASTVisitor {
	List<Statement> stmt_list;

	public StatementCollector() {
	    stmt_list = new ArrayList<Statement>();
	}

	public List<Statement> getStatementList() { return stmt_list; }

	@Override public void preVisit(ASTNode node) {
	    if (node instanceof Statement) {
		if (node instanceof Block) {} //Ignore any blocks.
		else { stmt_list.add((Statement) node); }
	    }
	}
    }
}
