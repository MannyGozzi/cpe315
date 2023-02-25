import java.util.Map;
import java.util.TreeMap;
/*
A predefined mapping for MIPS registers
 */
public class Register {
    Map<String, String> type;
    Register() {
        type = new TreeMap<>();
        type.put("zero", Operations.getBinaryWithSize(0, 5));
        type.put("0", Operations.getBinaryWithSize(0, 5));
        type.put("v0", Operations.getBinaryWithSize(2, 5));
        type.put("v1", Operations.getBinaryWithSize(3, 5));
        type.put("a0", Operations.getBinaryWithSize(4, 5));
        type.put("a1", Operations.getBinaryWithSize(5, 5));
        type.put("a2", Operations.getBinaryWithSize(6, 5));
        type.put("a3", Operations.getBinaryWithSize(7, 5));
        type.put("t0", Operations.getBinaryWithSize(8, 5));
        type.put("t1", Operations.getBinaryWithSize(9, 5));
        type.put("t2", Operations.getBinaryWithSize(10, 5));
        type.put("t3", Operations.getBinaryWithSize(11, 5));
        type.put("t4", Operations.getBinaryWithSize(12, 5));
        type.put("t5", Operations.getBinaryWithSize(13, 5));
        type.put("t6", Operations.getBinaryWithSize(14, 5));
        type.put("t7", Operations.getBinaryWithSize(15, 5));
        type.put("s0", Operations.getBinaryWithSize(16, 5));
        type.put("s1", Operations.getBinaryWithSize(17, 5));
        type.put("s2", Operations.getBinaryWithSize(18, 5));
        type.put("s3", Operations.getBinaryWithSize(19, 5));
        type.put("s4", Operations.getBinaryWithSize(20, 5));
        type.put("s5", Operations.getBinaryWithSize(21, 5));
        type.put("s6", Operations.getBinaryWithSize(22, 5));
        type.put("s7", Operations.getBinaryWithSize(23, 5));
        type.put("t8", Operations.getBinaryWithSize(24, 5));
        type.put("t9", Operations.getBinaryWithSize(25, 5));
        type.put("gp", Operations.getBinaryWithSize(28, 5));
        type.put("sp", Operations.getBinaryWithSize(29, 5));
        type.put("fp", Operations.getBinaryWithSize(30, 5));
        type.put("ra", Operations.getBinaryWithSize(31, 5));
    }

    public String getRegisterBin(String register) {
        return type.get(register);
    }
}
