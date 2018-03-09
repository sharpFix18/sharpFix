package sharpfix.global;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import sharpfix.util.ASTNodeLoader;
import sharpfix.util.ASTNodeFinder;

public class Chunk
{
    String fpath;
    String loc;
    List<ASTNode> node_list;
    boolean is_normalized;

    public Chunk(String fpath, String loc, List<ASTNode> node_list) {
	this.fpath = fpath;
	this.loc = loc;
	this.node_list = node_list;
	this.is_normalized = false;
    }

    public Chunk(String fpath, String loc, List<ASTNode> node_list, boolean is_normalized) {
	this.fpath = fpath;
	this.loc = loc;
	this.node_list = node_list;
	this.is_normalized = is_normalized;
    }
    
    public String getFilePath() { return fpath; }

    public String getLoc() { return loc; }

    public List<ASTNode> getNodeList() { return node_list; }

    public int getLengthInLines() {
	if (node_list==null || node_list.isEmpty()) { return 0; }
	else {
	    int length = 0;
	    CompilationUnit cu = (CompilationUnit) node_list.get(0).getRoot();
	    for (ASTNode node : node_list) {
		int start_pos = node.getStartPosition();
		int end_pos = start_pos + node.getLength();
		length += cu.getLineNumber(end_pos) - cu.getLineNumber(start_pos) + 1;
	    }
	    return length;
	}
    }

    public void setNormalized(boolean norm) {
	is_normalized = norm;
    }
    
    public boolean isNormalized() {
	return is_normalized;
    }

    public boolean isValid() {
	return (node_list != null) && (!node_list.isEmpty());
    }

    public CompilationUnit getCompilationUnit() {
	if (isValid()) {
	    return (CompilationUnit) node_list.get(0).getRoot();
	}
	else {
	    return null;
	}
    }
}
