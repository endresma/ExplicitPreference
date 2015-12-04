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

package lse;


import btg.BTGDataA;
import btg.BTGDataI;
import btg.BTGDataLevelBasedA;
import flatlc.levels.FlatLevelCombination;
import util.HashMapWrapper;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * User: endresma
 * Date: 03.07.2015
 * Sequentiel implementation of Lattice Skyline / Hexagon for EXPLICIT evaluation based on the
 * embedding of arbitrary strict partial orders into lattices.
 */
public class LSE extends AbstractLS {


    private HashMapWrapper keySignature;

    public LSE(Iterator input, final BTGDataA btg, HashMapWrapper keySignature) {
        super(input, btg);
        this.keySignature = keySignature;


    }


    protected boolean addObject(Object object) {


        FlatLevelCombination flc = (FlatLevelCombination) object;
        // FIXME me, here I only use one-dimensional SPOs
        // just for testing the algorithm before implementing LSE

        int[] flcComb = flc.getLevelCombination();
        Object[] obj = new Object[flcComb.length];
        for(int i=0; i<obj.length; ++i)
            obj[i] = flcComb[i];
        //        int obj = objArr[0];

        int id = -1;

        if (keySignature.containsKey(obj)) {
            //        int[] lc;
            //        if((lc = containsKey(obj)) != null) {
//            int[] lc = convertArrayList2Array(keySignature.get(obj));
            int[] lc = keySignature.get(obj);

            id = btg.getID(lc);


        } else {     // put object to the worst node
            id = btg.getSize() - 1;
        }


        return btg.addObject(id, object);


    }




    private int[] convertArrayList2Array(ArrayList<Integer> list) {
        int counter = list.size();

        int[] arr = new int[counter];

        for (int i = 0; i < counter; ++i) {
            arr[i] = list.get(i);
        }

        return arr;
    }


    public Iterator<Object> getResults() {

        //        long begin = System.currentTimeMillis();
        removeDominated();
        //        long end = System.currentTimeMillis();
        //        long fdRuntime = end - begin;

        //        System.out.println("Time to remove dominated nodes (sec): " + (fdRuntime/1000.));

        return btg;
    }

    @Override
    protected void computeResult() {

        //            MemoryObserver.initMemory();
        // give all input
        //            long begin = System.currentTimeMillis();
        while (input.hasNext()) {
            this.addObject(input.next());
        }

        //            long end = System.currentTimeMillis();
        //            System.out.println("Time to add objects in ms: " + (end - begin));
        //            System.out.println("Allocation (number of occupied nodes): " + getAllocation());
        //            System.out.println("Allocation (percentage):  " + (getAllocation() / (double) btg.getSize() * 100));

        //            int idCounter = 0;
        //
        //            for (int i = 0; i < ids.length; i++) {
        //                if (ids[i] != 0) {
        //                    System.out.println("ID: " + i + " : " + ids[i]);
        //                    idCounter++;
        //                }
        //            }
        //
        //            System.out.println("Occupied ECs: " + idCounter);


        //        long begin = System.currentTimeMillis();


        result = this.getResults();

        //            HashMap<Integer, Integer> hash = new HashMap<Integer, Integer>();
        //            int counter = 0;
        //            while (result.hasNext()) {
        //                FlatLevelCombination o = (FlatLevelCombination) result.next();
        //                int ident = o.getIdentifier();
        //                if (hash.containsKey(ident)) {
        //                    hash.put(ident, hash.get(ident) + 1);
        //                } else {
        //                    hash.put(ident, 1);
        //                    ArrayPrinter.println("" + ident, o.getLevelCombination());
        //                }
        //
        //                counter++;
        //            }//


        //            System.out.println("Hexagon Counter: " + counter);


        if (result.hasNext())
            peek = result.next();

        //        long end = System.currentTimeMillis();
        //        System.out.println("Time to compute result in ms: " + (end -
        //                begin));
        //        long currentMemory = MemoryObserver.currentMemory();
        //        System.out.println(getClass().getSimpleName() + " --- Hexaon MemoryObserver currentMemory in MB: : " +
        //                currentMemory
        //                /  1024. / 1024.);

    }


    /**
     * Remove Phase
     * removeDominated is called from @see AbstractHexagonInMemoryFLC
     * .getResults() and removes dominated nodes from the BTG
     */
    protected void removeDominated() {

        if (btg.getFirstNode() == 0) {
            System.out.println("Zero node occupied, return -------------");
            // only tuples with a level of 0 belong to the result set
            //            btg.next[0] = -1;
            btg.removeBetween(0, BTGDataI.END_OF_DATA);
            return;
        }
        //        }


        int current;
        current = 1;

        int position;

        while (current != BTGDataI.END_OF_DATA) {
            if (btg.getEC(current) != null) {

                for (int i = 0; i < btg.getMaxLevels().length; i++) {

                    position = current + btg.getWeight(i);
                    if (position < btg.getSize() && btg.getOverallLevel(current) + 1 == btg.getOverallLevel(position)) {
                        walkDown(position, i, true);
                    }
                }
            } else {
                // current node does not contain elements => remove it from list
                if (!(btg instanceof BTGDataLevelBasedA))
                    btg.removeBetween(btg.getPrevNode(current), btg.getNextNode(current));
                //                }
            }

            if (btg instanceof BTGDataLevelBasedA)
                current = btg.getNextOccupiedNode(current);
            else
                current = btg.getNextNode(current);

        }

        if (!(btg instanceof BTGDataLevelBasedA))
            btg.setFirstNode(btg.getNextNode(0));

    }


    /**
     * Sequential walk down the BTG and visit all dominated nodes.
     * <p/>
     * DFS, depth first search.
     * Walk down the BTG and visit all dominated nodes
     *
     * @param position start of the walk
     * @param edge     the edge the algorithm is "coming down"
     */

    protected void walkDown(int position, int edge, boolean remove) {

        FlatLevelCombination lc = null;

        //        if (btg.getEC(position) == null) {
        //            // node does not exist
        //            if (btg.isUsedClass(position)) {
        //                // node has been visited
        //                return;
        //            }
        //            // node has not been visited: walk down followers
        //            lc = btg.getBTG().constructLevelCombination(position);
        //        } else {
        //            lc = btg.getEC(position).getLevelCombination();
        //
        //        }

        lc = btg.getBTG().constructLevelCombination(position);

        //        System.out.println("lc position " + position + " : " + lc);


        //        lc = btg.getBTG().constructLevelCombination(position);

        //        System.out.println("Position: " + position);

        for (int i = 0; i <= edge; i++) {
            if (lc.getLevel(i) < btg.getLevel(i)) {
                // Erhoehung moeglich
                walkDown(position + btg.getWeight(i), i, remove);
            }
        }
        // remove element at current position
        // keep in mind that the element has been visited
        if (remove) {
            btg.removeEntry(position);
            btg.setUsedClass(position, true);

            if (!(btg instanceof BTGDataLevelBasedA))
                btg.removeBetween(btg.getPrevNode(position), btg.getNextNode(position));
        }


    }

}
