package utils;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import core.pojo.AgentPojo;
import core.pojo.Task;
import core.pojo.Para;
import core.pojo.Test;
import core.pojo.Action;

public class ServerUtils {
    private static String agentConfigFile = "agent.properties";
    private static Logger logger = Logger.getLogger(ServerUtils.class);
    private static CloseableHttpClient client=null;
    private static Properties aProperty=null;
    private static Gson gson=new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    public static void init() throws Exception{
        try{
            aProperty = Utils.readPropery(agentConfigFile);
        }catch(Exception e){
            logger.debug("cannot find config file: "+agentConfigFile);
            throw new Exception("cannot find config file: "+agentConfigFile);
        }
        client=HttpClients.createDefault();
    }

    private static CloseableHttpResponse callMethod(String method,HttpRequestBase request) throws Exception{
        CloseableHttpResponse res=null;
        request.addHeader("Content-Type", "application/json;charset=UTF-8");
        request.addHeader("Accept", "application/json;charset=UTF-8");
        String url="http://" + aProperty.getProperty("server.host")+method;
        try{
            request.setURI(URI.create(url));
            
            res =client.execute(request);
            Integer status = res.getStatusLine().getStatusCode();
            if (status != 200) {
                throw new Exception("call " + method+" failed, status code "+String.valueOf(status));
            }
        }catch(Exception e){
            logger.debug(e.getMessage()+"\n"+e.getCause());
            throw new Exception(e.getMessage());
        }
        return res;
    }

    public static Task getTask(String jobName,Integer buildId) throws Exception{
        String method="/api/v2/jenkins/job/"+jobName+"/build/"+String.valueOf(buildId);
        HttpGet get=new HttpGet();
        CloseableHttpResponse res=callMethod(method, get);
        Task result=gson.fromJson(EntityUtils.toString(res.getEntity()), Task.class);
        res.close();
        return result;
    }
    public static void updateExecStatus(Task suite)throws Exception{
        String method="/api/v2/jenkins/jobstatus";
        HttpPut put=new HttpPut();
        put.setEntity(new StringEntity(gson.toJson(suite),"utf-8"));
        callMethod(method, put).close();;
    }
    public static void updateCaseStatus(String jobName, Integer buildId, String caseId, Utils.ExecStatus status)throws Exception{
        String method="/api/v2/jenkins/casestatus";
        HttpPut put=new HttpPut();
        Task req=new Task();
        req.setJenkinsJobName(jobName);
        req.setJenkinsBuildId(buildId);
        req.addTest(caseId, status);
        put.setEntity(new StringEntity(gson.toJson(req),"utf-8"));
        callMethod(method, put).close();;
    }
    public static void updateAgentStatus(String jobName, Boolean isFree)throws Exception{
        String method="/api/v2/jenkins/agentstatus";
        HttpPut put=new HttpPut();
        AgentPojo req=new AgentPojo();
        req.setJobName(jobName);
        req.setStatus(isFree);
        put.setEntity(new StringEntity(gson.toJson(req),"utf-8"));
        callMethod(method, put).close();;
    }
    public static List<Para> getTestParas(String testId,String version)throws Exception{
        String method="/api/v2/paras/test/"+testId+"/version/"+version;
        HttpGet get=new HttpGet();
        CloseableHttpResponse res=callMethod(method, get);
        List<Para> result=gson.fromJson(EntityUtils.toString(res.getEntity()),new TypeToken<List<Para>>() {
        }.getType());
        res.close();
        return result;
    }
    public static List<Test> getTests(List<String> testIds)throws Exception{
        String method="/api/v2/tests";
        HttpPost post=new HttpPost();
        post.setEntity(new StringEntity(gson.toJson(testIds),"utf-8"));
        CloseableHttpResponse res=callMethod(method, post);
        List<Test> result=gson.fromJson(EntityUtils.toString(res.getEntity()),new TypeToken<List<Test>>() {
        }.getType());
        res.close();
        return result;
    }
    public static List<Action> getAllActions()throws Exception{
        String method="/api/v2/actions/all";
        HttpGet get=new HttpGet();
        CloseableHttpResponse res=callMethod(method, get);
        List<Action> result=gson.fromJson(EntityUtils.toString(res.getEntity()), new TypeToken<List<Action>>() {
        }.getType());
        res.close();
        return result;
    }
    public static Map<String,String> getUiObjectsByPage(String page)throws Exception{
        String method="/api/v2/objects/structedpage/"+page;
        HttpGet get=new HttpGet();
        CloseableHttpResponse res=callMethod(method, get);
        Map<String,String> result=gson.fromJson(EntityUtils.toString(res.getEntity()), new TypeToken<Map<String,String>>() {
        }.getType());
        res.close();
        return result;
    }
}