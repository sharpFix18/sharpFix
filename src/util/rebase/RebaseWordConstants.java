package sharpfix.util.rebase;



public interface RebaseWordConstants {


/********************************************************************************/
/*										*/
/*	Files									*/
/*										*/
/********************************************************************************/

String WORD_LIST_FILE = "words";



/********************************************************************************/
/*										*/
/*	Word options								*/
/*										*/
/********************************************************************************/

enum WordOptions {
   SPLIT_CAMELCASE,		// split camel case words
   SPLIT_UNDERSCORE,		// split words on underscores
   SPLIT_NUMBER,		// split words on numbers
   SPLIT_COMPOUND,		// split compound words
   STEM,			// do stemming
   VOWELLESS,			// add abbreviations from dropping vowels
}



/********************************************************************************/
/*										*/
/*	Computation options							*/
/*										*/
/********************************************************************************/

enum TermOptions {
   TERM_NORMAL, 		// df(wd) = f(wd)
   TERM_BOOLEAN,		// df(wd) = (f(wd) > 0 ? 1 : 0)
   TERM_LOG,			// df(wd) = log(1+f(wd))
   TERM_AUGMENTED,		// df(wd) = 0.5*f(wd)/MAX(f(wd))
   WORDS_ONLY,			// only suggest dictionary words
}



}	// end of interface RebaseWordConstants




/* end of RebaseWordConstants.java */
