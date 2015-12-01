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

package flatlc.inputrelations;

import flatlc.levels.FlatLevelCombination;

import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Random;


public class FlatLCRandomNodeResultSet extends FlatLCResultSetA {
    /**
     * default maxumum for column (=level) values
     */
    public static final int DEFAULT_MAX = 10;


    /**
     * counter containing the current row
     */
    int currentRow;

    /**
     * the origins of the nodes that will be returned
     */
    FlatLevelCombination[] nodes;

    /**
     * column value generator
     */
    Random generator;

    /**
     * meta data object
     */
    ResultSetMetaData meta;

    /**
     * maximum values for the column values
     */
    int[] maxValues;

    /**
     * multiplicators for the generation of unique ids
     */
    int[] multIDs;

    /**
     * Constructor. The column number is without counting the <code>id</code>
     * column.
     * The minimum of 1 node and 10% of the nodes will be chosen as the
     * number of return nodes.
     * The maximum column value is set to <code>DEFAULT_MAX</code>.
     *
     * @param cols number of columns of random data
     * @param rows number of rows
     */
    public FlatLCRandomNodeResultSet(int cols, int rows) {
        this(cols, rows, Math.max((int) Math.pow(DEFAULT_MAX + 1, cols) / 10, 1), DEFAULT_MAX);
    }

    /**
     * Constructor. The column number is without counting the <code>id</code>
     * column. The maximum column value is set to <code>DEFAULT_MAX</code>.
     *
     * @param cols      number of columns of random data
     * @param rows      number of rows
     * @param nodeCount the number of different nodes the returned tuples belong to
     */
    public FlatLCRandomNodeResultSet(int cols, int rows, int nodeCount) {
        this(cols, rows, nodeCount, DEFAULT_MAX);
    }


    /**
     * Constructor. The column number is without counting the <code>id</code>
     * column.
     *
     * @param cols      number of columns of random data
     * @param rows      number of rows
     * @param nodeCount the number of different nodes the returned tuples belong to
     * @param maximum   maximum level value for each column
     */
    public FlatLCRandomNodeResultSet(int cols, int rows, int nodeCount, int maximum) {
        this.rows = rows;
        this.maxValues = new int[cols];
        for (int i = 0; i < maxValues.length; i++) {
            maxValues[i] = maximum;
        }
        init(nodeCount);
    }

    /**
     * Constructor.
     * The minimum of 1 node and 10% of the nodes will be chosen as the
     * number of return nodes.
     *
     * @param rows      number of rows
     * @param maxValues maximum level values
     */
    public FlatLCRandomNodeResultSet(int rows, int[] maxValues) {
        this.rows = rows;
        this.maxValues = maxValues;
        int count = 1;
        for (int i = 0; i < maxValues.length; i++) {
            count *= maxValues[i] + 1;
        }

        init(Math.max(count / 10, 1));
    }

    /**
     * Constructor
     *
     * @param rows      number of rows
     * @param maxValues maximum level values
     */
    public FlatLCRandomNodeResultSet(int rows, int[] maxValues, int nodeCount) {
        this.rows = rows;
        this.maxValues = maxValues;
        init(nodeCount);
    }


    /**
     * Constructor
     *
     * @param rows      number of rows
     * @param maxValues maximum level values
     */
    public FlatLCRandomNodeResultSet(int rows, int[] maxValues, int[][] nodes) {
        this.rows = rows;
        this.maxValues = maxValues;
        init(nodes.length);

        for (int i = 0; i < nodes.length; i++) {
            Object[] tmp = new Object[nodes[i].length];
            for (int j = 0; j < tmp.length; j++) {
                tmp[j] = new Integer(nodes[i][j]);
            }
            this.nodes[i] = new FlatLevelCombination(nodes[i], tmp, maxValues, multIDs);
        }
    }

    public static void main(String[] args) {
        FlatLCRandomNodeResultSet test = new FlatLCRandomNodeResultSet(4, 100);
        while (test.hasNext()) {
            System.out.println(test.next());
        }
        System.out.println("===================");
        int[] mx = new int[]{4, 2, 1, 5, 3};
        test = new FlatLCRandomNodeResultSet(50, mx, 2);
        while (test.hasNext()) {
            System.out.println(test.next());
        }
    }

    /**
     * Initializes the random number generators.
     */
    private void init(int nodeCount) {
        int len = this.maxValues.length;

        this.currentRow = 0;
        this.generator = new Random(0);

        // compute the multiplicators
        this.multIDs = new int[len];
        this.multIDs[len - 1] = 1;
        for (int i = len; --i > 0; ) {
            this.multIDs[i - 1] = this.multIDs[i] * (maxValues[i] + 1);
        }

        // create a number of prototype nodes
        this.nodes = new FlatLevelCombination[nodeCount];
        for (int i = 0; i < nodes.length; i++) {
            Object[] obj = new Object[len];
            int[] lvl = new int[len];
            for (int j = 0; j < lvl.length; j++) {
                lvl[j] = generator.nextInt(maxValues[j]);
                obj[j] = new Integer(lvl[j]);
            }
            nodes[i] = new FlatLevelCombination(lvl, obj, maxValues, multIDs);
        }
    }

    public void reset() {
        init(nodes.length);
    }

    public Object getMetaData() {
        if (meta == null) {
            meta = new RandomResultSetMetaData(maxValues);
        }
        return meta;
    }

    public boolean hasNext() throws IllegalStateException {
        return currentRow < rows;
    }

    public Object next() throws IllegalStateException, NoSuchElementException {
        FlatLevelCombination result = null;
        if (currentRow++ < rows) {
            // generate a new row:
            // chose one of the prototype nodes
            int[] levels = nodes[generator.nextInt(nodes.length)].getLevelCombination();
            // fill object array
            Object[] object = new Number[levels.length];
            for (int i = 0; i < levels.length; i++) {
                object[i] = new Integer(levels[i]);
            }
            result = new FlatLevelCombination(levels, object, maxValues, multIDs);
        }
        return result;
    }

    public Object peek() throws IllegalStateException, NoSuchElementException, UnsupportedOperationException {
        throw new UnsupportedOperationException("peek is not supported");
    }

    public void open() {
    }

    public void close() {
    }

    public boolean supportsPeek() {
        return false;
    }

    public boolean supportsRemove() {
        return false;
    }

    public boolean supportsReset() {
        return true;
    }

    public boolean supportsUpdate() {
        return false;
    }

    public void remove() throws IllegalStateException, UnsupportedOperationException {
        throw new UnsupportedOperationException("remove is not supported");
    }

    public void update(Object arg0) throws IllegalStateException, UnsupportedOperationException {
        throw new UnsupportedOperationException("update is not supported");
    }

    public ArrayList<Object> getElements() {
        throw new UnsupportedOperationException("Not implemented yet");
    }


}
