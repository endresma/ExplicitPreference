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

package spo;

import java.util.ArrayList;


public class NodeLists {

    ArrayList<ArrayList<Integer>> list1;
    ArrayList<ArrayList<Integer>> list2;

    Object[] array_of_2_lists = new Object[2];


    /**
     * 2 lists: list1 one contains nodes, all are better than all nodes from list2
     */
    public NodeLists(ArrayList<ArrayList<Integer>> list1, ArrayList<ArrayList<Integer>> list2) {
        this.list1 = list1;
        this.list2 = list2;
    }


    public ArrayList<ArrayList<Integer>> getList1() {
        return list1;
    }


    public ArrayList<ArrayList<Integer>> getList2() {
        return list2;
    }


}

