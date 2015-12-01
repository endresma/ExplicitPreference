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

package flatlc.realdata;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;


/**
 * Uses real data from Zillow, NBA, House, etc.
 */
public class Converter {


    /**
     * findmax values for the data set
     * This is equal to set the maxLevels
     */
    public static String getMaxValues(String filename, int cols, int rows) {

        ArrayList<int[]> rawData = Converter.readRawData(filename, cols, rows);

        int[] tuple;

        int[] max = new int[cols];

        for (int i = 0; i < rawData.size(); i++) {
            tuple = rawData.get(i);
            setMax(max, tuple);
        }


        String out = "";
        for (int i = 0; i < max.length; i++)
            out += max[i] + " ";


        return out;
    }


    // note that the first component is the ID
    private static void setMax(int[] max, int[] tuple) {
        if (max.length != tuple.length)
            throw new RuntimeException("Wrong size of arrays");
        for (int i = 0; i < max.length; i++) {
            max[i] = max[i] > tuple[i] ? max[i] : tuple[i];
        }

    }


    private static String getString(int[] val) {

        StringBuffer sb = new StringBuffer();

        for (int i = 1; i < val.length; i++) {
            if (i != 4) {
                sb.append(val[i]);
                sb.append(" ");
            }
        }

        return sb.toString();
    }

    /**
     * Open file and initiate scanner /**
     *
     * @return sc
     */
    private static Scanner getScanner(String filename) {
        Scanner sc;
        try {
            sc = new Scanner(new File(filename));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Unable to read file: " + filename);
        }
        sc.useLocale(Locale.US);
        return sc;
    }


    private static ArrayList<int[]> readRawData(String filename, int cols, int rows) {

        ArrayList<int[]> rawData = new ArrayList<>();
        Scanner sc = Converter.getScanner(filename);

        // read raw data into ArrayList
        for (int i = 0; i < rows; ++i) {
            //            sc.nextInt();
            int[] tuple = new int[cols];
            for (int j = 0; j < cols; ++j) {
                double val = sc.nextDouble();

                tuple[j] = (int) val;
            }
            rawData.add(tuple);
            sc.nextLine();
        }

        sc.close();
        return rawData;

    }

    //
    //    public static void main(String[] args) {
    //        String canonicalPath = null;
    //        try {
    //            canonicalPath = new File(".").getCanonicalPath();
    //        } catch (IOException e) {
    //            e.printStackTrace();
    //        }
    //        String prefix = canonicalPath + "/FlatLCDataGenerator/data/";
    //
    //
    //        //    private String prefix;
    //        String NBA = "5d-nba-17265.txt";
    //        String HOU = "6d-hou-127931.txt";
    //        String ZILLOW = "ZillowData.txt";
    //        int ZILLOW_SIZE = 2245109;
    ////        int ZILLOW_SIZE = 2000000;
    //        String TEST = "test.txt";
    //
    //
    //        String inputFile = prefix + ZILLOW;
    //        String outputFile = prefix + "zillow_mod.txt";
    //
    //        System.out.println("Max: " + Converter.getMaxValues(outputFile, 4, 2236252));
    //
    //    }


}
