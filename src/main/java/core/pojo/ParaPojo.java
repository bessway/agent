package core.pojo;
import java.util.Comparator;
public class ParaPojo{
    private String pValue=null;
    private Integer pIndex=null;
    private String pDesc=null;
    public static Comparator<ParaPojo> pindexComp=new Comparator<ParaPojo>() {
        @Override
        public int compare(ParaPojo first, ParaPojo second){
            return first.getpIndex()-second.getpIndex();
        }
    };

    public String getpValue(){
        return this.pValue;
    }
    public void setpValue(String pValue){
        this.pValue=pValue;
    }
    public String getpDesc(){
        return this.pDesc;
    }
    public void setpDesc(String pDesc){
        this.pDesc=pDesc;
    }
    public Integer getpIndex(){
        return this.pIndex;
    }
    public void setpIndex(Integer pIndex){
        this.pIndex=pIndex;
    }
    public String toString(){
        return "pValue:"+this.pValue
        +"\npIndex:"+String.valueOf(this.pIndex)
        +"\npDesc:"+this.pDesc;
    }
}