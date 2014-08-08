package src;
/**
 * This file contains the methods responsible for creating infoflow with the void onClikcks as sinks
 * Created by a0r262s on 04.05.2014.
 */
import java.io.*;
import java.util.*;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;
import soot.*;
import soot.Body;
import soot.jimple.*;
import soot.jimple.Stmt;
import soot.jimple.infoflow.InfoflowResults;
import soot.jimple.infoflow.android.AndroidSourceSinkManager;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.android.data.AndroidMethod;
import soot.jimple.infoflow.handlers.ResultsAvailableHandler;
import soot.jimple.infoflow.solver.IInfoflowCFG;
import soot.jimple.infoflow.taintWrappers.EasyTaintWrapper;
import soot.jimple.toolkits.callgraph.Targets;
import soot.jimple.toolkits.callgraph.TransitiveTargets;
import soot.options.Options;
import soot.util.Chain;
import javax.xml.parsers.ParserConfigurationException;
public class MyCallGraph {
    InfoflowResults infoflowResults;
    static Set <AndroidMethod> androidMethodSetSinks;
    static Set <AndroidMethod> androidMethodsSetSources;
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
        //PackManager.v().runPacks();
        //
        //Options.v().set_prepend_classpath(true);
        //Options.v().prepend_classpath();
        //Options.v().allow_phantom_refs();//new
        //Options.v().whole_program();
        //
       ////
        //Options.v().set_validate(true);
        //Options.v().setPhaseOption("cg.cha", "on");
        //String outputDirJimple="jimple";
        //Options.v().set_output_dir(outputDirJimple);
        //Options.v().set_output_format(Options.output_format_jimple);
        //Options.v().set_process_dir(Collections.singletonList(APKPath));
        //Options.v().setPhaseOption("cg.spark", "rta:true");
        //Options.v().setPhaseOption("cg.spark", "vta:true");
       //Scene.v().setPhantomRefs(true);//
        //Options.v().soot_classpath();
        //Options.v().set_soot_classpath(forcedPathAndroidJar);
        //PackManager.v().getPack("wjtp");
       //Infoflow.setPathAgnosticResults(false);
        setupApplication.setIgnoreFlowsInSystemPackages(true);
        setupApplication.setEnableImplicitFlows(true);
        setupApplication.setEnableStaticFieldTracking(true);
        setupApplication.setFlowSensitiveAliasing(true);
        setupApplication.setEnableCallbacks(true);
        //setupApplication.setEnableExceptionTracking(true);
        setupApplication.setComputeResultPaths(true);
        //Scene.v().addBasicClass("java.lang.Exception",SootClass.BODIES);
        //Scene.v().loadDynamicClasses();//
        //Scene.v().loadBasicClasses();//
        //Scene.v().loadNecessaryClasses();
        /**
         * Gets the set of sinks loaded into FlowDroid
         * @return The set of sinks loaded into FlowDroid
         */
        androidMethodSetSinks = setupApplication.getSinks();
        //System.out.println("AndroidSInks:\t"+androidMethodSetSinks);
        System.out.println("\nSize of sink set before automatically adding new sinks\t"+androidMethodSetSinks.size());
        //System.out.println("\n*** Sinks extracted");
        androidMethodsSetSources=setupApplication.getSources();
        //System.out.println("\n*** Sources Extracted");
        /**the following has to be called always after calculateSourceSinkEntrypoints**/
        AndroidSourceSinkManager androidSourceSinkManager =setupApplication.getSourceSinkManager();
        androidSourceSinkManager.setEnableCallbackSources(true);//TODO ?? The functionality--refer to the emails
        //System.out.println("CallBack\t"+setupApplication.getEntryPointCreator().getCallbackFunctions());//TODO-Parsing the functions from here, DONE!
        /** Automatically add the sinks which they contains onclick in method name**/
        ResultsAvailableHandler resultsAvailableHandlerWithOnClickAsSink = new ResultsAvailableHandler() {
            @Override
            public void onResultsAvailable(IInfoflowCFG iInfoflowCFG, InfoflowResults infoflowResults) {
                int i=1;
                Map<InfoflowResults.SinkInfo, Set<InfoflowResults.SourceInfo>>
                        infoSetMap =infoflowResults.getResults();
                for (Map.Entry<InfoflowResults.SinkInfo, Set<InfoflowResults.SourceInfo>> entry : infoSetMap.entrySet()) {
                    //sink
                   System.out.println("\nIFlow to sink:\t" +
                           entry.getKey()+ "\nin method\t"+iInfoflowCFG.getMethodOf(entry.getKey().getContext()).getSignature()
                           + "\nfrom the following sources:");
                    writeInFile("path","\nIFlow to sink:\t" +
                            entry.getKey()+ "\nin method\t"+iInfoflowCFG.getMethodOf(entry.getKey().getContext()).getSignature()
                            + "\nfrom the following sources:\n");
                    for (InfoflowResults.SourceInfo source : entry.getValue()) {
                        System.out.println("\nSource:\t" +source+ "\nin method\t"+iInfoflowCFG.getMethodOf(source.getContext()).getSignature() + "\n");
                        writeInFile("path","\n\nSource:\t" +source+ "\nin method\t"+iInfoflowCFG.getMethodOf(source.getContext()).getSignature() + "\n");
                        List<Stmt> stmtList=null ;
                        if (source.getPath() != null && !source.getPath().isEmpty())
                            stmtList=source.getPath();
                        System.out.println("On Path:"+"\t"+i+"\t:");
                        writeInFile("path", "\nOn Path:" + "\t" + i + "\t:" + "\n");
                        i++;
                        for (Unit u : stmtList){
                            System.out.println("Method:\t"+iInfoflowCFG.getMethodOf(u));//returns the method

                            // which this unit u resides
                            System.out.println("Unit:\t"+u);
                            writeInFile("path", "Unit:\t" + u + "\n");
                            writeInFile("path","in method:\t"+iInfoflowCFG.getMethodOf(u)+"\n\n");
                        }
                    }
                }
                try {
                    SourceSinkExtractor.saveResultsInExternalFile(iInfoflowCFG, infoflowResults, APKPath);
                } catch (ParserConfigurationException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }



            }
        };
        //TODO
        //Infoflow.getPathAgnosticResults();
        //Infoflow.setPathAgnosticResults(false);
        //Infoflow infoflow = new Infoflow(AndroidJar,true);
//        infoflow.computeInfoflow(APKPath, forcedPathAndroidJar, setupApplication.getEntryPointCreator(), androidSourceSinkManager);
       // infoflow.getResults();
        //infoflow.removeResultsAvailableHandler(resultsAvailableHandler);

