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


import ec.EquivalenceClass;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * User: endresma
 * Date: 31.01.13
 * Time: 16:40
 * <p/>
 * This class represents the BTG as an array of levels. Each level contains
 * a HashMap with a pair of ID and EquivalenceClass.
 * This version is NOT thread-safe!
 */
public class BTGDataLevelBasedHashMap extends BTGDataLevelBasedA {

    private Iterator<Integer> keySetIterator = null;

    /**
     * ctor.
     *
     * @param btg
     * @return
     */
    public BTGDataLevelBasedHashMap(BTG btg) {
        super(btg);
        // array of HashMaps
        this.data = new ConcurrentHashMap[btg.getMaxLevel() + 1];
        this.usedClasses = new ConcurrentHashMap[btg.getMaxLevel() + 1];

        minLevel = btg.getMaxLevel();
    }


    @Override
    protected Map getNewMap(int level) {
        int initialCapacity = btg.getWidth(level);
        return new ConcurrentHashMap<Integer, EquivalenceClass>(initialCapacity, 100);
    }


    @Override
    public int getNextOccupiedNode(int current) {

        Integer next = null;
        // int currentLevel = level;

        if (keySetIterator != null && keySetIterator.hasNext()) {
            next = keySetIterator.next();
        } else {
            int currentLevel = btg.getOverallLevel(current);

            keySetIterator = null;
            while (keySetIterator == null && currentLevel < btg.getMaxLevel()) {
                // no key found in currentLevel, goto next level
                currentLevel++;

                if (currentLevel > btg.getMaxLevel()) {
                    return END_OF_DATA;
                }
                if (data[currentLevel] != null) {
                    keySetIterator = data[currentLevel].keySet().iterator();
                    if (keySetIterator.hasNext()) {
                        next = keySetIterator.next();
                    } else {
                        keySetIterator = null;
                    }
                }
            }
        }

        if (next == null) {
            return END_OF_DATA; // no more data found
        }

        // otherwise there is a next node
        return next;
    }


    @Override
    public int getFirstNode() {
        Map<Integer, EquivalenceClass> tmp = data[minLevel];
        Set keySet = tmp.keySet();

        keySetIterator = keySet.iterator();

        return keySetIterator.next();


    }


}
