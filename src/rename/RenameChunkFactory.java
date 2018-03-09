package sharpfix.rename;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.apache.commons.io.FileUtils;
import sharpfix.util.*;
import sharpfix.global.*;

public class RenameChunkFactory
{
    public static RenameChunk getRenameChunk(String fpath, String loc) {
	File f = new File(fpath);
	String fctnt = null;
	try { fctnt = FileUtils.readFileToString(f, (String) null); }
	catch (IOException e) { System.err.println(e); e.printStackTrace(); }
	if (fctnt == null) {
	    return new RenameChunk(fpath, loc, new ArrayList<ASTNode>(), null);
	}
	CompilationUnit cu = (CompilationUnit) ASTNodeLoader.getResolvedASTNode(fpath, fctnt);
	List<ASTNode> node_list = ASTNodeFinder.find(cu, loc);
	return new RenameChunk(fpath, loc, node_list, fctnt);
    }

    public static RenameChunk getRenameChunk(Chunk chunk) {
	File f = new File(chunk.getFilePath());
	String fctnt = null;
	try { fctnt = FileUtils.readFileToString(f, (String) null); }
	catch (IOException e) { System.err.println(e); e.printStackTrace(); }
	if (fctnt == null) {
	    return new RenameChunk(chunk.getFilePath(), chunk.getLoc(), new ArrayList<ASTNode>(), null);
	}
	else {
	    return new RenameChunk(chunk.getFilePath(), chunk.getLoc(), chunk.getNodeList(), fctnt);
	}
    }
}
