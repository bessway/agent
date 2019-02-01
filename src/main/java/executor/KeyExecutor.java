package executor;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aventstack.extentreports.Status;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import core.pojo.KeyPojo;
import core.pojo.ParaPojo;
import utils.ReportUtils;
import utils.SeleniumUtils;
import utils.Utils;

import java.lang.reflect.Method;
import core.pojo.StepDataPojo;

public class KeyExecutor implements Executor<KeyPojo,StepDataPojo>{
    private static Logger logger=Logger.getLogger(KeyExecutor.class);
    private KeyPojo test=null;
    private List<ParaPojo> data=null;
    private String target=null;
    private String response=null;
    private static String keySuffix="Key";
    public KeyExecutor(KeyPojo test,StepDataPojo data){
        this.test=test;
        this.data=data.getSortedStepParas();
        this.target=data.getTarget();
        this.response=data.getResponse();
    }
    public String execute(Map<String,String> sPara,Map<String,String> gPara) throws Exception{
        String funcName=this.test.getRegFunc();
        funcName=funcName+keySuffix;
        int paraCount=this.data.size();
        if(this.test.getTarget()){
            paraCount=paraCount+1;
        }
        Class<String>[] mPara=new Class[paraCount];
        for(int i=0;i<mPara.length;i++){
            mPara[i]=String.class;
        }
        Object result=null;
        try{
            Method toExe=SeleniumUtils.class.getMethod(funcName, mPara);
            //Method toExe=SeleniumUtils.class.getMethod(funcName, String.class, String.class, String.class);
            List<String> mParaValue=new ArrayList<String>();
            if(this.target!=null&&!this.target.equals("")){
                mParaValue.add(0,this.getXpath(this.target,sPara,gPara));
            }
            mParaValue.addAll(this.wrapPara(sPara,gPara));

            ReportUtils.addLog(Status.INFO, funcName+this.paraToString(mParaValue)+" "+String.valueOf(this.target), null);
        
            result=toExe.invoke(null, mParaValue.toArray());
            if(this.test.getResponse()){
                if(this.response.contains(Utils.gParaSymbol)){
                    gPara.put(this.response,String.valueOf(result));
                }
                if(this.response.contains(Utils.sParaSymbol)){
                    sPara.put(this.response,String.valueOf(result));
                }
            }
        }catch(NoSuchMethodException e){
            logger.debug("cannot find the method "+funcName);
            result=Utils.execFail;
        }catch(Exception e){
            logger.debug("excute method "+funcName+" failed");
            result=Utils.execFail;
        }
        

        return String.valueOf(result);
    }
    public Executor getSuccessor(KeyPojo test,StepDataPojo data){
        return null;
    }
    public List<String> wrapPara(Map<String,String> sPara,Map<String,String> gPara){
        List<String> result=new ArrayList<String>();
        for(int i=0;i<this.data.size();i++){
            result.add(i,this.unpackPara(this.data.get(i).getpValue(), sPara, gPara));
        }
        return result;
    }
    public String unpackPara(String para,Map<String,String> sPara,Map<String,String> gPara){
        if(para.contains(Utils.gParaSymbol)){
            Pattern p=Pattern.compile(".*("+Utils.gParaSymbol+".*"+Utils.gParaSymbol+").*");
            Matcher m=p.matcher(para);
            m.find();
            if(m.groupCount()>0){
                for(int i=1;i<=m.groupCount();i++){
                    para=para.replace(m.group(i), gPara.get(m.group(i)));
                }
            }
        }
        if(para.contains(Utils.sParaSymbol)){
            Pattern p=Pattern.compile(".*("+Utils.sParaSymbol+".*"+Utils.sParaSymbol+").*");
            Matcher m=p.matcher(para);
            m.find();
            if(m.groupCount()>0){
                for(int i=1;i<=m.groupCount();i++){
                    para=para.replace(m.group(i), sPara.get(m.group(i)));
                }
            }
        }
        return para;
    }
    private String paraToString(List<String> paras){
        String result="";
        for(String item:paras){
            result=result+", "+item;
        }
        return result;
    }
    private String getXpath(String pathName,Map<String,String> sPara,Map<String,String> gPara) throws Exception{
        String result=UITask.cachedObj.get(pathName);
        if(result==null){
            throw new Exception("cannot find the xpath: "+pathName);
        }
        result=this.unpackPara(result, sPara, gPara);
        return result;
    }
}