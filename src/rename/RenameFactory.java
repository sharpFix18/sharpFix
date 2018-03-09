package sharpfix.rename;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.apache.commons.io.FileUtils;
import sharpfix.global.*;
import sharpfix.util.*;

public class RenameFactory
{
    /* The method creates a renamed version of cchunk using sharpFix's renaming method.*/
    public static CandidateChunk getRenamedCandidateChunk(BuggyChunk bchunk, CandidateChunk cchunk) {
	return getRenamedCandidateChunk(bchunk, cchunk, false);
    }

    public static CandidateChunk getRenamedCandidateChunk(BuggyChunk bchunk, CandidateChunk cchunk, boolean print_renaming) {
	String cfpath = cchunk.getFilePath();
	
	//Record the track locs for the untranslated candidate nodes
	List<List<TrackLoc>> track_locs_list = new ArrayList<List<TrackLoc>>();
	List<ASTNode> cnodes = cchunk.getNodeList();
	int cnodes_size = cnodes.size();
	CompilationUnit ccu = cchunk.getCompilationUnit();
	for (ASTNode cnode : cnodes) {
	    track_locs_list.add(ASTNodeTracker.getTrackLocs(cnode));
	}
	
	//Produce a method chunk for cchunk
	CandidateChunk cmdchunk = null;
	ASTNode cchunk_node0 = cchunk.getNodeList().get(0);
	if (cchunk_node0 instanceof MethodDeclaration) {
	    cmdchunk = cchunk;
	}
	else {
	    ASTNode cchunk_node0_md = cchunk_node0;
	    while (cchunk_node0_md != null) {
		if (cchunk_node0_md instanceof MethodDeclaration) { break; }
		cchunk_node0_md = cchunk_node0_md.getParent();
	    }
	    if (cchunk_node0_md != null) {
		int cchunk_node0_md_startpos = cchunk_node0_md.getStartPosition();
		String cchunk_node0_md_loc = "slc:"+ccu.getLineNumber(cchunk_node0_md_startpos)+","+ccu.getColumnNumber(cchunk_node0_md_startpos);
		List<ASTNode> cmdchunk_nodes = new ArrayList<ASTNode>();
		cmdchunk_nodes.add(cchunk_node0_md);
		cmdchunk = new CandidateChunk(cfpath, cchunk_node0_md_loc, cmdchunk_nodes);
	    }
	}
	if (cmdchunk == null) {
	    System.err.println("Failed in producing the method candidate chunk.");
	    return new CandidateChunk(cfpath, null, new ArrayList<ASTNode>());
	}

	//Do the renaming between bchunk & cmdcchunk (sharpFix renames any identifier in cmdchunk to identifiers that are not just within bchunk but within an extended scope of bchunk: the method body of bchunk, the class fields, the declared methods in bchunk's class)
	RenameChunk bchunk_for_rename = RenameChunkFactory.getRenameChunk(bchunk);
	RenameChunk cchunk_for_rename = RenameChunkFactory.getRenameChunk(cmdchunk);
	Renamer renamer = new Renamer();
	List<String> rcfctnts = renamer.rename(bchunk_for_rename, cchunk_for_rename, 1, print_renaming);
	if (rcfctnts.isEmpty()) { //Add the candidate file's content
	    //File cf = new File(cfpath);
	    //String cfctnt = null;
	    //try { cfctnt = FileUtils.readFileToString(cf, (String) null); }
	    //catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
	    //if (cfctnt != null) { rcfctnts.add(cfctnt); }
	    return new CandidateChunk(cfpath, null, new ArrayList<ASTNode>());
	}

	String rcloc = null;
	String rcfctnt = rcfctnts.get(0);
	CompilationUnit rccu = null;
	try {
	    rccu = (CompilationUnit) ASTNodeLoader.getResolvedASTNode(cfpath, rcfctnt);
	}
	catch (Throwable t) {
	    System.err.println("Cannot Resolve the Translated Candidate Content");
	    System.err.println("bchunk fpath: " + bchunk.getFilePath());
	    System.err.println("bchunk loc: " + bchunk.getLoc());
	    System.err.println("cchunk fpath: " + cchunk.getFilePath());
	    System.err.println("cchunk loc: " + cchunk.getLoc());
	    System.err.println(t);
	    t.printStackTrace();
	}
	if (rccu == null) {
	    return new CandidateChunk(cfpath, null, new ArrayList<ASTNode>());
	}
	
	List<ASTNode> rcnode_list = new ArrayList<ASTNode>();
	for (int i=0; i<cnodes_size; i++) {
	    List<TrackLoc> track_locs = track_locs_list.get(i);
	    ASTNode rcnode = ASTNodeTracker.track(rccu, track_locs);
	    if (rcnode == null) {
		System.err.println("******");
		System.err.println("Failed to track the renamed candidate node.");
		System.err.println("Candidate file path: " + cfpath);
		System.err.println("The original candidate node:");
		System.err.println(cnodes.get(i));
		System.err.println("The track locs:");
		for (TrackLoc track_loc : track_locs) { System.err.println(track_loc); }
		System.err.println("******");
	    }
	    else {
		rcnode_list.add(rcnode);
	    }
	}

	//Produce patches
	CandidateChunk rcchunk = new CandidateChunk(cfpath, rcloc, rcnode_list); //Currently, rcloc is set as null (and will not be used later)
	return rcchunk;
    }
}
