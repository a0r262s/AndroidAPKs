package src;
import com.beust.jcommander.JCommander;
/**
 * Created by a0r262s on 26.06.2014.
 */
public class ArgsParserMSC {
    static String file;
    static String android;
    static String dirOut;
    static boolean boolJimple;
    static String jarSignPath;
    /*
    ArgsParser(String file, String android, String dirOut,boolean boolJimple )
    {
        this.android=android;
        this.file=file;
        this.boolJimple=boolJimple;
        this.dirOut=dirOut;

    }
    */
    public static void extractor (String args[])
    {
        MyJCommanderMSC myJCommander = new MyJCommanderMSC();
        new JCommander(myJCommander, args);
        file=myJCommander.fileName;
        android=myJCommander.androidJar;
        dirOut=myJCommander.outputdir;
        boolJimple=myJCommander.jimple;
        jarSignPath=myJCommander.jarSigner;
    }
}
