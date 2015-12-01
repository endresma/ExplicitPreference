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

package util;

import flatlc.levels.FlatLevelCombination;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.BellmanFordShortestPath;
import org.jgrapht.graph.DefaultEdge;
import spo.OrderedGraph;

/**
 * <p/>
 * Implements an explicit preference based on an arbitrary strict partial order.
 * <p/>
 * User: endresma
 * Date: 23.09.15
 * Time: 14:38
 */
public class ExplicitPreference implements IPreference {


    /**
     * <p>
     * JGraphT models the EXPLICIT preference graph
     * and detects cycles. If the graph contains a path from
     * a node G to a node L, the semantics are that G is greater than L
     * and L is less than G.
     * </p>
     */
    private DirectedGraph<Object, DefaultEdge> graph;


    public ExplicitPreference(OrderedGraph graph) {

        this.graph = graph.getgraph();

    }

    public int compare(Object objA, Object objB) {


        if (objA == null || objB == null) {
            if (objA == null && objB != null) {
                return IPreference.LESS;
            } else if (objA != null && objB == null) {
                return IPreference.GREATER;
            }
        }

        //        Object A = objA;
        //        Object B = objB;

        Object A = ((FlatLevelCombination) objA).getLevelCombination()[0];
        Object B = ((FlatLevelCombination) objB).getLevelCombination()[0];


        if (A.equals(B)) {
            return IPreference.EQUAL;
        }
        //        else if (getSVRelation().isSubstitutable(A, B)) {
        //            //values can be substitutable even if they are not in the graph
        //            return IPreference.IPreference.SUBSTITUTABLE;
        //        }

        boolean containsA = graph.containsVertex(A);
        boolean containsB = graph.containsVertex(B);


        //Strings are not equal and not substitutable if we are here
        if (containsA && containsB) {
            boolean hasPathAB = BellmanFordShortestPath.findPathBetween(graph, A, B) != null;
            boolean hasPathBA = BellmanFordShortestPath.findPathBetween(graph, B, A) != null;

            // both objects in range
            if (hasPathBA) {
                //B -> ... -> A <=> A < B
                return IPreference.LESS;
            } else if (hasPathAB) {
                //A -> ... -> B <=> A > B
                return IPreference.GREATER;
            }

            return IPreference.UNRANKED;
        } else if (containsA) {
            // only A in range
            return IPreference.GREATER;
        } else if (containsB) {
            // only B in range
            return IPreference.LESS;
        }

        return IPreference.UNRANKED;
    }


}
