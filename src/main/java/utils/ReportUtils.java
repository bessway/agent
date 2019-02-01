package utils;

import java.io.File;
import java.util.Date;
import java.util.LinkedList;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.ResourceCDN;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.ChartLocation;
import com.aventstack.extentreports.reporter.configuration.Protocol;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ReportUtils{
    private static LinkedList<ExtentTest> tests=new LinkedList<ExtentTest>();
    private static ExtentTest currTest=null;
    private static ExtentReports extent=null;
    public static String reportRoot="report-output/";

    public static void init(String desc){
        File reportDir= new File(reportRoot);
        if(!reportDir.exists()&& !reportDir .isDirectory()){
            reportDir.mkdir();
        }
        ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter(reportRoot+desc+".html");
        htmlReporter.config().setTestViewChartLocation(ChartLocation.TOP);
        htmlReporter.config().setChartVisibilityOnOpen(true);
        htmlReporter.config().setTheme(Theme.STANDARD);
        htmlReporter.config().setEncoding("UTF-8");
        htmlReporter.config().setDocumentTitle("report");
        htmlReporter.config().setReportName(desc);
        htmlReporter.config().setTimeStampFormat("MM-dd hh:mm:ss");
        htmlReporter.config().setResourceCDN(ResourceCDN.EXTENTREPORTS);
        htmlReporter.config().setProtocol(Protocol.HTTP);
        
        extent = new ExtentReports();
        extent.attachReporter(htmlReporter);
    }
    public static void generateReport(){
        extent.flush();
    }
    public static void addSubTest(String desc){
        if(currTest==null){
            currTest=extent.createTest(desc);
        }else{
            currTest.createNode(desc);
        }
        tests.addLast(currTest);
    }

    public static void completeTestReport(){
        tests.removeLast();
        if(tests.size()==0){
            currTest=null;
        }else{
            currTest=tests.getLast();
        }
    }
    public static void addLog(Status status,String detail,String path){
        if(path!=null){
            try{
                currTest.log(status,detail).addScreenCaptureFromPath(path);
            }catch(Exception e){
                currTest.log(status,detail+"(failed to attach screenshot at "+path+")");
            }
        }else{
            currTest.log(status,detail);
        }
    }
    public static void addStartTime(Date start){
        currTest.getModel().setStartTime(start);
    }
    public static void addEndTime(Date start){
        currTest.getModel().setEndTime(start);
    }
}