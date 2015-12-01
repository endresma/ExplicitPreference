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

package btg;

import flatlc.levels.FlatLevelCombination;

import java.util.Arrays;

/**
 * Representation of a BTG
 *
 * @author endresma
 */

public class BTG implements BTGI {

    /**
     * array which represents the zero node (0,...,0)
     */
    private final int[] nodeZero;

    /**
     * maximum level values of the contained preferences
     */
    private final int[] btgMaxLevels;

    /**
     * height of the BTG
     */
    private final int btgHeight;

    /**
     * size of the BTG, i.e. the number of nodes in the BTG
     */
    private final int btgSize;

    /**
     * array with the edge weights of the BTG
     */
    private int[] btgEdgeWeights;


    /**
     * ctor. Only for BTG analysis.
     *
     * @param maxValues
     */
    public BTG(int[] maxValues) {
        this.btgMaxLevels = maxValues;

        this.btgHeight = height(this.btgMaxLevels);
        this.btgEdgeWeights = edgeWeights(this.btgMaxLevels);
        this.btgSize = size(this.btgMaxLevels);

        this.nodeZero = new int[btgMaxLevels.length];

    }

    /**
     * main method to thest the BTG implementation
     *
     * @param args
     */
    public static void main(String[] args) {

        int maxLevels[] = new int[]{0, 1, 2, 3, 4, 5, 6};

        //        BTG btg = new BTG(maxLevels);


        //        System.out.println("maxLevels: " + Arrays.toString(btg.getMaxLevels()));
        //        System.out.println("height: " + btg.getHeight());
        //        System.out.println("size: " + btg.getSize());
        //
        //        System.out.println("dim: " + btg.getDimension());
        //        System.out.println("edgeWeights: "
        //                + Arrays.toString(btg.getEdgeWeights()));
        //        int ids[] = new int[]{0, 12, 47, 59};
        // Result should be
        // 0: (0,0,0)
        // 12: (0,2,2)
        // 47: (2,1,2)
        // 59: (2,3,4)
        //        for (int i = 0; i < ids.length; i++) {
        //            System.out.println("levelCombination of ID " + ids[i] + ": "
        //                    + Arrays.toString(btg.getLevelCombination(ids[i])));
        //        }
        //
        //        System.out.println("maxLevel BTG: " + btg.getMaxLevel());
        //
        //        int ID = 12;
        //
        //        System.out.println("overallLevel ID " + ID + " : "
        //                + btg.getOverallLevel(ID));
        //        int P = 2;
        //        System.out.println("weight of preference " + P + " : "
        //                + btg.getWeight(P));

        //        int lvl = 6;
        //        System.out.println("width level " + lvl + " : " + btg.getWidth(lvl));
        //        System.out.println("with2 level " + lvl + " : " + btg.getWidth2(lvl));

        //        for (int i = 0; i < btg.getMaxLevel(); i++) {
        //            System.out.println("width level " + i + " : " + btg.getWidth(i));
        //            System.out.println("with2 level " + i + " : " + btg.getWidth2(i));
        //
        //        }


        //        int[] lvlComb = new int[maxLevels.length];
        //        System.out.println("invertID " + ID + " : "
        //                + btg.getInvertedID(ID, lvlComb));
        //        System.out.println("lvlCom of ID " + ID + " : "
        //                + Arrays.toString(lvlComb));

        // test minimum
        // int arr[] = new int[] { 1, 4, 0, 1, 4, 2, 10, 0 };
        // System.out.println("getMinimum  " + btg.getMinimum(arr));

        //        System.out.println("pruning Level of ID " + ID + " : "
        //                + btg.getPruningLevel(ID));
        //        ID = 8;
        //        System.out.println("pruning Level of ID " + ID + " : "
        //                + btg.getPruningLevel(ID));
        //        ID = 0;
        //        System.out.println("pruning Level of ID " + ID + " : "
        //                + btg.getPruningLevel(ID));
        //        ID = 59;
        //        System.out.println("pruning Level of ID " + ID + " : "
        //                + btg.getPruningLevel(ID));
        //
        //        int[] lvlC = new int[]{0, 0, 0};
        //        System.out.println("ID for combination (0,0,0)0: " + btg.getID(lvlC));
        //
        //        lvlC = new int[]{0, 2, 2};
        //        System.out.println("ID for combination (0,2,2)12: " + btg.getID(lvlC));
        //
        //        lvlC = new int[]{2, 0, 3};
        //        System.out.println("ID for combination (2,0,3)43: " + btg.getID(lvlC));
        //
        //        lvlC = new int[]{2, 3, 4};
        //        System.out.println("ID for combination (2,3,4)59: " + btg.getID(lvlC));
        //
        //        System.out.println("Pruning Level von 47(2,1,2): "
        //                + btg.getPruningLevel(47));
        //        System.out.println("Pruning Level von 47(2,1,2): "
        //                + btg.getPruningLevel(new int[]{2, 1, 2}));
        //
        //        int[] node1 = new int[]{0, 1, 1};
        //        int[] node2 = new int[]{2, 0, 2};
        //        System.out
        //                .println("Minimum of "
        //                        + Arrays.toString(node1)
        //                        + " and "
        //                        + Arrays.toString(node2)
        //                        + " should be (0,0,1) , is: "
        //                        + Arrays.toString(btg.getMinimumLevelCombination(node1,
        //                        node2)));
        //
        //        // extended Pruning Level
        //        Vector<int[]> v = new Vector<int[]>();
        //        v.add(node1);
        //        v.add(node2);

    }

