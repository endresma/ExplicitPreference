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

package btg;


import ec.IEquivalenceClass;

import java.util.Iterator;
import java.util.Map;

/**
 * @author endresma
 * Interface for all BTG data structures.
 */
 public interface BTGDataI extends Iterator {

    /**
     * mark the end of data in the BTG
     */
     final static int END_OF_DATA = -1;

    /**
     * Add a new object with the given id to the BTG data structure.
     *
     * @param id
     * @param object
     * @return boolean true if addObject was successful, otherwise false
     */
     boolean addObject(int id, Object object);


    /**
     * Only add the given object to this BTG node if the node is not
     * dominated.
     *
     * @param id
     * @param object
     * @return
     */
     boolean checkedAddObject(int id, Object object);


    /**
     * Return the number of occupied nodes in the BTG data structure.
     *
     * @return
     */
     int getAllocation();

    /**
     * Return the node id after the current node which is occupied with an
     * equivalence class.
     * Returns -1
     * if there is no more
     * node available.
     *
     * @param id
     * @return node after current node
     */
     int getNextOccupiedNode(int id);

    /**
     * Return the node id after the given id. Note, that this method returns
     * the next node, even if it is not occupied by any equivalence class.
     */
     int getNextNode(int id);


    /**
     * Remove all node id's between 'from' and 'to', not including.
     *
     * @param from
     * @param to
     * @return
     */
     void removeBetween(int from, int to);

    /**
     * Remove all node id's between the previous node of id and the next node
     * of id. Note, the prev node or the next node are not necessarily the
     * nodes with id-1 and id+1, respectively. The prev nodes and the next
     * nodes are considered concerning the data structure!
     *
     * @param id
     */
     void removeBetween(int id);

    /**
     * Return the node id before the given id. Note, that this method returns
     * the previous node, even if it is not occupied by any equivalence class.
     *
     * @param id node
     * @return int previous node
     */
     int getPrevNode(int id);

    /**
     * Return if the node with ID is domindated or not.
     */
     boolean isDominated(int id);


    /**
     * Atomically removes the equivalence class with the given id from the BTG and marks it as used.
     * This method is only atomic for thread safe BTG implementations.
     *
     * @param id
     */
     void removeAndSetUsed(int id);

//    /**
//     * Return the LevelCombination from the given ID.
//     *
//     * @param id
//     * @return
//     */
//     LevelCombination getLC(int id);


    /**
     * Return the first node in the data structure.
     *
     * @return first node in dats structure
     */
     int getFirstNode();


    /**
     * Returns true if there is at least one more element.
     *
     * @return true, if more elements. Otherwise false.
     */
     boolean hasNext();


    /**
     * Return next element
     *
     * @return Object next element from the data structure
     */
     Object next();


    /**
     * Removes an element. Not supported in the current version.
     */
     void remove();


    /**
     * Set the first node which should be considered in the data structure
     *
     * @param id
     */
     void setFirstNode(int id);


    /**
     * Sets a node in the BTG as visited or not.
     * The node is identified by the given level in the array of maps at
     * the given position, i.e. the ID of the node.
     *
     * @param id
     * @param value
     */
     void setUsedClass(int id, boolean value);


    /**
     * Returns if a node in the BTG was already visited during the
     * dominance traversal. Returns true if it is visited and dominated,
     * otherwise false.
     *
     * @param id
     * @return
     */
     boolean isUsedClass(int id);


    /**
     * Remove the dynamic data structure at the given level if it is empty,
     * i.e. set it to NULL.
     *
     * @param level
     */
     void removeLevelIfEmpty(int level);


    /**
     * Remove the the dynamic data structure at the given level,
     * i.e. set it to NULL.
     *
     * @param level
     */
     void removeLevel(int level);

    /**
     * Return the minimum occupied level in this data structure
     *
     * @return int minimal level with occupied nodes
     */
     int getMinLevel();

    /**
     * Delete id from the data structure.
     *
     * @param id
     */
     void removeEntry(int id);


    /**
     * Get the abstract map at the given level in the array of Maps.
     *
     * @param level
     * @return abstract map
     */
     Map getLevelData(int level);


    /**
     * Get the equivalence class at the given id.
     *
     * @param id
     * @return EquivalenceClass
     */
     IEquivalenceClass getEC(int id);


    /**
     * Set the equivalence class to the given value.
     */
     void setEC(int id, IEquivalenceClass ec);
}
