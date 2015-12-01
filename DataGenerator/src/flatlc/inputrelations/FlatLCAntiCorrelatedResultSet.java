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

public class FlatLCAntiCorrelatedResultSet extends FlatLCResultSetA {
    /**
     * default maxumum for column (=level) values
     */
    public static final int DEFAULT_MAX = 10;

    /**
     * Seed value for random data generator. Note that the same seed value
     * always leads to the same random numbers.
     */
    private int seed = 0;

    /**
     * counter containing the current row
     */
    int currentRow;
    int currentRowInMem;

    /**
     * number of tuples produced and thrown away at object initialization
     */
    int offset;

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
     * multiplicators for the generation of unique ids (= edge weights)
     */
    int[] multIDs;

    /**
     * in memory flag. Keeps the generated result set in memory. I.e. - peek is
     * supported - reset is supported on the same input tuples
     */
    boolean inMem = false;

    /**
     * set with objects that will be returned by <code>next()</code>
     */
    ArrayList<Object> elements;

    /**
     * Constructor. The column number is without counting the <code>id</code>
     * column. The maximum column value is set to <code>DEFAULT_MAX</code>.
     *
     * @param cols number of columns of random data
     * @param rows number of rows
     */
    public FlatLCAntiCorrelatedResultSet(int cols, int rows) {
        this(cols, rows, DEFAULT_MAX, 0);
    }

    /**
     * Constructor. The column number is without counting the <code>id</code>
     * column. The minimum column value is set to 0 (zero), the maximum column
     * value is set to 100.
     *
     * @param cols   number of columns of random data
     * @param rows   number of rows.
     * @param offset number of rows thrown away before returning the first row
     */
    public FlatLCAntiCorrelatedResultSet(int cols, int rows, int offset) {
        this(cols, rows, DEFAULT_MAX, offset);
    }

    /**
     * Constructor. The column number is without counting the <code>id</code>
     * column.
     *
     * @param cols    number of columns of random data
     * @param rows    number of rows
     * @param maximum maximum level value
     * @param offset  number of rows thrown away before returning the first row
     */
    public FlatLCAntiCorrelatedResultSet(int cols, int rows, int maximum,
                                         int offset) {
        this.rows = rows;
        this.offset = offset;
        this.maxValues = new int[cols];
        for (int i = 0; i < maxValues.length; i++) {
            maxValues[i] = maximum;
        }
        init();
    }

    /**
     * Constructor
     *
     * @param rows      number of rows
     * @param maxValues maximum level values
     */
    public FlatLCAntiCorrelatedResultSet(int rows, int[] maxValues) {
        this(rows, maxValues, 0);
    }

    /**
     * Constructor
     *
     * @param rows      number of rows
     * @param maxValues maximum level values
     * @param inMem     input distribution will be hold in main memory
     */
    public FlatLCAntiCorrelatedResultSet(int rows, int[] maxValues,
                                         boolean inMem) {
        this.rows = rows;
        this.maxValues = maxValues;
        this.inMem = inMem;
        this.currentRowInMem = 0;
        init();
    }

    /**
     * Constructor
     *
     * @param rows      number of rows
     * @param maxValues maximum level values
     * @param offset    number of rows thrown away before returning the first row
     */
    public FlatLCAntiCorrelatedResultSet(int rows, int[] maxValues, int offset) {
        this(rows, maxValues, offset, 0);
    }

    public FlatLCAntiCorrelatedResultSet(int rows, int[] maxValues, int offset, int seed) {
        this.rows = rows;
        this.maxValues = maxValues;
        this.offset = offset;
        this.seed = seed;
        init();
    }

    /**
     * Initializes the random number generators.
     */
    private void init() {
        int len = this.maxValues.length;
        // construct new preference object
        // ParetoPreference preference = new ParetoPreference();
        // try {
        // for (int i = 0; i < len; i++) {
        // preference.append(new ExtremalPreference(null, false,
        // DefaultSVRelation.REGULAR, 1.0, 0.0, this.maxValues[i]));
        // }
        // } catch (PreferenceException e) {
        // e.printStackTrace();
        // }

        currentRow = 0;
        generator = new Random(seed);

        // compute the multiplicators
        this.multIDs = new int[len];
        this.multIDs[len - 1] = 1;
        for (int i = len; --i > 0; ) {
            this.multIDs[i - 1] = this.multIDs[i] * (maxValues[i] + 1);
        }

        // remove the offset
        // FIXME: Bug, if used with inMem Flag
        for (int i = 0; i < offset; i++) {
            next();
        }
        // reset the row count
        currentRow = 0;

        if (inMem) {
            elements = new ArrayList<Object>();
            while (currentRowInMem++ < rows)
                elements.add(nextResult());
        }

        currentRowInMem = 0;
        currentRow = 0;

    }

    /**
     * Resets the row count. After calling this method, the number of rows to be
     * returned is reset. The row values are not identical to the already
     * returned objects. For a complete clone a new
     * <code>FlatLCRandomResultSet</code> object has to be created (using the
     * same parameters).
     */
    public void reset() {
        if (inMem)
            currentRowInMem = 0;
//        currentRowInMem = offset;
        else
            init();
    }

