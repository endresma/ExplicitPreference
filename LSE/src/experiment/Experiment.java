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

package experiment;

import bnl.BNL;
import btg.*;
import flatlc.inputrelations.FlatLCResultSetA;
import lse.LSE;
import spo.OrderedGraph;
import spo.OrderedPair;
import util.ExplicitPreference;
import util.InputGenerator;
import util.MemoryObserver;
import util.Stopwatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * User: endresma
 * Date: 17.06.15
 * Time: 15:24
 */
public class Experiment {

    public static void main(String[] args) {


        // SPO, strict partial order

        //Eingabe als eine Liste von spo.Tuple (a,b), wobei  b ist schlechter als a
        //alleinstehende Knoten als: (null, Knoten) eingeben
        ArrayList<OrderedPair> orderedPairs = new ArrayList<OrderedPair>();
        int[] domain = null;
        int[] inputSize = null;

        int runs = 3;

        // ----------------------------------------------------------------------
        // Example 7, Figure 9
        //                OrderedPair a1 = new OrderedPair(0, 3);
        //                OrderedPair a2 = new OrderedPair(0, 4);
        //                OrderedPair a3 = new OrderedPair(1, 3);
        //                OrderedPair a4 = new OrderedPair(1, 5);
        //                OrderedPair a5 = new OrderedPair(2, 4);
        //                OrderedPair a6 = new OrderedPair(2, 5);
        //
        //                orderedPairs.add(a1);
        //                orderedPairs.add(a2);
        //                orderedPairs.add(a3);
        //                orderedPairs.add(a4);
        //                orderedPairs.add(a5);
        //                orderedPairs.add(a6);
        //
        //
        //                // generate integer domain with 101 different objects, 0..100
        //                domain = new int[]{6};
        //
        //                // size of the input relation based on the given domain
        //                inputSize = new int[]{1000000};


        // ----------------------------------------------------------------------
        // Test Setting 1: Figure 11
        // red -> 0, blue -> 1, green -> 2, yellow -> 3, purple -> 4, black -> 5, cyan ->6
//        OrderedPair a1 = new OrderedPair(0, 3);
//        OrderedPair a2 = new OrderedPair(0, 4);
//        OrderedPair a3 = new OrderedPair(1, 3);
//        OrderedPair a4 = new OrderedPair(1, 4);
//        OrderedPair a5 = new OrderedPair(2, 3);
//        OrderedPair a6 = new OrderedPair(2, 4);
//        OrderedPair a7 = new OrderedPair(3, 5);
//        OrderedPair a8 = new OrderedPair(3, 6);
//        OrderedPair a9 = new OrderedPair(4, 5);
//        OrderedPair a10 = new OrderedPair(4, 6);
//
//        orderedPairs.add(a1);
//        orderedPairs.add(a2);
//        orderedPairs.add(a3);
//        orderedPairs.add(a4);
//        orderedPairs.add(a5);
//        orderedPairs.add(a6);
//        orderedPairs.add(a7);
//        orderedPairs.add(a8);
//        orderedPairs.add(a9);
//        orderedPairs.add(a10);
//
//        domain = new int[]{6};
//        inputSize = new int[]{5000000};

        // ----------------------------------------------------------------------
        // Test Setting 2: Figure 6a
        // r -> 0, b -> 1, g -> 2, y -> 3, p -> 4, k -> 5, c -> 6
                OrderedPair a1 = new OrderedPair(0, 3);
                OrderedPair a2 = new OrderedPair(1, 4);
                OrderedPair a3 = new OrderedPair(2, null);
                OrderedPair a4 = new OrderedPair(3, 5);
                OrderedPair a5 = new OrderedPair(3, 6);
                OrderedPair a6 = new OrderedPair(4, 6);
                orderedPairs.add(a1);
                orderedPairs.add(a2);
                orderedPairs.add(a3);
                orderedPairs.add(a4);
                orderedPairs.add(a5);
                orderedPairs.add(a6);

                domain = new int[]{100000};
                inputSize = new int[]{1000000};


        // ----------------------------------------------------------------------
        // Test Setting 3: Example 14


        Stopwatch sw = new Stopwatch();
        OrderedGraph graph = new OrderedGraph(0, orderedPairs);
        long runtimeLatticeConstruction = sw.getElapsedNanoSecTime();
        System.out.println("Runtime Lattice Construction: " + nanoToSeconds(runtimeLatticeConstruction));

        System.out.printf("max level values: [ ");
//        int maxLevels[] = graph.getMax_values_array();
        int maxLevels[] = graph.getMaxValuesArray();
        for (int i = 0; i < maxLevels.length; i++) {
            System.out.print(maxLevels[i]);
            System.out.print(" ");
        }
        System.out.print("]");


        // does not matter for a one-dimensional domain
        // however, is necessary for more-dimensional domains
        String distribution = "ind";


        /** generate Input data */
        FlatLCResultSetA input = InputGenerator.generateInput(domain, distribution, inputSize[0]);

        //                while (input.hasNext()) {
        //                    FlatLevelCombination flc = (FlatLevelCombination) input.next();
        //                    System.out.print(flc.toSimpleString());
        //                    System.out.print(" ");
        //                }
        //
        //                input.reset();

        /** test to construct an EXPLICIT preference */
        //        FlatLCAttributeSelector attributeSelector = new FlatLCAttributeSelector(0);
        ExplicitPreference explicit = new ExplicitPreference(graph);

        //        System.gc();


        long[] runtimes_bnl = new long[runs];
        long[] runtimes_hm = new long[runs];
        long[] runtimes_sl = new long[runs];
        long[] runtimes_a = new long[runs];

        for (int i = 0; i < runs; ++i) {

            /** run BNL */
//            System.out.println("\n\n ********** Run BNL *********** ");
            sw = new Stopwatch();

            BNL bnl = new BNL(input, explicit);

            //        int resultSizeBNL = countResult(bnl);

            Object o = null;
            ArrayList<Object> bnlResult = new ArrayList<>();
            int bnlCounter = 0;
            while (bnl.hasNext()) {
                o = bnl.next();
                bnlResult.add(o);
                //            System.out.println(o.toString());
                bnlCounter++;
            }


            long runtimeBNL = sw.getElapsedNanoSecTime();
            runtimes_bnl[i] = runtimeBNL;

//            System.out.println("Runtime BNL: " + nanoToSeconds(runtimeBNL));
//            System.out.println("resultSize BNL: " + bnlCounter);

            input.reset();
            System.gc();


            /** run LSE-SL  */

            HashMap<Object, ArrayList<Integer>> keySign = graph.getKeySignatureAssignments();


//            System.out.println("\n\n ********** Run LSE-HM *********** ");

            // FIXME: same input for all algorithms, FlatLCResultSet vs. ArrayList
            ArrayList<Object> arrayInput = input.getElements();

            MemoryObserver.initMemory();
            sw = new Stopwatch();


            //        BTGDataA btg = new BTGDataArray(new BTG(maxLevels));
            BTGDataA btg_hm = new BTGDataLevelBasedHashMap(new BTG(maxLevels));
            LSE lse_hm = new LSE(arrayInput.iterator(), btg_hm, keySign);

            o = null;
            ArrayList<Object> lseResult_hm = new ArrayList<>();
            int lseCounter = 0;
            while (lse_hm.hasNext()) {
                o = lse_hm.next();
                lseResult_hm.add(o);
                //                        System.out.println(o.toString());
                lseCounter++;
            }

            //        int resultSizeLSE = countResult(lse);
            long runtimeLSE = sw.getElapsedNanoSecTime();
            runtimes_hm[i] = runtimeLSE;
            //        long test_memory = MemoryObserver.currentMemory();
//            double lse_hm_memory = MemoryObserver.currentMemoryMB();


//            System.out.println("Runtime LSE-HM: " + nanoToSeconds(runtimeLSE));
//            System.out.println("resultSize LSE-HM: " + lseCounter);
//            System.out.println("LSE-HM memory usage: " + lse_hm_memory);
            //        System.out.printf("test memory " + test_memory);

            input.reset();
            System.gc();


            /** run LSE-A  */
//            System.out.println("\n\n ********** Run LSE-A *********** ");

            // FIXME: same input for all algorithms, FlatLCResultSet vs. ArrayList
            arrayInput = input.getElements();
//            MemoryObserver.initMemory();
            sw = new Stopwatch();


            BTGDataA btg_ar = new BTGDataArray(new BTG(maxLevels));


            LSE lse_ar = new LSE(arrayInput.iterator(), btg_ar, keySign);

            o = null;
            ArrayList<Object> lseResult_ar = new ArrayList<>();
            lseCounter = 0;
            while (lse_ar.hasNext()) {
                o = lse_ar.next();
                lseResult_ar.add(o);
                //                        System.out.println(o.toString());
                lseCounter++;
            }
            //        int resultSizeLSE = countResult(lse);
            runtimeLSE = sw.getElapsedNanoSecTime();
//            double lse_ar_memory = MemoryObserver.currentMemoryMB();
            runtimes_a[i] = runtimeLSE;
//            System.out.println("Runtime LSE-A: " + nanoToSeconds(runtimeLSE));
//            System.out.println("resultSize LSE-A: " + lseCounter);
//            System.out.println("LSE-A memory usage: " + (lse_ar_memory));


            input.reset();
            System.gc();


            /** run LSE-SL  */
//            System.out.println("\n\n ********** Run LSE-SL *********** ");

            // FIXME: same input for all algorithms, FlatLCResultSet vs. ArrayList
            arrayInput = input.getElements();
//            MemoryObserver.initMemory();
            sw = new Stopwatch();


            BTGDataA btg_sl = new BTGDataLevelBasedSkipList(new BTG(maxLevels));

            LSE lse_sl = new LSE(arrayInput.iterator(), btg_sl, keySign);

            o = null;
            ArrayList<Object> lseResult_sl = new ArrayList<>();
            lseCounter = 0;
            while (lse_sl.hasNext()) {
                o = lse_sl.next();
                lseResult_sl.add(o);
                //                        System.out.println(o.toString());
                lseCounter++;
            }
            runtimeLSE = sw.getElapsedNanoSecTime();
//            double lse_sl_memory = MemoryObserver.currentMemoryMB();

//            System.out.println("Runtime LSE-SL: " + nanoToSeconds(runtimeLSE));
//            System.out.println("resultSize LSE-SL: " + lseCounter);
//            System.out.println("LSE-SL memory usage: " + lse_sl_memory);
            runtimes_sl[i] = runtimeLSE;

            //        System.out.println("\n\nCheck Assertions");
            //        assert equal(bnlResult, lseResult_hm);
            //        assert equal(bnlResult, lseResult_ar);
            //        assert equal(bnlResult, lseResult_sl);
        }




        // different runs finished


        double bnl_mean = mean(runtimes_bnl);
        double hm_mean = mean(runtimes_hm);
        double sl_mean = mean(runtimes_sl);
        double a_mean = mean(runtimes_a);

        double bnl_min = min(runtimes_bnl);
        double hm_min = min(runtimes_hm);
        double sl_min = min(runtimes_sl);
        double a_min = min(runtimes_a);

        double bnl_max = max(runtimes_bnl);
        double hm_max = max(runtimes_hm);
        double sl_max = max(runtimes_sl);
        double a_max = max(runtimes_a);

        System.out.printf("\n\n");

        System.out.printf("BNL: Mean ylow yhigh");
        System.out.printf("     " + nanoToSecondsDouble(bnl_mean));
        System.out.printf("     " + nanoToSecondsDouble(bnl_min));
        System.out.printf("     " + nanoToSecondsDouble(bnl_max));

        System.out.printf("\n\n");
        System.out.printf("LSE-HM: Mean ylow yhigh");
        System.out.printf("      " + nanoToSecondsDouble(hm_mean));
        System.out.printf("      " + nanoToSecondsDouble(hm_min));
        System.out.printf("     " + nanoToSecondsDouble(hm_max));

        System.out.printf("\n\n");
        System.out.printf("LSE-SL: Mean ylow yhigh");
        System.out.printf("      " + nanoToSecondsDouble(sl_mean));
        System.out.printf("      " + nanoToSecondsDouble(sl_min));
        System.out.printf("     " + nanoToSecondsDouble(sl_max));

        System.out.printf("\n\n");
        System.out.printf("LSE-A: Mean ylow yhigh");
        System.out.printf("      " + nanoToSecondsDouble(a_mean));
        System.out.printf("      " + nanoToSecondsDouble(a_min));
        System.out.printf("     " + nanoToSecondsDouble(a_max));


        System.out.println("\n\n");

    }




