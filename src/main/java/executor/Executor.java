package executor;

import java.util.Map;

public interface Executor<T,D>{
    public String execute(Map<String,String> sPara,Map<String,String> gPara) throws Exception;
    public Executor<T,D> getSuccessor(T test,D data);
}