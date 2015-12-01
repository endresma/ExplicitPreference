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
 * Date: 24.03.14
 * Time: 10:26
 */
public class MemoryObserver {

    private static final Runtime s_runtime = Runtime.getRuntime();


    private static long memory;

    public static long totalMemory() {
        return (s_runtime.totalMemory());
    }


    public static long usedMemory() {
        return (s_runtime.totalMemory() - s_runtime.freeMemory());
    }

    public static void initMemory() {
        memory = usedMemory();
    }

    /**
     * return used memory in bytes
     * @return
     */
    public static long currentMemory() {
        return (usedMemory() - memory);
    }


    /**
     * return used memory in MB, where 1024 KB = 1 MB
     */
    public static double currentMemoryMB() {
        return (usedMemory() - memory) / 1024. / 1024.;
    }


}
