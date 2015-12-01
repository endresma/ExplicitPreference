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

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;


/**
 * User: endresma
 * Date: 31.01.13
 * Time: 16:40
 * <p/>
 * This class represents the BTG as an array of levels. Each level contains
 * a SkipList with a pair of ID and EquivalenceClass.
 */
public class BTGDataLevelBasedSkipList extends BTGDataLevelBasedA {


    /**
     * ctor.
     *
     * @param btg
     */
    public BTGDataLevelBasedSkipList(BTG btg) {
        super(btg);
        // array of SkipLists
//        this.data = new ConcurrentSkipListMap[btg.getMaxLevel() + 1];
        this.data = new ConcurrentSkipListMap[btg.getMaxLevel() + 1];
        this.usedClasses = new ConcurrentSkipListMap[btg.getMaxLevel() + 1];

//        for (int i = 0; i < btg.getMaxLevel() + 1; i++) {
//            usedClasses[i] = new ConcurrentSkipListMap<>();
//        }

        minLevel = btg.getMaxLevel();

//        System.out.println("SkipList -> BTG size: " + btg.getSize());
//        System.out.println("Allocation: " + allocation);

    }


    @Override
    protected Map getNewMap(int level) {
        return new ConcurrentSkipListMap<Integer, EquivalenceClass>();
    }


    @Override
    public int getNextOccupiedNode(int current) {
        int level = btg.getOverallLevel(current);
        // get first entry after the entry with ID = current


        Integer next = null;
        int currentLevel = level;


        ConcurrentSkipListMap<Integer, EquivalenceClass> currentMap = (ConcurrentSkipListMap<Integer, EquivalenceClass>) data[currentLevel];

        if (data[currentLevel] != null)
//            next = data[currentLevel].higherKey(current);
            next = currentMap.higherKey(current);
        // this level is empty, take the next level;
        while (next == null && currentLevel < btg.getMaxLevel()) {
            // no key found in currentLevel, goto next level
            currentLevel++;

            if (data[currentLevel] != null) {
                if (!data[currentLevel].isEmpty()) {
                    currentMap = (ConcurrentSkipListMap<Integer, EquivalenceClass>) data[currentLevel];
                    next = currentMap.firstKey();
                }
//                    next = data[currentLevel].firstKey();
            }
        }

        if (next == null || currentLevel > btg.getMaxLevel()) {
            return END_OF_DATA; // no more data found
        }

        // otherwise there is a next node
        return next;
    }


    @Override
    public int getFirstNode() {
        ConcurrentSkipListMap<Integer, EquivalenceClass> minMap = (ConcurrentSkipListMap<Integer, EquivalenceClass>) data[minLevel];
        return minMap.firstKey();
    }


}
