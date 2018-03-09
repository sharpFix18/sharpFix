package sharpfix.util;

public class CodeToken
{
    String text;
    int property;

    public CodeToken(String text) {
	this.text = text;
	this.property = -1; //no property
    }

    public CodeToken(String text, int property) {
	this.text = text;
	this.property = property;
    }

    public String getText() { return text; }

    public void setText(String text) { this.text = text; }
    
    public int getProp() { return property; }

    public void setProp(int property) { this.property = property; }
}
