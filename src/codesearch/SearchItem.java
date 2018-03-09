package sharpfix.codesearch;

public class SearchItem
{
    String fpathloc;
    float score;

    public SearchItem(String fpl, float s) {
	fpathloc = fpl;
	score = s;
    }

    public String getPathLoc() { return fpathloc; }

    public float getScore() { return score; }
}
