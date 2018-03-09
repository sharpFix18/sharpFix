package sharpfix.rename;

import java.util.List;
import java.util.ArrayList;
import sharpfix.util.*;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class TestRenamer
{
    public static void main(String[] args) {
	String bfpath = args[0];
	String bloc = args[1];
	String cfpath = args[2];
	String cloc = args[3];
	int flag = Integer.parseInt(args[4]);
	RenameChunk bchunk = RenameChunkFactory.getRenameChunk(bfpath, bloc);
	RenameChunk cchunk = RenameChunkFactory.getRenameChunk(cfpath, cloc);

	//Record the track locs for candidate nodes
	List<List<TrackLoc>> track_locs_list = new ArrayList<List<TrackLoc>>();
	List<ASTNode> cnode_list = cchunk.getNodeList();
	for (ASTNode cnode : cnode_list) {
	    track_locs_list.add(ASTNodeTracker.getTrackLocs(cnode));
	}
	
	Renamer renamer = new Renamer();
	List<String> renamed_strs = renamer.rename(bchunk, cchunk, flag, true);
	String rcfctnt0 = renamed_strs.get(0);
	CompilationUnit rccu =
	    (CompilationUnit) ASTNodeLoader.getResolvedASTNode(cfpath, rcfctnt0);
	List<ASTNode> rcnode_list = new ArrayList<ASTNode>();
	for (List<TrackLoc> track_locs : track_locs_list) {
	    ASTNode rcnode = ASTNodeTracker.track(rccu, track_locs);
	    rcnode_list.add(rcnode);
	}

	System.out.println("\n\n---------- TCHUNK ----------\n");
	List<ASTNode> bnode_list = bchunk.getNodeList();
	for (ASTNode bnode : bnode_list) { System.out.println(bnode); }
	
	System.out.println("\n\n---------- RENAMED CCHUNK ----------\n");
	for (ASTNode rcnode : rcnode_list) { System.out.println(rcnode); }
	
	System.out.println("\n\n---------- RENAMED FILE ----------\n");
	if (!renamed_strs.isEmpty()) { System.out.println(renamed_strs.get(0)); }
    }
}
