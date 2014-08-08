package src;

/**
 * Created by a0r262s on 04.06.2014.
 */

import java.util.*;
import java.io.*;

class StreamGobblerMSC extends Thread {
    InputStream is;
    String type;

    StreamGobblerMSC(InputStream is, String type) {
        this.is = is;
        this.type = type;
    }

    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null)
                System.out.println(type + ">  " + line);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

}
