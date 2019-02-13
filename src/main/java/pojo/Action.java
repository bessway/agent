package pojo;

import java.util.List;

public class Action implements Executable{
    private String actionId = null;
    private String actionName = null;
    private Integer hasUIObject = null;
    private Integer hasResponse = null;
    private List<ActionPara> actionParas = null;
    private String regFunc = null;
    // 1=ui, 2=api, 3=ref
    private Integer actionType = null;

    public void setActionId(String actionId){
        this.actionId=actionId;
    }
    public String getActionId(){
        return this.actionId;
    }

    public void setActionName(String actionName){
        this.actionName=actionName;
    }
    public String getActionName(){
        return this.actionName;
    }

    public void setHasUIObject(Integer hasUIObject){
        this.hasUIObject=hasUIObject;
    }
    public Integer getHasUIObject(){
        return this.hasUIObject;
    }

    public void setHasResponse(Integer hasResponse){
        this.hasResponse=hasResponse;
    }
    public Integer getHasResponse(){
        return this.hasResponse;
    }

    public void setActionParas(List<ActionPara> actionParas){
        this.actionParas=actionParas;
    }
    public List<ActionPara> getActionParas(){
        return this.actionParas;
    }
    public Action(String actionId){
        this.actionId=actionId;
    }
    public String getRegFunc () {
        return this.regFunc;
    }
    public void setRegFunc (String regFunc) {
        this.regFunc = regFunc;
    }
    public Integer getActionType () {
        return this.actionType;
    }
    public void setActionType (Integer actionType) {
        this.actionType = actionType;
    }
}