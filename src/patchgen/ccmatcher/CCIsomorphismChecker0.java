package sharpfix.patchgen.ccmatcher;

import org.eclipse.jdt.core.dom.*;


public class CCIsomorphismChecker0
{
    private static ASTNode stripNode(ASTNode node) {
	if (node instanceof ExpressionStatement) {
	    ExpressionStatement es = (ExpressionStatement) node;
	    return es.getExpression();
	}
	else if (node instanceof ParenthesizedExpression) {
	    ParenthesizedExpression pe = (ParenthesizedExpression) node;
	    return pe.getExpression();
	}
	else if (node instanceof ParameterizedType) {
	    ParameterizedType pt = (ParameterizedType) node;
	    return pt.getType();
	}
	else {
	    return node;
	}
    }

    private static boolean isLoopNodeType(int node_type) {
	if ((node_type == ASTNode.DO_STATEMENT) ||
	    (node_type == ASTNode.ENHANCED_FOR_STATEMENT) ||
	    (node_type == ASTNode.FOR_STATEMENT) ||
	    (node_type == ASTNode.WHILE_STATEMENT)) {
	    return true;
	}
	else {
	    return false;
	}
    }
    
    public static boolean isomorphic(ASTNode unode1, ASTNode unode2) {

	if (unode1==null || unode2 == null) { return false; }
	ASTNode node1 = stripNode(unode1);
	ASTNode node2 = stripNode(unode2);
	int node1_type = node1.getNodeType();
	int node2_type = node2.getNodeType();	
	if (node1_type != node2_type) {
	    if (isLoopNodeType(node1_type) && isLoopNodeType(node2_type)) {
		return true;
	    }
	    else {
		return false;
	    }
	}
	if (node1 instanceof ArrayAccess) {
	    ArrayAccess aa1 = (ArrayAccess) node1;
	    ArrayAccess aa2 = (ArrayAccess) node2;
	    return isomorphic(aa1.getArray(), aa2.getArray());
	}
	else if (node1 instanceof ArrayCreation) {
	    ArrayCreation ac1 = (ArrayCreation) node1;
	    ArrayCreation ac2 = (ArrayCreation) node2;
	    return isomorphic(ac1.getType(), ac2.getType());
	}
	else if (node1 instanceof Assignment) {
	    Assignment a1 = (Assignment) node1;
	    Assignment a2 = (Assignment) node2;
	    String op1_str = a1.getOperator().toString();
	    String op2_str = a2.getOperator().toString();
	    if (op1_str==null || op2_str==null) {
		return false;
	    }
	    else {
		return op1_str.equals(op2_str);
	    }
	}
	else if (node1 instanceof ClassInstanceCreation) {
	    ClassInstanceCreation cic1 = (ClassInstanceCreation) node1;
	    ClassInstanceCreation cic2 = (ClassInstanceCreation) node2;
	    return isomorphic(cic1.getType(), cic2.getType());
	}
	else if (node1 instanceof InfixExpression) {
	    InfixExpression ie1 = (InfixExpression) node1;
	    InfixExpression ie2 = (InfixExpression) node2;
	    String op1_str = ie1.getOperator().toString();
	    String op2_str = ie2.getOperator().toString();
	    if (op1_str==null || op2_str==null){
		return false;
	    }
	    else {
		return op1_str.equals(op2_str);
	    }
	}
	else if (node1 instanceof PostfixExpression) {
	    PostfixExpression pe1 = (PostfixExpression) node1;
	    PostfixExpression pe2 = (PostfixExpression) node2;
	    String op1_str = pe1.getOperator().toString();
	    String op2_str = pe2.getOperator().toString();
	    if (op1_str==null || op2_str==null){
		return false;
	    }
	    else {
		return op1_str.equals(op2_str);
	    }
	}
	else if (node1 instanceof PrefixExpression) {
	    PrefixExpression pe1 = (PrefixExpression) node1;
	    PrefixExpression pe2 = (PrefixExpression) node2;
	    String op1_str = pe1.getOperator().toString();
	    String op2_str = pe2.getOperator().toString();
	    if (op1_str==null || op2_str==null){
		return false;
	    }
	    else {
		return op1_str.equals(op2_str);
	    }
	}
	else if (node1 instanceof MethodDeclaration) {
	    MethodDeclaration md1 = (MethodDeclaration) node1;
	    MethodDeclaration md2 = (MethodDeclaration) node2;
	    String md1_name_str = md1.getName().toString();
	    String md2_name_str = md2.getName().toString();	    
	    return md1_name_str.equals(md2_name_str);
	}
	else if (node1 instanceof MethodInvocation) {
	    MethodInvocation mi1 = (MethodInvocation) node1;
	    MethodInvocation mi2 = (MethodInvocation) node2;
	    String mi1_name_str = mi1.getName().toString();
	    String mi2_name_str = mi2.getName().toString();
	    return mi1_name_str.equals(mi2_name_str);
	}
	else if (node1 instanceof ArrayType) {
	    ArrayType at1 = (ArrayType) node1;
	    ArrayType at2 = (ArrayType) node2;
	    if (at1.dimensions().size() == at2.dimensions().size()) {
		return isomorphic(at1.getElementType(), at2.getElementType());
	    }
	    else {
		return false;
	    }
	}
	else if (node1 instanceof PrimitiveType) {
	    PrimitiveType pt1 = (PrimitiveType) node1;
	    PrimitiveType pt2 = (PrimitiveType) node2;
	    String pt1_str = pt1.toString();
	    String pt2_str = pt2.toString();
	    return pt1_str.equals(pt2_str);
	}
	else if (node1 instanceof QualifiedType) {
	    QualifiedType qt1 = (QualifiedType) node1;
	    QualifiedType qt2 = (QualifiedType) node2;
	    String qt1_str = qt1.toString();
	    String qt2_str = qt2.toString();
	    return qt1_str.equals(qt2_str);
	}
	else if (node1 instanceof SimpleName) {
	    SimpleName sn1 = (SimpleName) node1;
	    SimpleName sn2 = (SimpleName) node2;
	    String sn1_str = sn1.toString();
	    String sn2_str = sn2.toString();
	    return sn1_str.equals(sn2_str);
	}
	else if (node1 instanceof SimpleType) {
	    SimpleType st1 = (SimpleType) node1;
	    SimpleType st2 = (SimpleType) node2;
	    String st1_str = st1.toString();
	    String st2_str = st2.toString();
	    return st1_str.equals(st2_str);
	}
	else {
	    //As long as the node types are identical
	    return true;
	}
    }
}
