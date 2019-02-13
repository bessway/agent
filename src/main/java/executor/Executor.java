package executor;

import java.util.List;
import pojo.Para;
import pojo.Executable;

public interface Executor{
    public String execute() throws Exception;
    public Executor getSuccessor(Executable test,List<Para> data);
}