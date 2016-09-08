/*
 * Copyright (c) 2015. markus endres, timotheus preisinger
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

package util;

import flatlc.levels.FlatLevelCombination;

import java.util.*;


public class TopSort {


    /**
     * Sort data in order given by the asc flag.
     * asc == true: ascending
     * asc == false: descending
     *
     * @param data
     * @param asc
     */
    public static void sort(List<Object> data, boolean asc) {
        if (asc)
            TopSort.sortAsc(data);
        else
            TopSort.sortDesc(data);
    }

    public static Iterator sort(Iterator iterator) {
        List<Object> data = new ArrayList<>();

        while(iterator.hasNext()) {
            data.add(iterator.next());
        }

        TopSort.sort(data);
        return data.iterator();
    }



    /**
     * Sort data in ascending order.
     *
     * @param data
     */
    public static void sort(List<Object> data) {
        TopSort.sort(data, true);
    }


    private static void sortDesc(List<Object> data) {

        Comparator entropyComparator = new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {

                FlatLevelCombination flc_o1 = (FlatLevelCombination) o1;
                FlatLevelCombination flc_o2 = (FlatLevelCombination) o2;

                float e1 = entropy(flc_o1);
                float e2 = entropy(flc_o2);

                if (e1 < e2)
                    return 1;
                else if (e1 > e2)
                    return -1;
                else // e1 == e2
                    return 0;


            }
        };


        Collections.sort(data, entropyComparator);

    }

    /**
     * sort a list of input data concerning the entropy criterion mentioned in
     * Godfrey: Skyline with Presorting
     *
     * @param data
     */
    private static void sortAsc(List<Object> data) {

        Comparator entropyComparator = new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {

                FlatLevelCombination flc_o1 = (FlatLevelCombination) o1;
                FlatLevelCombination flc_o2 = (FlatLevelCombination) o2;

                float e1 = entropy(flc_o1);
                float e2 = entropy(flc_o2);

                if (e1 < e2)
                    return -1;
                else if (e1 > e2)
                    return 1;
                else // e1 == e2
                    return 0;


            }
        };


        Collections.sort(data, entropyComparator);

    }


    private static float entropy(FlatLevelCombination flc) {
        int[] t = flc.getLevelCombination();
        float e = 0;


        // E(t) = \sum_{i=1}^k ln(t[a_i] + 1)
        for (int i = 0; i < t.length; ++i) {
            e += Math.log(t[i] + 1);
        }

        return e;
    }


    /**
     * Based on the Maximum Coordinate Sorting presented in the paper
     * SaLSa: Computing the Skyline without Scanning the Whole Sky
     * Ilaria Bartolini, Paolo Ciaccia, Marco Patella DEIS, University of Bologna, Italy
     * <p>
     * max(p) = ( max_j {p[j]}, sum(p) )
     *
     * @param flc
     * @return
     */
    private static float[] maxSortFunction(FlatLevelCombination flc) {

        float max[] = new float[2];


        int[] t = flc.getLevelCombination();

        max[0] = t[0];
        max[1] = t[0];
        // max
        for (int j = 1; j < t.length; ++j) {
            // max
            max[0] = max[0] < t[j] ? t[j] : max[0];

            //sum
            max[1] += t[j];
        }

        return max;

    }


    /**
     * sort a list of input data concerning the max criterion mentioned in
     * SaLSa: Computing the Skyline without Scanning the Whole Sky
     *
     * @param data
     */
    public static void sortMax(List<Object> data) {

        Comparator maxComparator = new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {

                FlatLevelCombination flc_o1 = (FlatLevelCombination) o1;
                FlatLevelCombination flc_o2 = (FlatLevelCombination) o2;

                float[] e1 = maxSortFunction(flc_o1);
                float[] e2 = maxSortFunction(flc_o2);

                if (e1[0] < e2[0])
                    return -1;
                else if (e1[0] > e2[0])
                    return 1;
                else { // e1[0] == e2[0]
                    if (e1[1] < e2[1])
                        return -1;
                    else if (e1[1] > e2[1])
                        return 1;
                    else
                        return 0;
                }

            }
        };
        Collections.sort(data, maxComparator);
    }
}