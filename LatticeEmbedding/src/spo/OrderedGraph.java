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

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.GraphIterator;

import java.util.*;

/**
 * Class to represent a general strict partial order as a Hasse diagram, BTG.
 */

public class OrderedGraph {

	/**
	 * Two methods for signature computation. MIN: use the method presented in
	 * the paper Beyond Skylines: Excplicit Preferences, i.e. use the minimal
	 * incoming edge for signature determination RANDOM: use two random but
	 * different positions to increase the signature value
	 */
	public final static int MIN = 0;
	public final static int RANDOM = 1;

	protected final static String VIRTUAL_TOPNODE = "topNode";
	protected final static String VIRTUAL_BOTTOMNODE = "bottomNode";

	/**
	 * true: The preference order is given by OrderedPairs false: The preference
	 * order is given by NodeLists
	 */
	protected boolean standardGraph = true;
	/**
	 * There are several different graphs given by OrderdPairs
	 */
	protected boolean severalOrders = false;

	/**
	 * Method used to compute the signature. MIN: RANDOM:
	 */
	protected int SIGNATURE_METHOD = -1;

	NodeListsOrder nodeListsOrder;

	private Object currentOrder = null;
	/**
	 * List of combined keys when there are several different orders.
	 */
	private ArrayList<Object[]> combinedKeys = new ArrayList<Object[]>();

	/**
	 * hier alle signaturen kombiniert: [(key_1,..,key_n)
	 * [signatur_1,...,signatur_n]]
	 */
	private HashMap<Object[], int[]> combinedSignatures = new HashMap<>();
	private Map<Object, int[]> maxSignatureValues = new HashMap<>();
	/**
	 * signatures for each order
	 */
	private HashMap<ArrayList<Object>, ArrayList<Integer>> signatureAssignments = new HashMap<>();
	private HashMap<Object, Object> orderSignatures = new HashMap<>();

	/**
	 * several orders
	 */

	private Object[] orders;
	private OrderedPairOrder orderedPairOrder = null;
	/**
	 * input tuples to graph
	 */
	private DirectedGraph<Object, DefaultEdge> graph = null;

	/**
	 * original graph(s)
	 */
	private ArrayList<DirectedGraph<Object, DefaultEdge>> originalGraph;
	/**
	 * vertex-set
	 */
	private Set<Object> vertSet;
	private Set<Object> children;
	private Set<Object> parents;
	private Set<Object> tops;
	private Set<Object> bottoms;
	/**
	 * array with max-values for each not-zero position (STANDARD_ METHOD)
	 */
	private int maxValuesArray[];

	/**
	 * HashMap with depth_search results [target-vertex, [sourse-vertex,
	 * count-value]]
	 */
	private HashMap<Object, HashMap<Object, Integer>> depthResult;
	/**
	 * HashMap after breath_search with the node signatures: [node, Array-List
	 * signature] (STANDARD_METHOD)
	 */
	private HashMap<Object, ArrayList<Integer>> signatures;

	/**
	 * length of signature
	 */
	private int length = -1;
	/**
	 * use standard_method as default
	 */

	private int signatureLength = 0;

	/**
	 * ctor: Create graph from orders pairs.
	 *
	 * @param signatureMethod
	 * @param orderedPairOrder
	 */
	public OrderedGraph(int signatureMethod, OrderedPairOrder orderedPairOrder) {
		this(signatureMethod);
		this.orderedPairOrder = orderedPairOrder;
		init();

	}

	/**
	 * ctor: Create graph from list of nodes.
	 *
	 * @param signatureMethod
	 * @param listenpaarOrder
	 */
	public OrderedGraph(int signatureMethod, NodeListsOrder listenpaarOrder) {
		this(signatureMethod);
		this.nodeListsOrder = listenpaarOrder;
		standardGraph = false;
		init();

	}

	/**
	 * ctor: Create graph from several orders.
	 *
	 * @param signatureMethod
	 * @param orders
	 */
	public OrderedGraph(int signatureMethod, Object... orders) {
		this(signatureMethod);

		this.orders = orders;
		standardGraph = false;
		severalOrders = true;
		combinedKeys = new ArrayList<>();
		init();
		combineSignatures();

	}

	private OrderedGraph(int signatureMethod) {
		this.SIGNATURE_METHOD = signatureMethod;
		graph = new DefaultDirectedGraph<>(DefaultEdge.class);
		vertSet = new HashSet<>();
		children = new HashSet<>();
		parents = new HashSet<>();
		originalGraph = new ArrayList<>();

	}

