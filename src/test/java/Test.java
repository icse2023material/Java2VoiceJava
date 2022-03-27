import com.lyun.kexin.utils.FileAnalysis;
import com.lyun.kexin.utils.ParameterAnalysis;

public class Test {
    public static void main(String[] args) {
//        System.out.println(FileAnalysis.singleFile("F:\\project\\java_project\\JavaToVoiceJava\\src\\main\\resources\\in.java"));
        String[] arg = {"F:\\workspace\\voice2cod\\voice2cod-dataset-generation\\examples\\javatest","d"};
        ParameterAnalysis.analysis(arg);
    }
}
