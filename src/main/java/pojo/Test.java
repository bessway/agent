package pojo;

import java.util.List;

public class Test implements Executable{
    private Long index = null;
    private String testId = null;
    private String testDesc = null;
    private String parentId = null;
    private List<Step> steps = null;
    private Integer status = null;
    private Integer isRefered = null;

    public void setIndex(Long index){
        this.index=index;
    }
    public Long getIndex(){
        return this.index;
    }

    public void setTestId(String testId){
        this.testId=testId;
    }
    public String getTestId(){
        return this.testId;
    }

    public void setTestDesc(String testDesc){
        this.testDesc=testDesc;
    }
    public String getTestDesc(){
        return this.testDesc;
    }

    public void setParentId(String parentId){
        this.parentId=parentId;
    }
    public String getParentId(){
        return this.parentId;
    }

    public void setSteps(List<Step> steps){
        this.steps=steps;
    }
    public List<Step> getSteps(){
        return this.steps;
    }

    public void setStatus(Integer status){
        this.status=status;
    }
    public Integer getStatus(){
        return this.status;
    }

    public void setIsRefered(Integer isRefered){
        this.isRefered=isRefered;
    }
    public Integer getIsRefered(){
        return this.isRefered;
    }
}