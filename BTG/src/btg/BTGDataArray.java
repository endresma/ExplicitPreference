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
import ec.IEquivalenceClass;
import flatlc.levels.FlatLevelCombination;

import java.util.Map;

/**
 * @author endresma
 * <p/>
 * This class represents the BTG data structure as an array.
 */
public class BTGDataArray extends BTGDataA {

    protected IEquivalenceClass data[];

    protected int[] prev;
    protected int[] next;
    protected boolean[] usedClasses;
    protected int[] lvl;

    protected int[] first;
    protected int[] last;


    /**
     * minimal id found in input set
     */
    protected int minId;

    /**
     * ctor. Initialize array data structure (@see EquivalenceClass) to represent the BTG.
     *
     * @param btg
     */
    public BTGDataArray(final BTG btg) {
        super(btg);

        initDataStructure();


        this.usedClasses = new boolean[getSize()];
        this.last = new int[btg.getMaxLevel() + 1];
        this.first = new int[last.length];
        this.lvl = new int[getSize()];

        // init breadth-first walk
        initBFT();
    }


    protected void initDataStructure() {
        this.data = new EquivalenceClass[getSize()];
        // this.usedClasses = new boolean[classes];
        minId = getSize();
        this.prev = new int[getSize()];
        this.next = new int[getSize()];

    }


    /**
     * generate arrays for breadth-first walk
     */
    protected void initBFT() {

        last[0] = 0;
        // look at each node
        for (int i = 1; i < getSize(); i++) {
            int currLvl = btg.getOverallLevel(i);
            lvl[i] = currLvl;
            // next[]
            next[last[currLvl]] = i;
            if (first[currLvl] == 0) {
                first[currLvl] = i;
            }

            last[currLvl] = i;
        }

        for (int i = 1; i < first.length; i++) {
            next[last[i - 1]] = first[i];
            //            System.out.println("next[" + i + "] = " + next[i] + " : first[" + i + "] = " + first[i] + " : last[" + i
            //                    + "] = " + last[i]);


        }
        for (int i = 0; i < getSize(); i++) {
            prev[next[i]] = i;
        }
    }


    @Override
    public boolean addObject(int id, Object object) {
        if (usedClasses[id]) {
            // members of this class exist already.
            return data[id].add(object);
        } else {
            // first member of this equivalence class found
            //            data[id] = new EquivalenceClass(btg.getLevelManager().getLevelInstance(object, null));

            //            LevelCombination lvl = btg.getLevelManager().getLevelInstance(object, null);


            //            IPreference tmpPref = (ParetoPreference) (((BNLEquivalenceClass) factory).getPreference()).clone();//
            //            data[id] = new BNLEquivalenceClass(tmpPref, id, lvl);
            data[id] = getNewEquivalenceClass(id, object);


            allocation.incrementAndGet();

            this.usedClasses[id] = true;
            if (id < minId)
                minId = id;

            return true;
        }
    }

    @Override
    public boolean checkedAddObject(int id, Object object) {
        throw new RuntimeException("checkedAddObject not supported in " + getClass().getName());
    }

    /**
     * Create a new instance of equivalence class used in this data structure.
     *
     * @param object
     * @return
     */
    protected IEquivalenceClass getNewEquivalenceClass(int id, Object object) {
        FlatLevelCombination lvl = btg.getLevelCombination(object);

        return new EquivalenceClass(lvl);
    }


    @Override
    public void removeBetween(int from, int to) {

        if (to == END_OF_DATA) {
            next[from] = END_OF_DATA;
            prev[0] = from;
        } else {
            next[from] = to;
            prev[to] = from;
        }
    }

    @Override
    public void removeBetween(int id) {
        int from = getPrevNode(id);
        int to = getNextNode(id);
        removeBetween(from, to);
    }

    @Override
    public int getPrevNode(int id) {
        if (id <= 0)
            return END_OF_DATA;
        return prev[id];
    }

    @Override
    public boolean isDominated(int id) {
        throw new UnsupportedOperationException(("isDominated in " + getClass().getName() + " not supported yet"));
    }

    @Override
    public void removeAndSetUsed(int id) {
        throw new UnsupportedOperationException(("removeAndSetUsed in " + getClass().getName() + " not supported yet"));
    }

    //    @Override
    public FlatLevelCombination getLC(int id) {
        if (data[id] != null)
            return data[id].getLevelCombination();
        return btg.constructLevelCombination(id);
    }


    @Override
    public int getNextNode(int id) {
        if (id == (next.length - 1))
            //            return 0;
            return END_OF_DATA;

        return next[id];
    }

    @Override
    public int getNextOccupiedNode(int id) {
        do {
            id = getNextNode(id);
        } while (id != END_OF_DATA && id < getSize() && data[id] == null); //
        // id > 0

        if (id >= getSize())
            //            return -1;
            return END_OF_DATA;

        return id;
    }


    @Override
    public IEquivalenceClass getEC(int id) {
        return data[id];
    }

    @Override
    public void setEC(int id, IEquivalenceClass ec) {
        data[id] = ec;
    }

    @Override
    public int getFirstNode() {
        return minId;
    }

    @Override
    public void setFirstNode(int id) {
        this.minId = id;
    }

    @Override
    public void setUsedClass(int position, boolean value) {
        usedClasses[position] = value;
    }

    @Override
    public boolean isUsedClass(int id) {
        return usedClasses[id];
    }

    @Override
    public void removeLevelIfEmpty(int level) {
        throw new RuntimeException("BTGDataArray.removeLevelIfEmpty not " + "supported");
    }

    @Override
    public void removeLevel(int level) {
        for (int i = 0; i < data.length; i++) {
            if (getOverallLevel(i) == level) {
                removeEntry(i);
            }
        }

    }


    @Override
    public void removeEntry(int id) {
        data[id] = null;
        usedClasses[id] = false;
    }

    @Override
    public Map<Integer, IEquivalenceClass> getLevelData(int level) {
        throw new RuntimeException("BTGDataArray.getLevelData not supported");
    }


    @Override
    public int getMinLevel() {
        return btg.getOverallLevel(minId);
    }

}
