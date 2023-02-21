import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/*
Resources:
    https://www.dcc.fc.up.pt/~ricroc/aulas/1920/ac/apontamentos/P04_encoding_mips_instructions.pdf
    http://users.csc.calpoly.edu/~jseng/MD00565-2B-MIPS32-QRC-01.01.pdf
    https://opencores.org/projects/plasma/opcodes
    https://www.dsi.unive.it/~gasparetto/materials/MIPS_Instruction_Set.pdf
 */

/*
New process
1. run each clock, increment pc after instruction is done running
2. don't move onto next instruction until previous instruction does not need to stall.
 */
public class MIPSParser {
    Map<String, Integer> labelToLine;
    String inputFile;
    Instruction inst = new Instruction();
    Register reg = new Register();
    ArrayList<String> commands = new ArrayList<>();
    boolean isInstructionComplete = true;

    int[] registers = new int[32];
    ArrayList<String> regNames = new ArrayList<>(Arrays.asList("0", "v0", "v1", "a0", "a1", "a2", "a3", "t0", "t1", "t2", "t3", "t4", "t5", "t6", "t7", "s0", "s1", "s2", "s3", "s4", "s5", "s6", "s7", "t8", "t9", "sp", "ra", "zero"));
    int[] mem = new int[8192];
    int instructionDecrease = 0;
    int pc = 0;
    int inProgressPC = 0;
    int clocks = 0;
    public void parse(String inputFile) {
        this.inputFile = inputFile;
        labelToLine = new TreeMap<>();
        firstPass();
        // printMapping();
        // printCommands();
        // secondPass();
    }

