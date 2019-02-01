package core.pojo;

import java.util.ArrayList;
import java.util.Comparator;

public class StepDetailPojo{
    private Integer id=null;
    private String action=null;
    private String page=null;
    private String type=null;
    private String name=null;
    private String path=null;
    private ArrayList<String> paras=null;
    private String response=null;
    public static Comparator<StepDetailPojo> sidComp=new Comparator<StepDetailPojo>() {
        @Override
        public int compare(StepDetailPojo first, StepDetailPojo second){
            return first.getId()-second.getId();            
        }
    };

    public void setid(Integer id){
        this.id=id;
    }
    public Integer getId(){
        return this.id;
    }
    public void setAction(String action){
        this.action=action;
    }
    public String getAction(){
        return this.action;
    }
    public void setPage(String page){
        this.page=page;
    }
    public String getPage(){
        return this.page;
    }
    public void setType(String type){
        this.type=type;
    }
    public String getType(){
        return this.type;
    }
    public void setName(String name){
        this.name=name;
    }
    public String getName(){
        return this.name;
    }
    public void setPath(String path){
        this.path=path;
    }
    public String getPath(){
        return this.path;
    }
    public void setResponse(String response){
        this.response=response;
    }
    public String getResponse(){
        return this.response;
    }
    public void setParas(ArrayList<String> paras){
        this.paras=paras;
    }
    public ArrayList<String> getParas(){
        return this.paras;
    }
    public void addPara(Integer index,String para){
        if(this.paras==null){
            this.paras=new ArrayList<String>();
        }
        this.paras.add(index,para);
    }
    public String getPara(Integer index){
        if(this.paras==null){
            return null;
        }
        return this.paras.get(index);
    }
    
}