	public static void main(String[] args) {
		// Eingabe als eine Liste von Tuple (a,b), wobei b ist schlechter als a
		// alleinstehende Knote als: (null, Knoten) eingeben !!!!!!

		// ArrayList<OrderedPair> orderedPairs = new ArrayList<OrderedPair>();

		// System.out.println("Example 4, Fig. 4");
		// ArrayList<OrderedPair> orderedPairs = new ArrayList<OrderedPair>();
		// OrderedPair a1= new OrderedPair ("a", "b");
		// OrderedPair a2 = new OrderedPair("b", "c");
		// OrderedPair a3 = new OrderedPair("d", "e");
		// OrderedPair a4 = new OrderedPair("e", "f");
		// OrderedPair a5 = new OrderedPair("b", "f");
		// OrderedPair a6 = new OrderedPair(null, "g");
		// orderedPairs.add(a1);
		// orderedPairs.add(a2);
		// orderedPairs.add(a3);
		// orderedPairs.add(a4);
		// orderedPairs.add(a5);
		// orderedPairs.add(a6);
		// OrderedPairOrder o_order= new OrderedPairOrder(orderedPairs);
		// OrderedGraph graph= new OrderedGraph(USE_MIN_AND_RANDOM,o_order);
		// ##############################################################################################################################

		// System.out.println("Example 5, Fig. 6");
		ArrayList<OrderedPair> orderedPairs = new ArrayList<>();
		OrderedPair a1 = new OrderedPair(1, 2);
		OrderedPair a2 = new OrderedPair(null, 0);
		orderedPairs.add(a1);
		orderedPairs.add(a2);
		OrderedPairOrder o_order = new OrderedPairOrder(orderedPairs);
		// OrderedGraph graph = new OrderedGraph(OrderedGraph.MIN, o_order);
		// ##############################################################################################################################

		// System.out.println("Lemma 3, Fig. 8");
		// ArrayList<OrderedPair> orderedPairs = new ArrayList<OrderedPair>();
		// OrderedPair a1 = new OrderedPair("a", "d");
		// OrderedPair a2 = new OrderedPair("b", "d");
		// OrderedPair a3 = new OrderedPair("c", "f");
		// OrderedPair a4 = new OrderedPair("a", "e");
		// OrderedPair a5 = new OrderedPair("b", "f");
		// OrderedPair a6 = new OrderedPair("c", "e");
		// orderedPairs.add(a1);
		// orderedPairs.add(a2);
		// orderedPairs.add(a3);
		// orderedPairs.add(a4);
		// orderedPairs.add(a5);
		// orderedPairs.add(a6);
		// OrderedPairOrder o_order= new OrderedPairOrder(orderedPairs);
		// OrderedGraph graph = new OrderedGraph(USE_MIN_AND_RANDOM,o_order);
		// ##############################################################################################################################

		// System.out.printf("Example 8, Fig. 10");
		// ArrayList<OrderedPair> orderedPairs = new ArrayList<OrderedPair>();
		// OrderedPair a1 = new OrderedPair("red", "yellow");
		// OrderedPair a2 = new OrderedPair("red", "brown");
		// OrderedPair a3 = new OrderedPair("red", "black");
		//
		// OrderedPair a4 = new OrderedPair("blue", "yellow");
		// OrderedPair a5 = new OrderedPair("blue", "brown");
		// OrderedPair a6 = new OrderedPair("blue", "black");
		//
		// OrderedPair a7 = new OrderedPair("yellow", "purple");
		// OrderedPair a8 = new OrderedPair("brown", "purple");
		// OrderedPair a9 = new OrderedPair("black", "purple");
		// orderedPairs.add(a1);
		// orderedPairs.add(a2);
		// orderedPairs.add(a3);
		// orderedPairs.add(a4);
		// orderedPairs.add(a5);
		// orderedPairs.add(a6);
		// orderedPairs.add(a7);
		// orderedPairs.add(a8);
		// orderedPairs.add(a9);
		// OrderedPairOrder o_order= new OrderedPairOrder(orderedPairs);
		// OrderedGraph graph = new OrderedGraph(USE_MIN_AND_RANDOM,o_order);
		// ##############################################################################################################################

		// System.out.printf("Neues Bsp");
		// ArrayList<OrderedPair> orderedPairs = new ArrayList<OrderedPair>();
		// OrderedPair a1 = new OrderedPair("a", "c");
		// OrderedPair a2 = new OrderedPair("a", "d");
		// OrderedPair a3 = new OrderedPair(null, "b");
		// orderedPairs.add(a1);
		// orderedPairs.add(a2);
		// orderedPairs.add(a3);
		// OrderedPairOrder o_order= new OrderedPairOrder(orderedPairs);
		// OrderedGraph graph = new OrderedGraph(USE_MIN_AND_RANDOM,o_order);
		// ##############################################################################################################################

		// System.out.printf("Neues Bsp");
		// ArrayList<OrderedPair> orderedPairs2 = new ArrayList<OrderedPair>();
		// OrderedPair a_1 = new OrderedPair("a", "b");
		// OrderedPair a_2 = new OrderedPair("d", "b");
		// OrderedPair a_3 = new OrderedPair(null, "c");
		// OrderedPair a_4 = new OrderedPair("b", "e");
		// orderedPairs2.add(a_1);
		// orderedPairs2.add(a_2);
		// orderedPairs2.add(a_3);
		// orderedPairs2.add(a_4);
		// OrderedPairOrder o_order2 = new OrderedPairOrder(orderedPairs2);
		// OrderedGraph graph = new OrderedGraph(MIN,
		// o_order,
		// o_order2);
		// ##############################################################################################################################

		// System.out.printf("Neues Bsp");
		// ArrayList<OrderedPair> orderedPairs = new ArrayList<OrderedPair>();
		// OrderedPair a1 = new OrderedPair("a", "d");
		// OrderedPair a2 = new OrderedPair("a", "e");
		// OrderedPair a3 = new OrderedPair("a", "f");
		// OrderedPair a4 = new OrderedPair("b", "d");
		// OrderedPair a5 = new OrderedPair("b", "e");
		// OrderedPair a6 = new OrderedPair("b", "f");
		// OrderedPair a7 = new OrderedPair("c", "d");
		// OrderedPair a8 = new OrderedPair("c", "e");
		// OrderedPair a9 = new OrderedPair("c", "f");
		//
		// orderedPairs.add(a1);
		// orderedPairs.add(a2);
		// orderedPairs.add(a3);
		// orderedPairs.add(a4);
		// orderedPairs.add(a5);
		// orderedPairs.add(a6);
		// orderedPairs.add(a7);
		// orderedPairs.add(a8);
		// orderedPairs.add(a9);
		// OrderedPairOrder o_order= new OrderedPairOrder(orderedPairs);
		// OrderedGraph graph = new OrderedGraph(USE_MIN_AND_RANDOM,o_order);
		// ##############################################################################################################################

		// System.out.printf("Neues Bsp");
		// ArrayList<OrderedPair> orderedPairs = new ArrayList<OrderedPair>();
		// OrderedPair a1 = new OrderedPair("a", "d");
		// OrderedPair a2 = new OrderedPair("a", "e");
		// OrderedPair a3 = new OrderedPair("b", "d");
		// OrderedPair a4 = new OrderedPair("b", "e");
		// OrderedPair a5 = new OrderedPair("c", "d");
		// OrderedPair a6 = new OrderedPair("c", "e");
		// OrderedPair a7 = new OrderedPair("d", "f");
		// OrderedPair a8 = new OrderedPair("d", "g");
		// OrderedPair a9 = new OrderedPair("e", "f");
		// OrderedPair a10 = new OrderedPair("e", "g");
		// OrderedPair a11 = new OrderedPair("u", "g");
		// orderedPairs.add(a1);
		// orderedPairs.add(a2);
		// orderedPairs.add(a3);
		// orderedPairs.add(a4);
		// orderedPairs.add(a5);
		// orderedPairs.add(a6);
		// orderedPairs.add(a7);
		// orderedPairs.add(a8);
		// orderedPairs.add(a9);
		// orderedPairs.add(a10);
		// orderedPairs.add(a11);
		// OrderedPairOrder o_order= new OrderedPairOrder(orderedPairs);
		// OrderedGraph graph = new OrderedGraph(USE_MIN_AND_RANDOM, o_order);
		// ##############################################################################################################################

		// Eingabe mit 2 Listen
		// System.out.printf("Example 8, Fig. 10");
		// ArrayList<Object> list1 = new ArrayList<Object>();
		// list1.add("red");
		// list1.add("blue");
		// //
		// ArrayList<Object> list2 = new ArrayList<Object>();
		// list2.add("yellow");
		// list2.add("brown");
		// list2.add("black");
		//
		// ArrayList<Object> list3 = new ArrayList<>();
		// list3.add("purple");
		//
		// //
		// ListenPaar l1 = new ListenPaar(list1, list2);
		// ListenPaar l2 = new ListenPaar(list2, list3);
		// ListenPaarOrder l_order= new ListenPaarOrder(l1, l2);
		// OrderedGraph graph = new OrderedGraph(USE_MIN_AND_RANDOM, l_order);
		// ##############################################################################################################################

		// ArrayList<OrderedPair> orderedPairs = new ArrayList<OrderedPair>();
		// OrderedPair a1 = new OrderedPair("a", "c");
		// OrderedPair a2 = new OrderedPair("a", "d");
		// OrderedPair a3 = new OrderedPair("b", "c");
		// OrderedPair a4 = new OrderedPair("b", "d");
		// OrderedPair a5 = new OrderedPair("c", "e");
		// OrderedPair a6 = new OrderedPair("c", "f");
		// OrderedPair a7 = new OrderedPair("d", "e");
		// OrderedPair a8 = new OrderedPair("d", "f");
		// orderedPairs.add(a1);
		// orderedPairs.add(a2);
		// orderedPairs.add(a3);
		// orderedPairs.add(a4);
		// orderedPairs.add(a5);
		// orderedPairs.add(a6);
		// orderedPairs.add(a7);
		// orderedPairs.add(a8);
		// OrderedPairOrder o_order= new OrderedPairOrder(orderedPairs);
		// OrderedGraph graph = new OrderedGraph(MIN, o_order);

		// ##############################################################################################################################
		// Eingabe mit 2 Listen
		// ArrayList<Object> list1 = new ArrayList<Object>();
		// list1.add("a");
		// list1.add("b");
		//
		// ArrayList<Object> list2 = new ArrayList<Object>();
		// list2.add("c");
		// list2.add("d");
		//
		// ListenPaar l1 = new ListenPaar(list1, list2);
		//
		// ArrayList<Object> list3 = new ArrayList<Object>();
		// list3.add("c");
		// list3.add("d");
		//
		// ArrayList<Object> list4 = new ArrayList<Object>();
		// list4.add("e");
		// list4.add("f");
		//
		// ListenPaar l2 = new ListenPaar(list3, list4);
		// ListenPaarOrder l_order= new ListenPaarOrder(l1, l2);
		// OrderedGraph graph = new OrderedGraph(USE_MIN_AND_RANDOM, l_order);
		// ################################################################################################################################

		// ##############################################################################################################################

		// System.out.printf("Neues Bsp");
		// ArrayList<OrderedPair> orderedPairs2 = new ArrayList<OrderedPair>();
		// OrderedPair a_1 = new OrderedPair("a", "b");
		// orderedPairs2.add(a_1);
		// OrderedPairOrder o_order2 = new OrderedPairOrder(orderedPairs2);
		// OrderedGraph graph = new OrderedGraph(MIN, USE_MIN_AND_RANDOM,
		// o_order,
		// o_order2);
		// ##############################################################################################################################

		// ArrayList<OrderedPair> orderedPairs2 = new ArrayList<OrderedPair>();
		// OrderedPair a_1 = new OrderedPair("a", "c");
		// OrderedPair a_2 = new OrderedPair("a", "d");
		// OrderedPair a_3 = new OrderedPair("b", "c");
		// OrderedPair a_4 = new OrderedPair("b", "d");
		// OrderedPair a_5 = new OrderedPair("c", "e");
		// OrderedPair a_6 = new OrderedPair("c", "f");
		// OrderedPair a_7 = new OrderedPair("d", "e");
		// OrderedPair a_8 = new OrderedPair("d", "f");
		// orderedPairs2.add(a_1);
		// orderedPairs2.add(a_2);
		// orderedPairs2.add(a_3);
		// orderedPairs2.add(a_4);
		// orderedPairs2.add(a_5);
		// orderedPairs2.add(a_6);
		// orderedPairs2.add(a_7);
		// orderedPairs2.add(a_8);
		// OrderedPairOrder o_order2 = new OrderedPairOrder(orderedPairs2);
		// OrderedGraph graph = new OrderedGraph(MIN, o_order2, o_order);

		ArrayList<OrderedPair> orderedPairs2 = new ArrayList<OrderedPair>();
		OrderedPair a_1 = new OrderedPair(0, 1);
		OrderedPair a_2 = new OrderedPair(1, 2);
		OrderedPair a_3 = new OrderedPair(2, 3);

		orderedPairs2.add(a_1);
		orderedPairs2.add(a_2);
		orderedPairs2.add(a_3);

		OrderedPairOrder o_order2 = new OrderedPairOrder(orderedPairs2);
		OrderedGraph graph = new OrderedGraph(MIN, o_order2, o_order);

		int[] a = graph.getMaxValuesArray();

		graph.getKeySignatureAssignments();
		graph.printKeySignatureAssignments();

	}

