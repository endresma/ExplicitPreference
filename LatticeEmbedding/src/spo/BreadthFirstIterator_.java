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

import java.util.*;

/**
 * This is a re-implementation of the class BreadthFirstIterator in JGraphT, because we need some special constructs.
 * The original version is from Barak Naveh.
 * <p/>
 * A breadth-first iterator for a directed and an undirected graph. For this
 * iterator to work correctly the graph must not be modified during iteration.
 * Currently there are no means to ensure that, nor to fail-fast. The results of
 * such modifications are undefined.
 *
 * @author Barak Naveh
 * @since Jul 19, 2003
 * </p>
 */
public class BreadthFirstIterator_<V, E> extends CrossComponentIteratorEdge<V, E, CrossComponentIteratorEdge.VisitColorEdge> {

    private static boolean containEqualSignatures;
    private static boolean equalFirstRun;
    private final Object SENTINEL = new Object();
    protected int countLevel = -1;
    // standardVerfahren(ohne Knoten mit gleichen Signaturen)
    private HashMap<V, ArrayList<Integer>> resultTuple = new HashMap<V, ArrayList<Integer>>();
    private Deque<Object> queue = new ArrayDeque<Object>();
    private transient TypeUtil<V> vertexTypeDecl = null;
    private int max_depth = 0;
    private HashMap<Object, HashMap<Object, Integer>> depthResult;
    // temporaer (jeweils innerhalb 1 Level) speichern den minimalen Index fuer
    // Knoten, die mehr als eine eingehende Kante haben
    private HashMap<V, Integer> minIndexes;
    private HashMap<ArrayList<Integer>, ArrayList<V>> nodesEqualSignatureOneLevel;

    private int minValue;
    private boolean firstLevel = false;

    private int SIGNATURE_METHOD = -1;


    /**
     * Creates a new breadth-first iterator for the specified graph.
     *
     * @param g the graph to be iterated.
     */
    public BreadthFirstIterator_(Graph<V, E> g, int type, int maxDepth, HashMap<Object, HashMap<Object, Integer>> depthResult, int signatureMethod) {
        this(g, null, type, maxDepth, depthResult, signatureMethod);


    }

    /**
     * Creates a new breadth-first iterator for the specified graph. Iteration
     * will start at the specified start vertex and will be limited to the
     * connected component that includes that vertex. If the specified start
     * vertex is <code>null</code>, iteration will start at an arbitrary vertex
     * and will not be limited, that is, will be able to traverse all the graph.
     *
     * @param g           the graph to be iterated.
     * @param startVertex the vertex iteration to be started.
     */
    @SuppressWarnings("unchecked")
    public BreadthFirstIterator_(Graph<V, E> g, V startVertex, int type, int max_depth, HashMap<Object, HashMap<Object, Integer>> depthResult, int methode) {
        super(g, startVertex, type);
        containEqualSignatures = false;
        equalFirstRun = false;
        this.SIGNATURE_METHOD = methode;
        queue.add(SENTINEL);
        this.max_depth = max_depth;
        this.depthResult = depthResult;
        minIndexes = new HashMap<V, Integer>();
        nodesEqualSignatureOneLevel = new HashMap<ArrayList<Integer>, ArrayList<V>>();

        // oben: (0,...0);
        ArrayList<Integer> array = new ArrayList<Integer>();
        for (int i = 0; i < getMax_depth(); i++) {
            array.add(i, 0);
        }
        resultTuple.put((V) OrderedGraph.VIRTUAL_TOPNODE, array);

    }

    public int getMax_depth() {
        return max_depth;
    }


    protected boolean isConnectedComponentExhausted() {
        if (queue.size() == 1) {
            Object o = queue.removeFirst();
            if (o == SENTINEL) {
            	if(resultTuple.containsKey(OrderedGraph.VIRTUAL_TOPNODE)){
            		resultTuple.remove(OrderedGraph.VIRTUAL_TOPNODE);
            	}
            	if(resultTuple.containsKey(OrderedGraph.VIRTUAL_BOTTOMNODE)){
            		resultTuple.remove(OrderedGraph.VIRTUAL_BOTTOMNODE);
            	}
                return true;
            } else {
                V v = TypeUtil.uncheckedCast(o, vertexTypeDecl);
                queue.add(v);
            }
        }
        return queue.isEmpty();
    }

