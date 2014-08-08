package src;
import java.io.*;
/**
 * Created by a0r262s on 13.05.2014.
 */
public class MyJarSignerMSC {
    public void signAndAlign(File apkFile) throws IOException {
        
        String pathOfapkFile = null;
        for (File file: apkFile.listFiles())
            if (!file.isDirectory()) {
                pathOfapkFile = file.getAbsolutePath();
                System.out.println("PATH of APK to be signed=   " + pathOfapkFile);
            }
        String jarSignerPath = ArgsParserMSC.jarSignPath;
        try {
            ProcessBuilder pb = new ProcessBuilder(jarSignerPath,
                    "-verbose",
                    "-keystore", "mine-release-key.keystore",
                    "-storepass", "12341234", "-keypass", "20152015",
                    "-sigalg" ,"SHA1withRSA", "-digestalg", "SHA1",
                    pathOfapkFile,
                    "my_alias_name");
            pb.directory(new File(getWorkingPath()));
            Process p = pb.start();
            // any error message?
            StreamGobblerMSC errorGobbler = new
                    StreamGobblerMSC(p.getErrorStream(), "ERROR");
            // any output?
            StreamGobblerMSC outputGobbler = new
                    StreamGobblerMSC(p.getInputStream(), "OUTPUT");
            // kick them off
            errorGobbler.start();
            outputGobbler.start();
            int exitVal = p.waitFor();
            System.out.println("ExitValue: " + exitVal);

        } catch (Throwable exc) {
            System.out.println("exc");
            exc.printStackTrace();
        }
    }
    public String getWorkingPath() {
        File directory = new File(".");
        String path = null;
        try {
            path = directory.getCanonicalPath();
            System.out.println("Path:       "+path);
        } catch (Exception e) {
            System.out.println("Exceptione is =" + e.getMessage());
        }
        return path;
       }
}

