import java.util.ArrayList;
import java.util.Arrays;

public class Pipeline {
    //IF: Instruction fetch
    //ID: Instruction decode
    //EX: Execute
    //MEM: Memory access
    //WB: Write back
    static final ArrayList<String> pipeline = new ArrayList<>(Arrays.asList("if/id", "id/exe", "exe/mem", "mem/wb"));
    static ArrayList<String> pipeLineOps = new ArrayList<>(Arrays.asList("empty", "empty", "empty", "empty"));
    static ArrayList<String> pipelineRegs = new ArrayList<>(Arrays.asList("empty", "empty", "empty", "empty"));

    // shifts values in pipeline 1 unit right
    private static void shiftPipelineRight() {
        for (int i = pipeLineOps.size() - 1; i > 0; i--) {
            pipeLineOps.set(i, pipeLineOps.get(i - 1));
        }
    }

    // shifts values in pipeline 1 unit right
    private static void shiftPipelineRegRight() {
        for (int i = pipelineRegs.size() - 1; i > 0; i--) {
            pipelineRegs.set(i, pipelineRegs.get(i - 1));
        }
    }

    /*
    returns true when the instruction is complete,
    false when the instruction is not complete
     */
    public static boolean run(String opcode, String destination, String requirement1, String requirement2) {
        boolean shouldStall = needsToStall(opcode, requirement1, requirement2);
        shiftPipelineRight();
        shiftPipelineRegRight();
        pipeLineOps.set(0, opcode);
        pipelineRegs.set(0, destination);
        if (shouldStall) {
            pipeLineOps.set(1, "stall");
            pipelineRegs.set(1, "empty");
            //System.out.println("Should stall");
            // should insert a stall, but we return true in order to indicate an instruction is complete
            return true;
        }
        shouldStall = needsToStall(opcode, requirement1, requirement2);
        if (shouldStall) {
            //System.out.println("Should stall");
            return false;
        }
        return true;
    }

    private static boolean needsToStall(String opcode, String requirement1, String requirement2) {
        switch (opcode) {
            case "add", "sub", "and", "or", "slt", "sll", "srl", "lw", "sw" -> {
                if (pipeLineOps.get(1).equals("lw") && (pipelineRegs.get(1).equals(requirement1) || pipelineRegs.get(1).equals(requirement2))) {
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

//    public static void printPipeline(int pc) {
//        int spacing = 10;
//        System.out.print(padRightSpaces("pc", spacing));
//        for (String s : pipeline) {
//            System.out.print(padRightSpaces(s, spacing));
//        }
//        System.out.println();
//        System.out.print(padRightSpaces(Integer.toString(pc), spacing));
//        for (String pipelineVal : pipeLineOps) {
//            System.out.print(padRightSpaces(pipelineVal, spacing));
//        }
//        System.out.println("\n");
//        /*System.out.print(padRightSpaces("dest.", spacing));
//        for (String pipelineReg : pipelineRegs) {
//            System.out.print(padRightSpaces(pipelineReg, spacing));
//        }
//        System.out.println("\n");
//         */
//    }

    public static void printPipeline(int pc) {
        System.out.print("pc\t");
        for (String s : pipeline) {
            System.out.print(s + "\t");
        }
        System.out.println("\r");
        System.out.print(pc + "\t");
        for (String pipelineVal : pipeLineOps) {
            System.out.print(pipelineVal + "\t");
        }
        System.out.println("\r\n\r");
        /*System.out.print(padRightSpaces("dest.", spacing));
        for (String pipelineReg : pipelineRegs) {
            System.out.print(padRightSpaces(pipelineReg, spacing));
        }
        System.out.println("\n");
         */
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
