/*
 *  Copyright (c) 2015. markus endres, timotheus preisinger
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package spo;

import org.jgrapht.Graph;
import org.jgrapht.util.TypeUtil;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;

/**
 * <p/>
 * Reimplementation of DepthFirstIteratorEdge in JGraphT. The original version is from Barak Naveh and Liviu Rau.
 * <p/>
 * <p/>
 * A depth-first iterator for a directed and an undirected graph. For this
 * iterator to work correctly the graph must not be modified during iteration.
 * Currently there are no means to ensure that, nor to fail-fast. The results of
 * such modifications are undefined.
 *
 * @author Liviu Rau
 * @author Barak Naveh
 * @since Jul 29, 2003
 * </p>
 */
public class DepthFirstIteratorEdge<V, E> extends CrossComponentIteratorEdge<V, E, CrossComponentIteratorEdge.VisitColorEdge> {

    /**
     * Sentinel object. Unfortunately, we can't use null, because ArrayDeque
     * won't accept those. And we don't want to rely on the caller to provide a
     * sentinel object for us. So we have to play typecasting games.
     */
    public static final Object SENTINEL = new Object();

    /**
     * @see #getStack
     */
    private Deque<Object> stack = new ArrayDeque<Object>();
    private Deque<Object> stackEdge = new ArrayDeque<Object>();

    private transient TypeUtil<V> vertexTypeDecl = null;
    private transient TypeUtil<E> edgeTypeDecl = null;
    private boolean leer = false;


    /**
     * Creates a new depth-first iterator for the specified graph.
     *
     * @param g the graph to be iterated.
     */
    public DepthFirstIteratorEdge(Graph<V, E> g, int type) {
        this(g, null, type);
    }

    /**
     * Creates a new depth-first iterator for the specified graph. Iteration
     * will start at the specified start vertex and will be limited to the
     * connected component that includes that vertex. If the specified start
     * vertex is <code>null</code>, iteration will start at an arbitrary vertex
     * and will not be limited, that is, will be able to traverse all the graph.
     *
     * @param g           the graph to be iterated.
     * @param startVertex the vertex iteration to be started.
     */
    public DepthFirstIteratorEdge(Graph<V, E> g, V startVertex, int type) {
        super(g, startVertex, type);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    protected boolean isConnectedComponentExhausted() {
        for (; ; ) {
            if (stack.isEmpty()) {
                return true;
            }
            if (stack.getLast() != SENTINEL) {
                // Found a non-sentinel.
                return false;
            }

            // Found a sentinel: pop it, record the finish time,
            // and then loop to check the rest of the stack.

            // Pop null we peeked at above.
            stack.removeLast();

            // This will pop corresponding vertex to be recorded as finished.
            recordFinish();
        }
    }

    public HashMap<E, Integer> getEdge_evaluator() {
        return edge_evaluator;
    }

    protected boolean isConnectedComponentExhaustedEdge() {
        for (; ; ) {
            if (stackEdge.isEmpty()) {
                return true;
            }
            if (stackEdge.getLast() != SENTINEL) {
                // Found a non-sentinel.
                return false;
            }

            // Found a sentinel: pop it, record the finish time,
            // and then loop to check the rest of the stack.

            // Pop null we peeked at above.
            stackEdge.removeLast();

            // This will pop corresponding vertex to be recorded as finished.
            recordFinishEdge();
        }
    }

    public int getCounter() {
        return counter;
    }


    protected void encounterVertex(V vertex, E edge) {


        if (edge != null) {

            putSeenEdge(edge, VisitColorEdge.WHITE);
            stackEdge.addLast(edge);
        }
        putSeenData(vertex, VisitColorEdge.WHITE);
        stack.addLast(vertex);
    }


    protected void encounterVertexAgain(V vertex, E edge) {
        VisitColorEdge color = getSeenData(vertex);
        if (color != VisitColorEdge.WHITE) {
            // We've already visited this vertex; no need to mess with the
            // stack (either it's BLACK and not there at all, or it's GRAY
            // and therefore just a sentinel).
            return;
        }

        // Since we've encountered it before, and it's still WHITE, it
        // *must* be on the stack. Use removeLastOccurrence on the
        // assumption that for typical topologies and traversals,
        // it's likely to be nearer the top of the stack than
        // the bottom of the stack.
        boolean found = stack.removeLastOccurrence(vertex);
        assert (found);
        stack.addLast(vertex);
    }

    protected void encounterEdgeAgain(E edge) {
        VisitColorEdge color = getSeenDataEdge(edge);
        if (color != VisitColorEdge.WHITE) {
            // We've already visited this edge; no need to mess with the
            // stack (either it's BLACK and not there at all, or it's GRAY
            // and therefore just a sentinel).
            return;
        }

        // Since we've encountered it before, and it's still WHITE, it
        // *must* be on the stack. Use removeLastOccurrence on the
        // assumption that for typical topologies and traversals,
        // it's likely to be nearer the top of the stack than
        // the bottom of the stack.
        boolean found = stackEdge.removeLastOccurrence(edge);
        assert (found);
        stackEdge.addLast(edge);
    }


    protected V provideNextVertex() {
        V v;
        for (; ; ) {
            Object o = stack.removeLast();
            if (o == SENTINEL) {
                // This is a finish-time sentinel we previously pushed.
                recordFinish();
                // Now carry on with another pop until we find a non-sentinel
            } else {
                // Got a real vertex to start working on
                v = TypeUtil.uncheckedCast(o, vertexTypeDecl);
                break;
            }
        }

        // Push a sentinel for v onto the stack so that we'll know
        // when we're done with it.
        stack.addLast(v);
        stack.addLast(SENTINEL);
        putSeenData(v, VisitColorEdge.GRAY);
        return v;
    }

    protected E provideNextEdge() {
        E e;

        for (; ; ) {
            if (stackEdge.size() > 0) {
                Object o = stackEdge.removeLast();
                if (o == SENTINEL) {
                    // This is a finish-time sentinel we previously pushed.
                    recordFinishEdge();
                    // Now carry on with another pop until we find a
                    // non-sentinel
                } else {
                    // Got a real vertex to start working on
                    e = TypeUtil.uncheckedCast(o, edgeTypeDecl);
                    break;
                }
            } else
                leer = true;
        }

        if (!leer) {
            // Push a sentinel for v onto the stack so that we'll know
            // when we're done with it.
            stackEdge.addLast(e);
            stackEdge.addLast(SENTINEL);
            putSeenEdge(e, VisitColorEdge.GRAY);
            return e;
        } else
            return null;
    }

    private void recordFinish() {
        V v = TypeUtil.uncheckedCast(stack.removeLast(), vertexTypeDecl);

        putSeenData(v, VisitColorEdge.BLACK);
        finishVertex(v);
    }

    private void recordFinishEdge() {
        E e = TypeUtil.uncheckedCast(stackEdge.removeLast(), edgeTypeDecl);

        putSeenEdge(e, VisitColorEdge.BLACK);

    }

    /**
     * Retrieves the LIFO stack of vertices which have been encountered but not
     * yet visited (WHITE). This stack also contains <em>sentinel</em> entries
     * representing vertices which have been visited but are still GRAY. A
     * sentinel entry is a sequence (v, SENTINEL), whereas a non-sentinel entry
     * is just (v).
     *
     * @return stack
     */
    public Deque<Object> getStack() {
        return stack;
    }
}

