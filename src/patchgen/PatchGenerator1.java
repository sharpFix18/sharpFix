package sharpfix.patchgen;

import java.util.List;
import java.util.ArrayList;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AST;
import sharpfix.patchgen.ccmatcher.CCMatcher1;
import sharpfix.patchgen.ccmatcher.CCMatcher2;
import sharpfix.patchgen.ccmatcher.CCMatcher3;
import sharpfix.patchgen.ccmatcher.CCMaps;
import sharpfix.patchgen.ccmatcher.CCMaps2;
import sharpfix.global.*;

public class PatchGenerator1
{
    private CCMatcher1 ccmatcher1;
    private CCMatcher2 ccmatcher2;
    private CCMatcher3 ccmatcher3;
    private Modifier1 modifier1;
    private Modifier2 modifier2;

    public PatchGenerator1() {
	ccmatcher1 = new CCMatcher1();
	ccmatcher2 = new CCMatcher2();
	ccmatcher3 = new CCMatcher3();
	modifier1 = new Modifier1();
	modifier2 = new Modifier2();
    }
    
    public List<Modification> generatePatches(BuggyChunk bchunk, CandidateChunk rcchunk) {
	return generatePatches(bchunk, rcchunk, 2, 1, false, false);
    }
    
    public List<Modification> generatePatches(BuggyChunk bchunk, CandidateChunk rcchunk, int ccmatch_flag, int modifier_flag) {
	return generatePatches(bchunk, rcchunk, ccmatch_flag, modifier_flag, false, false);
    }
    
    public List<Modification> generatePatches(BuggyChunk bchunk, CandidateChunk rcchunk, boolean print_ccmatching, boolean print_patches) {
	return generatePatches(bchunk, rcchunk, 2, 1, print_ccmatching, print_patches);
    }
	
    public List<Modification> generatePatches(BuggyChunk bchunk, CandidateChunk rcchunk, int ccmatch_flag, int modifier_flag, boolean print_ccmatching, boolean print_patches) {

	if (!bchunk.isNormalized()) {
	    Normalizer.normalize(bchunk);
	    bchunk.setNormalized(true);
	}
	if (!rcchunk.isNormalized()) {
	    Normalizer.normalize(rcchunk);
	    rcchunk.setNormalized(true);
	}
	
	List<ASTNode> node_list0 = bchunk.getNodeList();
	List<ASTNode> node_list1 = rcchunk.getNodeList();
	
	if (print_ccmatching || print_patches) {
	    System.out.println("------ Buggy Chunk ------");
	    for (ASTNode node0 : node_list0) { System.out.println(node0); }
	    System.out.println("------ Candidate Chunk ------");
	    for (ASTNode node1 : node_list1) { System.out.println(node1); }
	}
	
	if (node_list0.isEmpty()) { return new ArrayList<Modification>(); }
	AST ast0 = node_list0.get(0).getAST();
	CCMaps ccmaps = new CCMaps();
	CCMaps2 ccmaps2 = new CCMaps2();
	List<Modification> patch_rws = new ArrayList<Modification>();

	//Code Component Matching
	if (ccmatch_flag == 1) {
	    ccmaps = ccmatcher1.match(node_list0, node_list1); }
	else if (ccmatch_flag == 2) {
	    ccmaps = ccmatcher2.match(node_list0, node_list1); }
	else if (ccmatch_flag == 3) {
	    ccmaps2 = ccmatcher3.match(node_list0, node_list1); }
	if (print_ccmatching) {
	    if (ccmatch_flag == 3) { System.out.println(ccmaps2); }
	    else { System.out.println(ccmaps); }
	}

	//Code Modification
	if (modifier_flag == 1) {
	    if (ccmatch_flag != 3) {
		patch_rws = modifier1.modify(node_list0, node_list1, ccmaps, ast0); }}
	else if (modifier_flag == 2) {
	    if (ccmatch_flag == 3) {
		patch_rws = modifier2.modify(node_list0, node_list1, ccmaps2, ast0); }
	}

	if (print_patches) {
	    int patch_rws_size = patch_rws.size();
	    for (int i=0; i<patch_rws_size; i++) {
		System.out.println("------ Patch " + i + " ------");
		System.out.println(patch_rws.get(i));
	    }
	}

	return patch_rws; //to be validated
    }
}