        //infoflow.setEnableImplicitFlows(true);
        //infoflow.setComputeResultPaths(true);
        //infoflow.setFlowSensitiveAliasing(true);
        //infoflow.setInspectSinks(true);
        //infoflow.setInspectSinks(true);


        /**addSinkAuto*/
        addSinksAuto(setupApplication, androidSourceSinkManager);

        System.out.println("Starting runInfoFlow in MyCallGraph");
        long startTime = System.nanoTime();
        infoflowResults = setupApplication.runInfoflow(resultsAvailableHandlerWithOnClickAsSink);
        long stopTime = System.nanoTime();

        System.out.println("myCallGraph nanoTime:\t"+ (double)(stopTime-startTime) / 1000000000.0 +"  seconds");
        System.out.println("End of running in MyCallGraph");
        //PackManager.v().runPacks();
        //PackManager.v().writeOutput();
       // ArrayList <SootMethod> sootMethodArrayList=onClickExtractor(setupApplication);//Method TODO after inforflowresults

       // addOnClickSinks(sootMethodArrayList);
        System.out.println("\n*** InfoflowResults-get:  ");
        infoflowResults.getResults();
       System.out.println(infoflowResults.getResults());
        System.out.println("\n*** End-of-InfoflowResults-get  ");
        System.out.println("\n*** InfoflowResults-print:  ");
        infoflowResults.printResults();
        System.out.println("\n*** End-of-InfoflowResults-print  ");
        /*if runpack is activated then the size of cal graph will be reduced */
        //PackManager.v().runPacks();
        System.out.println("\nAnalysis:\t");
        mainActivityExtractor();// Method

