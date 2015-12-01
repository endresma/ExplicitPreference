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


public class FlatLCUnsharpLevelRandomResultSet extends FlatLCResultSetA {
    /**
     * default maxumum for column (=level) values
     */
    public static final int DEFAULT_MAX = 10;

    /**
     * counter containing the current row
     */
    int currentRow;

    /**
     * number of tuples produced and thrown away at object initialization
     */
    int offset;

    /**
     * the level all returned tuples will belong to
     */
    int lvl;

    /**
     * maximum overall level value (the sum of the components of the array
     * <code>maxValues</code>
     */
    int maxValue;

    /**
     * intervals for the different level array components: this array will have
     * one component for each level from 1 to the maximum value. For each
     * component of the preference (and therefore each element of
     * <code>maxValues</code>), it will hold its index the maximum level value
     * of this component times.<br/>
     * Example:<br/>
     * <code>maxValues = { 4, 2, 1, 5}</code><br/>
     * This means, the maximum overall level value is 12.<br/>
     * The array <code>interval</code> will be
     * <code>{ 0, 0, 0, 0, 1, 1, 2, 3, 3, 3, 3, 3 }</code>.<br/>
     * When creating one element to return by the <code>next()</code> method,
     * random numbers <code>rnd</code> between <code>0</code> and
     * <code>maxValue - 1</code> are created. For such a random number, the
     * level value at the position <code>interval[rnd]</code> is increased. This
     * is done until the overall level specified in the constructor is reached.
     * The method leads to a equal distribution of the overall level value
     * according to the maximum base level values.
     * 
     */
    int[] interval;

    /**
     * column value generator
     */
    Random generator;

    /**
     * additional generator for <code>nextVal()</code>
     */
    Random generatorNextVal;

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
     * column. The maximum column value is set to <code>DEFAULT_MAX</code>. The
     * overall level of returned columns is set to the default value,
     * <code>(DEFAULT_MAX * cols) / 2</code>.
     * 
     * @param cols
     *            number of columns of random data
     * @param rows
     *            number of rows
     */
    public FlatLCUnsharpLevelRandomResultSet(int cols, int rows) {
	this(cols, rows, (DEFAULT_MAX * cols) / 2, 0, DEFAULT_MAX);
    }

    /**
     * Constructor. The column number is without counting the <code>id</code>
     * column. The maximum column value is set to <code>DEFAULT_MAX</code>.
     * 
     * @param cols
     *            number of columns of random data
     * @param rows
     *            number of rows
     * @param lvl
     *            overall level of the tuples produced
     */
    public FlatLCUnsharpLevelRandomResultSet(int cols, int rows, int lvl) {
	this(cols, rows, lvl, 0);
    }

    /**
     * Constructor. The column number is without counting the <code>id</code>
     * column. The minimum column value is set to 0 (zero), the maximum column
     * value is set to 100.
     * 
     * @param cols
     *            number of columns of random data
     * @param rows
     *            number of rows.
     * @param offset
     *            number of rows thrown away before returning the first row
     * @param lvl
     *            overall level of the tuples produced
     */
    public FlatLCUnsharpLevelRandomResultSet(int cols, int rows, int lvl,
	    int offset) {
	this(cols, rows, lvl, offset, DEFAULT_MAX);
    }

    /**
     * Constructor. The column number is without counting the <code>id</code>
     * column. The overall level of returned columns is set to the default
     * value, <code>(maximum * cols) / 2</code>.
     * 
     * @param cols
     *            number of columns of random data
     * @param rows
     *            number of rows
     * @param lvl
     *            overall level of the tuples produced
     * @param offset
     *            number of rows thrown away before returning the first row
     * @param maximum
     *            maximum level value for each column
     */
    public FlatLCUnsharpLevelRandomResultSet(int cols, int rows, int lvl,
	    int offset, int maximum) {
	this.rows = rows;
	this.offset = offset;
	this.lvl = lvl;
	this.maxValues = new int[cols];
	for (int i = 0; i < maxValues.length; i++) {
	    maxValues[i] = maximum;
	}
	init();
    }