    protected void encounterVertex(V vertex, E edge) {

        if (!queue.contains(vertex)) {
            putSeenData(vertex, null);
            queue.add(vertex);
        }
    }

    protected void encounterVertexAgain(V vertex, E edge) {
    }

    protected V provideNextVertex() {
        V v = null;
        for (; ; ) {
            Object o = queue.removeFirst();
            if (o == SENTINEL) {
                changeSignature();
                // remove minimal indexes after each niveau
                if (!minIndexes.isEmpty()) {
                    minIndexes = new HashMap<V, Integer>();
                }

                if (queue.size() > 0) {
                    countLevel++;
                }
                queue.add(SENTINEL);
            } else {
                v = TypeUtil.uncheckedCast(o, vertexTypeDecl);

                if (!v.equals(OrderedGraph.VIRTUAL_BOTTOMNODE) && !v.equals(OrderedGraph.VIRTUAL_TOPNODE)) {
                    // no nodes with equal signatures --> use standard method
                    if (!containEqualSignatures && !equalFirstRun) {
                        resultTuple = setLevel(v, resultTuple);
                        if (!firstLevel && resultTuple.containsKey(OrderedGraph.VIRTUAL_TOPNODE) && countLevel > 1) {
                            resultTuple.remove(OrderedGraph.VIRTUAL_TOPNODE);
                            firstLevel = true;
                        }

                    }
                    // there are at least one pair of nodes with same signatures --> use non-standard method
                    else if (containEqualSignatures && equalFirstRun) {
                        if (SIGNATURE_METHOD == OrderedGraph.MIN) {
                            resultTuple = setLevel(v, resultTuple);
                        }
                        if (SIGNATURE_METHOD == OrderedGraph.RANDOM) {
                            resultTuple = setLevel(v, resultTuple);
                        }
                    }
                }
                break;
            }

        }
        return v;
    }


    /**
     * Check if there are groups with equal signatures.
     * If so, change them using the given signature computation method MIN or RANDOM.
     */
    private void changeSignature() {

        // standard method
        if (!containEqualSignatures && !equalFirstRun && !nodesEqualSignatureOneLevel.isEmpty()) {
            changeSignatureStandard();
        }

        // if groups with equal signatures exists
        else if (containEqualSignatures && equalFirstRun) {

            if (SIGNATURE_METHOD == OrderedGraph.MIN) {
                if (!nodesEqualSignatureOneLevel.isEmpty()) {
                    changeSignatureMin();
                }
            }

            if (SIGNATURE_METHOD == OrderedGraph.RANDOM) {
                if (!nodesEqualSignatureOneLevel.isEmpty()) {
                    changeSignatureRandom();
                }
            }

        }
    }

    private void changeSignatureStandard() {
        for (ArrayList<Integer> signature : nodesEqualSignatureOneLevel.keySet()) {

            // falls eine Gruppe enthaelt mindestens 2 Elemente mit gleiche
            // Signatur--> Signaturen veraendern innerhalb jeder Gruppe
            if (nodesEqualSignatureOneLevel.get(signature).size() > 1) {
                ArrayList<V> nodesEqualSignaturOneLvl = nodesEqualSignatureOneLevel.get(signature);

                if (!containEqualSignatures) {
                    // further use not a standard method
                    containEqualSignatures = true;
                }


                // wird nur bei Uebergang von standard_Methode zu einer bzw.
                // beiden methode/n ausgefuert
                if (containEqualSignatures && !equalFirstRun) {
                    fillSets();
                }
                if (SIGNATURE_METHOD == OrderedGraph.MIN) {
                    resetSignaturesMinIndex(nodesEqualSignaturOneLvl);
                }
                if (SIGNATURE_METHOD == OrderedGraph.RANDOM) {
                    resetSignaturesRandomIndex(nodesEqualSignaturOneLvl, signature);
                }
                equalFirstRun = true;
            }
        }
        // alle Gruppen dises Niveau sind gespeichert
        nodesEqualSignatureOneLevel = new HashMap<ArrayList<Integer>, ArrayList<V>>();
    }