	public Object[] getOrders() {
		return orders;
	}

	public boolean hasSeveralOrders() {
		return severalOrders;
	}

	@SuppressWarnings("unchecked")
	private void init() {
		// build a complete graph with pseudo-vertexes
		// System.out.println("GRAPHSTRUKTUR");
		// Graph erstellt aus geordneten Paaren
		if (standardGraph == true && severalOrders == false) {
			buildStandardGraph();
			specifyMinimalSignatureValues();
		}
		// Graph erstellt aus Listenpaaren
		else if (standardGraph == false && severalOrders == false) {

			buildGraphFromListsNodeLists();
			specifyMinimalSignatureValues();
		}
		// mehreren Ordnungen
		else if (standardGraph == false && severalOrders == true) {
			for (int i = 0; i < orders.length; i++) {
				currentOrder = orders[i];
				if (currentOrder instanceof OrderedPairOrder) {
					orderedPairOrder = (OrderedPairOrder) currentOrder;
					buildStandardGraph();
					specifyMinimalSignatureValues();
				} else if (currentOrder instanceof NodeListsOrder) {
					buildGraphFromListsNodeLists();
					specifyMinimalSignatureValues();
				}

				clear();
			}

		}
		if (!severalOrders) {

			for (Object obj : ((HashMap<Object, ArrayList<Integer>>) orderSignatures
					.get(currentOrder)).keySet()) {
				ArrayList<Object> list = new ArrayList<>();
				list.add(obj);
				signatureAssignments.put(list,
						((HashMap<Object, ArrayList<Integer>>) orderSignatures
								.get(currentOrder)).get(obj));
			}

		}
	}

