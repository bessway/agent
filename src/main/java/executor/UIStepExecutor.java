package executor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    
    public UIStepExecutor(Executable step,Map<String, Para> data){
        super(step, data);
    }

    @Override
    public String execute() throws Exception{
        ReportUtils.addLog(Status.INFO,"Step index: "+String.valueOf(this.step.getIndex()), null);
        action=Utils.cachedAction.get(this.step.getActionId());
        this.getXpath();
        this.getParas();

        String funcName=action.getRegFunc();
            funcName=funcName+keySuffix;
            Integer paraCount=this.step.getParas().size();
            if(action.getHasUIObject().equals(1)){
                paraCount=paraCount+1;
            }
            Class<String>[] mPara=new Class[paraCount];
            for(int i=0;i<mPara.length;i++){
                mPara[i]=String.class;
            }
            Object result=null;
            try{
                Method toExe=SeleniumUtils.class.getMethod(funcName, mPara);
                List<String> mParaValue=new ArrayList<String>();
                //如果有xpath，必须是第一个参数
                if(action.getHasUIObject().equals(1)){
                    mParaValue.add(0,uitarget);
                }
                mParaValue.addAll(paraValues);

                ReportUtils.addLog(Status.INFO, funcName+this.paraToString(mParaValue)+" "+uitarget.split(Utils.uiObjSeperator)[0], null);
            
                result=toExe.invoke(null, mParaValue.toArray());
                //设置返回值,返回值参数仅仅用于保存执行的值
                if(action.getHasResponse().equals(1)){
                    String paraId=String.valueOf(this.step.getResParaId());
                    this.data.get(paraId).setParaValue(String.valueOf(result));
                }
            }catch(NoSuchMethodException e){
                logger.debug("cannot find the method "+funcName);
                return Utils.ExecStatus.FAILED.name();
            }catch(Exception e){
                logger.debug("excute method "+funcName+" failed");
                return Utils.ExecStatus.FAILED.name();
            }
        
            return Utils.ExecStatus.SUCCESS.name();
    }
    private void getXpath() throws Exception{
        if(action.getHasUIObject().equals(1)){
            //如果uiobject没有读取到，按page load所有uiobject
            if(Utils.cachedUiObj==null || !Utils.cachedUiObj.containsKey(this.step.getUiObjectId())){
                Uiobject uiObj = ServerUtils.getUiObjectById(this.step.getUiObjectId());
                this.cacheUiObjByPage(uiObj.getUiObjectPage());
            }
            uitarget=Utils.cachedUiObj.get(this.step.getUiObjectId());
            uitarget=this.paraHelper.unpackPara(uitarget.split(Utils.uiObjSeperator)[1]);
        }
    }
    
    private void cacheUiObjByPage(String page) throws Exception{
        List<Uiobject> result = ServerUtils.getUiObjectsByPage(page);
        for(Uiobject item: result){
            String value = item.getUiObjectPage() + "." + item.getUiObjectType() + "." + item.getUiObjectName() 
                            + Utils.uiObjSeperator + item.getUiObjectPath();
            Utils.cachedUiObj.put(item.getUiObjectId(), value);
        }
    }
}