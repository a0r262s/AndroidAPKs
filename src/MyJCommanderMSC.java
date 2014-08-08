package src;
import com.beust.jcommander.Parameter;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by a0r262s on 18.05.2014.
 */
public class MyJCommanderMSC {
        @Parameter
        private List<String> parameters = new ArrayList<String>();
        @Parameter(names = "-file", description = "The input APK file name",
                required = true,
                echoInput = true)
        protected String  fileName;
    @Parameter(names = "-jsigner", description = "Jar sihner path",
            required = true,
            echoInput = true)
    protected String  jarSigner;
    @Parameter(names = "-androidjar", description = "The input Android jar file name",
            required = true,
            echoInput = true)
    protected String  androidJar;
    @Parameter(names = "-outputdir", description = "The input Android jar file name",
            required = true,
            echoInput = true)
    protected String  outputdir;
    @Parameter(names = "-jimple", description = "if is set true, it produces jimple files"
                , required = true,
                arity = 1,
                echoInput = true)
        protected boolean jimple=true ;


}
