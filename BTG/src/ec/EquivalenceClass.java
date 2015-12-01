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

package ec;


import flatlc.levels.FlatLevelCombination;
import util.IPreference;

import java.util.*;

/**
 * An object of this class represents an equivalence class (i.e. a node) of a
 * better-than-graph (BTG). For each node, the level combination identifying the equivalence class and the objects belonging to it are stored.
 * @author endresma
 */
public class EquivalenceClass implements IEquivalenceClass {


    /**
     * need it for synchronize in parallel Hexagon algorithms. It is not
     * possible to synchronize(null), must be on an object.
     */
    protected boolean isNullObject = false;

    /**
     * tuples belonging to the equivalence class
     */
    protected List<Object> elements = new ArrayList<Object>();


    /**
     * level combination representing this equivalence class
     */
    protected FlatLevelCombination lvl;
    protected int hash = Integer.MAX_VALUE;


    /**
     * Constructor. The equivalence class is initialized with its level
     * combination.
     *
     * @param lvl level combination of this equivalence class
     */
    public EquivalenceClass(FlatLevelCombination lvl) {
        this.lvl = lvl;
        this.elements.add(this.lvl);
    }

    @Override
    public void add(FlatLevelCombination level) {
        elements.add(level);
    }

    @Override
    public boolean checkedAdd(FlatLevelCombination level) {
        // Element hinzufuegen, wenn es die gleiche LevelCombination hat (also
        // EQUAL
        // oder SUBSTITUTABLE ist)
        switch (this.lvl.compare(level)) {
            case IPreference.EQUAL:
            case IPreference.SUBSTITUTABLE:
                return elements.add(level);
        }
        return false;
    }

    @Override
    public boolean add(Object object) {
        return elements.add(object);
    }

    @Override
    public boolean add(IEquivalenceClass ec) {
        // Element hinzufuegen, wenn es die gleiche LevelCombination hat (also
        // EQUAL
        // oder SUBSTITUTABLE ist)
        switch (this.lvl.compare(ec.getLevelCombination())) {
            case IPreference.EQUAL:
                break;
            case IPreference.SUBSTITUTABLE:
                break;
            default:
                return false;
        }

        return elements.addAll(ec.getElements());
    }

    @Override
    public FlatLevelCombination getLevelCombination() {
        return this.lvl;
    }

    @Override
    public List<Object> getElementsSorted(Comparator<Object> comparator) {
        Collections.sort(elements, comparator);
        return this.elements;
    }

    @Override
    public List<Object> getElements() {
        return this.elements;
    }

    @Override
    public Iterator<Object> iterator() {
        return this.elements.iterator();
    }

    @Override
    public int getSize() {
        return this.elements.size();
    }

    @Override
    public Object getElement(int index) {
        return this.elements.get(index);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        // Lazy-Instantiierung
        if (hash == Integer.MAX_VALUE)
            ;
        {
            double result = this.lvl.getLevel(0);
            int size = this.lvl.getSize();
            for (int i = 1; i < size; i++) {
                result *= 1000.0;
                result += this.lvl.getLevel(i);
            }
            hash = (int) result;
        }

        return hash;
    }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append(getClass().getName());
        result.append('[');
        int size = elements.size();
        result.append(this.lvl).append(size).append(" Element");
        if (size != 1)
            result.append('e');
        if (size > 0) {
            result.append(": ");
            for (int i = 0; i < size; i++) {
                result.append(elements.get(i));
            }
        }
        result.append(']');
        return result.toString();
    }


    @Override
    public int compare(IEquivalenceClass ec) {
        return this.lvl.compare(ec.getLevelCombination());
    }

    @Override
    public int compareTo(Object o) {

        return this.compare((IEquivalenceClass) o);

    }


}
