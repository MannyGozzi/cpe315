import java.util.Map;
import java.util.TreeMap;
/*
A predefined mapping for the MIPS instruction set
 */
public class Instruction {
    Map<String, String> opcode;
    Map<String, String> format;

    Map<String, String> funct;

    Instruction() {
        opcode = new TreeMap<>();
        format = new TreeMap<>();
        funct = new TreeMap<>();
        opcode.put("and",     "000000"); format.put("and",    "R"); funct.put("and", "100100");
        opcode.put("or",      "000000"); format.put("or",     "R"); funct.put("or",  "100101");
        opcode.put("add",     "000000"); format.put("add",    "R"); funct.put("add", "100000");
        opcode.put("addi",    "001000"); format.put("addi",   "I");
        opcode.put("sll",     "000000"); format.put("sll",    "I");
        opcode.put("sub",     "000000"); format.put("sub",    "R"); funct.put("sub", "100010");
        opcode.put("slt",     "000000"); format.put("slt",    "R"); funct.put("slt", "101010");
        opcode.put("beq",     "000100"); format.put("beq",    "I");
        opcode.put("bne",     "000101"); format.put("bne",    "I");
        opcode.put("lw",      "100011"); format.put("lw",     "I");
        opcode.put("sw",      "101011"); format.put("sw",     "I");
        opcode.put("j",       "000010"); format.put("j",      "J");
        opcode.put("jr",      "000000"); format.put("jr",     "J");
        opcode.put("jal",     "000000"); format.put("jal",    "J");
    }

    public String getOpCodeBin(String opcode_) {
        return opcode.get(opcode_);
    }

    public String getOpcodeFormat(String opcode) {
        return format.get(opcode);
    }

    public String getOpcodeFunct(String opcode) {
        return funct.get(opcode);
    }
}
