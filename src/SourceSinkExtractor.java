package src;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.DefinitionStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Stmt;
import soot.jimple.infoflow.InfoflowResults;
import soot.jimple.infoflow.InfoflowResults.SinkInfo;
import soot.jimple.infoflow.InfoflowResults.SourceInfo;
import soot.jimple.toolkits.ide.icfg.BiDiInterproceduralCFG;
import soot.tagkit.LineNumberTag;

public class SourceSinkExtractor {
	private static boolean isWindows = System.getProperty("os.name").toLowerCase().contains("windows");
	
	public static void saveResultsInExternalFile(
			BiDiInterproceduralCFG<Unit, SootMethod> cfg,
			InfoflowResults results, String appName) throws ParserConfigurationException {

		boolean saveFile = false;
		DocumentBuilderFactory docFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		Element elems = doc.createElement("elems");
		doc.appendChild(elems);

		for (SinkInfo sink : results.getResults().keySet()) {
			Element sinkElem = doc.createElement("sink");

			SootMethod method = sink.getContext().getInvokeExpr().getMethod();

			getMethodInfo(doc, method, sinkElem, sink.getContext().getInvokeExpr(), sink.getContext(), cfg);

			Element sourcesMethod = doc.createElement("sources");
			sinkElem.appendChild(sourcesMethod);
			Element source = null;
			for (SourceInfo sourceInfo : results.getResults().get(sink)) {
				if(sourceInfo.getContext().containsInvokeExpr()){
					source = doc.createElement("source");
					sourcesMethod.appendChild(source);
					getMethodInfo(doc, sourceInfo.getContext().getInvokeExpr().getMethod(), source, sourceInfo.getContext().getInvokeExpr(), sourceInfo.getContext(), cfg);
				}
				else{
					continue;
				}
				if (sourceInfo.getPath() != null && !sourceInfo.getPath().isEmpty()){
					Element sourcePath = doc.createElement("path");
					List<String> usedSignatures = new ArrayList<String>();
					for(Unit u : sourceInfo.getPath()){
						Element mEntry = doc.createElement("methodEntry");
						if(u instanceof InvokeStmt){
							InvokeExpr invokeExpr = ((InvokeStmt) u).getInvokeExpr();
							SootMethod invokeMethod = invokeExpr.getMethod();
							if(!usedSignatures.contains(u.toString())){
								getMethodInfo(doc, invokeMethod, mEntry, invokeExpr, null, cfg);
								sourcePath.appendChild(mEntry);
								usedSignatures.add(u.toString());
								Element callerClass = doc.createElement("callerClassName");
								callerClass.appendChild(doc.createTextNode(cfg.getMethodOf(u).getDeclaringClass().getName()));
								mEntry.appendChild(callerClass);
								Element callerMethod = doc.createElement("callerMethodName");
								callerMethod.appendChild(doc.createTextNode(cfg.getMethodOf(u).getName()));
								mEntry.appendChild(callerMethod);
							}
						}
						else if (u instanceof DefinitionStmt) {
							DefinitionStmt ds = (DefinitionStmt) u;
							if(ds.containsInvokeExpr()){
								InvokeExpr iexpr = ds.getInvokeExpr();
								SootMethod invokeMethod = iexpr.getMethod();
								if(!usedSignatures.contains(u.toString())){
									getMethodInfo(doc, invokeMethod, mEntry, iexpr, null, cfg);
									sourcePath.appendChild(mEntry);
									usedSignatures.add(u.toString());
									if(ds.getDefBoxes() != null && ds.getDefBoxes().size() > 0){
										Element defBoxes = doc.createElement("leftSides");
										for(int i=0; i< ds.getDefBoxes().size(); i++){
											Element defBox = doc.createElement("leftSide");
											ValueBox box = ds.getDefBoxes().get(i);
											Value value = box.getValue();
											defBox.appendChild(doc.createTextNode(value.toString()));
											defBoxes.appendChild(defBox);
										}
										mEntry.appendChild(defBoxes);
										Element callerClass = doc.createElement("callerClassName");
										callerClass.appendChild(doc.createTextNode(cfg.getMethodOf(u).getDeclaringClass().getName()));
										mEntry.appendChild(callerClass);
										Element callerMethod = doc.createElement("callerMethodName");
										callerMethod.appendChild(doc.createTextNode(cfg.getMethodOf(u).getName()));
										mEntry.appendChild(callerMethod);
									}
								}
							}
						}
					}
					//sourcePath.appendChild(doc.createTextNode(sourceInfo.getPath().toString()));
					source.appendChild(sourcePath);
				}
			}
			if(!saveFile){
				saveFile = sourcesMethod.hasChildNodes();
			}
			if(sourcesMethod.hasChildNodes()){
				elems.appendChild(sinkElem);
			}
		}
		if(saveFile){
			try {
				try {
					writeToFile(doc, appName);
				} catch (TransformerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			System.out.println("Nothing to save");
		}
	}
	private static void getMethodInfo(Document doc,	SootMethod method, Element source, InvokeExpr invokeExpr, Stmt context, BiDiInterproceduralCFG<Unit, SootMethod> cfg) {
		Element sourceMethod = doc.createElement("method");
		sourceMethod.appendChild(doc.createTextNode(method.getName()));
		source.appendChild(sourceMethod);
        Element methodSignature = doc.createElement("signature");
		methodSignature.appendChild(doc.createTextNode(method.getSignature()));
		source.appendChild(methodSignature);
        Element fullSignatureWithParams = doc.createElement("fullsignature");
		fullSignatureWithParams.appendChild(doc.createTextNode(invokeExpr.toString()));
		source.appendChild(fullSignatureWithParams);
		Element sourceClass = doc.createElement("class");
		sourceClass.appendChild(doc.createTextNode(method
				.getDeclaringClass().getName()));
		source.appendChild(sourceClass);
		if(context != null){
			Element callerClass = doc.createElement("callerClassName");
			callerClass.appendChild(doc.createTextNode(cfg.getMethodOf(context).getDeclaringClass().getName()));
			source.appendChild(callerClass);
			Element callerMethod = doc.createElement("callerMethodName");
			callerMethod.appendChild(doc.createTextNode(cfg.getMethodOf(context).getName()));
			source.appendChild(callerMethod);
		}
		for(int i=0; i< method.getParameterCount(); i++){
			Element sourceParamTypes = doc
					.createElement("parameterType");
			sourceParamTypes.appendChild(doc.createTextNode(method.getParameterType(i).toString()));
			source.appendChild(sourceParamTypes);
			if(invokeExpr != null && invokeExpr.getArgs() != null && invokeExpr.getArgs().size() != 0 && invokeExpr.getArgs().size() == method.getParameterCount()){
				Element sourceParamValues = doc.createElement("parameterValue");
				sourceParamValues.appendChild(doc.createTextNode(invokeExpr.getArg(i).toString()));
				source.appendChild(sourceParamValues);
			}
		}
		Element sourceReturnType = doc.createElement("returnType");
		sourceReturnType.appendChild(doc.createTextNode(method.getReturnType()
				.toString()));
		source.appendChild(sourceReturnType);
			if (context != null) {
			if(context.hasTag("LineNumberTag")){
				Element sinkLine = doc.createElement("line");
				sinkLine.appendChild(doc.createTextNode((Integer
						.toString(((LineNumberTag) context.getTag("LineNumberTag")).getLineNumber()))));
				source.appendChild(sinkLine);
			}
			if(context.getDefBoxes() != null && context.getDefBoxes().size() > 0 && !method.getReturnType().toString().equals("void")){
				Element defBoxes = doc.createElement("leftSides");
				for(int i=0; i< context.getDefBoxes().size(); i++){
					Element defBox = doc.createElement("leftSide");
					ValueBox box = context.getDefBoxes().get(i);
					Value value = box.getValue();
					defBox.appendChild(doc.createTextNode(value.toString()));
					defBoxes.appendChild(defBox);
				}
				source.appendChild(defBoxes);
			}
		}
	}
	private static void writeToFile(Document doc, String apkName) throws IOException,
			TransformerException {
		createLogDirIfNotExsist();
		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		String[] splittedLine;
		if(!isWindows){
			splittedLine = apkName.split("\\/");
		}
		else{
			splittedLine = apkName.split("\\\\");
		}
		String fileName = splittedLine[splittedLine.length - 1];
		
		StreamResult result = new StreamResult(new File(String.format("results"+File.separator+"%s_results.xml", fileName)));
		// Output to console for testing
		// StreamResult result = new StreamResult(System.out);
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(
				"{http://xml.apache.org/xslt}indent-amount", "2");
		transformer.transform(source, result);
		System.out.println("File saved!");
	}
	private static void createLogDirIfNotExsist(){
		File theDir = new File("results");
		if (!theDir.exists()) {
			boolean result = theDir.mkdir(); 
		     if(result) {    
		       System.out.println("Results will be saved to 'results' directory");  
		     }
		}
	}
}
