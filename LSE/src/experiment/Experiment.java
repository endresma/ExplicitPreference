/*
 * Copyright (c) 2015. markus endres, timotheus preisinger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package experiment;

import bnl.BNL;
import btg.*;
import flatlc.inputrelations.FlatLCResultSetA;
import lse.LSE;
import preference.ExplicitPreference;
import sfs.SFS;
import salsa.SaLSa;
import spo.OrderedGraph;
import spo.OrderedPair;
import spo.OrderedPairOrder;
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

        /**
         * Input is a list of spo.Tuple (a,b). Thereby, a is better than b.
         * Single nodes are (null, b)
         */

        int[] domain = null;
        int[] inputSize = null;

        /**
         * Generate some input data. We use the data generator for independent data.
         * This does not matter for a one-dimensional domain, but it's necessary for more-dimensional cases.
         */
        String distribution = "anti";

        int runs = 1;

        // ----------------------------------------------------------------------
        // Example 7, Figure 9
//        ArrayList<OrderedPair> orderedPairs = new ArrayList<>();
//        OrderedPair a1 = new OrderedPair(0, 3);
//        OrderedPair a2 = new OrderedPair(0, 4);
//        OrderedPair a3 = new OrderedPair(1, 3);
//        OrderedPair a4 = new OrderedPair(1, 5);
//        OrderedPair a5 = new OrderedPair(2, 4);
//        OrderedPair a6 = new OrderedPair(2, 5);
//
//        orderedPairs.add(a1);
//        orderedPairs.add(a2);
//        orderedPairs.add(a3);
//        orderedPairs.add(a4);
//        orderedPairs.add(a5);
//        orderedPairs.add(a6);
//
//
//        // generate integer domain with 101 different objects, 0..100
//        domain = new int[]{6};
//
//        // size of the input relation based on the given domain
//        inputSize = new int[]{10};
//        OrderedPairOrder orderedPairOrder = new OrderedPairOrder(orderedPairs);
//        OrderedGraph graph = new OrderedGraph(OrderedGraph.MIN, orderedPairOrder);
//        ExplicitPreference explicit = new ExplicitPreference(graph);


        // ----------------------------------------------------------------------
        // Test Setting 1: Example 10, Figure 11
        // red -> 0, blue -> 1, green -> 2, yellow -> 3, purple -> 4, black -> 5, cyan ->6
        ArrayList<OrderedPair> orderedPairs = new ArrayList<>();
        OrderedPair a1 = new OrderedPair(0, 3);
        OrderedPair a2 = new OrderedPair(0, 4);
        OrderedPair a3 = new OrderedPair(1, 3);
        OrderedPair a4 = new OrderedPair(1, 4);
        OrderedPair a5 = new OrderedPair(2, 3);
        OrderedPair a6 = new OrderedPair(2, 4);
        OrderedPair a7 = new OrderedPair(3, 5);
        OrderedPair a8 = new OrderedPair(3, 6);
        OrderedPair a9 = new OrderedPair(4, 5);
        OrderedPair a10 = new OrderedPair(4, 6);

        orderedPairs.add(a1);
        orderedPairs.add(a2);
        orderedPairs.add(a3);
        orderedPairs.add(a4);
        orderedPairs.add(a5);
        orderedPairs.add(a6);
        orderedPairs.add(a7);
        orderedPairs.add(a8);
        orderedPairs.add(a9);
        orderedPairs.add(a10);

        /** domain size. Note that we start at 0, i.e., |dom(color)| = 7 */
        domain = new int[]{6};
        /** input size */
        inputSize = new int[]{100000, 500000, 5000000};
        OrderedPairOrder orderedPairOrder = new OrderedPairOrder(orderedPairs);
        OrderedGraph graph = new OrderedGraph(OrderedGraph.MIN, orderedPairOrder);
        ExplicitPreference pref = new ExplicitPreference(graph.getgraph());

        // ----------------------------------------------------------------------
        // Test Setting 2: Example 7, Figure 6a and lattice as in Figure 7
        // r -> 0, b -> 1, g -> 2, y -> 3, p -> 4, k -> 5, c -> 6
