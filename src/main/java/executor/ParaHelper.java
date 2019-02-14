package executor;

import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngineManager;

import pojo.Para;

public class ParaHelper{
    //paraId@stepId=para
    private Map<String, Para> paras=null;
    //paraName=para, 没有引用参数
    private Map<String, Para> nameKeyParas=null;
    private static Integer maxRefLevel = 4;
    private static String paraRegx = "\\{[\\s\\S]\\}";
    private static String formulaRegx = "eval\\([\\s\\S]\\)";

    public ParaHelper(Map<String, Para> paras){
        this.paras=paras;
        this.nameKeyParas();
    }
    private void nameKeyParas(){
        nameKeyParas=new Hashtable<String, Para>();
        for(Para item:this.paras.values()){
            if(item.getIsFormalPara().equals(0)){
                nameKeyParas.put(item.getParaName(), item);
            }
        }
    }
    //{pName}，可能是引用参数的值
    //othertexteval(1+{pName}){pName}othertext,pName=othertexteval(1+{pName1}){pName1}othertext
    //不能引用自己，或者循环引用，所以需要限制引用层级
    public String unpackPara(String paraValue) throws Exception{
        Integer refLevel=1;
        try{
            paraValue = this.limitUnpackPara(paraValue, refLevel);
        }catch(Exception e){
            throw new Exception(e.getMessage()+": "+paraValue);
        }
        
        return paraValue;
    }
    private String limitUnpackPara(String paraValue, Integer level) throws Exception{
        if(level>maxRefLevel){
            throw new Exception("参数引用层级太多，可能存在循环引用");
        }
        if(!paraValue.matches(paraRegx)){
            return paraValue;
        }
        paraValue = this.replacePara(paraValue, level);
        paraValue=this.eval(paraValue);
        return paraValue;
    }
    private String eval(String paraValue) throws Exception{
        if(paraValue.matches(paraRegx)){
            throw new Exception("计算表达式时仍然存在参数: "+paraValue);
        }
        return paraValue;
    }
    private String replacePara(String paraValue, Integer level) throws Exception{
        Pattern p=Pattern.compile(paraRegx);
        Matcher m=p.matcher(paraValue);
        while(m.find()){
            String refParaName=m.group();
            String refParaValue=this.nameKeyParas.get(refParaName).getParaValue();
            refParaValue=this.limitUnpackPara(refParaValue, level+1);
            
            paraValue=paraValue.replace(refParaName, refParaValue);
        }

        return paraValue;
    }
}