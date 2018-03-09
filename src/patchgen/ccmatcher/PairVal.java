package sharpfix.patchgen.ccmatcher;

public class PairVal<T1, T2>
{
    T1 elem1;
    T2 elem2;
    double val;

    public PairVal() {

	elem1 = null;
	elem2 = null;
	val = -1;
    }

    public PairVal(T1 e1, T2 e2, double v) {
	
	elem1 = e1;
	elem2 = e2;
	val = v;
    }

    public void setElem1(T1 e1) { elem1 = e1; }

    public T1 getElem1() { return elem1; }

    public void setElem2(T2 e2) { elem2 = e2; }

    public T2 getElem2() { return elem2; }

    public void setValue(double v) { val = v; }

    public double getValue() { return val; }
}
