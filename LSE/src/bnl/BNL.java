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

package bnl;

import flatlc.levels.FlatLevelCombination;
import util.IPreference;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * An implementation of the BNL algorithm for <code>FlatLevelCombination</code>
 * objects. These objects will only be used as input objects. The level values
 * they contain will not be used - only the object attributes these level are
 * computed with. This behaviour is miming the standard BNL, although
 * the elements read are always kept in memory. No externalization is done.
 *
 * @author markus endres
 * @author Timotheus Preisinger
 * @version 2007-06-18
 */
public class BNL implements Iterator {
    /**
     * the preference used by this object
     */
    IPreference preference;

    /**
     * input cursor
     */
    Iterator input;

    /**
     * candidate list
     */
    ArrayList candidates;

    /**
     * position in candidate list while returning result set elements
     */
    int position = -1;
    int nrOfcomparison = 0;

    /**
     * Constructor.
     *
     * @param input      cursor on the input relation
     * @param preference the pareto preference this BNL is evaluating
     */
    public BNL(Iterator input, IPreference preference) {
        this.preference = preference;
        this.input = input;
    }

    private boolean addObject(FlatLevelCombination cand) {
        //	  System.out.println("bnl: " + cand);
        //	   System.out.println("Window Size: " + candidates.size());


        try {
            // FlatLevelCombination cand = (FlatLevelCombination) object;
            // compare the new tuple to all tuples in the candidate list
            int cCounter = 0;
            for (int i = 0; i < candidates.size(); i++) {
                cCounter++;
                int result = IPreference.UNRANKED;
                result = preference.compare(candidates.get(i), cand);
                nrOfcomparison++;

                //        System.out.println("current: " + cand);
                //        System.out.println("result: " + result);

                switch (result) {
                    case IPreference.GREATER:
                        // element in candidate list is better: discard new element
                        //        	  System.out.println("Checked: " + cCounter);
                        return false;
                    case IPreference.LESS:
                        // element in candidate list is worse: remove it
                        int last = candidates.size() - 1;
                        if (i < last) {
                            // Overwrite the current position with the last element and
                            // make sure that this element will be checked, too.
                            candidates.set(i--, candidates.get(last));
                            candidates.remove(last);
                        } else {
                            // last element reached: just remove it
                            candidates.remove(last);
                        }
                        break;
                    case IPreference.EQUAL:
                        // objects are equal: new element cannot be dominated by others
                    case IPreference.SUBSTITUTABLE:
                        // objects are substitutable: cand cannot be dominated by others
                        candidates.add(cand);
                        //            System.out.println("Checked: " + cCounter);
                        return true;
                }
            }
            //                  System.out.println("Checked: " + cCounter);

            // new candidate is incomparable to or better than any other: add it
            candidates.add(cand);
            //      System.out.println("Window Size: " + candidates.size());


        } catch (Exception e) {
            e.printStackTrace(System.out);
            return false;
        }


        return true;
    }


    public boolean hasNext() {
        // compute the result set
        if (candidates == null) {
            candidates = new ArrayList();
            while (input.hasNext()) {
                addObject((FlatLevelCombination) input.next());
            }
            position = 0;
            //            System.out.println("*************** BNL nrOfComparison: " + nrOfcomparison);


        }


        return candidates.size() > position;
    }

    public Object next() {
        return candidates.get(position++);
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
