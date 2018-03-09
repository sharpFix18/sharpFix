package sharpfix.rename;

import sharpfix.util.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Collections;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.ASTVisitor;
import edu.brown.cs.ivy.jcomp.JcompAst;
import edu.brown.cs.ivy.jcomp.JcompSymbol;
import edu.brown.cs.ivy.jcomp.JcompType;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.jface.text.Document;
import org.apache.commons.io.FileUtils;


public class Renamer
{
    private CodeTokenGenerator ctgen;
    private CodeItemExtractor cie;
    private int short_length;

    public Renamer() {
	ctgen = new CodeTokenGenerator();
	cie = new CodeItemExtractor();
	short_length = 2;
    }
    
    public List<String> rename(RenameChunk bchunk, RenameChunk cchunk) {
	return rename(bchunk, cchunk, 0, false);
    }

    public List<String> rename(RenameChunk bchunk, RenameChunk cchunk, int flag) {
	return rename(bchunk, cchunk, flag, false);
    }    

    /* flag 0: ssFix's renamer
       flag 1: renaming in stages */
    public List<String> rename(RenameChunk bchunk, RenameChunk cchunk, int flag, boolean print_map) {
	List<String> renamed_can_ctnts = new ArrayList<String>();

	//Create name map
	NameMap namemap = null;
	if (flag == 0) { namemap = mapNames0(bchunk, cchunk); }
	else { namemap = mapNames1(bchunk, cchunk); }

	if (print_map) { System.out.println(namemap); }
	Document cdoc = new Document(cchunk.getFileContent());
	List<ASTNode> cnodes = cchunk.getNodeList();
	ASTRewrite rw = ASTRewrite.create(cnodes.get(0).getAST());
	RenameVisitor rv = new RenameVisitor(rw, namemap);
	for (ASTNode cnode : cnodes) { cnode.accept(rv); }
	try { rw.rewriteAST(cdoc, null).apply(cdoc); }
	catch (Throwable t) {
	    System.err.println("Text Edit Apply Error: " + t);
	    t.printStackTrace(); }
	renamed_can_ctnts.add(cdoc.get());

	return renamed_can_ctnts;
    }

    /* This is ssFix's renaming method */
    public NameMap mapNames0(RenameChunk bchunk, RenameChunk cchunk) {

	NameMap0 name_map0 = new NameMap0();
	
	//First get all components (collected in pre-order) from a chunk
	List<ASTNode> bnodes = bchunk.getNodeList();
	List<ASTNode> cnodes = cchunk.getNodeList();
	List<ASTNode> bcpt_list = new ArrayList<ASTNode>();
	List<ASTNode> ccpt_list = new ArrayList<ASTNode>();
	for (ASTNode bnode : bnodes) {
	    List<ASTNode> bcpt_list0 = ComponentGetter.getComponents(bnode);
	    for (ASTNode bcpt : bcpt_list0) { bcpt_list.add(bcpt); }
	}
	for (ASTNode cnode : cnodes) {
	    List<ASTNode> ccpt_list0 = ComponentGetter.getComponents(cnode);
	    for (ASTNode ccpt : ccpt_list0) { ccpt_list.add(ccpt); }
	}

	//Match the component patterns
	int bcpt_list_size = bcpt_list.size();
	int ccpt_list_size = ccpt_list.size();
	Set<ASTNode> matched_bcpts = new HashSet<ASTNode>();
	Set<ASTNode> matched_ccpts = new HashSet<ASTNode>();
	for (int i=0; i<bcpt_list_size; i++) {
	    ASTNode bcpt = bcpt_list.get(i);
	    List<CodeToken> btokens = ctgen.getCTs(bcpt);
	    List<String> btoken_ptns = new ArrayList<String>();
	    int btokens_length = btokens.size();
	    for (CodeToken btoken : btokens) {
		btoken_ptns.add(btoken.getPatternString());
	    }
	    
	    for (int j=0; j<ccpt_list_size; j++) {
		ASTNode ccpt = ccpt_list.get(j);
		if (matched_ccpts.contains(ccpt)) {
		    continue; //ccpt is already matched
		}
		List<CodeToken> ctokens = ctgen.getCTs(ccpt);
		int ctokens_length = ctokens.size();
		if (btokens_length != ctokens_length) {
		    //Component Pattern NOT match!
		    continue;
		}

		List<String> ctoken_ptns = new ArrayList<String>();
		for(CodeToken ctoken : ctokens) {
		    ctoken_ptns.add(ctoken.getPatternString());
		}

		boolean ptns_equal = true;
		for (int k=0; k<btokens_length; k++) {
		    if (!btoken_ptns.get(k).equals(ctoken_ptns.get(k))) {
			ptns_equal = false;
			break;
		    }
		}
		if (ptns_equal) {
		    matched_bcpts.add(bcpt);
		    matched_ccpts.add(ccpt);

		    //Add mapped names (btokens_length==ctokens_length is true!)
		    for (int k=0; k<btokens_length; k++) {
			String ctoken_ptn = ctoken_ptns.get(k);
			if (("$v$".equals(ctoken_ptn)) || ("$m$".equals(ctoken_ptn))) {
			    
			    //Ignore all-caps tokens
			    if (isAllUpperCase(ctokens.get(k).getString())) { continue; }
			    
			    ASTNode cnode = ctokens.get(k).getNode();
			    JcompSymbol jcsymbol = JcompAst.getReference(cnode);
			    String bname = btokens.get(k).getString();
			    if (jcsymbol != null) {
				name_map0.add(jcsymbol, bname);
			    }
			    else {
				name_map0.add(ctokens.get(k).getString(), bname);
			    }
			}
			else if ("$t$".equals(ctoken_ptn)) {
			    ASTNode cnode = ctokens.get(k).getNode();
			    JcompType jctype = JcompAst.getJavaType(cnode);
			    String bname = btokens.get(k).getString();
			    if (jctype != null) {
				name_map0.add(jctype, bname);
			    }
			    else {
				name_map0.add(ctokens.get(k).getString(), bname);
			    }
			}
		    }
		    
		    break;
		}
		
	    }
	}

	return name_map0.getNameMap(); //Get a mapped name with the max count
	
    }    

