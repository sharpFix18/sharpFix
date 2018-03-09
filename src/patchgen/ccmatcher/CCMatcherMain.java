package sharpfix.patchgen.ccmatcher;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import org.eclipse.jdt.core.dom.*;
import sharpfix.util.ConceptWordExtractor;
import sharpfix.util.ConceptTokenExtractor;
import sharpfix.global.*;

public class CCMatcherMain
{
    public static void main(String[] args) {
	String tfpath = args[0];
	String tloc = args[1];
	String rcfpath = args[2];
	String rcloc = args[3];
	String which_matcher = args[4];

	BuggyChunk tchunk = ChunkFactory.getBuggyChunk(tfpath, tloc);
	CandidateChunk rcchunk = ChunkFactory.getCandidateChunk(rcfpath, rcloc);
	List<ASTNode> tnodes = tchunk.getNodeList();
	List<ASTNode> rcnodes = rcchunk.getNodeList();

	CCMaps map = null;
	if ("1".equals(which_matcher)) {
	    CCMatcher1 ccmatcher = new CCMatcher1();
	    map = ccmatcher.match(tnodes, rcnodes, true);
	}
	System.out.println(map);
    }
}
