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
    public static int run(String opcode, int pc, String requirement1, String requirement2) {
        shiftPipelineRight();
        pipelineVals.set(0, opcode);
        if (needsToStall(opcode, requirement1, requirement2)) {
            pipelineVals.set(1, "stall");
            pipelineVals.set(0, opcode);
            return pc;
        }
        return pc + 1;
    }

    private static boolean needsToStall(String opcode, String requirement1, String requirement2) {

        return false;
    }

    public static void printPipeline(int pc) {
        int spacing = 10;
        System.out.print(padRightSpaces("pc", spacing));
        for (int i = 0; i < pipeline.size(); i++) {
            System.out.print(padRightSpaces(pipeline.get(i), spacing));
        }
        System.out.println();
        System.out.print(padRightSpaces(Integer.toString(pc), spacing));
        for (int i = 0; i < pipelineVals.size(); i++) {
            System.out.print(padRightSpaces(pipelineVals.get(i), spacing));
        }
        System.out.println("\n");
    }

    private static String padRightSpaces(String inputString, int length) {
        if (inputString.length() >= length) {
            return inputString;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(inputString);
        while (sb.length() < length) {
            sb.append(' ');
        }

        return sb.toString();
    }
}