    public NameMap mapNames1(RenameChunk bchunk, RenameChunk cchunk) {

	CodeItem bci = cie.extract(bchunk.getNodeList());
	CodeItem cci = cie.extract(cchunk.getNodeList());

	//Fill bvar_names, btype_names, & bmc_names with names from bci.
	Set<String> bvar_names = new HashSet<String>();
	Set<String> btype_names = new HashSet<String>();
	Set<String> bmc_names = new HashSet<String>();
	Set<String> bclassm_names = new HashSet<String>(); //This set contains all the declared method names from bchunk's enclosing class
	List<String> bvar_name_list = bci.getVariableNames();
	List<String> btype_name_list = bci.getTypeNames();
	List<String> bmc_name_list = bci.getMethodCallNames();
	for (String bvar_name : bvar_name_list) { bvar_names.add(bvar_name); }
	for (String btype_name : btype_name_list) { btype_names.add(btype_name); }
	for (String bmc_name : bmc_name_list) { bmc_names.add(bmc_name); }

	String bc_name = bci.getClassName();
	String bm_name = bci.getMethodName();
	bclassm_names.add(bm_name); //Add bchunk's method name

	//Get the enclosing class & method for bchunk
	ASTNode bnode0 = bchunk.getNodeList().get(0);
	AbstractTypeDeclaration batd = null;
	MethodDeclaration bm = null;
	ASTNode bcurrnode = bnode0;
	while (bcurrnode != null) {
	    if (batd != null && bm != null) { break; }
	    if (bcurrnode instanceof TypeDeclaration) {
		if (batd == null) { batd = (AbstractTypeDeclaration) bcurrnode; }
	    }
	    if (bcurrnode instanceof MethodDeclaration) {
		if (bm == null) { bm = (MethodDeclaration) bcurrnode; }
	    }
	    bcurrnode = bcurrnode.getParent();
	}

	//Fill bvar_names & bmc_names with class fields & class declared methods
	//(Note: bmc_names doesn't contain bchunk's enclosing method.)
	//Fill bclassm_names with class declared methods
	if (batd != null) {
	    TypeDeclaration bnode_td = (TypeDeclaration) batd;
	    FieldDeclaration[] fds = bnode_td.getFields();
	    MethodDeclaration[] mds = bnode_td.getMethods();
	    for (FieldDeclaration fd : fds) {
		List frag_objs = fd.fragments();
		for (Object frag_obj : frag_objs) {
		    String fdname = ((VariableDeclarationFragment) frag_obj).getName().getIdentifier();
		    bvar_names.add(fdname);
		}
	    }
	    for (MethodDeclaration md : mds) {
		String mdname = md.getName().getIdentifier();
		if (!mdname.equals(bm_name)) { //Ignore the enclosing method
		    bmc_names.add(mdname);
		    bclassm_names.add(mdname);
		}
	    }
	}

	//Fill bvar_names, btype_names, bmc_names with names from bchunk's method
	List<ASTNode> bm_list = new ArrayList<ASTNode>();
	bm_list.add(bm);
	CodeItem bmci = cie.extract(bm_list);
	List<String> bmvar_name_list = bmci.getVariableNames();
	List<String> bmtype_name_list = bmci.getTypeNames();
	List<String> bmmc_name_list = bmci.getMethodCallNames();
	for (String bmvar_name : bmvar_name_list) { bvar_names.add(bmvar_name); }
	for (String bmtype_name : bmtype_name_list) { btype_names.add(bmtype_name); }
	for (String bmmc_name : bmmc_name_list) { bmc_names.add(bmmc_name); }

	//===============
	/*
	System.err.println("BMC NAMES:");
	for (String bmc_name : bmc_names) { System.err.print(bmc_name+" "); }
	System.err.println();
	*/
	//===============
	
	//STEP 1:
	//Map not-too-short names shared in bchunk (extended) & cchunk
	List<String> cvar_names = cci.getVariableNames();
	List<String> ctype_names = cci.getTypeNames();
	List<String> cmc_names = cci.getMethodCallNames();
	String cc_name = cci.getClassName();
	String cm_name = cci.getMethodName();
	
	Set<String> mapped_vnames = new HashSet<String>();
	Set<String> mapped_tnames = new HashSet<String>();
	Set<String> mapped_mcnames = new HashSet<String>();
	
	for (String cvar_name : cvar_names) {
	    if (cvar_name.length() <= short_length) { continue; }
	    if (bvar_names.contains(cvar_name)) { mapped_vnames.add(cvar_name); }
	}
	for (String ctype_name : ctype_names) {
	    if (ctype_name.length() <= short_length) { continue; }
	    if (btype_names.contains(ctype_name)) { mapped_tnames.add(ctype_name); }
	}
	//================
	/*
	System.out.println("CMC Names:");
	for (String cmc_name : cmc_names) {
	    System.out.print(cmc_name + " ");
	}
	System.out.println();

	System.out.println("BMC Names:");
	for (String bmc_name : bmc_names) {
	    System.out.print(bmc_name + " ");
	}
	System.out.println();
	*/
	//================
	
	for (String cmc_name : cmc_names) {
	    if (cmc_name.length() <= short_length) { continue; }
	    if (bmc_names.contains(cmc_name)) { mapped_mcnames.add(cmc_name); }
	}

	//===============
	/*
	System.out.println("Identical Mapped Names:");
	for (String mapped_vname : mapped_vnames) { System.out.print(mapped_vname + " "); }
	for (String mapped_tname : mapped_tnames) { System.out.print(mapped_tname + " "); }
	for (String mapped_mcname : mapped_mcnames) { System.out.print(mapped_mcname + " "); }
	System.out.println();
	*/
	//===============
	
	//STEP 2:
	//Map names based on usage context patterns
	NameSet mapped_ns = new NameSet(mapped_vnames, mapped_tnames, mapped_mcnames);
	NameMap name_map = mapNames1Helper(bchunk, cchunk, bc_name, bm_name, cc_name, cm_name, mapped_ns);

	//===============
	//System.out.println("Name mapping created after context matching:");
	//System.out.println(name_map);
	//===============	

	//STEP 3:
	//Map enclosing class & method names
	AbstractTypeDeclaration catd = null;
	MethodDeclaration cmd = null;
	ASTNode ccurrnode = cchunk.getNodeList().get(0);
	while (ccurrnode != null) {
	    if (ccurrnode instanceof AbstractTypeDeclaration) {
		if (catd == null) { catd = (AbstractTypeDeclaration) ccurrnode; }
	    }
	    else if (ccurrnode instanceof MethodDeclaration) {
		if (cmd == null) { cmd = (MethodDeclaration) ccurrnode; }
	    }
	    ccurrnode = ccurrnode.getParent();
	}
	if (catd != null) {
	    SimpleName catd_sn = catd.getName();
	    String catd_name = catd_sn.getIdentifier();
	    if (catd_name.equals(cc_name)) { //Should be consistent
		JcompType jctype = JcompAst.getJavaType(catd_sn);
		//if (jctype != null) { name_map.add(jctype, bc_name); }
		//else { name_map.add(catd_name, bc_name); }
		if (jctype != null) { addToMap(name_map, jctype, bc_name); }
		else { addToMap(name_map, catd_name, bc_name); }
	    }
	}
	if (cmd != null && !cmd.isConstructor()) {
	    SimpleName cmd_sn = cmd.getName();
	    String cmd_name = cmd_sn.getIdentifier();
	    if (cmd_name.equals(cm_name)) { //Should be consistent
		if (!bclassm_names.contains(cmd_name)) { //If cmd_name is equal to any declared method's name from bchunk's class, then we do not change cmd_name
		    JcompSymbol jcsymbol = JcompAst.getReference(cmd_sn);
		    //if (jcsymbol != null) { name_map.add(jcsymbol, bm_name); }
		    //else { name_map.add(cmd_name, bm_name); }
		    if (jcsymbol != null) { addToMap(name_map, jcsymbol, bm_name); }
		    else { addToMap(name_map, cmd_name, bm_name); }
		}
		else {
		    mapped_ns.getMethodCallNames().add(cmd_name);
		}
	    }
	}

	//STEP 4: 
	//Update the name set with newly mapped names
	List<SimpleName> unmapped_csn_list = new ArrayList<SimpleName>();
	List<ASTNode> cnode_list = cchunk.getNodeList();
	for (ASTNode cnode : cnode_list) {
	    UnmappedCNameCollector unc = new UnmappedCNameCollector(mapped_ns, name_map);
	    cnode.accept(unc);
	    List<SimpleName> unmapped_csn_list0 = unc.getSimpleNameList();
	    for (SimpleName unmapped_csn : unmapped_csn_list0) {
		unmapped_csn_list.add(unmapped_csn);
	    }
	}
	//Consider all unmapped names from the bchunk's enclosing method
	List<SimpleName> unmapped_bsn_list = new ArrayList<SimpleName>();
	//List<ASTNode> bnode_list = bchunk.getNodeList();
	List<ASTNode> bnode_list = new ArrayList<ASTNode>();
	List bm_stmts = bm.getBody().statements();
	for (Object bm_stmt : bm_stmts) { bnode_list.add((ASTNode) bm_stmt); }
	for (ASTNode bnode : bnode_list) {
	    UnmappedBNameCollector unb = new UnmappedBNameCollector(mapped_ns, name_map);
	    bnode.accept(unb);
	    List<SimpleName> unmapped_bsn_list0 = unb.getSimpleNameList();
	    for (SimpleName unmapped_bsn : unmapped_bsn_list0) {
		unmapped_bsn_list.add(unmapped_bsn);
	    }
	}
	//Need to consider unmapped fields & methods
	if (batd != null) {
	    TypeDeclaration bnode_td = (TypeDeclaration) batd;
	    FieldDeclaration[] fds = bnode_td.getFields();
	    MethodDeclaration[] mds = bnode_td.getMethods();
	    List<String> nm_values = name_map.getValues(); //mapped names
	    
	    for (FieldDeclaration fd : fds) {
		List frag_objs = fd.fragments();
		for (Object frag_obj : frag_objs) {
		    SimpleName fdname = ((VariableDeclarationFragment) frag_obj).getName();
		    String fdname_str = fdname.getIdentifier();
		    if (!mapped_ns.containsName(fdname_str) &&
			!nm_values.contains(fdname_str)) {
			unmapped_bsn_list.add(fdname);
		    }
		}
	    }
	    for (MethodDeclaration md : mds) {
		SimpleName mdname = md.getName();
		String mdname_str = mdname.getIdentifier();
		if (!mdname_str.equals(bm_name)) { //Ignore the enclosing method
		    if (!mapped_ns.containsName(mdname_str) &&
			!nm_values.contains(mdname_str)) {
			unmapped_bsn_list.add(mdname);
		    }
		}
	    }
	}

	//Now we have unmapped_bsn_list & unmapped_csn_list
	//Compare all the unmapped names between the lists
	List<NamePairVal> npv_list = new ArrayList<NamePairVal>();
	for (SimpleName ucsn : unmapped_csn_list) {
	    //====================
	    //System.out.println("Unmapped CSN: " + ucsn);
	    //====================	    
	    //List<String> ucsn_ctk_list = ConceptTokenExtractor.extract(ucsn);
	    List<String> ucsn_ctk_list = ConceptTokenExtractor.extract(ucsn, true, true);
	    for (SimpleName ubsn : unmapped_bsn_list) {
		//List<String> ubsn_ctk_list = ConceptTokenExtractor.extract(ubsn);
		List<String> ubsn_ctk_list = ConceptTokenExtractor.extract(ubsn, true, true);
		//=====================
		/*
		System.out.println("Unmapped CSN: " + ucsn);
		for (String xx : ucsn_ctk_list) { System.out.print(xx + " "); }
		System.out.println();
		System.out.println("Unmapped BSN: " + ubsn);
		for (String xx : ubsn_ctk_list) { System.out.print(xx + " "); }
		System.out.println();
		*/
		//=====================
		
		float score = computeConceptSimilarity(ucsn_ctk_list, ubsn_ctk_list);
		if (score > 0) {
		    //=====================
		    //System.out.println("Unmapped CSN: " + ucsn);		    
		    //System.out.println("Unmapped BSN: " + ubsn);
		    //System.out.println("Score: " + score);
		    //=====================
		    npv_list.add(new NamePairVal(ucsn, ubsn, score));
		}
	    }
	}

	//Sort the name pairs
	Collections.sort(npv_list, new Comparator<NamePairVal>() {
		@Override public int compare(NamePairVal npv0, NamePairVal npv1) {
		    Float f0 = (Float) npv0.getScore();
		    Float f1 = (Float) npv1.getScore();
		    return f1.compareTo(f0);
		}
	    });

	//====================
	/*
	System.out.println("Mapped by Conceptual Words");
	for (NamePairVal npv : npv_list) {
	    System.out.println("CSN: " + npv.getSimpleName0());
	    System.out.println("BSN: " + npv.getSimpleName1());
	}
	*/
	//====================	
	
	//Update the name map
	for (NamePairVal npv : npv_list) {
	    SimpleName ucsn = npv.getSimpleName0();
	    JcompType jctype = JcompAst.getJavaType(ucsn);
	    //==================
	    //System.out.println("UCSN: " + ucsn);
	    //==================	    
	    if (jctype != null) {
		if (!name_map.containsKeyAsJcompType(jctype)) {
		    //name_map.add(jctype, npv.getSimpleName1().getIdentifier());
		    addToMap(name_map, jctype, npv.getSimpleName1().getIdentifier());
		    //===============
		    //System.out.println(name_map);
		    //===============		    
		}
	    }
	    else {
		JcompSymbol jcsymbol = JcompAst.getReference(ucsn);
		if (jcsymbol != null) {
		    if (!name_map.containsKeyAsJcompSymbol(jcsymbol)) {
			//name_map.add(jcsymbol, npv.getSimpleName1().getIdentifier());
			addToMap(name_map, jcsymbol, npv.getSimpleName1().getIdentifier());
			//===============
			//System.out.println(name_map);
			//===============
		    }
		}
		else {
		    String ucsn_str = ucsn.getIdentifier();
		    if (!name_map.containsKeyAsString(ucsn_str)) {
			//name_map.add(ucsn_str, npv.getSimpleName1().getIdentifier());
			addToMap(name_map, ucsn_str, npv.getSimpleName1().getIdentifier());
			//===============
			//System.out.println(name_map);
			//===============
			
		    }
		}
	    }
	}

	return name_map;
    }

