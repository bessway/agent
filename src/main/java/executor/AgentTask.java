package executor;

import org.testng.collections.Lists;

import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import pojo.Task;
import pojo.Test;
import pojo.Para;
import pojo.Action;
import pojo.Executable;
import utils.ReportUtils;
import utils.SeleniumUtils;
import utils.ServerUtils;
import utils.Utils;

public class AgentTask implements Executor {
    private static Logger logger = Logger.getLogger(AgentTask.class);
    private Task suite = null;
    private Executor successor = null;
    private List<Test> tests = null;

    @BeforeClass
    @Parameters({ "jobName", "buildId", "dataVersion", "logLevel", "env" })
    public void loadData(String jobName, Integer buildId, String dataVersion, String env, @Optional String logLevel)
            throws Exception {
        ReportUtils.init(jobName + String.valueOf(buildId));
        ServerUtils.init();
        // 根据suiteid获取所有的case
        this.suite = ServerUtils.getTask(jobName, buildId);
        List<String> testIds = Lists.newArrayList(this.suite.getTests().keySet());
        this.tests = ServerUtils.getTests(testIds);
        this.loadActions();
        Utils.dataVersion = dataVersion;
        Utils.logLevel = logLevel;
        //从数据库获取环境变量
        //this.getEnvConfig();
    }

    @org.testng.annotations.Test
    public void start() throws Exception {
        this.execute();
    }

    public String execute() throws Exception {
        Utils.ExecStatus taskResult = Utils.ExecStatus.SUCCESS;
        for (Test casz : this.tests) {
            //一个addSubTest必须对应到一个completeTestReport
            ReportUtils.addSubTest(String.valueOf(casz.getIndex())+'-'+casz.getTestDesc());
            if (checkStopExec().equals(1)) {
                this.suite.setFroceStop(1);
                taskResult = Utils.ExecStatus.FORCESTOP;
                break;
            }
            this.suite.setStartTime(new Date());
            ReportUtils.addStartTime(new Date());
            String result = Utils.ExecStatus.SUCCESS.name();
            try {
                result = this.getSuccessor(casz, this.getTestParas(casz.getTestId())).execute();
            } catch (Exception e) {
                result = Utils.ExecStatus.FAILED.name();
            }finally{
                SeleniumUtils.closeBrowsersKey(null);
            }
            // 有一个case失败则suite是失败状态
            if (result.equals(Utils.ExecStatus.FAILED.name())) {
                taskResult = Utils.ExecStatus.FAILED;
            }
            this.suite.getTests().put(casz.getTestId(), result);
            ServerUtils.updateTestStatus(this.suite.getJenkinsJobName(), this.suite.getJenkinsBuildId(), 
                                        casz.getTestId(),result);
            
            ReportUtils.addEndTime(new Date());
            ReportUtils.completeTestReport();
        }

        this.suite.setTaskStatus(taskResult.name());
        // 同步服务端job的执行情况,同时重置agent状态,放在jenkins做了
        // this.syncRunningJob();
        // 保存suite执行结果
        this.suite.setEndTime(new Date());
        Utils.cachedAction=null;
        Utils.cachedUiObj=null;
        ServerUtils.updateExecStatus(this.suite);
        ReportUtils.generateReport();
        return taskResult.toString();
    }

    @Override
    public Executor getSuccessor(Executable test, Map<String, Para> data) {
        this.successor = new TestExecutor(test, data);
        return this.successor;
    }
    // ================================================================================================================//
    // 检查是否手工停止执行
    private Integer checkStopExec() throws Exception {
        Task result = ServerUtils.getTask(this.suite.getJenkinsJobName(), this.suite.getJenkinsBuildId());
        return result.isFroceStop() == null ? 0 : result.isFroceStop();
    }

    private void loadActions() throws Exception {
        List<Action> result = ServerUtils.getAllActions();

        Utils.cachedAction = new Hashtable<String, Action>();
        for (Action item : result) {
            Utils.cachedAction.put(item.getActionId(), item);
        }
        logger.debug(Utils.cachedAction.get("click").toString());
    }
    private Map<String, Para> getTestParas(String testId) throws Exception{
        List<Para> paras = ServerUtils.getTestParas(testId, Utils.dataVersion);
        Map<String, Para> result=new Hashtable<String, Para>();
        for(Para item:paras){
            //如果是refPara，跟步骤有关系
            if(item.getRefTestId()!=null){
                result.put(String.valueOf(item.getParaId())+"@"+String.valueOf(item.getStepId()), item);
            }else{
                result.put(String.valueOf(item.getParaId()), item);
            }
        }
        return result;
    }
}