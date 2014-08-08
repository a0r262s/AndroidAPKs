package src;
/**
 * Created by a0r262s on 04.05.2014.
 */
import org.xmlpull.v1.XmlPullParserException;
import soot.PackManager;
import soot.Transform;

import java.io.File;
import java.io.IOException;
public class MyMain {
    public static void main(String[] args) throws IOException, XmlPullParserException {
/**Main MSC */
        ArgsParserMSC.extractor(args);
       SettingsMSC.initialiseSoot();
        System.out.println("dirOut: "+ ArgsParserMSC.dirOut);
        File existingFile = new File(ArgsParserMSC.dirOut);//outputDirAPK is a directory
        for (File file: existingFile.listFiles()) if (!file.isDirectory()) file.delete();
        if(existingFile.isDirectory()){
            if(existingFile.list().length>0){
                System.out.println("Apk Output Directory is not empty!");
            }
            else{
                System.out.println("Apk Output Directory is empty!");
            }
        }else{
            System.out.println("This path for output is not a directory");
        }
        /** delete the jimble directory first*/
        File jimbleDir = new File(".\\jimple");//outputDirAPK is a directory
        if (jimbleDir.exists()) {
            for (File file : jimbleDir.listFiles()) if (!file.isDirectory()) file.delete();
        }
        /***/
        /** delete the result txt file in ./*/
        int index = ArgsParserMSC.file.lastIndexOf(File.separator);
        String fN = ArgsParserMSC.file.substring(index + 1);
        String rf =  "result"+"_"+fN+".txt";
        String tf= "type"+"_"+fN+".txt";
        String cf= "call"+"_"+fN+".txt";
        String pf="path"+"_"+fN+".txt";
        File rfFile = new File(rf);
        File tfFile= new File (tf);
        File cfFile= new File (cf);
        File pfFile= new File (pf);
        if (rfFile.exists())
            rfFile.delete();
        if (tfFile.exists())
            tfFile.delete();
        if (cfFile.exists())
            cfFile.delete();
        if (pfFile.exists())
            pfFile.delete();
        /***/
        System.out.println("        Getting Pack");
        PackManager.v().getPack("wjtp").add(new Transform("wjtp.MyAnalysis", new MySceneTransformerMSC()));
        System.out.println("        Running Pack");
        PackManager.v().runPacks();
        System.out.println("        Writing Output");
        PackManager.v().writeOutput();
        if (existingFile.list().length>0) {
            System.out.println("\nThe Apk-Output-Dir is not empty.. "+existingFile.list().length);
            MyJarSignerMSC myJarSignerMSC = new MyJarSignerMSC();
            myJarSignerMSC.signAndAlign(existingFile);
        }
        //soot.Main.main(args);
        /***/

        //SourceSinkFileConv sourceSinkFileConv =new SourceSinkFileConv();
        SourceSinkFileConv.fileFormat();//
        //ArgsParserMSC.extractor(args);//
        /**Create info flow without onClicks as sinks*/
        MyInfoFlow myInfoFlow = new MyInfoFlow();
        myInfoFlow.initialiseSoot();
        /**Create info flow with onClicks as extra sinks*/
       MyCallGraph mycallGraph = new MyCallGraph();//
       mycallGraph.initialiseSoot();//

        ReadXMLFile readXMLFile = new ReadXMLFile();
        readXMLFile.readAndParseXML();

        MyReadXML myReadXML = new MyReadXML();
        myReadXML.readAndParseXML();

    }
}