	private void specifyMinimalSignatureValues() {
		vertSet = graph.vertexSet();
		tops = getTopObjects(children, parents);
		bottoms = getBottomObjects(children, parents);
		setTopsAndBottoms();
		completeGraphWithTopAndBottom();
		System.out.println("--------------------------------------------");

		// depth-search
		// results of deph-search save as depthResult
		depthSearch();

		// breadth-search
		// results of breadth-search save as resultTuple
		// results as a node- signatures, in the form of ArrayList<Integer>
		breadthSearch();

		// positions, wich contain only zero-values have to be removed
		// array with max positions- value is saved as max_values_array

		maxValuesArray = new int[length];

		removeMaxZeroPositions();

		System.out.println("Signatures: ");
		for (Object s : signatures.keySet()) {
			System.out.println(s + " :" + signatures.get(s).toString());
		}
		System.out
				.println("\n ################################################################");

		signatureLength = signatureLength + combinedSignatureLength();

	}

	private int combinedSignatureLength() {
		int length = 0;

		length = signatures.values().iterator().next().size();

		return length;
	}

	private void clear() {

		tops = null;
		bottoms = null;
		graph = new DefaultDirectedGraph<>(DefaultEdge.class);
		vertSet = new HashSet<>();
		children = new HashSet<>();
		parents = new HashSet<>();
	}

