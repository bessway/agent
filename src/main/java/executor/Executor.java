package executor;

import java.util.Map;
import pojo.Para;
import pojo.Executable;

public interface Executor{
    public String execute() throws Exception;
    public Executor getSuccessor(Executable test,Map<String, Para> data);
}