    /* This is identical to mapNames0(...) except that (1) names that are from 
       mapped_ns are shown as what they are for pattern matching and (2) names
       that are enclosing classes/methods are shown as ENCLOSINGCLASS/ENCLOSINGMETHOD
       pattern matching. Names of (1) and (2) are NOT included in the name map. 
       Also, we check whether two names are compatible before adding them to a name map.
    */
    public NameMap mapNames1Helper(RenameChunk bchunk, RenameChunk cchunk,
				   String bcname, String bmname,
				   String ccname, String cmname, NameSet mapped_ns) {

	NameMap0 name_map0 = new NameMap0();
	
	//First get all components (collected in pre-order) from a chunk
	List<ASTNode> bnodes = bchunk.getNodeList();
	List<ASTNode> cnodes = cchunk.getNodeList();
	List<ASTNode> bcpt_list = new ArrayList<ASTNode>();
	List<ASTNode> ccpt_list = new ArrayList<ASTNode>();
	for (ASTNode bnode : bnodes) {
	    List<ASTNode> bcpt_list0 = ComponentGetter.getComponents(bnode);
	    for (ASTNode bcpt : bcpt_list0) { bcpt_list.add(bcpt); }
	}
	for (ASTNode cnode : cnodes) {
	    List<ASTNode> ccpt_list0 = ComponentGetter.getComponents(cnode);
	    for (ASTNode ccpt : ccpt_list0) { ccpt_list.add(ccpt); }
	}

	//Match the component patterns
	int bcpt_list_size = bcpt_list.size();
	int ccpt_list_size = ccpt_list.size();
	Set<ASTNode> matched_bcpts = new HashSet<ASTNode>();
	Set<ASTNode> matched_ccpts = new HashSet<ASTNode>();
	for (int i=0; i<bcpt_list_size; i++) {
	    ASTNode bcpt = bcpt_list.get(i);
	    List<CodeToken> btokens = ctgen.getCTs(bcpt);
	    List<String> btoken_ptns = new ArrayList<String>();	    
	    int btokens_length = btokens.size();
	    for (CodeToken btoken : btokens) {
		btoken_ptns.add(getPatternString(btoken, bcname, bmname, mapped_ns));
	    }
	    //=======================
	    /*
	    System.out.println("B COMPONENT:");
	    System.out.println(bcpt);
	    for (String btoken_ptn : btoken_ptns) { System.out.print(btoken_ptn + " "); }
	    System.out.println();
	    */
	    //=======================	    

	    
	    for (int j=0; j<ccpt_list_size; j++) {
		ASTNode ccpt = ccpt_list.get(j);
		if (matched_ccpts.contains(ccpt)) {
		    continue; //ccpt is already matched
		}
		List<CodeToken> ctokens = ctgen.getCTs(ccpt);
		int ctokens_length = ctokens.size();
		if (btokens_length != ctokens_length) {
		    //Component Pattern NOT match!
		    continue;
		}

		List<String> ctoken_ptns = new ArrayList<String>();
		for(CodeToken ctoken : ctokens) {
		    ctoken_ptns.add(getPatternString(ctoken, ccname, cmname, mapped_ns));
		}

		boolean ptns_equal = true;
		for (int k=0; k<btokens_length; k++) {
		    //=============
		    //System.out.println("BTOKEN PATTERNS: " + btoken_ptns.get(k));
		    //System.out.println("CTOKEN PATTERNS: " + ctoken_ptns.get(k));
		    //=============
		    
		    if (!btoken_ptns.get(k).equals(ctoken_ptns.get(k))) {
			ptns_equal = false;
			break;
		    }
		}
		if (ptns_equal) {
		    matched_bcpts.add(bcpt);
		    matched_ccpts.add(ccpt);

		    //=============
		    /*
		    System.err.println(bcpt);
		    System.err.println(ccpt);
		    for (String ctoken_ptn : ctoken_ptns) {
			System.err.print(ctoken_ptn + " ");
		    }
		    System.err.println();
		    */
		    //=============		    

		    //Add mapped names (btokens_length==ctokens_length is true!)
		    for (int k=0; k<btokens_length; k++) {
			//=================
			//System.out.println("BTOKEN: " + btokens.get(k));
			//System.out.println("BTOKEN PATTERN: " + btoken_ptns.get(k));
			//System.out.println("CTOKEN: " + ctokens.get(k));
			//System.out.println("CTOKEN PATTERN: " + ctoken_ptns.get(k));
			//=================
			
			String ctoken_ptn = ctoken_ptns.get(k);
			//Note here we do not consider enclosing class & method names,
			//since their patterns are ENCLOSINGCLASS & ENCLOSINGMETHOD
			if (("$v$".equals(ctoken_ptn)) || ("$m$".equals(ctoken_ptn))) {

			    //====================
			    //System.err.println(ctoken_ptn);
			    //====================
			    
			    //Ignore all-caps tokens
			    //=====================
			    //DO WE NEED THIS?
			    if (isAllUpperCase(ctokens.get(k).getString())) { continue; }
			    //=====================			    
			    
			    ASTNode cnode = ctokens.get(k).getNode();
			    JcompSymbol jcsymbol = JcompAst.getReference(cnode);
			    String bname = btokens.get(k).getString();
			    if (jcsymbol != null) {
				//name_map0.add(jcsymbol, bname);
				addToMap(name_map0, jcsymbol, bname);
			    }
			    else {
				//name_map0.add(ctokens.get(k).getString(), bname);
				addToMap(name_map0, ctokens.get(k).getString(), bname);
			    }
			}
			else if ("$t$".equals(ctoken_ptn)) {
			    ASTNode cnode = ctokens.get(k).getNode();
			    JcompType jctype = JcompAst.getJavaType(cnode);
			    String bname = btokens.get(k).getString();
			    if (jctype != null) {
				//name_map0.add(jctype, bname);
				addToMap(name_map0, jctype, bname);
			    }
			    else {
				//name_map0.add(ctokens.get(k).getString(), bname);
				addToMap(name_map0, ctokens.get(k).getString(), bname);
			    }
			}
		    }
		    
		    break;
		}
		
	    }
	}

	return name_map0.getNameMap(); //Get a mapped name with the max count
    }

