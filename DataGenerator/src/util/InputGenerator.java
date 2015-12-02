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

package util;

import flatlc.inputrelations.*;
import flatlc.realdata.FlatLCFileDataGenerator;

/**
 * User: endresma
 * Date: 23.09.15
 * Time: 14:28
 */
public class InputGenerator {


    /**
     * Some algorithms need different data structures
     */
    //    private static FlatLCResultSetA input;


    // gleichverteilt, unabhaengig verteilt
    private static final String[] EQUAL = new String[]{"equal", "e", "eq", "i", "ind", "indep", "independent"};
    // korrelliert
    private static final String[] CORREALTED = new String[]{"cor", "corr", "correlated", "c"};
    // anti-korreliert
    private static final String[] ANTICORRELATED = new String[]{"anti", "anticorr", "anti-corr", "anticorrelated", "anti-correlated"};
    // normalverteilt
    private static final String[] GAUSSIAN = new String[]{"gauss", "g", "normal"};
    // real world
    private static final String[] REAL_WORLD = new String[]{"zillow", "z", "nba", "n", "hou", "h", "test", "t", "real", "r"};
    /**
     * specifies if the input data set should be generated and hold in memory or
     * using a perstance service. For our algorithms the in memory tuples should
     * be the best option.
     */

    private final boolean inMemory = true;

    /**
     * Generates the input data set for the algorithms. The distribution is
     * based on the given distribution string.
     *
     * @return FlatLCResultSet
     */

    public static FlatLCResultSetA generateInput(int[] maxLevels, String distribution, int inputSize) {
        // "ind", "corr", "anti", "sortedanti"

        //        FlatLCResultSetA input;
        boolean inMemory = true;

        String realWorldData = null;
        // generate mega byte

        System.out.println("\n********************************************************************************");

        System.out.println("Generate " + inputSize + " tuples");

        FlatLCResultSetA input = null;

        // generate independent / equal distributed data
        for (int i = 0; i < EQUAL.length; i++) {
            if (distribution.equals(EQUAL[i])) {
                input = new FlatLCRandomResultSet(inputSize, maxLevels, inMemory);
            }
        }

        // generate correlated data
        for (int i = 0; i < CORREALTED.length; i++) {
            if (distribution.equals(CORREALTED[i])) {
                input = new FlatLCCorrelatedResultSet(inputSize, maxLevels, inMemory);
            }
        }

        // generate anti-correlated data
        for (int i = 0; i < ANTICORRELATED.length; i++) {
            if (distribution.equals(ANTICORRELATED[i])) {
                input = new FlatLCAntiCorrelatedResultSet(inputSize, maxLevels, inMemory);
            }
        }

        // generate gaussian / normal distributed data
        for (int i = 0; i < GAUSSIAN.length; i++) {
            if (distribution.equals(GAUSSIAN[i])) {
                input = new FlatLCGaussianResultSet(inputSize, maxLevels, inMemory);
            }
        }


        // real world distribution
        for (int i = 0; i < REAL_WORLD.length; i++) {
            if (distribution.equals(REAL_WORLD[i])) {
                if(distribution.equals("zillow")) {
                    realWorldData = "zillow_data.txt";
                    input = new FlatLCFileDataGenerator(realWorldData, inputSize, maxLevels);
                } else
                    throw new RuntimeException("Not supported at the moment: InputGenerator.generateInput(...)");

            }
        }


        if (input != null) {
            //	    new ShowInputDistribution(input, inputSize);
            //	    input.reset();
            return input;
        } else

            throw new RuntimeException("Wrong distribution in RunParallelTest.generateInput");
    }


}
