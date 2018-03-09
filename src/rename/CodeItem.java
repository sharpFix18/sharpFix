package sharpfix.rename;

import java.util.List;
import java.util.ArrayList;

public class CodeItem
{
    String class_name;
    String method_name;
    List<String> param_names;
    List<String> param_type_names;
    List<String> method_call_names;
    List<String> type_names;
    List<String> var_names;

    public CodeItem() {
	class_name = null;
	method_name = null;
	param_names = new ArrayList<String>();
	param_type_names = new ArrayList<String>();
	method_call_names = new ArrayList<String>();
	type_names = new ArrayList<String>();
	var_names = new ArrayList<String>();
    }

    public CodeItem(String fcn, String mn, List<String> pns, List<String> ptns, List<String> mcns, List<String> tns, List<String> vns) {
	class_name = fcn;
	method_name = mn;
	param_names = pns;
	param_type_names = ptns;
	method_call_names = mcns;
	type_names = tns;
	var_names = vns;
    }
    
    public String getClassName() { return class_name; }

    public String getMethodName() { return method_name; }

    public List<String> getParameterNames() { return param_names; }
    
    public List<String> getParameterTypeNames() { return param_type_names; }
    
    public List<String> getMethodCallNames() { return method_call_names; }

    public List<String> getTypeNames() { return type_names; }

    public List<String> getVariableNames() { return var_names; }

    public boolean isValid() {
	return (class_name != null) && (method_name != null);
    }
}