	// graph erstellen
	private void buildGraphFromListsNodeLists() {
		NodeLists[] listen = nodeListsOrder.getLists();
		for (int i = 0; i < listen.length; i++) {

			buildFullyConnectedGraph(listen[i]);
		}

		originalGraph.add(graph);
	}

	private void buildFullyConnectedGraph(NodeLists listPairs) {

		for (int i = 0; i < listPairs.getList1().size(); i++) {

			// falls Knoten noch nicht erzeugt wurde
			if (!graph.containsVertex(listPairs.getList1().get(i))) {
				graph.addVertex(listPairs.getList1().get(i));
			}
			if (!parents.contains(listPairs.getList1().get(i))) {
				parents.add(listPairs.getList1().get(i));
			}

			// falls Knoten noch nicht erzeugt wurde
			for (int j = 0; j < listPairs.getList2().size(); j++) {

				if (!graph.containsVertex(listPairs.getList2().get(j))) {
					graph.addVertex(listPairs.getList2().get(j));
				}

				if (!children.contains(listPairs.getList2().get(j))) {
					children.add(listPairs.getList2().get(j));
				}

				// Erstelle eine Kante
				graph.addEdge(listPairs.getList1().get(i), listPairs.getList2()
						.get(j));
				System.out.println(listPairs.getList1().get(i) + " -> "
						+ listPairs.getList2().get(j));

			}
		}

	}