    /*
    runs interactive mode in using script file or standard in if script is an empty string
    i.e. runInteractiveMode(file, script_file);
         runInteractivemode(file, "");
     */
    public void runInteractiveMode(String file, String script) {
        parse(file);
        Scanner input;
        if (!script.equals("")) {
            try {
                File script_file = new File(script);
                input = new Scanner(script_file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                input = new Scanner(System.in);
            }

        } else {
            input = new Scanner(System.in);
        }
        displayPrompt();
        while(input.hasNextLine()) {
            String inputString = input.nextLine();
            String[] commands = inputString.trim().split(" ");
            if (!script.equals("")) System.out.println(inputString + "\n");
            if (commands[0].equals("h")) {
                printHelp();
            } else if (commands[0].equals("d")){
                dump();
            } else if (commands.length == 2 && commands[0].equals("s")){
                step(Integer.parseInt(commands[1]));
            } else if (commands[0].equals("s")){
                step(1);
            } else if (commands[0].equals("r")){
                step(-1);
            } else if (commands.length == 3 && commands[0].equals("m")){
                memDump(Integer.parseInt(commands[1]), Integer.parseInt(commands[2]));
            } else if (commands[0].equals("c")){
                pc = 0;
                mem = new int[8192];
                registers = new int[32];
                inProgressPC = 0;
                System.out.println("        Simulator reset\n");
            } else if (commands[0].equals("q")){
                System.exit(1);
            }
            displayPrompt();
        }
    }

    private void printHelp() {
        System.out.println("""

                h = show help
                d = dump register state
                s = single step through the program (i.e. execute 1 instruction and stop)
                s num = step through num instructions of the program
                r = run until the program ends
                m num1 num2 = display data memory from location num1 to num2
                c = clear all registers, memory, and the program counter to 0
                q = exit the program
                """);
    }

    /*
    Executes <steps> number of steps in the program and prints to the screen.
    Executes until program termination if steps = -1
     */
    private void step(int steps) {
        if (steps < 0) {
            while (pc < commands.size()) {
                //System.out.println("pc: " + pc + " " + commands.get(pc));
                executeCommand(commands.get(pc));
                //System.out.println("pc: " + pc);
            }
        } else {
            for (int i = 0; i < steps && pc < commands.size(); ++i) {
                //System.out.println("pc: " + pc);
                //System.out.println("pc: " + pc + " " + commands.get(pc));
                // System.out.println(commands.get(pc) + " pc: " + pc);
                executeCommand(commands.get(pc));
                Pipeline.printPipeline(inProgressPC);
            }
        }
            //System.out.println("        " + steps + " instruction(s) executed");
        if (pc == commands.size()) {
            clocks += 5;
            printProgramComplete();
        }
    }


    private void executeCommand(String command) {
        String[] tokens = command.split("[\\s,$#()]+");
        String opName = tokens[0];
        //System.out.println(command);
        switch (opName) {
            case "and" -> and(tokens[1], tokens[2], tokens[3]);
            case "or" -> or(tokens[1], tokens[2], tokens[3]);
            case "add" -> add(tokens[1], tokens[2], tokens[3]);
            case "addi" -> addi(tokens[1], tokens[2], tokens[3]);
            case "sll" -> sll(tokens[1], tokens[2], tokens[3]);
            case "sub" -> sub(tokens[1], tokens[2], tokens[3]);
            case "slt" -> slt(tokens[1], tokens[2], tokens[3]);
            case "beq" -> beq(tokens[1], tokens[2], tokens[3]);
            case "bne" -> bne(tokens[1], tokens[2], tokens[3]);
            case "lw" -> lw(tokens[1], tokens[2], tokens[3]);
            case "sw" -> sw(tokens[1], tokens[2], tokens[3]);
            case "j" -> j(tokens[1]);
            case "jr" -> jr(tokens[1]);
            case "jal" -> jal(tokens[1]);
        }
        ++clocks;
    }

    private void and(String dest, String src1, String src2) {
        // and $1,$2,$3
        if (isInstructionComplete) {
            int destIndex = regNames.indexOf(dest);
            registers[destIndex] = registers[regNames.indexOf(src1)] & registers[regNames.indexOf(src2)];
            isInstructionComplete = false;
            ++inProgressPC;
        }
        if (Pipeline.run("and", dest, src1, src2)) {
            isInstructionComplete = true;
            ++pc;
        }
    }

    private void or(String dest, String src1, String src2) {
        // or $1,$2,$3
        if (isInstructionComplete) {
            int destIndex = regNames.indexOf(dest);
            registers[destIndex] = registers[regNames.indexOf(src1)] | registers[regNames.indexOf(src2)];
            isInstructionComplete = false;
            ++inProgressPC;
        }
        if (Pipeline.run("or", dest, src1, src2)) {
            isInstructionComplete = true;
            ++pc;
        }
    }

    private void add(String dest, String src1, String src2) {
        // add $1,$2,$3
        if (isInstructionComplete) {
            int destIndex = regNames.indexOf(dest);
            registers[destIndex] = registers[regNames.indexOf(src1)] + registers[regNames.indexOf(src2)];
            isInstructionComplete = false;
            ++inProgressPC;
        }
        if (Pipeline.run("add", dest, src1, src2)) {
            isInstructionComplete = true;
            ++pc;
        }
    }

    private void addi(String dest, String src1, String num1) {
        // addi $1,$2,100
        if (isInstructionComplete) {
            int destIndex = regNames.indexOf(dest);
            registers[destIndex] = registers[regNames.indexOf(src1)] + Integer.parseInt(num1);
            isInstructionComplete = false;
            ++inProgressPC;
        }
        if (Pipeline.run("addi", dest, src1, "")) {
            isInstructionComplete = true;
            ++pc;
        }
    }

    private void sll(String dest, String src1, String num1) {
        // sll $1,$2,10
        if (isInstructionComplete) {
            int destIndex = regNames.indexOf(dest);
            registers[destIndex] = registers[regNames.indexOf(src1)] << Integer.parseInt(num1);
            isInstructionComplete = false;
            ++inProgressPC;
        }
        if (Pipeline.run("sll", dest, src1, "")) {
            isInstructionComplete = true;
            ++pc;
        }
    }

    private void sub(String dest, String src1, String src2) {
        // sub $1,$2,$3
        if (isInstructionComplete) {
            int destIndex = regNames.indexOf(dest);
            registers[destIndex] = registers[regNames.indexOf(src1)] - registers[regNames.indexOf(src2)];
            isInstructionComplete = false;
            ++inProgressPC;
        }
        if (Pipeline.run("sub", dest, src1, src2)) {
            isInstructionComplete = true;
            ++pc;
        }
    }

    private void slt(String dest, String src1, String src2) {
        // slt $1,$2,$3
        if (isInstructionComplete) {
            int destIndex = regNames.indexOf(dest);
            if (registers[regNames.indexOf(src1)] < registers[regNames.indexOf(src2)]) {
                registers[destIndex] = 1;
            } else {
                registers[destIndex] = 0;
            }
            isInstructionComplete = false;
            ++inProgressPC;
        }
        if (Pipeline.run("slt", dest, src1, src2)) {
            isInstructionComplete = true;
            ++pc;
        }
    }

    private void beq(String src1, String src2, String label) {
        // beq $1,$2,end
        if (isInstructionComplete) {
            isInstructionComplete = false;
            ++inProgressPC;
        }
        if (Pipeline.run("beq", src1 + " " + src2 + " " + label, src1, src2)) {
            isInstructionComplete = true;
            Pipeline.addRegisterRestore(registers);
            if (registers[regNames.indexOf(src1)] == registers[regNames.indexOf(src2)]) {
                //pc = labelToLine.get(label);
                // dump();
                instructionDecrease += 3;
            } else {
                // System.out.println("not equal");
            }
            ++pc;
        }
    }

    private void bne(String src1, String src2, String label) {
        if (isInstructionComplete) {
            isInstructionComplete = false;
            ++inProgressPC;
        }
        // stores the dependencies in the destination register
        if (Pipeline.run("bne", src1 + " " + src2 + " " + label, src1, src2)) {
            isInstructionComplete = true;
            Pipeline.addRegisterRestore(registers.clone());
            if (registers[regNames.indexOf(src1)] != registers[regNames.indexOf(src2)]) {
                //pc = labelToLine.get(label);
                // dump();
                instructionDecrease += 3;
            }
            ++pc;
        }
    }

    private void lw(String dest, String offset, String offsetSrc) {
        // lw $1,100($2)
        if (isInstructionComplete) {
            int destIndex = regNames.indexOf(dest);
            registers[destIndex] = mem[Integer.parseInt(offset) + registers[regNames.indexOf(offsetSrc)]];
            isInstructionComplete = false;
            ++inProgressPC;
        }
        if (Pipeline.run("lw", dest, offsetSrc, "")) {
            isInstructionComplete = true;
            ++pc;
        }
    }

    private void sw(String data, String offset, String offsetSrc) {
        // sw $1,100($2)
        int store_address = Integer.parseInt(offset) + registers[regNames.indexOf(offsetSrc)];
        if (isInstructionComplete) {
            isInstructionComplete = false;
            int srcData = registers[regNames.indexOf(data)];
            mem[store_address] = srcData;
            ++inProgressPC;
        }
        if (Pipeline.run("sw", Integer.toString(store_address), offsetSrc, "")) {
            isInstructionComplete = true;
            ++pc;
        }
    }

    private void j(String label) {
        // j loop
        if (isInstructionComplete) {
            isInstructionComplete = false;
            instructionDecrease += 1;
            ++inProgressPC;
        }
        if (Pipeline.run("j", "", "", "")) {
            isInstructionComplete = true;
            Pipeline.setLatentJumpLocation(labelToLine.get(label));
            pc++;
        }
    }

    private void jr(String src) {
        // jr $s1
        if (isInstructionComplete) {
            isInstructionComplete = false;
            instructionDecrease += 1;
            ++inProgressPC;
        }
        if (Pipeline.run("jr", "", src, "")) {
            isInstructionComplete = true;
            Pipeline.setLatentJumpLocation(registers[regNames.indexOf(src)]);
            pc++;
        }
    }

    private void jal(String label) {
        // jal fibonnaci
        if (isInstructionComplete) {
            isInstructionComplete = false;
            instructionDecrease += 1;
            ++inProgressPC;
        }
        if (Pipeline.run("jal", "", "ra", "")) {
            isInstructionComplete = true;
            registers[regNames.indexOf("ra")] = pc + 1;
            Pipeline.setLatentJumpLocation(labelToLine.get(label));
            pc++;
        }
    }

    private void displayPrompt() {
        System.out.print("mips> ");
    }

    /*
    Finds the labels via the first pass and stores them
     */
    private void firstPass() {
        try {
            BufferedReader asm = new BufferedReader(new FileReader(inputFile));
            int counter = 0;
            while (asm.ready()) {
                String line = asm.readLine().trim();
                if (line.length() == 0 || line.charAt(0) == '#') continue;
                Pattern labelPattern = Pattern.compile("(\\w+):(.*)", Pattern.CASE_INSENSITIVE);
                Matcher matcher = labelPattern.matcher(line);
                if (matcher.find()) {
                    // System.out.println("match " + counter + ": " + matcher.group(1) + " remainder: \"" + matcher.group(2) + "\""); // gets the first match
                    labelToLine.put(matcher.group(1), counter);
                    String commandAfterLabel = matcher.group(2).trim();
                    if (commandAfterLabel.length() > 0) commands.add(commandAfterLabel);
                } else {
                    commands.add(line);
                }
                ++counter;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /*
    Performs the MIPS to binary conversion utilizing the labels from the firstPass()
     */
    private void secondPass() {
        int lineNum = 0;
        for (String command : commands) {
            processCommand(command, lineNum);
            ++lineNum;
        }
        printProgramComplete();
    }

    private void processCommand(String command, int lineNum) {
        if (command.charAt(0) == '#') return;

        String[] tokens = command.split("[\\s,$#()]+");
        if (inst.getOpCodeBin(tokens[0]) == null) {
            System.out.println("invalid instruction: " + tokens[0]);
            System.exit(1);
            return;
        }
        //printTokens(tokens);
        String formatType = inst.getOpcodeFormat(tokens[0]);
        switch (formatType) {
            case "R" -> printRFormat(tokens);
            case "I" -> printIFormat(tokens, lineNum);
            case "J" -> printJFormat(tokens);
        }
    }

    private void printMapping() {
        for (String label : labelToLine.keySet()) {
            System.out.print(label + ":         " + labelToLine.get(label));
            System.out.println();
        }
    }

    private void printRFormat(String[] tokens) {
        String op, rs, rt = "", rd, shamt, funct;
        op = inst.getOpCodeBin(tokens[0]);
        rd = reg.getRegisterBin(tokens[1]);
        rs = reg.getRegisterBin(tokens[2]);
        shamt = Operations.getBinaryWithSize(0,5);
        funct = inst.getOpcodeFunct(tokens[0]);
        if (reg.getRegisterBin(tokens[3]) != null) {
            rt = reg.getRegisterBin(tokens[3]);
        } else if (labelToLine.get(tokens[3]) != null) {
            rt = Operations.getBinaryWithSize(labelToLine.get(tokens[3]), 26);
        }
        System.out.println(op + " " + rs + " " + rt + " " + rd + " " + shamt + " " + funct);
    }

    private void printIFormat(String[] tokens, int currLineNum) {
        String op, rs, rt, register, offset, format;
        op = inst.getOpCodeBin(tokens[0]);
        register = reg.getRegisterBin(tokens[3]);
        format = inst.getOpcodeFunct(tokens[0]);
        // jal, opcode target
        if (format != null) {
            String rd = reg.getRegisterBin(tokens[1]);
            rt = reg.getRegisterBin(tokens[2]);
            rs = Operations.getBinaryWithSize(0, 5);
            String funct = inst.getOpcodeFunct(tokens[0]);
            String shiftAmt = Operations.getBinaryWithSize(Integer.parseInt(tokens[3]), 5);
            System.out.println(op + " " + rs + " " + rt + " " + rd + " " + shiftAmt + " " + funct);
            return;
        }
        // format opcode, rt, offset(rs)
        if (register != null) {
            rs = register;
            rt = reg.getRegisterBin(tokens[1]);
            offset = Operations.getBinaryWithSize(Integer.parseInt(tokens[2]), 16);
            System.out.println(op + " " + rs + " " + rt + " " + offset);
            return;
        }
        // format opcode, rs, rt, offset
        rs = reg.getRegisterBin(tokens[1]);
        rt = reg.getRegisterBin(tokens[2]);
        Integer lineNum = labelToLine.get(tokens[3]);
        if (lineNum != null) {
            offset = Operations.getBinaryWithSize(lineNum - currLineNum - 1, 16);
        } else {
            offset = Operations.getBinaryWithSize(Integer.parseInt(tokens[3]), 16);
        }
        System.out.println(op + " " + rs + " " + rt + " " + offset);
    }

    private void printJFormat(String[] tokens) {
        // we have a jr instruction
        String funct = inst.getOpcodeFunct(tokens[0]);
        if (funct != null) {
            System.out.println(inst.getOpCodeBin(tokens[0]) + " " + reg.getRegisterBin(tokens[1]) + " " + Operations.getBinaryWithSize(0, 15) + " " + funct);
            return;
        }
        System.out.println(inst.getOpCodeBin(tokens[0]) + " " + Operations.getBinaryWithSize(labelToLine.get(tokens[1]), 26));
    }

    private void printCommands() {
        for (String command : commands) {
            System.out.println(command);
        }
    }

    private void printTokens(String[] tokens) {
        for (String token : tokens) {
            System.out.print(token + " ");
        }
        System.out.println();
    }

    private void dump() {
        System.out.println("\npc = " + pc);
        for (int i = 0; i < regNames.size() - 1; ++i) { // don't wanna print the $zero register
            String msg = "$" + regNames.get(i) + " = " + registers[i];
            if ((i+1) % 4 != 0 && i != regNames.size() - 2) msg = padRightSpaces(msg, 16);
            System.out.print(msg);
            if ((i+1) % 4 == 0) {
                System.out.println();
            }
        }
        System.out.println("\n");
    }

    private void memDump(int start, int end) {
        if (start >= end) {
            System.out.println("Please enter a valid range {start < end}");
        } else {
            System.out.println();
            for (int i = start; i < end + 1 && i < mem.length; ++i) {
                System.out.println("[" + i + "] = " + mem[i]);
            }
            System.out.println();
        }
    }

    public String padRightSpaces(String inputString, int length) {
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

    public void printProgramComplete() {
        System.out.println("\n\rProgram complete");
        System.out.print("CPI = " + String.format("%.3f ", (float) clocks/(commands.size() - instructionDecrease)) + "\t");
        System.out.print("Cycles = " + clocks + "\t");
        System.out.println(" Instructions = " + (commands.size() - instructionDecrease) + "\t");
        System.out.println();
    }

    public int[] getRegisters() {
        return registers;
    }

    public ArrayList<String> getRegNames() {
        return regNames;
    }

    public void setPc(int pc) {
        this.pc = pc;
    }

    public int getPc() {
        return pc;
    }



    public void setInProgressPC(int pc) {
        this.inProgressPC = pc;
    }

    public void setRegisters(int[] registers) {
        this.registers = Arrays.copyOf(registers, 32);
    }
}
