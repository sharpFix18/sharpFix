package sharpfix.util.rebase;

/*
  Simplified Version of RebaseWordFactory by Qi Xin.
*/


//import edu.brown.cs.ivy.xml.*;

import java.io.*;
import java.util.*;


public class RebaseWordFactory implements RebaseWordConstants
{

private static EnumSet<WordOptions>	word_options;

private static RebaseWordFactory word_factory = null;

private static Set<String>		stop_words;
private static Map<String,String>	short_words;
private static Set<String>		dictionary_words;



static {
   word_options = EnumSet.allOf(WordOptions.class);
   setupWordSets();
}



public synchronized static RebaseWordFactory getFactory()
{
   if (word_factory == null) {
       word_factory = new RebaseWordFactory();
   }
   return word_factory;
}


public RebaseWordFactory()
{}



public static List<String> getCandidateWords(RebaseWordStemmer stm,String text,int off,int len,boolean filter_stopwords,boolean filter_short_long_words)
{
   if (filter_short_long_words && (len < 3 || len > 32)) return null;

   int [] breaks = new int[32];
   int breakct = 0;

   char prev = 0;
   for (int i = 0; i < len; ++i) {
      char ch = text.charAt(off+i);
      if (word_options.contains(WordOptions.SPLIT_CAMELCASE)) {
	 if (Character.isUpperCase(ch) && Character.isLowerCase(prev)) {
	    breaks[breakct++] = i;
	  }
       }
      if (word_options.contains(WordOptions.SPLIT_NUMBER)) {
	 if (Character.isDigit(ch) && !Character.isDigit(prev) && i > 0) {
	    breaks[breakct++] = i;
	  }
	 else if (Character.isDigit(prev) && !Character.isDigit(ch)) {
	    breaks[breakct++] = i;
	  }
       }
      if (word_options.contains(WordOptions.SPLIT_UNDERSCORE)) {
	 if (ch == '_') {
	    breaks[breakct++] = i;
	  }
       }
      prev = ch;
    }

   if (stm == null) stm = new RebaseWordStemmer();
   List<String> rslt = new ArrayList<String>();

   // first use whole word
   addCandidateWords(stm,text,off,len,rslt,filter_stopwords,filter_short_long_words);

   if (breakct > 0) {
      int lbrk = 0;
      for (int i = 0; i < breakct; ++i) {
	if (filter_short_long_words) {
	    if (breaks[i] - lbrk >= 3) {
		addCandidateWords(stm,text,off+lbrk,breaks[i]-lbrk,rslt,filter_stopwords,filter_short_long_words);
	    }
	}
	else {
	    addCandidateWords(stm,text,off+lbrk,breaks[i]-lbrk,rslt,filter_stopwords,filter_short_long_words);
	}
	lbrk = breaks[i];
      }
      addCandidateWords(stm,text,off+lbrk,len-lbrk,rslt,filter_stopwords,filter_short_long_words);
    }

   return rslt;
}


private static void addCandidateWords(RebaseWordStemmer stm,String text,int off,int len,List<String> rslt,boolean filter_stopwords,boolean filter_short_long_words)
{
   if (filter_short_long_words && (len < 3)) return;

   String wd1 = text.substring(off,off+len);
   String wd0 = wd1.toLowerCase();
   addCandidateWord(wd0,rslt,filter_stopwords,filter_short_long_words);

   String wd = wd0;
   if (word_options.contains(WordOptions.STEM)) {
      for (int i = 0; i < len; ++i) {
	 stm.add(text.charAt(off+i));
       }
      wd = stm.stem();	  // stem and convert to lower case
      if (dictionary_words.contains(wd) && !wd0.equals(wd)) {
	 // System.err.println("STEM " + wd0 + " => " + wd);
	 addCandidateWord(wd,rslt,filter_stopwords,filter_short_long_words);
       }
    }

   if (word_options.contains(WordOptions.SPLIT_COMPOUND)) {
      if (!dictionary_words.contains(wd0) && !dictionary_words.contains(wd) &&
	    wd0.equals(wd1)) {
	 for (int i = 3; i < len-3; ++i) {
	    String s1 = wd0.substring(0,i);
	    String s2 = wd0.substring(i);
	    if (dictionary_words.contains(s1) || short_words.containsKey(s1)) {
	       if (dictionary_words.contains(s2) || short_words.containsKey(s2)) {
		  if (!s1.equals(wd)) {
		     addCandidateWord(s1,rslt,filter_stopwords,filter_short_long_words);
		     addCandidateWord(s2,rslt,filter_stopwords,filter_short_long_words);
		   }
		}
	     }
	  }
       }
    }
}





private static void addCandidateWord(String wd,List<String> rslt,boolean filter_stopwords,boolean filter_short_long_words)
{
   if (filter_stopwords && stop_words.contains(wd)) return;
   if (filter_short_long_words && (wd.length() < 3 || wd.length() > 24)) return;

   rslt.add(wd);

   if (word_options.contains(WordOptions.VOWELLESS)) {
      String nwd = short_words.get(wd);
      if (nwd != null) rslt.add(nwd);
    }
}




/********************************************************************************/
/*										*/
/*	Create programmer abbreviations of common words 			*/
/*										*/
/********************************************************************************/

private static void setupWordSets()
{
   stop_words = new HashSet<String>();
   String wds = "a,able,about,across,after,all,almost,also,am,among,an,and,any,are,as,at," +
   "be,because,been,but,by,can,cannot,could,dear,did,do,does,either,else,ever,every," +
   "for,from,get,got,had,has,have,he,her,hers,him,his,how,however,i,if,in,into,is,it,its," +
   "just,least,let,like,likely,may,me,might,most,must,my,neither,no,nor,not," +
   "of,off,often,on,only,or,other,our,own,rather,said,say,says,she,should,since,so,some," +
   "than,that,the,their,them,then,there,these,they,this,tis,to,too,twas,us," +
   "wants,was,we,were,what,when,where,which,while,who,whom,why,will,with,would," +
   "yet,you,your";

   String keys = "abstract,break,boolean,byte,case,catch,char,class,const,continue," +
   "default,do,double,else,enum,extends,false,final,finally,float,for,goto,if," +
   "implements,import,instanceof,int,interface,long,native,new,null,package,private," +
   "protected,public,return,short,static,super,switch,synchronized,this,throw,throws," +
   "true,try,void,while,java,com,org,javax";

   for (StringTokenizer tok = new StringTokenizer(wds," ,"); tok.hasMoreTokens(); ) {
      stop_words.add(tok.nextToken());
    }
   for (StringTokenizer tok = new StringTokenizer(keys," ,"); tok.hasMoreTokens(); ) {
      stop_words.add(tok.nextToken());
    }

   dictionary_words = new HashSet<String>();
   short_words = new HashMap<String,String>();
   HashSet<String> fnd = new HashSet<String>();

   //String root = "/pro/bubbles";
   //File f1 = new File(root);
   //File f2 = new File(f1,"lib");
   File f = new File("/vol/cocker/"+WORD_LIST_FILE);

   try {
      BufferedReader br = new BufferedReader(new FileReader(f));
      for ( ; ; ) {
	 String wd = br.readLine();
	 if (wd == null) break;
	 if (wd.contains("'") || wd.contains("-")) continue;
	 if (wd.length() < 3 || wd.length() > 24) continue;
	 wd = wd.toLowerCase();
	 dictionary_words.add(wd);
	 String nwd = wd.replaceAll("[aeiou]","");
	 if (!nwd.equals(wd) && nwd.length() >= 3) {
	    if (fnd.contains(nwd)) {
	       short_words.remove(nwd);
	     }
	    else {
	       fnd.add(nwd);
	       short_words.put(nwd,wd);
	     }
	  }
       }
      br.close();
    }
   catch (IOException e) {
      System.err.println("Problem reading word file: " + e);
    }
}



}


