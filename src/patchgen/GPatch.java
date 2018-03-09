package sharpfix.patchgen;

import sharpfix.global.Patch;

public class GPatch
{
    Patch patch;
    int used_candidate;

    public GPatch(Patch p, int u) {
	patch = p;
	used_candidate = u;
    }

    public Patch getPatch() { return patch; }

    public int getNumOfUsedCandidates() { return used_candidate; }
}