    public FlatLevelCombination getLevelCombination(Object value) {
        FlatLevelCombination flc = (FlatLevelCombination) value;
        return flc;
    }

    public FlatLevelCombination constructLevelCombination(int id) {
        double[] values = new double[this.btgMaxLevels.length];
        for (int i = values.length - 1; i >= 0; i--) {
            int mx = 1 + (int) btgMaxLevels[i];
            values[i] = id % mx;
            id = id / mx;
        }
        return constructLevelCombination(values);
    }

    FlatLevelCombination constructLevelCombination(double[] values) {
        //        if (values.length == this.basePrefs.length) {

        double[] maxLevels = new double[btgMaxLevels.length];
        double[] edgeWeights = new double[btgEdgeWeights.length];


        for (int i = 0; i < maxLevels.length; ++i) {
            maxLevels[i] = (int) btgMaxLevels[i];
            edgeWeights[i] = (int) btgEdgeWeights[i];
        }

        int[] level = new int[values.length];
        Object[] value = new Object[values.length];
        for (int i = 0; i < values.length; ++i) {
            level[i] = (int) values[i];
            value[i] = values[i];
        }

        FlatLevelCombination flc = new FlatLevelCombination(level, value, btgMaxLevels, btgEdgeWeights);
        return flc;
    }

    @Override
    public int getDimension() {
        return btgMaxLevels.length;
    }

    @Override
    public int getHeight() {
        return this.btgHeight;
    }

    @Override
    public int[] getEdgeWeights() {
        return this.btgEdgeWeights;
    }

    @Override
    public int getWeight(int index) {
        return btgEdgeWeights[index];
    }

    @Override
    public int getSize() {
        return this.btgSize;
    }

    @Override
    public int getOverallLevel(int id) {
        int result = 0;
        int tmp;
        for (int i = 0; i < btgEdgeWeights.length; i++) {
            tmp = id / btgEdgeWeights[i];
            result += tmp;
            id -= tmp * btgEdgeWeights[i];
        }
        return result;
    }

    @Override
    public int getOverallLevel(int[] levelCombination) {
        return this.getOverallLevel(getID(levelCombination));
    }

    @Override
    public int[] getLevelCombination(int id) {
        int[] result = new int[btgEdgeWeights.length];
        int tmp;
        for (int i = 0; i < btgEdgeWeights.length; i++) {
            tmp = id / btgEdgeWeights[i];
            result[i] = tmp;
            id -= tmp * btgEdgeWeights[i];
        }
        return result;
    }

    @Override
    public int getID(int[] levelCombination) {
        int id = 0;

        for (int i = 0; i < btgEdgeWeights.length; i++) {
            id += btgEdgeWeights[i] * levelCombination[i];
        }

        return id;
    }

    @Override
    public int getInvertedID(int id, int[] levelComb) {
        int result = 0;
        int tmp;
        for (int i = 0; i < btgEdgeWeights.length; i++) {
            tmp = id / btgEdgeWeights[i];
            result += tmp;
            id -= tmp * btgEdgeWeights[i];
            levelComb[i] = tmp;
        }
        return result;
    }

    @Override
    public int getLevel(int i) {
        return btgMaxLevels[i];
    }

    @Override
    public int getMaxLevel() {
        return this.btgHeight - 1;
    }

    @Override
    public int[] getMaxLevels() {
        return btgMaxLevels;
    }

    @Override
    public int getWidth(int level) {
        return this.width(this.btgMaxLevels, level, this.btgHeight, btgMaxLevels.length / 2);
    }


    /**
     * computes the height of the BTG given by mxLvls
     *
     * @param mxLvls
     * @return
     */
    private final int height(int[] mxLvls) {
        return height(mxLvls, 0, mxLvls.length - 1);

    }

    /**
     * computes the height of the BTG given by mxLvls for the
     * ParetoPreference specified by leftIndex and rightIndex (both inclusive)
     *
     * @param mxLvls
     * @param leftIndex
     * @param rightIndex
     * @return
     */
    private final int height(int[] mxLvls, int leftIndex, int rightIndex) {
        int height = 1 + mxLvls[leftIndex];
        for (int i = leftIndex + 1; i <= rightIndex; i++) {
            height += mxLvls[i];
        }
        return height;
    }

