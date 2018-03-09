package sharpfix.patchgen.ccmatcher;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.eclipse.jdt.core.dom.ASTNode;
import sharpfix.util.ASTNodeStringRegularizer;


public class MatchNodeValues
{
    private static ASTNodeStringRegularizer node_reg = new ASTNodeStringRegularizer();
    

    public static double match(ASTNode node1, ASTNode node2, int k) {

	if (node1 == null || node2 == null) { return -1; }

	//Get the value strings (connected by all the non-white-space characters)
	String node1_ptn = node_reg.getPattern(node1);
	String node2_ptn = node_reg.getPattern(node2);
	StringBuilder sb1 = new StringBuilder();
	StringBuilder sb2 = new StringBuilder();
	String[] node1_reg_strs = node1_ptn.split(" ");
	String[] node2_reg_strs = node2_ptn.split(" ");
	for (String node1_reg_str : node1_reg_strs) { sb1.append(node1_reg_str); }
	for (String node2_reg_str : node2_reg_strs) { sb2.append(node2_reg_str); }

	String node1_val_str = sb1.toString();
	String node2_val_str = sb2.toString();
	Map<String, Integer> kgram_map1 = getKGramMap(node1_val_str, k);
	Map<String, Integer> kgram_map2 = getKGramMap(node2_val_str, k);

	//Count the kgram intersections & unions
	int inter_val = 0, union_val = 0;
	for (Map.Entry<String, Integer> entry1 : kgram_map1.entrySet()) {
	    int count1 = entry1.getValue().intValue();
	    union_val += count1;
	}
	for (Map.Entry<String, Integer> entry2 : kgram_map2.entrySet()) {
	    int count2 = entry2.getValue().intValue();
	    union_val += count2;
	    Integer count1_obj = kgram_map1.get(entry2.getKey());
	    if (count1_obj != null) {
		int count1 = count1_obj.intValue();
		inter_val += (count1 <= count2) ? count1 : count2;
	    }
	}

	//Use the dice coefficient
	return (2.0 * inter_val) / union_val;
    }


    private static Map<String, Integer> getKGramMap(String val_str, int k) {

	Map<String, Integer> kgram_map = new HashMap<String, Integer>();
	int length = val_str.length();
	if (length < k) { kgram_map.put(val_str, 1); }
	else {
	    for (int i=0; i<=length-k; i++) {
		String kgram = val_str.substring(i, i+k);
		Integer count_obj = kgram_map.get(kgram);
		if (count_obj == null) { kgram_map.put(kgram, 1); }
		else { kgram_map.put(kgram, count_obj.intValue()+1); }
	    }
	}
	return kgram_map;
    }
}
