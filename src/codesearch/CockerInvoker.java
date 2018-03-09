package sharpfix.codesearch;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;
import edu.brown.cs.cocker.application.ApplicationChunkQuery;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.ASTNode;

public class CockerInvoker
{
    public List<String> invoke(String bfpath, String search_loc, String anal_method, String cocker_rslt_fpath, long time_budget) {
	//Invoke cocker
	List<String> cocker_arg_list = new ArrayList<String>();
	cocker_arg_list.add("-a");
	cocker_arg_list.add(anal_method);
	cocker_arg_list.add("-data");
	cocker_arg_list.add(search_loc);
	cocker_arg_list.add("-s");
	cocker_arg_list.add("even_weighted");
	cocker_arg_list.add(bfpath);
	String[] cocker_args = cocker_arg_list.toArray(new String[0]);

	System.out.println("*** Cocker Search Arguments ***");
	for (String cocker_arg : cocker_args) {
	    System.out.print(cocker_arg + " ");
	}
	System.out.println();
	
	boolean cocker_ok = true;
	CockerRunner cocker_runner = new CockerRunner(cocker_args);
	ExecutorService executor = Executors.newSingleThreadExecutor();
	Future<String> future = executor.submit(cocker_runner);
	executor.shutdown();
	String search_rslt0 = null;
	try { search_rslt0 = future.get(time_budget, TimeUnit.MINUTES); }
	catch (Exception e) { System.err.println(e); e.printStackTrace(); }
	if (!executor.isTerminated()) { executor.shutdownNow(); }
	if (search_rslt0 == null) {
	    System.out.println("Cocker Failed.");
	    return new ArrayList<String>();
	}
	else {
	    System.out.println("Cocker Finished");
	}
	search_rslt0 = search_rslt0.trim();
	if (search_rslt0.equals("")) {
	    System.err.println("No cocker result available.");
	    return new ArrayList<String>();
	}

	if (cocker_rslt_fpath != null) {
	    System.err.println("Writing Cocker Result ...");
	    File cocker_rslt_f = new File(cocker_rslt_fpath);
	    try { FileUtils.writeStringToFile(cocker_rslt_f, search_rslt0); }
	    catch (Throwable t) { System.err.println(t); t.printStackTrace(); }
	    System.err.println("Done Writing Cocker Result.");
	}

	return Arrays.asList(search_rslt0.split("\n"));
    }

    private class CockerRunner implements Callable<String> {
	private String[] cocker_args;
	public CockerRunner(String[] args) { cocker_args = args; }
	@Override public String call() {
	    ApplicationChunkQuery chunker = new ApplicationChunkQuery(cocker_args);
	    return chunker.execute();
	}
    }
}
