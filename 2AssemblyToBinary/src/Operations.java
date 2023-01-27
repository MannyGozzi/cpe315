/*
useful operations for generating binary in a string representation
 */

public class Operations {
    public static String getBinaryWithSize(int number, int bitCount) {
        StringBuilder sb = new StringBuilder();
        String inputString;
        boolean isNegative = false;
        if (number < 0) {
            number *= -1;
            isNegative = true;
        }
        inputString = Integer.toBinaryString(number);
        while (sb.length() < bitCount - inputString.length()) {
            sb.append('0');
        }
        sb.append(inputString);
        String truncated = sb.substring(sb.length() - bitCount);
        if (isNegative) {
            return addOne(flip(truncated));
        }
        return truncated;
    }

    private static String flip(String inputString) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < inputString.length(); i++) {
            if (inputString.charAt(i) == '0') {
                sb.append('1');
            } else {
                sb.append('0');
            }
        }
        return sb.toString();
    }

    private static String addOne(String inputString) {
        StringBuilder sb = new StringBuilder();
        boolean carry = true;
        for (int i = inputString.length() - 1; i >= 0; i--) {
            if (inputString.charAt(i) == '0' && carry) {
                sb.append('1');
                carry = false;
            } else if (inputString.charAt(i) == '1' && carry) {
                sb.append('0');
            } else {
                sb.append(inputString.charAt(i));
            }
        }
        return sb.reverse().toString();
    }
}
