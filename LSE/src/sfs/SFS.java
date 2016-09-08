/*
* Copyright (c) 2016. markus endres, timotheus preisinger
*
*     This program is free software: you can redistribute it and/or modify
*     it under the terms of the GNU General Public License as published by
*     the Free Software Foundation, either version 3 of the License, or
*     (at your option) any later version.
*
*     This program is distributed in the hope that it will be useful,
*     but WITHOUT ANY WARRANTY; without even the implied warranty of
*     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*     GNU General Public License for more details.
*
*     You should have received a copy of the GNU General Public License
*     along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/


package sfs;

import flatlc.levels.FlatLevelCombination;
import util.IPreference;
import util.TopSort;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * User: endresma
 * SFS
 */
public class SFS implements Iterator {

    protected ArrayList<Object> input;
    //    protected Iterator result;
    protected ArrayList result;
    /**
     * the preference used by this object
     */
    IPreference preference;
    int nrOfcomparison = 0;
    ArrayList<Long> domTime = new ArrayList<>();
    private int position = -1;
    private boolean print = false;

    public SFS(ArrayList<Object> input, IPreference preference) {
        this.preference = preference;
        this.input = input;

    }

    int counter = 0;

    protected void addObject(FlatLevelCombination cand) {

        int compare = IPreference.UNRANKED;
        long begin, end;
        begin = System.currentTimeMillis();

        counter++;
        if (print)
            System.out.println("Counter: " + counter + " SFS Result Size " + result.size());

        for (int i = 0; i < result.size(); ++i) {
            //            FlatLevelCombination flc_t1 = (FlatLevelCombination) result.get(i);


//            compare = preference.compare(cand, result.get(i));
            Object q = result.get(i);
            compare = preference.compare(q, cand);
            nrOfcomparison++;

            // t is worse than t1
            // FIXME: me ACHTUNG! das compare== IPreference.UNRANKED koennte evtl. was bei Pareto bzgl. der Auswertung
            // kaputt machen !!!
//            if (compare == IPreference.GREATER || compare == IPreference.EQUAL || compare == IPreference.UNRANKED) {
            if (compare == IPreference.GREATER || compare == IPreference.EQUAL) {
                if (print)
                    System.out.println("SFS Break at i = " + i);
                break;
            }
        }
        end = System.currentTimeMillis();
        domTime.add(end - begin);

        if (compare != IPreference.GREATER) {
            result.add(cand);
//            result.add(cand);
        }

    }


    @Override
    public boolean hasNext() {
        // compute the result set
        if (result == null) {
            result = new ArrayList();
            TopSort.sort(input);
//            for(int i=0; i<input.size(); ++i) {
//                input.get(i);
//            }
            //            while (input.hasNext()) {
            for (int i = 0; i < input.size(); ++i) {
                addObject((FlatLevelCombination) input.get(i));
            }

            long sum = 0;
            for (Long time : domTime) {
                sum += time;
            }
            if (print)
                System.out.println("Runtime dominance test in sum: " + sum);

            //            }
            //                compute();
            position = 0;
            System.out.println("*************** SFS nrOfComparison: " + nrOfcomparison);


        }

        return result.size() > position;
    }

    @Override
    public Object next() {
        return result.get(position++);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}


