package src; /**
 * Created by a0r262s on 17.06.2014.
 */
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
public class ReadXMLFile {
    String INFOTEXT="Here you can see all the paths between predefined sources and sinks. For each path\n" +
            "it lists the signature for each entry method in such a sequence as it will be probably called in the\n" +
            "Android application. As you may see it also says the number of methods in each path and\n" +
            "assigns a number to each path and each method. The line INFO in between indicates that a certain path is\n"+
            "not secure.\n";
    public void readAndParseXML() {
        List<Integer> AlertDialogPathHalter = new ArrayList<Integer>();
        int counterSig;
       counterSig=0;
        //MyCommander jct = new MyCommander();
        //new JCommander(jct, args);
       // System.out.println("FileName=    " + jct.APKfileName);
       // String apkfn =  jct.xmlFileName;
        String apkfn =  ".\\results\\"+ ArgsParserMSC.file+"_results.xml";
        //String apkfn = jct.APKfileName;
        try {
            DeleteFile(apkfn);

            File fXmlFile = openXMLFile(apkfn);
            if (fXmlFile.exists()){
                WriteResult(apkfn, INFOTEXT);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("path");
            //loop on paths
            for (int temp = 0; temp < nList.getLength(); temp++) {
                //counterAD=0;
                //counterADB=0;
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    WriteResult(apkfn, "\nNumber of of MethodEntries in Path.No   " + (temp + 1) + "  is   " + eElement.getElementsByTagName("methodEntry").getLength() + ":");
                    int NumberofMEs = eElement.getElementsByTagName("methodEntry").getLength();
                    //loop on method entries for each path
                    for (int i = 0; i < NumberofMEs; i++) {
                        Node en = eElement.getElementsByTagName("methodEntry").item(i);
                        Element een = (Element) en;
                        String result = een.getElementsByTagName("signature").item(0).getTextContent();
                        String callerClassName = een.getElementsByTagName("callerClassName").item(0).getTextContent();
                        counterSig++;
                        /*Just for determining if it has any Show()*/
                        if (result.equals("<android.app.AlertDialog$Builder: android.app.AlertDialog show()>")) {
                            AlertDialogPathHalter.add(temp + 1);// in which path
                            //  counterADB++;
                        }
                        if (result.equals("<android.app.Dialog: void show()>")) {
                            AlertDialogPathHalter.add(temp + 1);// in which path
                            //counterAD++;
                        }
                        WriteResult(apkfn, "\nSignature of Method Entry No.  " + (i + 1) + "   is " + result + ", Class:\t" + callerClassName);
                    }
                    WriteResult(apkfn, "\n");
                }
            }
            WriteResult(apkfn, "\nNumber of total Signatures:" + Integer.toString(counterSig));
            int SizeOfList = AlertDialogPathHalter.size();
            WriteResult(apkfn, "\nUser interactions occurs  " + Integer.toString(SizeOfList) + " times.");
            WriteResult(apkfn, "\nIn these paths there exist User Interaction, No.:   ");
            for (int k = 0; k < SizeOfList; k++) {
                int r = AlertDialogPathHalter.get(k);
                WriteResult(apkfn, Integer.toString(r) + "  ");
            }
        }
        }//try
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File openXMLFile(String outXmlFile) {
        //outXmlFile = outXmlFile + "_results.xml";
        String wp = getWorkingPath();
        //String fdp = "\\FlowDroid";
       // File filemDir = new File (wp+fdp);
        String xml =  outXmlFile;
        //boolean success= (filemDir).mkdirs();
        //if (!success) {
          //  System.out.println("Directory created");
        //}
        File xmlFile = new File(xml);
        if (xmlFile.exists()) {
            System.out.println("Results-XML-File-Size>   " + xmlFile.length()+ "  And "+xmlFile.getAbsolutePath());
        }
        return xmlFile;
    }
    public String getWorkingPath() {
        File directory = new File(".");
        String path = null;
        try {
            path = directory.getCanonicalPath();
        } catch (Exception e) {
            System.out.println("Exceptione is =" + e.getMessage());
        }
        return path;
    }
    public void DeleteFile(String file) {
        file = file + "_xmlparse.txt";
        //String wp = getWorkingPath();
        //String fdp = "\\XMLAnalysis\\";
        String xml = file;
       // File filemDir = new File (wp+fdp);
        File fileD = new File(xml);
        //boolean success= (filemDir).mkdirs();
        //if (!success) {
            //System.out.println("Directory created");
        //}
        if (fileD.exists()) {
            fileD.delete();
        }
    }
    public void WriteResult(String outXmlFile, String data) {
        try {
            outXmlFile = outXmlFile + "_xmlparse.txt";
            //String wp = getWorkingPath();
            //String fdp = "\\XMLAnalysis\\";
            //File filemDir = new File (wp+fdp);
            String xml =  outXmlFile;
            File file = new File(xml);
            //
          //boolean success= (filemDir).mkdirs();
          //  if (!success) {
           //     System.out.println("Directory created");
           // }
            //
            //if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }
            //true = append file
            FileWriter fileWritter = new FileWriter(file, true);
            BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
            bufferWritter.write(data);
            bufferWritter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