    public Object getMetaData() {
        if (meta == null) {
            meta = new RandomResultSetMetaData(maxValues);
        }
        return meta;
    }

    public boolean hasNext() throws IllegalStateException {
        if (inMem)
            return elements.size() > currentRowInMem;
        else
            return currentRow < rows;
    }

    private double lowerBorderX = 1;
    private double lowerBorderY = 1;

    private double getLow(double x) {
        return lowerBorderY
                - Math.sqrt((lowerBorderX - 1) * (lowerBorderX - 1)
                + lowerBorderY * lowerBorderY - (lowerBorderX - x)
                * (lowerBorderX - x));
    }

    private double higherBorderX = 0;
    private double higherBorderY = 0;

    private double getHigh(double x) {
        return Math.sqrt(higherBorderX * higherBorderX + (1 + higherBorderY)
                * (1 + higherBorderY) - (higherBorderX + x)
                * (higherBorderX + x))
                - higherBorderY;
    }

    public double[] nextVal() {
        double[] result = new double[maxValues.length];

        // create first value:
        double first = 0.5 + generator.nextGaussian();
        // do {
        // first = 0.5 + generator.nextGaussian();
        // } while (first < 0.0 || first > 1.0);
        if (first == 1) {
            result[0] = 1;
            return result;
        } else if (first == 0) {
            for (int i = 0; i < result.length; i++) {
                result[i] = 1;
            }
        }
        // if (first < 0.0 || first > 1.0) {
        // first = 0.5 + (first % 0.5);
        // }
        if (first < 0.0)
            first = -first;
        if (first > 1.0) {
            if (((int) first % 2) == 0) {
                first = first % 1;
            } else {
                first = 1 - (first % 1);
            }
        }

        result[0] = first;
        // int count = 0;
        // double mult = (Math.min(first, 1 - first));
        // double low = 1.0 - Math.sqrt(1.0 - (1.0 - first) * (1.0 - first));
        // low = 2 - Math.sqrt(5.0 - (2.0 - first) * (2.0 - first));
        // double high = Math.sqrt(1.0 - first * first);
        // high = Math.sqrt(5.0 - first * first);
        double low = getLow(first);
        double high = getHigh(first);
        double distance = high - low;
        double middle = 1.0 - first;

        for (int i = 1; i < result.length; i++) {
            // double candidate = generator.nextGaussian() * mult;
            // while (candidate > mult || candidate < -mult) {
            // candidate /= 2.58;
            // }
            // if (candidate > maxVALUE) {
            // maxVALUE = candidate;
            // System.out.println("MAX = " + candidate);
            // }
            // result[i] = candidate + 1 - first;
            double candidate = low + generator.nextDouble() * distance;
            // double candidate = generator.nextGaussian() * distance;
            // while (middle + candidate < low || middle + candidate > high) {
            // candidate = generator.nextGaussian() * distance;
            // }
            result[i] = candidate;

            // double candidate = generator.nextGaussian();
            // if (result[0] <= 0.5) {
            // candidate *= result[0];
            // } else {
            // candidate *= 1.0 - result[0];
            // }
            // result[i] = 1 - result[0] + candidate;
            // if (result[i] < 0.0) result[i] = -result[i];
            // if (result[i] > 1.0) {
            // if (((int) result[i] % 2) == 0) {
            // result[i] = result[i] % 1;
            // } else {
            // result[i] = 1 - (result[i] % 1);
            // }
            // }
        }
        return result;
    }

    private double maxVALUE = 0.0;

    private FlatLevelCombination nextResult() throws IllegalStateException,
            NoSuchElementException {
        FlatLevelCombination result = null;
        if (currentRow++ < rows) {
            // generate a new row
            double[] values = nextVal();
            Object[] object = new Number[maxValues.length];
            int[] levels = new int[maxValues.length];

            // convert double values to integers
            for (int i = 0; i < levels.length; i++) {
                int val = (int) (values[i] * (maxValues[i] + 1));
                object[i] = new Integer(val);
                levels[i] = val;
            }
            result = new FlatLevelCombination(levels, object, maxValues,
                    multIDs);
        }
        return result;
    }

    public Object next() throws IllegalStateException, NoSuchElementException {
        if (inMem) {
            return elements.get(currentRowInMem++);
        }

        return nextResult();
    }

    public Object peek() throws IllegalStateException, NoSuchElementException,
            UnsupportedOperationException {
        if (inMem) {
            return elements.get(currentRowInMem);
        } else
            throw new UnsupportedOperationException("peek is not supported");
    }

    public void open() {
    }

    public void close() {
    }

    public boolean supportsPeek() {
        if (inMem)
            return true;

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

    public void remove() throws IllegalStateException,
            UnsupportedOperationException {
        throw new UnsupportedOperationException("remove is not supported");
    }

    public void update(Object arg0) throws IllegalStateException,
            UnsupportedOperationException {
        throw new UnsupportedOperationException("update is not supported");
    }

    @Override
    public ArrayList<Object> getElements() {
        return (ArrayList<Object>) elements.clone();
    }

}