	private void buildStandardGraph() {
		ArrayList<OrderedPair> orderedpairList = orderedPairOrder
				.getListOfPairs();
		for (int i = 0; i < orderedpairList.size(); i++) {

			// if the nodes do not exists --> add them
			if (orderedpairList.get(i).getChild() != null
					&& !(graph
							.containsVertex(orderedpairList.get(i).getChild()))) {

				graph.addVertex(orderedpairList.get(i).getChild());

			}

			if (orderedpairList.get(i).getChild() != null) {
				if (orderedpairList.get(i).getParent() == null) {
				} else if (!children
						.contains(orderedpairList.get(i).getChild())) {
					children.add(orderedpairList.get(i).getChild());
				}
			}
			if (orderedpairList.get(i).getParent() != null
					&& !(graph.containsVertex(orderedpairList.get(i)
							.getParent()))) {

				graph.addVertex(orderedpairList.get(i).getParent());

			}
			if (orderedpairList.get(i).getParent() != null
					&& !parents.contains(orderedpairList.get(i).getParent())) {
				parents.add(orderedpairList.get(i).getParent());
			}

			// fuegt Kante hinzu, falls beide Knoten nicht NULL sind
			if (orderedpairList.get(i).getParent() != null
					&& orderedpairList.get(i).getChild() != null) {
				graph.addEdge(orderedpairList.get(i).getParent(),
						orderedpairList.get(i).getChild());
				System.out.println(orderedpairList.get(i).getParent() + " -> "
						+ orderedpairList.get(i).getChild());
			}
		}
		DirectedGraph<Object, DefaultEdge> temp_gr = new DefaultDirectedGraph<>(
				DefaultEdge.class);
		for (Object o : graph.vertexSet()) {
			temp_gr.addVertex(o);
		}
		for (DefaultEdge e : graph.edgeSet()) {
			temp_gr.addEdge(graph.getEdgeSource(e), graph.getEdgeTarget(e));

		}
		originalGraph.add(temp_gr);
	}

	/**
	 * Add single nodes to tops and bottoms.
	 */
	private void setTopsAndBottoms() {
		Iterator<Object> it = vertSet.iterator();
		while (it.hasNext()) {
			Object item = it.next();
			if (!children.contains(item) && !parents.contains(item)) {
				tops.add(item);
				bottoms.add(item);
			}
		}
	}

	/**
	 * Depth search
	 */
	private void depthSearch() {

		GraphIterator<Object, DefaultEdge> iterator_depthFirst = new DepthFirstIteratorEdge<>(
				graph, VIRTUAL_TOPNODE, 0);

		while (iterator_depthFirst.hasNext()) {
			iterator_depthFirst.next();
		}

		depthResult = ((DepthFirstIteratorEdge<Object, DefaultEdge>) iterator_depthFirst)
				.getDepthResults();

		length = ((DepthFirstIteratorEdge<Object, DefaultEdge>) iterator_depthFirst)
				.getCounter() - 1;

	}

	/**
	 * Breadth First Search
	 */
	private void breadthSearch() {

		GraphIterator<Object, DefaultEdge> iterator_breathFirst = new BreadthFirstIterator_<>(
				graph, VIRTUAL_TOPNODE, 4, length, depthResult,
				SIGNATURE_METHOD);
		// System.out.println("BREATH FIRST");
		while (iterator_breathFirst.hasNext()) {
			iterator_breathFirst.next();
		}

		BreadthFirstIterator_<Object, DefaultEdge> breadth_iterator = (BreadthFirstIterator_<Object, DefaultEdge>) iterator_breathFirst;

		signatures = breadth_iterator.getResultTuple();

		orderSignatures.put(currentOrder, signatures);

	}