    private void addToMap(NameMap0 name_map0, JcompSymbol jcsymbol, String mapped_name) {
	if (isCompatible(jcsymbol.getName(), mapped_name)) {
	    name_map0.add(jcsymbol, mapped_name);
	}
    }

    private void addToMap(NameMap0 name_map0, JcompType jctype, String mapped_name) {
	if (isCompatible(jctype.getName(), mapped_name)) {
	    name_map0.add(jctype, mapped_name);
	}
    }

    private void addToMap(NameMap0 name_map0, String s, String mapped_name) {
	if (isCompatible(s, mapped_name)) {
	    name_map0.add(s, mapped_name);
	}
    }

    private void addToMap(NameMap name_map, JcompSymbol jcsymbol, String mapped_name) {
	if (isCompatible(jcsymbol.getName(), mapped_name)) {
	    name_map.add(jcsymbol, mapped_name);
	}
    }

    private void addToMap(NameMap name_map, JcompType jctype, String mapped_name) {
	if (isCompatible(jctype.getName(), mapped_name)) {
	    name_map.add(jctype, mapped_name);
	}
    }

    private void addToMap(NameMap name_map, String s, String mapped_name) {
	if (isCompatible(s, mapped_name)) {
	    name_map.add(s, mapped_name);
	}
    }