       System.out.println( "\n*** Size from myCallGraph "+Scene.v().getCallGraph().size()+"\n");//TODO Call-Graph
       callGraphAnalyser();//TODO callGraph analysis

    }

    public void addSinksAuto(SetupApplication setupApplication, AndroidSourceSinkManager androidSourceSinkManager){
        /****/
        /**Getting the list of callbacks**/
        Map <String, List<String>> stringListMap = setupApplication.getEntryPointCreator().getCallbackFunctions();
        //System.out.println("Values:\t" + stringListMap.values());
        Collection<List<String>> listCollection=stringListMap.values();
        for (List<String > ls : listCollection){
            //System.out.println("ListString\t"+ls);
            for(String item:ls){
                if (item.contains("void onClick(android.content.DialogInterface,int)")) {

                    item = item.substring(item.indexOf("<") + 1);
                    item = item.substring(0, item.indexOf(":"));
                    //System.out.println("Item\t" + item);
                    AndroidMethod am =new AndroidMethod("onClick",Arrays.asList("android.content.DialogInterface", "int"), "void", item);
                    System.out.println("AM\t"+am);
                    writeInFile("type",am.toString()+"\n");
                    am.setSink(true);
                    /*
                    Set<AndroidMethod> sinks
                     */
                    androidMethodSetSinks.add(am);



                }
            }
        }
        androidSourceSinkManager.addSink(androidMethodSetSinks);//TODO
        System.out.println("\nSize of sinks after adding\t"+setupApplication.getSinks().size());

    }
    public ArrayList<SootMethod> onClickExtractor (SetupApplication setupApplication)
    {
        ArrayList<SootMethod> signatureArray = new ArrayList<SootMethod>();
        SootMethod entryPoint = setupApplication.getEntryPointCreator().createDummyMain();
        Options.v().set_main_class(entryPoint.getSignature());
        Scene.v().setEntryPoints(Collections.singletonList(entryPoint));
        //System.out.println("getActiveBody of entry-point             ");
        Body entryPointActiveBody = entryPoint.getActiveBody();
        //System.out.println("getActiveBody of entry-point             \n"+entryPointActiveBody);
        Iterator<Unit> i = entryPointActiveBody.getUnits().snapshotIterator();
        while (i.hasNext()) {//
            Unit u = i.next();
            if (u instanceof InvokeStmt) {
                InvokeStmt invoke = (InvokeStmt) u;
                InvokeExpr invokeExpr =invoke.getInvokeExpr();
               if (invokeExpr.getMethod().getSubSignature().equals(	"void onClick(android.content.DialogInterface,int)")){
                   signatureArray.add(invokeExpr.getMethod());
                    SootMethod currentMethod = entryPointActiveBody.getMethod();
                    System.out.println("Statement:\t" + u+  "\nSubSig:\t" +invokeExpr.getMethod() +
                            "\nIn Method:\t"+ currentMethod +"\n\n");
               }
            }

        }//while
        return signatureArray;
    }
    /**generates pairs of methods related to buttons of an AlertDialog and the type of their arguments
     * the result will be wrote in txt file with "type_"*/
    public void mainActivityExtractor (){

        //Scene.v().addBasicClass("java.lang.Exception",SootClass.BODIES);
        //CHATransformer.v().transform();//TODO example
        //SparkTransformer.v().transform();
        Chain<SootClass> a = Scene.v().getClasses();// it must be getClasess method, as without it we cannot have the onClick method in results of callGraph
        soot.jimple.toolkits.callgraph.CallGraph cg = Scene.v().getCallGraph();
        System.out.println("\n*** Size in onResultsAvailable:  " + cg.size());
        for (SootClass c : a) {
            //System.out.println("Class:\t"+ c.getName());
            List<SootMethod> methods = c.getMethods();
            //System.out.println("\nSize:  "+cg.size());
            //String d= sootMethodElement.getDeclaration();
            for (SootMethod sootMethodElement : methods)
                if (sootMethodElement.isConcrete()) {
                    Body body = sootMethodElement.retrieveActiveBody();
                    //SootMethod currentMethod = body.getMethod();
                    Iterator<Unit> i = body.getUnits().snapshotIterator();
                    while (i.hasNext()) {//
                        Unit u = i.next();
                        //if (u instanceof InvokeStmt) {
                        //  InvokeStmt invoke = (InvokeStmt) u;
                        //SootMethod sm = invoke.getInvokeExpr().getMethod();
                        //if (u instanceof AssignStmt) {
                        //AssignStmt assignStmt = (AssignStmt) u;
                        //String stringAssStmt =assignStmt.toString();
                        //if  (stringAssStmt.contains("new"))
                        // if (!stringAssStmt.contains("java.lang.RuntimeException"));
                        // System.out.println("\tAssignStmt " + assignStmt + "\t");
                        //}
                        if (u instanceof InvokeStmt) {
                            InvokeStmt invoke = (InvokeStmt) u;
                            InvokeExpr invokeExpr = invoke.getInvokeExpr();
                            String signature = invokeExpr.getMethod().getSignature();
                            List<Value> lv;
                            if (signature.contains("(int,android.content.DialogInterface$OnClickListener)")||
                                    signature.contains("(java.lang.CharSequence,android.content.DialogInterface$OnClickListener)")) {

                                //SootMethod currentMethod = body.getMethod();
                                lv = invoke.getInvokeExpr().getArgs();
                                //System.out.println("List Value\t"+lv);
                                for (Value v : lv) {
                                    v.getType();
                                    if (v.getType().toString().contains("$")) {
                                        //System.out.println("\tType\t" + v + "\tValue\t" + v.getType());
                                        //System.out.println("Statement:\t" + u + "\nType of its argument\t" + v.getType()+"\n");
                                        writeInFile("type","Statement:\t" + u + "\nType of its argument\t" + v.getType()+"\n");
                                        //"\nIs in Class:\t" + c + "\nIn Method:\t" + currentMethod +
                                        //"\n\n");
                                    }
                                }

                            }
                        }
                        /** Returns an iterator over all edges that have u as their source unit. */
                        //sootMethodElement.getTags();
                        //Iterator<MethodOrMethodContext> targets = new Targets(cg.edgesOutOf(sootMethodElement));
                        //while (targets.hasNext()) {
                        //  MethodOrMethodContext methodOrMethodContext = targets.next();
                        //Context context =methodOrMethodContext.context();
                        //SootMethod tgt = (SootMethod) methodOrMethodContext;
                        //Body tgtBody= tgt.retrieveActiveBody();
                        //System.out.println("\n" + sootMethodElement + "\n\tMay call    " + tgt + "\n");
                        //}
                    }
                }//
        }

    }

    public void addOnClickSinks(ArrayList <SootMethod> sootMethodArrayList){
        /**For the file of flowDroid.jar**/
        String category=" (NO_CATEGORY)";

        /** for the soot-infoflow-android**/
        String sink =" -> _SINK_";
        String fileCatName = ".\\Ouput_CatSinks_v0_9.txt";
        String fileSiSuName =".\\SourcesAndSinks.txt";


        try {

            File fileCat = new File(fileCatName);
            File fileSiSu = new File(fileSiSuName);
            //System.out.println("\nAbsolute path to write "+fileCat.getAbsolutePath()+"\n");
            //if file doesnt exists, then create it
            if (!fileCat.exists()) {
                fileCat.createNewFile();
            }
            if (!fileSiSu.exists()) {
                fileSiSu.createNewFile();
            }
            //true = append file
            FileWriter fileWritter1 = new FileWriter(fileCat, true);
            FileWriter fileWritter2 = new FileWriter(fileSiSu, true);

            BufferedWriter bufferWritter1 = new BufferedWriter(fileWritter1);
            BufferedWriter bufferWritter2 = new BufferedWriter(fileWritter2);

            for (int i=0; i< sootMethodArrayList.size(); i++){
                sootMethodArrayList.get(i);
                System.out.println("ArrayList:\t"+sootMethodArrayList.get(i));
                /**For the file of flowDroid.jar**/
                bufferWritter1.write("\n"+sootMethodArrayList.get(i)+category);
                /** for the soot-infoflow-android**/
                bufferWritter2.write("\n"+sootMethodArrayList.get(i)+sink);
            }

            bufferWritter1.close();
            bufferWritter2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void callGraphAnalyser (){
        Scene.v().addBasicClass("java.lang.Exception",SootClass.BODIES);
       // CHATransformer.v().transform();//TODO example
        //SparkTransformer.v().transform();
        Chain<SootClass> a = Scene.v().getClasses();// it must be getClasess method, as without it we cannot have the onClick method in results of callGraph
        soot.jimple.toolkits.callgraph.CallGraph cg = Scene.v().getCallGraph();
        System.out.println("\n*** Size in callGraphAnalyzer:  " + cg.size());
        for (SootClass c : a) {
            //System.out.println("Class:\t"+ c.getName());
            List<SootMethod> methods = c.getMethods();
            //System.out.println("\nSize:  "+cg.size());
            //String d= sootMethodElement.getDeclaration();
            for (SootMethod sootMethodElement : methods){
               // if (sootMethodElement.isConcrete()) {
                  //  Body body = sootMethodElement.retrieveActiveBody();
                    //SootMethod currentMethod = body.getMethod();
                   // Iterator<Unit> i = body.getUnits().snapshotIterator();
                  //  while (i.hasNext()) {//
                     //   Unit u = i.next();
                        //if (u instanceof InvokeStmt) {
                        //  InvokeStmt invoke = (InvokeStmt) u;
                        //SootMethod sm = invoke.getInvokeExpr().getMethod();
                        //if (u instanceof AssignStmt) {
                        //AssignStmt assignStmt = (AssignStmt) u;
                        //String stringAssStmt =assignStmt.toString();
                        //if  (stringAssStmt.contains("new"))
                        // if (!stringAssStmt.contains("java.lang.RuntimeException"));
                        // System.out.println("\tAssignStmt " + assignStmt + "\t");
                        //}
                        /*
                        if (u instanceof InvokeStmt) {
                            InvokeStmt invoke = (InvokeStmt) u;
                            InvokeExpr invokeExpr = invoke.getInvokeExpr();
                            String signature = invokeExpr.getMethod().getSignature();
                            List<Value> lv;
                            if (signature.contains("(int,android.content.DialogInterface$OnClickListener)")||
                                    signature.contains("(java.lang.CharSequence,android.content.DialogInterface$OnClickListener)")) {

                                //SootMethod currentMethod = body.getMethod();
                                lv = invoke.getInvokeExpr().getArgs();
                                //System.out.println("List Value\t"+lv);
                                for (Value v : lv) {
                                    v.getType();
                                    if (v.getType().toString().contains("$")) {
                                        //System.out.println("\tType\t" + v + "\tValue\t" + v.getType());
                                        System.out.println("Statement:\t" + u + "\nType of its argument\t" + v.getType()+"\n");
                                        //"\nIs in Class:\t" + c + "\nIn Method:\t" + currentMethod +
                                        //"\n\n");
                                    }
                                }

                            }
                        }
                        */
                        /** Returns an iterator over all edges that have u as their source unit. */
                        //sootMethodElement.getTags();
                        Iterator<MethodOrMethodContext> targets = new Targets(cg.edgesOutOf(sootMethodElement));
                        while (targets.hasNext()) {
                            MethodOrMethodContext methodOrMethodContext = targets.next();
                            Context context = methodOrMethodContext.context();
                            SootMethod tgt = (SootMethod) methodOrMethodContext;
                            //Body tgtBody= tgt.retrieveActiveBody();
                            //System.out.println("\n" + sootMethodElement + "\n\tMay call    " + tgt + "\n");
                            writeInFile("call", "\n" + sootMethodElement + "\n\tMay call    " + tgt + "\n");
                        }

                            TransitiveTargets transitiveTargets = new TransitiveTargets(cg);
                            Iterator <MethodOrMethodContext> methodOrMethodContextIterator=
                                     transitiveTargets.iterator(sootMethodElement);//TODO unit, replace it for getting the class, in resultAvailable Handler

                            //System.out.println("SootMethod in\t"+sootMethodElement+"\n Transitive Targets:\t");
                            writeInFile("call", "\n" + "SootMethod in\t"+sootMethodElement+"\nTransitive targets:\n");
                            while (methodOrMethodContextIterator.hasNext()){

                                //System.out.println("\t"+ methodOrMethodContextIterator.next());
                                writeInFile("call", "\n\t"+ methodOrMethodContextIterator.next()+"\n");
                        }

                    }
               // }//
        }//for soot class

    }

    public void writeInFile(String fn ,String data) {
        try {

            int index = ArgsParserMSC.file.lastIndexOf(File.separator);
            String fN = ArgsParserMSC.file.substring(index + 1);
            String xml =  fn+"_"+fN+".txt";
            File file = new File(xml);

            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file, true);
            BufferedWriter bufferWriter = new BufferedWriter(fileWriter);
            bufferWriter.write(data);
            bufferWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    }
