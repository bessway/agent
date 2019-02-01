package utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.Properties;

public class Utils{
    public enum ExecStatus{
        READYTOSTART,
        RUNNING,
        FAILED,
        SUCCESS,
        EXCEPTION,
        FAILEDTOSTART,
        FORCESTOP,
    }
    public final static String execPass="exec pass";
    public final static String execFail="exec fail";
    public final static String execException="exec stopped";
    public final static String gParaSymbol="%%";
    public final static String sParaSymbol="@@";

    public static Properties readPropery(String fileName) throws Exception{
        String path=Utils.class.getResource("/").toURI().getRawPath();
        path=URLDecoder.decode(path, "UTF-8");
        InputStream io=new BufferedInputStream(new FileInputStream(new File("/"+path+fileName)));
        Properties result= new Properties();
        result.load(io);
        return result;
    }
}