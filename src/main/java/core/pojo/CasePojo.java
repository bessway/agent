package core.pojo;

import java.util.ArrayList;
import java.util.Collections;


public class CasePojo{
    private String caseId=null;
    private String desc=null;
    private ArrayList<StepPojo> steps=null;
    public String getDesc(){
        return this.desc;
    }
    public void setDesc(String desc){
        this.desc=desc;
    }
    public String getCaseId(){
        return this.caseId;
    }
    public ArrayList<StepPojo> getSteps(){
        return this.steps;
    }
    public ArrayList<StepPojo> getSortedSteps(){
        Collections.sort(this.steps,StepPojo.indexComp);
        return this.steps;
    }
    public String toString(){
        String ret="caseId:"+this.caseId;
        for(StepPojo item:steps){
            ret=ret+"\nstep----"+item.toString();
        }
        return ret;
    }
}