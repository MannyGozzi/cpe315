/*
useful operations for generating binary in a string representation
 */

public class Operations {
    public static String getBinaryWithSize(int number, int bitCount) {
        String inputString = Integer.toBinaryString(number);
        if (inputString.length() >= bitCount) {
            return inputString;
        }
        StringBuilder sb = new StringBuilder();
        while (sb.length() < bitCount - inputString.length()) {
            sb.append('0');
        }
        sb.append(inputString);

        return sb.toString();
    }
}