    //For example, "CategoryDataSet" is not compatible with "getDataSet".
    private boolean isCompatible(String s0, String s1) {
	int i0=0, i1=0;
	int lastIndexOfDot0 = s0.lastIndexOf(".");
	int lastIndexOfDot1 = s1.lastIndexOf(".");
	if (lastIndexOfDot0 != -1) { i0 = lastIndexOfDot0 + 1; }
	if (lastIndexOfDot1 != -1) { i1 = lastIndexOfDot1 + 1; }
	char c0 = s0.charAt(i0);
	char c1 = s1.charAt(i1);
	return (Character.isUpperCase(c0) == Character.isUpperCase(c1));
    }
    
    private class NamePairVal
    {
	SimpleName sn0;
	SimpleName sn1;
	float score;

	public NamePairVal(SimpleName sn0, SimpleName sn1, float score) {
	    this.sn0 = sn0;
	    this.sn1 = sn1;
	    this.score = score;
	}

	public SimpleName getSimpleName0() { return sn0; }

	public SimpleName getSimpleName1() { return sn1; }

	public float getScore() { return score; }
    }
    
    private float computeConceptSimilarity(List<String> tk_strs0, List<String> tk_strs1) {
	int tk_strs0_size = tk_strs0.size();
	int tk_strs1_size = tk_strs1.size();
	boolean[] matched_arr_b = new boolean[tk_strs1_size];
	int match_count = 0;
	for (int i=0; i<tk_strs0_size; i++) {
	    String tk_str0 = tk_strs0.get(i);
	    for (int j=0; j<tk_strs1_size; j++) {
		if (matched_arr_b[j]) { continue; }
		String tk_str1 = tk_strs1.get(j);
		if (tk_str0.equals(tk_str1)) {
		    match_count += 1;
		    matched_arr_b[j] = true;
		    break;
		}
	    }
	}
	
	return (2.0f * match_count) / (tk_strs0_size + tk_strs1_size);
    }
    
