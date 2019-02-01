package core.pojo;

import java.util.Date;
import java.util.HashMap;

import utils.Utils;


public class BuildPojo{
    private String jobName=null;
    private Integer buildId=null;
    private Utils.ExecStatus buildStatus=null;
    private HashMap<String,String> paras=null;
    private Date startTime=null;
    private Date endTime=null;
    private HashMap<String,Utils.ExecStatus> cases=null;
    private Boolean forceStop=null;
    private Date createTime=null;
    private Integer passed=null;
    private Integer failed=null;
    private String logLevel=null;

    public void setLogLevel(String logLevel){
        this.logLevel=logLevel;
    }
    public String getLogLevel(){
        return this.logLevel;
    }
    public void setPassed(Integer passed){
        this.passed=passed;
    }
    public void setFailed(Integer failed){
        this.failed=failed;
    }
    public Integer getPassed(){
        return this.passed;
    }
    public Integer getFailed(){
        return this.failed;
    }
    public void setCreateTime(Date createTime){
        this.createTime=createTime;
    }
    public Date getCreateTime(){
        return this.createTime;
    }
    public void setFroceStop(Boolean forceStop){
        this.forceStop=forceStop;
    }
    public Boolean isFroceStop(){
        return this.forceStop;
    }
    public Date getStartTime(){
        return this.startTime;
    }
    public void setStartTime(Date startTime){
        this.startTime=startTime;
    }
    public Date getEndTime(){
        return this.endTime;
    }
    public void setEndTime(Date endTime){
        this.endTime=endTime;
    }
    public HashMap<String,Utils.ExecStatus> getCases(){
        return this.cases;
    }
    public void setCases(HashMap<String,Utils.ExecStatus> cases){
        this.cases=cases;
    }
    public String getJobName(){
        return this.jobName;
    }
    public Integer getBuildId(){
        return this.buildId;
    }
    public Utils.ExecStatus getBuildStatus(){
        return this.buildStatus;
    }
    public void setJobName(String jobName){
        this.jobName=jobName;
    }
    public void setBuildId(Integer buildId){
        this.buildId=buildId;
    }
    public void setBuildStatus(Utils.ExecStatus buildStatus){
        this.buildStatus=buildStatus;
    }
    public HashMap<String,String> getParas(){
        return this.paras;
    }
    public void setParas(HashMap<String,String> paras){
        this.paras=paras;
    }
    public void addPara(String key,String value){
        if(this.paras==null){
            this.paras=new HashMap<String,String>();
        }
        this.paras.put(key,value);
    }
    public void addCase(String key,Utils.ExecStatus value){
        if(cases==null){
            this.cases=new HashMap<String,Utils.ExecStatus>();
        }
        this.cases.put(key,value);
    }
}