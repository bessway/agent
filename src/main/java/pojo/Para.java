package pojo;

public class Para{
    private String testId = null;
    private Long paraId = null;
    private String paraName = null;
    private String paraValue = null;
    private String refTestId = null;
    private Integer isFormalPara = null;
    private Integer stepId = null;

    public Para(){}

    public void setTestId(String testId){
        this.testId=testId;
    }
    public String getTestId(){
        return this.testId;
    }

    public void setParaId(Long paraId){
        this.paraId=paraId;
    }
    public Long getParaId(){
        return this.paraId;
    }

    public void setParaName(String paraName){
        this.paraName=paraName;
    }
    public String getParaName(){
        return this.paraName;
    }

    public void setParaValue(String paraValue){
        this.paraValue=paraValue;
    }
    public String getParaValue(){
        return this.paraValue;
    }

    public void setRefTestId(String refTestId){
        this.refTestId=refTestId;
    }
    public String getRefTestId(){
        return this.refTestId;
    }

    public void setIsFormalPara(Integer isFormalPara){
        this.isFormalPara=isFormalPara;
    }
    public Integer getIsFormalPara(){
        return this.isFormalPara;
    }

    public void setStepId(Integer stepId){
        this.stepId=stepId;
    }
    public Integer getStepId(){
        return this.stepId;
    }
    public Para(Long paraId, Integer stepId, String refTestId){
        this.paraId=paraId;
        /*只有引用的步骤需要记录stepId*/
        if(refTestId!=null && !"".equals(refTestId)){
            this.stepId=stepId;
            this.refTestId=refTestId;
        }
    }
}