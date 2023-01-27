import jdk.dynalink.Operation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/*
Resources:
    https://www.dcc.fc.up.pt/~ricroc/aulas/1920/ac/apontamentos/P04_encoding_mips_instructions.pdf
    http://users.csc.calpoly.edu/~jseng/MD00565-2B-MIPS32-QRC-01.01.pdf
    https://opencores.org/projects/plasma/opcodes
 */
public class MIPSParser {
    Map<String, Integer> labelToBinLoc;
    String inputFile;
    Instruction inst = new Instruction();
    Register reg = new Register();
    ArrayList<String> commands = new ArrayList<>();

    public void printFileToBinary(String inputFile) {
        this.inputFile = inputFile;
        labelToBinLoc = new TreeMap<>();
        firstPass();
        //printMapping();
        //printCommands();
        secondPass();
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
                    labelToBinLoc.put(matcher.group(1), counter);
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

    private void processCommand(String command, int lineNum) {
        if (command.charAt(0) == '#') return;

        String[] tokens = command.split("[\\s,$#()]+");
        if (inst.getOpCodeBin(tokens[0]) == null) {
            System.out.println("invalid instruction: " + tokens[0]);
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
        for (String label : labelToBinLoc.keySet()) {
            System.out.print(label + ":\t " + labelToBinLoc.get(label));
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
        } else if (labelToBinLoc.get(tokens[3]) != null) {
            rt = Operations.getBinaryWithSize(labelToBinLoc.get(tokens[3]), 26);
        }
        /* else {
            System.out.println("Tokens from error: ");
            printTokens(tokens);
            System.out.println("Error: " + tokens[3] + " is not a valid register");
        } */
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
        Integer lineNum = labelToBinLoc.get(tokens[3]);
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
        System.out.println(inst.getOpCodeBin(tokens[0]) + " " + Operations.getBinaryWithSize(labelToBinLoc.get(tokens[1]), 26));
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
}
