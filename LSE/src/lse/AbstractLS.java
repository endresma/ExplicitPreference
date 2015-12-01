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

package lse;


import btg.BTGDataA;

import java.util.Iterator;

/**
 * User: endresma
 * <p/>
 * The abstract super class of all lattice-based algorithms using
 * different data structures for the BTG.
 */
public abstract class AbstractLS<T extends Iterator<Object>> implements Iterator {


    /**
     * The data structure for the BTG.
     */
    protected BTGDataA btg;


    //    protected boolean hasElements = false;

    protected Iterator result;
    protected Object peek;


    /**
     * Input data for the algorithm
     */
    protected T input;

    /**
     * ctor
     */
    protected AbstractLS() {
    }

    protected AbstractLS(final T input, final BTGDataA btg) {
        this.input = input;
        this.btg = btg;
    }


    /**
     * adding Phase
     */
    protected abstract void computeResult();


    @Override
    public boolean hasNext() {
        if (result == null) {
            computeResult();
        }
        return peek != null;
    }

    @Override
    public Object next() {
        if (result == null) {
            computeResult();
        }
        Object res = peek;
        if (result.hasNext()) {
            peek = result.next();

        } else
            peek = null;
        return res;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove not supported in " + getClass().getName());

    }


}
