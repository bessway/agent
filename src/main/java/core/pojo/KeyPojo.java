package core.pojo;

import java.util.ArrayList;




public class KeyPojo{
    private String name=null;
    private String regFunc=null;
    private Boolean target=null;
    private Boolean response=null;
    private ArrayList<ParaPojo> paras=null;

    public String getName(){
        return this.name;
    }
    public String getRegFunc(){
        return this.regFunc;
    }
    public Boolean getTarget(){
        return this.target;
    }
    public Boolean getResponse(){
        return this.response;
    }
    public ArrayList<ParaPojo> getParas(){
        return this.paras;
    }
    public String toString(){
        String ret="regFunc:"+this.regFunc
        +"\ntarget:"+String.valueOf(this.target)
        +"\nresponse:"+String.valueOf(this.response);
        for(ParaPojo item:this.paras){
            ret=ret+"\nPara----"+item.toString();
        }
        return ret;
    }
}