    private class UnmappedBNameCollector extends ASTVisitor
    {
	NameSet mapped_ns;
	NameMap name_map;
	List<SimpleName> sn_list;
	private List<String> name_map_values;

	public UnmappedBNameCollector(NameSet mns, NameMap nm) {
	    mapped_ns = mns;
	    name_map = nm;
	    sn_list = new ArrayList<SimpleName>();
	    name_map_values = name_map.getValues();
	}

	public List<SimpleName> getSimpleNameList() { return sn_list; }

	@Override public boolean visit(SimpleName sn) {
	    String sn_name = sn.getIdentifier();
	    if (mapped_ns.containsName(sn_name)) {
		return false;
	    }
	    if (name_map_values.contains(sn_name)) {
		return false;
	    }
	    if (isAllUpperCase(sn_name)) {
		return false;
	    }
	    JcompType jctype = JcompAst.getJavaType(sn);
	    if (jctype != null) {
		if (!jctype.getName().startsWith("java.")) { sn_list.add(sn); }
	    }
	    else {
		JcompSymbol jcsymbol = JcompAst.getReference(sn);
		if (jcsymbol != null) {
		    if (!jcsymbol.getFullName().startsWith("java.")) {
			sn_list.add(sn);
		    }
		}
	    }
	    return false;
	}
    }
    
