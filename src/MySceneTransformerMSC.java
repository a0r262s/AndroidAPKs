package src;
/**
 * Created by a0r262s on 04.05.2014.
 */
import java.io.*;
import java.util.*;
import soot.*;
import soot.jimple.*;
public class MySceneTransformerMSC extends SceneTransformer {
    //String result = ".\\result";
    @Override
    protected void internalTransform(String arg0, Map arg1) {
        for (SootClass c : Scene.v().getApplicationClasses()) {
            //if (c.getName().startsWith("com"))//TODO
           // if(c.getName().start)
            try {
                    transform(c);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

        }
    }
    private void transform(SootClass c) throws FileNotFoundException, UnsupportedEncodingException {
        for (SootMethod m : c.getMethods()) {
            if (m.isConcrete()) {
                Body body = m.retrieveActiveBody();
                Iterator<Unit> i = body.getUnits().snapshotIterator();
                while (i.hasNext()) {
                    Unit u = i.next();
                    extractAlertDialogShow(u, body, c);
          //          insertLogAfterShow(u, body, c);
                }
            }
        }

    }
    private void extractAlertDialogShow(Unit u, Body body, SootClass c) throws FileNotFoundException, UnsupportedEncodingException {
        if (u instanceof InvokeStmt) {
            InvokeStmt invoke = (InvokeStmt) u;
            String signature =invoke.getInvokeExpr().getMethod().getSignature();
            if (signature.equals("<android.app.Dialog: void show()>")) {
                SootMethod currentMethod = body.getMethod();
                writeInFile("Statement:     " + u+ "\nWith Signature:     "+signature+ "\nIs in Class:     " + c + "\nIn Method:   " + currentMethod +
                        "\nLine Number in Java Source Code:    " + u.getJavaSourceStartLineNumber() + "\n\n");
            }
            if (signature.equals("<android.app.AlertDialog$Builder: android.app.AlertDialog show()>")) {
                SootMethod currentMethod = body.getMethod();
                writeInFile("Statement:     " + u + "\nWith Signature:     "+signature+"\nIs in Class:     " + c + "\nIn Method:   " + currentMethod +
                        "\nLine Number in Java Source Code:    " + u.getJavaSourceStartLineNumber() + "\n\n");
            }
        }
    }
    private void insertLogAfterShow(Unit u, Body body, SootClass c) {
        SootMethod sm = Scene.v().getMethod("<android.util.Log: int i(java.lang.String,java.lang.String)>");
        Value logType = StringConstant.v("INFO");
        Value logMessage = StringConstant.v("Inserted Log after show()");
        StaticInvokeExpr invokeExpr = Jimple.v().newStaticInvokeExpr(sm.makeRef(), logType, logMessage);
        Unit generated = Jimple.v().newInvokeStmt(invokeExpr);
        if (u instanceof InvokeStmt) {
            InvokeStmt invoke = (InvokeStmt) u;
            String signature =invoke.getInvokeExpr().getMethod().getSignature();
            if (signature.equals("<android.app.Dialog: void show()>")) {
                System.out.println("    Show()" + u.getJavaSourceStartLineNumber() + u.toString());
                body.getUnits().insertAfter(generated, u);
            }
            if (signature.equals("<android.app.AlertDialog$Builder: android.app.AlertDialog show()>")) {
                System.out.println("    Show()" + u.getJavaSourceStartLineNumber() + u.toString());
                body.getUnits().insertAfter(generated, u);
            }
            System.out.println(invoke.getInvokeExpr().getMethod().getSignature());
        }

    }
    public void writeInFile( String data) {
        try {

            int index = ArgsParserMSC.file.lastIndexOf(File.separator);
            String fN = ArgsParserMSC.file.substring(index + 1);
            String xml =  "result"+"_"+fN+".txt";
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
