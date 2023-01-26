import java.util.Map;
import java.util.TreeMap;
/*
A predefined mapping for the MIPS instruction set
 */
public class Instruction {
    Map<String, String> opcode;
    Map<String, String> format;

    Instruction() {
        opcode = new TreeMap<>();
        format = new TreeMap<>();
        opcode.put("and",     "100100"); format.put("and",    "R");
        opcode.put("or",      "100101"); format.put("or",     "R");
        opcode.put("add",     "100000"); format.put("add",    "R");
        opcode.put("addi",    "001000"); format.put("addi",   "R");
        opcode.put("sll",     "000000"); format.put("sll",    "R");
        opcode.put("sub",     "100010"); format.put("sub",    "R");
        opcode.put("slt",     "101010"); format.put("slt",    "R");
        opcode.put("beq",     "000100"); format.put("beq",    "R");
        opcode.put("bne",     "000101"); format.put("bne",    "R");
        opcode.put("lw",      "100011"); format.put("lw",     "I");
        opcode.put("sw",      "101011"); format.put("sw",     "I");
        opcode.put("j",       "000010"); format.put("j",      "J");
        opcode.put("jr",      "001000"); format.put("jr",     "J");
        opcode.put("jal",     "000011"); format.put("jal",    "J");
    }

    public String getOpCodeBin(String opcode_) {
        return opcode.get(opcode_);
    }

    public String getOpcodeFormat(String opcode) {
        return format.get(opcode);
    }
}
