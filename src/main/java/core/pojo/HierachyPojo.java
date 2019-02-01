package core.pojo;


public class HierachyPojo{
    private String refId=null;
    private String label=null;
    private String parentId=null;

    public String toString(){
        return "refId:"+this.refId
        +"\nlabel:"+this.label
        +"\nparentId:"+this.parentId;
    }
    public void setLabel(String label){
        this.label=label;
    }
    public String getLabel(){
        return this.label;
    }
    public void setRefId(String refId){
        this.refId=refId;
    }
    public void setParentId(String parentId){
        this.parentId=parentId;
    }
    public String getRefId(){
        return this.refId;
    }
    public String getParentId(){
        return this.parentId;
    }
}