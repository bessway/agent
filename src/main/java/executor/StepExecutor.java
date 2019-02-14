package executor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aventstack.extentreports.Status;

import org.apache.log4j.Logger;

import pojo.Action;
import pojo.Para;
import pojo.Step;
import pojo.Test;
import pojo.Uiobject;
import pojo.Executable;
import utils.ReportUtils;
import utils.SeleniumUtils;
import utils.ServerUtils;
import utils.Utils;

public class StepExecutor implements Executor{
    private Step step=null;
    private Executor successor=null;
    private Map<String, Para> data=null;
    private static String keySuffix="Key";
    private static Logger logger=Logger.getLogger(StepExecutor.class);

    public StepExecutor(Executable step,Map<String, Para> data){
        this.step=(Step)step;
        this.data=data;
    }
    
    public String execute() throws Exception{
        ReportUtils.addLog(Status.INFO,"Step index: "+String.valueOf(this.step.getIndex()), null);
        //0:ui, 1:api, 2:ref, ref step 没有action
        if(this.step.getStepType().equals(0)){
            Action action=Utils.cachedAction.get(this.step.getActionId());
            String uitarget=null;
            List<String> paraValues=null;
            if(action.getHasUIObject().equals(1)){
                //如果uiobject没有读取到，按page load所有uiobject
                if(Utils.cachedUiObj==null || !Utils.cachedUiObj.containsKey(this.step.getUiObjectId())){
                    Uiobject uiObj = ServerUtils.getUiObjectById(this.step.getUiObjectId());
                    this.cacheUiObjByPage(uiObj.getUiObjectPage());
                }
                uitarget=Utils.cachedUiObj.get(this.step.getUiObjectId());
                uitarget=this.unpackPara(uitarget.split(Utils.uiObjSeperator)[1]);
            }
            if(action.getActionParas()!=null && action.getActionParas().size()>1){
                paraValues=new ArrayList<String>();
                for(int i=0;i<action.getActionParas().size();i++){
                    for(Para item:this.data){
                        if(item.getParaId().equals(this.step.getParas().get(i))){
                            paraValues.add(this.unpackPara(item.getParaValue()));
                            break;
                        }
                    }
                }
            }
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
                //Method toExe=SeleniumUtils.class.getMethod(funcName, String.class, String.class, String.class);
                List<String> mParaValue=new ArrayList<String>();
                if(action.getHasUIObject().equals(1)){
                    mParaValue.add(0,uitarget);
                }
                mParaValue.addAll(paraValues);

                ReportUtils.addLog(Status.INFO, funcName+this.paraToString(mParaValue)+" "+uitarget.split(Utils.uiObjSeperator)[0], null);
            
                result=toExe.invoke(null, mParaValue.toArray());
                if(action.getHasResponse().equals(1)){
                    for(Para item:this.data){
                        if(item.getParaId().equals(this.step.getResParaId())){
                            item.setParaValue(String.valueOf(result));
                            break;
                        }
                    }
                }
            }catch(NoSuchMethodException e){
                logger.debug("cannot find the method "+funcName);
                return Utils.ExecStatus.FAILED.name();
            }catch(Exception e){
                logger.debug("excute method "+funcName+" failed");
                return Utils.ExecStatus.FAILED.name();
            }
        }else if(this.step.getStepType().equals(1)){
            Action action=Utils.cachedAction.get(this.step.getActionId());
            //TODO
        }else{
            Test reftest=ServerUtils.getRefTestDetail(this.step.getRefTestId());
            List<Para> refTestParas=ServerUtils.getTestParas(reftest.getTestId(), Utils.dataVersion);
            List<Para> refTestFormalParas=ServerUtils.getRefTestParas(this.data.get(0).getTestId(), this.step.getIndex(), Utils.dataVersion);
            for(Para formalPara:refTestFormalParas){
                for(Para oriPara:refTestParas){
                    if(oriPara.getParaId().equals(formalPara.getParaId())){
                        oriPara.setParaValue(formalPara.getParaValue());
                        break;
                    }
                }
            }
            return new TestExecutor(reftest, refTestParas).execute();
        }
        return Utils.ExecStatus.SUCCESS.name();
    }

    public Executor getSuccessor(Executable test, List<Para> data){
        return null;
    }
    private void cacheUiObjByPage(String page) throws Exception{
        List<Uiobject> result = ServerUtils.getUiObjectsByPage(page);
        for(Uiobject item: result){
            String value = item.getUiObjectPage() + "." + item.getUiObjectType() + "." + item.getUiObjectName() 
                            + Utils.uiObjSeperator + item.getUiObjectPath();
            Utils.cachedUiObj.put(item.getUiObjectId(), value);
        }
    }
    private String replacePara(String paraValue){
        Pattern p=Pattern.compile("\\{[\\s\\S]+\\}");
        Matcher m=p.matcher(paraValue);
        while(m.find()){
            String name=m.group();
            for(Para item:this.data){
                if(name.equals(item.getParaName())){
                    paraValue= paraValue.replace(name, item.getParaValue());
                    break;
                }
            }
        }
        return paraValue;
    }
    private String unpackPara(String paraValue){
        paraValue=this.replacePara(paraValue);
        paraValue=this.eval(paraValue);
        return paraValue;
    }
    private String eval(String paraValue){
        Pattern p=Pattern.compile("\\{[\\s\\S]+\\}");
        Matcher m=p.matcher(paraValue);
        while(m.find()){
            String name=m.group();
            for(Para item:this.data){
                if(name.equals(item.getParaName())){
                    String value=item.getParaValue();
                    paraValue=this.unpackPara(value);
                    break;
                }
            }
        }
        return paraValue;
    }
    private String paraToString(List<String> paras){
        String result="";
        for(String item:paras){
            result=result+", "+item;
        }
        return result;
    }
}