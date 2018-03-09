package sharpfix.patchgen.ccmatcher;

public interface CompareFunction<T> {
    public double compare(T elem1, T elem2);
}
