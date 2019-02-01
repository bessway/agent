package core.pojo;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
public class StepDataPojo{
    private Integer sIndex=null;
    private String response=null;
    private String target=null;
    private ArrayList<ParaPojo> stepParas=null;
    public static Comparator<StepDataPojo> sindexComp=new Comparator<StepDataPojo>() {
        @Override
        public int compare(StepDataPojo first, StepDataPojo second){
            return first.getsIndex()-second.getsIndex();
        }
    };
    public Integer getsIndex(){
        return this.sIndex;
    }
    public void setsIndex(Integer sIndex){
        this.sIndex=sIndex;
    }
    public String getResponse(){
        return this.response;
    }
    public void setResponse(String response){
        this.response=response;
    }
    public String getTarget(){
        return this.target;
    }
    public void setTarget(String target){
        this.target=target;
    }
    public void setStepParas(ArrayList<ParaPojo> stepParas){
        this.stepParas=stepParas;
    }
    public ArrayList<ParaPojo> getStepParas(){
        return this.stepParas;
    }
    public ArrayList<ParaPojo> getSortedStepParas(){
        Collections.sort(this.stepParas,ParaPojo.pindexComp);
        return this.stepParas;
    }
    public void addStepPara(Integer index,ParaPojo p){
        if(this.stepParas==null){
            this.stepParas=new ArrayList<ParaPojo>();
        }
        this.stepParas.add(index,p);
    }
    public String toString(){
        String ret="sIndex:"+String.valueOf(this.sIndex)
        +"\nresponse:"+this.response
        +"\ntarget:"+this.target;
        for(ParaPojo item:this.stepParas){
            ret=ret+"\nstepParas----"+item.toString();
        }
        return ret;
    }
}