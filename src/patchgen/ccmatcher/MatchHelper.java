package sharpfix.patchgen.ccmatcher;

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;


public class MatchHelper<T>
{
    /**
       It is up to the compare function to determine the match order 
       given obj1 & obj2

       For some types, compare(obj1, obj2) is always the same as compare(obj2, obj1).
       There are types that it is not true.
     */

    public List<PairVal<T, T>> getAllPairsList(List<T> list1, List<T> list2,
					       CompareFunction<T> cf) {

        int size1 = list1.size();
	int size2 = list2.size();
        List<PairVal<T, T>> pvList = new ArrayList<PairVal<T, T>>();
	
        for (int i=0; i<size1; i++) {
            T obj1 = list1.get(i);
            for (int j=0; j<size2; j++) {
		T obj2 = list2.get(j);
		double score = cf.compare(obj1, obj2);
		PairVal<T, T> pv = new PairVal<T, T>(obj1, obj2, score);
		pvList.add(pv);
            }
        }
	
	return pvList;
    }


    public List<PairVal<T, T>> getAllPairsListWithNons(List<T> list1, List<T> list2, 
				       CompareFunction<T> cf, double non_match_score) {

	//Add pairs between list1,2
        int size1 = list1.size();
	int size2 = list2.size();
        List<PairVal<T, T>> pvList = new ArrayList<PairVal<T, T>>();
	
        for (int i=0; i<size1; i++) {
            T obj1 = list1.get(i);
            for (int j=0; j<size2; j++) {
                T obj2 = list2.get(j);
                double score = cf.compare(obj1, obj2);
		if (score > 0) {
		    PairVal<T, T> pv = new PairVal<T, T>(obj1, obj2, score);
		    pvList.add(pv);
		}
            }
        }
	
	//Add non-match pairs
	for (int i=0; i<size2; i++) {
	    T obj2 = list2.get(i);
	    PairVal<T, T> pv = new PairVal<T, T>(null, obj2, non_match_score);
	    pvList.add(pv);
	}
	
	return pvList;
    }

    public List<PairVal<T, T>> sortHighToLow(List<PairVal<T, T>> pvList) {

	Collections.sort(pvList, new Comparator<PairVal<T, T>>() {
		@Override public int compare(PairVal<T, T> pv1, PairVal<T, T> pv2) {
		    Double d1 = (Double) pv1.getValue();
		    Double d2 = (Double) pv2.getValue();
		    return d2.compareTo(d1);
		}
	    });
	
	return pvList;
    }

    public double getMatchScore(List<PairVal<T, T>> pvList) {
	//sum of log
	double sum = 0;
	for (PairVal<T, T> pv : pvList) sum += Math.log(pv.getValue());
	return sum;
    }

    public List<PairVal<T, T>> getMatchList(List<PairVal<T, T>> sortedList) { return null; }	

    public List<PairVal<T, T>> match(List<T> list1, List<T> list2,
				     CompareFunction<T> cf) { return null;}

    public List<PairVal<T, T>> getMatchListWithNons(List<T> list1, List<T> list2, 
		     CompareFunction<T> cf, double non_match_score) { return null;}

    public ElemVal[] getMatchLists(int match_size, int n, 
				   List<PairVal<T, T>> sorted_list) { return null; }

}
