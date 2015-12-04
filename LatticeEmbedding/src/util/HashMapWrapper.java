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

package util;

import java.util.*;

/**
 * User: endresma
 * Date: 04.12.15
 * Time: 09:32
 */
public class HashMapWrapper extends HashMap<Object[], int[]> {


    private HashMap<ArrayList<Integer>, int[]> hashMap;

    public HashMapWrapper() {

        hashMap = new HashMap<>();

    }

    public static void main(String[] args) {
        HashMapWrapper hashMap = new HashMapWrapper();

        Object[] key1 = new Object[]{0, 0};
        int[] val1 = new int[]{0, 1, 2};
        Object[] key2 = new Object[]{1, 0};
        int[] val2 = new int[]{3, 4, 5};

        Object[] key3 = new Object[]{4, 4};

        hashMap.put(key1, val1);
        hashMap.put(key2, val2);

        boolean cont = hashMap.containsKey(key1);
        System.out.println("cont: " + cont);
        hashMap.containsKey(key3);

        int[] test = hashMap.get(key1);

        System.out.println("Test finished");


    }

    public int size() {
        return hashMap.size();
    }

    public boolean isEmpty() {
        return hashMap.isEmpty();
    }

    public void putAll(Map m) {
        throw new RuntimeException("putAll not supported");
    }

    public void remove(Object[] key) {
        throw new RuntimeException("remove not supported");
    }

    public void clear() {
        hashMap.clear();
    }

    public boolean containsValue(Object value) {
        throw new RuntimeException("containsValue not supported");
    }

    public Object clone() {
        throw new RuntimeException("clone not supported");
    }

    public Set keySet() {
        Set keySet = hashMap.keySet();

        Set<Object[]> objectSet = new HashSet();

        Iterator it = keySet.iterator();

        while (it.hasNext()) {
            ArrayList<Integer> value = (ArrayList<Integer>) it.next();
            Object[] o = value.toArray();
            objectSet.add(o);

        }

        return objectSet;
    }

    public Collection values() {
        throw new RuntimeException("values not supported");
    }

    public Set entrySet() {
        throw new RuntimeException("entrySet not supported");
    }

    private ArrayList<Integer> convert(Object[] obj) {
        ArrayList<Integer> tmp = new ArrayList<>();

        for (Object o : obj) {
            tmp.add((Integer) o);
        }

        return tmp;
    }

    public int[] put(Object[] key, int[] value) {

        int[] tmp = null;

        if (this.containsKey(key)) {
            tmp = this.get(key);
        }

        hashMap.put(convert(key), value);
        return tmp;


    }

    public boolean containsKey(Object[] key) {

        //        if (!(key instanceof Object[])) {
        //            return false;
        //        }


        ArrayList<Integer> _key = convert(key);

        return hashMap.containsKey(_key);


    }

    public int[] get(Object[] key) {

        if (this.containsKey(key)) {
            ArrayList<Integer> _key = convert(key);
            return hashMap.get(_key);
        }

        return null;
    }

}
