import java.util.Map;
import java.util.TreeMap;
/*
A predefined mapping for the MIPS instruction set
 */
public class Instruction {
    Map<String, String> type;

    Instruction() {
        type = new TreeMap<>();
        type.put("add", "");
    }
}
