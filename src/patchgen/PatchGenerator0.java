package sharpfix.patchgen;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import sharpfix.rename.Renamer;
import sharpfix.rename.RenameChunk;
import sharpfix.rename.RenameChunkFactory;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.apache.commons.io.FileUtils;
import sharpfix.util.*;
import sharpfix.global.*;

public class PatchGenerator0
{
    private PatchGenerator1 pgen1;
    private Renamer renamer;
    
    public PatchGenerator0() {
	pgen1 = new PatchGenerator1();
	renamer = new Renamer();
    }

    public List<Modification> generatePatches(BuggyChunk bchunk, CandidateChunk cchunk) {
	return generatePatches(bchunk, cchunk, 1, 2, 1, false, false, false);
    }

    public List<Modification> generatePatches(BuggyChunk bchunk, CandidateChunk cchunk, int rename_flag, int ccm_flag, int mod_flag) {
	return generatePatches(bchunk, cchunk, rename_flag, ccm_flag, mod_flag, false, false, false);
    }

    public List<Modification> generatePatches(BuggyChunk bchunk, CandidateChunk cchunk, boolean print_renaming, boolean print_ccmatching, boolean print_patches) {
	return generatePatches(bchunk, cchunk, 1, 2, 1, print_renaming, print_ccmatching, print_patches);
    }
    
    public List<Modification> generatePatches(BuggyChunk bchunk, CandidateChunk cchunk, int rename_flag, int ccm_flag, int mod_flag, boolean print_renaming, boolean print_ccmatching, boolean print_patches) {
	//Record the track locs for the candidate nodes
	List<List<TrackLoc>> track_locs_list = new ArrayList<List<TrackLoc>>();
	List<ASTNode> cnode_list = cchunk.getNodeList();
	int cnode_list_size = cnode_list.size();
	if (cnode_list.isEmpty()) { return new ArrayList<Modification>(); }
	CompilationUnit cu_node = cchunk.getCompilationUnit();
	for (ASTNode cnode : cnode_list) {
	    track_locs_list.add(ASTNodeTracker.getTrackLocs(cnode));
	}

	//We use the renaming method whose renaming flag is 1.
	//RenameChunk bck = RenameChunkFactory.getRenameChunk(bchunk.getFilePath(), bchunk.pgetLoc());
	//RenameChunk cck = RenameChunkFactory.getRenameChunk(cchunk.getFilePath(), cchunk.getLoc());
	RenameChunk bck = RenameChunkFactory.getRenameChunk(bchunk);
	RenameChunk cck = RenameChunkFactory.getRenameChunk(cchunk);
	List<String> rcfctnts = renamer.rename(bck, cck, 1, print_renaming);
	
	if (rcfctnts.isEmpty()) { //Add the candidate file's content
	    File cf = new File(cchunk.getFilePath());
	    String cfctnt = null;
	    try { cfctnt = FileUtils.readFileToString(cf, (String) null); }
	    catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
	    if (cfctnt != null) { rcfctnts.add(cfctnt); }
	}

	//Produce patches for the bug
	List<Modification> patch_rws = new ArrayList<Modification>();
	String cfpath = cchunk.getFilePath();
	String rcloc = null;
	for (String rcfctnt : rcfctnts) {
	    CompilationUnit rccu = (CompilationUnit) ASTNodeLoader.getResolvedASTNode(cfpath, rcfctnt);
	    List<ASTNode> rcnode_list = new ArrayList<ASTNode>();
	    
	    for (int i=0; i<cnode_list_size; i++) {		
		List<TrackLoc> track_locs = track_locs_list.get(i);
		ASTNode rcnode = ASTNodeTracker.track(rccu, track_locs);
		if (rcnode == null) {
		    System.err.println("******");
		    System.err.println("Failed to track the renamed candidate node.");
		    System.err.println("Candidate file path: " + cfpath);
		    System.err.println("The original candidate node:");
		    System.err.println(cnode_list.get(i));
		    System.err.println("The track locs:");
		    for (TrackLoc track_loc : track_locs) {
			System.err.println(track_loc);
		    }
		    System.err.println("******");
		    return new ArrayList<Modification>();
		}
		else {
		    rcnode_list.add(rcnode);
		}
	    }

	    CandidateChunk rcchunk = new CandidateChunk(cfpath, rcloc, rcnode_list);
	    List<Modification> patch_rws0 = pgen1.generatePatches(bchunk, rcchunk, ccm_flag, mod_flag, print_ccmatching, print_patches);
	    for (Modification patch_rw0 : patch_rws0) {
		patch_rws.add(patch_rw0);
	    }
	}

	return patch_rws;
    }
}
