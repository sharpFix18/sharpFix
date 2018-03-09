package sharpfix.codesearch;

import java.util.List;
import java.util.ArrayList;

public class TokenCollection
{
    private List<String> tk_strs0;
    
    public TokenCollection(List<String> tk_strs0) {
	this.tk_strs0 = tk_strs0;
    }

    public List<String> getTokenStrings() { return tk_strs0; }
}
