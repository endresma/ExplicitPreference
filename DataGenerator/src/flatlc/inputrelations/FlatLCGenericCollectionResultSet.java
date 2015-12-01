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
import mdc.MetaDataCursor;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;


/**
 * A generic flat lc collection which is possible to return several iterators.
 * Necessary for parallel iteration over the generated FlatLevelCombinations,
 * e.g. for SemiParetoIntersection.
 * 
 * @author endresma
 * 
 */
public class FlatLCGenericCollectionResultSet implements Collection {

	private Object[] result;
	private Object metaData;

	public FlatLCGenericCollectionResultSet(MetaDataCursor flc) {

		// result = new LinkedList();
		FlatLCResultSetA flcData = (FlatLCResultSetA) flc;
		// System.out.println("Data.size: " + flcData.getSize());
		result = new Object[flcData.getSize()];
		this.metaData = flc.getMetaData();

		int counter = 0;
		// while (flc.hasNext()) {
		// result.add((FlatLevelCombination)flc.next());
		// }
		while (flc.hasNext()) {
			result[counter++] = flc.next();
		}

	}

	@Override
	public boolean add(Object o) {
		throw new UnsupportedOperationException(
				"not supported: FlatLCGenericCollectionResultSet");
		// return result.add(o);
	}

	@Override
	public boolean addAll(Collection c) {
		// return result.addAll(c);
		throw new UnsupportedOperationException(
				"not supported: FlatLCGenericCollectionResultSet");
	}

	@Override
	public void clear() {
		// result.clear();
		throw new UnsupportedOperationException(
				"not supported: FlatLCGenericCollectionResultSet");
	}

	@Override
	public boolean contains(Object o) {
		// return result.contains(o);
		throw new UnsupportedOperationException(
				"not supported: FlatLCGenericCollectionResultSet");
	}

	@Override
	public boolean containsAll(Collection c) {
		// return result.containsAll(c);
		throw new UnsupportedOperationException(
				"not supported: FlatLCGenericCollectionResultSet");
	}

	@Override
	public boolean isEmpty() {
		// return result.isEmpty();
		throw new UnsupportedOperationException(
				"not supported: FlatLCGenericCollectionResultSet");
	}

	@Override
	public Iterator iterator() {
		// return result.iterator();
		return new FlatLCGenericCollectionResultSetIterator(result, metaData);
	}

	@Override
	public boolean remove(Object o) {
		// return result.remove(o);
		throw new UnsupportedOperationException(
				"not supported: FlatLCGenericCollectionResultSet");
	}

	@Override
	public boolean removeAll(Collection c) {
		throw new UnsupportedOperationException(
				"not supported: FlatLCGenericCollectionResultSet");
	}

	@Override
	public boolean retainAll(Collection c) {
		// return result.retainAll(c);
		throw new UnsupportedOperationException(
				"not supported: FlatLCGenericCollectionResultSet");
	}

	@Override
	public int size() {
		return result.length;

	}

	@Override
	public Object[] toArray() {
		return result;
	}

	@Override
	public Object[] toArray(Object[] a) {
		// return result.toArray(a);
		throw new UnsupportedOperationException(
				"not supported: FlatLCGenericCollectionResultSet");
	}

}

class FlatLCGenericCollectionResultSetIterator implements MetaDataCursor {

	private int position = 0;
	private Object[] result;
	private Object metaData;

	FlatLCGenericCollectionResultSetIterator(Object[] result, Object metaData) {
		this.result = result;
		this.metaData = metaData;
	}

	@Override
	public boolean hasNext() {
		return position < result.length;
	}

	@Override
	public Object next() {
		/* necessary, because each iterator returns a reference to the object in
		 * result[]. If one iterator modifies a object, it also modifies the
		 * object in result[] and therefore for all iterators.
		 */
		FlatLevelCombination flcCopy = new FlatLevelCombination(
				(FlatLevelCombination) result[position++]);
		flcCopy.setNextNull();
		return flcCopy;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException(
				"not supported: FlatLCGenericCollectionResultSet");
	}

	@Override
	public Object getMetaData() {
		return metaData;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
	}

	@Override
	public void open() {
		// TODO Auto-generated method stub

	}

	@Override
	public Object peek() throws IllegalStateException, NoSuchElementException,
			UnsupportedOperationException {
		return result[position + 1];
	}

	@Override
	public void reset() throws UnsupportedOperationException {
		position = 0;
	}

	@Override
	public boolean supportsPeek() {
		return true;
	}

	@Override
	public boolean supportsRemove() {

		return false;
	}

	@Override
	public boolean supportsReset() {
		return true;
	}

	@Override
	public boolean supportsUpdate() {
		return false;
	}

	@Override
	public void update(Object arg0) throws IllegalStateException,
			UnsupportedOperationException {

	}

}