    private void changeSignatureMin() {
        for (ArrayList<Integer> signature : nodesEqualSignatureOneLevel.keySet()) {
            // falls eine Gruppe enthaelt mindestens 2 Elemente mit gleiche
            // Signatur--> Signaturen veraendern innerhalb jeder Gruppe
            if (nodesEqualSignatureOneLevel.get(signature).size() > 1) {
                ArrayList<V> nodesEqualSignaturOneLvl = nodesEqualSignatureOneLevel.get(signature);
                resetSignaturesMinIndex(nodesEqualSignaturOneLvl);
            }
        }
        nodesEqualSignatureOneLevel = new HashMap<ArrayList<Integer>, ArrayList<V>>();
    }

    private void changeSignatureRandom() {
        for (ArrayList<Integer> signature : nodesEqualSignatureOneLevel.keySet()) {
            if (nodesEqualSignatureOneLevel.get(signature).size() > 1) {
                ArrayList<V> nodesEqualSignaturOneLvl = nodesEqualSignatureOneLevel.get(signature);
                resetSignaturesRandomIndex(nodesEqualSignaturOneLvl, signature);
            }
        }
        nodesEqualSignatureOneLevel = new HashMap<ArrayList<Integer>, ArrayList<V>>();
    }

    // kopiere alles, was bereits in resultTuple gespeichert ist
    private void fillSets() {
        Iterator<V> it1 = resultTuple.keySet().iterator();
        while (it1.hasNext()) {
            V vertex = it1.next();

            if (SIGNATURE_METHOD == OrderedGraph.MIN) {
                ArrayList<Integer> temp_min = new ArrayList<Integer>();
                for (int o = 0; o < resultTuple.get(vertex).size(); o++) {
                    temp_min.add(o, resultTuple.get(vertex).get(o));
                }
                resultTuple.put(vertex, temp_min);
            }
            if (SIGNATURE_METHOD == OrderedGraph.RANDOM) {
                ArrayList<Integer> temp_rand = new ArrayList<Integer>();
                for (int o = 0; o < resultTuple.get(vertex).size(); o++) {
                    temp_rand.add(o, resultTuple.get(vertex).get(o));
                }
                resultTuple.put(vertex, temp_rand);
            }

        }

    }


    //MIN_METHOD
    private void resetSignaturesMinIndex(ArrayList<V> nodesEqualSignaturOneLvl) {

        for (int i = 0; i < nodesEqualSignaturOneLvl.size(); i++) {
            V v = nodesEqualSignaturOneLvl.get(i);
            int index = minIndexes.get(v) - 1;
            ArrayList<Integer> list = new ArrayList<Integer>();
            list = resultTuple.get(v);
            list.set(index, list.get(index) + 1);
            resultTuple.put(v, list);
        }

    }


    // RANDOM_METHOD
    private void resetSignaturesRandomIndex(ArrayList<V> nodesEqualSignaturOneLvl, ArrayList<Integer> signature) {
        HashSet<Integer> not_zero_positions = notZeroPositions(signature);
        int j = 0;
        Iterator<Integer> it = not_zero_positions.iterator();
        for (int i = 0; i < nodesEqualSignaturOneLvl.size(); i++) {
            ArrayList<Integer> new_signature = new ArrayList<Integer>();
            new_signature.addAll(signature);
            if (it.hasNext()) {
                Integer next_position = it.next();
                new_signature.set(next_position, new_signature.get(next_position) + 1);
            } else {
                while (not_zero_positions.contains(new Integer(j))) {
                    j++;
                }
                new_signature.set(j, new_signature.get(j) + 1);
            }
            resultTuple.put(nodesEqualSignaturOneLvl.get(i), new_signature);
        }

    }

