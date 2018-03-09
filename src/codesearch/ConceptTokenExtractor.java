package sharpfix.codesearch;

import java.util.List;
import java.util.ArrayList;
import org.eclipse.jdt.core.dom.ASTNode;

public class ConceptTokenExtractor
{
    private static CodeTokenGenerator ctgen;
    static { ctgen = new CodeTokenGenerator(); }
    
    public static List<String> extract(ASTNode node) {
	List<String> cpt_tk_list = new ArrayList<String>();
	if (node == null) { return cpt_tk_list; }
	List<CodeToken> code_tk_list = ctgen.getCTs(node, -1);
	for (CodeToken code_tk : code_tk_list) {
	    String code_tk_text = code_tk.getText();
	    List<String> cw_list = ConceptWordExtractor.extract(code_tk_text, false, false); //do not filter stop, short, or long words
	    if (cw_list.isEmpty() || (cw_list.size()==1 && "".equals(cw_list.get(0)))) {
		//code_tk_text is an non-Java-identifier, non-number symbol
		cpt_tk_list.add(code_tk_text);
	    }
	    else {
		for (String cw : cw_list) {
		    cpt_tk_list.add(cw);
		}
	    }
	}
	return cpt_tk_list;
    }
}
