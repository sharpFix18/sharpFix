package sharpfix.global;

import java.util.List;
import org.eclipse.jdt.core.dom.ASTNode;

public class CandidateChunk extends Chunk
{
    boolean is_local;
    
    public CandidateChunk(String fpath, String loc, List<ASTNode> node_list) {
	super(fpath, loc, node_list);
	is_local = false;
    }

    public CandidateChunk(String fpath, String loc, List<ASTNode> node_list, boolean is_normalized) {
	super(fpath, loc, node_list, is_normalized);
	is_local = false;
    }

    public CandidateChunk(String fpath, String loc, List<ASTNode> node_list, boolean is_normalized, boolean islocal) {
	super(fpath, loc, node_list, is_normalized);
	is_local = islocal;
    }
    
    public void setLocal(boolean local) { is_local = local; }

    public boolean isLocal() { return is_local; }
}
