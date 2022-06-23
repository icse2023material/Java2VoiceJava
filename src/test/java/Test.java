import com.lyun.kexin.utils.FileAnalysis;
import com.lyun.kexin.utils.ParameterAnalysis;

public class Test {
    public static void main(String[] args) {
//        System.out.println(FileAnalysis.singleFile("F:\\workspace\\voice2cod\\grammer\\test.java"));
//
//        String[] arg = {"F:\\workspace\\voice2cod\\fastjson-master\\src\\main\\java\\com\\alibaba","d"};
//        ParameterAnalysis.analysis(arg);

        String[] arg = {"/Users/stefanzan/Research/2021/voice-coding/voice2code/src/main/java/cn/edu/lyun"
                ,"o","/Users/stefanzan/Research/2021/voice2CodeInVoiceJava"};
        ParameterAnalysis.analysis(arg);

//         String[] arg = {"/Users/stefanzan/Research/2021/dubbo/dubbo-cluster/src/main/java/org/apache/dubbo/rpc/cluster/ClusterScopeModelInitializer.java"};
//        String[] arg = {"/Users/stefanzan/Research/2021/JavaToVoiceJava/test/testcases/ListHelper.java"};
//        ParameterAnalysis.analysis(arg);
    }
}
