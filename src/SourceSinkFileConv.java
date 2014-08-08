package src;
import java.io.*;
import java.util.Scanner;

/**
 * Created by a0r262s on 21.07.2014.
 */
public class SourceSinkFileConv {
    public static void fileFormat() {
        int counter = 0;
        //boolean si = false;
        String sink =" -> _SINK_\n";
        String source= " -> _SOURCE_\n";
        // String item=null;
        //item = item.substring(item.indexOf("<") + 1);
        //  item = item.substring(0, item.indexOf(":"));
        try {
            File inputSinks = new File(".\\Ouput_CatSinks_v0_9.txt");
            File inputSources = new File(".\\Ouput_CatSources_v0_9.txt");
            File output = new File(".\\SourcesAndSinks.txt");
            System.out.println("Size of SourcesAndSinks.txt\t"+output.length());
            if (!(output.length() > 0)) {
                if (output.exists()) {
                    output.delete();
                }
                Scanner scSinks = new Scanner(inputSinks);
                Scanner scSources = new Scanner(inputSources);
                PrintWriter printer = new PrintWriter(output);
                while (scSinks.hasNextLine()) {
                    String si = scSinks.nextLine();
                    if (si.startsWith("<")) {
                        //s=s.substring(s.indexOf("<")+1);
                        si = si.substring(0, si.lastIndexOf("(") - 1);
                        si = si.concat(sink);
                        printer.write(si);
                        //si=true;
                    }
                    counter++;
                    printer.flush();
                }
                while (scSources.hasNextLine()) {
                    String so = scSources.nextLine();
                    if (so.startsWith("<")) {
                        //s=s.substring(s.indexOf("<")+1);
                        so = so.substring(0, so.lastIndexOf("(") - 1);
                        so = so.concat(source);
                        printer.write(so);
                        //si=true;
                    }
                    counter++;
                    printer.flush();
                }//while
                System.out.println("Lines of two files:\t" + counter);
            }
        }
        catch (FileNotFoundException e) {
            System.err.println("File not found. Please scan in new file.");
        }
    }
}
