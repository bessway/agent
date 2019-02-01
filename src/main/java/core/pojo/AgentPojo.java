package core.pojo;

public class AgentPojo{
    private String osType=null;
    private String osVersion=null;
    private String browserType=null;
    private String browserVersion=null;
    private Boolean status=null;
    private String jobName=null;
    private Boolean isPublic=null;
    private String owner=null;

    public void setOwner(String owner){
        this.owner=owner;
    }
    public String getOwner(){
        return this.owner;
    }
    public void setPublic(Boolean isPublic){
        this.isPublic=isPublic;
    }
    public Boolean isPublic(){
        return this.isPublic;
    }
    public void setJobName(String jobName){
        this.jobName=jobName;
    }
    public String getJobName(){
        return this.jobName;
    }
    public void setOsType(String osType){
        this.osType=osType;
    }
    public String getOsType(){
        return this.osType;
    }
    public void setOsVersion(String osVersion){
        this.osVersion=osVersion;
    }
    public String getOsVersion(){
        return this.osVersion;
    }
    public void setBrowserType(String browserType){
        this.browserType=browserType;
    }
    public String getBrowserType(){
        return this.browserType;
    }
    public void setBrowserVersion(String browserVersion){
        this.browserVersion=browserVersion;
    }
    public String getBrowserVersion(){
        return this.browserVersion;
    }
    public void setStatus(Boolean status){
        this.status=status;
    }
    public Boolean getStatus(){
        return this.status;
    }
}