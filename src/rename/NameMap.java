package sharpfix.rename;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import org.eclipse.jdt.core.dom.ASTNode;
import edu.brown.cs.ivy.jcomp.JcompAst;
import edu.brown.cs.ivy.jcomp.JcompSymbol;
import edu.brown.cs.ivy.jcomp.JcompType;

public class NameMap
{
    Map<JcompSymbol, String> map0;
    Map<JcompType, String> map1;
    Map<String, String> map2;

    public NameMap() {
	map0 = new HashMap<JcompSymbol, String>();
	map1 = new HashMap<JcompType, String>();
	map2 = new HashMap<String, String>();
    }

    public void add(JcompSymbol jcsymbol, String mapped_name) {
	map0.put(jcsymbol, mapped_name);
    
    }

    public void add(JcompType jctype, String mapped_name) {
	map1.put(jctype, mapped_name);
    }

    public void add(String s, String mapped_name) {
	map2.put(s, mapped_name);
    }

    public String getTypeName(JcompType jctype) {
	return map1.get(jctype);
    }

    public String getTypeName(String str) {
	return map2.get(str);
    }

    public String getSymbolName(JcompSymbol jcsymbol) {
	return map0.get(jcsymbol);
    }

    public String getSymbolName(String str) {
	return map2.get(str);
    }

    public boolean containsKeyAsJcompSymbol(JcompSymbol js) {
	return map0.containsKey(js);
    }

    public boolean containsKeyAsJcompType(JcompType jt) {
	return map1.containsKey(jt);
    }

    public boolean containsKeyAsString(String s) {
	return map2.containsKey(s);
    }

    public Set<String> getKeys() {
	Set<String> keys = new HashSet<String>();
	Set<JcompSymbol> map0_keys = map0.keySet();
	for (JcompSymbol map0_key : map0_keys) { keys.add(map0_key.getName()); }
	Set<JcompType> map1_keys = map1.keySet();
	for (JcompType map1_key : map1_keys) { keys.add(map1_key.getName()); }
	Set<String> map2_keys = map2.keySet();
	for (String map2_key : map2_keys) { keys.add(map2_key); }
	return keys;
    }
    
    public List<String> getValues() {
	List<String> values = new ArrayList<String>();
	Collection<String> values0 = map0.values();
	for (String value : values0) { values.add(value); }
	Collection<String> values1 = map1.values();
	for (String value : values1) { values.add(value); }
	Collection<String> values2 = map2.values();
	for (String value : values2) { values.add(value); }
	return values;
    }
    
    public String toString() {

	StringBuilder sb = new StringBuilder();
	sb.append("---------- Name Mapping ----------"); sb.append("\n");
	for (Map.Entry<JcompSymbol, String> entry : map0.entrySet()) {
	    sb.append(entry.getKey() + ", " + entry.getValue());
	    sb.append("\n");
	}
	for (Map.Entry<JcompType, String> entry : map1.entrySet()) {
	    sb.append(entry.getKey() + ", " + entry.getValue());
	    sb.append("\n");
	}
	for (Map.Entry<String, String> entry : map2.entrySet()) {
	    sb.append(entry.getKey() + ", " + entry.getValue());
	    sb.append("\n");
	}
	return sb.toString();
    }
}
