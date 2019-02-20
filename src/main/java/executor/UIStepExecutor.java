package executor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.apache.log4j.Logger;
import com.aventstack.extentreports.Status;

import pojo.Executable;
import pojo.Para;
import pojo.Uiobject;
import utils.ReportUtils;
import utils.SeleniumUtils;
import utils.ServerUtils;
import utils.Utils;

public class UIStepExecutor extends StepExecutor{
    private static String keySuffix="Key";
    private static Logger logger=Logger.getLogger(UIStepExecutor.class);
    private String uitarget = null;
    private String xpath =null;
    
    public UIStepExecutor(Executable step,Map<String, Para> data){
        super(step, data);
    }

    @Override
    public String execute() throws Exception{
        logger.debug("start ui step");
        action=Utils.cachedAction.get(this.step.getActionId());
        this.getXpath();
        this.getParas();

        String funcName = action.getRegFunc();
        funcName = funcName + keySuffix;
        List<String> mParaValue=new ArrayList<String>();
        //如果有xpath，必须是第一个参数
        if(action.getHasUIObject().equals(1)){
            mParaValue.add(0,this.xpath);
        }
        if(this.paraValues!=null){
            mParaValue.addAll(this.paraValues);
        }
        String reportContent = "Step "+String.valueOf(this.step.getIndex()+1)+"-"+this.step.getStepDesc();
        reportContent = reportContent + funcName+": "+this.paraToString(mParaValue);
        if(action.getHasUIObject().equals(1)){
            reportContent = reportContent + " " + this.uitarget.split(Utils.uiObjSeperator)[0];
        }
        logger.debug(reportContent);
        ReportUtils.addLog(Status.INFO,reportContent, null);
        String result= Utils.ExecStatus.SUCCESS.name();
        result = this.executeKey(funcName, mParaValue);
        //System.out.println(funcName);
        // for(int i=0;i<mParaValue.size();i++){
        //     System.out.println(mParaValue.get(i));
        // }
        return result;
    }
    private String executeKey(String funcName, List<String> mParaValue){
        Integer paraCount=mParaValue.size();
        Class<String>[] mPara=new Class[paraCount];
        for(int i=0;i<paraCount;i++){
            mPara[i]=String.class;
        }
        try{
            Method toExe=SeleniumUtils.class.getMethod(funcName, mPara);
        
            Object result=toExe.invoke(null, mParaValue.toArray());
            //设置返回值,返回值参数仅仅用于保存执行的值
            if(action.getHasResponse().equals(1)){
                String paraId=String.valueOf(this.step.getResParaId());
                this.data.get(paraId).setParaValue(String.valueOf(result));
            }
        }catch(NoSuchMethodException e){
            logger.debug("cannot find the method "+funcName);
            return Utils.ExecStatus.FAILED.name();
        }catch(Exception e){
            logger.debug("excute method "+funcName+" failed",e);
            ReportUtils.addLog(Status.ERROR, "exception " +e.getMessage(), null);
            return Utils.ExecStatus.FAILED.name();
        }
    
        return Utils.ExecStatus.SUCCESS.name();
    }
    private void getXpath() throws Exception{
        if(action.getHasUIObject().equals(1)){
            //如果uiobject没有读取到，按page load所有uiobject
            if(Utils.cachedUiObj==null || !Utils.cachedUiObj.containsKey(this.step.getUiObjectId())){
                this.cacheUiObjByPage(this.step.getUiObjectId());
            }
            this.uitarget=Utils.cachedUiObj.get(this.step.getUiObjectId());
            this.xpath=this.paraHelper.unpackPara(this.uitarget.split(Utils.uiObjSeperator)[1]);
        }
    }
    
    private void cacheUiObjByPage(String objId) throws Exception{
        List<Uiobject> result = ServerUtils.getUiObjectsByPage(objId);
        if(Utils.cachedUiObj==null){
            Utils.cachedUiObj = new HashMap<String, String>();
        }
        for(Uiobject item: result){
            String value = item.getUiObjectPage() + "." + item.getUiObjectType() + "." + item.getUiObjectName() 
                            + Utils.uiObjSeperator + item.getUiObjectPath();
            Utils.cachedUiObj.put(item.getUiObjectId(), value);
        }
    }
}