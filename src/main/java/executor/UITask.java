package executor;

import org.testng.annotations.Test;
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

import core.pojo.BuildPojo;
import core.pojo.CaseDataPojo;
import core.pojo.CasePojo;
import core.pojo.KeyPojo;
import utils.ReportUtils;
import utils.SeleniumUtils;
import utils.ServerUtils;
import utils.Utils;

public class UITask implements Executor<CasePojo, CaseDataPojo> {
    private static Logger logger = Logger.getLogger(UITask.class);
    private Map<String, String> gPara = null;
    private BuildPojo suite = null;
    private Executor successor = null;
    private Map<String, CaseDataPojo> data = null;
    private List<CasePojo> tests = null;
    public static Hashtable<String, KeyPojo> cachedKey = null;
    public static Map<String,String> cachedObj=null;

    @BeforeClass
    @Parameters({ "jobName", "buildId", "dataVersion", "logLevel" })
    public void loadData(String jobName, Integer buildId, String dataVersion, @Optional String logLevel)
            throws Exception {
        ReportUtils.init(jobName + String.valueOf(buildId));
        ServerUtils.init();
        // 从数据库获取所有的全局变量
        this.loadGlobalParas();
        // 从数据库根据suiteid获取所有的case
        this.suite = ServerUtils.getExecution(jobName, buildId);
        List<String> casesId = Lists.newArrayList(this.suite.getCases().keySet());
        this.tests = ServerUtils.getCases(casesId);
        // 从数据库根据suiteid获取所有的数据
        this.loadTestData(casesId, dataVersion);
        this.loadKeys();
        cachedObj=ServerUtils.getAllObjects();
    }

    @Test
    public void start() throws Exception {
        this.execute(null, this.gPara);
    }

    public String execute(Map<String, String> sPara, Map<String, String> gPara) throws Exception {
        String taskResult = Utils.execPass;
        for (CasePojo casz : this.tests) {
            //一个addSubTest必须对应到一个completeTestReport
            ReportUtils.addSubTest(casz.getDesc());
            if (checkStopExec()) {
                this.suite.setFroceStop(true);
                taskResult = Utils.execException;
                continue;
            }
            this.suite.setStartTime(new Date());
            ReportUtils.addStartTime(new Date());
            String result = "";
            try {
                if (casz.getSteps() == null || casz.getSteps().size() == 0) {
                    throw new Exception("case does not have steps: " + casz.getCaseId());
                }
                result = this.getSuccessor(casz, this.getTestData(casz)).execute(this.getSharedData(), gPara);
            } catch (Exception e) {
                result = Utils.execFail;
            }finally{
                SeleniumUtils.closeBrowsersKey(null);
            }
            // 有一个case失败则suite是失败状态
            if (result.equals(Utils.execFail)) {
                taskResult = result;
                this.suite.getCases().put(casz.getCaseId(), Utils.ExecStatus.FAILED);
            } else {
                this.suite.getCases().put(casz.getCaseId(), Utils.ExecStatus.SUCCESS);
            }
            this.updateCaseStatus(casz.getCaseId(), result);
            
            ReportUtils.addEndTime(new Date());
            ReportUtils.completeTestReport();
        }

        if (taskResult.equals(Utils.execException)) {
            this.suite.setBuildStatus(Utils.ExecStatus.FORCESTOP);
        } else if (taskResult.equals(Utils.execFail)) {
            this.suite.setBuildStatus(Utils.ExecStatus.FAILED);
        } else {
            this.suite.setBuildStatus(Utils.ExecStatus.SUCCESS);
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
        this.successor = new CaseExecutor(test, data);
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
        BuildPojo result = ServerUtils.getExecution(this.suite.getJobName(), this.suite.getBuildId());
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

    private void loadGlobalParas() throws Exception {
        List<String> result = ServerUtils.getGlobalParas();
        this.gPara = new HashMap<String, String>();
        for (String item : result) {
            this.gPara.put(item, "");
        }
        logger.debug(this.gPara.keySet());
    }

    private void loadKeys() throws Exception {
        List<KeyPojo> result = ServerUtils.getAllKeys();

        UITask.cachedKey = new Hashtable<String, KeyPojo>();
        for (KeyPojo item : result) {
            UITask.cachedKey.put(item.getName(), item);
        }
        logger.debug(UITask.cachedKey.get("click").toString());
    }

    private void loadTestData(List<String> casesId, String version) throws Exception {
        List<CaseDataPojo> result = ServerUtils.getCasesData(casesId, version);

        this.data = new HashMap<String, CaseDataPojo>();
        for (CaseDataPojo item : result) {
            this.data.put(item.getCaseId(), item);
        }
        logger.debug(this.data.size());
    }
}