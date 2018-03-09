package sharpfix.util;

import java.util.List;
import java.util.ArrayList;
import org.eclipse.jdt.core.dom.*;

public class ASTNodeReduceChecker0
{
    private static boolean isLiteral(ASTNode node) {
	return ((node instanceof BooleanLiteral) ||
		(node instanceof CharacterLiteral) ||
		(node instanceof NullLiteral) ||
		(node instanceof NumberLiteral) ||
		(node instanceof StringLiteral) ||
		(node instanceof TypeLiteral));
    }
    
    private static boolean isEqualOrReduced0(ASTNode node0, ASTNode node1) {
	if (!isLiteral(node0) && isLiteral(node1)) {
	    return true;
	}
	else {
	    String node0_str = node0.toString();
	    String node1_str = node1.toString();
	    return (node0_str.contains(node1_str));
	}
    }
    
    /* Check if node1 is a reduced version of node0.
       This is we check whether some component(s) of node1 are removed from node0,
       but other component(s) of node1 are unchanged from node0. */
    public static boolean isEqualOrReduced(ASTNode node0, ASTNode node1) {
	if (node0 == null && node1 == null) { return true; }
	else if (node0 != null && node1 == null) { return true; }
	else if (node0 == null && node1 != null) { return false; }

	//Is node1 is equal to or a sub-component of node0?
	//Is node1 a literal but node0 is not?
	if (isEqualOrReduced0(node0, node1)) { return true; }

	//Check if (1) all components are equal or (2) some component of node1 is a sub-component of node0, and all other components are equal.
	int ntype0 = node0.getNodeType();
	int ntype1 = node1.getNodeType();
	if (ntype0 != ntype1) { return false; }

	if (node0 instanceof CompilationUnit) {
	    CompilationUnit cu0 = (CompilationUnit) node0;
	    CompilationUnit cu1 = (CompilationUnit) node1;
	    return isEqualOrReduced(cu0.getPackage(),cu1.getPackage())
		&& isEqualOrReduced(cu0.imports(),cu1.imports())
		&& isEqualOrReduced(cu0.types(),cu1.types());
	}
	else if (node0 instanceof ImportDeclaration) {
	    ImportDeclaration id0 = (ImportDeclaration) node0;
	    ImportDeclaration id1 = (ImportDeclaration) node1;
	    return (id0.isOnDemand()==id1.isOnDemand())
		&& (id0.isStatic()==id1.isStatic())
		&& isEqualOrReduced(id0.getName(),id1.getName());
	}
	else if (node0 instanceof PackageDeclaration) {
	    PackageDeclaration pd0 = (PackageDeclaration) node0;
	    PackageDeclaration pd1 = (PackageDeclaration) node1;
	    return isEqualOrReduced(pd0.getName(),pd1.getName());
	}
	else if (node0 instanceof TypeDeclaration) {
	    TypeDeclaration td0 = (TypeDeclaration) node0;
	    TypeDeclaration td1 = (TypeDeclaration) node1;
	    return isEqualOrReduced(td0.getName(),td1.getName())
		&& isEqualOrReduced(td0.bodyDeclarations(),td1.bodyDeclarations());
	}
	else if (node0 instanceof EnumDeclaration) {
	    EnumDeclaration ed0 = (EnumDeclaration) node0;
	    EnumDeclaration ed1 = (EnumDeclaration) node1;
	    return isEqualOrReduced(ed0.enumConstants(),ed1.enumConstants());
	}
	else if (node0 instanceof EnumConstantDeclaration) {
	    EnumConstantDeclaration ecd0 = (EnumConstantDeclaration) node0;
	    EnumConstantDeclaration ecd1 = (EnumConstantDeclaration) node1;
	    return isEqualOrReduced(ecd0.getName(),ecd1.getName())
		&& isEqualOrReduced(ecd0.arguments(),ecd1.arguments());
	}
	else if (node0 instanceof FieldDeclaration) {
	    FieldDeclaration fd0 = (FieldDeclaration) node0;
	    FieldDeclaration fd1 = (FieldDeclaration) node1;
	    return isEqualOrReduced(fd0.getType(),fd1.getType())
		&& isEqualOrReduced(fd0.fragments(),fd1.fragments());
	}
	else if (node0 instanceof Initializer) {
	    Initializer init0 = (Initializer) node0;
	    Initializer init1 = (Initializer) node1;
	    return isEqualOrReduced(init0.getBody(),init1.getBody());
	}
	else if (node0 instanceof MethodDeclaration) {
	    MethodDeclaration md0 = (MethodDeclaration) node0;
	    MethodDeclaration md1 = (MethodDeclaration) node1;
	    return isEqualOrReduced(md0.getReturnType2(),md1.getReturnType2())
		&& isEqualOrReduced(md0.getName(),md1.getName())
		&& isEqualOrReduced(md0.parameters(),md1.parameters())
		&& isEqualOrReduced(md0.typeParameters(),md1.typeParameters())
		&& isEqualOrReduced(md0.getBody(),md1.getBody());
	}	
	else if (node0 instanceof AssertStatement) {
	    AssertStatement as0 = (AssertStatement) node0;
	    AssertStatement as1 = (AssertStatement) node1;
	    return isEqualOrReduced(as0.getExpression(),as1.getExpression())
		&& isEqualOrReduced(as0.getMessage(),as1.getMessage());
	}
	else if (node0 instanceof Block) {
	    Block b0 = (Block) node0;
	    Block b1 = (Block) node1;
	    return isEqualOrReduced(b0.statements(), b1.statements());
	}
	else if (node0 instanceof BreakStatement) {
	    BreakStatement bs0 = (BreakStatement) node0;
	    BreakStatement bs1 = (BreakStatement) node1;
	    return isEqualOrReduced(bs0.getLabel(), bs1.getLabel());
	}
	else if (node0 instanceof ConstructorInvocation) {
	    ConstructorInvocation ci0 = (ConstructorInvocation) node0;
	    ConstructorInvocation ci1 = (ConstructorInvocation) node1;
	    return isEqualOrReduced(ci0.arguments(),ci1.arguments())
		&& isEqualOrReduced(ci0.typeArguments(),ci1.typeArguments());
	}
	else if (node0 instanceof ContinueStatement) {
	    ContinueStatement cs0 = (ContinueStatement) node0;
	    ContinueStatement cs1 = (ContinueStatement) node1;
	    return isEqualOrReduced(cs0.getLabel(),cs1.getLabel());
	}
	else if (node0 instanceof DoStatement) {
	    DoStatement ds0 = (DoStatement) node0;
	    DoStatement ds1 = (DoStatement) node1;
	    return isEqualOrReduced(ds0.getBody(),ds1.getBody())
		&& isEqualOrReduced(ds0.getExpression(),ds1.getExpression());
	}
	else if (node0 instanceof EmptyStatement) {
	    return true;
	}
	else if (node0 instanceof EnhancedForStatement) {
	    EnhancedForStatement efs0 = (EnhancedForStatement) node0;
	    EnhancedForStatement efs1 = (EnhancedForStatement) node1;
	    return isEqualOrReduced(efs0.getBody(), efs1.getBody())
		&& isEqualOrReduced(efs0.getExpression(), efs1.getExpression())
		&& isEqualOrReduced(efs0.getParameter(), efs1.getParameter());
	}
	else if (node0 instanceof ExpressionStatement) {
	    ExpressionStatement es0 = (ExpressionStatement) node0;
	    ExpressionStatement es1 = (ExpressionStatement) node1;
	    return isEqualOrReduced(es0.getExpression(),es1.getExpression());
	}
	else if (node0 instanceof ForStatement) {
	    ForStatement fs0 = (ForStatement) node0;
	    ForStatement fs1 = (ForStatement) node1;
	    return isEqualOrReduced(fs0.initializers(),fs1.initializers())
		&& isEqualOrReduced(fs0.getExpression(),fs1.getExpression())
		&& isEqualOrReduced(fs0.updaters(),fs1.updaters())
		&& isEqualOrReduced(fs0.getBody(),fs1.getBody());
	}
	else if (node0 instanceof IfStatement) {
	    IfStatement is0 = (IfStatement) node0;
	    IfStatement is1 = (IfStatement) node1;
	    return isEqualOrReduced(is0.getExpression(),is1.getExpression())
		&& isEqualOrReduced(is0.getThenStatement(),is1.getThenStatement())
		&& isEqualOrReduced(is0.getElseStatement(),is1.getElseStatement());
	}
	else if (node0 instanceof LabeledStatement) {
	    LabeledStatement ls0 = (LabeledStatement) node0;
	    LabeledStatement ls1 = (LabeledStatement) node1;
	    return isEqualOrReduced(ls0.getBody(),ls1.getBody())
		&& isEqualOrReduced(ls0.getLabel(),ls1.getLabel());
	}
	else if (node0 instanceof ReturnStatement) {
	    ReturnStatement rs0 = (ReturnStatement) node0;
	    ReturnStatement rs1 = (ReturnStatement) node1;
	    return isEqualOrReduced(rs0.getExpression(),rs1.getExpression());
	}
	else if (node0 instanceof SuperConstructorInvocation) {
	    SuperConstructorInvocation sci0 = (SuperConstructorInvocation) node0;
	    SuperConstructorInvocation sci1 = (SuperConstructorInvocation) node1;
	    return isEqualOrReduced(sci0.arguments(),sci1.arguments())
		&& isEqualOrReduced(sci0.getExpression(),sci1.getExpression())
		&& isEqualOrReduced(sci0.typeArguments(),sci1.typeArguments());
	}
	else if (node0 instanceof SwitchCase) {
	    SwitchCase sc0 = (SwitchCase) node0;
	    SwitchCase sc1 = (SwitchCase) node1;
	    if (sc0.isDefault() == sc1.isDefault()) {
		return isEqualOrReduced(sc0.getExpression(),sc1.getExpression());
	    }
	    else { return false; }
	}
	else if (node0 instanceof SwitchStatement) {
	    SwitchStatement ss0 = (SwitchStatement) node0;
	    SwitchStatement ss1 = (SwitchStatement) node1;
	    return isEqualOrReduced(ss0.getExpression(),ss1.getExpression())
		&& isEqualOrReduced(ss0.statements(),ss1.statements());
	}
	else if (node0 instanceof SynchronizedStatement) {
	    SynchronizedStatement ss0 = (SynchronizedStatement) node0;
	    SynchronizedStatement ss1 = (SynchronizedStatement) node1;
	    return isEqualOrReduced(ss0.getBody(),ss1.getBody())
		&& isEqualOrReduced(ss0.getExpression(),ss1.getExpression());
	}
	else if (node0 instanceof ThrowStatement) {
	    ThrowStatement ts0 = (ThrowStatement) node0;
	    ThrowStatement ts1 = (ThrowStatement) node1;
	    return isEqualOrReduced(ts0.getExpression(),ts1.getExpression());
	}
	else if (node0 instanceof TryStatement) {
	    TryStatement ts0 = (TryStatement) node0;
	    TryStatement ts1 = (TryStatement) node1;
	    return isEqualOrReduced(ts0.getBody(),ts1.getBody())
		&& isEqualOrReduced(ts0.catchClauses(),ts1.catchClauses())
		&& isEqualOrReduced(ts0.getFinally(),ts1.getFinally());
	}
	else if (node0 instanceof TypeDeclarationStatement) {
	    return false; //Not handling this
	}
	else if (node0 instanceof VariableDeclarationStatement) {
	    VariableDeclarationStatement vds0 = (VariableDeclarationStatement) node0;
	    VariableDeclarationStatement vds1 = (VariableDeclarationStatement) node1;
	    return isEqualOrReduced(vds0.getType(),vds1.getType())
		&& isEqualOrReduced(vds0.fragments(),vds1.fragments());
	}
	else if(node0 instanceof WhileStatement) {
	    WhileStatement ws0 = (WhileStatement) node0;
	    WhileStatement ws1 = (WhileStatement) node1;
	    return isEqualOrReduced(ws0.getBody(),ws1.getBody())
		&& isEqualOrReduced(ws0.getExpression(),ws1.getExpression());
	}
	else if (node0 instanceof ArrayAccess) {
	    ArrayAccess aa0 = (ArrayAccess) node0;
	    ArrayAccess aa1 = (ArrayAccess) node1;
	    return isEqualOrReduced(aa0.getArray(),aa1.getArray())
		&& isEqualOrReduced(aa0.getIndex(),aa1.getIndex());
	}
	else if (node0 instanceof ArrayCreation) {
	    ArrayCreation ac0 = (ArrayCreation) node0;
	    ArrayCreation ac1 = (ArrayCreation) node1;
	    return isEqualOrReduced(ac0.getType(),ac1.getType())
		&& isEqualOrReduced(ac0.getInitializer(),ac1.getInitializer())
		&& isEqualOrReduced(ac0.dimensions(),ac1.dimensions());
	}
	else if (node0 instanceof Assignment) {
	    Assignment assign0 = (Assignment) node0;
	    Assignment assign1 = (Assignment) node1;
	    String op0 = assign0.getOperator().toString();
	    String op1 = assign1.getOperator().toString();
	    return (op0.equals(op1)) && isEqualOrReduced(assign0.getLeftHandSide(),assign1.getLeftHandSide()) && isEqualOrReduced(assign0.getRightHandSide(),assign1.getRightHandSide());
	}
	else if (node0 instanceof BooleanLiteral) {
	    BooleanLiteral bl0 = (BooleanLiteral) node0;
	    BooleanLiteral bl1 = (BooleanLiteral) node1;
	    return (bl0.booleanValue()==bl1.booleanValue());
	}
	else if (node0 instanceof CastExpression) {
	    CastExpression ce0 = (CastExpression) node0;
	    CastExpression ce1 = (CastExpression) node1;
	    return isEqualOrReduced(ce0.getType(),ce1.getType())
		&& isEqualOrReduced(ce0.getExpression(),ce1.getExpression());
	}
	else if (node0 instanceof CharacterLiteral) {
	    CharacterLiteral cl0 = (CharacterLiteral) node0;
	    CharacterLiteral cl1 = (CharacterLiteral) node1;
	    return cl0.charValue() == cl1.charValue();
	}
	else if (node0 instanceof ClassInstanceCreation) {
	    ClassInstanceCreation cic0 = (ClassInstanceCreation) node0;
	    ClassInstanceCreation cic1 = (ClassInstanceCreation) node1;
	    return isEqualOrReduced(cic0.getType(),cic1.getType())
		&& isEqualOrReduced(cic0.getExpression(),cic1.getExpression())
		&& isEqualOrReduced(cic0.arguments(),cic1.arguments())
		&& isEqualOrReduced(cic0.typeArguments(),cic1.typeArguments());
	}
	else if (node0 instanceof ConditionalExpression) {
	    ConditionalExpression ce0 = (ConditionalExpression) node0;
	    ConditionalExpression ce1 = (ConditionalExpression) node1;
	    return isEqualOrReduced(ce0.getExpression(),ce1.getExpression())
		&& isEqualOrReduced(ce0.getThenExpression(),ce1.getThenExpression())
		&& isEqualOrReduced(ce0.getElseExpression(),ce1.getElseExpression());
	}
	else if (node0 instanceof FieldAccess) {
	    FieldAccess fa0 = (FieldAccess) node0;
	    FieldAccess fa1 = (FieldAccess) node1;
	    return isEqualOrReduced(fa0.getExpression(),fa1.getExpression())
		&& isEqualOrReduced(fa0.getName(),fa1.getName());
	}
	else if (node0 instanceof InfixExpression) {
	    InfixExpression ie0 = (InfixExpression) node0;
	    InfixExpression ie1 = (InfixExpression) node1;
	    return ie0.getOperator().toString().equals(ie1.getOperator().toString())
		&& isEqualOrReduced(ie0.getLeftOperand(),ie1.getLeftOperand())
		&& isEqualOrReduced(ie0.getRightOperand(),ie1.getRightOperand())
		&& isEqualOrReduced(ie0.extendedOperands(),ie1.extendedOperands());
	}
	else if (node0 instanceof InstanceofExpression) {
	    InstanceofExpression ie0 = (InstanceofExpression) node0;
	    InstanceofExpression ie1 = (InstanceofExpression) node1;
	    return isEqualOrReduced(ie0.getLeftOperand(),ie1.getLeftOperand())
		&& isEqualOrReduced(ie0.getRightOperand(),ie1.getRightOperand());
	}
	else if (node0 instanceof LambdaExpression) {
	    LambdaExpression le0 = (LambdaExpression) node0;
	    LambdaExpression le1 = (LambdaExpression) node1;
	    return isEqualOrReduced(le0.getBody(),le1.getBody())
		&& isEqualOrReduced(le0.parameters(),le1.parameters());
	}
	else if (node0 instanceof MethodInvocation) {
	    MethodInvocation mi0 = (MethodInvocation) node0;
	    MethodInvocation mi1 = (MethodInvocation) node1;
	    return isEqualOrReduced(mi0.getExpression(),mi1.getExpression())
		&& isEqualOrReduced(mi0.getName(),mi1.getName())
		&& isEqualOrReduced(mi0.arguments(),mi1.arguments())
		&& isEqualOrReduced(mi0.typeArguments(),mi1.typeArguments());
	}
	else if (node0 instanceof SimpleName) {
	    SimpleName sn0 = (SimpleName) node0;
	    SimpleName sn1 = (SimpleName) node1;
	    return sn0.getIdentifier().equals(sn1.getIdentifier());
	}
	else if (node0 instanceof QualifiedName) {
	    QualifiedName qn0 = (QualifiedName) node0;
	    QualifiedName qn1 = (QualifiedName) node1;
	    return isEqualOrReduced(qn0.getName(),qn1.getName())
		&& isEqualOrReduced(qn0.getQualifier(),qn1.getQualifier());
	}
	else if (node0 instanceof NullLiteral) {
	    return true;
	}
	else if (node0 instanceof NumberLiteral) {
	    NumberLiteral nl0 = (NumberLiteral) node0;
	    NumberLiteral nl1 = (NumberLiteral) node1;
	    return nl0.getToken().equals(nl1.getToken());
	}
	else if (node0 instanceof ParenthesizedExpression) {
	    ParenthesizedExpression pe0 = (ParenthesizedExpression) node0;
	    ParenthesizedExpression pe1 = (ParenthesizedExpression) node1;
	    return isEqualOrReduced(pe0.getExpression(),pe1.getExpression());
	}
	else if (node0 instanceof PostfixExpression) {
	    PostfixExpression pe0 = (PostfixExpression) node0;
	    PostfixExpression pe1 = (PostfixExpression) node1;
	    return pe0.getOperator().toString().equals(pe1.getOperator().toString())
		&& isEqualOrReduced(pe0.getOperand(),pe1.getOperand());
	}
	else if (node0 instanceof PrefixExpression) {
	    PrefixExpression pe0 = (PrefixExpression) node0;
	    PrefixExpression pe1 = (PrefixExpression) node1;
	    return pe0.getOperator().toString().equals(pe1.getOperator().toString())
		&& isEqualOrReduced(pe0.getOperand(),pe1.getOperand());
	}
	else if (node0 instanceof StringLiteral) {
	    StringLiteral sl0 = (StringLiteral) node0;
	    StringLiteral sl1 = (StringLiteral) node1;
	    return sl0.getLiteralValue().equals(sl1.getLiteralValue());
	}
	else if (node0 instanceof SuperFieldAccess) {
	    SuperFieldAccess sfa0 = (SuperFieldAccess) node0;
	    SuperFieldAccess sfa1 = (SuperFieldAccess) node1;
	    return isEqualOrReduced(sfa0.getQualifier(),sfa1.getQualifier())
		&& isEqualOrReduced(sfa0.getName(),sfa1.getName());
	}
	else if (node0 instanceof SuperMethodInvocation) {
	    SuperMethodInvocation smi0 = (SuperMethodInvocation) node0;
	    SuperMethodInvocation smi1 = (SuperMethodInvocation) node1;
	    return isEqualOrReduced(smi0.getQualifier(),smi1.getQualifier())
		&& isEqualOrReduced(smi0.getName(),smi1.getName())
		&& isEqualOrReduced(smi0.arguments(),smi1.arguments())
		&& isEqualOrReduced(smi0.typeArguments(),smi1.typeArguments());
	}
	else if (node0 instanceof ThisExpression) {
	    ThisExpression te0 = (ThisExpression) node0;
	    ThisExpression te1 = (ThisExpression) node1;
	    return isEqualOrReduced(te0.getQualifier(),te1.getQualifier());
	}
	else if (node0 instanceof TypeLiteral) {
	    TypeLiteral tl0 = (TypeLiteral) node0;
	    TypeLiteral tl1 = (TypeLiteral) node1;
	    return isEqualOrReduced(tl0.getType(),tl1.getType());
	}
	else if (node0 instanceof VariableDeclarationExpression) {
	    VariableDeclarationExpression vde0 = (VariableDeclarationExpression) node0;
	    VariableDeclarationExpression vde1 = (VariableDeclarationExpression) node1;
	    return isEqualOrReduced(vde0.getType(),vde1.getType())
		&& isEqualOrReduced(vde0.fragments(),vde1.fragments());
	}
	else if (node0 instanceof CatchClause) {
	    CatchClause cc0 = (CatchClause) node0;
	    CatchClause cc1 = (CatchClause) node1;
	    return isEqualOrReduced(cc0.getException(),cc1.getException())
		&& isEqualOrReduced(cc0.getBody(),cc1.getBody());
	}
	else if (node0 instanceof SingleVariableDeclaration) {
	    SingleVariableDeclaration svd0 = (SingleVariableDeclaration) node0;
	    SingleVariableDeclaration svd1 = (SingleVariableDeclaration) node1;
	    return isEqualOrReduced(svd0.getName(),svd1.getName())
		&& isEqualOrReduced(svd0.getInitializer(),svd1.getInitializer());
	}
	else if (node0 instanceof VariableDeclarationFragment) {
	    VariableDeclarationFragment vdf0 = (VariableDeclarationFragment) node0;
	    VariableDeclarationFragment vdf1 = (VariableDeclarationFragment) node1;
	    return isEqualOrReduced(vdf0.getName(),vdf1.getName())
		&& isEqualOrReduced(vdf0.getInitializer(),vdf1.getInitializer());
	}
	else if (node0 instanceof PrimitiveType) {
	    PrimitiveType pt0 = (PrimitiveType) node0;
	    PrimitiveType pt1 = (PrimitiveType) node1;
	    return pt0.getPrimitiveTypeCode().toString().equals(pt1.getPrimitiveTypeCode().toString());
	}
	else if (node0 instanceof QualifiedType){
	    QualifiedType qt0 = (QualifiedType) node0;
	    QualifiedType qt1 = (QualifiedType) node1;
	    return isEqualOrReduced(qt0.getName(),qt1.getName())
		&& isEqualOrReduced(qt0.getQualifier(),qt1.getQualifier());
	}
	else if (node0 instanceof SimpleType){
	    SimpleType st0 = (SimpleType) node0;
	    SimpleType st1 = (SimpleType) node1;
	    return isEqualOrReduced(st0.getName(),st1.getName());
	}
	else if (node0 instanceof ArrayType) {
	    ArrayType at0 = (ArrayType) node0;
	    ArrayType at1 = (ArrayType) node1;
	    return (at0.getDimensions()==at1.getDimensions())
		&& isEqualOrReduced(at0.getElementType(),at1.getElementType());
	}
	else if (node0 instanceof ParameterizedType) {
	    ParameterizedType pt0 = (ParameterizedType) node0;
	    ParameterizedType pt1 = (ParameterizedType) node1;
	    return isEqualOrReduced(pt0.getType(),pt1.getType())
		&& isEqualOrReduced(pt0.typeArguments(),pt1.typeArguments());
	}
	else if (node0 instanceof IntersectionType) {
	    IntersectionType it0 = (IntersectionType) node0;
	    IntersectionType it1 = (IntersectionType) node1;
	    return isEqualOrReduced(it0.types(),it1.types());
	}
	else if (node0 instanceof UnionType) {
	    UnionType ut0 = (UnionType) node0;
	    UnionType ut1 = (UnionType) node1;
	    return isEqualOrReduced(ut0.types(),ut1.types());
	}
	else if (node0 instanceof TypeParameter) {
	    TypeParameter pt0 = (TypeParameter) node0;
	    TypeParameter pt1 = (TypeParameter) node1;
	    return isEqualOrReduced(pt0.getName(),pt1.getName())
		&& isEqualOrReduced(pt0.typeBounds(),pt1.typeBounds());
	}
	else {
	    return false;
	}
    }

    private static boolean isEqualOrReduced(List l0, List l1) {
	if (l0 == null && l1 == null) { return true; }
	else if (l0 != null && l1 == null) { return true; }
	else if (l0 == null && l1 != null) { return false; }
	else {
	    int size0 = l0.size();
	    int size1 = l1.size();
	    if (size0 < size1) { return false; }
	    for (int i=0; i<size1; i++) {
		ASTNode n0 = (ASTNode) l0.get(i);
		ASTNode n1 = (ASTNode) l1.get(i);
		if (!isEqualOrReduced(n0, n1)) {
		    return false;
		}
	    }
	    return true;
	}
    }
}
