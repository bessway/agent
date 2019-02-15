package executor;

import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;

import pojo.Para;

public class ParaHelper{
    //paraId@stepId=para
    private Map<String, Para> paras=null;
    //paraName=para, 没有引用参数
    private Map<String, Para> nameKeyParas=null;
    private static Integer maxRefLevel = 4;
    private static String paraRegx = "\\{[\\s\\S]+?\\}";
    private static String formulaRegx = "@\\([\\s\\S]+?\\)";

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
        if(!paraValue.matches("[\\s\\S]*"+paraRegx+"[\\s\\S]*")){
            return paraValue;
        }
        paraValue = this.replacePara(paraValue, level);
        paraValue=this.eval(paraValue);
        return paraValue;
    }
    //这里只处理数值的计算
    public String eval(String paraValue) throws Exception{
        if(paraValue.matches("[\\s\\S]*"+paraRegx+"[\\s\\S]*")){
            throw new Exception("计算表达式时仍然存在参数: "+paraValue);
        }
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");
        Object result=null;

        Pattern p=Pattern.compile(formulaRegx);
        Matcher m=p.matcher(paraValue);
        while(m.find()){
            String oriFormula=m.group();
            String formula=oriFormula.substring(2, oriFormula.length()-1);
            String accurate="0";
            //有逗号表示指定了精度
            if(formula.contains(",")){
                String[] sFormula=formula.split(",");
                formula = sFormula[0];
                accurate=sFormula[1];
                formula="("+formula+").toFixed("+accurate+")";
            }
            result=engine.eval(formula);
            paraValue=paraValue.replace(oriFormula, String.valueOf(result));
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
    // private static Para getPara(Long id,String value){
    //     Para newPara=new Para();
    //     newPara.setParaId(id);
    //     newPara.setParaName("{name"+String.valueOf(id)+"}");
    //     newPara.setParaValue(value);
    //     newPara.setIsFormalPara(0);

    //     return newPara;
    // }
    // public static void main(String[] args) throws Exception{
    //     Map<String, Para> data = new Hashtable<>();
    //     Para newPara=getPara(1L,"first@({name2}+3)first@({name2}/3,2)second@({name3}*3)");
    //     data.put(String.valueOf(newPara.getParaId()),newPara);

    //     newPara=getPara(2L,"@({name3}-1+{name4})");
    //     data.put(String.valueOf(newPara.getParaId()),newPara);

    //     newPara=getPara(3L,"4");
    //     data.put(String.valueOf(newPara.getParaId()),newPara);

    //     newPara=getPara(4L,"2");
    //     data.put(String.valueOf(newPara.getParaId()),newPara);

    //     String paraValue="init{name1}init{name3}";

    //     paraValue = new ParaHelper(data).unpackPara(paraValue);
    //     System.out.println(paraValue);
    // }
}