//                ArrayList<OrderedPair> orderedPairs = new ArrayList<>();
//                OrderedPair a1 = new OrderedPair(0, 3);
//                OrderedPair a2 = new OrderedPair(1, 4);
//                OrderedPair a3 = new OrderedPair(2, null);
//                OrderedPair a4 = new OrderedPair(3, 5);
//                OrderedPair a5 = new OrderedPair(3, 6);
//                OrderedPair a6 = new OrderedPair(4, 6);
//                orderedPairs.add(a1);
//                orderedPairs.add(a2);
//                orderedPairs.add(a3);
//                orderedPairs.add(a4);
//                orderedPairs.add(a5);
//                orderedPairs.add(a6);
//
//                domain = new int[]{1000};
//                inputSize = new int[]{150000};
//                OrderedPairOrder orderedPairOrder = new OrderedPairOrder(orderedPairs);
//                OrderedGraph graph = new OrderedGraph(OrderedGraph.MIN, orderedPairOrder);
//                ExplicitPreference pref = new ExplicitPreference(graph.getgraph());


        // ----------------------------------------------------------------------
        // Test Setting 3: Example 13, regular SV-semantics

        // create preference on colors
        // blue -> 0, red -> 1, black -> 2
//                ArrayList<OrderedPair> orderedPairs = new ArrayList<>();
//                OrderedPair a1 = new OrderedPair(1, 2);
//                OrderedPair a2 = new OrderedPair(null, 0);
//                orderedPairs.add(a1);
//                orderedPairs.add(a2);
//                OrderedPairOrder colorPref = new OrderedPairOrder(orderedPairs);
//
//                // create preference on price
//                // 50K -> 0, [40;50[ union ]50;60] -> 1, ...
//                ArrayList<OrderedPair> orderedPairs2 = new ArrayList<OrderedPair>();
//                OrderedPair b1 = new OrderedPair(0, 1);
//                OrderedPair b2 = new OrderedPair(1, 2);
//                OrderedPair b3 = new OrderedPair(2, 3);
//                //        OrderedPair b4 = new OrderedPair(2, 3);
//                //        OrderedPair b5 = new OrderedPair(3, 4);
//                orderedPairs2.add(b1);
//                orderedPairs2.add(b2);
//                orderedPairs2.add(b3);
//                //        orderedPairs2.add(b4);
//                //        orderedPairs2.add(b5);
//                OrderedPairOrder pricePref = new OrderedPairOrder(orderedPairs2);
//
//
//                domain = new int[]{7, 3};
//                inputSize = new int[]{1000};
//                OrderedGraph graph = new OrderedGraph(OrderedGraph.MIN, colorPref, pricePref);
//
//                ArrayList<DirectedGraph<Object, DefaultEdge>> originalGraph = graph.getOriginalGraph();
//
//                ParetoPreference pref = new ParetoPreference();
//                for (DirectedGraph<Object, DefaultEdge> g : originalGraph) {
//                    pref.append(new ExplicitPreference(g));
//                }


        // ----------------------------------------------------------------------
        // Test Setting 4: Example 13, trivial SV-semantics


        // ----------------------------------------------------------------------
        // Test Setting 5: Real World Data, Zillow
        //        OrderedPair a1 = new OrderedPair(2, 1);
        //        OrderedPair a2 = new OrderedPair(null, 4);
        //        OrderedPair a3 = new OrderedPair(2, 5);
        //        OrderedPair a4 = new OrderedPair(3, 5);
        //        ArrayList<OrderedPair> orderedPairs = new ArrayList<>();
        //        orderedPairs.add(a1);
        //        orderedPairs.add(a2);
        //        orderedPairs.add(a3);
        //        orderedPairs.add(a4);
        //        OrderedPairOrder bedrooms = new OrderedPairOrder(orderedPairs);
        //
        //        ArrayList<OrderedPair> orderedPairs2 = new ArrayList<>();
        //        OrderedPair b1 = new OrderedPair(2, 1);
        //        OrderedPair b2 = new OrderedPair(null, 3);
        //        orderedPairs2.add(b1);
        //        orderedPairs2.add(b2);
        //        OrderedPairOrder bathrooms = new OrderedPairOrder(orderedPairs2);
        //
        //        ArrayList<OrderedPair> orderedPairs3 = new ArrayList<>();
        //        OrderedPair c1 = new OrderedPair(8, 4);
        //        OrderedPair c2 = new OrderedPair(10, 4);
        //        OrderedPair c3 = new OrderedPair(8, 7);
        //        OrderedPair c4 = new OrderedPair(10, 7);
        //        OrderedPair c5 = new OrderedPair(null, 12);
        //
        //
        //        orderedPairs3.add(c1);
        //        orderedPairs3.add(c2);
        //        orderedPairs3.add(c3);
        //        orderedPairs3.add(c4);
        //        orderedPairs3.add(c5);
        //
        //        OrderedPairOrder livingArea = new OrderedPairOrder(orderedPairs3);
        //
        //        ArrayList<OrderedPair> orderedPairs4 = new ArrayList<>();
        //        OrderedPair d1 = new OrderedPair(5, 30);
        //        OrderedPair d2 = new OrderedPair(5, 40);
        //        OrderedPair d3 = new OrderedPair(10, 30);
        //        OrderedPair d4 = new OrderedPair(10, 40);
        //        OrderedPair d5 = new OrderedPair(20, 30);
        //        OrderedPair d6 = new OrderedPair(20, 40);
        //        OrderedPair d7 = new OrderedPair(null, 2);
        //        OrderedPair d8 = new OrderedPair(null, 3);
        //
        //        orderedPairs4.add(d1);
        //        orderedPairs4.add(d2);
        //        orderedPairs4.add(d3);
        //        orderedPairs4.add(d4);
        //        orderedPairs4.add(d5);
        //        orderedPairs4.add(d6);
        //        orderedPairs4.add(d7);
        //        orderedPairs4.add(d8);
        //
        //        OrderedPairOrder age = new OrderedPairOrder(orderedPairs4);


        /** config for real world data */
        // Zillow
        //        inputSize = new int[]{2236252};
        //        domain = new int[]{10, 10, 36, 45};

        // anti, corr, ind, gaussian, zillow, nba, house, weather
        // zillow_data.txt, nba_data.txt, house_data.txt
        //        distribution = "zillow";

        // NBA
        //        int[] n = new int[]{17265};
        //        int maxLevels[] = new int[]{10, 10, 10, 10, 10};

        // House
        //         int[] n = new int[]{127931};
        //         int maxLevels[] = new int[]{10000,10000,10000,10000,10000,10000};
        //        int maxLevels[] = new int[]{100,100,100,100,100,100};

        //
        //        OrderedGraph graph = new OrderedGraph(OrderedGraph.MIN, bedrooms, bathrooms, livingArea, age);

        //        ArrayList<DirectedGraph<Object, DefaultEdge>> originalGraph = graph.getOriginalGraph();
        //        ParetoPreference pref = new ParetoPreference();
        //        for (DirectedGraph<Object, DefaultEdge> g : originalGraph) {
        //            pref.append(new ExplicitPreference(g));
        //        }

        //


        //        long runtimeLatticeConstruction = sw.getElapsedNanoSecTime();
        //        System.out.println("\n\nRuntime Lattice Construction: " + nanoToSeconds(runtimeLatticeConstruction));

        //        System.out.printf("max level values: [ ");
        int maxLevels[] = graph.getMaxValuesArray();


        /** generate Input data */
        FlatLCResultSetA input = InputGenerator.generateInput(domain, distribution, inputSize[0]);

        // print input
        //        System.out.println("Input objects:");
        //        while (input.hasNext()) {
        //            System.out.println(input.next());
        //        }
        //        input.reset();

        long[] runtimes_bnl = new long[runs];
        long[] runtimes_sfs = new long[runs];
        long[] runtimes_salsa = new long[runs];
        long[] runtimes_hm = new long[runs];
        long[] runtimes_sl = new long[runs];
        long[] runtimes_a = new long[runs];

        Stopwatch sw;
        for (int i = 0; i < runs; ++i) {

            /** run BNL */
            System.out.println("\n\n ********** Run BNL *********** ");
            MemoryObserver.initMemory();
            sw = new Stopwatch();

            BNL bnl = new BNL(input, pref);


            ArrayList<Object> bnlResult = new ArrayList<>();
            int bnlCounter = 0;
            while (bnl.hasNext()) {
                //                Object o = bnl.next();
                bnl.next();
                //                bnlResult.add(o);
                //                System.out.println(o.toString());
                bnlCounter++;
            }


            long runtimeBNL = sw.getElapsedNanoSecTime();
            runtimes_bnl[i] = runtimeBNL;
            double bnl_memory = MemoryObserver.currentMemoryMB();

            System.out.println("Runtime BNL: " + nanoToSeconds(runtimeBNL));
            System.out.println("resultSize BNL: " + bnlCounter);
            System.out.println("BNL memory usage: " + bnl_memory);

            input.reset();
            //            System.gc();


            /** run SFS */
            System.out.println("\n\n ********** Run SFS *********** ");

            // convert iterator to ArrayList
            //            ArrayList<Object> sfsInput = input.getElements();

            MemoryObserver.initMemory();
            sw = new Stopwatch();

            SFS sfs = new SFS(input.getElements(), pref);


            //            ArrayList<Object> sfsResult = new ArrayList<>();
            int sfsCounter = 0;
            while (sfs.hasNext()) {
                //                Object o = sfs.next();
                sfs.next();
                //                sfsResult.add(o);
                //                System.out.println(o.toString());
                sfsCounter++;
            }


            long runtimeSFS = sw.getElapsedNanoSecTime();
            runtimes_sfs[i] = runtimeSFS;
            double sfs_memory = MemoryObserver.currentMemoryMB();

            System.out.println("Runtime SFS: " + nanoToSeconds(runtimeSFS));
            System.out.println("resultSize SFS: " + sfsCounter);
            System.out.println("SFS memory usage: " + sfs_memory);

            input.reset();
            //            System.gc();


            /** run SaLSA */
            System.out.println("\n\n ********** Run SaLSA *********** ");

            // convert iterator to ArrayList
            //            ArrayList<Object> salsaInput = input.getElements();

            MemoryObserver.initMemory();
            sw = new Stopwatch();

            SaLSa salsa = new SaLSa(input.getElements(), pref);


            //            ArrayList<Object> salsaResult = new ArrayList<>();
            int salsaCounter = 0;
            while (salsa.hasNext()) {
                //                Object o = sfs.next();
                salsa.next();
                //                sfsResult.add(o);
                //                System.out.println(o.toString());
                salsaCounter++;
            }


            long runtimeSaLSa = sw.getElapsedNanoSecTime();
            runtimes_salsa[i] = runtimeSaLSa;
            double salsa_memory = MemoryObserver.currentMemoryMB();

            System.out.println("Runtime SaLSa: " + nanoToSeconds(runtimeSaLSa));
            System.out.println("resultSize SaLSa: " + salsaCounter);
            System.out.println("SaLSa memory usage: " + salsa_memory);

            input.reset();
            //            System.gc();


            /** run LSE-SL  */
            HashMap<ArrayList<Object>, ArrayList<Integer>> keySign = graph.getKeySignatureAssignments();

            System.out.println("\n\n ********** Run LSE-HM *********** ");

            // FIXME: same input for all algorithms, FlatLCResultSet vs. ArrayList
            ArrayList<Object> arrayInput = input.getElements();

            MemoryObserver.initMemory();
            //            System.out.println("runtimeMemory: " + MemoryObserver.runtimeMemory());
            sw = new Stopwatch();


            //        BTGDataA btg = new BTGDataArray(new BTG(maxLevels));
            BTGDataA btg_hm = new BTGDataLevelBasedHashMap(new BTG(maxLevels));
            LSE lse_hm = new LSE(arrayInput.iterator(), btg_hm, keySign);

            //            o = null;
            //            ArrayList<Object> lseResult_hm = new ArrayList<>();
            int lseCounter = 0;
            while (lse_hm.hasNext()) {
                //                Object o = lse_hm.next();
                lse_hm.next();
                //                lseResult_hm.add(o);
                //                System.out.println(o.toString());
                lseCounter++;
            }

            //        int resultSizeLSE = countResult(lse);
            long runtimeLSE = sw.getElapsedNanoSecTime();
            runtimes_hm[i] = runtimeLSE;
            double lse_hm_memory = MemoryObserver.currentMemoryMB();


            System.out.println("Runtime LSE-HM: " + nanoToSeconds(runtimeLSE));
            System.out.println("resultSize LSE-HM: " + lseCounter);
            System.out.println("LSE-HM memory usage: " + lse_hm_memory);
            //            System.out.println("currentMemory: " + MemoryObserver.currentMemory());
            //            System.out.println("currentMemoryMB: " + MemoryObserver.currentMemoryMB());
            //            System.out.println("usedMemory: " + MemoryObserver.usedMemory());
            //            System.out.println("runtimeMemory: " + MemoryObserver.runtimeMemory());
            //            MemoryObserver.initMemory();
            //            System.out.println("runtimeMemory after initMemory: " + MemoryObserver.runtimeMemory());


            input.reset();
            //            System.gc();


            /** run LSE-A  */
            System.out.println("\n\n ********** Run LSE-A *********** ");

            // FIXME: same input for all algorithms, FlatLCResultSet vs. ArrayList
            arrayInput = input.getElements();
            MemoryObserver.initMemory();
            //            System.out.println("runtimeMemory: " + MemoryObserver.runtimeMemory());

            sw = new Stopwatch();


            BTGDataA btg_ar = new BTGDataArray(new BTG(maxLevels));


            LSE lse_ar = new LSE(arrayInput.iterator(), btg_ar, keySign);

            //            o = null;
            //            ArrayList<Object> lseResult_ar = new ArrayList<>();
            lseCounter = 0;
            Object o = null;
            while (lse_ar.hasNext()) {
                o = lse_ar.next();
                //                lse_ar.next();
                //                lseResult_ar.add(o);
                //                System.out.println(o.toString());
                lseCounter++;
            }
            //        int resultSizeLSE = countResult(lse);
            runtimeLSE = sw.getElapsedNanoSecTime();
            double lse_ar_memory = MemoryObserver.currentMemoryMB();
            runtimes_a[i] = runtimeLSE;


            System.out.println("Runtime LSE-A: " + nanoToSeconds(runtimeLSE));
            System.out.println("resultSize LSE-A: " + lseCounter);
            System.out.println("LSE-A memory usage: " + (lse_ar_memory));


            input.reset();
            //            System.gc();


            /** run LSE-SL  */
            System.out.println("\n\n ********** Run LSE-SL *********** ");

            // FIXME: same input for all algorithms, FlatLCResultSet vs. ArrayList
            arrayInput = input.getElements();
            MemoryObserver.initMemory();
            sw = new Stopwatch();


            BTGDataA btg_sl = new BTGDataLevelBasedSkipList(new BTG(maxLevels));

            LSE lse_sl = new LSE(arrayInput.iterator(), btg_sl, keySign);

            //            o = null;
            ArrayList<Object> lseResult_sl = new ArrayList<>();
            lseCounter = 0;
            while (lse_sl.hasNext()) {
                lse_sl.next();
                //                Object o = lse_sl.next();
                //                lseResult_sl.add(o);
                //                System.out.println(o.toString());
                lseCounter++;
            }
            runtimeLSE = sw.getElapsedNanoSecTime();
            double lse_sl_memory = MemoryObserver.currentMemoryMB();
            runtimes_sl[i] = runtimeLSE;

            System.out.println("Runtime LSE-SL: " + nanoToSeconds(runtimeLSE));
            System.out.println("resultSize LSE-SL: " + lseCounter);
            System.out.println("LSE-SL memory usage: " + lse_sl_memory);
            System.out.println("currentMemory: " + MemoryObserver.currentMemory());

            //            System.gc();


            //            System.out.println("\n\nCheck Assertions");
            //            assert equal(bnlResult, lseResult_hm);
            //            assert equal(bnlResult, lseResult_ar);
            //            assert equal(bnlResult, lseResult_sl);
        }


        // different runs finished


        double bnl_mean = mean(runtimes_bnl);
        double sfs_mean = mean(runtimes_sfs);
        double salsa_mean = mean(runtimes_salsa);
        double hm_mean = mean(runtimes_hm);
        double sl_mean = mean(runtimes_sl);
        double a_mean = mean(runtimes_a);

        double bnl_min = min(runtimes_bnl);
        double sfs_min = min(runtimes_sfs);
        double salsa_min = min(runtimes_salsa);
        double hm_min = min(runtimes_hm);
        double sl_min = min(runtimes_sl);
        double a_min = min(runtimes_a);

        double bnl_max = max(runtimes_bnl);
        double sfs_max = max(runtimes_sfs);
        double salsa_max = max(runtimes_salsa);
        double hm_max = max(runtimes_hm);
        double sl_max = max(runtimes_sl);
        double a_max = max(runtimes_a);

        System.out.printf("\n\n");

        System.out.printf("BNL: Mean ylow yhigh");
        System.out.printf("     " + nanoToSecondsDouble(bnl_mean));
        System.out.printf("     " + nanoToSecondsDouble(bnl_min));
        System.out.printf("     " + nanoToSecondsDouble(bnl_max));

        System.out.printf("\n\n");
        System.out.printf("SFS: Mean ylow yhigh");
        System.out.printf("     " + nanoToSecondsDouble(sfs_mean));
        System.out.printf("     " + nanoToSecondsDouble(sfs_min));
        System.out.printf("     " + nanoToSecondsDouble(sfs_max));

        System.out.printf("\n\n");
        System.out.printf("SaLSa: Mean ylow yhigh");
        System.out.printf("     " + nanoToSecondsDouble(salsa_mean));
        System.out.printf("     " + nanoToSecondsDouble(salsa_min));
        System.out.printf("     " + nanoToSecondsDouble(salsa_max));

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
        for (int i = 0; i < arr.length; ++i)
            mean += arr[i];

        return mean / arr.length;

    }


    private static double min(long arr[]) {
        double a = arr[0];

        for (int i = 1; i < arr.length; ++i) {
            if (arr[i] < a)
                a = arr[i];

        }

        return a;

    }


    private static double max(long arr[]) {
        double a = arr[0];

        for (int i = 1; i < arr.length; ++i) {
            if (arr[i] > a)
                a = arr[i];

        }

        return a;

    }


    private static boolean equal(ArrayList<Object> bnlResult, ArrayList<Object> lseResult) {

        if (bnlResult.size() != lseResult.size())
            System.out.println("Wrong result");
        //            return false;

        for (Object o : bnlResult) {
            if (!lseResult.contains(o))
                System.out.println("Wrong result");
            //                return false;
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
