package core.pojo;

public class ActionPara{
    private String paraDesc = null;
    private Integer paraId = null;

    public void setParaDesc(String paraDesc){
        this.paraDesc=paraDesc;
    }
    public String getParaDesc(){
        return this.paraDesc;
    }

    public void setParaId(Integer paraId){
        this.paraId=paraId;
    }
    public Integer getParaId(){
        return this.paraId;
    }
}