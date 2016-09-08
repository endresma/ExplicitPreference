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

/**
 * User: endresma
 * Date: 17.06.15
 * Time: 14:12
 */
public interface IPreference {

    public int compare(Object objA, Object objB);

    public int compare(Object objA, Object objB, int idx);

    /**
     * Expresses that an object is equal to another one. This value will be
     * returned if and only if <code>o1.equals(o2) == true</code> for two
     * objects passed to. Note that
     */
    public final int EQUAL = 0;

    /**
     * Expresses that an object is greater than another with respect to the
     * underlying preference. That means that A is preferred to B.
     */
    public final int GREATER = 1;

    /**
     * Expresses that an object A is lesser than another object B with respect
     * to the underlying preference. That means that B is preferred to A.
     */
    public final int LESS = -1;


//    /**
//     * Expresses that two objects are equal and substitutable with respect to
//     * the underlying preference but not identical. Not identical means
//     * always return <code>false</code>.
//     */
//    public final int SUBSTITUTABLE = 2;


    /**
     * Expresses that two objects cannot be compared nor ranked with respect to
     * the underlying preference.
     */
    public final int UNRANKED = -2;

}
