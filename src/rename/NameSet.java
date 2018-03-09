package sharpfix.rename;

import java.util.Set;
import java.util.HashSet;

public class NameSet
{
    Set<String> var_names;
    Set<String> type_names;
    Set<String> method_call_names;
    
    public NameSet() {
	var_names = new HashSet<String>();
	type_names = new HashSet<String>();
	method_call_names = new HashSet<String>();
    }

    public NameSet(Set<String> vnames, Set<String> tnames, Set<String> mcnames) {
	var_names = vnames;
	type_names = tnames;
	method_call_names = mcnames;
    }

    public boolean containsVarName(String vname) {
	return var_names.contains(vname);
    }

    public boolean containsTypeName(String tname) {
	return type_names.contains(tname);
    }

    public boolean containsMethodCallName(String mcname) {
	return method_call_names.contains(mcname);
    }

    public boolean containsName(String name) {
	return containsVarName(name) || containsTypeName(name) || containsMethodCallName(name);
    }

    public Set<String> getVariableNames() { return var_names; }

    public Set<String> getTypeNames() { return type_names; }

    public Set<String> getMethodCallNames() { return method_call_names; }
}
