package sharpfix.rename;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Comparator;
import org.eclipse.jdt.core.dom.ASTNode;
import edu.brown.cs.ivy.jcomp.JcompAst;
import edu.brown.cs.ivy.jcomp.JcompSymbol;
import edu.brown.cs.ivy.jcomp.JcompType;
import org.apache.lucene.search.spell.LevensteinDistance;

public class NameMap0
{
    Map<JcompSymbol, List<Pair>> map0;
    Map<JcompType, List<Pair>> map1;
    Map<String, List<Pair>> map2;
    
    private static LevensteinDistance ld;

    static {
	ld = new LevensteinDistance();
    }

    public NameMap0() {
	map0 = new HashMap<JcompSymbol, List<Pair>>();
	map1 = new HashMap<JcompType, List<Pair>>();
	map2 = new HashMap<String, List<Pair>>();
    }

    public void add(JcompSymbol jcsymbol, String mapped_name) {
	List<Pair> plist = map0.get(jcsymbol);
	if (plist == null) {
	    plist = new ArrayList<Pair>();
	    map0.put(jcsymbol, plist);
	}
	boolean found = false;
	for (Pair p : plist) {
	    if (mapped_name.equals(p.getMappedName())) {
		p.addCountByOne();
		found = true;
		break;
	    }
	}

	if (!found) {
	    Pair p = new Pair(mapped_name, 1);
	    plist.add(p);
	}
    }

    public void add(JcompType jctype, String mapped_name) {
	List<Pair> plist = map1.get(jctype);
	if (plist == null) {
	    plist = new ArrayList<Pair>();
	    map1.put(jctype, plist);
	}
	boolean found = false;
	for (Pair p : plist) {
	    if (mapped_name.equals(p.getMappedName())) {
		p.addCountByOne();
		found = true;
		break;
	    }
	}

	if (!found) {
	    Pair p = new Pair(mapped_name, 1);
	    plist.add(p);
	}
    }


    public void add(String s, String mapped_name) {
	List<Pair> plist = map2.get(s);
	if (plist == null) {
	    plist = new ArrayList<Pair>();
	    map2.put(s, plist);
	}
	boolean found = false;
	for (Pair p : plist) {
	    if (mapped_name.equals(p.getMappedName())) {
		p.addCountByOne();
		found = true;
		break;
	    }
	}

	if (!found) {
	    Pair p = new Pair(mapped_name, 1);
	    plist.add(p);
	}
    }

    public NameMap getNameMap() {

	NameMap name_map = new NameMap();
	for (Map.Entry<JcompSymbol, List<Pair>> entry : map0.entrySet()) {
	    JcompSymbol key = entry.getKey();
	    List<Pair> plist = entry.getValue();
	    if (key != null) {
		String cname = getSimpleName(key.getName());
		String best_mapped_name = getBestMappedName(cname, plist);
		name_map.add(key, best_mapped_name);
	    }
	}

	for (Map.Entry<JcompType, List<Pair>> entry : map1.entrySet()) {
	    JcompType key = entry.getKey();
	    List<Pair> plist = entry.getValue();
	    if (key != null) {
		String cname = getSimpleName(key.getName());
		String best_mapped_name = getBestMappedName(cname, plist);
		name_map.add(key, best_mapped_name);
	    }
	}

	for (Map.Entry<String, List<Pair>> entry : map2.entrySet()) {
	    String key = entry.getKey();
	    List<Pair> plist = entry.getValue();
	    if (key != null) {
		String cname = getSimpleName(key);
		String best_mapped_name = getBestMappedName(cname, plist);
		name_map.add(key, best_mapped_name);
	    }
	}

	return name_map;
    }

    private String getBestMappedName(String cname, List<Pair> plist) {

	plist = getSortedPairList(plist);

	//Get the bnames with the maximum counts
	int plist_size = plist.size();
	int max_count = plist.get(0).getCount();
	List<String> bnames = new ArrayList<String>();
	for (int i=0; i<plist_size; i++) {
	    Pair pi = plist.get(i);
	    if (pi.getCount() == max_count) {
		bnames.add(pi.getMappedName());
	    }
	    else {
		break;
	    }
	}

	//Select bname that is the most similar to cname
	float best_sim = -1;
	String best_bname = null;
	int bnames_size = bnames.size();
	for (int i=0; i<bnames_size; i++) {
	    String bname = bnames.get(i);
	    float sim = ld.getDistance(bname, cname);
	    if (sim > best_sim) {
		best_sim = sim;
		best_bname = bname;
	    }
	}

	return (best_bname == null) ? cname : best_bname;
	

	/*
	int best_index = -1;
	int max_count = -1;
	int plist_size = plist.size();
	for (int i=0; i<plist_size; i++) {
	    int count = plist.get(i).getCount();
	    if (count > max_count) {
		max_count = count;
		best_index = i;
	    }
	}
	return plist.get(best_index).getMappedName();
	*/
    }
    
    private List<Pair> getSortedPairList(List<Pair> plist) {
	Collections.sort(plist, new Comparator<Pair>() {
		@Override public int compare(Pair p1, Pair p2) {
		    Integer i1 = new Integer(p1.getCount());
		    Integer i2 = new Integer(p2.getCount());
		    return i2.compareTo(i1);
		}
	    });
	return plist;
    }
    
    
    private String getSimpleName(String name) {
	if (name == null) {
	    return null;
	}
	else {
	    int lastDotIndex = name.lastIndexOf(".");
	    if (lastDotIndex == -1) {
		return name;
	    }
	    else {
		return name.substring(lastDotIndex+1);
	    }
	}
    }
    
    private class Pair
    {
	String mapped_name;
	int count;

	public Pair(String s, int c) {
	    mapped_name = s;
	    count = c;
	}

	public void addCountByOne() { count += 1; }

	public String getMappedName() { return mapped_name; }

	public int getCount() { return count; }
    }
}