    private class UnmappedCNameCollector extends ASTVisitor
    {
	NameSet mapped_ns;
	NameMap name_map;
	List<SimpleName> sn_list;
	private Set<String> name_map_keys;

	public UnmappedCNameCollector(NameSet mns, NameMap nm) {
	    mapped_ns = mns;
	    name_map = nm;
	    sn_list = new ArrayList<SimpleName>();
	    name_map_keys = nm.getKeys();
	}

	public List<SimpleName> getSimpleNameList() { return sn_list; }
	
	@Override public boolean visit(SimpleName sn) {
	    String sn_name = sn.getIdentifier();
	    if (mapped_ns.containsName(sn_name)) {
		return false;
	    }
	    if (isAllUpperCase(sn_name)) {
		return false;
	    }
	    JcompType jctype = JcompAst.getJavaType(sn);
	    if (jctype != null) {
		if (!name_map.containsKeyAsJcompType(jctype) &&
		    !jctype.getName().startsWith("java.")) {
		    sn_list.add(sn); }
	    }
	    else {
		JcompSymbol jcsymbol = JcompAst.getReference(sn);
		if (jcsymbol != null) {
		    if (jcsymbol.isMethodSymbol() &&
			name_map_keys.contains(sn_name)) {
			//The simple name of a method declaration and the self-method call from the method body do not seem to have the same reference.
			return false;
		    }
		    else if (!name_map.containsKeyAsJcompSymbol(jcsymbol) &&
			!jcsymbol.getFullName().startsWith("java.")) {
			sn_list.add(sn);
		    }
		}
		else {
		    if (!name_map.containsKeyAsString(sn_name)) {
			sn_list.add(sn);
		    }
		}
	    }
	    return false;
	}
    }
    
