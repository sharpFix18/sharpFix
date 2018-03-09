package sharpfix.global;

public class Patch
{
    String fpath;
    boolean is_correct;
    int tested_num;
    String modtype;
    float heightsize;
    float strsim;

    public Patch(String fpath, boolean is_correct) {
	this.fpath = fpath;
	this.is_correct = is_correct;
	this.tested_num = 0;
	modtype = null;
	heightsize = -1;
	strsim = -1;
    }

    public Patch(String fpath, boolean is_correct, int tested_num) {
	this.fpath = fpath;
	this.is_correct = is_correct;
	this.tested_num = tested_num;
	modtype = null;
	heightsize = -1;
	strsim = -1;
    }

    public String getFilePath() { return fpath; }

    public boolean isCorrect() { return is_correct; }

    public void setTestedNum(int tn) { tested_num = tn; }

    public int getTestedNum() { return tested_num; }

    public void setModificationType(String mtype) { modtype = mtype; }

    public String getModificationType() { return modtype; }

    public void setHeightSize(float hsize) { heightsize = hsize; }

    public float getHeightSize() { return heightsize; }

    public void setStringSimilarity(float ssim) { strsim = ssim; }

    public float getStringSimilarity() { return strsim; }
}
