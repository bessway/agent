package core.pojo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


public class CaseDataPojo{
    private String caseId=null;
    private String version=null;
    private HashMap<String,String> sharedParas=null;
    private ArrayList<StepDataPojo> stepsData=null;
    
    public String getCaseId(){
        return this.caseId;
    }
    public void setCaseId(String caseId){
        this.caseId=caseId;
    }
    public String getVersion(){
        return this.version;
    }
    public void setVersion(String version){
        this.version=version;
    }
    public HashMap<String,String> getSharedParas(){
        return this.sharedParas;
    }
    public void setSharedParas(HashMap<String,String> sharedParas){
        this.sharedParas=sharedParas;
    }
    public ArrayList<StepDataPojo> getStepsData(){
        return this.stepsData;
    }
    public void setStepsData(ArrayList<StepDataPojo> stepsData){
        this.stepsData=stepsData;
    }
    public void addStepsData(Integer index,StepDataPojo data){
        if(this.stepsData==null){
            this.stepsData=new ArrayList<StepDataPojo>();
        }
        this.stepsData.add(index,data);
    }
    public ArrayList<StepDataPojo> getSortedStepsData(){
        Collections.sort(this.stepsData,StepDataPojo.sindexComp);
        return this.stepsData;
    }
    public void addSharedParas(String key,String value){
        if(this.sharedParas==null){
            this.sharedParas=new HashMap<String,String>();
        }
        this.sharedParas.put(key,value);
    }
    public void appendSharedParas(Map<String,String> para){
        if(this.sharedParas==null){
            this.sharedParas=new HashMap<String,String>();
        }
        this.sharedParas.putAll(para);
    }
    public String toString(){
        String ret="caseId:"+this.caseId
        +"\nversion:"+this.version;
        for(Entry<String,String> item:this.sharedParas.entrySet()){
            ret=ret+"\nsharedparas----"+item.getKey()+"="+item.getValue();
        }
        for(StepDataPojo item:this.stepsData){
            ret=ret+"\nstepsdata----"+item.toString();
        }
        return ret;
    }
}