    /**
     * Constructor. The level the most tuples will belong to is the middle level
     * of the BTG (i. e. the widest level).
     * 
     * @param rows
     *            number of rows
     * @param maxValues
     *            maximum level values
     */
    public FlatLCUnsharpLevelRandomResultSet(int rows, int[] maxValues) {
	this.rows = rows;
	this.maxValues = maxValues;
	this.lvl = maxValues[0];
	for (int i = maxValues.length; --i > 0;) {
	    this.lvl += maxValues[i];
	}
	this.lvl /= 2;
	init();
    }

    /**
     * Constructor
     * 
     * @param rows
     *            number of rows
     * @param maxValues
     *            maximum level values
     * @param lvl
     *            overall level of the tuples produced
     */
    public FlatLCUnsharpLevelRandomResultSet(int rows, int[] maxValues, int lvl) {
	this.rows = rows;
	this.maxValues = maxValues;
	this.lvl = lvl;
	init();
    }

    /**
     * Constructor
     * 
     * @param rows
     *            number of rows
     * @param maxValues
     *            maximum level values
     * @param lvl
     *            overall level of the tuples produced
     * @param offset
     *            number of rows thrown away before returning the first row
     */
    public FlatLCUnsharpLevelRandomResultSet(int rows, int[] maxValues,
	    int lvl, int offset) {
	this.rows = rows;
	this.maxValues = maxValues;
	this.offset = offset;
	this.lvl = lvl;
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
	generator = new Random(0);
	generatorNextVal = new Random(0);

	// compute the multiplicators and the maximum level value
	this.multIDs = new int[len];
	this.multIDs[len - 1] = 1;
	this.maxValue = maxValues[0];
	for (int i = len; --i > 0;) {
	    this.multIDs[i - 1] = this.multIDs[i] * (maxValues[i] + 1);
	    this.maxValue += maxValues[i];
	}

	// check if the level to be returned is possible
	// with the given component maximum level values:
	// if not, the (level modulo maxValue) is taken
	if (this.lvl > this.maxValue) {
	    this.lvl = this.lvl % this.maxValue;
	}

	// compute the intervals
	this.interval = new int[maxValue];
	int curr = 0;
	for (int i = 0; i < len; i++) {
	    for (int j = curr; j < curr + maxValues[i]; j++) {
		this.interval[j] = i;
	    }
	    curr += maxValues[i];
	}

	// remove the offset
	for (int i = 0; i < offset; i++) {
	    next();
	}
	// reset the row count
	currentRow = 0;
    }

    public void reset() {
	init();
    }

    public Object getMetaData() {
	if (meta == null) {
	    meta = new RandomResultSetMetaData(maxValues);
	}
	return meta;
    }

    /**
     * Generate a new tuple with values in the interval of [0,1[.
     * 
     * <p>
     * Remark: In opposite to most other classes, the class here uses the values
     * generated by method <code>next()</code> to generate the results of
     * <code>nextVal()</code>. For each integer value from
     * <code>next</next>, a double value in the
     * accordant interval is found. Values in one interval are
     * uniformly distributed.
     * </p>
     */
    public double[] nextVal() {
	FlatLevelCombination flc = (FlatLevelCombination) next();
	--currentRow;
	double[] result = new double[maxValues.length];

	for (int i = 0; i < maxValues.length; i++) {
	    // compute interval
	    double part = 1.0 / (1.0 + maxValues[i]);
	    // compute interval start
	    result[i] = flc.getLevel(i) * part;
	    // form uniform distibution in interval
	    result[i] += generatorNextVal.nextDouble() * part;
	}

	return result;
    }

    public boolean hasNext() throws IllegalStateException {
	return currentRow < rows;
    }

    int[] offsets = new int[20];