	private void completeGraphWithTopAndBottom() {
		// fuege pseudo-knoten hinzu
		addVirtualNodes();
		// erzeuge Kanten v. oberem Pseudo-Knoten zu tops
		Iterator<Object> it1 = tops.iterator();
		while (it1.hasNext()) {
			Object item = it1.next();
			graph.addEdge(VIRTUAL_TOPNODE, item);
			// System.out.println(VIRTUAL_TOPNODE + " -> " + item);
		}

		// erzeuge Kanten v. unterem Pseudo-Knoten zu bottoms
		Iterator<Object> it2 = bottoms.iterator();
		while (it2.hasNext()) {
			Object item = it2.next();
			graph.addEdge(item, VIRTUAL_BOTTOMNODE);
			// System.out.println(item + " -> " + VIRTUAL_BOTTOMNODE);
		}
	}

	/**
	 * Remove 0 from final signatures.
	 */
	private void removeMaxZeroPositions() {
		int[] signatures;

		removeMaxZeroPositions_standard();
		if (severalOrders) {
			signatures = maxValuesArray;
			maxSignatureValues.put(currentOrder, signatures);
		}

	}

	// normale methode
	private void removeMaxZeroPositions_standard() {
		int j = 0;
		int l = length;
		for (int i = 0; i < l; i++) {
			int max = 0;
			for (Object vertex : signatures.keySet()) {
				if (signatures.get(vertex).get(i) > max) {
					max = signatures.get(vertex).get(i);
				}
			}
			if (max == 0) {
				signatures = removePosition(i, signatures);
				l--;
				i--;
				resizeMaxValuesArray();
			} else {

				maxValuesArray[j] = max;
				j++;
			}
		}
	}

	/**
	 * Adapt maxValuesArray based on the removed 0-positions.
	 */
	private void resizeMaxValuesArray() {
		int temp[] = new int[maxValuesArray.length - 1];
		for (int i = 0; i < maxValuesArray.length - 1; i++) {
			temp[i] = maxValuesArray[i];
		}
		maxValuesArray = temp;

	}

	// entferne aus jede ergebnis des resultSets eine Position
	private HashMap<Object, ArrayList<Integer>> removePosition(int i,
			HashMap<Object, ArrayList<Integer>> resultTuple) {
		for (Object s : resultTuple.keySet()) {
			resultTuple.get(s).remove(i);
		}

		return resultTuple;

	}

	private Set<Object> getTopObjects(Set<Object> kinder, Set<Object> eltern) {
		Set<Object> tops = new HashSet<>();
		Iterator<Object> it = eltern.iterator();
		while (it.hasNext()) {
			Object o = it.next();
			if (!(kinder.contains(o))) {
				tops.add(o);
			}
		}

		return tops;

	}

	private Set<Object> getBottomObjects(Set<Object> kinder, Set<Object> eltern) {
		Set<Object> bottoms = new HashSet<>();
		Iterator<Object> it = kinder.iterator();
		while (it.hasNext()) {
			Object o = it.next();
			if (!eltern.contains(o)) {
				bottoms.add(o);
			}
		}

		return bottoms;

	}

	private void addVirtualNodes() {
		getgraph().addVertex(VIRTUAL_TOPNODE);
		getgraph().addVertex(VIRTUAL_BOTTOMNODE);
	}

	public DirectedGraph<Object, DefaultEdge> getgraph() {
		return graph;
	}

	public void setGraph(DirectedGraph<Object, DefaultEdge> graph) {
		this.graph = graph;
	}

	public int getSignatureMethod() {
		return SIGNATURE_METHOD;
	}

