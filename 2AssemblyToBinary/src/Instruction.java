import java.util.Map;
import java.util.TreeMap;
/*
A predefined mapping for the MIPS instruction set
 */
public class Instruction {
    Map<String, String> type;
    Map<String, String> format;

    Instruction() {
        type = new TreeMap<>();
        format = new TreeMap<>();
        type.put("and",     "100100"); format.put("and",    "R");
        type.put("or",      "100101"); format.put("or",     "R");
        type.put("add",     "100000"); format.put("add",    "R");
        type.put("addi",    "001000"); format.put("addi",   "R");
        type.put("sll",     "000000"); format.put("sll",    "R");
        type.put("sub",     "100010"); format.put("sub",    "R");
        type.put("slt",     "101010"); format.put("slt",    "R");
        type.put("beq",     "000100"); format.put("beq",    "R");
        type.put("bne",     "000101"); format.put("bne",    "R");
        type.put("lw",      "100011"); format.put("lw",     "I");
        type.put("sw",      "101011"); format.put("sw",     "I");
        type.put("j",       "000010"); format.put("j",      "J");
        type.put("jr",      "001000"); format.put("jr",     "J");
        type.put("jal",     "000011"); format.put("jal",    "J");
    }
}
