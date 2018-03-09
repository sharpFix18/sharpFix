package sharpfix.patchgen.ccmatcher;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;



/*
  Create the best one-to-one mapping(s).
*/

public class BestMatchHelper<T> extends MatchHelper<T>
{
    /*
      Try matching any pairs whose associated score is greater than 0.
     */
    
    public List<PairVal<T, T>> getMatchList(List<PairVal<T, T>> sortedList) {

        List<PairVal<T, T>> rslt_list = new ArrayList<PairVal<T, T>>();
        Set<T> set1 = new HashSet<T>();
	Set<T> set2 = new HashSet<T>();
	
        for (PairVal<T, T> pv : sortedList) {
            T elem1 = pv.getElem1();
	    T elem2 = pv.getElem2();
            double score = pv.getValue();

            if (!set1.contains(elem1) && !set2.contains(elem2) && score > 0) {
                rslt_list.add(pv);
                if (elem1 != null) set1.add(elem1);
                if (elem2 != null) set2.add(elem2);
            }
        }

        return rslt_list;
    }


    public List<PairVal<T, T>> match(List<T> list1, List<T> list2,
					    CompareFunction<T> cf) {
	
        List<PairVal<T, T>> pvList = getAllPairsList(list1, list2, cf);
	pvList = sortHighToLow(pvList);
        List<PairVal<T, T>> matchList = getMatchList(pvList);
        return matchList;
    }



    public List<PairVal<T, T>> getMatchListWithNons(List<T> list1, List<T> list2, CompareFunction<T> cf, double non_match_score) {

        List<PairVal<T, T>> pvList = getAllPairsListWithNons(list1, list2, cf, non_match_score);
	pvList = sortHighToLow(pvList);
        List<PairVal<T, T>> matchList = getMatchList(pvList);
        return matchList;
    }
    
}
