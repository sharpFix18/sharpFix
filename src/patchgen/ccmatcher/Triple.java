package sharpfix.patchgen.ccmatcher;

public class Triple<T1, T2, T3>
{
    T1 elem1;
    T2 elem2;
    T3 elem3;

    public Triple() {

	elem1 = null;
	elem2 = null;
	elem3 = null;
    }

    public Triple(T1 e1, T2 e2, T3 e3) {
	
	elem1 = e1;
	elem2 = e2;
	elem3 = e3;
    }

    public void setElem1(T1 e1) { elem1 = e1; }

    public T1 getElem1() { return elem1; }

    public void setElem2(T2 e2) { elem2 = e2; }

    public T2 getElem2() { return elem2; }

    public void setElem3(T3 e3) { elem3 = e3; }

    public T3 getElem3() { return elem3; }

}
