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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.NoSuchElementException;


public class FlatLCSortedAntiCorrelatedResultSet extends FlatLCAntiCorrelatedResultSet {
	/**
	 * comparator for producing a sorted set.
	 */
	Comparator comparator;

	/**
	 * sorted set with objects that will be returned by <code>next()</code>
	 */
	ArrayList elements;

	/**
	 * Constructor. The column number is without counting the <code>id</code>
	 * column. The maximum column value is set to <code>DEFAULT_MAX</code>.
	 * 
	 * @param cols
	 *            number of columns of random data
	 * @param rows
	 *            number of rows
	 * @param comp
	 *            comparator to order the produced flat level combinations
	 */
	public FlatLCSortedAntiCorrelatedResultSet(int cols, int rows,
			Comparator comp) {
		this(cols, rows, DEFAULT_MAX, 0, comp);
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
	 * @param comp
	 *            comparator to order the produced flat level combinations
	 */
	public FlatLCSortedAntiCorrelatedResultSet(int cols, int rows, int offset,
			Comparator comp) {
		this(cols, rows, DEFAULT_MAX, offset, comp);
	}

	/**
	 * Constructor. The column number is without counting the <code>id</code>
	 * column.
	 * 
	 * @param cols
	 *            number of columns of random data
	 * @param rows
	 *            number of rows
	 * @param maximum
	 *            maximum level value
	 * @param offset
	 *            number of rows thrown away before returning the first row
	 * @param comp
	 *            comparator to order the produced flat level combinations
	 */
	public FlatLCSortedAntiCorrelatedResultSet(int cols, int rows, int maximum,
			int offset, Comparator comp) {
		super(cols, rows, maximum, offset);
		if (comp == null) {
			throw new IllegalArgumentException("comparator must not be null");
		}
		this.comparator = comp;
		init();
	}

	/**
	 * Constructor
	 * 
	 * @param rows
	 *            number of rows
	 * @param maxValues
	 *            maximum level values
	 * @param comp
	 *            comparator to order the produced flat level combinations
	 */
	public FlatLCSortedAntiCorrelatedResultSet(int rows, int[] maxValues,
			Comparator comp) {
		this(rows, maxValues, 0, comp);
	}

	/**
	 * Constructor
	 * 
	 * @param rows
	 *            number of rows
	 * @param maxValues
	 *            maximum level values
	 * @param offset
	 *            number of rows thrown away before returning the first row
	 * @param comp
	 *            comparator to order the produced flat level combinations
	 */
	public FlatLCSortedAntiCorrelatedResultSet(int rows, int[] maxValues,
			int offset, Comparator comp) {
		super(rows, maxValues, offset);
		if (comp == null) {
			throw new IllegalArgumentException("comparator must not be null");
		}
		this.comparator = comp;
		init();
	}

	/**
	 * Initializes the random number generators.
	 */
	private void init() {
		// skip the offset
		for (int i = 0; i < offset; i++) {
			super.next();
		}
		super.currentRow = 0;

		// create all tuples and add them to the list
		elements = new ArrayList(rows);
		for (int i = rows; --i >= 0;) {
			elements.add(super.next());
		}

		// then sort the list
		Collections.sort(elements, comparator);

		currentRow = 0;
	}

	public void reset() {
		currentRow = 0;
	}

	public Object getMetaData() {
		if (meta == null) {
			meta = new RandomResultSetMetaData(maxValues);
		}
		return meta;
	}

	public boolean hasNext() throws IllegalStateException {
		return elements.size() > currentRow;
	}

	public Object next() throws IllegalStateException, NoSuchElementException {
		return elements.get(currentRow++);
	}

	public Object peek() throws IllegalStateException, NoSuchElementException,
			UnsupportedOperationException {
		return elements.get(currentRow);
	}

	public boolean supportsPeek() {
		return true;
	}

	public boolean supportsReset() {
		return true;
	}

	public void remove() throws IllegalStateException,
			UnsupportedOperationException {
		throw new UnsupportedOperationException("remove is not supported");
	}

	public void update(Object arg0) throws IllegalStateException,
			UnsupportedOperationException {
		throw new UnsupportedOperationException("update is not supported");
	}

	public ArrayList<Object> getElements() {
	    return elements;
	}
	
}
