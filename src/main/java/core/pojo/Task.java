package core.pojo;

import java.util.Date;
import java.util.Map;
import java.util.HashMap;

import utils.Utils;

public class Task{
    private String jenkinsJobName=null;
    private Integer jenkinsBuildId=null;
    private Utils.ExecStatus taskStatus=null;
    private Date startTime=null;
    private Date endTime=null;
    private Map<String,Utils.ExecStatus> tests=null;
    private Integer forceStop=null;
    private Date createTime=null;
    private Integer passedCnt=null;
    private Integer failedCnt=null;
    private String logLevel=null;
    private String dataVersion=null;
    private String env=null;

    public void setLogLevel(String logLevel){
        this.logLevel=logLevel;
    }
    public String getLogLevel(){
        return this.logLevel;
    }
    public void setPassedCnt(Integer passedCnt){
        this.passedCnt=passedCnt;
    }
    public void setFailedCnt(Integer failedCnt){
        this.failedCnt=failedCnt;
    }
    public Integer getPassedCnt(){
        return this.passedCnt;
    }
    public Integer getFailedCnt(){
        return this.failedCnt;
    }
    public void setCreateTime(Date createTime){
        this.createTime=createTime;
    }
    public Date getCreateTime(){
        return this.createTime;
    }
    public void setFroceStop(Integer forceStop){
        this.forceStop=forceStop;
    }
    public Integer isFroceStop(){
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
    public Map<String,Utils.ExecStatus> getTests(){
        return this.tests;
    }
    public void setTests(Map<String,Utils.ExecStatus> tests){
        this.tests=tests;
    }
    public String getJenkinsJobName(){
        return this.jenkinsJobName;
    }
    public Integer getJenkinsBuildId(){
        return this.jenkinsBuildId;
    }
    public Utils.ExecStatus getTaskStatus(){
        return this.taskStatus;
    }
    public void setJenkinsJobName(String jenkinsJobName){
        this.jenkinsJobName=jenkinsJobName;
    }
    public void setJenkinsBuildId(Integer jenkinsBuildId){
        this.jenkinsBuildId=jenkinsBuildId;
    }
    public void setTaskStatus(Utils.ExecStatus taskStatus){
        this.taskStatus=taskStatus;
    }
    public void addTest(String key,Utils.ExecStatus value){
        if(tests==null){
            this.tests=new HashMap<String,Utils.ExecStatus>();
        }
        this.tests.put(key,value);
    }
    public void setDataVersion(String dataVersion){
        this.dataVersion=dataVersion;
    }
    public String getDataVersion(){

        return this.dataVersion;
    }
    public void setEnv(String env){
        this.env=env;
    }
    public String getEnv(){
        return this.env;
    }
}