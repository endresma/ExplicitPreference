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

/**
 * Interface for the BTG (not the data structure).
 * @author endresma
 */
public interface BTGI {



    /**
     * Returns the number of weak order preferences used to create the BTG.
     *
     * @return number of contained preferences
     */
    int getDimension();


    /**
     * Returns the height of the BTG.
     *
     * @return
     */
    int getHeight();


    /**
     * Returns the edge weights of the BTG:
     *
     * @return
     */
    int[] getEdgeWeights();


    /**
     * Returns the edge weight for an edge existing for domination due to the
     * given preference. <i>P<sub>1</sub>'s edge weight will be read by
     * <code>index = 0</code>.
     *
     * @return edge weight
     */
    int getWeight(int index);


    /**
     * BTG size, i.e. the number of nodes in the BTG
     *
     * @return
     */
    int getSize();


    /**
     * Compute the level of a node with a given ID.
     *
     * @param id the unique ID of a node
     * @return the level of the given node
     */
    int getOverallLevel(int id);


    /**
     * Compute the level of a node with a given level combination.
     *
     * @return the level of the given node
     */
    int getOverallLevel(int[] levelCombination);


    /**
     * Compute the overall level combination for a node with a given ID.
     *
     * @param id the unique ID of a node
     * @return the level combination for the given node
     */
    int[] getLevelCombination(int id);


    /**
     * Compute ID for a given level combination.
     *
     * @param levelCombination
     * @return
     */
    int getID(int[] levelCombination);


    /**
     * Compute the overall level combination for a node with a given ID. The
     * level combination is returned in the input array. The overall level of
     * the node is the value returned by the method
     *
     * @param id        the unique ID of a node
     * @param levelComb array for the level combination
     * @return the level of the given node
     */
    int getInvertedID(int id, int[] levelComb);


    /**
     * Returns the maximal level value at the given position
     */
    int getLevel(int i);


    /**
     * Returns the maximum level of the BTG.
     *
     * @return maximum level value
     */
    int getMaxLevel();


    /**
     * Returns an array containing all maximum level values of the contained
     * WOPs.
     *
     * @return array with all maximum level values
     */
    int[] getMaxLevels();


    /**
     * Returns the width of the BTG in a specified level, cp. Diss Timotheus
     * Preisinger, Graph-based Pareto Algorithms, page 40 and 41.
     *
     * @param level
     * @return
     */
    int getWidth(int level);

    /**
     * Returns the pruning level of the given node. The pruning level is the
     * level of the BTG in which all nodes are dominated by the given node. The
     * computation method was introduced in
     * "The BNL++ Algorithm for Evaluating Pareto Preference Queries"
     *
     * @param lvlCombination
     * @return pruning level of the given node
     */
    // FIXME: me: kann man effizienter implementieren, da hier zuerst die ID
    // berechnet wird, aber in der augerufenen Methode aus der ID wieder eine
    // level combination gemacht wird
    int getPruningLevel(int[] lvlCombination);


    /**
     * Returns the pruning level of the given node. The pruning level is the
     * level of the BTG in which all nodes are dominated by the given node. The
     * computation method was introduced in
     * "The BNL++ Algorithm for Evaluating Pareto Preference Queries"
     *
     * @param id
     * @return pruning level of the given node
     */
    int getPruningLevel(int id);


}
