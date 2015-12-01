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

package ec;



import flatlc.levels.FlatLevelCombination;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;


public interface IEquivalenceClass extends Comparable<Object> {

   /**
    * Adds one element to the equivalence class. Elements with different level
    * combinations will be added as well.
    *
    * @param level the level combination of the object to be added.
    */
    void add(FlatLevelCombination level);

   /**
    * Adds one element to the equivalence class. Elements with different level
    * combinations will not be added!
    *
    * @param level the level combination of the object to be added. It is
    *              important that the level combination object contains the
    *              element to be added.
    * @return <code>true</code> if the element could be added,
    *         <code>false</code> otherwise.
    */
    boolean checkedAdd(FlatLevelCombination level);

   /**
    * Adds an object to this equivalence class. It is not checked that the
    * object really belongs to this equivalence class.
    *
    * @param object the object to add
    * @return <code>true</code>
    */
    boolean add(Object object);

   /**
    * Adds all preference objects from a given equivalence class if (and only
    * if) they represent the same level combination.
    *
    * @param ec the equivalence class of the object to be added.
    * @return <code>true</code> if the elements could be added,
    *         <code>false</code> otherwise.
    */
    boolean add(IEquivalenceClass ec);

   /**
    * Returns the <code>preference.csv.optimize.LevelCombination</code>-Object
    * of the equivalence class.
    *
    * @return the equivalence class's level combination object
    */
    FlatLevelCombination getLevelCombination();


   /**
    * Returns all elements of the equivalence class sorted by a given
    * comparator.
    *
    * @param comparator comparator for sorting
    * @return an <code>java.util.ArrayList</code>-Object containing all
    *         elements sorted by the comparator
    */
    List<Object> getElementsSorted(Comparator<Object> comparator);


   /**
    * Returns all elements of the equivalence class.
    *
    * @return an <code>java.util.ArrayList</code>-Object containing all
    *         elements
    * @uml.property name="elements"
    */
    List<Object> getElements();

   /**
    * Returns an iterator over all elements of the equivalence class
    *
    * @return an <code>java.util.Iterator</code> over all contained elements
    */
    Iterator<Object> iterator();

   /**
    * Returns the number of elements in this equivalence class.
    *
    * @return the number of elements in this equivalence class
    */
    int getSize();

   /**
    * Returns the element at the specified position.
    *
    * @param index the index of the result to return.
    * @return the requested result
    */
    Object getElement(int index);

    boolean equals(Object obj);

    int hashCode();

    String toString();

   /**
    * Compares <code>this</code> equivalence class with another one. The
    * comparison is done by their <code>LevelCombination</code> objects.
    *
    * @param ec the equivalence class to compare to
    * @return number indicating which equivalence class is better (or if they
    *         are equal, substitable, incomparable, etc.)
    */
    int compare(IEquivalenceClass ec);

    int compareTo(Object o);




}