	@SuppressWarnings("unchecked")
	protected void combineSignatures() {
		ArrayList<HashMap<Object, ArrayList<Integer>>> list_of_signatures = new ArrayList<>();
		ArrayList<ArrayList<Object>> list_of_signature_keys = new ArrayList<>();
		for (int i = 0; i < orders.length; i++) {
			ArrayList<Object> list = new ArrayList<>();
			HashMap<Object, ArrayList<Integer>> signaturesOfSomeOrder = new HashMap<>();
			Object order = orders[i];

			Set<Object> set = ((HashMap<Object, ArrayList<Integer>>) orderSignatures
					.get(order)).keySet();
			Iterator<Object> iter = set.iterator();
			while (iter.hasNext()) {
				Object key = iter.next();
				list.add(key);
				signaturesOfSomeOrder.put(key,
						((HashMap<Object, ArrayList<Integer>>) orderSignatures
								.get(order)).get(key));

			}

			list_of_signatures.add(i, signaturesOfSomeOrder);
			list_of_signature_keys.add(i, list);
		}

		Object[] values = new Object[orders.length];

		createCombinations(list_of_signature_keys, 0, values);

		int len;

		for (int j = 0; j < combinedKeys.size(); j++) {
			int[] signature_kombiniert = new int[signatureLength];
			len = 0;
			for (int k = 0; k < combinedKeys.get(j).length; k++) {
				ArrayList<Integer> einzeln_signatur;
				einzeln_signatur = list_of_signatures.get(k).get(
						combinedKeys.get(j)[k]);
				for (int z = 0; z < einzeln_signatur.size(); z++) {
					signature_kombiniert[len] = einzeln_signatur.get(z)
							.intValue();
					len++;
				}
			}

			combinedSignatures.put(combinedKeys.get(j), signature_kombiniert);
		}

		getCombinedSignatureValueArray();
		signatureAssignments = computeKeySignatureAssignments();

	}

	private void getCombinedSignatureValueArray() {

		int[] new_mva = new int[signatureLength];
		int j = 0;
		int l = signatureLength;
		for (int i = 0; i < l; i++) {
			int max = 0;
			for (Object combined_key : combinedSignatures.keySet()) {
				if (combinedSignatures.get(combined_key)[i] > max) {
					max = combinedSignatures.get(combined_key)[i];
				}
			}

			new_mva[j] = max;
			j++;

		}

		maxValuesArray = new_mva;

	}

	private void createCombinations(ArrayList<ArrayList<Object>> keys,
			int index, Object[] values) {
		ArrayList<Object> key = keys.get(index);

		for (int i = 0; i < key.size(); i++) {

			values[index] = key.get(i);
			if (index < keys.size() - 1) {

				createCombinations(keys, index + 1, values);

			} else {

				combinedKeys.add((Object[]) values.clone());
			}
		}
	}

	// FIXME: return correct signatures, standard signatures, min signatures or
	// random signatures
	public HashMap<ArrayList<Object>, ArrayList<Integer>> getKeySignatureAssignments() {

//		ArrayList<Object> o = new ArrayList<>();
//		o.add(0, new Integer(1));
//		o.add(1, new Integer(0));
//		if (signatureAssignments.containsKey(o))
//			System.out.println("\no enthalten");

	//	printKeySignatureAssignments();
		return signatureAssignments;
	}



	protected HashMap<ArrayList<Object>, ArrayList<Integer>> computeKeySignatureAssignments() {
		HashMap<ArrayList<Object>, ArrayList<Integer>> tempSign = new HashMap<>();

		for (Object[] o : combinedSignatures.keySet()) {
			ArrayList<Integer> tempList = new ArrayList<Integer>();
			ArrayList<Object> oList = new ArrayList<Object>();
			for (int j = 0; j < o.length; j++) {
				oList.add(j, o[j]);
			}
			for (int i = 0; i < combinedSignatures.get(o).length; i++) {
				tempList.add(i, new Integer(combinedSignatures.get(o)[i]));
			}
			tempSign.put(oList, tempList);
		}

		return tempSign;
	}

	private void printKeySignatureAssignments() {

		Set<ArrayList<Object>> set = signatureAssignments.keySet();
		Iterator<ArrayList<Object>> iter = set.iterator();

		while (iter.hasNext()) {
			ArrayList<Object> o = iter.next();
			System.out.print("\n[ ");
			for (int i = 0; i < o.size(); i++) {
				System.out.print(o.get(i) + " ");
			}
			System.out.print(" ]");

			System.out.print("[ ");
			for (int i = 0; i < signatureAssignments.get(o).size(); i++) {
				System.out.print(signatureAssignments.get(o).get(i) + " ");
			}
			System.out.print("]");
		}

	}

	public int[] getMaxValuesArray() {

		int[] max_array = maxValuesArray;
		System.out.print("\nmax values: [ ");
		for (int i = 0; i < max_array.length; i++) {
			System.out.print(max_array[i]);
			System.out.print(" ");
		}
		System.out.printf("]");
		return max_array;
	}

	public ArrayList<DirectedGraph<Object, DefaultEdge>> getOriginalGraph() {
		return originalGraph;
	}

}

