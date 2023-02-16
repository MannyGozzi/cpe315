import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class Pipeline {
    //IF: Instruction fetch
    //ID: Instruction decode
    //EX: Execute
    //MEM: Memory access
    //WB: Write back
    static final ArrayList<String> pipeline = new ArrayList<>(Arrays.asList("if/id", "id/exe", "exe/mem", "mem/wb"));
    static ArrayList<String> pipelineVals = new ArrayList<>(Arrays.asList("empty", "empty", "empty", "empty"));

    // shifts values in pipeline 1 unit right
    private static void shiftPipelineRight() {
        for (int i = pipelineVals.size() - 1; i > 0; i--) {
            pipelineVals.set(i, pipelineVals.get(i - 1));
        }
    }
    public static void run(String opcode) {
        shiftPipelineRight();
        pipelineVals.set(0, opcode);
    }

    public static void printPipeline(int pc) {
        System.out.print("pc\t\t");
        for (int i = 0; i < pipeline.size(); i++) {
            System.out.print(pipeline.get(i) + "\t\t");
        }
        System.out.print("\n" + pc + "\t\t");
        for (int i = 0; i < pipelineVals.size(); i++) {
            System.out.print(pipelineVals.get(i) + "\t\t");
        }
        System.out.println();
    }
}
