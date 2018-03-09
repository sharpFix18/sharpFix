package sharpfix.global;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import sharpfix.util.ASTNodeLoader;
import sharpfix.util.ASTNodeFinder;

public class ChunkFactory
{
    public static Chunk getChunk(String fpath, String loc) {
	return new Chunk(fpath, loc, loadNodes(fpath,loc));
    }

    public static Chunk getChunk(String fpath, String loc, boolean is_normalized) {
	return new Chunk(fpath, loc, loadNodes(fpath,loc), is_normalized);
    }
    
    public static BuggyChunk getBuggyChunk(String fpath, String loc) {
	return new BuggyChunk(fpath, loc, loadNodes(fpath,loc));
    }

    public static BuggyChunk getBuggyChunk(String fpath, String loc, boolean is_normalized) {
	return new BuggyChunk(fpath, loc, loadNodes(fpath,loc), is_normalized);
    }

    public static CandidateChunk getCandidateChunk(String fpath, String loc) {
	return new CandidateChunk(fpath, loc, loadNodes(fpath,loc));
    }

    public static CandidateChunk getCandidateChunk(String fpath, String loc, boolean is_normalized) {
	return new CandidateChunk(fpath, loc, loadNodes(fpath,loc), is_normalized);
    }
    
    public static CandidateChunk getCandidateChunk(String fpath, String loc, boolean is_normalized, boolean is_local) {
	return new CandidateChunk(fpath, loc, loadNodes(fpath,loc), is_normalized, is_local);
    }
    
    private static List<ASTNode> loadNodes(String fpath, String loc) {
	CompilationUnit cu = null;
	try { cu = (CompilationUnit) ASTNodeLoader.getResolvedASTNode(new File(fpath));	}
	catch (Throwable t) {
	    System.err.println(t);
	    t.printStackTrace();
	}
	if (cu == null) { return new ArrayList<ASTNode>(); }
	else { return ASTNodeFinder.find(cu, loc); }
    }
}