    private String getPatternString(CodeToken token, String cname, String mname, NameSet mapped_ns) {
	String token_str = token.getString();
	if (token_str.equals(cname)) {
	    return "$ENCLOSECLASS$";
	}
	else if (token_str.equals(mname)) {
	    return "$ENCLOSEMETHOD$";
	}
	else {
	    String token_ptn = token.getPatternString();
	    //=============
	    //System.out.println("CODE TOKEN STRING: " + token_str);
	    //System.out.println("CODE TOKEN PATTERN0: " + token_ptn);
	    //=============	    
	    if (mapped_ns != null) {
		if ("$v$".equals(token_ptn) &&
		    mapped_ns.containsVarName(token_str)) {
		    return token_str; //Use the string itself
		}
		else if ("$t$".equals(token_ptn) &&
			 mapped_ns.containsTypeName(token_str)) {
		    return token_str; //Use the string itself
		}
		else if ("$m$".equals(token_ptn) &&
			 mapped_ns.containsMethodCallName(token_str)) {
		    //==============
		    //System.out.println("RETURN THE TOKEN STRING ITSELF");
		    //==============		    
		    return token_str; //Use the string itself
		}
		else {
		    return token_ptn;
		}
	    }
	    else {
		return token_ptn;
	    }
	}
    }
    
    private boolean isAllUpperCase(String s) {
	if (s == null) { return false; }
	boolean is_all_upper_case = true;
	char[] cs = s.toCharArray();
	for (char c : cs) {
	    if (Character.isLetter(c) && !Character.isUpperCase(c)) {
		is_all_upper_case = false;
		break;
	    }
	}
	return is_all_upper_case;
    }
}
