package sharpfix.rename;

import java.util.List;
import java.util.ArrayList;
import org.eclipse.jdt.core.dom.ASTNode;

public class RenameChunk
{
    String fpath;
    String loc;
    List<ASTNode> node_list;
    String fctnt;

    public RenameChunk(String fpath, String loc, List<ASTNode> node_list, String fctnt) {
	this.fpath = fpath;
	this.loc = loc;
	this.node_list = node_list;
	this.fctnt = fctnt;
    }

    public String getFilePath() { return fpath; }

    public String getLoc() { return loc; }
    
    public List<ASTNode> getNodeList() { return node_list; }

    public String getFileContent() { return fctnt; }

    public void addNode(ASTNode node) {
	if (node_list == null) { node_list = new ArrayList<ASTNode>(); }
	node_list.add(node);
    }
}
