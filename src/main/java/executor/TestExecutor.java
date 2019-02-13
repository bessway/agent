package executor;

import pojo.Para;
import pojo.Step;
import pojo.Test;

import utils.Utils;
import java.util.List;
import pojo.Executable;

public class TestExecutor implements Executor{
    private Test test=null;
    private Executor successor=null;
    private List<Para> data=null;

    public TestExecutor(){
        
    }
    public TestExecutor(Executable test,List<Para> data){
        this.test=(Test)test;
        this.data=data;
    }
    
    public String execute() throws Exception{
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
    public Executor getSuccessor(Executable test,List<Para> data){
        this.successor=new StepExecutor(test, data);
        return this.successor;
    }
}