package executor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import pojo.Action;
import pojo.Para;
import pojo.Step;
import pojo.Executable;

public class StepExecutor implements Executor{
    protected Step step=null;
    protected Map<String, Para> data=null;
    protected Action action = null;
    private static Logger logger=Logger.getLogger(StepExecutor.class);
    protected ParaHelper paraHelper=null;
    protected List<String> paraValues=null;

    public StepExecutor(){}

    public StepExecutor(Executable step,Map<String, Para> data){
        this.step=(Step)step;
        this.data=data;
        paraHelper=new ParaHelper(data);
    }
    
    public String execute() throws Exception{
        return null;
    }

    public Executor getSuccessor(Executable step, Map<String, Para> data){
        return null;
    }
    protected String paraToString(List<String> paras){
        String result="";
        for(String item:paras){
            result=result+", "+item;
        }
        return result;
    }
    protected void getParas() throws Exception{
        if(action.getActionParas()!=null && action.getActionParas().size()>0){
            paraValues=new ArrayList<String>();
            for(int i=0;i<action.getActionParas().size();i++){
                String paraId=String.valueOf(this.step.getParas().get(i));
                if("".equals(paraId)||paraId==null){
                    paraValues.add("");
                }else{
                    paraValues.add(this.paraHelper.unpackPara(this.data.get(paraId).getParaValue()));
                }
            }
        }
    }
}