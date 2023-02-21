import java.util.*;

public class Pipeline {
    //IF: Instruction fetch
    //ID: Instruction decode
    //EX: Execute
    //MEM: Memory access
    //WB: Write back
    static final ArrayList<String> pipeline = new ArrayList<>(Arrays.asList("if/id", "id/exe", "exe/mem", "mem/wb"));
    static ArrayList<String> pipeLineOps = new ArrayList<>(Arrays.asList("empty", "empty", "empty", "empty"));
    static ArrayList<String> pipelineRegs = new ArrayList<>(Arrays.asList("empty", "empty", "empty", "empty"));

    static MIPSParser parser;
    static int latentSquashCount = 0;
    static int latentJumpLocation = 0;

    public static Queue<int[]> registerRestores = new LinkedList<>();
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
        String op = pipeLineOps.get(3);
        shiftPipelineRight();
        shiftPipelineRegRight();
        pipeLineOps.set(0, opcode);
        pipelineRegs.set(0, destination);
        handleBeqBne();
        if (latentSquashCount == 5) {
            latentSquashCount--;
            return true;
        } else if (latentSquashCount == 4) {
            pipeLineOps.set(0, "squash");
            pipelineRegs.set(0, "empty");
            parser.setPc(latentJumpLocation - 1);
            parser.setInProgressPC(latentJumpLocation);
            latentSquashCount = 0;
            return true;
        } else if (shouldStall) {
            pipeLineOps.set(1, "stall");
            pipelineRegs.set(1, "empty");
            // System.out.println("Should stall");
            // should insert a stall, but we return true in order to indicate an instruction is complete
            return true;
        } else if (op.equals("beq") || op.equals("bne")) {
            return true;
        }
        shouldStall = needsToStall(opcode, requirement1, requirement2);
        if (shouldStall) {
            //System.out.println("Should stall");
            return false;
        }
        return true;
    }

    private static void handleBeqBne() {
        String op = pipeLineOps.get(3);
        if (!op.equals("beq") && !op.equals("bne")) return;
        String args = pipelineRegs.get(3);
        String[] splitArgs = args.split(" ");
        ArrayList<String> regNames = parser.getRegNames();
        String src1 = splitArgs[0];
        String src2 = splitArgs[1];
        String label = splitArgs[2];
        int[] registers = registerRestores.remove();
        if (op.equals("bne")) {
            if (registers[regNames.indexOf(src1)] != registers[regNames.indexOf(src2)]) {
                parser.setPc(parser.labelToLine.get(label));
                parser.setInProgressPC(parser.labelToLine.get(label));
                squash3();
                parser.setRegisters(registers);
                // System.out.println("JUMPING BNE " + args);
            }
        }
        if (op.equals("beq")) {
            if (registers[regNames.indexOf(src1)] == registers[regNames.indexOf(src2)]) {
                parser.setPc(parser.labelToLine.get(label));
                parser.setInProgressPC(parser.labelToLine.get(label));
                squash3();
                // System.out.println("JUMPING BEQ " + args);
                // dumpRegisters(registers);
                parser.setRegisters(registers);
            }
        }
    }

    private static void squash3() {
        pipeLineOps.set(0, "squash");
        pipelineRegs.set(0, "empty");
        pipeLineOps.set(1, "squash");
        pipelineRegs.set(1, "empty");
        pipeLineOps.set(2, "squash");
        pipelineRegs.set(2, "empty");
    }

    private static boolean needsToStall(String opcode, String requirement1, String requirement2) {
        switch (opcode) {
            case "add", "sub", "and", "or", "slt", "sll", "srl", "lw", "sw" -> {
                String pipelineValue = pipelineRegs.get(1);
                if (pipelineValue.equals("empty")) return false;
                if ( (pipelineValue.equals(requirement1) || pipelineValue.equals(requirement2))) {
                    return true;
                }
            }
            case "beq", "bne" -> {
                return false;
            }
            case "j", "jr", "jal" -> {
                latentSquashCount = 5;
                // 5 is used as code for squashing for jal, jr, and j
                return false;
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

    public static void setParser(MIPSParser mipsParser) {
        parser = mipsParser;
    }

    public static void setLatentJumpLocation(int latentJumpLocation) {
        Pipeline.latentJumpLocation = latentJumpLocation;
    }

    private static void dumpRegisters(int[] registers) {
        System.out.println("\npc = " + parser.getPc());
        for (int i = 0; i < parser.getRegNames().size() - 1; ++i) { // don't wanna print the $zero register
            String msg = "$" + parser.getRegNames().get(i) + " = " + registers[i];
            if ((i+1) % 4 != 0 && i != parser.getRegNames().size() - 2) msg = padRightSpaces(msg, 16);
            System.out.print(msg);
            if ((i+1) % 4 == 0) {
                System.out.println();
            }
        }
        System.out.println("\n");
    }

    public static void addRegisterRestore(int[] registers) {
        registerRestores.add(registers.clone());
    }
}
