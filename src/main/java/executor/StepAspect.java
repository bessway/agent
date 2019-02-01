package executor;

import com.aventstack.extentreports.Status;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import utils.ReportUtils;
import utils.SeleniumUtils;
import utils.Utils;



@Aspect
public class StepAspect{
    @Pointcut("execution(public static String utils.SeleniumUtils.*Key(..))")
    public void pointcutName(){}

    @AfterThrowing(pointcut="pointcutName()", throwing="e")
    public void afterThrowing(Exception e){
        String screenshot=null;
        try{
            screenshot=SeleniumUtils.takeScreenshot();
            ReportUtils.addLog(Status.FAIL, e.getMessage()+"\n"+e.getCause(), screenshot);
        }catch(Exception e1){
            ReportUtils.addLog(Status.FAIL, e.getMessage()+"\n"+e.getCause()+"\nfailed to attach screenshot", screenshot);
        }
    }
 
    @AfterReturning(pointcut="pointcutName()", returning="result")
    public void afterReturning(Object result) {
        if(Utils.execFail.equals(String.valueOf(result))){
            String screenshot=null;
            try{
                screenshot=SeleniumUtils.takeScreenshot();
                ReportUtils.addLog(Status.FAIL, "step failed,response: "+String.valueOf(result), screenshot);
            }catch(Exception e){
                ReportUtils.addLog(Status.FAIL, "step failed but failed to get screenshot,response: "+String.valueOf(result), null);
            }
        }else{
            ReportUtils.addLog(Status.PASS, "step passed,response: "+String.valueOf(result), null);
        }
    }
    
}
