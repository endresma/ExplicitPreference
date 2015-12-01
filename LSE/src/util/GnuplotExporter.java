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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GnuplotExporter {

    private static String MAC_VIEWER = "/Applications/Preview.app/Contents/MacOS/Preview";
    private static String MAC_GNUPLOT = "/opt/local/bin/gnuplot";

    private static String LINUX_VIEWER = "/usr/bin/pqiv";
    private static String LINUX_GNUPLOT = "/usr/bin/gnuplot";

    private static String VIEWER;
    private static String GNUPLOT;

    static {

            String OS = System.getProperty("os.name");
            if (OS.equals("Mac OS X")) {
                VIEWER = MAC_VIEWER;
                GNUPLOT = MAC_GNUPLOT;
            } else {
                VIEWER = LINUX_VIEWER;
                GNUPLOT = LINUX_GNUPLOT;
            }
    }

    /**
     * file that contains data for the diagram
     */
    private File datFile;
    /**
     * file that contains config
     */
    private File pltFile;

    /**
     * labels for the y-coordinates, e.g. algorithm name, etc.
     */
    private String[] labels;
    /**
     * title for the diagram,
     */
    private String[] title;
    /**
     * path to save all output files
     */
    private String path;
    /**
     * x-lavel
     */
    private String xlabel = "x";
    /**
     * y-label
     */
    private String ylabel = "y";
    /**
     * title of the diagram
     */
    private String diaTitle = "";
    // /** file name of result diagram */
    // private String resultFileNmae = "";

    // BufferedWriter
    private BufferedWriter datOut, pltOut;

    /**
     * @param path
     * @param datFileName
     * @param pltFileName
     * @param labels
     * @param xylabels
     * @param title
     */
    public GnuplotExporter(String path, String datFileName, String pltFileName, String[] labels, String[] xylabels, String[] title) {

        for (int i = 0; i < title.length; i++) {
            this.diaTitle += title[i];
//            if (i < title.length - 1)
//                this.diaTitle += "-";
        }

        this.path = path;
        this.datFile = new File(datFileName + diaTitle);
        this.pltFile = new File(pltFileName + diaTitle);
        this.labels = labels;
        this.title = title;
        if (xylabels != null) {
            this.xlabel = xylabels[0];
            this.ylabel = xylabels[1];
        }

        this.createFile();
        writePltFile();
    }

    /**
     * create files
     */
    private void createFile() {
        try {
            datOut = new BufferedWriter(new FileWriter(path + datFile));
            pltOut = new BufferedWriter(new FileWriter(path + pltFile));
        } catch (IOException e) {
            printException(e);
        }
    }

    /**
     * delete the file
     */
    public void delete() {
        this.close();
        datFile.delete();
        pltFile.delete();
    }

    /**
     * clear the content of the file
     */
    public void clearContent() {
        this.delete();
        this.createFile();
    }

    /**
     * write a String s to the file
     *
     * @param s
     */
    public void write(String s) {
        try {
            datOut.write(s);
            datOut.newLine();
        } catch (IOException e) {
            printException(e);
        }
    }

    private void writePltFile() {
        try {
            // pltOut.write("set output \"" + epsFile + "\"");
            pltOut.write("set output \"" + path + datFile + ".jpg" + "\"");
            pltOut.newLine();
            //
            pltOut.write("set terminal jpeg enhanced font 'Helvetica, " +
                    "12' linewidth 4");
            pltOut.newLine();

            pltOut.write("set title \"" + datFile + "\"");
            pltOut.newLine();

            pltOut.write("set xlabel \"" + xlabel + "\"");
            pltOut.newLine();

            pltOut.write("set ylabel \"" + ylabel + "\"");
            pltOut.newLine();

            pltOut.write("set key left");
            pltOut.newLine();

            pltOut.write("set pointsize 2");
            pltOut.newLine();

            pltOut.write("set datafile missing '?'");
            pltOut.newLine();

            // plot command
            pltOut.write("plot");

            // Aussehen der Line, gestrichelt, durchgehende, gepunktet, etc.
            int k = 1;
            for (int i = 0; i < labels.length; i++) {
                pltOut.write(" \"" + path + datFile + "\"");
                pltOut.write(" using 1:" + (i + 2));
                pltOut.write(" with linespoints title");
                pltOut.write(" \"" + labels[i] + "\"");
                pltOut.write(" ls " + k++);

                if (i < labels.length - 1)
                    pltOut.write(", ");
            }

        } catch (IOException e) {
            printException(e);
        }
    }

    /**
     * write some meta data, additional information, to a separate file
     *
     * @param filename
     * @param s
     */
    public static void writeMetaData(String filename, String s) {
        File file = new File(filename);
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.append(s);
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * close the file
     */
    public void close() {
        try {
            datOut.close();
            pltOut.close();
        } catch (IOException e) {
            printException(e);
        }

    }

    /**
     * print exception
     *
     * @param e
     */
    private void printException(IOException e) {
        System.out.println("GnuplotExporter IOException");
        System.out.println(e.getMessage());
        e.printStackTrace();
    }

    public void plot() {
        try {

            String gnu = GNUPLOT + " " + path + pltFile;

            Runtime.getRuntime().exec(gnu);

        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * calls gnuplot and viewer to view the results
     */
    public void showRuntimeDiagram() {

        System.out.println("Show runtimeDiagramm");

        try {

            // String gnu = GNUPLOT + " " + path + pltFile;

            // Runtime.getRuntime().exec(gnu);

            String view = VIEWER + " " + path + datFile + ".jpg";
            // System.out.println(view);
            Runtime.getRuntime().exec("open -a " + view);

        } catch (IOException e) {
            String msg = e.getMessage();
            System.err.println(e.getMessage());
            e.printStackTrace();
        }

    }

}
