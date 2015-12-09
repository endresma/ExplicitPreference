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

package preference;

import util.IPreference;

import java.util.LinkedList;
import java.util.List;


/**
 * This class models the complex compound preference "Pareto preference". A
 * <code>ParetoPreference</code> weights all it's child preferences equally.
 * This can be seen as a conjunction (linking child preferences with an "AND").
 * <p/>
 */
public class ParetoPreference implements IPreference {


    protected List<IPreference> childPreferences;

    public ParetoPreference() {

        childPreferences = new LinkedList<>();
    }


    public boolean append(IPreference subPreference) {
        return this.childPreferences.add(subPreference);
    }


    public int countChildPreferences() {
        return this.childPreferences.size();
    }


    public boolean equals(Object obj) {

        if (!(obj instanceof ParetoPreference)) {
            return false;
        }

        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof ParetoPreference))
            return false;
        ParetoPreference other = (ParetoPreference) obj;
        if (this.childPreferences == null) {
            if (other.childPreferences != null)
                return false;
        } else if (!this.childPreferences.equals(other.childPreferences))
            return false;
        return true;
    }


    public List<IPreference> getChildPreferences() {
        return this.childPreferences;
    }


    public IPreference getPreference(int index) {
        return childPreferences.get(index);
    }


    public int compare(Object a, Object b, int idx) {
        throw new RuntimeException("Idx compare not supported in " + getClass().getSimpleName());

    }

    public int compare(Object a, Object b) {
        int result = IPreference.UNRANKED;

        int idx = 0;
        for (IPreference childPreference : this.getChildPreferences()) {
            switch (childPreference.compare(a, b, idx)) {
                case IPreference.GREATER:
                    if (result == IPreference.LESS) {
                        return IPreference.UNRANKED;
                    }
                    result = IPreference.GREATER;
                    break;
                case IPreference.LESS:
                    if (result == IPreference.GREATER) {
                        return IPreference.UNRANKED;
                    }
                    result = IPreference.LESS;
                    break;
                //                case IPreference.SUBSTITUTABLE:
                //                    // substitutables "soften" equal
                //                    if (result == IPreference.EQUAL)
                //                        result = IPreference.SUBSTITUTABLE;
                //                        // if substitutable is the first result we must set result to
                //                        // substitutable
                //                    else if (result == IPreference.UNRANKED)
                //                        result = IPreference.SUBSTITUTABLE;
                //                    // substitutables don't change the semantics of greater/less
                //                    break;
                case IPreference.UNRANKED:
                    return IPreference.UNRANKED;
                case IPreference.EQUAL:
                    // equal does not change anything but if it's the first result
                    // we must set result to equal
                    if (result == IPreference.UNRANKED)
                        result = IPreference.EQUAL;
            }
            ++idx;
        }

        return result;
    }


    public int hashCode() {
        return super.hashCode() + 821;
    }


}