    // bestimme Positionen, wo keine Nullen stehen
    private HashSet<Integer> notZeroPositions(ArrayList<Integer> array) {
        HashSet<Integer> notZero = new HashSet<Integer>();
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i).intValue() != 0) {
                notZero.add(i);
            }
        }
        return notZero;
    }


    private int defineMinValue(V v, Object vertex, int vorherige_minimum) {
        int min = vorherige_minimum;
        if (depthResult.get(v).get(vertex) != 0 && depthResult.get(v).get(vertex) < min) {
            min = depthResult.get(v).get(vertex);
        }
        return min;
    }


    private HashMap<V, ArrayList<Integer>> setLevel(V v,/* int count,*/
                                                    HashMap<V, ArrayList<Integer>> resultTupleMinIndex) {
        HashMap<V, ArrayList<Integer>> results = resultTupleMinIndex;

        // mehrere Eingangskanten
        if (depthResult.get(v).size() > 1) {
            minValue = Integer.MAX_VALUE;

            // method min-indexes
            ArrayList<Integer> arrayMin = new ArrayList<Integer>();

            boolean tupleNotAvailable = false;
            for (int i = 0; i < getMax_depth(); i++) {
                arrayMin.add(i, new Integer(0));
                int maxValue_minIndex = 0;
                for (Object vertex : depthResult.get(v).keySet()) {
                    minValue = defineMinValue(v, vertex, minValue);
                    if (!results.containsKey(vertex)) {
                        tupleNotAvailable = true;
                        break;
                    } else if (results.get(vertex).get(i) > maxValue_minIndex) {
                        maxValue_minIndex = results.get(vertex).get(i);
                    }
                }
                arrayMin.set(i, new Integer(maxValue_minIndex));
                if (tupleNotAvailable) {
                    break;
                }
            }
            // fuege zu minIndizies minimale Index fuer Knoten v
            // hinzu
            if (!minIndexes.containsKey(v)) {
                minIndexes.put(v, minValue);
            }
            if (tupleNotAvailable) {
            } else {
                results.put(v, arrayMin);
            }
            putGroup(arrayMin, v);

        }
        // eine Eingangskante
        else {
            for (Object vertex : depthResult.get(v).keySet()) {
                int index = ((Integer) (depthResult.get(v).get(vertex) - 1)).intValue();

                ArrayList<Integer> arrayMin = new ArrayList<Integer>();

                // vertex signatur: results.get(vertex)
                for (int j = 0; j < results.get(vertex).size(); j++) {
                    Integer zahl = new Integer(results.get(vertex).get(j));
                    if (j != index) {
                        arrayMin.add(j, zahl);
                    } else {
                        arrayMin.add(j, zahl + 1);
                    }
                }
                results.put(v, arrayMin);
                putGroup(arrayMin, v);
            }
        }
        return results;
    }


    private void putGroup(ArrayList<Integer> array, V v) {
        if (!nodesEqualSignatureOneLevel.containsKey(array)) {
            ArrayList<V> listOfNodes = new ArrayList<V>();
            listOfNodes.add(v);
            nodesEqualSignatureOneLevel.put(array, listOfNodes);
        } else {

            nodesEqualSignatureOneLevel.get(array).add(v);
        }
    }


    public HashMap<V, ArrayList<Integer>> getResultTuple() {
        return resultTuple;
    }

    public boolean isContainEqualSignatures() {
        return containEqualSignatures;
    }

    public HashMap<Object, HashMap<Object, Integer>> getDepthResult() {
        return depthResult;
    }


    @Override
    protected boolean isConnectedComponentExhaustedEdge() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected E provideNextEdge() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void encounterEdgeAgain(E edge) {
        // TODO Auto-generated method stub

    }
}
