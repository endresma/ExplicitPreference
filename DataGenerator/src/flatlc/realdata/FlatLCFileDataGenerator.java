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

import flatlc.inputrelations.FlatLCResultSetA;
import flatlc.inputrelations.RandomResultSetMetaData;
import flatlc.levels.FlatLevelCombination;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;


/**
 * Uses real data from Zillow, NBA, etc.
 * Based on the implementation of LVDN14, APSkyline
 */
public class FlatLCFileDataGenerator extends FlatLCResultSetA {


    /**
     * meta data object
     */
    ResultSetMetaData meta;
    // rows is in the super class
    //    private int rows;
    int currentRowInMem = 0;
    private String filename;
    private int col;
    private ArrayList<Object> elements;

    private ArrayList<int[]> rawData = new ArrayList<>();
    // min and max value for each column
    //    private double[] minLevels;
    private int[] maxValues;

    // multIDs
    private int[] multIDs;


    public FlatLCFileDataGenerator(String filename, int inputSize, int[] maxValues) {


        this.filename = getFilePath(filename);

        // remove ID column, i.e. the first column of the data set
        this.maxValues = maxValues;
        this.col = maxValues.length;
        this.rows = inputSize;


        this.multIDs = new int[col];
        this.multIDs[col - 1] = 1;
        for (int i = col; --i > 0; ) {
            this.multIDs[i - 1] = this.multIDs[i] * (maxValues[i] + 1);
        }

        readRawData();

        convert();
    }


    private String getFilePath(String filename) {
        String canonicalPath = null;
        String OS = null;
        String prefix = null;
        try {
            canonicalPath = new File(".").getCanonicalPath();

            OS = System.getProperty("os.name");
            if (OS.equals("Mac OS X")) {
                prefix = canonicalPath + "/DataGenerator/data/";
            } else {
                prefix = canonicalPath + "/data/";
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


        return (prefix + filename);

    }


    /**
     * Open file and initiate scanner /**
     *
     * @return sc
     */
    private Scanner getScanner() {
        Scanner sc;
        try {
            sc = new Scanner(new File(filename));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Unable to read file: " + filename);
        }
        sc.useLocale(Locale.US);
        return sc;
    }


    /**
     * create FlatLevelCombination from the raw data
     */
    private void convert() {
        elements = new ArrayList<>(rawData.size());

        for (int i = 0; i < rawData.size(); i++) {

            int[] levels = rawData.get(i);
            Object[] object = new Object[levels.length];
            for (int j = 0; j < levels.length; j++)
                object[j] = levels[j];

            FlatLevelCombination flc = new FlatLevelCombination(levels, object, maxValues, multIDs);

            elements.add(flc);

        }

    }


    private void readRawData() {
        Scanner sc = this.getScanner();

        // read raw data into ArrayList
        for (int i = 0; i < rows && sc.hasNext(); ++i) {
            int[] tuple = new int[col];
            for (int j = 0; j < col; ++j) {
                double val = sc.nextDouble();

                tuple[j] = (int) val;
            }
            rawData.add(tuple);
            sc.nextLine();
        }


    }




    @Override
    public Object getMetaData() {
        if (meta == null)
            meta = new RandomResultSetMetaData(maxValues);
        return meta;
    }


    @Override
    public ArrayList<Object> getElements() {
        return (ArrayList<Object>) elements.clone();
    }

    @Override
    public Object next() {
        return elements.get(currentRowInMem++);
    }

    @Override
    public Object peek() throws IllegalStateException, NoSuchElementException, UnsupportedOperationException {
        return elements.get(currentRowInMem);
    }

    @Override
    public boolean supportsPeek() {
        return true;
    }

    @Override
    public void remove() throws IllegalStateException, UnsupportedOperationException {
        throw new UnsupportedOperationException("remove is not supported");
    }

    @Override
    public boolean supportsRemove() {
        return false;
    }

    @Override
    public void update(Object object) throws IllegalStateException, UnsupportedOperationException {
        throw new UnsupportedOperationException("update is not supported");
    }

    @Override
    public boolean supportsUpdate() {
        return false;
    }

    @Override
    public void open() {

    }

    @Override
    public void close() {

    }

    @Override
    public boolean hasNext() {
        return elements.size() > currentRowInMem;
    }

    @Override
    public void reset() {
        currentRowInMem = 0;
    }

    @Override
    public boolean supportsReset() {
        return true;
    }


}
