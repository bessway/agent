package utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.Properties;
import java.util.Hashtable;
import java.util.Map;

import pojo.Action;

public class Utils{
    //id=action
    public static Hashtable<String, Action> cachedAction = null;
    //id=name@@xpath
    public static Map<String,String> cachedUiObj=null;
    public static String uiObjSeperator="@@";
    public static Map<String,String> cachedTestPara=null;
    public static String dataVersion = null;
    public static String logLevel = null;
    public enum ExecStatus{
        READYTOSTART,
        RUNNING,
        FAILED,
        SUCCESS,
        EXCEPTION,
        FAILEDTOSTART,
        FORCESTOP,
    }

    public static Properties readPropery(String fileName) throws Exception{
        String path=Utils.class.getResource("/").toURI().getRawPath();
        path=URLDecoder.decode(path, "UTF-8");
        InputStream io=new BufferedInputStream(new FileInputStream(new File("/"+path+fileName)));
        Properties result= new Properties();
        result.load(io);
        return result;
    }
}