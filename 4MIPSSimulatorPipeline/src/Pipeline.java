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
    static ArrayList<String> pipelineRegs = new ArrayList<>(Arrays.asList("empty", "empty", "empty", "empty"));

    // shifts values in pipeline 1 unit right
    private static void shiftPipelineRight() {
        for (int i = pipelineVals.size() - 1; i > 0; i--) {
            pipelineVals.set(i, pipelineVals.get(i - 1));
        }
    }

    // shifts values in pipeline 1 unit right
    private static void shiftPipelineRegRight() {
        for (int i = pipelineRegs.size() - 1; i > 0; i--) {
            pipelineRegs.set(i, pipelineRegs.get(i - 1));
        }
    }

    public static boolean run(String opcode, String destination, String requirement1, String requirement2) {
        shiftPipelineRight();
        shiftPipelineRegRight();
         else {
            pipelineVals.set(0, opcode);
            pipelineRegs.set(0, destination);
            return true;
        } if (needsToStall(opcode, requirement1, requirement2)) {
            pipelineVals.set(1, "stall");
            pipelineVals.set(0, opcode);
            pipelineRegs.set(0, destination);
            pipelineRegs.set(1, "empty");
            return false;
        }
    }

    private static boolean needsToStall(String opcode, String requirement1, String requirement2) {
        switch (opcode) {
            case "add", "sub", "and", "or", "slt", "sll", "srl", "lw", "sw" -> {
                if (pipelineRegs.get(1).equals(requirement1) || pipelineRegs.get(1).equals(requirement2)) {
                    return true;
                }
            }
            case "beq", "bne" -> {
                if (pipelineRegs.get(1).equals(requirement1) || pipelineRegs.get(1).equals(requirement2)) {
                    return true;
                }
            }
            case "j", "jr" -> {
                if (pipelineRegs.get(1).equals(requirement1) || pipelineRegs.get(1).equals(requirement2)) {
                    return true;
                }
            }
            default -> {}
        }
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
