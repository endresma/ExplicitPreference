

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
 * Just for measuring the time for some algorithms.
 *
 * @author markus endres
 */
public class Stopwatch {

    private long startTime;

    /**
     * ctor. Set current system time in nano secons
     */
    public Stopwatch() {
        this.startTime = System.nanoTime();
    }

    /**
     * Return elapsed time in nano seconds.
     *
     * @return
     */
    public long getElapsedNanoSecTime() {
        long now = System.nanoTime();
        return (now - startTime);
    }

    /**
     * Return elapsed time in milli seconds.
     *
     * @return
     */
    public double getElapsedMillSecTime() {
        return this.getElapsedNanoSecTime() / 1000. / 1000.;
    }

    /**
     * Return elapsed Time in seconds.
     *
     * @return
     */
    public double getElapsedSecTime() {
        return this.getElapsedMillSecTime() / 1000;
    }


}
