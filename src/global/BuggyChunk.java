package sharpfix.global;

import java.util.List;
import java.util.ArrayList;
import org.eclipse.jdt.core.dom.ASTNode;

public class BuggyChunk extends Chunk
{
    public BuggyChunk(String fpath, String loc, List<ASTNode> node_list) {
	super(fpath, loc, node_list);
    }

    public BuggyChunk(String fpath, String loc, List<ASTNode> node_list, boolean is_normalized) {
	super(fpath, loc, node_list, is_normalized);
    }
    
}
