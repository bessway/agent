package executor;

import org.testng.collections.Lists;

import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import core.pojo.Task;
import core.pojo.Test;
import core.pojo.Para;
import core.pojo.Action;
import utils.ReportUtils;
import utils.SeleniumUtils;
import utils.ServerUtils;
import utils.Utils;

public class UITask implements Executor<Test, Para> {
    private static Logger logger = Logger.getLogger(UITask.class);
    private Task suite = null;
    private Executor successor = null;
    private Map<String, Para> data = null;
    private List<Test> tests = null;
    public static Hashtable<String, Action> cachedKey = null;
    public static Map<String,String> cachedUiObj=null;

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
            ReportUtils.addSubTest(casz.getTestDesc());
            if (checkStopExec()) {
                this.suite.setFroceStop(1);
                taskResult = Utils.ExecStatus.FORCESTOP;
                break;
            }
            this.suite.setStartTime(new Date());
            ReportUtils.addStartTime(new Date());
            Utils.ExecStatus result = Utils.ExecStatus.SUCCESS;
            try {
                if (casz.getSteps() == null || casz.getSteps().size() == 0) {
                    throw new Exception("case does not have steps: " + casz.getTestId());
                }
                result = this.getSuccessor(casz, this.getTestData(casz)).execute(this.getSharedData(), gPara);
            } catch (Exception e) {
                result = Utils.ExecStatus.FAILED;
            }finally{
                SeleniumUtils.closeBrowsersKey(null);
            }
            // 有一个case失败则suite是失败状态
            if (result.equals(Utils.execFail)) {
                taskResult = result;
                this.suite.getTests().put(casz.getTestId(), Utils.ExecStatus.FAILED);
            } else {
                this.suite.getTests().put(casz.getTestId(), Utils.ExecStatus.SUCCESS);
            }
            this.updateTestStatus(casz.getTestId(), result);
            
            ReportUtils.addEndTime(new Date());
            ReportUtils.completeTestReport();
        }

        if (taskResult.equals(Utils.execException)) {
            this.suite.setTaskStatus(Utils.ExecStatus.FORCESTOP);
        } else if (taskResult.equals(Utils.execFail)) {
            this.suite.setTaskStatus(Utils.ExecStatus.FAILED);
        } else {
            this.suite.setTaskStatus(Utils.ExecStatus.SUCCESS);
        }
        // 同步服务端job的执行情况,同时重置agent状态,放在jenkins做了
        // this.syncRunningJob();
        // 保存suite执行结果

        this.suite.setEndTime(new Date());
        ServerUtils.updateExecStatus(this.suite);
        ReportUtils.generateReport();
        return taskResult;
    }

    @Override
    public Executor getSuccessor(CasePojo test, CaseDataPojo data) {
        this.successor = new TestExecutor(test, data);
        return this.successor;
    }

    public CaseDataPojo getTestData(CasePojo test) throws Exception {
        CaseDataPojo result = this.data.get(test.getCaseId());
        if (result == null || result.getStepsData() == null || result.getStepsData().size() == 0) {
            throw new Exception("does not have data: " + test.getCaseId());
        }
        return result;
    }

    public Map<String, String> getSharedData() {
        return null;
    }

    // ================================================================================================================//
    // 检查是否停止执行
    private Boolean checkStopExec() throws Exception {
        Task result = ServerUtils.getExecution(this.suite.getJenkinsJobName(), this.suite.getJenkinsBuildId());
        return result.isFroceStop() == null ? false : result.isFroceStop();
    }

    // 更新suite中每个case的执行结果
    private void updateCaseStatus(String caseId, String status) throws Exception {
        if (status.equals(Utils.execFail)) {
            ServerUtils.updateCaseStatus(this.suite.getJobName(), this.suite.getBuildId(), caseId,
                    Utils.ExecStatus.FAILED);
        } else {
            ServerUtils.updateCaseStatus(this.suite.getJobName(), this.suite.getBuildId(), caseId,
                    Utils.ExecStatus.SUCCESS);
        }
    }

    private void loadActions() throws Exception {
        List<Action> result = ServerUtils.getAllActions();

        UITask.cachedKey = new Hashtable<String, Action>();
        for (Action item : result) {
            UITask.cachedKey.put(item.getActionId(), item);
        }
        logger.debug(UITask.cachedKey.get("click").toString());
    }
}