    public Object next() throws IllegalStateException, NoSuchElementException {
	FlatLevelCombination result = null;
	if (currentRow++ < rows) {
	    // generate a new row
	    Object[] object = new Number[maxValues.length];
	    int[] levels = new int[maxValues.length];
	    // compute offset from exact "lvl" level to create unsharpness
	    int off = (int) (generator.nextGaussian()) / 2;
	    ++offsets[off + 10];
	    // while (-2 > off || off > 2) {
	    // off = (int) (generator.nextGaussian() / 2);
	    // }
	    int level = lvl + off;
	    // add exactly "level" times one to one of the values
	    for (int i = 0; i < level; i++) {
		// choose the component to which is added now
		int index = interval[generator.nextInt(maxValue)];

		if (levels[index] < maxValues[index]) {
		    // increase value
		    levels[index]++;
		} else {
		    // level value at computed index has reached maximum: try
		    // again
		    i--;
		}
	    }
	    // fill object array
	    for (int i = 0; i < levels.length; i++) {
		object[i] = new Integer(levels[i]);
	    }
	    result = new FlatLevelCombination(levels, object, maxValues,
		    multIDs);
	}
	return result;
    }

    public Object peek() throws IllegalStateException, NoSuchElementException,
	    UnsupportedOperationException {
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

    public void remove() throws IllegalStateException,
	    UnsupportedOperationException {
	throw new UnsupportedOperationException("remove is not supported");
    }

    public void update(Object arg0) throws IllegalStateException,
	    UnsupportedOperationException {
	throw new UnsupportedOperationException("update is not supported");
    }

    public static void main(String[] args) {
	final int ROWS = 1000;
	final int[] MAXVALUES = new int[] { 2, 2, 1, 4 };
	final int LEVEL = 6;
	FlatLCUnsharpLevelRandomResultSet test = new FlatLCUnsharpLevelRandomResultSet(
		ROWS, MAXVALUES, LEVEL, 0);
	// for (int i = 0; i < ROWS; i++) {
	// double[] nxt = test.nextVal();
	// //System.out.print(nxt[0]);
	// int sumIntValues = (int) (nxt[0] * (MAXVALUES[0] + 1));
	// StringBuffer intValues = new StringBuffer("\t").append((int) (nxt[0]
	// * (MAXVALUES[0] + 1)));
	// for (int j = 1; j < nxt.length; j++) {
	// //System.out.print("," + nxt[j]);
	// sumIntValues += (int) (nxt[j] * (MAXVALUES[j] + 1));
	// intValues.append(',').append((int) (nxt[j] * (MAXVALUES[j] + 1)));
	// }
	// //System.out.println(intValues);
	// if (sumIntValues != LEVEL) {
	// System.out.println("FEHLER in ZEILE " + i + ": Level " + intValues +
	// " statt " + LEVEL);
	// }
	// }
	test = new FlatLCUnsharpLevelRandomResultSet(ROWS, MAXVALUES);
	System.exit(0);
	while (test.hasNext()) {
	    System.out.println(test.next());
	    // test.next();
	}
	System.out.println("Offset-Anteile:");
	for (int i = 0; i < test.offsets.length; i++) {
	    int cur = test.offsets[i];
	    double anteil = (double) cur / ROWS;
	    double percent = ((int) (anteil * 10000.0)) / 100;
	    System.out.println((i - 10) + ": " + cur + "\t entspricht "
		    + percent + " %");
	}
	// System.exit(0);

	System.out.println("===================");
	int[] mx = new int[] { 4, 2, 1, 5, 3 };
	test = new FlatLCUnsharpLevelRandomResultSet(ROWS, mx, 3, 0);
	while (test.hasNext()) {
	    System.out.println(test.next());
	}
    }

    public ArrayList<Object> getElements() {
	throw new UnsupportedOperationException(
		"Not a valid operation in FlatLCUnsharpLevelRandomResultSet");
    }

}
