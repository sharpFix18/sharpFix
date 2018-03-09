package sharpfix.codesearch;

import java.io.File;
import sharpfix.util.*;
import java.util.List;
import java.util.ArrayList;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class LocalCodeSearchMain
{
    public static void main(String[] args) {
	String qfpath = args[0];
	String qloc = args[1];
	String idxfpath = args[2]; //index file path
	int index_flag = Integer.parseInt(args[3]);
	int max = 500;

	File qf = new File(qfpath);
	File idxf = new File(idxfpath);
	//CodeSearch cs = new CodeSearch(idxf);
	LocalCodeSearch cs = new LocalCodeSearch();
	List<SearchItem> si_list = cs.search(qf, qloc, idxf, index_flag);
	int si_list_size = si_list.size();
	int size = (si_list_size <= max) ? si_list_size : max;
	for (int i=0; i<size; i++) {
	    SearchItem si = si_list.get(i);
	    String pathloc = si.getPathLoc();
	    float score = si.getScore();
	    System.out.println("--- Rank " + i + " ---");
	    System.out.println(pathloc+";"+score);
	    System.out.println(getContentString(pathloc));
	}
    }

    private static String getContentString(String pathloc) {
	int index0 = pathloc.indexOf(",");
	String fpath = pathloc.substring(0, index0);
	String loc = pathloc.substring(index0+1);
	File f = new File(fpath);
	ASTNode root = ASTNodeLoader.getASTNode(f);
	List<ASTNode> found_nodes = ASTNodeFinder.find((CompilationUnit) root, loc);
	return found_nodes.get(0).toString();
    }
}
