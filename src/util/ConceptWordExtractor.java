package sharpfix.util;

import java.util.List;
import java.util.ArrayList;
import sharpfix.util.rebase.*;

public class ConceptWordExtractor
{
    static RebaseWordStemmer stemmer;
    static { stemmer = new RebaseWordStemmer(); }

    public static List<String> extract(String cnts) {
	return extract(cnts, true, true);
    }
    
    public static List<String> extract(String cnts, boolean filter_stopwords, boolean filter_short_long_words) {
	List<String> token_list = new ArrayList<String>();
	int size = cnts.length();
	for (int i = 0; i < size; ++i) {
	    char ch = cnts.charAt(i);
	    if (Character.isJavaIdentifierPart(ch)) {
		boolean havealpha = Character.isAlphabetic(ch);
		int start = i;
		while (Character.isJavaIdentifierPart(ch)) {
		    havealpha |= Character.isAlphabetic(ch);
		    if (++i >= size) break;
		    ch = cnts.charAt(i);
		}
		//If the token has alpha, then it is a name
		if (havealpha) {
		    List<String> token_sublist = RebaseWordFactory.getCandidateWords(stemmer, cnts, start, i-start, filter_stopwords, filter_short_long_words);
		    if (token_sublist != null) {
			for (String token : token_sublist) {
			    token_list.add(token);
			}
		    }
		}
	    }
	}
	return token_list;
    }
}
