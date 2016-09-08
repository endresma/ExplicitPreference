package salsa;

import flatlc.levels.FlatLevelCombination;
import util.IPreference;
import util.TopSort;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Implementation of SaLSa, based on the MAX stop point criteria
 * User: endresma
 * Date: 02.08.16
 * Time: 17:10
 */
public class SaLSa implements Iterator {


    protected ArrayList<Object> input;

    /**
     * list of Skyline candidates
     */
    protected ArrayList S;
    /**
     * p_stop <- undefined
     */
    protected FlatLevelCombination pStop = null;
    /**
     * the preference used by this object
     */
    IPreference preference;
    int nrOfcomparison = 0;
    private int position = -1;

    public SaLSa(ArrayList<Object> input, IPreference preference) {
        this.preference = preference;
        this.input = input;

    }


    // FIXME: me optimize this function wrt. code optimization
    protected void compute() {

        // S <- {} // emptyset
        S = new ArrayList();

        // U <- r
        ArrayList<Object> U = input;

        // stop <- false
        boolean stop = false;


        /** sort U according to F. Here, F is the maximum criterion mentioned in
         * SaLSa: Computing the Skyline without Scanning the Whole Sky
         */
        TopSort.sortMax(U);

        // while not stop && U != {} do
        int index = 0;
        while (!stop && !U.isEmpty()) {
            // p <- get next point from U
            FlatLevelCombination p = (FlatLevelCombination) U.get(index);

            // U <- U \ {p}
            U.remove(0);

            // no object in S anyDominates p
            if (!anyDominates(S, p)) {
                // S <- S union {p}
                S.add(p);
                updatepStop(p);
            }

            // if pStop > U then stop <- true
            if (dominatesAll(pStop, U)) {
                // stop <- true
                stop = true;
            }

            if (stop)
                break;
        }

    }

    /**
     * check if any object in S dominates p
     * return true if any object in S dominates p, otherwise false
     *
     * @param S
     * @param p
     * @return
     */
    private boolean anyDominates(ArrayList<Object> S, FlatLevelCombination p) {
        int compare;

        for (int i = 0; i < S.size(); ++i) {

            compare = preference.compare(S.get(i), p);
            nrOfcomparison++;

            if (compare == IPreference.EQUAL) {
                return false;
            }

            if (compare == IPreference.GREATER) {
                //                return IPreference.GREATER;
                return true;
            }
        }

        //        if (compare != IPreference.GREATER) {
        //            S.add(p);
        //            // update pStop
        //            pStop = computepStop();
        //
        //        }

        // if p is not dominated by any object in S
        // then p is UNRANKED
        //        return IPreference.UNRANKED;
        return false;
    }


    /**
     * Check if p anyDominates all objects in S.
     * This is line 6 in Algorithm 1, SaLSa in the paper.
     *
     * @param p
     * @param U
     * @return
     */
    private boolean dominatesAll(FlatLevelCombination p, ArrayList<Object> U) {

        int compare;

        for (int i = 0; i < U.size(); ++i) {

            FlatLevelCombination q = (FlatLevelCombination) U.get(i);
            compare = preference.compare(p, q);
            nrOfcomparison++;
            if (compare != IPreference.GREATER) {
                //            if (compare == IPreference.GREATER || compare == IPreference.UNRANKED) {
                return false;
            }
        }

        return true;
    }

    /**
     * compute pStop based on the MaxiMin rule
     *
     * @return
     */
    private void updatepStop(FlatLevelCombination flc) {


        if (pStop == null) {
            pStop = flc;
            return;
        }

        int p[] = flc.getLevelCombination();
        int p_max = max(p);
        int pStop_max = max(pStop.getLevelCombination());

        pStop = pStop_max < p_max ? pStop : flc;

    }


    private int max(int[] p) {

        int max = p[0];
        for (int j = 1; j < p.length; ++j) {
            max = max > p[j] ? max : p[j];
        }
        return max;
    }

    private int min(int[] p) {

        int min = p[0];
        for (int j = 1; j < p.length; ++j) {
            min = min < p[j] ? min : p[j];
        }
        return min;
    }


    @Override
    public boolean hasNext() {
        // compute the result set
        if (S == null) {
            compute();
            position = 0;
            System.out.println("*************** SaLSa nrOfComparison: " + nrOfcomparison);
        }

        return S.size() > position;
    }

    @Override
    public Object next() {
        return S.get(position++);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }


}