    /**
     * computes the edge weights in the BTG given by mxLvls
     *
     * @param mxLvls
     * @return
     */
    private int[] edgeWeights(int[] mxLvls) {
        btgEdgeWeights = new int[btgMaxLevels.length];
        btgEdgeWeights[btgMaxLevels.length - 1] = 1;

        for (int i = btgMaxLevels.length - 2; i >= 0; i--) {
            btgEdgeWeights[i] = btgEdgeWeights[i + 1] * (btgMaxLevels[i + 1] + 1);
        }

        return btgEdgeWeights;
    }

    /**
     * computes the size of the BTG given by mxLvls, i.e. the number of nodes in
     * the BTG
     *
     * @param mxLvls
     * @return
     */
    private int size(int[] mxLvls) {
        int size = mxLvls[0] + 1;
        for (int i = 1; i < mxLvls.length; i++) {
            size *= mxLvls[i] + 1;
        }
        return size;
    }


    /**
     * computes the width of the level v
     *
     * @param maxLvls   (maximal levels of the BTG)
     * @param v         (level to compute the width for)
     * @param btgHeight (height of the BTG)
     * @param rightSize (size of the Pareto splitting)
     * @return
     */
    private int width(int[] maxLvls, int v, int btgHeight, int rightSize) {
        // Terminierungsfaelle
        int width = 0;

        if (rightSize >= maxLvls.length) {
            rightSize = 0;
        }

        if (v == 0 || v == btgHeight - 1)
            return 1;

        if (v > btgHeight - 1)
            return 0;

        if (maxLvls.length < 2) {
            width = 1;
        }

        if (maxLvls.length == 2) {
            if (v <= Math.min(maxLvls[0], maxLvls[1])) {
                width = v + 1;
            } else if (Math.min(maxLvls[0], maxLvls[1]) < v && v <= (maxLvls[0] + maxLvls[1]) / 2) {
                width = Math.min(maxLvls[0], maxLvls[1]) + 1;
            } else { // (maxLevels[0] + maxLevels[1]) / 2) < level
                width = width(maxLvls, btgHeight - 1 - v, height(maxLvls), rightSize);
            }
        }

        if (maxLvls.length > 2) {
            int split;
            if (rightSize <= 0 || rightSize > maxLvls.length)
                split = maxLvls.length / 2;
            else
                split = maxLvls.length - rightSize;

            for (int i = 0; i <= v; i++) {
                // the range
                // of the array, do not copy the Arrays
                int[] left = Arrays.copyOfRange(maxLvls, 0, split);
                int[] right = Arrays.copyOfRange(maxLvls, split, maxLvls.length);
                width += width(left, i, height(left), rightSize) * width(right, v - i, height(right), rightSize);
            }
        }

        return width;
    }

    // FIXME: me: kann man effizienter implementieren, da hier zuerst die ID
    // berechnet wird, aber in der augerufenen Methode aus der ID wieder eine
    // level combination gemacht wird
    @Override
    public int getPruningLevel(int[] lvlCombination) {
        return getPruningLevel(getID(lvlCombination));
    }

    @Override
    public int getPruningLevel(int id) {
        int[] lvlCombination = new int[this.getDimension()];
        int lvl = this.getInvertedID(id, lvlCombination);

        // WOP preference, not a Pareto preference
        if (btgMaxLevels.length == 1)
            return lvl + 1;

        // the node in level 0 has pruning level 1
        if (lvl == 0)
            return 1;

        if (lvl == this.getMaxLevel())
            return this.getMaxLevel() + 1;

        int sumMaxPi = 0;
        for (int l : btgMaxLevels) {
            sumMaxPi += l;
        }

        // --
        // int sum = 0;
        // // if minimal node
        // for (int s : lvlCombination)
        // sum += s;
        // if (sum == 0)
        // return 1;
        // Vector<Integer> tmp = new Vector<Integer>();
        int[] tmp = new int[lvlCombination.length];
        // ArrayList<Integer> tmp = new ArrayList<Integer>();

        for (int i = 0; i < lvlCombination.length; i++) {

            if (lvlCombination[i] != 0) {
                tmp[i] = btgMaxLevels[i] - lvlCombination[i];
            } else {
                tmp[i] = Integer.MAX_VALUE;
            }
        }

        // return (this.getMaxLevel() - getMinimumForPruningLevel(tmp));
        return sumMaxPi - getMinimumForPruningLevel(tmp);
    }

    /**
     * Compute the minimum for each component and return the minimal level
     * combination
     *
     * @param a
     * @param b
     * @return
     */
    private int[] getMinimumLevelCombination(int[] a, int[] b) {

        if (a.length != b.length)
            throw new RuntimeException("Wrong input in BTG.min");

        int[] result = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = a[i] < b[i] ? a[i] : b[i];
        }

        return result;

    }

    /**
     * @param arr
     * @return
     */
    private int getMinimumForPruningLevel(int[] arr) {

        int min = Integer.MAX_VALUE;
        for (int a : arr) {
            if (a < 0)
                throw new RuntimeException("Negative value not allowd in BTG.getMinimum");
            // 0 not allowd, cp. Diss T. Preisinger, Pruning Level computation
            // if (a != 0) {
            if (a < min)
                min = a;
            // }
        }

        return min;
    }

}
