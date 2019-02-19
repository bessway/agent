package executor;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import com.aventstack.extentreports.Status;

import pojo.Executable;
import pojo.Para;
import pojo.Test;
import utils.ReportUtils;
import utils.ServerUtils;
import utils.Utils;

public class RefStepExecutor extends StepExecutor{
    private static Logger logger=Logger.getLogger(RefStepExecutor.class);
    
    public RefStepExecutor(Executable step,Map<String, Para> data){
        super(step, data);
    }
    @Override
    public String execute() throws Exception{
        ReportUtils.addSubTest("Step index: "+String.valueOf(this.step.getIndex()+1)+"-"+this.step.getStepDesc());
        logger.debug("start refered step "+String.valueOf(this.step.getIndex()+1)+"-"+this.step.getStepDesc());

        Test reftest=ServerUtils.getRefTestDetail(this.step.getRefTestId());
        Map<String, Para> refTestParas=this.getTestParasAll(reftest.getTestId());
        this.replaceParaOfRefTest(refTestParas);
        ReportUtils.completeTestReport();
        return this.getSuccessor(reftest, refTestParas).execute();
    }

    @Override
    public Executor getSuccessor(Executable test,Map<String, Para> data){
        return new TestExecutor(test, data);
    }
    //替换reftest中的形参的值
    private void replaceParaOfRefTest(Map<String, Para> paraOfRefTest) throws Exception{
        String stepId=String.valueOf(this.step.getIndex());
        for(Para item:paraOfRefTest.values()){
            if(item.getIsFormalPara().equals(1)){
                String key=String.valueOf(item.getParaId())+Utils.paraSeperator+stepId;
                String paraValue = this.data.get(key).getParaValue();
                paraValue=this.paraHelper.unpackPara(paraValue);
                item.setParaValue(paraValue);
            }
        }
    }
    private Map<String, Para> getTestParasAll(String testId) throws Exception{
        List<Para> paras = ServerUtils.getTestParasAll(testId, Utils.dataVersion);
        Map<String, Para> result=new Hashtable<String, Para>();
        for(Para item:paras){
            //如果是refPara，跟步骤有关系
            if(item.getRefTestId()!=null){
                result.put(String.valueOf(item.getParaId())+"@"+String.valueOf(item.getStepId()), item);
            }else{
                result.put(String.valueOf(item.getParaId()), item);
            }
        }
        return result;
    }
}