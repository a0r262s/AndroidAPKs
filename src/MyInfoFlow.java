package src;
import org.xmlpull.v1.XmlPullParserException;
import soot.Scene;
import soot.jimple.Stmt;
import soot.jimple.infoflow.InfoflowResults;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.handlers.ResultsAvailableHandler;
import soot.jimple.infoflow.solver.IInfoflowCFG;
import soot.jimple.infoflow.taintWrappers.EasyTaintWrapper;
import soot.options.Options;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
/**
 * Created by a0r262s on 28.07.2014.
 */
public class MyInfoFlow {
    static InfoflowResults infoflowResults;
    String APKPath=".\\apks\\"+ArgsParserMSC.file;
    public  void initialiseSoot() throws IOException, XmlPullParserException {

        String AndroidJar=System.getenv("ANDROID_HOME")+"\\platforms";
        String forcedPathAndroidJar=System.getenv("ANDROID_HOME")+"\\platforms\\android-17\\android.jar";
        SetupApplication setupApplication = new SetupApplication(forcedPathAndroidJar, APKPath);
        setupApplication.setTaintWrapper(new EasyTaintWrapper("EasyTaintWrapperSource.txt"));
        setupApplication.calculateSourcesSinksEntrypoints("SourcesAndSinks.txt");

        soot.G.reset();
        Options.v().set_src_prec(Options.src_prec_apk);
        Options.v().set_process_dir(Collections.singletonList(APKPath));
        Options.v().set_android_jars(AndroidJar);
        Options.v().set_force_android_jar(forcedPathAndroidJar);
        Options.v().set_whole_program(true);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_output_format(Options.output_format_class);
        Options.v().setPhaseOption("cg.spark", "on");

        Scene.v().loadNecessaryClasses();

        setupApplication.setEnableImplicitFlows(true);
        setupApplication.setEnableStaticFieldTracking(true);
        setupApplication.setFlowSensitiveAliasing(true);
        setupApplication.setEnableCallbacks(true);
        setupApplication.setComputeResultPaths(true);

        ResultsAvailableHandler resultsAvailableHandler = new ResultsAvailableHandler() {
            @Override
            public void onResultsAvailable(IInfoflowCFG iInfoflowCFG, InfoflowResults infoflowResults) {

                Map<InfoflowResults.SinkInfo, Set<InfoflowResults.SourceInfo>>
                        infoSetMap =infoflowResults.getResults();
                for (InfoflowResults.SinkInfo sink : infoSetMap.keySet()) {
                    //sink
                    for (InfoflowResults.SourceInfo source : infoSetMap.get(sink)) {

                        List<Stmt> stmtList=null ;
                        if (source.getPath() != null && !source.getPath().isEmpty())
                            stmtList=source.getPath();
                        int size =stmtList.size();
                        for (Stmt s : stmtList){
                            if ( s.toString().contains("void onClick(android.content.DialogInterface,int)>"))
                            {
                                System.out.println("Index of\t"+stmtList.indexOf(s));
                                System.out.println("Path from void onClick "+stmtList.subList(
                                        0,size));


                            }
                        }

                    }

                }

                try {
                    MySourceSinkExtractor.saveResultsInExternalFile(iInfoflowCFG, infoflowResults, APKPath);
                } catch (ParserConfigurationException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        };


        System.out.println("Starting runInfoFlow in MyInfoFlow");
        long startTime = System.nanoTime();
        infoflowResults = setupApplication.runInfoflow(resultsAvailableHandler);
        long stopTime = System.nanoTime();

        System.out.println("MyInfoFlow nanoTime:\t"+  (double)(stopTime-startTime) / 1000000000.0 +"  seconds");
        System.out.println("End of running in MyInfoFlow");

        System.out.println("\n*** InfoflowResults-get:  ");
        System.out.println(infoflowResults.getResults());
        System.out.println("\n*** End-of-InfoflowResults-get  ");


        System.out.println("\n*** InfoflowResults-print:  ");
        infoflowResults.printResults();
        System.out.println("\n*** End-of-InfoflowResults-print  ");

        System.out.println( "\n*** Size from myCallGraph "+Scene.v().getCallGraph().size()+"\n");


    }
}
