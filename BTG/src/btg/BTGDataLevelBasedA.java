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
 * User: endresma
 * Date: 26.02.13
 * Time: 16:30
 * <p/>
 * This class represents the BTG as an array of levels.
 * This is the abstract class for all btg structures Array -> Map
 */
public abstract class BTGDataLevelBasedA extends BTGDataA {

    /**
     * Holds the data of the BTG,
     * i.e. the nodes (equivalence classes)
     */
    protected Map<Integer, EquivalenceClass> data[];

    /**
     * describes if a node is used or not
     */
    protected Map<Integer, Boolean> usedClasses[];

    /**
     * ctor.
     *
     * @return
     */
    public BTGDataLevelBasedA(BTG btg) {
        super(btg);
    }


    @Override
    public Map getLevelData(int level) {
        return data[level];
    }


    /**
     * returns a new dynamic Map (HashMap, SkipList, etc.) for the addObject
     * method with a load factor of 100.
     *
     * @param level
     * @return
     */
    protected abstract Map getNewMap(int level);


    @Override
    public void removeEntry(int id) {
        int level = btg.getOverallLevel(id);
        if (data[level] != null)
            data[level].remove(id);
    }

    @Override
    public int getMinLevel() {
        return minLevel;
    }


    @Override
    public void removeLevelIfEmpty(int level) {
        if (data[level].isEmpty()) {
            data[level] = null;
        }
    }


    @Override
    public void removeLevel(int level) {
        data[level] = null;
    }


    @Override
    public boolean isUsedClass(int id) {
        int level = btg.getOverallLevel(id);
        if (usedClasses[level] != null) {
            if (usedClasses[level].get(id) != null)
                return true;
        }

        return false;
    }


    @Override
    public void setUsedClass(int id, boolean value) {
        int level = btg.getOverallLevel(id);
        if (usedClasses[level] == null) {
            //            usedClasses[level] = new ConcurrentHashMap<>();
            usedClasses[level] = getNewMap(level);
        }

        usedClasses[level].put(id, value);
    }


    @Override
    public IEquivalenceClass getEC(int id) {
        int level = btg.getOverallLevel(id);

        if (data[level] != null) {
            if (data[level].get(id) != null)
                return data[level].get(id);
        }

        return null;

    }


    @Override
    public boolean isDominated(int id) {
        //        if (id <= 0)
        //            return true;
        //
        //        int level = btg.getOverallLevel(id);
        //
        //        if (data[level] == null) {
        //            return false;
        //        }
        //
        //        if (data[level].get(id) == null) {
        //            return false;
        //        }
        //
        //        return data[level].get(id).isDominated();
        throw new UnsupportedOperationException(("isDominated in " + getClass().getName() + " not supported yet"));
    }

    public void removeAndSetUsed(int id) {
        throw new UnsupportedOperationException(("removeAndSetUsed in " + getClass().getName() + " not supported yet"));
    }

//    @Override
//    public LevelCombination getLC(int id) {
//        throw new UnsupportedOperationException(("getLC in " + getClass().getName() + " not supported yet"));
//    }

    @Override
    public void setEC(int id, IEquivalenceClass ec) {
        int level = btg.getOverallLevel(id);
        data[level].put(id, (EquivalenceClass) ec);
    }


    protected EquivalenceClass getNewEquivalenceClass(int id, Object object)  {
//        return new EquivalenceClass(btg.getLevelManager().getLevelInstance(object, null));
        FlatLevelCombination lvl = btg.getLevelCombination(object);
        return new EquivalenceClass(lvl);
    }

    @Override
    public boolean addObject(int id, Object object)  {

        // compute level for the node with the given id
        int level = btg.getOverallLevel(id);

        // initialize map if not done
        if (data[level] == null) {
            data[level] = getNewMap(level);

            EquivalenceClass equivalenceClass = getNewEquivalenceClass(id, object);

            data[level].put(id, equivalenceClass);
            allocation.incrementAndGet();
        } else { // map already initialized, check id
            if (data[level].containsKey(id)) {         // id already in the map

                data[level].get(id).add(object);
            } else { // id not in the map, insert new id
                EquivalenceClass equivalenceClass = getNewEquivalenceClass(id, object);
                //                        new EquivalenceClass(btg.getLevelManager().getLevelInstance(object, null));

                data[level].put(id, equivalenceClass);
                allocation.incrementAndGet();
            }
        }

        if (level < minLevel) {
            minLevel = level;
        }

        return true;
    }


    @Override
    public boolean checkedAddObject(int id, Object object) {
        throw new RuntimeException("checkedAddObject not supported in " + getClass().getName());
    }

    @Override
    public int getNextNode(int id) {
        throw new RuntimeException();
        //        return getNextOccupiedNode(id);
    }

    @Override
    public void removeBetween(int id) {
        int from = getPrevNode(id);
        int to = getNextNode(id);
        removeBetween(from, to);
    }

    @Override
    public void removeBetween(int from, int to) {
        int fromLevel = btg.getOverallLevel(from);

        int toLevel;
        if (to == BTGDataI.END_OF_DATA) {
            toLevel = data.length - 1;
        } else {
            toLevel = btg.getOverallLevel(to);
        }

        // all nodes lie in the same level
        if (fromLevel == toLevel) {
            if (data[fromLevel] != null) {
                for (int i = from + 1; i < to; i++) {
                    data[fromLevel].remove(i);
                    if (usedClasses[fromLevel] != null)
                        usedClasses[fromLevel].remove(i);
                }
            }
        } else { // from is in another level as to

            // delete all in the fromLevel
            if (data[fromLevel] != null) {
                for (int i = from + 1; i < data[fromLevel].size(); i++) {
                    data[fromLevel].remove(i);
                    if (usedClasses[fromLevel] != null)
                        usedClasses[fromLevel].remove(i);
                }
            }

            // delete all in higher levels
            for (int currentLevel = fromLevel + 1; currentLevel < toLevel; currentLevel++) {
                removeLevel(currentLevel);
                //                if (data[currentLevel] != null) {
                //                    for (int i = 0; i < data[currentLevel].size(); i++) {
                //                        data[currentLevel].remove(i);
                //                        if (usedClasses[currentLevel] != null)
                //                            usedClasses[currentLevel].remove(i);
                //                    }
                //                }
            }

            // delete all nodes til the to node
            if (data[toLevel] != null) {
                for (int i = 0; i < to; i++) {
                    data[fromLevel].remove(i);
                    if (usedClasses[fromLevel] != null)
                        usedClasses[fromLevel].remove(i);
                }
            }

        }
    }

    //    @Override
    //    public void removeBetween(int id) {
    //        throw new RuntimeException("BTGDataLevelBasedA.removeBetween(int id) " +
    //                "not supported");
    //    }


    @Override
    public int getPrevNode(int id) {
        throw new RuntimeException();
    }


    @Override
    public void setFirstNode(int id) {
        throw new RuntimeException("BTGDataLevelBasedHashMap.setFirstNode not" + " supported.");
    }

}
