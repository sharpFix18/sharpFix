package sharpfix.codesearch;

import java.io.File;

public class IndexMain
{
    public static void main(String[] args) {
	String src_path = args[0]; //Might be a directory or a file
	String index_fpath = args[1]; //The index file to be created
	int index_flag = Integer.parseInt(args[2]); //0: Code-Component-Based (without spaces), 1: Code-Token-Based (with word splitting)
	File src = new File(src_path);
	File index_f = new File(index_fpath);
	Indexer indexer = new Indexer(index_flag);
	indexer.index(src, index_f);
    }
}
