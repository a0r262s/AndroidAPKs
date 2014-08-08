package src;
/**
 * Created by a0r262s on 04.05.2014.
 */
import java.io.*;
import java.util.Collections;
import soot.G;
import soot.Scene;
import soot.options.Options;
public class SettingsMSC {
    private static boolean SOOT_INITIALIZED = false;

    public static void initialiseSoot() {
        Options.v().set_allow_phantom_refs(true);
        System.out.println("Dirout in Settings=    "+ ArgsParserMSC.dirOut);
        System.out.println("Jimple=    "+ ArgsParserMSC.boolJimple);
        String androidJAR= ArgsParserMSC.android;
        String outputdir= ArgsParserMSC.dirOut;
        boolean success= (new File (outputdir)).mkdirs();
        System.out.println("Dirs are created    "+success);
        File dest = new File(".\\apks\\"+ ArgsParserMSC.file);
        String outputDirJimple = ".\\My "+ArgsParserMSC.file+" jimple";
        String apk=dest.getPath();
        if (SOOT_INITIALIZED)
            return;
        G.reset();
        Options.v().set_whole_program(true);
        Options.v().set_prepend_classpath(true);
        Options.v().set_validate(true);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_output_format(Options.output_format_dex);
        Options.v().set_process_dir(Collections.singletonList(apk));
        Options.v().set_force_android_jar(androidJAR);
        Options.v().set_src_prec(Options.src_prec_apk);
        Options.v().set_soot_classpath(androidJAR);
        Scene.v().loadClassAndSupport("android.util.Log");
        Scene.v().loadNecessaryClasses();
        Scene.v().loadDynamicClasses();//
        Scene.v().loadBasicClasses();//
        if (ArgsParserMSC.boolJimple)
            Options.v().set_output_format(Options.output_format_jimple);
        else
            Options.v().set_output_format(Options.output_format_dex);
       Options.v().set_process_dir(Collections.singletonList(apk));
        if (ArgsParserMSC.boolJimple)
            Options.v().set_output_dir(outputDirJimple);
        else
            Options.v().set_output_dir(outputdir);
        Options.v().set_soot_classpath(androidJAR);
        SOOT_INITIALIZED = true;
    }
}
