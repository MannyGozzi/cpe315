import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/*
Resources:
    https://www.dcc.fc.up.pt/~ricroc/aulas/1920/ac/apontamentos/P04_encoding_mips_instructions.pdf
    http://users.csc.calpoly.edu/~jseng/MD00565-2B-MIPS32-QRC-01.01.pdf
    https://opencores.org/projects/plasma/opcodes
 */
public class MIPSParser {
    Map<String, Integer> labelToLine;
    String inputFile;
    Instruction inst = new Instruction();
    Register reg = new Register();
    ArrayList<String> commands = new ArrayList<>();

    int[] registers = new int[32];
    ArrayList<String> regNames = new ArrayList<>(Arrays.asList("0", "v0", "v1", "a0", "a1", "a2", "a3", "t0", "t1", "t2", "t3", "t4", "t5", "t6", "t7", "s0", "s1", "s2", "s3", "s4", "s5", "s6", "s7", "t8", "t9", "sp", "ra", "zero"));
    int[] mem = new int[8192];
    int pc = 0;

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
            String[] commands = input.nextLine().trim().split(" ");
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

            } else if (commands[0].equals("c")){
                pc = 0;
                mem = new int[8192];
                registers = new int[32];
                System.out.println("\t\tSimulator reset\n");
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
        System.out.println(steps + " instruction(s) executed\n");
        if (steps < 0) {
            while (pc < commands.size()) {
                executeCommand(commands.get(pc));
                System.out.println("pc: " + pc);
            }
        } else {
            for (int i = 0; i < steps && pc < commands.size(); ++i) {
                executeCommand(commands.get(pc));
                System.out.println("pc: " + pc);
            }
        }
    }


    private void executeCommand(String command) {
        String[] tokens = command.split("[\\s,$#()]+");
        String opName = tokens[0];
        System.out.println(command);
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
    }

        /*
    opcode.put("and",     "000000"); format.put("and",    "R"); funct.put("and", "100100");
        opcode.put("or",      "000000"); format.put("or",     "R"); funct.put("or",  "100101");
        opcode.put("add",     "000000"); format.put("add",    "R"); funct.put("add", "100000");
        opcode.put("addi",    "001000"); format.put("addi",   "I");
        opcode.put("sll",     "000000"); format.put("sll",    "I"); funct.put("sll", "000000");
        opcode.put("sub",     "000000"); format.put("sub",    "R"); funct.put("sub", "100010");
        opcode.put("slt",     "000000"); format.put("slt",    "R"); funct.put("slt", "101010");
        opcode.put("beq",     "000100"); format.put("beq",    "I");
        opcode.put("bne",     "000101"); format.put("bne",    "I");
        opcode.put("lw",      "100011"); format.put("lw",     "I");
        opcode.put("sw",      "101011"); format.put("sw",     "I");
        opcode.put("j",       "000010"); format.put("j",      "J");
        opcode.put("jr",      "000000"); format.put("jr",     "J"); funct.put("jr", "001000");
        opcode.put("jal",     "000011"); format.
     */

    private void and(String dest, String src1, String src2) {
        // and $1,$2,$3
        int destIndex = regNames.indexOf(dest);
        registers[destIndex] = registers[regNames.indexOf(src1)] & registers[regNames.indexOf(src2)];
        ++pc;
    }

    private void or(String dest, String src1, String src2) {
        // or $1,$2,$3
        int destIndex = regNames.indexOf(dest);
        registers[destIndex] = registers[regNames.indexOf(src1)] | registers[regNames.indexOf(src2)];
        ++pc;
    }

    private void add(String dest, String src1, String src2) {
        // add $1,$2,$3
        int destIndex = regNames.indexOf(dest);
        registers[destIndex] = registers[regNames.indexOf(src1)] + registers[regNames.indexOf(src2)];
        ++pc;
    }

    private void addi(String dest, String src1, String num1) {
        // addi $1,$2,100
        int destIndex = regNames.indexOf(dest);
        registers[destIndex] = registers[regNames.indexOf(src1)] + Integer.parseInt(num1);
        ++pc;
    }

    private void sll(String dest, String src1, String num1) {
        // sll $1,$2,10
        int destIndex = regNames.indexOf(dest);
        registers[destIndex] = registers[regNames.indexOf(src1)] << Integer.parseInt(num1);
        ++pc;
    }

    private void sub(String dest, String src1, String src2) {
        // sub $1,$2,$3
        int destIndex = regNames.indexOf(dest);
        registers[destIndex] = registers[regNames.indexOf(src1)] - registers[regNames.indexOf(src2)];
        ++pc;
    }

    private void slt(String dest, String src1, String src2) {
        // slt $1,$2,$3
        int destIndex = regNames.indexOf(dest);
        if (registers[regNames.indexOf(src1)] < registers[regNames.indexOf(src2)]) {
            registers[destIndex] = 1;
        } else {
            registers[destIndex] = 0;
        }
        ++pc;
    }

    private void beq(String src1, String src2, String label) {
        // beq $1,$2,end
        if (registers[regNames.indexOf(src1)] == registers[regNames.indexOf(src2)]) {
            pc = labelToLine.get(label);
        } else {
            ++pc;
        }
    }

    private void bne(String src1, String src2, String label) {
        // bne $1,$2,end
        if (registers[regNames.indexOf(src1)] != registers[regNames.indexOf(src2)]) {
            pc = labelToLine.get(label);
        } else {
            ++pc;
        }
    }

    private void lw(String dest, String offset, String offsetSrc) {
        // lw $1,100($2)
        int destIndex = regNames.indexOf(dest);
        registers[destIndex] = mem[Integer.parseInt(offset) + registers[regNames.indexOf(offsetSrc)]];
        ++pc;
    }

    private void sw(String data, String offset, String offsetSrc) {
        // sw $1,100($2)
        int srcData = registers[regNames.indexOf(data)];
        mem[Integer.parseInt(offset) + registers[regNames.indexOf(offsetSrc)]] = srcData;
        ++pc;
    }

    private void j(String label) {
        // j loop
        pc = labelToLine.get(label) - 1;
    }

    private void jr(String src) {
        // jr $s1
        pc = registers[regNames.indexOf(src)];
    }

    private void jal(String src) {
        // jal fibonnaci
        registers[regNames.indexOf("ra")] = pc + 1;
        pc = labelToLine.get(src);
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
    }

    private void processInteractiveCommand(String command) {
        System.out.println("Processing command " + command);
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
            System.out.print(label + ":\t " + labelToLine.get(label));
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
        for (int i = 0; i < regNames.size(); ++i) {
            System.out.print("$" + regNames.get(i) + " = " + registers[i] + "\t\t");
            if ((i+1) % 4 == 0) {
                System.out.println();
            }
        }
        System.out.println("\n");
    }
}
