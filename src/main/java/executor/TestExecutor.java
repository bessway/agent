package executor;

import pojo.Para;
import pojo.Step;
import pojo.Test;
import org.apache.log4j.Logger;
import utils.Utils;
import java.util.Map;
import pojo.Executable;

public class TestExecutor implements Executor{
    private Test test=null;
    private static Logger logger = Logger.getLogger(TestExecutor.class);
    //paraId@stepId=para
    private Map<String, Para> data=null;

    public TestExecutor(){
        
    }
    public TestExecutor(Executable test,Map<String, Para> data){
        this.test=(Test)test;
        this.data=data;
    }
    
    public String execute() throws Exception{
        logger.debug("test start "+String.valueOf(this.test.getIndex())+'-'+this.test.getTestDesc());
        Utils.ExecStatus testResult = Utils.ExecStatus.SUCCESS;
        for(int i=0;i<this.test.getSteps().size();i++){
            Step step=this.test.getSteps().get(i);
            String result = this.getSuccessor(step, this.data).execute();
            //有一步失败则整体是失败状态，停止当前case的执行
            if (result.equals(Utils.ExecStatus.FAILED.name())) {
                testResult = Utils.ExecStatus.FAILED;
                break;
            }
        }
        return testResult.name();
    }
    public Executor getSuccessor(Executable test,Map<String, Para> data){
        Step step=(Step)test;
        Executor result=null;
        //0:ui, 1:api, 2:ref, ref step 没有action
        if(step.getStepType().equals(0)){
            result=new UIStepExecutor(step, data);
        }else if(step.getStepType().equals(1)){
        }else{
            result=new RefStepExecutor(step, data);
        }
        return result;
    }
}