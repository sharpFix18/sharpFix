package sharpfix.patchgen.ccmatcher;

public class ElemVal<T>
{
    T elem;
    double val;

    public ElemVal() {

	elem = null;
	val = -1;
    }

    public ElemVal(T e, double v) {

	elem = e;
	val = v;
    }

    public void setElem(T e) { elem = e; }

    public T getElem() { return elem; }

    public void setValue(double v) { val = v; }

    public double getValue() { return val; }
}