    private static double mean(long arr[]) {

        double mean = 0;
        for(int i=0; i<arr.length; ++i)
            mean += arr[i];

        return mean / arr.length;

    }


   private static double min(long arr[]) {
       double a= arr[0];

       for(int i=1; i<arr.length; ++i) {
           if(arr[i] < a)
               a = arr[i];

       }

       return a;

   }


    private static double max(long arr[]) {
        double a= arr[0];

        for(int i=1; i<arr.length; ++i) {
            if(arr[i] > a)
                a = arr[i];

        }

        return a;

    }



    private static boolean equal(ArrayList<Object> bnlResult, ArrayList<Object> lseResult) {

        //         boolean equal = false;

        if (bnlResult.size() != lseResult.size())
            return false;

        for (Object o : bnlResult) {
            if (!lseResult.contains(o))
                return false;
        }

        return true;
    }


    /**
     * converts nano seconds to seconds
     */
    public static double nanoToSeconds(long nano) {
        return nano / 1000. / 1000. / 1000.;
    }

    /**
     * converts nano seconds to seconds
     */
    public static double nanoToSecondsDouble(Double nano) {
        return nano / 1000. / 1000. / 1000.;
    }



    /**
     * count the size of the BMO size. Just iterator through the cursor.
     *
     * @param cursor
     * @return
     */
    private static int countResult(Iterator cursor) {
        int counter = 0;

        Object o = null;
        while (cursor.hasNext()) {
            o = cursor.next();
            System.out.println(o.toString());
            counter++;
        }


        return counter;
    }


}
