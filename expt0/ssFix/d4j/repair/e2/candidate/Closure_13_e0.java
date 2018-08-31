package graph;

/* See restrictions in Graph.java. */

import java.util.Arrays;
import java.util.Collection;
import java.util.Queue;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.ArrayDeque;
/** Implements a generalized traversal of a graph.  At any given time,
 *  there is a particular collection of untraversed vertices---the "fringe."
 *  Traversal consists of repeatedly removing an untraversed vertex
 *  from the fringe, visting it, and then adding its untraversed
 *  successors to the fringe.
 *
 *  Generally, the client will extend Traversal.  By overriding the visit
 *  method, the client can determine what happens when a node is visited.
 *  By supplying an appropriate type of Queue object to the constructor,
 *  the client can control the behavior of the fringe. By overriding the
 *  shouldPostVisit and postVisit methods, the client can arrange for
 *  post-visits of a node (as in depth-first search).  By overriding
 *  the reverseSuccessors and processSuccessor methods, the client can control
 *  the addition of neighbor vertices to the fringe when a vertex is visited.
 *
 *  Traversals may be interrupted or restarted, remembering the previously
 *  marked vertices.
 *  @author
 */
public abstract class Traversal {

    /** A Traversal of G, using FRINGE as the fringe. */
    protected Traversal(Graph G, Queue<Integer> fringe) {
        _G = G;
        _fringe = fringe;
        _numbersInMark = new ArrayList<Integer>();
    }

    /** Unmark all vertices in the graph. */
    public void clear() {
        // FIXME
        _numbersInMark.clear();

    }

    /** Initialize the fringe to V0 and perform a traversal. */
    public void traverse(Collection<Integer> V0) {
        // FIXME

        if (_fringe instanceof java.util.LinkedList) {
            
            LinkedList<Integer> _fringe1 = new LinkedList<Integer>();
            for (int i : V0) {
                _fringe1.add(i);
            }




            while(!_fringe1.isEmpty()) {
                int vertex = _fringe1.poll();    



                if (!marked(vertex)) {
                    mark(vertex);
                    visit(vertex);
                    for (int successor : _G.successors(vertex)) {
                        if (processSuccessor(vertex, successor)) {
                            _fringe1.add(successor);
                        }
                    }
                }
            }

            



        } else if (_fringe instanceof java.util.ArrayDeque) { //This is instance of java.util.ArrayDeque
            
            ArrayDeque<Integer> _fringe2 = new ArrayDeque<Integer>();
            for (int i : V0) {
                _fringe2.add(i);
            }


            while(!_fringe2.isEmpty()) {
                int vertex = _fringe2.pop();

                if (!marked(vertex)) {
                    mark(vertex);
                    visit(vertex);
                    for (int successor : _G.successors(vertex)) {
                        if (processSuccessor(vertex, successor)) {
                            _fringe2.push(successor);
                        }
                    }
                }
            }
        } else if (_fringe instanceof java.util.PriorityQueue) {


            // System.out.println("==================");

            // for (int vvv : _fringe) {
                
            //     System.out.print("I am now with index: " + vvv + "with distance :");
            //     System.out.println();
            // }
            // System.out.println(" ");
            // System.out.println("=========================");




            while(!_fringe.isEmpty()) {
                int vertex = _fringe.poll();

            //     System.out.println("I poped out this vertex :" + vertex);

            //                 System.out.println("***********************");

            // for (int vvv : _fringe) {
                
            //     System.out.print("I am now with index: " + vvv + "with distance :");
            //     System.out.println();
            // }
            // System.out.println(" ");
            // System.out.println("***************************");






                if(!marked(vertex)) {
                    mark(vertex);
                    // visit(vertex);
                    if (!visit(vertex)) {
                        break;
                    }

                    // System.out.println("I visited : " + vertex);
                    // for (int successor : _G.successors(vertex)) {
                    //     if (processSuccessor(vertex, successor)) {
                    //         _fringe.add(successor);
                    //     }
                    // }
                }
            }
        }

    }

    /** Initialize the fringe to { V0 } and perform a traversal. */
    public void traverse(int v0) {
        traverse(Arrays.<Integer>asList(v0));
    }

    /** Returns true iff V has been marked. */
    protected boolean marked(int v) {
        // FIXME
        if (_numbersInMark.contains(v)) {
            return true;
        }
        return false;
    }

    /** Mark vertex V. */
    protected void mark(int v) {
        // FIXME
        _numbersInMark.add(v);
    }

    /** Perform a visit on vertex V.  Returns false iff the traversal is to
     *  terminate immediately. */
    protected boolean visit(int v) {
        return true;
    }

    /** Return true if we should postVisit V after traversing its
     *  successors.  (Post-visiting generally is useful only for depth-first
     *  traversals, although we define it for all traversals.) */
    protected boolean shouldPostVisit(int v) {
        return false;
    }

    /** Revisit vertex V after traversing its successors.  Returns false iff
     *  the traversal is to terminate immediately. */
    protected boolean postVisit(int v) {
        return true;
    }

    /** Return true if we should schedule successors of V in reverse order. */
    protected boolean reverseSuccessors(int v) {
        return false;
    }

    /** Process successor V to U.  Returns true iff V is then to
     *  be added to the fringe.  By default, returns true iff V is unmarked. */
    protected boolean processSuccessor(int u, int v) {
        return !marked(v);
    }

    /** The graph being traversed. */
    private final Graph _G;
    /** The fringe. */
    protected final Queue<Integer> _fringe;
    // FIXME
    private ArrayList<Integer> _numbersInMark;


}
